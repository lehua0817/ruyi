package com.bt.elderbracelet.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bttow.elderbracelet.R;


public class TitleView extends LinearLayout implements OnClickListener {
    private LinearLayout layout_top_bg;
    private ImageView ivBack, ivtitleimg;
    private TextView mTitle, tvright;
    private onBackLister monClickListener;
    private onShareLister mShareonClickListener;
    private onSetLister monSetClickListener;

    public TitleView(Context context)
    {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public TitleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.title_view, this, true);
        layout_top_bg = (LinearLayout) findViewById(R.id.title_bg);
        mTitle = (TextView) findViewById(R.id.tvTitle);
        ivBack = (ImageView) findViewById(R.id.ivback);
        ivtitleimg = (ImageView) findViewById(R.id.ivtitleimg);
        tvright = (TextView) findViewById(R.id.tvright);
        ivBack.setOnClickListener(this);
        tvright.setOnClickListener(this);
        ivtitleimg.setOnClickListener(this);
    }

    public interface onBackLister {
        public void onClick(View button);
    }

    public interface onShareLister {
        public void onClick(View button);
    }

    public interface onSetLister {
        public void onClick(View button);
    }

    public void setbg(int res)
    {
        layout_top_bg.setBackgroundResource(res);
    }

    public void setcolor(String ss)
    {
        layout_top_bg.setBackgroundColor(Color.parseColor(ss));
    }

    public void settextcolor(String ss)
    {
        tvright.setTextColor(Color.parseColor(ss));
        mTitle.setTextColor(Color.parseColor(ss));
    }

    public void setBack(int res, onBackLister listener)
    {
        ivBack.setBackgroundResource(res);
        ivBack.setVisibility(View.VISIBLE);

        monClickListener = listener;
    }

    public void HiddenBackImg()
    {
        ivBack.setVisibility(View.INVISIBLE);
    }

    public void showBack()
    {
        ivBack.setVisibility(View.VISIBLE);
    }


    public void titleImg(int res)
    {
        ivtitleimg.setVisibility(View.VISIBLE);
        ivtitleimg.setImageResource(res);

    }

    public void HiddenShareImg()
    {
        ivtitleimg.setVisibility(View.INVISIBLE);
    }

    public void ShowShareImg()
    {
        ivtitleimg.setVisibility(View.VISIBLE);
    }


    public void right(int resid, onSetLister listener)
    {
        //tvright.setBackgroundResource(resid);
        tvright.setText(resid);
        tvright.setVisibility(View.VISIBLE);
        monSetClickListener = listener;
    }

    public void right(onSetLister listener)
    {
        monSetClickListener = listener;
    }

    public void setText(int res)
    {
        tvright.setText(res);
        tvright.setVisibility(View.VISIBLE);

    }

    public void hideRight()
    {
        tvright.setVisibility(View.INVISIBLE);
    }

    public void showRight()
    {
        tvright.setVisibility(View.VISIBLE);
    }


    public void setTitle(int stringID)
    {
        mTitle.setVisibility(View.VISIBLE);
        mTitle.setText(stringID);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.ivback:
                if (monClickListener != null)
                    monClickListener.onClick(v);
                break;

            case R.id.ivtitleimg:
                if (mShareonClickListener != null)
                    mShareonClickListener.onClick(v);
                break;
            case R.id.tvright:
                if (monSetClickListener != null)
                    monSetClickListener.onClick(v);
                break;
        }
    }

}
