package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.entity.others.CityData;
import com.bt.elderbracelet.entity.others.DistrictData;
import com.bt.elderbracelet.entity.others.ProvinceData;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.view.ArrayWheelAdapter;
import com.bt.elderbracelet.view.NumericWheelAdapter;
import com.bt.elderbracelet.view.OnWheelChangedListener;
import com.bt.elderbracelet.view.TitleView2;
import com.bt.elderbracelet.view.WheelView;
import com.bttow.elderbracelet.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class RegisterActivity extends Activity implements OnClickListener {
    private TitleView2 titleView;    //标题栏
    private EditText etname, etnum, etPassword, etCid;  //名字，电话号码，密码，联系的客服编号
    //    private EditText etInputCode;  //输入验证码
//    private TextView textGetCode;  //获取验证码
    private EditText eturgentname, eturgentnum;      //紧急联系人姓名，紧急联系人电话号码
    private TextView tvProvince, tvCity, tvArea;      //省份，城市，区县
    private TextView age, sex, height, weight, tv_step_distance; //年龄，性别，身高，体重,步距
    private TextView argee_pro, tv_already_register, tv_forget_pwd;     //“用户使用协议” ，"我已注册"
    private TextView tvnext; // 下一步
    private Button btnsure, btnsureSex;  //数值选择器中的确定按钮
    private CheckBox box;      //“已阅读同意”的复选框

    private boolean flag = false;
    private SpannableString msp = null, msp_two;
    private String name, num, password, nsex, nage, nweight, nheight, nCid;
    private String nungentName, nungentNum, step_distance;
    private String province, city, area;
    //    private String verifyCode;
    private ModelDao modelDao;
    private PopupWindow popupWindow, popupWindowSex;
    //popupWindow 代表年龄，体重，身高，步距的弹出窗口
    //popupWindowSex 代表性别 的弹出窗口
    private View viewWindow, viewWindowSex;
    //viewWindow 是 popupWindow 中的视图View
    //viewWindowSex 是 popupWindowSex 中的视图View
    private WheelView wheel, wheelSex;
    //wheel 是 viewWindow中的数字选择器
    //wheelSex 是 viewWindowSex 中的数据选择器
    private int value;
    private int registerStatus;
    private List<ProvinceData> provinceDataList;
    private List<CityData> cityData, cityCheckData;   //前者放置所有的城市数据，后者放置根据条件选择的城市数据
    private List<DistrictData> disData, disCheckData;//同上
    private List<String> ProNameList;
    private List<String> cityNameList;
    private List<String> districtNameList;
    private String[] proNameArray;
    private String[] citys;
    private String[] areas;
    private int position_province_item;
    private int position_city_item;
    private InputStreamReader reader;
    private Gson gson;
    private final int INTERVAL_TIME = 20;

    /**
     * le:这里解释一下 int registerStatus 的作用：由于身高，体重，年龄，步距等多个控件使用了同一个弹出窗口wheel
     * 当 触发事件时，都是wheel 发生了触发事件，则无法判断是谁发生了触发事件，也就不知道wheel的数据该给谁
     * 本文中和以往不同，以前多个按钮使用同一个OnClickListener,判断触发源时可以用 switch(view.getId),但这次不行，
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_register22);
        MyApplication.getInstance().addActivity(this);
//        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        modelDao = new ModelDao(getApplicationContext());
        titleView = (TitleView2) findViewById(R.id.titleview);    //标题栏
        titleView.setbg(R.drawable.register_titlebg);
        titleView.setTitle(R.string.registerInfo);

        etname = (EditText) findViewById(R.id.etname);         //名字，电话号码，密码，联系的客服编号
        etnum = (EditText) findViewById(R.id.etnum);           //紧急联系人姓名，紧急联系人电话号码
        etPassword = (EditText) findViewById(R.id.et_password);
        eturgentname = (EditText) findViewById(R.id.eturgentname);
        eturgentnum = (EditText) findViewById(R.id.eturgentnum);
        etCid = (EditText) findViewById(R.id.et_CID);

//        etInputCode = (EditText) findViewById(R.id.etInputCode);
//        textGetCode = (TextView) findViewById(R.id.textGetCode);
//        textGetCode.setOnClickListener(this);

        tvProvince = (TextView) findViewById(R.id.tv_Province);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvArea = (TextView) findViewById(R.id.tv_area);

        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvArea.setOnClickListener(this);

        age = (TextView) findViewById(R.id.tvage);              //年龄，性别，身高，体重,步距
        sex = (TextView) findViewById(R.id.tvsex);              //都设置了点击触发事件
        height = (TextView) findViewById(R.id.tvheight);
        weight = (TextView) findViewById(R.id.tvweight);
        tv_step_distance = (TextView) findViewById(R.id.tv_step_distance);
        age.setOnClickListener(this);
        sex.setOnClickListener(this);
        height.setOnClickListener(this);
        weight.setOnClickListener(this);
        tv_step_distance.setOnClickListener(this);

        msp = new SpannableString("<<" + "用户使用协议" + ">>");
        msp.setSpan(new UnderlineSpan(), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        argee_pro = (TextView) findViewById(R.id.argee_pro);     //“用户使用协议”
        argee_pro.setText(msp);                                  //设置了触发事件，但是点击事件为空
        argee_pro.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        msp_two = new SpannableString("我已注册");
        msp_two.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_already_register = (TextView) findViewById(R.id.tv_already_register);  //"我已注册"，触发事件是跳到另一个界面
        tv_already_register.setText(msp_two);
        tv_already_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mintent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mintent);
                finish();
            }
        });
        msp_two = new SpannableString("忘记密码");
        msp_two.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);  //"忘记密码"，触发事件是跳到另一个界面
        tv_forget_pwd.setText(msp_two);
        tv_forget_pwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mintent = new Intent(getApplicationContext(), ResetPwdActivity.class);
                startActivity(mintent);
                finish();
            }
        });

        tvnext = (TextView) findViewById(R.id.tvbasic_next);    //下一步
        tvnext.setOnClickListener(new OnClickListener() {       //触发事件是将用户注册信息上传到服务器，同时又保存到本地数据库
            @Override
            public void onClick(View v) {
                name = etname.getText().toString().trim();
                num = etnum.getText().toString().trim();
                password = etPassword.getText().toString();
                province = tvProvince.getText().toString().trim();
                city = tvCity.getText().toString().trim();
                area = tvArea.getText().toString().trim();
                nage = age.getText().toString().trim();
                nsex = sex.getText().toString().trim();
                nheight = height.getText().toString().trim();
                nweight = weight.getText().toString().trim();
                step_distance = tv_step_distance.getText().toString().trim();
                nungentName = eturgentname.getText().toString().trim();
                nungentNum = eturgentnum.getText().toString().trim();
                nCid = etCid.getText().toString().trim();
//                verifyCode = etInputCode.getText().toString().trim();

                //检查输入是否合法
                if (checkInputIllegal()) {
                    Register register = new Register();
                    register.setName(name);
                    register.setNum(num);
                    register.setPassword(password);
                    register.setProvince(province);
                    register.setCity(city);
                    register.setArea(area);

                    register.setAge(nage);
                    register.setSex(nsex);
                    register.setWeight(nweight);
                    register.setHeight(nheight);
                    register.setStepDistance(step_distance);
                    register.setUrgentContactName(nungentName);
                    register.setUrgentContactPhone(nungentNum);
                    register.setServiceId(nCid);

                    Intent intent = new Intent(RegisterActivity.this, RegisterTwoActivity.class);
                    intent.putExtra("registerInfo", register);
                    startActivity(intent);
                }
            }
        });


        box = (CheckBox) findViewById(R.id.checkbox_agree);    //“已阅读同意”的复选框
        box.setOnClickListener(this);

        viewWindow = getLayoutInflater().inflate(R.layout.bracelet_regi_view, null);
        viewWindowSex = getLayoutInflater().inflate(R.layout.bracelet_regi_sex, null);
        popupWindow = new PopupWindow(viewWindow, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindowSex = new PopupWindow(viewWindowSex, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
        popupWindowSex.setTouchable(true);
        popupWindowSex.setOutsideTouchable(true);
        popupWindowSex.setBackgroundDrawable(new BitmapDrawable());

        initWheel();       //初始化 身高，体重，年龄，步距 数据选择器
        initWheelSex();    //初始化 性别数据选择器

        btnsure = (Button) viewWindow.findViewById(R.id.btnsure);       //身高，体重，年龄，步距窗口的确定按钮
        btnsureSex = (Button) viewWindowSex.findViewById(R.id.btnsure);
        btnsure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerStatus == 1) {
                    age.setText(value + "");
                } else if (registerStatus == 2) {
                    height.setText(value + "");
                } else if (registerStatus == 3) {
                    weight.setText(value + "");
                } else if (registerStatus == 5) {
                    tv_step_distance.setText(value + "");
                }
                popupWindow.dismiss();
            }
        });

        btnsureSex.setOnClickListener(new OnClickListener() {  //性别窗口的确定按钮
            @Override
            public void onClick(View v) {
                if (registerStatus == 4) {
                    if (value == 1) {
                        sex.setText("男");
                    } else if (value == 0) {
                        sex.setText("女");
                    }
                    popupWindowSex.dismiss();
                }
            }
        });


        gson = new Gson();
        //刚进来的时候，立刻给省的ListView填充数据
        try {
            Type proType = new TypeToken<ArrayList<ProvinceData>>() {
            }.getType();
            provinceDataList = getLocData("ProvinceData.json", proType);  //这个是ProvinceData类型的列表
            ProNameList = new ArrayList<>();  //这个是String类型的列表

            for (ProvinceData data : provinceDataList) {
                ProNameList.add(data.getName());
            }

            proNameArray = new String[ProNameList.size()];
            ProNameList.toArray(proNameArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始化另外两个ListView
        cityData = new ArrayList<>();
        cityNameList = new ArrayList<>();
        cityCheckData = new ArrayList<>();

        disData = new ArrayList<>();
        districtNameList = new ArrayList<>();
        disCheckData = new ArrayList<>();
    }


    private void initWheel() {
        wheel = (WheelView) viewWindow.findViewById(R.id.wvAge);
        wheel.setAdapter(new NumericWheelAdapter(0, 200));
        wheel.addChangingListener(changedListener);        //给数据选择器设置监听器
        wheel.setCyclic(true);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }

    private void initWheelSex() {
        wheelSex = (WheelView) viewWindowSex.findViewById(R.id.wvSex);
        String countries[] = new String[]{"女", "男"};
        wheelSex.setVisibleItems(3);
        wheelSex.setAdapter(new ArrayWheelAdapter<>(countries));
        wheelSex.addChangingListener(changedListener);    //给数据选择器设置监听器
        wheelSex.setCyclic(true);
        wheelSex.setInterpolator(new AnticipateOvershootInterpolator());
    }


    //数据选择器的触发器
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            value = newValue;
        }
    };

//    public void getVerifyCode() {
//        JSONObject object = new JSONObject();
//        try {
//            object.put("phone", etnum.getText().toString().trim());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        HttpRequest.post(URLConstant.URL_GET_VERIFY_CODE, object.toString(), new HttpRequest.HttpRequestCallback() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                if (response.optString("error").equals("0")) {
//                    MethodUtils.showToast(getApplicationContext(), "验证码获取成功");
//                } else {
//                    MethodUtils.showToast(getApplicationContext(), "验证码获取失败: " + response.optString("error_info"));
//                }
//            }
//
//            @Override
//            public void onFailure() {
//                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
//            }
//        });
//    }

    public int count = INTERVAL_TIME;
    public Timer timer = null;
    public TimerTask task = null;
    public Message msg = null;

//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String content = (String) (msg.obj);
//            textGetCode.setText(content);
//            if (!textGetCode.isClickable() && content.equals("重新获取")) {
//                textGetCode.setClickable(true);
//                textGetCode.setBackgroundColor(getResources().getColor(R.color.distance_color));
//            }
//        }
//    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.textGetCode:
//
//                if (TextUtils.isEmpty(etnum.getText().toString())) {
//                    MethodUtils.showToast(RegisterActivity.this, "请先输入电话号码");
//                    return;
//                }
//                if (!Pattern.matches("^1[3|4|5|8][0-9]\\d{8}$", etnum.getText().toString().trim())) {
//                    MethodUtils.showToast(getApplicationContext(), "手机号码格式不对，请输入正确的手机号码");
//                    return;
//                }
//                textGetCode.setClickable(false);
//                textGetCode.setBackgroundColor(Color.parseColor("#363738"));
//
//                timer = new Timer(true);
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        msg = new Message();
//                        msg.obj = count + "秒";
//                        handler.sendMessage(msg);
//                        if (count == 0) {
//                            task.cancel();
//                            task = null;
//                            timer.cancel();
//                            timer = null;
//                            msg = new Message();
//                            msg.obj = "重新获取";
//                            count = INTERVAL_TIME;
//                            handler.sendMessage(msg);
//                        }
//                        count--;
//                    }
//                };
//                timer.schedule(task, 0, 1000);
//
//                /**
//                 *  向服务器获取验证码
//                 */
//                if (MethodUtils.isWifi(getApplicationContext())
//                        || MethodUtils.is3G(getApplicationContext())) {
//                    getVerifyCode();
//                } else {
//                    MethodUtils.showToast(getApplicationContext(), "请检查网络是否良好");
//                }
//                break;
            case R.id.tvage:
                registerStatus = 1;
                value = 55;
                wheel.setCurrentItem(value);
                popupWindow.setAnimationStyle(R.style.AnimationPreview);
                popupWindow.showAsDropDown(age, 0, 0);
                MyApplication.registerValue = 1;
                break;
            case R.id.tvsex:
                registerStatus = 4;
                value = 0;
                MyApplication.registerValue = 2;
                popupWindowSex.setAnimationStyle(R.style.AnimationPreview);
                popupWindowSex.showAsDropDown(sex, 0, 0);
                break;
            case R.id.tvheight:
                registerStatus = 2;
                value = 165;
                wheel.setCurrentItem(value);
                MyApplication.registerValue = 3;
                popupWindow.setAnimationStyle(R.style.AnimationPreview);
                popupWindow.showAsDropDown(height, 0, 0);
                break;
            case R.id.tvweight:
                registerStatus = 3;
                value = 55;
                wheel.setCurrentItem(value);
                MyApplication.registerValue = 4;
                popupWindow.setAnimationStyle(R.style.AnimationPreview);
                popupWindow.showAsDropDown(weight, 0, 0);
                break;
            case R.id.tv_Province:
                showProvinceListDialog();
                break;
            case R.id.tv_city:
                if (!TextUtils.isEmpty(tvProvince.getText().toString().trim()) && !TextUtils.isEmpty(tvCity.getText().toString().trim())) {
                    if (null != proNameArray && proNameArray.length > 0) {
                        for (int i = 0; i < proNameArray.length; i++) {
                            if (proNameArray[i].equals(tvProvince.getText().toString().trim())) {
                                position_province_item = i;
                                break;
                            }
                        }
                    }
                    showcityListDialog(position_province_item);
                } else if (!TextUtils.isEmpty(tvProvince.getText().toString().trim())) {
                    showcityListDialog(position_province_item);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请先选择省份");
                }
                break;
            case R.id.tv_area:
                if (!TextUtils.isEmpty(tvCity.getText().toString().trim()) && !TextUtils.isEmpty(tvArea.getText().toString().trim())) {

                    if (null != proNameArray && proNameArray.length > 0) {
                        for (int i = 0; i < proNameArray.length; i++) {
                            if (proNameArray[i].equals(tvProvince.getText().toString().trim())) {
                                position_province_item = i;
                                break;
                            }
                        }
                    }
                    citysForAreaDialog(position_province_item);

                    if (null != citys && citys.length > 0) {
                        for (int i = 0; i < citys.length; i++) {
                            if (citys[i].equals(tvCity.getText().toString().trim())) {
                                position_city_item = i;
                                break;
                            }
                        }
                    }
                    showAreaListDialog(position_city_item);
                } else if (!TextUtils.isEmpty(tvCity.getText().toString().trim())) {
                    showAreaListDialog(position_city_item);
                } else {
                    MethodUtils.showToast(getApplicationContext(), "请先选择城市");
                }
                break;

            case R.id.tv_step_distance:
                registerStatus = 5;
                value = 50;
                wheel.setCurrentItem(value);
                MyApplication.registerValue = 5;
                popupWindow.setAnimationStyle(R.style.AnimationPreview);
                popupWindow.showAsDropDown(tv_step_distance, 0, 0);
                break;
            case R.id.checkbox_agree:
                flag = box.isChecked();
                break;
        }
    }

    private void showProvinceListDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("请选择省份");
        /**

         * 1、public Builder setItems(int itemsId, final OnClickListener

         * listener) itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。 2、public Builder

         * setItems(CharSequence[] items, final OnClickListener listener)

         * items表示用于显示在列表中的字符串数组

         */
        builder.setItems(proNameArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击了选项以后，要干很多事情
                tvProvince.setText(proNameArray[which]);       //第一：更新界面
                tvCity.setText("");
                tvArea.setText("");
                position_province_item = which;                //第二：保存你现在点击了那个，
                // 方便之后显示该省份对应的城市
                dialog.dismiss();                                //第三：关闭界面
            }
        });
        builder.create().show();

    }

    private void showcityListDialog(int position) {

        cityData.clear();//添加数据前，先删除旧数据
        cityNameList.clear();
        cityCheckData.clear();

        districtNameList.clear();  //清除区的数据列表，并通知区更新数据
        disData.clear();
        disCheckData.clear();
        try {
            if (null == provinceDataList || provinceDataList.size() == 0)
                return;
            int proId = provinceDataList.get(position).getProID();    //proId
            Type cityType = new TypeToken<ArrayList<CityData>>() {
            }.getType();
            cityData = getLocData("CityData.json", cityType);
            for (CityData data : cityData) {
                if (data.getProID() == proId) {      //根据proId，查找名下的城市
                    cityNameList.add(data.getName());
                    cityCheckData.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        citys = new String[cityNameList.size()];
        cityNameList.toArray(citys);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(citys, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                tvCity.setText(citys[which]);
                tvArea.setText("");
                dialog.dismiss();
                position_city_item = which;
            }

        });
        builder.create().show();

    }

    private void citysForAreaDialog(int position) {
        cityData.clear();//添加数据前，先删除旧数据
        cityNameList.clear();
        cityCheckData.clear();

        districtNameList.clear();  //清除区的数据列表，并通知区更新数据
        disData.clear();
        disCheckData.clear();
        //   disAdapter.notifyDataSetChanged();
        try {
            if (null == provinceDataList || provinceDataList.size() == 0)
                return;
            int proId = provinceDataList.get(position).getProID();    //proId
            Type cityType = new TypeToken<ArrayList<CityData>>() {
            }.getType();
            cityData = getLocData("CityData.json", cityType);
            for (CityData data : cityData) {
                if (data.getProID() == proId) {      //根据proId，查找名下的城市
                    cityNameList.add(data.getName());
                    cityCheckData.add(data);
                }
            }
            //   cityAdapter.notifyDataSetChanged();    //通知listView更新数据
        } catch (IOException e) {
            e.printStackTrace();
        }
        citys = new String[cityNameList.size()];
        cityNameList.toArray(citys);


    }

    private void showAreaListDialog(int position) {
        districtNameList.clear();
        disData.clear();
        disCheckData.clear();
        try {
            if (null == cityCheckData || cityCheckData.size() == 0)
                return;
            int cityId = cityCheckData.get(position).getCityID();
            Log.i("TAG", "cityId:" + cityId);
            Type disType = new TypeToken<ArrayList<DistrictData>>() {
            }.getType();
            disData = getLocData("DistrictData.json", disType);
            for (DistrictData data : disData) {
                if (data.getCityID() == cityId) {
                    districtNameList.add(data.getDisName());
                    disCheckData.add(data);
                }
            }
            //   disAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }

        areas = new String[districtNameList.size()];
        districtNameList.toArray(areas);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(areas, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {
                tvArea.setText(areas[which]);
                dialog.dismiss();
            }

        });

        builder.create().show();

    }

    /**
     * 泛型方法，读取assets目录下的json文件，并填充到List中
     *
     * @param fileName
     * @param collectionType
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> List<T> getLocData(String fileName, Type collectionType) throws IOException {
        //获取assets目录下的json文件
        reader = new InputStreamReader(getAssets().open(fileName));
        List<T> locDatas = gson.fromJson(reader, collectionType);
        reader.close();
        return locDatas;
    }

    private boolean checkInputIllegal() {
        if (TextUtils.isEmpty(name)) {
            MethodUtils.showToast(getApplicationContext(), "姓名不能为空");
            return false;
        }
        if (TextUtils.isEmpty(num)) {
            MethodUtils.showToast(getApplicationContext(), "电话号码不能为空");
            return false;
        }
        if (!Pattern.matches("^1[3|4|5|8][0-9]\\d{8}$", num)) {
            MethodUtils.showToast(getApplicationContext(), "手机号码格式不对，请输入正确的手机号码");
            return false;
        }
//        if (TextUtils.isEmpty(verifyCode)) {
//            MethodUtils.showToast(getApplicationContext(), "验证码不能为空");
//            return false;
//        }
        if (TextUtils.isEmpty(password)) {
            MethodUtils.showToast(getApplicationContext(), "密码不能为空");
            return false;
        }
        if (TextUtils.isEmpty(nsex)) {
            MethodUtils.showToast(getApplicationContext(), "性别不能为空");
            return false;
        }
        if (TextUtils.isEmpty(nage)) {
            MethodUtils.showToast(getApplicationContext(), "年龄不能为空");
            return false;
        }
        if (TextUtils.isEmpty(nweight)) {
            MethodUtils.showToast(getApplicationContext(), "体重不能为空");
            return false;
        }
        if (TextUtils.isEmpty(nheight)) {
            MethodUtils.showToast(getApplicationContext(), "身高不能为空");
            return false;
        }

        if (TextUtils.isEmpty(province)) {
            MethodUtils.showToast(getApplicationContext(), "省份不能为空");
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            MethodUtils.showToast(getApplicationContext(), "城市不能为空");
            return false;
        }
        if (TextUtils.isEmpty(area)) {
            MethodUtils.showToast(getApplicationContext(), "区/县不能为空");
            return false;
        }
        if (TextUtils.isEmpty(step_distance)) {
            MethodUtils.showToast(getApplicationContext(), "步距不能为空");
            return false;
        }
        if (nungentName.equals("")) {
            MethodUtils.showToast(getApplicationContext(), "紧急联系人姓名不能为空");
            return false;
        }
        if (nungentNum.equals("")) {
            MethodUtils.showToast(getApplicationContext(), "紧急联系人号码不能为空");
            return false;
        }
        //紧急联系人号码 两方面验证，移动电话号码 和 固定电话号码
        if (!Pattern.matches("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$", nungentNum)
                && !Pattern.matches("^1[3|4|5|8][0-9]\\d{8}$", nungentNum)) {
            MethodUtils.showToast(getApplicationContext(), "紧急联系人号码格式不对");
            return false;
        }
        if (TextUtils.isEmpty(nCid)) {
            MethodUtils.showToast(getApplicationContext(), "请把客服号填写完整");
            return false;
        }
        if (!flag) {
            MethodUtils.showToast(getApplicationContext(), "请选择同意协议");
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
    }

//    public void onEventMainThread(Event event) {
//        if (!TextUtils.isEmpty(event.msg)) {
//            System.out.println(event.msg);
//            if (event.msg.contains("验证码是："))
//                etInputCode.setText(getCodeFromMsg(event.msg));
//        }
//    }

    public String getCodeFromMsg(String msg) {
        String[] strs = msg.split("验证码是：");
        return strs[1].substring(0, 4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
