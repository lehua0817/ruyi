package com.bt.elderbracelet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bt.elderbracelet.entity.DeviceInfo;
import com.bttow.elderbracelet.R;

import java.util.ArrayList;

public class DeviceAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<DeviceInfo> deviceInfos;
	public void setSourceData(ArrayList<DeviceInfo> list) {
		if(deviceInfos != null)
			this.deviceInfos = list;
		else
			deviceInfos = new ArrayList<>();
	}
	public DeviceAdapter(Context context , ArrayList<DeviceInfo> list) {
		this.inflater = LayoutInflater.from(context);
		this.setSourceData(list);
	}
	public void changeData(ArrayList<DeviceInfo> list){
		this.setSourceData(list);
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return deviceInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	//加ViewHolder优化Listview，主要优化的过程是convertView.findViewById()这个过程
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bracelet_device_item, null);
			//下面 是将convertView 的控件进行打包
					holder = new ViewHolder();
			holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
			holder.tv_device_mac = (TextView) convertView.findViewById(R.id.tv_device_mac);
			//将控件包还给convertView，以后有convertView的控件赋值时，就不需要直接给convertView中控件赋值了
			//而是给控件包中的控件赋值
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DeviceInfo deviceInfo = deviceInfos.get(position);
		//给控件包中的控件赋值
		holder.tv_device_name.setText(deviceInfo.getName());
		holder.tv_device_mac.setText(deviceInfo.getMac());

		return convertView;
	}
	class ViewHolder {
		TextView tv_device_name;
		TextView tv_device_mac;
	}

}
