package com.bt.elderbracelet.entity;

import java.io.Serializable;

public class Register implements Serializable {

    private int id;
    private String name;              //姓名
    private String province;          //省份
    private String city;              //城市
    private String area;              //区/县
    private String num;               //电话号码
    private String password;          //登录密码
    private String sex;               //性别
    private String age;               //年龄

    private String height;            //身高
    private String weight;            //体重
    private String stepDistance;      //步距
    private String urgentContactName;    //紧急联系人名字
    private String urgentContactPhone;    //紧急联系人电话
    private String serviceId;    //客服ID

    // 后来添加的字段
    private String art;         // 喜欢的文娱项目
    private String sportsRate;  // 体育锻炼频道
    private String diet;        // 饮食习惯
    private String smoke;       // 抽烟状况
    private String drink;       // 喝酒状况
    private String allergic;     // 过敏情况
    private String illness;      // 患病情况


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStepDistance() {
        return stepDistance;
    }

    public void setStepDistance(String stepDistance) {
        this.stepDistance = stepDistance;
    }

    public String getUrgentContactName() {
        return urgentContactName;
    }

    public void setUrgentContactName(String urgentContactName) {
        this.urgentContactName = urgentContactName;
    }

    public String getUrgentContactPhone() {
        return urgentContactPhone;
    }

    public void setUrgentContactPhone(String urgentContactPhone) {
        this.urgentContactPhone = urgentContactPhone;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getSportsRate() {
        return sportsRate;
    }

    public void setSportsRate(String sportsRate) {
        this.sportsRate = sportsRate;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getAllergic() {
        return allergic;
    }

    public void setAllergic(String allergic) {
        this.allergic = allergic;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
