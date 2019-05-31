package com.bt.elderbracelet.entity.others;

import java.io.Serializable;

public class CallNumInfo implements Serializable{
	private int id;
	private String name;
	private String phone_num;
	private boolean isHave_cbox ;//是否显示 checkBox
	private boolean ischecked;// checkbox状态

	public boolean ischecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

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

	public String getPhone_num() {
		return phone_num;
	}

	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public boolean isHave_cbox() {
		return isHave_cbox;
	}

	public void setHave_cbox(boolean have_cbox) {
		isHave_cbox = have_cbox;
	}
}
