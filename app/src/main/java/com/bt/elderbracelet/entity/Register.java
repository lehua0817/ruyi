package com.bt.elderbracelet.entity;

import java.io.Serializable;

public class Register implements Serializable {

    /**
     *
     */
    private int id;
    private String name;              //姓名
    private String province;         //省份
    private String city;              //城市
    private String area;              //区/县
    private String num;               //电话号码
    private String sex;               //性别
    private String age;               //年龄
    private String height;           //身高
    private String weight;           //体重
    private String stepDistance;    //步距
    private String urgentContactName;    //紧急联系人名字
    private String urgentContactPhone;    //紧急联系人电话
    private String serviceId;    //客服ID

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNum()
    {
        return num;
    }

    public void setNum(String num)
    {
        this.num = num;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getWeight()
    {
        return weight;
    }

    public void setWeight(String weight)
    {
        this.weight = weight;
    }

    public String getStepDistance()
    {
        return stepDistance;
    }

    public void setStepDistance(String stepDistance)
    {
        this.stepDistance = stepDistance;
    }

    public String getUrgentContactName()
    {
        return urgentContactName;
    }

    public void setUrgentContactName(String urgentContactName)
    {
        this.urgentContactName = urgentContactName;
    }

    public String getUrgentContactPhone()
    {
        return urgentContactPhone;
    }

    public void setUrgentContactPhone(String urgentContactPhone)
    {
        this.urgentContactPhone = urgentContactPhone;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }


    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }


    @Override
    public String toString()
    {
        return "Register{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", num='" + num + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", stepDistance='" + stepDistance + '\'' +
                ", urgentContactName='" + urgentContactName + '\'' +
                ", urgentContactPhone='" + urgentContactPhone + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }
}
