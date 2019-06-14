package com.bt.elderbracelet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bt.elderbracelet.activity.PushDetailActivity;
import com.bt.elderbracelet.entity.others.PushMessage;
import com.bttow.elderbracelet.R;

import java.util.List;

/**
 * Created by pendragon on 17-4-17.
 */

public class PushAdapter extends BaseAdapter {

    private List<PushMessage> messageList;
    private Context context;

    public PushAdapter(List<PushMessage> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.bracelet_push_item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvBrief = (TextView) convertView.findViewById(R.id.tv_brief);
            viewHolder.btnDetail = (Button) convertView.findViewById(R.id.btn_detail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final PushMessage pushMessage = messageList.get(position);
        viewHolder.tvTitle.setText(pushMessage.getTitle());
        if (!TextUtils.isEmpty(pushMessage.getTitleColor())) {
            String color = pushMessage.getTitleColor();
            viewHolder.tvTitle.setTextColor(getColor(color));
        }

        if (pushMessage.getTitleStrong() == 1) {
            TextPaint tp = viewHolder.tvTitle.getPaint();
            tp.setFakeBoldText(true);
        }

        viewHolder.tvBrief.setText(pushMessage.getBrief());
        viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PushDetailActivity.class);
                intent.putExtra("pushMessage", pushMessage);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle, tvBrief;
        Button btnDetail;
    }


    /**
     * 从后台服务器传过来的颜色数值可能是“#123456”,这是正常的，我们不需要修改
     * 也有可能是是“#123”，安卓API不认识这种表达方式，则我们要将它扩招为“#112233”
     */
    public int getColor(String colorString) {
        int color = 0;
        if (!TextUtils.isEmpty(colorString)) {
            if (colorString.length() == 4) {
                StringBuilder builder = new StringBuilder();
                builder.append(colorString.charAt(0));
                builder.append(colorString.charAt(1));
                builder.append(colorString.charAt(1));
                builder.append(colorString.charAt(2));
                builder.append(colorString.charAt(2));
                builder.append(colorString.charAt(3));
                builder.append(colorString.charAt(3));
                return Color.parseColor(builder.toString());
            }
            return Color.parseColor(colorString);
        }
        return color;
    }
}
