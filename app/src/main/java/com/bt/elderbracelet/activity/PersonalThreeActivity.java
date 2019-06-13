package com.bt.elderbracelet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bonten.ble.application.MyApplication;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.others.PersonalDetailOne;
import com.bt.elderbracelet.entity.others.PersonalDetailThree;
import com.bt.elderbracelet.entity.others.PersonalDetailTwo;
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

import de.greenrobot.event.EventBus;

/**
 * 提醒类
 *
 * @author Administrator
 */
public class PersonalThreeActivity extends Activity implements OnClickListener, CompoundButton.OnCheckedChangeListener {

    TitleView titleview;

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

    CheckBox cbYin;
    CheckBox cbSpecial;
    CheckBox cbQi;
    CheckBox cbHeat;
    CheckBox cbPhlegm;
    CheckBox cbBloodStasis;
    CheckBox cbYang;
    CheckBox cbQiDeficiency;
    CheckBox cbGentleness;

    Button btnFinish;


    private PersonalDetailOne detailOne;
    private PersonalDetailTwo detailTwo;
    private PersonalDetailThree detailThree;

    private Register registerInfo;
    private ModelDao modelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bracelet_presonal_three_page);
        MyApplication.getInstance().addActivity(this);
        initView();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
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

        //这 15 种是常见疾病
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


        //下面 9 种是个人体质
        cbYin = (CheckBox) findViewById(R.id.cb_yin);
        cbSpecial = (CheckBox) findViewById(R.id.cb_special);
        cbQi = (CheckBox) findViewById(R.id.cb_qi);
        cbHeat = (CheckBox) findViewById(R.id.cb_heat);
        cbPhlegm = (CheckBox) findViewById(R.id.cb_phlegm);
        cbBloodStasis = (CheckBox) findViewById(R.id.cb_blood_stasis);
        cbYang = (CheckBox) findViewById(R.id.cb_yang);
        cbQiDeficiency = (CheckBox) findViewById(R.id.cb_qi_deficiency);
        cbGentleness = (CheckBox) findViewById(R.id.cb_gentleness);

        btnFinish = (Button) findViewById(R.id.btn_finish);

    }

    private void initListener() {
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

        cbYin.setOnCheckedChangeListener(this);
        cbSpecial.setOnCheckedChangeListener(this);
        cbQi.setOnCheckedChangeListener(this);
        cbHeat.setOnCheckedChangeListener(this);
        cbPhlegm.setOnCheckedChangeListener(this);
        cbBloodStasis.setOnCheckedChangeListener(this);
        cbYang.setOnCheckedChangeListener(this);
        cbQiDeficiency.setOnCheckedChangeListener(this);
        cbGentleness.setOnCheckedChangeListener(this);

        btnFinish.setOnClickListener(this);
    }

    private void initData() {
        MyApplication.illnessList = new ArrayList<>();
        modelDao = new ModelDao(this);

        detailOne = (PersonalDetailOne) getIntent().getSerializableExtra("detailOne");
        detailTwo = (PersonalDetailTwo) getIntent().getSerializableExtra("detailTwo");

        registerInfo = (Register) getIntent().getSerializableExtra("registerInfo");

        if (SpHelp.getObject(SpHelp.PERSONAL_DETAIL_THREE) == null) {
            detailThree = new PersonalDetailThree();
        } else {
            detailThree = (PersonalDetailThree) SpHelp.getObject(SpHelp.PERSONAL_DETAIL_THREE);
        }
        initDetailThree(detailThree);
    }

    private void initDetailThree(PersonalDetailThree detailThree) {
        if (detailThree == null)
            return;

        if (!TextUtils.isEmpty(detailThree.getIllness())) {
            String[] illnessArray = detailThree.getIllness().split("@");
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

        if (!TextUtils.isEmpty(detailThree.getPhysique())) {
            switch (detailThree.getPhysique()) {
                case "阴虚质":
                    cbYin.setChecked(true);
                    break;
                case "特稟质":
                    cbSpecial.setChecked(true);
                    break;
                case "气郁质":
                    cbQi.setChecked(true);
                    break;
                case "湿热质":
                    cbHeat.setChecked(true);
                    break;
                case "痰湿质":
                    cbPhlegm.setChecked(true);
                    break;
                case "血瘀质":
                    cbBloodStasis.setChecked(true);
                    break;
                case "阳虚质":
                    cbYang.setChecked(true);
                    break;
                case "气虚质":
                    cbQiDeficiency.setChecked(true);
                    break;
                case "平和质":
                    cbGentleness.setChecked(true);
                    break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_finish) {
            if (MyApplication.illnessList != null && MyApplication.illnessList.size() > 0) {
                StringBuilder builder = new StringBuilder("");

                for (String illness : MyApplication.illnessList) {
                    builder.append(illness).append("@");
                }
                builder.deleteCharAt(builder.length() - 1);
                detailThree.setIllness(builder.toString());
            } else {
                detailThree.setIllness("");
            }
            if (!checkFinish()) {
                return;
            }
            if (MethodUtils.is3G(getApplicationContext()) || MethodUtils.isWifi(getApplicationContext())) {
                uploadPersonalDetail(registerInfo, detailOne, detailTwo, detailThree);
            } else {
                MethodUtils.showToast(getApplicationContext(), "请检查网络是否正常!");
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_low_sugar:
                cbLowSugar.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("低血糖");
                } else {
                    MyApplication.illnessList.remove("低血糖");
                }
                break;
            case R.id.cb_high_sugar:
                cbHighSugar.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("高血糖");
                } else {
                    MyApplication.illnessList.remove("高血糖");
                }
                break;
            case R.id.cb_cardiovas:
                cbCardiovas.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("心脑血管");
                } else {
                    MyApplication.illnessList.remove("心脑血管");
                }
                break;
            case R.id.high_xuezhi:
                cbHighXuezhi.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("高血脂");
                } else {
                    MyApplication.illnessList.remove("高血脂");
                }
                break;
            case R.id.cb_high_pressure:
                cbHighPressure.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("高血压");
                } else {
                    MyApplication.illnessList.remove("高血压");
                }
                break;
            case R.id.cb_others:
                cbOthers.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("其他");
                } else {
                    MyApplication.illnessList.remove("其他");
                }
                break;
            case R.id.cb_sub_health:
                cbSubHealth.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("亚健康");
                } else {
                    MyApplication.illnessList.remove("亚健康");
                }
                break;
            case R.id.cb_tumor:
                cbTumor.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("免疫、肿瘤");
                } else {
                    MyApplication.illnessList.remove("免疫、肿瘤");
                }
                break;
            case R.id.cb_eye_disease:
                cbEyeDisease.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("眼部疾病");
                } else {
                    MyApplication.illnessList.remove("眼部疾病");
                }
                break;
            case R.id.cb_breath_diseases:
                MyApplication.illnessList.add("呼吸类疾病");
                break;
            case R.id.cb_disorders:
                cbDisorders.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("胃肠疾病");
                } else {
                    MyApplication.illnessList.remove("胃肠疾病");
                }
                break;
            case R.id.cb_rheumatic_pain:
                cbRheumaticPain.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("风湿类疼痛");
                } else {
                    MyApplication.illnessList.remove("风湿类疼痛");
                }
                break;
            case R.id.cb_neck_pain:
                cbNeckPain.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("颈肩腰腿疼");
                } else {
                    MyApplication.illnessList.remove("颈肩腰腿疼");
                }
                break;
            case R.id.cb_arthritis:
                cbArthritis.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("关节炎");
                } else {
                    MyApplication.illnessList.remove("关节炎");
                }
                break;
            case R.id.cb_Liver_disease:
                cbLiverDisease.setChecked(isChecked);
                if (isChecked) {
                    MyApplication.illnessList.add("肝病");
                } else {
                    MyApplication.illnessList.remove("肝病");
                }
                break;

            case R.id.cb_gentleness:
                if (isChecked) {
                    detailThree.setPhysique("平和质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(true);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_yang:
                if (isChecked) {
                    detailThree.setPhysique("阳虚质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(true);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_blood_stasis:
                if (isChecked) {
                    detailThree.setPhysique("血瘀质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(true);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_phlegm:
                if (isChecked) {
                    detailThree.setPhysique("痰湿质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(true);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_heat:
                if (isChecked) {
                    detailThree.setPhysique("湿热质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(true);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;

            case R.id.cb_qi:
                if (isChecked) {
                    detailThree.setPhysique("气郁质");
                    cbQi.setChecked(true);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_special:
                if (isChecked) {
                    detailThree.setPhysique("特稟质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(true);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;
            case R.id.cb_yin:
                if (isChecked) {
                    detailThree.setPhysique("阴虚质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(true);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(false);
                }
                break;

            case R.id.cb_qi_deficiency:
                if (isChecked) {
                    detailThree.setPhysique("气虚质");
                    cbQi.setChecked(false);
                    cbSpecial.setChecked(false);
                    cbYin.setChecked(false);
                    cbHeat.setChecked(false);
                    cbPhlegm.setChecked(false);
                    cbBloodStasis.setChecked(false);
                    cbYang.setChecked(false);
                    cbGentleness.setChecked(false);
                    cbQiDeficiency.setChecked(true);
                }
                break;
            default:
                break;
        }

    }

    private void uploadPersonalDetail(final Register register, final PersonalDetailOne detailOne, final PersonalDetailTwo detailTwo, final PersonalDetailThree detailThree) {
        try {
            JSONObject object = new JSONObject();
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
                    .put("town", register.getArea());

            object.put("folk", detailOne.getNation())
                    .put("education", detailOne.getEducation())
                    .put("job", detailOne.getOccupation())
                    .put("address", detailOne.getAddress())
                    .put("isWatchHealthTV", detailOne.isWatchHealthTv());

            object.put("art", detailTwo.getHobby())
                    .put("sportsRate", detailTwo.getSport())
                    .put("diet", detailTwo.getDiet())
                    .put("smoke", detailTwo.getSmoke())
                    .put("drink", detailTwo.getDrink())
                    .put("allergic", detailTwo.getAllergic());

            object.put("illness", detailThree.getIllness())
                    .put("body", detailThree.getPhysique());


            HttpRequest.post(URLConstant.URL_UPLOAD_PERSONAL_DETAIL, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        MethodUtils.showToast(getApplicationContext(), "基本信息提交成功");

                        SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_ONE, detailOne);

                        SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_TWO, detailTwo);

                        SpHelp.saveObject(SpHelp.PERSONAL_DETAIL_THREE, detailThree);
                        modelDao.updateRegister(register, register.getId());
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


    private boolean checkFinish() {
        if (!cbHighPressure.isChecked() && !cbHighXuezhi.isChecked()
                && !cbCardiovas.isChecked() && !cbHighSugar.isChecked() && !cbLowSugar.isChecked()
                && !cbArthritis.isChecked() && !cbNeckPain.isChecked()
                && !cbRheumaticPain.isChecked() && !cbDisorders.isChecked() && !cbBreathDiseases.isChecked()
                && !cbEyeDisease.isChecked() && !cbLiverDisease.isChecked()
                && !cbTumor.isChecked() && !cbSubHealth.isChecked() && !cbOthers.isChecked()) {
            MethodUtils.showToast(PersonalThreeActivity.this, "请完善个人疾病信息");
            return false;
        }

        if (!cbYin.isChecked() && !cbSpecial.isChecked() && !cbQi.isChecked()
                && !cbHeat.isChecked() && !cbPhlegm.isChecked() && !cbBloodStasis.isChecked()
                && !cbYang.isChecked() && !cbQiDeficiency.isChecked() && !cbGentleness.isChecked()) {
            MethodUtils.showToast(PersonalThreeActivity.this, "请完善个人体质信息");
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modelDao = null;
        EventBus.getDefault().unregister(this);
    }
}
