package com.bt.elderbracelet.okhttp;

import android.os.AsyncTask;
import android.util.Log;

import com.bonten.ble.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pendragon on 17-4-9.
 */

public class HttpRequest {

    /**
     * @param url      相对路径, 会与 #ROOT 进行拼接
     * @param jsonData JSON 请求字符串
     * @param callback 处理网络请求的回调接口
     */

    public static void post(final String url, final String jsonData, final HttpRequestCallback callback)
    {
        new PostTask(url, jsonData, callback).execute();
    }

    /**
     * @param url      相对路径, 会与 #ROOT 进行拼接
     * @param headers  headers, 可选
     * @param params   form-data 类型的请求参数
     * @param callback 处理网络请求的回调接口
     */
    public static void get(final String url, final Map<String, String> headers,
                           final Map<String, Object> params, final HttpRequestCallback callback)
    {
        new GetTask(headers, params, url, callback).execute();
    }

    public interface HttpRequestCallback {
        void onSuccess(JSONObject response);

        void onFailure();
    }

    private static String attachHttpGetParams(String url, Map<String, Object> params)
    {
        if (url == null)
            return null;
        if (params == null || params.isEmpty())
            return url;

        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue().toString());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static class PostTask extends AsyncTask<Void, Void, String> {
        private String url;
        private String jsonData;
        private HttpRequestCallback callback;

        PostTask(String url, String jsonData, HttpRequestCallback callback)
        {
            this.url = url;
            this.jsonData = jsonData;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody rb = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
            Request request = new Request.Builder()
                    .url(MyApplication.Ip_Address + url)
                    .post(rb)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response)
        {
            //注意：这个参数，实际上并不是response，而只是response.body().string()
            //它本身也是一个JsonObject的字符串，所以我们要获取其中的数据，
            //必须再次强转为JsonObject,然后通过opt(key)方法获取其中的真实数据
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    callback.onSuccess(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            } else
                callback.onFailure();
        }
    }

    private static class GetTask extends AsyncTask<Void, Void, String> {
        private Map<String, String> headers;
        private Map<String, Object> params;
        private String url;
        private HttpRequestCallback callback;

        GetTask(Map<String, String> headers, Map<String, Object> params, String url, HttpRequestCallback callback)
        {
            this.headers = headers;
            this.params = params;
            this.url = url;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            OkHttpClient httpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder();
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }

            if (params != null) {
                builder.url(attachHttpGetParams(MyApplication.Ip_Address + url, this.params));
            } else {
                builder.url(MyApplication.Ip_Address + url);
            }

            Request request = builder.build();
            Response response;
            try {
                response = httpClient.newCall(request).execute();
                Log.e("HTTP", new Date().toString() + ": " + request.url().toString());
                    if (!response.isSuccessful()) {
                    return null;
                }
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response)
        {
            Log.e("HTTP", new Date().toString() + ": response");
            Log.e("HTTP", response == null ? "null" : response);
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    callback.onSuccess(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure();
                }
            } else
                callback.onFailure();
        }
    }
}
