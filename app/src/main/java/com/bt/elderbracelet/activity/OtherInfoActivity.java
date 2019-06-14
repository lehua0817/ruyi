package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class OtherInfoActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = OtherInfoActivity.class.getSimpleName();
    TitleView titleview;
    //文化娱乐
    CheckBox cbBooks;
    CheckBox cbDrama;
    CheckBox cbDance;
    CheckBox cbOther;
    //体育锻炼
    CheckBox cbTrainEveryDay;
    CheckBox cbTrainManyEveryWeek;
    CheckBox cbTrainOccasionally;
    CheckBox cbTrainNever;
    //饮食习惯
    CheckBox cbMeatAndVegetables;
    CheckBox cbMeatMain;
    CheckBox cbVegetablesMain;
    CheckBox cbAddictedToSalt;
    CheckBox cbAddictedToOil;
    // 抽烟情况
    CheckBox cbSmokeNever;
    CheckBox cbSmokeQuit;
    CheckBox cbSmoking;
    // 喝酒情况
    CheckBox cbDrinkNever;
    CheckBox cbDrinkOccasionally;
    CheckBox cbDrinkFrequency;
    CheckBox cbDrinkEveryDay;
    // 过敏情况
    CheckBox cbHighProteinFood;
    CheckBox cbSeafood;
    CheckBox cbPollen;
    CheckBox cbDrug;
    CheckBox cbPlaster;
    CheckBox cbCosmetic;
    CheckBox cbAlcohol;
    CheckBox cbPaint;
    CheckBox cbNothing;
    // 以往病史
    CheckBox cbHighPressure;
    CheckBox cbHighXuezhi;
    CheckBox cbCardiovas;
    CheckBox cbHighSugar;
    CheckBox cbLowSugar;
    CheckBox cbArthritis;
    CheckBox cbNeckPain;
    CheckBox cbRheumaticPain;
    CheckBox cbDisorders;
    CheckBox cbBreathDiseases;
    CheckBox cbEyeDisease;
    CheckBox cbLiverDisease;
    CheckBox cbTumor;
    CheckBox cbSubHealth;
    CheckBox cbOthers;

    Button btnFinish;

    private Register registerInfo;

    private String art = "";         // 喜欢的文娱项目
    private String sportsRate = "";  // 体育锻炼频道
    private String diet = "";        // 饮食习惯
    private String smoke = "";       // 抽烟状况
    private String drink = "";       // 喝酒状况
    private String allergic = "";     // 过敏情况
    private String illness = "";      // 患病情况

    private List<String> hobbyList = new ArrayList<>();
    private List<String> dietList = new ArrayList<>();
    private List<String> allergicList = new ArrayList<>();
    private List<String> illnessList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.other_info_activity);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titleview = (TitleView) findViewById(R.id.titleview);
        titleview.setTitle(R.string.parsonal_basic_info);
        titleview.setcolor("#76c5f0");
        titleview.settextcolor("#ffffff");
        titleview.setBack(R.drawable.steps_back, new onBackLister() {
            @Override
            public void onClick(View button) {
                finish();
            }
        });

        //兴趣爱好 (多选)
        cbBooks = (CheckBox) findViewById(R.id.cb_books);
        cbDrama = (CheckBox) findViewById(R.id.cb_drama);
        cbDance = (CheckBox) findViewById(R.id.cb_dance);
        cbOther = (CheckBox) findViewById(R.id.cb_other);

        //锻炼运动
        cbTrainEveryDay = (CheckBox) findViewById(R.id.cb_train_every_day);
        cbTrainManyEveryWeek = (CheckBox) findViewById(R.id.cb_train_many_every_week);
        cbTrainOccasionally = (CheckBox) findViewById(R.id.cb_train_occasionally);
        cbTrainNever = (CheckBox) findViewById(R.id.cb_train_never);

        //饮食习惯 （多选）
        cbMeatAndVegetables = (CheckBox) findViewById(R.id.cb_meat_and_vegetables);
        cbMeatMain = (CheckBox) findViewById(R.id.cb_Meat_main);
        cbVegetablesMain = (CheckBox) findViewById(R.id.cb_vegetables_main);
        cbAddictedToSalt = (CheckBox) findViewById(R.id.cb_addicted_to_salt);
        cbAddictedToOil = (CheckBox) findViewById(R.id.cb_Addicted_to_oil);

        //抽烟习惯
        cbSmokeNever = (CheckBox) findViewById(R.id.cb_smoke_never);
        cbSmokeQuit = (CheckBox) findViewById(R.id.cb_smoke_quit);
        cbSmoking = (CheckBox) findViewById(R.id.cb_smoking);

        //喝酒习惯
        cbDrinkNever = (CheckBox) findViewById(R.id.cb_drink_never);
        cbDrinkOccasionally = (CheckBox) findViewById(R.id.cb_drink_occasionally);
        cbDrinkFrequency = (CheckBox) findViewById(R.id.cb_drink_frequency);
        cbDrinkEveryDay = (CheckBox) findViewById(R.id.cb_drink_every_day);

        //过敏情况（多选）
        cbHighProteinFood = (CheckBox) findViewById(R.id.cb_High_protein_food);
        cbSeafood = (CheckBox) findViewById(R.id.cb_seafood);
        cbPollen = (CheckBox) findViewById(R.id.cb_pollen);
        cbDrug = (CheckBox) findViewById(R.id.cb_drug);
        cbPlaster = (CheckBox) findViewById(R.id.cb_Plaster);
        cbCosmetic = (CheckBox) findViewById(R.id.cb_cosmetic);
        cbAlcohol = (CheckBox) findViewById(R.id.cb_alcohol);
        cbPaint = (CheckBox) findViewById(R.id.cb_paint);
        cbNothing = (CheckBox) findViewById(R.id.cb_nothing);


        // 以往病史(多选)
        cbHighPressure = (CheckBox) findViewById(R.id.cb_high_pressure);
        cbHighXuezhi = (CheckBox) findViewById(R.id.high_xuezhi);
        cbCardiovas = (CheckBox) findViewById(R.id.cb_cardiovas);
        cbHighSugar = (CheckBox) findViewById(R.id.cb_high_sugar);
        cbLowSugar = (CheckBox) findViewById(R.id.cb_low_sugar);
        cbArthritis = (CheckBox) findViewById(R.id.cb_arthritis);
        cbNeckPain = (CheckBox) findViewById(R.id.cb_neck_pain);
        cbRheumaticPain = (CheckBox) findViewById(R.id.cb_rheumatic_pain);
        cbDisorders = (CheckBox) findViewById(R.id.cb_disorders);
        cbBreathDiseases = (CheckBox) findViewById(R.id.cb_breath_diseases);
        cbEyeDisease = (CheckBox) findViewById(R.id.cb_eye_disease);
        cbLiverDisease = (CheckBox) findViewById(R.id.cb_Liver_disease);
        cbTumor = (CheckBox) findViewById(R.id.cb_tumor);
        cbSubHealth = (CheckBox) findViewById(R.id.cb_sub_health);
        cbOthers = (CheckBox) findViewById(R.id.cb_others);

        btnFinish = (Button) findViewById(R.id.btn_finish);
    }

    private void initListener() {
        cbNothing.setOnCheckedChangeListener(this);
        cbPaint.setOnCheckedChangeListener(this);
        cbAlcohol.setOnCheckedChangeListener(this);
        cbCosmetic.setOnCheckedChangeListener(this);
        cbPlaster.setOnCheckedChangeListener(this);
        cbDrug.setOnCheckedChangeListener(this);
        cbPollen.setOnCheckedChangeListener(this);
        cbSeafood.setOnCheckedChangeListener(this);
        cbHighProteinFood.setOnCheckedChangeListener(this);
        cbDrinkEveryDay.setOnCheckedChangeListener(this);
        cbDrinkFrequency.setOnCheckedChangeListener(this);
        cbDrinkOccasionally.setOnCheckedChangeListener(this);
        cbDrinkNever.setOnCheckedChangeListener(this);
        cbSmoking.setOnCheckedChangeListener(this);
        cbSmokeQuit.setOnCheckedChangeListener(this);
        cbSmokeNever.setOnCheckedChangeListener(this);
        cbAddictedToOil.setOnCheckedChangeListener(this);
        cbAddictedToSalt.setOnCheckedChangeListener(this);
        cbVegetablesMain.setOnCheckedChangeListener(this);
        cbMeatMain.setOnCheckedChangeListener(this);
        cbMeatAndVegetables.setOnCheckedChangeListener(this);
        cbTrainNever.setOnCheckedChangeListener(this);
        cbTrainOccasionally.setOnCheckedChangeListener(this);
        cbTrainManyEveryWeek.setOnCheckedChangeListener(this);
        cbTrainEveryDay.setOnCheckedChangeListener(this);
        cbOther.setOnCheckedChangeListener(this);
        cbDrama.setOnCheckedChangeListener(this);
        cbBooks.setOnCheckedChangeListener(this);
        cbDance.setOnCheckedChangeListener(this);

        cbHighPressure.setOnCheckedChangeListener(this);
        cbHighXuezhi.setOnCheckedChangeListener(this);
        cbCardiovas.setOnCheckedChangeListener(this);
        cbHighSugar.setOnCheckedChangeListener(this);
        cbLowSugar.setOnCheckedChangeListener(this);
        cbArthritis.setOnCheckedChangeListener(this);
        cbNeckPain.setOnCheckedChangeListener(this);
        cbRheumaticPain.setOnCheckedChangeListener(this);
        cbDisorders.setOnCheckedChangeListener(this);
        cbBreathDiseases.setOnCheckedChangeListener(this);
        cbEyeDisease.setOnCheckedChangeListener(this);
        cbLiverDisease.setOnCheckedChangeListener(this);
        cbTumor.setOnCheckedChangeListener(this);
        cbSubHealth.setOnCheckedChangeListener(this);
        cbOthers.setOnCheckedChangeListener(this);
        btnFinish.setOnClickListener(this);
    }

    private void initData() {
        registerInfo = (Register) SpHelp.getObject(SpHelp.REGISTER_INFO);
        Log.v("OtherInfoActivity", "Info = " + registerInfo.toString());
        initDetailTwo(registerInfo);
    }

    private void initDetailTwo(Register register) {
        if (register == null) {
            return;
        }
        if (!TextUtils.isEmpty(register.getArt())) {
            String[] hobbys = register.getArt().split("@");
            for (String hobby : hobbys) {
                switch (hobby) {
                    case "评书":
                        cbBooks.setChecked(true);
                        break;
                    case "戏曲":
                        cbDrama.setChecked(true);
                        break;
                    case "舞蹈":
                        cbDance.setChecked(true);
                        break;
                    case "其他":
                        cbOther.setChecked(true);
                        break;
                }
            }
        }

        if (!TextUtils.isEmpty(register.getSportsRate())) {
            switch (register.getSportsRate()) {
                case "每天":
                    cbTrainEveryDay.setChecked(true);
                    break;
                case "每周多次":
                    cbTrainManyEveryWeek.setChecked(true);
                    break;
                case "偶尔":
                    cbTrainOccasionally.setChecked(true);
                    break;
                case "不锻炼":
                    cbTrainNever.setChecked(true);
                    break;
            }
        }

        if (!TextUtils.isEmpty(register.getDiet())) {
            String[] attrs = register.getDiet().split("@");
            for (String attr : attrs) {
                switch (attr) {
                    case "嗜油":
                        cbAddictedToOil.setChecked(true);
                        break;
                    case "嗜盐":
                        cbAddictedToSalt.setChecked(true);
                        break;
                    case "素食为主":
                        cbVegetablesMain.setChecked(true);
                        break;
                    case "荤食为主":
                        cbMeatMain.setChecked(true);
                        break;
                    case "荤素均衡":
                        cbMeatAndVegetables.setChecked(true);
                        break;
                }
            }
        }

        if (!TextUtils.isEmpty(register.getSmoke())) {
            switch (register.getSmoke()) {
                case "从不":
                    cbSmokeNever.setChecked(true);
                    break;
                case "已戒烟":
                    cbSmokeQuit.setChecked(true);
                    break;
                case "吸烟":
                    cbSmoking.setChecked(true);
            }
        }

        if (!TextUtils.isEmpty(register.getDrink())) {
            switch (register.getDrink()) {
                case "每天":
                    cbDrinkEveryDay.setChecked(true);
                    break;
                case "经常":
                    cbDrinkFrequency.setChecked(true);
                    break;
                case "偶尔":
                    cbDrinkOccasionally.setChecked(true);
                    break;
                case "从不":
                    cbDrinkNever.setChecked(true);
                    break;
            }
        }

        if (!TextUtils.isEmpty(register.getAllergic())) {
            String[] allergics = register.getAllergic().split("@");
            for (String allergic : allergics) {
                switch (allergic) {
                    case "无":
                        cbNothing.setChecked(true);
                        break;
                    case "高蛋白食物":
                        cbHighProteinFood.setChecked(true);
                        break;
                    case "海鲜":
                        cbSeafood.setChecked(true);
                        break;
                    case "花粉":
                        cbPollen.setChecked(true);
                        break;
                    case "药物":
                        cbDrug.setChecked(true);
                        break;
                    case "膏药":
                        cbPlaster.setChecked(true);
                        break;
                    case "化妆品":
                        cbCosmetic.setChecked(true);
                        break;
                    case "酒精":
                        cbAlcohol.setChecked(true);
                        break;
                    case "油漆":
                        cbPaint.setChecked(true);
                        break;
                }
            }
        }

        if (!TextUtils.isEmpty(register.getIllness())) {
            String[] illnessArray = register.getIllness().split("@");
            for (String illness : illnessArray) {
                switch (illness) {
                    case "高血压":
                        cbHighPressure.setChecked(true);
                        break;
                    case "高血糖":
                        cbHighSugar.setChecked(true);
                        break;
                    case "低血糖":
                        cbLowSugar.setChecked(true);
                        break;
                    case "高血脂":
                        cbHighXuezhi.setChecked(true);
                        break;
                    case "心脑血管":
                        cbCardiovas.setChecked(true);
                        break;
                    case "关节炎":
                        cbArthritis.setChecked(true);
                        break;
                    case "颈肩腰腿疼":
                        cbNeckPain.setChecked(true);
                        break;
                    case "风湿类疼痛":
                        cbRheumaticPain.setChecked(true);
                        break;
                    case "胃肠疾病":
                        cbDisorders.setChecked(true);
                        break;
                    case "呼吸类疾病":
                        cbBreathDiseases.setChecked(true);
                        break;
                    case "眼部疾病":
                        cbEyeDisease.setChecked(true);
                        break;
                    case "肝病":
                        cbLiverDisease.setChecked(true);
                        break;
                    case "免疫、肿瘤":
                        cbTumor.setChecked(true);
                        break;
                    case "亚健康":
                        cbSubHealth.setChecked(true);
                        break;
                    case "其他":
                        cbOthers.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_finish) {
            if (!checkFinish()) {
                return;
            }

            if (hobbyList != null && hobbyList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String hobby : hobbyList) {
                    builder.append(hobby).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                art = builder.toString();
            }
            if (dietList != null && dietList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String diet : dietList) {
                    builder.append(diet).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                diet = builder.toString();
            }
            if (allergicList != null && allergicList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String allergic : allergicList) {
                    builder.append(allergic).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                allergic = builder.toString();
            }

            if (illnessList != null && illnessList.size() > 0) {
                StringBuilder builder = new StringBuilder("");

                for (String illness : illnessList) {
                    builder.append(illness).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                illness = builder.toString();
            }

            registerInfo.setArt(art);
            registerInfo.setSportsRate(sportsRate);
            registerInfo.setDiet(diet);
            registerInfo.setSmoke(smoke);
            registerInfo.setDrink(drink);
            registerInfo.setAllergic(allergic);
            registerInfo.setIllness(illness);
            uploadPersonalDetail(registerInfo);
        }
    }

    private boolean checkFinish() {
        if (!cbBooks.isChecked() && !cbDrama.isChecked()
                && !cbDance.isChecked() && !cbOther.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人兴趣爱好");
            return false;
        }

        if (!cbTrainEveryDay.isChecked() && !cbTrainManyEveryWeek.isChecked()
                && !cbTrainOccasionally.isChecked() && !cbTrainNever.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人运动信息");
            return false;
        }

        if (!cbMeatAndVegetables.isChecked() && !cbMeatMain.isChecked()
                && !cbVegetablesMain.isChecked() && !cbAddictedToSalt.isChecked()
                && !cbAddictedToOil.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人饮食信息");
            return false;
        }

        if (!cbSmokeNever.isChecked() && !cbSmokeQuit.isChecked()
                && !cbSmoking.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人抽烟信息");
            return false;
        }

        if (!cbDrinkNever.isChecked() && !cbDrinkOccasionally.isChecked()
                && !cbDrinkFrequency.isChecked() && !cbDrinkEveryDay.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人喝酒信息");
            return false;
        }

        if (!cbHighProteinFood.isChecked() && !cbSeafood.isChecked()
                && !cbPollen.isChecked() && !cbDrug.isChecked()
                && !cbPlaster.isChecked() && !cbCosmetic.isChecked()
                && !cbAlcohol.isChecked() && !cbPaint.isChecked() && !cbNothing.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人过敏情况");
            return false;
        }

        if (!cbHighPressure.isChecked() && !cbHighXuezhi.isChecked()
                && !cbCardiovas.isChecked() && !cbHighSugar.isChecked()
                && !cbLowSugar.isChecked() && !cbArthritis.isChecked()
                && !cbNeckPain.isChecked() && !cbRheumaticPain.isChecked() && !cbDisorders.isChecked()
                && !cbBreathDiseases.isChecked() && !cbEyeDisease.isChecked() && !cbLiverDisease.isChecked()
                && !cbTumor.isChecked() && !cbSubHealth.isChecked() && !cbOthers.isChecked()) {
            MethodUtils.showToast(OtherInfoActivity.this, "请完善个人以往患病情况");
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            // 设置 兴趣爱好
            case R.id.cb_other:
                cbOther.setChecked(isChecked);
                if (isChecked) {
                    hobbyList.add("其他");
                } else {
                    hobbyList.remove("其他");
                }
                break;
            case R.id.cb_dance:
                cbDance.setChecked(isChecked);
                if (isChecked) {
                    hobbyList.add("舞蹈");
                } else {
                    hobbyList.remove("舞蹈");
                }
                break;
            case R.id.cb_drama:
                cbDrama.setChecked(isChecked);
                if (isChecked) {
                    hobbyList.add("戏曲");
                } else {
                    hobbyList.remove("戏曲");
                }
                break;
            case R.id.cb_books:
                cbBooks.setChecked(isChecked);
                if (isChecked) {
                    hobbyList.add("评书");
                } else {
                    hobbyList.remove("评书");
                }
                break;

            //设置锻炼频率
            case R.id.cb_train_never:
                if (isChecked) {
                    sportsRate = "不锻炼";
                    cbTrainNever.setChecked(true);
                    cbTrainOccasionally.setChecked(false);
                    cbTrainManyEveryWeek.setChecked(false);
                    cbTrainEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_train_occasionally:
                if (isChecked) {
                    sportsRate = "偶尔";
                    cbTrainNever.setChecked(false);
                    cbTrainOccasionally.setChecked(true);
                    cbTrainManyEveryWeek.setChecked(false);
                    cbTrainEveryDay.setChecked(false);
                }
                break;

            case R.id.cb_train_many_every_week:
                if (isChecked) {
                    sportsRate = "每周多次";
                    cbTrainNever.setChecked(false);
                    cbTrainOccasionally.setChecked(false);
                    cbTrainManyEveryWeek.setChecked(true);
                    cbTrainEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_train_every_day:
                if (isChecked) {
                    sportsRate = "每天";
                    cbTrainNever.setChecked(false);
                    cbTrainOccasionally.setChecked(false);
                    cbTrainManyEveryWeek.setChecked(false);
                    cbTrainEveryDay.setChecked(true);
                }
                break;

            //设置饮食习惯
            case R.id.cb_Addicted_to_oil:
                if (isChecked) {
                    cbAddictedToOil.setChecked(true);
                    dietList.add("嗜油");
                } else {
                    cbAddictedToOil.setChecked(false);
                    dietList.remove("嗜油");
                }
                break;
            case R.id.cb_addicted_to_salt:
                if (isChecked) {
                    cbAddictedToSalt.setChecked(true);
                    dietList.add("嗜盐");
                } else {
                    cbAddictedToSalt.setChecked(false);
                    dietList.remove("嗜盐");
                }
                break;
            case R.id.cb_meat_and_vegetables:
                if (isChecked) {
                    cbMeatAndVegetables.setChecked(true);
                    cbMeatMain.setChecked(false);
                    cbVegetablesMain.setChecked(false);
                    dietList.add("荤素均衡");
                    if (dietList.contains("荤食为主")) {
                        dietList.remove("荤食为主");
                    }
                    if (dietList.contains("素食为主")) {
                        dietList.remove("素食为主");
                    }

                } else {
                    cbMeatAndVegetables.setChecked(false);
                    dietList.remove("荤素均衡");
                }
                break;
            case R.id.cb_Meat_main:
                if (isChecked) {
                    cbMeatMain.setChecked(true);
                    cbMeatAndVegetables.setChecked(false);
                    cbVegetablesMain.setChecked(false);
                    dietList.add("荤食为主");
                    if (dietList.contains("荤素均衡")) {
                        dietList.remove("荤素均衡");
                    }
                    if (dietList.contains("素食为主")) {
                        dietList.remove("素食为主");
                    }
                } else {
                    cbMeatMain.setChecked(false);
                    dietList.remove("荤食为主");
                }
                break;
            case R.id.cb_vegetables_main:
                if (isChecked) {
                    cbVegetablesMain.setChecked(true);
                    cbMeatAndVegetables.setChecked(false);
                    cbMeatMain.setChecked(false);
                    dietList.add("素食为主");
                    if (dietList.contains("荤素均衡")) {
                        dietList.remove("荤素均衡");
                    }
                    if (dietList.contains("荤食为主")) {
                        dietList.remove("荤食为主");
                    }
                } else {
                    cbVegetablesMain.setChecked(false);
                    dietList.remove("素食为主");
                }
                break;


            //设置抽烟情况
            case R.id.cb_smoke_never:
                if (isChecked) {
                    smoke = "从不";
                    cbSmokeNever.setChecked(true);
                    cbSmokeQuit.setChecked(false);
                    cbSmoking.setChecked(false);
                }
                break;
            case R.id.cb_smoking:
                if (isChecked) {
                    smoke = "吸烟";
                    cbSmokeNever.setChecked(false);
                    cbSmokeQuit.setChecked(false);
                    cbSmoking.setChecked(true);
                }
                break;
            case R.id.cb_smoke_quit:
                if (isChecked) {
                    smoke = "已戒烟";
                    cbSmoking.setChecked(false);
                    cbSmokeNever.setChecked(false);
                    cbSmokeQuit.setChecked(true);
                }
                break;

            //设置喝酒情况
            case R.id.cb_drink_every_day:
                if (isChecked) {
                    drink = "每天";
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(false);
                    cbDrinkFrequency.setChecked(false);
                    cbDrinkEveryDay.setChecked(true);
                }
                break;
            case R.id.cb_drink_frequency:
                if (isChecked) {
                    drink = "经常";
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(false);
                    cbDrinkFrequency.setChecked(true);
                    cbDrinkEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_drink_occasionally:
                if (isChecked) {
                    drink = "偶尔";
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(true);
                    cbDrinkFrequency.setChecked(false);
                    cbDrinkEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_drink_never:
                if (isChecked) {
                    drink = "从不";
                    cbDrinkNever.setChecked(true);
                    cbDrinkOccasionally.setChecked(false);
                    cbDrinkFrequency.setChecked(false);
                    cbDrinkEveryDay.setChecked(false);
                }
                break;

            //设置过敏情况
            case R.id.cb_nothing:
                cbNothing.setChecked(isChecked);
                if (isChecked) {
                    cbHighProteinFood.setChecked(false);
                    cbSeafood.setChecked(false);
                    cbPollen.setChecked(false);
                    cbDrug.setChecked(false);
                    cbPlaster.setChecked(false);
                    cbCosmetic.setChecked(false);
                    cbAlcohol.setChecked(false);
                    cbPaint.setChecked(false);
                    allergicList.add("无");
                } else {
                    allergicList.remove("无");
                }
                break;
            case R.id.cb_paint:
                cbPaint.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("油漆");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("油漆");
                }
                break;
            case R.id.cb_alcohol:
                cbAlcohol.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("酒精");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("酒精");
                }
                break;
            case R.id.cb_cosmetic:
                cbCosmetic.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("化妆品");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("化妆品");
                }
                break;
            case R.id.cb_Plaster:
                cbPlaster.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("膏药");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("膏药");
                }
                break;
            case R.id.cb_drug:
                cbDrug.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("药物");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("药物");
                }
                break;
            case R.id.cb_pollen:
                cbPollen.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("花粉");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("花粉");
                }
                break;
            case R.id.cb_seafood:
                cbSeafood.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("海鲜");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("海鲜");
                }
                break;
            case R.id.cb_High_protein_food:
                cbHighProteinFood.setChecked(isChecked);
                if (isChecked) {
                    allergicList.add("高蛋白食物");
                    cbNothing.setChecked(false);
                } else {
                    allergicList.remove("高蛋白食物");
                }
                break;

            case R.id.cb_low_sugar:
                cbLowSugar.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("低血糖");
                } else {
                    illnessList.remove("低血糖");
                }
                break;
            case R.id.cb_high_sugar:
                cbHighSugar.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("高血糖");
                } else {
                    illnessList.remove("高血糖");
                }
                break;
            case R.id.cb_cardiovas:
                cbCardiovas.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("心脑血管");
                } else {
                    illnessList.remove("心脑血管");
                }
                break;
            case R.id.high_xuezhi:
                cbHighXuezhi.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("高血脂");
                } else {
                    illnessList.remove("高血脂");
                }
                break;
            case R.id.cb_high_pressure:
                cbHighPressure.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("高血压");
                } else {
                    illnessList.remove("高血压");
                }
                break;

            case R.id.cb_sub_health:
                cbSubHealth.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("亚健康");
                } else {
                    illnessList.remove("亚健康");
                }
                break;
            case R.id.cb_tumor:
                cbTumor.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("免疫、肿瘤");
                } else {
                    illnessList.remove("免疫、肿瘤");
                }
                break;
            case R.id.cb_eye_disease:
                cbEyeDisease.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("眼部疾病");
                } else {
                    illnessList.remove("眼部疾病");
                }
                break;
            case R.id.cb_breath_diseases:
                cbBreathDiseases.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("呼吸类疾病");
                } else {
                    illnessList.remove("呼吸类疾病");
                }
                break;
            case R.id.cb_disorders:
                cbDisorders.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("胃肠疾病");
                } else {
                    illnessList.remove("胃肠疾病");
                }
                break;
            case R.id.cb_rheumatic_pain:
                cbRheumaticPain.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("风湿类疼痛");
                } else {
                    illnessList.remove("风湿类疼痛");
                }
                break;
            case R.id.cb_neck_pain:
                cbNeckPain.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("颈肩腰腿疼");
                } else {
                    illnessList.remove("颈肩腰腿疼");
                }
                break;
            case R.id.cb_arthritis:
                cbArthritis.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("关节炎");
                } else {
                    illnessList.remove("关节炎");
                }
                break;
            case R.id.cb_Liver_disease:
                cbLiverDisease.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("肝病");
                } else {
                    illnessList.remove("肝病");
                }
                break;
            case R.id.cb_others:
                cbOthers.setChecked(isChecked);
                if (isChecked) {
                    illnessList.add("其他");
                } else {
                    illnessList.remove("其他");
                }
                break;
            default:
                break;
        }

    }

    private void uploadPersonalDetail(final Register register) {
        try {
            JSONObject object = new JSONObject();

            Log.v(TAG, register.toString());

            object.put("name", register.getName())
                    .put("sex", register.getSex())
                    .put("age", register.getAge())
                    .put("height", register.getHeight())
                    .put("weight", register.getWeight())
                    .put("phone", register.getNum())
                    .put("stepDistance", register.getStepDistance())
                    .put("urgentName", register.getUrgentContactName())
                    .put("urgentPhone", register.getUrgentContactPhone())
                    .put("cId", register.getServiceId())
                    .put("province", register.getProvince())
                    .put("city", register.getCity())
                    .put("town", register.getArea())
                    .put("folk", register.getNation())
                    .put("education", register.getEducation())
                    .put("job", register.getOccupation())
                    .put("address", register.getAddress())
                    .put("isWatchHealthTV", register.isWatchHealthTv())
                    .put("art", register.getArt())
                    .put("sportsRate", register.getSportsRate())
                    .put("diet", register.getDiet())
                    .put("smoke", register.getSmoke())
                    .put("drink", register.getDrink())
                    .put("allergic", register.getAllergic())
                    .put("illness", register.getIllness())
                    .put("body", register.getBody());

            HttpRequest.post(URLConstant.URL_UPLOAD_PERSONAL_DETAIL, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        MethodUtils.showToast(getApplicationContext(), "基本信息提交成功");
                        SpHelp.saveObject(SpHelp.REGISTER_INFO, register);
                        finish();
                    } else {
                        MethodUtils.showToast(getApplicationContext(), "基本信息提交失败: " + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请求失败, 请稍后重试");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
