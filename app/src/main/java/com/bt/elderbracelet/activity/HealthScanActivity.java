package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.BloodSugar;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.HorizontalProgressBarWithNumber;
import com.bttow.elderbracelet.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class HealthScanActivity extends Activity implements OnClickListener {

    ImageView back;
    TextView tvYear;
    TextView tvMonth;
    TextView tvDay;

    Button btPreDay;
    Button btNextDay;

    TextView tvStep;
    TextView tvDistance;
    TextView tvCalorie;
    TextView tvSportTime;

    TextView tvSleepTime;
    TextView tvSleepDeepTime;

    TextView tvHeartRate;
    TextView tvBloodOxygen;

    TextView tvSugarPre;
    TextView tvSugarAfter;
    TextView tvPressureHigh;
    TextView tvPressureLow;
    RatingBar rating_scorer;
    Button btnShare;

    private ModelDao modelDao;
    private Bitmap Bmp;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_yesterday_health_data);
        MyApplication.getInstance().addActivity(this);
        initView();
        initData();
    }

    private void initView()
    {
        back = (ImageView) findViewById(R.id.back);
        btnShare = (Button) findViewById(R.id.bt_healthscan_share);
        tvYear = (TextView) findViewById(R.id.tv_year);
        tvMonth = (TextView) findViewById(R.id.tv_month);
        tvDay = (TextView) findViewById(R.id.tv_day);

        btPreDay = (Button) findViewById(R.id.btn_pre_day);
        btNextDay = (Button) findViewById(R.id.btn_next_day);

        rating_scorer = (RatingBar) findViewById(R.id.rating_score);
        rating_scorer.setRating(0);

        tvStep = (TextView) findViewById(R.id.tv_steps);  //运动步数
        tvDistance = (TextView) findViewById(R.id.tv_distance); //运动距离
        tvCalorie = (TextView) findViewById(R.id.tv_calorias); //消耗卡路里
        tvSportTime = (TextView) findViewById(R.id.tv_sportTimes); //运动时间

        tvSleepTime = (TextView) findViewById(R.id.tv_sleep_time); //睡眠时间
        tvSleepDeepTime = (TextView) findViewById(R.id.tv_sleep_deep_time); //深睡时间

        tvHeartRate = (TextView) findViewById(R.id.tv_heartRate); //血氧
        tvBloodOxygen = (TextView) findViewById(R.id.tv_bloodOxygen); //心率

        tvSugarPre = (TextView) findViewById(R.id.tv_sugar_pre); //餐前血糖
        tvSugarAfter = (TextView) findViewById(R.id.tv_sugar_after); //餐后血糖
        tvPressureHigh = (TextView) findViewById(R.id.tv_pressure_high); //最高血压
        tvPressureLow = (TextView) findViewById(R.id.tv_pressure_low); //最低血压

        back.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btPreDay.setOnClickListener(this);
        btNextDay.setOnClickListener(this);
    }


    public void initData()
    {
        try {
            ShareSDK.initSDK(HealthScanActivity.this);
        } catch (Exception ignored) {
        }
        modelDao = new ModelDao(getApplicationContext());
        HorizontalProgressBarWithNumber.setDEFAULT_TEXT_COLOR(0XFF00d0c2);   //不重要，不用在乎
        HorizontalProgressBarWithNumber.setDEFAULT_TEXT_COLOR2(0XFFffb3fe);

        currentDate = BaseUtils.getYesterdayDate();
        initOneDay(currentDate);   //初始化显示 前一天的健康数据
        btNextDay.setBackgroundColor(getResources().getColor(R.color.gray));
        btNextDay.setEnabled(false);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.bt_healthscan_share:
                try {
                    showShare();//分享
                } catch (Exception ignored) {
                }

                break;
            case R.id.back:
                finish();
                break;
            case R.id.btn_pre_day:
                if(!btNextDay.isEnabled()){
                    btNextDay.setEnabled(true);
                    btNextDay.setBackgroundColor(getResources().getColor(R.color.deeppink));
                }
                currentDate = BaseUtils.getPreDate(currentDate);
                initOneDay(currentDate);
                break;
            case R.id.btn_next_day:
                currentDate = BaseUtils.getNextDate(currentDate);
                initOneDay(currentDate);
                if (BaseUtils.getNextDate(currentDate).equals(BaseUtils.getTodayDate())) {
                    btNextDay.setBackgroundColor(getResources().getColor(R.color.gray));
                    btNextDay.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    private String getSDCardPath()
    {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    private void showShare()
    {

        ShareSDK.initSDK(HealthScanActivity.this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        //构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        findViewById(R.id.bt_healthscan_share).setVisibility(View.GONE);
        Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        //获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();


        if (!saveMyBitmap("fx", Bmp)) {
            MethodUtils.showToast(getApplicationContext(), "分享图片保存失败");
            return;
        }
        findViewById(R.id.bt_healthscan_share).setVisibility(View.VISIBLE);
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("我的健康状态");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        //oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        //oks.setText("我的健康状态");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/fx.png");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //	oks.setUrl("http://sharesdk.cn");

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //	oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(HealthScanActivity.this);

    }

    /**
     * 保存方法
     */
    public boolean saveMyBitmap(String bitName, Bitmap mBitmap)
    {
        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return false;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 从服务器中获取具体某天的数据，然后更新界面
     */
    public void initOneDay(final String date)
    {
        System.out.println("当前页面是：" + date);
        clearUI();

        String year = date.substring(0, 4);
        final String month = date.substring(5, 7);
        String day = date.substring(8);
        tvYear.setText(year);
        tvMonth.setText(month);
        tvDay.setText(day);

        final Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        params.put("date", date);

        //设置运动数据
        HttpRequest.get(URLConstant.URL_GET_ONE_SPORT, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");

                    System.out.println("Sport JsonObject " + data);

                    Sport sport = new Sport();
                    if (data != null) {
                        sport.setStep(data.optString("step"));
                        sport.setDistance(data.optString("distance"));
                        sport.setCalorie(data.optString("calorie"));
                        sport.setSportTime(data.optString("sportTime"));
                    }
                    initSportUI(sport);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "为了查询更多历史记录，请检查网络是否异常");
                Sport sport = modelDao.querySportByDate(date);

                System.out.println("sport前 :" + sport);
                if (sport == null) {
                    sport = new Sport();
                }
                System.out.println("sport后 :" + sport);
                initSportUI(sport);
            }
        });
        //设置睡眠数据
        HttpRequest.get(URLConstant.URL_GET_ONE_SLEEP, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");

                    System.out.println("Sleep JsonObject " + data);

                    Sleep sleep = new Sleep();
                    if (data != null) {
                        sleep.setSleepTime(data.optString("sleepTime"));
                        sleep.setSleepDeepTime(data.optString("sleepDeepTime"));
                    }
                    initSleepUI(sleep);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                Sleep sleep = modelDao.querySleepByDate(date);
                if (sleep == null) {
                    sleep = new Sleep();
                }
                initSleepUI(sleep);
            }
        });

        //设置血压数据
        HttpRequest.get(URLConstant.URL_GET_ONE_BLOODPRESSURE, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");
                    System.out.println("Pressure JsonObject " + data);

                    BloodPressure pressure = new BloodPressure();
                    if (data != null) {
                        pressure.setBloodPressureHigh(data.optString("highPressure"));
                        pressure.setBloodPressureLow(data.optString("lowPressure"));
                    }
                    initBloodPressureUI(pressure);
                } else {
                    System.out.println("血压数据错了");
                    System.out.println(response.optString("error_info"));

                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                System.out.println("血氧数据断网了");
                BloodPressure pressure = modelDao.queryPressureByDate(date);
                if (pressure == null) {
                    pressure = new BloodPressure();
                }
                initBloodPressureUI(pressure);
            }
        });

        //设置血糖数据
        HttpRequest.get(URLConstant.URL_GET_ONE_BLOODSUGAR, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");
                    System.out.println("Sugar JsonObject " + data);

                    BloodSugar sugar = new BloodSugar();
                    if (data != null) {
                        sugar.setBloodSugarBefore(data.optString("beforeMealSugar"));
                        sugar.setBloodSugarAfter(data.optString("afterMealSugar"));
                    }
                    initBloodSugarUI(sugar);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                BloodSugar sugar = modelDao.querySugarByDate(date);
                if (sugar == null) {
                    sugar = new BloodSugar();
                }
                initBloodSugarUI(sugar);
            }
        });

        //设置血氧
        HttpRequest.get(URLConstant.URL_GET_ONE_BLOODOXYGEN, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");
                    System.out.println("Oxygen JsonObject " + data);

                    BloodOxygen oxygen = new BloodOxygen();
                    if (data != null) {
                        oxygen.setBloodOxygen(data.optString("oxygen"));
                    }
                    initBloodOxygenUI(oxygen);
                } else {
                    System.out.println("血氧数据错了");
                    System.out.println(response.optString("error_info"));
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                BloodOxygen oxygen = modelDao.queryOxygenByDate(date);
                System.out.println("血氧数据断网了");
                if (oxygen == null) {
                    oxygen = new BloodOxygen();
                }
                System.out.println("血氧断网后 ：" + oxygen);
                initBloodOxygenUI(oxygen);
            }
        });

        //设置心率
        HttpRequest.get(URLConstant.URL_GET_ONE_HEART_RATE, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {

                    JSONObject data = response.optJSONObject("data");

                    System.out.println("HeartRate JsonObject " + data);

                    HeartRate rate = new HeartRate();
                    if (data != null) {
                        rate.setHeartRate(data.optString("heartRate"));
                    }
                    initHeartRateUI(rate);
                } else {
                    MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                HeartRate rate = modelDao.queryHeartRateByDate(date);
                if (rate == null) {
                    rate = new HeartRate();
                }
                initHeartRateUI(rate);
            }
        });
    }

    private void clearUI()
    {
        rating_scorer.setRating(0);

        tvStep.setText("");
        tvDistance.setText("");
        tvCalorie.setText("");
        tvSportTime.setText("");

        tvSleepTime.setText("");
        tvSleepDeepTime.setText("");

        tvHeartRate.setText("");
        tvBloodOxygen.setText("");

        tvSugarPre.setText("");
        tvSugarAfter.setText("");
        tvPressureHigh.setText("");
        tvPressureLow.setText("");

    }


    public void initSportUI(Sport sport)
    {
        if (sport == null) {
            System.out.println("initSportUI() 中sport数据为空 ");
            return;
        }
        tvStep.setText(sport.getStep());
        double distanceKM = (double) Integer.valueOf(sport.getDistance()) / 1000;
        DecimalFormat df = new DecimalFormat("0.00");
        tvDistance.setText(df.format(distanceKM));

        tvCalorie.setText(sport.getCalorie());
        if (!TextUtils.isEmpty(sport.getSportTime())) {    //设置运动时间： 将"80分钟解析为 "01:20"(1小时20分)
            int sportHour = Integer.parseInt(sport.getSportTime()) / 60;
            int sportMinite = Integer.parseInt(sport.getSportTime()) % 60;
            String minite = String.format("%02d", sportMinite);
            String hour = String.format("%02d", sportHour);
            tvSportTime.setText(hour + ":" + minite);
        }

        //下面是根据运动步数 来设置健康指数等级
        int hsScore = 0;
        if (!TextUtils.isEmpty(sport.getStep())) {
            hsScore = Integer.parseInt(sport.getStep());
        }
        if (hsScore >= 5000) {
            rating_scorer.setRating(5);
        } else if (hsScore >= 4000 && hsScore < 5000) {
            rating_scorer.setRating(4);
        } else if (hsScore >= 3000 && hsScore < 4000) {
            rating_scorer.setRating(3);
        } else if (hsScore >= 2000 && hsScore < 3000) {
            rating_scorer.setRating(2);
        } else if (hsScore >= 1000 && hsScore < 2000) {
            rating_scorer.setRating(1);
        } else if (hsScore < 1000) {
            rating_scorer.setRating(0);
        }
    }

    public void initSleepUI(Sleep sleep)
    {
        if (sleep == null) {
            System.out.println("initSleepUI() 中sleep数据为空 ");
            return;
        }
        String sleepTime = sleep.getSleepTime();
        if (!TextUtils.isEmpty(sleepTime)) {    //设置运动时间： 将"80分钟解析为 "01:20"(1小时20分)
            int sleepHour = Integer.parseInt(sleepTime) / 60;
            int sleepMinite = Integer.parseInt(sleepTime) % 60;
            String minite = String.format("%02d", sleepMinite);
            String hour = String.format("%02d", sleepHour);
            tvSleepTime.setText(hour + ":" + minite);
        }

        String sleepDeepTime = sleep.getSleepDeepTime();
        if (!TextUtils.isEmpty(sleepDeepTime)) {    //设置运动时间： 将"80分钟解析为 "01:20"(1小时20分)
            int sleepDeepHour = Integer.parseInt(sleepDeepTime) / 60;
            int sleepDeepMinite = Integer.parseInt(sleepDeepTime) % 60;
            String minite = String.format("%02d", sleepDeepMinite);
            String hour = String.format("%02d", sleepDeepHour);
            tvSleepDeepTime.setText(hour + ":" + minite);
        }
    }

    public void initHeartRateUI(HeartRate rate)
    {
        if (rate == null) {
            System.out.println("initHeartRateUI() 中rate数据为空 ");
            return;
        }
        tvHeartRate.setText(rate.getHeartRate());
    }

    public void initBloodPressureUI(BloodPressure pressure)
    {
        if (pressure == null) {
            System.out.println("initBloodPressureUI() 中pressure数据为空 ");
            return;
        }
        tvPressureHigh.setText(pressure.getBloodPressureHigh());
        tvPressureLow.setText(pressure.getBloodPressureLow());

    }

    public void initBloodSugarUI(BloodSugar sugar)
    {
        if (sugar == null) {
            System.out.println("initBloodPressureUI() 中pressure数据为空 ");
            return;
        }
        tvSugarPre.setText(sugar.getBloodSugarBefore());
        tvSugarAfter.setText(sugar.getBloodSugarAfter());
    }

    public void initBloodOxygenUI(BloodOxygen oxygen)
    {
        if (oxygen == null) {
            System.out.println("initBloodOxygenUI() 中oxygen数据为空 ");
            return;
        }
        tvBloodOxygen.setText(oxygen.getBloodOxygen());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (Bmp != null) {
            Bmp.recycle();
        }
    }


    public static Bitmap view2Bitmap(View view)
    {
        Bitmap bitmap = null;
        try {
            if (view != null) {
                view.setDrawingCacheEnabled(true);
                view.measure(
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    private void GetandSaveCurrentImage()
    {
        //构建Bitmap
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();
        Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        //获取屏幕
        View decorview = this.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();
        //图片存储路径
        String SavePath = getSDCardPath() + "/Demo/ScreenImages";
        //保存Bitmap
        try {
            File path = new File(SavePath);
            //文件
            String filepath = SavePath + "/Screen_1.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Toast.makeText(getBaseContext(), "截屏文件已保存至SDCard/ScreenImages/目录下", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
