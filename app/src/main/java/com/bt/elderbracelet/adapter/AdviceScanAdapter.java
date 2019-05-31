package com.bt.elderbracelet.adapter;
import java.util.ArrayList;

import com.bt.elderbracelet.entity.Sport;
import com.bttow.elderbracelet.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdviceScanAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<Sport> scans;
	private Context context;
	//int test = 0 ;

	public void setBreakRules(ArrayList<Sport> scans) {
		if(scans != null)
			this.scans = scans;
		else 
			scans = new ArrayList<Sport>();
	}
	public AdviceScanAdapter(Context context ,ArrayList<Sport> scans) {
		// TODO Auto-generated constructor stub
		this.inflater = LayoutInflater.from(context);
		this.setBreakRules(scans);
		this.context = context;
	}
	public void changeData(ArrayList<Sport> scans){
		this.setBreakRules(scans);
		this.notifyDataSetChanged();
		this.notifyDataSetInvalidated();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return scans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.bracelet_history_two_item, null);
			holder.tvdate = (TextView) convertView.findViewById(R.id.tvdate);
			holder.tvdata = (TextView) convertView.findViewById(R.id.tvdata);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Sport scan = scans.get(position);
		
		holder.tvdate.setText(scan.getDate());
		return convertView;
	}
	class ViewHolder {
		TextView tvdate;
		TextView tvdata;
	}
}
