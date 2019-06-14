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
import android.util.DisplayMetrics;
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
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class BasicInfoActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {
    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESIZE_REQUEST = 0xa2;

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
    RadioButton rbYes;
    RadioButton rbNo;
    ImageView ivPhoto;
    Button btnNext;
    EditText etCid;
    EditText etAddress;

    private Register registerInfo = null;

    private String[] educations = new String[]{"大学", "高中", "小学", "其它"};
    private String[] occupation = new String[]{"工人", "军人", "农民", "工务员", "其它"};
    private String[] nations = new String[]{"汉族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族", "朝鲜族", "满族", "侗族瑶族", "白族",
            "土家族", "哈尼族", "哈萨克族", "傣族", "黎族", "僳僳族", "佤族", "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克", "孜族", "土族",
            "达斡尔族", "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "塔吉克族", "怒族", "乌孜别克族", "俄罗斯族", "鄂温克族"
            , "德昂族", "保安族", "裕固族", "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"};
    private PopupWindow adviceWindow;
    private View adviceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.base_info_activity);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleview = (TitleView) findViewById((R.id.titleview));     //设置标题栏
        titleview.setTitle(R.string.parsonal_basic_info);
        titleview.setcolor("#76c5f0");
        titleview.settextcolor("#ffffff");
        titleview.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
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

        rbYes = (RadioButton) findViewById(R.id.rb_yes);
        rbNo = (RadioButton) findViewById(R.id.rb_no);
        etAddress = (EditText) findViewById(R.id.et_address);

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
            public void onClick(View v) {
                choosePhoto();
                if (null != adviceWindow) adviceWindow.dismiss();
            }
        });
        tvTakePhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                if (null != adviceWindow) adviceWindow.dismiss();
            }
        });
    }

    private void initListener() {
        ivPhoto.setOnClickListener(this);
        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        tvNation.setOnClickListener(this);
        tvEducationLevel.setOnClickListener(this);
        tvOccupation.setOnClickListener(this);

        rbYes.setOnCheckedChangeListener(this);
        rbNo.setOnCheckedChangeListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initData() {

        registerInfo = (Register) SpHelp.getObject(SpHelp.REGISTER_INFO);

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

        tvNation.setText(registerInfo.getNation());
        tvOccupation.setText(registerInfo.getOccupation());
        tvEducationLevel.setText(registerInfo.getEducation());
        etAddress.setText(registerInfo.getAddress());
        if (registerInfo.isWatchHealthTv()) {
            rbYes.setChecked(true);
            rbNo.setChecked(false);
        } else {
            rbYes.setChecked(false);
            rbNo.setChecked(true);
        }

        etEgName.clearFocus();
        etAddress.clearFocus();
        etEgNum.clearFocus();
        etAge.clearFocus();
        etHeight.clearFocus();
        etWeight.clearFocus();
    }

    @Override
    public void onClick(View v) {
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
            case R.id.btn_mainPage_next:
                updateUserInfo();
                intent = new Intent(getApplicationContext(), OtherInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 根据当前界面用户设置的新数据，更新用户信息
     */
    private void updateUserInfo() {

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
        registerInfo.setAddress(etAddress.getText().toString().trim());
        SpHelp.saveObject(SpHelp.REGISTER_INFO, registerInfo);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                registerInfo.setWatchHealthTv(true);
                break;
            case R.id.rb_no:
                rbNo.setChecked(isChecked);
                rbYes.setChecked(!isChecked);
                registerInfo.setWatchHealthTv(false);
                break;
        }
    }

    private void showEducationListDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(educations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerInfo.setEducation(educations[which]);
                tvEducationLevel.setText(educations[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showOccupationListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(occupation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                registerInfo.setOccupation(occupation[which]);
                tvOccupation.setText(occupation[which]);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showNationsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(nations, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                registerInfo.setNation(nations[which]);
                tvNation.setText(nations[which]);
                dialog.dismiss();
            }

        });
        builder.create().show();
    }

    // 从本地相册选取图片作为头像
    private void choosePhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");//图片
        startActivityForResult(galleryIntent, CODE_GALLERY_REQUEST);
    }

    //获取路径
    private Uri getImageUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME));
    }

    // 启动手机相机拍摄照片作为头像
    private void openCamera() {
        if (MethodUtils.hasSdcard()) {
            Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");//拍照
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(cameraIntent, CODE_CAMERA_REQUEST);
        } else {
            MethodUtils.showToast(BasicInfoActivity.this, "请插入sd卡");
        }
    }

    // 重新裁剪照片大小
    public void resizeImage(Uri uri) {
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
                                    Intent intent) {
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
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
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
    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String   regEx  =  "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


}
