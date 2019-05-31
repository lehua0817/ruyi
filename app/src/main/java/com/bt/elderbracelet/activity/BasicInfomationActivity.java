package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.others.CityData;
import com.bt.elderbracelet.entity.others.DistrictData;
import com.bt.elderbracelet.entity.others.PersonalDetailOne;
import com.bt.elderbracelet.entity.others.PersonalDetailThree;
import com.bt.elderbracelet.entity.others.PersonalDetailTwo;
import com.bt.elderbracelet.entity.others.ProvinceData;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class BasicInfomationActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESIZE_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;

    TitleView titleview;
    EditText etName;
    RadioButton rbMale;
    RadioButton rbFemale;
    EditText etAge;
    TextView tvNation;
    EditText etHeight;
    EditText etWeight;
    TextView tvEducationLevel;
    TextView tvOccupation;
    EditText etNum;
    EditText etEgName;
    EditText etEgNum;
    TextView tvProvince;
    TextView tvCity;
    TextView tvArea;
    EditText etDetailsAddress;
    RadioButton rbYes;
    RadioButton rbNo;
    ImageView ivPhoto;
    Button btnNext;
    EditText etCid;

    private ArrayList<Register> registerList = null;
    private Register registerInfo = null;
    private ModelDao modelDao;
    private InputStreamReader reader;
    private Gson gson;
    private List<ProvinceData> provinceDataList;
    private List<CityData> cityData, cityCheckData;   //前者放置所有的城市数据，后者放置根据条件选择的城市数据
    private List<DistrictData> disData, disCheckData;//同上
    private List<String> ProNameList;
    private List<String> cityNameList;
    private List<String> districtNameList;
    private String[] proNameArray;
    private String[] citys;
    private String[] areas;
    private String[] educations = new String[]{"大学", "高中", "小学", "其它"};
    private String[] occupation = new String[]{"工人", "军人", "农民", "工务员", "其它"};
    private String[] nations = new String[]{"汉族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族", "朝鲜族", "满族", "侗族瑶族", "白族",
            "土家族", "哈尼族", "哈萨克族", "傣族", "黎族", "僳僳族", "佤族", "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克", "孜族", "土族",
            "达斡尔族", "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "塔吉克族", "怒族", "乌孜别克族", "俄罗斯族", "鄂温克族"
            , "德昂族", "保安族", "裕固族", "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"};
    private int position_province_item;
    private int position_city_item;
    private PersonalDetailOne detailOne;
    private PopupWindow adviceWindow;
    private View adviceView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_presonal_one_page);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById((R.id.titleview));     //设置标题栏
        titleview.setTitle(R.string.parsonal_basic_info);
        titleview.setcolor("#76c5f0");
        titleview.settextcolor("#ffffff");
        titleview.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button)
            {
                finish();
            }
        });

        ivPhoto = (ImageView) findViewById(R.id.iv_photo);     //设置用户头像
        if (SpHelp.getUserPhoto() != null) {
            ivPhoto.setImageBitmap(SpHelp.getUserPhoto());
        }

        etName = (EditText) findViewById(R.id.et_name);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        etAge = (EditText) findViewById(R.id.et_age);
        tvNation = (TextView) findViewById(R.id.tv_nation);
        etHeight = (EditText) findViewById(R.id.et_height);
        etWeight = (EditText) findViewById(R.id.et_weight);
        tvEducationLevel = (TextView) findViewById(R.id.tv_education_level);
        tvOccupation = (TextView) findViewById(R.id.tv_occupation);
        etNum = (EditText) findViewById(R.id.et_num);
        etEgName = (EditText) findViewById(R.id.et_eg_name);
        etEgNum = (EditText) findViewById(R.id.et_eg_num);
        etCid = (EditText) findViewById(R.id.et_cid);

        tvProvince = (TextView) findViewById(R.id.tv_Province);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvArea = (TextView) findViewById(R.id.tv_area);
        etDetailsAddress = (EditText) findViewById(R.id.et_details_address);

        rbYes = (RadioButton) findViewById(R.id.rb_yes);
        rbNo = (RadioButton) findViewById(R.id.rb_no);

        btnNext = (Button) findViewById(R.id.btn_mainPage_next);

        // 更改头像
        adviceView = this.getLayoutInflater().inflate(R.layout.bracelet_choice_photo, null);
        TextView tvChoicePhoto = (TextView) adviceView.findViewById(R.id.tv_choice_photo);
        TextView tvTakePhoto = (TextView) adviceView.findViewById(R.id.tv_takePhoto);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels / 2;//宽度
        adviceWindow = new PopupWindow(adviceView, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        adviceWindow.setOutsideTouchable(true);
        adviceWindow.setBackgroundDrawable(new BitmapDrawable());
        tvChoicePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                choosePhoto();
                if (null != adviceWindow) adviceWindow.dismiss();
            }
        });
        tvTakePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openCamera();
                if (null != adviceWindow) adviceWindow.dismiss();
            }
        });
    }

    private void initListener()
    {
        ivPhoto.setOnClickListener(this);
        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

//        tvProvince.setOnClickListener(this);
//        tvArea.setOnClickListener(this);
//        tvCity.setOnClickListener(this);

        tvNation.setOnClickListener(this);
        tvEducationLevel.setOnClickListener(this);
        tvOccupation.setOnClickListener(this);

        rbYes.setOnCheckedChangeListener(this);
        rbNo.setOnCheckedChangeListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initData()
    {
        gson = new Gson();

        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    getUserDetailInfo();
                }
            });
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getRegisterInfo() == null) {
            return;
        }

        registerInfo = getRegisterInfo();         //给register表中的属性赋值

        etName.setText(registerInfo.getName());
        etAge.setText(registerInfo.getAge());
        etHeight.setText(registerInfo.getHeight());
        etWeight.setText(registerInfo.getWeight());
        etNum.setText(registerInfo.getNum());
        etEgName.setText(registerInfo.getUrgentContactName());
        etEgNum.setText(registerInfo.getUrgentContactPhone());
        tvProvince.setText(registerInfo.getProvince());
        tvCity.setText(registerInfo.getCity());
        tvArea.setText(registerInfo.getArea());
        etCid.setText(registerInfo.getServiceId());

        if (registerInfo.getSex().equals("男")) {
            rbMale.setChecked(true);
            rbFemale.setChecked(false);
        } else {
            rbMale.setChecked(false);
            rbFemale.setChecked(true);
        }

        // 接下来 给PersonalDetailOne表中的8个属性赋值
        if (SpHelp.getObject(SpHelp.PERSONAL_DETAIL_ONE) == null) {
            detailOne = new PersonalDetailOne();
        } else {
            detailOne = (PersonalDetailOne) SpHelp.getObject(SpHelp.PERSONAL_DETAIL_ONE);
        }


        tvNation.setText(detailOne.getNation());
        tvOccupation.setText(detailOne.getOccupation());
        tvEducationLevel.setText(detailOne.getEducation());
        etDetailsAddress.setText(detailOne.getAddress());
        if (detailOne.isWatchHealthTv()) {
            rbYes.setChecked(true);
            rbNo.setChecked(false);
        } else {
            rbYes.setChecked(false);
            rbNo.setChecked(true);
        }

        etEgName.clearFocus();
        etDetailsAddress.clearFocus();
        etEgNum.clearFocus();
        etAge.clearFocus();
        etHeight.clearFocus();
        etWeight.clearFocus();


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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        modelDao = null;
        registerList.clear();
        registerList = null;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_photo:
                if (null != adviceWindow) {
                    adviceWindow.showAsDropDown(v, -8, 0);
                }
                break;
            case R.id.tv_education_level:
                showEducationListDialog();
                break;
            case R.id.tv_nation:
                showNationsDialog();
                break;
            case R.id.tv_occupation:
                showOccupationListDialog();
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
            case R.id.btn_mainPage_next:
                intent = new Intent(getApplicationContext(), PersonalTwoActivity.class);
                Bundle bundle = new Bundle();

                if (null != detailOne) {
                    if (!TextUtils.isEmpty(etDetailsAddress.getText().toString())) {
                        detailOne.setAddress(StringFilter(etDetailsAddress.getText().toString().trim()));
                    }
                    bundle.putSerializable("detailOne", detailOne);

                    intent.putExtras(bundle);
                }

                if (getNewRegisterInfo() != null) {
                    bundle.putSerializable("registerInfo", getNewRegisterInfo());
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    public Register getRegisterInfo()
    {
        Register info = null;
        modelDao = new ModelDao(this);
        registerList = modelDao.queryAllRegister();
        if (registerList != null && registerList.size() > 0) {
            info = registerList.get(registerList.size() - 1);
        }
        return info;
    }


    /**
     * 根据当前界面用户设置的新数据，更新 注册信息
     */
    private Register getNewRegisterInfo()
    {
        if (registerInfo == null) {
            return null;
        }
        registerInfo.setName(etName.getText().toString().trim().trim());
        registerInfo.setNum(etNum.getText().toString().trim());
        registerInfo.setAge(etAge.getText().toString().trim());
        if (rbMale.isChecked()) {
            registerInfo.setSex("男");
        } else {
            registerInfo.setSex("女");
        }
        registerInfo.setWeight(etWeight.getText().toString().trim());
        registerInfo.setHeight(etHeight.getText().toString().trim());
        registerInfo.setUrgentContactName(etEgName.getText().toString().trim());
        registerInfo.setUrgentContactPhone(etEgNum.getText().toString().trim());
        registerInfo.setProvince(tvProvince.getText().toString().trim());
        registerInfo.setCity(tvCity.getText().toString().trim());
        registerInfo.setArea(tvArea.getText().toString().trim());
        return registerInfo;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId()) {
            case R.id.rb_male:
                rbMale.setChecked(isChecked);
                rbFemale.setChecked(!isChecked);
                break;
            case R.id.rb_female:
                rbFemale.setChecked(isChecked);
                rbMale.setChecked(!isChecked);
                break;

            case R.id.rb_yes:
                rbYes.setChecked(isChecked);
                rbNo.setChecked(!isChecked);
                detailOne.setWatchHealthTv(true);
                break;
            case R.id.rb_no:
                rbNo.setChecked(isChecked);
                rbYes.setChecked(!isChecked);
                detailOne.setWatchHealthTv(false);
                break;
        }
    }

    private void showProvinceListDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //  builder.setTitle("请选择省份");
        /**

         * 1、public Builder setItems(int itemsId, final OnClickListener

         * listener) itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。 2、public Builder

         * setItems(CharSequence[] items, final OnClickListener listener)

         * items表示用于显示在列表中的字符串数组

         */
        builder.setItems(proNameArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {                                                    //点击了选项以后，要干很多事情
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

    private void showcityListDialog(int position)
    {

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

            public void onClick(DialogInterface dialog, int which)
            {
                tvCity.setText(citys[which]);
                tvArea.setText("");
                dialog.dismiss();
                position_city_item = which;
            }

        });
        builder.create().show();

    }

    private void citysForAreaDialog(int position)
    {
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

    private void showAreaListDialog(int position)
    {
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

            public void onClick(DialogInterface dialog, int which)
            {
                tvArea.setText(areas[which]);
                dialog.dismiss();
            }

        });

        builder.create().show();

    }

    private void showEducationListDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(educations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                detailOne.setEducation(educations[which]);
                tvEducationLevel.setText(educations[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showOccupationListDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(occupation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                detailOne.setOccupation(occupation[which]);
                tvOccupation.setText(occupation[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showNationsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(nations, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which)
            {

                detailOne.setNation(nations[which]);
                tvNation.setText(nations[which]);
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
    public <T> List<T> getLocData(String fileName, Type collectionType) throws IOException
    {
        //获取assets目录下的json文件
        reader = new InputStreamReader(getAssets().open(fileName));
        List<T> locDatas = gson.fromJson(reader, collectionType);
        reader.close();
        return locDatas;
    }

    // 从本地相册选取图片作为头像
    private void choosePhoto()
    {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");//图片
        startActivityForResult(galleryIntent, CODE_GALLERY_REQUEST);
    }

    //获取路径
    private Uri getImageUri()
    {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }

    // 启动手机相机拍摄照片作为头像
    private void openCamera()
    {
        if (MethodUtils.hasSdcard()) {
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");//拍照
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(cameraIntent, CODE_CAMERA_REQUEST);
        } else {
            MethodUtils.showToast(BasicInfomationActivity.this, "请插入sd卡");
        }
    }

    // 重新裁剪照片大小
    public void resizeImage(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可以裁剪
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESIZE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_GALLERY_REQUEST:
                    resizeImage(intent.getData());
                    break;
                case CODE_CAMERA_REQUEST:
                    resizeImage(getImageUri());
                    break;
                case CODE_RESIZE_REQUEST:
                    if (intent != null) {
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            Bitmap sourcePhoto = bundle.getParcelable("data");
                            Bitmap bitmap = toRoundBitmap(sourcePhoto);
                            ivPhoto.setImageBitmap(bitmap);
                            SpHelp.saveUserPhoto(bitmap);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// 设置画笔无锯齿

        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

        return output;
    }


    // 过滤特殊字符
    public static String StringFilter(String str) throws PatternSyntaxException
    {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    private void getUserDetailInfo()
    {
        Map<String, Object> params = new HashMap<>();
        params.put("id", SpHelp.getUserId());
        HttpRequest.get(URLConstant.URL_GET_PERSONAL_DETAIL, null, params, new HttpRequest.HttpRequestCallback() {
            @Override
            public void onSuccess(JSONObject response)
            {
                if (response.optString("error").equals("0")) {
                    JSONObject data = response.optJSONObject("data");

                    PersonalDetailOne one = new PersonalDetailOne();
                    one.setNation(data.optString("folk"));
                    one.setEducation(data.optString("education"));
                    one.setOccupation(data.optString("job"));
                    one.setAddress(data.optString("address"));
                    one.setWatchHealthTv(data.optBoolean("isWatchHealthTV"));

                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_ONE, one);

                    PersonalDetailTwo two = new PersonalDetailTwo();
                    two.setHobby(data.optString("art"));
                    two.setSport(data.optString("sportsRate"));
                    two.setDiet(data.optString("diet"));
                    two.setSmoke(data.optString("smoke"));
                    two.setDrink(data.optString("drink"));
                    two.setAllergic(data.optString("allergic"));

                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_TWO, two);

                    PersonalDetailThree three = new PersonalDetailThree();
                    three.setIllness(data.optString("illness"));
                    three.setPhysique(data.optString("body"));
                    SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_THREE, three);

                } else {
                    MethodUtils.showToast(getApplicationContext(), "验证失败: " + response.optString("error_info"));
                }
            }

            @Override
            public void onFailure()
            {
                MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
            }
        });
    }

}
