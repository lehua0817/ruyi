package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.entity.others.Event;
import com.bt.elderbracelet.entity.others.PersonalDetailOne;
import com.bt.elderbracelet.entity.others.PersonalDetailTwo;
import com.bt.elderbracelet.entity.Register;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.bt.elderbracelet.view.TitleView;
import com.bt.elderbracelet.view.TitleView.onBackLister;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class PersonalTwoActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    TitleView titleview;
    CheckBox cbBooks;
    CheckBox cbDrama;
    CheckBox cbDance;
    CheckBox cbOther;
    CheckBox cbTrainEveryDay;
    CheckBox cbTrainManyEveryWeek;
    CheckBox cbTrainOccasionally;
    CheckBox cbTrainNever;
    CheckBox cbMeatAndVegetables;
    CheckBox cbMeatMain;
    CheckBox cbVegetablesMain;
    CheckBox cbAddictedToSalt;
    CheckBox cbAddictedToOil;
    CheckBox cbSmokeNever;
    CheckBox cbSmokeQuit;
    CheckBox cbSmoking;
    CheckBox cbDrinkNever;
    CheckBox cbDrinkOccasionally;
    CheckBox cbDrinkFrequency;
    CheckBox cbDrinkEveryDay;
    CheckBox cbHighProteinFood;
    CheckBox cbSeafood;
    CheckBox cbPollen;
    CheckBox cbDrug;
    CheckBox cbPlaster;
    CheckBox cbCosmetic;
    CheckBox cbAlcohol;
    CheckBox cbPaint;
    CheckBox cbNothing;
    Button btnNextPage;

    private PersonalDetailOne detailOne;
    private PersonalDetailTwo detailTwo;

    private Register registerInfo;
    private Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_presonal_two_page);
        MyApplication.getInstance().addActivity(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initData();
    }

    private void initView()
    {
        titleview = (TitleView) findViewById(R.id.titleview);
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

        btnNextPage = (Button) findViewById(R.id.btn_next_page);
    }

    private void initListener()
    {
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
        btnNextPage.setOnClickListener(this);
    }

    private void initData()
    {
        MyApplication.hobbyList = new ArrayList<>();
        MyApplication.dietList = new ArrayList<>();
        MyApplication.allergicList = new ArrayList<>();

        myIntent = getIntent();
        registerInfo = (Register) myIntent.getSerializableExtra("registerInfo");

        detailOne = (PersonalDetailOne) myIntent.getSerializableExtra("detailOne");
        if (SpHelp.getObject(SpHelp.PERSONAL_DETAIL_TWO)== null) {
            detailTwo = new PersonalDetailTwo();
        } else {
            detailTwo = (PersonalDetailTwo) SpHelp.getObject(SpHelp.PERSONAL_DETAIL_TWO);
        }
        initDetailTwo(detailTwo);
    }


    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_next_page) {

            if (!checkFinish()) {
                return;
            }
            Intent intent = new Intent(getApplicationContext(), PersonalThreeActivity.class);
            Bundle bundle = new Bundle();
            if (null != detailOne) {
                bundle.putSerializable("detailOne", detailOne);
            }

            if (MyApplication.hobbyList != null && MyApplication.hobbyList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String hobby : MyApplication.hobbyList) {
                    builder.append(hobby).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                detailTwo.setHobby(builder.toString());
            } else {
                detailTwo.setHobby("");
            }
            if (MyApplication.dietList != null && MyApplication.dietList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String diet : MyApplication.dietList) {
                    builder.append(diet).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                detailTwo.setDiet(builder.toString());
            } else {
                detailTwo.setDiet("");
            }
            if (MyApplication.allergicList != null && MyApplication.allergicList.size() > 0) {
                StringBuilder builder = new StringBuilder("");
                for (String allergic : MyApplication.allergicList) {
                    builder.append(allergic).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                detailTwo.setAllergic(builder.toString());
            } else {
                detailTwo.setAllergic("");
            }

            if (null != detailTwo) {
                bundle.putSerializable("detailTwo", detailTwo);
            }
            if (null != registerInfo) {
                bundle.putSerializable("registerInfo", registerInfo);
            }
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkFinish()
    {
        if (!cbBooks.isChecked() && !cbDrama.isChecked()
                && !cbDance.isChecked() && !cbOther.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人兴趣爱好");
            return false;
        }

        if (!cbTrainEveryDay.isChecked() && !cbTrainManyEveryWeek.isChecked()
                && !cbTrainOccasionally.isChecked() && !cbTrainNever.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人运动信息");
            return false;
        }

        if (!cbMeatAndVegetables.isChecked() && !cbMeatMain.isChecked()
                && !cbVegetablesMain.isChecked() && !cbAddictedToSalt.isChecked()
                && !cbAddictedToOil.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人饮食信息");
            return false;
        }

        if (!cbSmokeNever.isChecked() && !cbSmokeQuit.isChecked()
                && !cbSmoking.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人抽烟信息");
            return false;
        }

        if (!cbDrinkNever.isChecked() && !cbDrinkOccasionally.isChecked()
                && !cbDrinkFrequency.isChecked() && !cbDrinkEveryDay.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人喝酒信息");
            return false;
        }

        if (!cbHighProteinFood.isChecked() && !cbSeafood.isChecked()
                && !cbPollen.isChecked() && !cbDrug.isChecked()
                && !cbPlaster.isChecked() && !cbCosmetic.isChecked()
                && !cbAlcohol.isChecked() && !cbPaint.isChecked() && !cbNothing.isChecked()) {
            MethodUtils.showToast(PersonalTwoActivity.this, "请完善个人过敏情况");
            return false;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        switch (buttonView.getId()) {

            // 设置 兴趣爱好
            case R.id.cb_other:
                cbOther.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.hobbyList.add("其他");
                } else {
                    MyApplication.hobbyList.remove("其他");
                }
                break;
            case R.id.cb_dance:
                cbDance.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.hobbyList.add("舞蹈");
                } else {
                    MyApplication.hobbyList.remove("舞蹈");
                }
                break;
            case R.id.cb_drama:
                cbDrama.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.hobbyList.add("戏曲");
                } else {
                    MyApplication.hobbyList.remove("戏曲");
                }
                break;
            case R.id.cb_books:
                cbBooks.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.hobbyList.add("评书");
                } else {
                    MyApplication.hobbyList.remove("评书");
                }
                break;

            //设置锻炼频率
            case R.id.cb_train_never:
                if (isChecked) {
                    detailTwo.setSport("不锻炼");
                    cbTrainNever.setChecked(true);
                    cbTrainOccasionally.setChecked(false);
                    cbTrainManyEveryWeek.setChecked(false);
                    cbTrainEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_train_occasionally:
                if (isChecked) {
                    detailTwo.setSport("偶尔");
                    cbTrainNever.setChecked(false);
                    cbTrainOccasionally.setChecked(true);
                    cbTrainManyEveryWeek.setChecked(false);
                    cbTrainEveryDay.setChecked(false);
                }
                break;

            case R.id.cb_train_many_every_week:
                if (isChecked) {
                    detailTwo.setSport("每周多次");
                    cbTrainNever.setChecked(false);
                    cbTrainOccasionally.setChecked(false);
                    cbTrainManyEveryWeek.setChecked(true);
                    cbTrainEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_train_every_day:
                if (isChecked) {
                    detailTwo.setSport("每天");
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
                    MyApplication.dietList.add("嗜油");
                } else {
                    cbAddictedToOil.setChecked(false);
                    MyApplication.dietList.remove("嗜油");
                }
                break;
            case R.id.cb_addicted_to_salt:
                if (isChecked) {
                    cbAddictedToSalt.setChecked(true);
                    MyApplication.dietList.add("嗜盐");
                } else {
                    cbAddictedToSalt.setChecked(false);
                    MyApplication.dietList.remove("嗜盐");
                }
                break;
            case R.id.cb_meat_and_vegetables:
                if (isChecked) {
                    cbMeatAndVegetables.setChecked(true);
                    cbMeatMain.setChecked(false);
                    cbVegetablesMain.setChecked(false);
                    MyApplication.dietList.add("荤素均衡");
                    if (MyApplication.dietList.contains("荤食为主")) {
                        MyApplication.dietList.remove("荤食为主");
                    }
                    if (MyApplication.dietList.contains("素食为主")) {
                        MyApplication.dietList.remove("素食为主");
                    }

                } else {
                    cbMeatAndVegetables.setChecked(false);
                    MyApplication.dietList.remove("荤素均衡");
                }
                break;
            case R.id.cb_Meat_main:
                if (isChecked) {
                    cbMeatMain.setChecked(true);
                    cbMeatAndVegetables.setChecked(false);
                    cbVegetablesMain.setChecked(false);
                    MyApplication.dietList.add("荤食为主");
                    if (MyApplication.dietList.contains("荤素均衡")) {
                        MyApplication.dietList.remove("荤素均衡");
                    }
                    if (MyApplication.dietList.contains("素食为主")) {
                        MyApplication.dietList.remove("素食为主");
                    }
                } else {
                    cbMeatMain.setChecked(false);
                    MyApplication.dietList.remove("荤食为主");
                }
                break;
            case R.id.cb_vegetables_main:
                if (isChecked) {
                    cbVegetablesMain.setChecked(true);
                    cbMeatAndVegetables.setChecked(false);
                    cbMeatMain.setChecked(false);
                    MyApplication.dietList.add("素食为主");
                    if (MyApplication.dietList.contains("荤素均衡")) {
                        MyApplication.dietList.remove("荤素均衡");
                    }
                    if (MyApplication.dietList.contains("荤食为主")) {
                        MyApplication.dietList.remove("荤食为主");
                    }
                } else {
                    cbVegetablesMain.setChecked(false);
                    MyApplication.dietList.remove("素食为主");
                }
                break;


            //设置抽烟情况
            case R.id.cb_smoke_never:
                if (isChecked) {
                    detailTwo.setSmoke("从不");
                    cbSmokeNever.setChecked(true);
                    cbSmokeQuit.setChecked(false);
                    cbSmoking.setChecked(false);
                }
                break;
            case R.id.cb_smoking:
                if (isChecked) {
                    detailTwo.setSmoke("吸烟");
                    cbSmokeNever.setChecked(false);
                    cbSmokeQuit.setChecked(false);
                    cbSmoking.setChecked(true);
                }
                break;
            case R.id.cb_smoke_quit:
                if (isChecked) {
                    detailTwo.setSmoke("已戒烟");
                    cbSmoking.setChecked(false);
                    cbSmokeNever.setChecked(false);
                    cbSmokeQuit.setChecked(true);
                }
                break;

            //设置喝酒情况
            case R.id.cb_drink_every_day:
                if (isChecked) {
                    detailTwo.setDrink("每天");
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(false);
                    cbDrinkFrequency.setChecked(false);
                    cbDrinkEveryDay.setChecked(true);
                }
                break;
            case R.id.cb_drink_frequency:
                if (isChecked) {
                    detailTwo.setDrink("经常");
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(false);
                    cbDrinkFrequency.setChecked(true);
                    cbDrinkEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_drink_occasionally:
                if (isChecked) {
                    detailTwo.setDrink("偶尔");
                    cbDrinkNever.setChecked(false);
                    cbDrinkOccasionally.setChecked(true);
                    cbDrinkFrequency.setChecked(false);
                    cbDrinkEveryDay.setChecked(false);
                }
                break;
            case R.id.cb_drink_never:
                if (isChecked) {
                    detailTwo.setDrink("从不");
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
                    MyApplication.allergicList.add("无");
                } else {
                    MyApplication.allergicList.remove("无");
                }
                break;
            case R.id.cb_paint:
                cbPaint.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("油漆");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("油漆");
                }
                break;
            case R.id.cb_alcohol:
                cbAlcohol.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("酒精");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("酒精");
                }
                break;
            case R.id.cb_cosmetic:
                cbCosmetic.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("化妆品");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("化妆品");
                }
                break;
            case R.id.cb_Plaster:
                cbPlaster.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("膏药");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("膏药");
                }
                break;
            case R.id.cb_drug:
                cbDrug.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("药物");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("药物");
                }
                break;
            case R.id.cb_pollen:
                cbPollen.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("花粉");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("花粉");
                }
                break;
            case R.id.cb_seafood:
                cbSeafood.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("海鲜");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("海鲜");
                }
                break;
            case R.id.cb_High_protein_food:
                cbHighProteinFood.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.allergicList.add("高蛋白食物");
                    cbNothing.setChecked(false);
                } else {
                    MyApplication.allergicList.remove("高蛋白食物");
                }
                break;
            default:
                break;
        }

    }


    private void initDetailTwo(PersonalDetailTwo detailTwo)
    {
        if (detailTwo == null){
            return;
        }
        if (!TextUtils.isEmpty(detailTwo.getHobby())) {
            String[] hobbys = detailTwo.getHobby().split("@");
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

        if (!TextUtils.isEmpty(detailTwo.getSport())) {
            switch (detailTwo.getSport()) {
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

        if (!TextUtils.isEmpty(detailTwo.getDiet())) {
            String[] attrs = detailTwo.getDiet().split("@");
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

        if (!TextUtils.isEmpty(detailTwo.getSmoke())) {
            switch (detailTwo.getSmoke()) {
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

        if (!TextUtils.isEmpty(detailTwo.getDrink())) {
            switch (detailTwo.getDrink()) {
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

        if (!TextUtils.isEmpty(detailTwo.getAllergic())) {
            String[] allergics = detailTwo.getAllergic().split("@");
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
    }

    public void onEventMainThread(Event event)
    {
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
