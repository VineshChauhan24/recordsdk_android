package com.qukan.qkrecordupload.fileCut.CGEditChild;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;

public class CGEditColorView implements View.OnClickListener {

    private ImageView ivBlack;

    private ImageView iv_white;

    private ImageView iv_grey;

    private ImageView iv_red;

    private ImageView iv_green;

    private ImageView iv_orange;

    private ImageView iv_blue;

    private ImageView iv_purple;

    private ImageView iv_rose_red;

    private ImageView iv_yellow;

    private ImageView iv_light_blue;

    private ImageView iv_cyan;

    private ImageView iv_peach;

    private ImageView iv_dark_blue;

    private ImageView iv_light_green;

    private ImageView iv_coffee;

    private ImageView iv_dark_grey;

    private ImageView iv_dark_brown;

    private ImageView iv_light_brown;

    private ImageView iv_dark_green;

    Context context;
    View inflate;
    ViewTitleOnePart mVideoBean;
    private TextView mTextView;
    private TextView tvCG;

    public CGEditColorView(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(context).inflate(R.layout.fragment_cgedit_color, null);
//        Injector.inject(inflate, this);
        initView(inflate);
    }

    private void initView(View view){
        ivBlack = (ImageView) view.findViewById(R.id.iv_black);
        ivBlack.setOnClickListener(this);

        iv_white = (ImageView) view.findViewById(R.id.iv_white);
        iv_white.setOnClickListener(this);

        iv_grey = (ImageView) view.findViewById(R.id.iv_grey);
        iv_grey.setOnClickListener(this);

        iv_red = (ImageView) view.findViewById(R.id.iv_red);
        iv_red.setOnClickListener(this);

        iv_green = (ImageView) view.findViewById(R.id.iv_green);
        iv_green.setOnClickListener(this);

        iv_orange = (ImageView) view.findViewById(R.id.iv_orange);
        iv_orange.setOnClickListener(this);

        iv_blue = (ImageView) view.findViewById(R.id.iv_blue);
        iv_blue.setOnClickListener(this);

        iv_purple = (ImageView) view.findViewById(R.id.iv_purple);
        iv_purple.setOnClickListener(this);

        iv_rose_red = (ImageView) view.findViewById(R.id.iv_rose_red);
        iv_rose_red.setOnClickListener(this);

        iv_yellow = (ImageView) view.findViewById(R.id.iv_yellow);
        iv_yellow.setOnClickListener(this);

        iv_light_blue = (ImageView) view.findViewById(R.id.iv_light_blue);
        iv_light_blue.setOnClickListener(this);

        iv_cyan = (ImageView) view.findViewById(R.id.iv_cyan);
        iv_cyan.setOnClickListener(this);

        iv_peach = (ImageView) view.findViewById(R.id.iv_peach);
        iv_peach.setOnClickListener(this);

        iv_dark_blue = (ImageView) view.findViewById(R.id.iv_dark_blue);
        iv_dark_blue.setOnClickListener(this);

        iv_light_green = (ImageView) view.findViewById(R.id.iv_light_green);
        iv_light_green.setOnClickListener(this);

        iv_coffee = (ImageView) view.findViewById(R.id.iv_coffee);
        iv_coffee.setOnClickListener(this);

        iv_dark_grey = (ImageView) view.findViewById(R.id.iv_dark_grey);
        iv_dark_grey.setOnClickListener(this);

        iv_dark_brown = (ImageView) view.findViewById(R.id.iv_dark_brown);
        iv_dark_brown.setOnClickListener(this);

        iv_light_brown = (ImageView) view.findViewById(R.id.iv_light_brown);
        iv_light_brown.setOnClickListener(this);

        iv_dark_green = (ImageView) view.findViewById(R.id.iv_dark_green);
        iv_dark_green.setOnClickListener(this);
    }

    public View getPageView() {
        return this.inflate;
    }

    @Override
    public void onClick(View v) {
        String color = " ";
        if (v == ivBlack) {
            color = context.getString(R.string.cg_black);
        } else if (v == iv_white) {
            color = context.getString(R.string.cg_white);
        } else if (v == iv_grey) {
            color = context.getString(R.string.cg_grey);
        }else if (v == iv_red) {
            color = context.getString(R.string.cg_red);
        }else if (v == iv_green) {
            color = context.getString(R.string.cg_green);
        }else if (v == iv_orange) {
            color = context.getString(R.string.cg_orange);
        }else if (v == iv_blue) {
            color = context.getString(R.string.cg_blue);
        }else if (v == iv_purple) {
            color = context.getString(R.string.cg_purple);
        }else if (v == iv_rose_red) {
            color = context.getString(R.string.cg_rose_red);
        }else if (v == iv_yellow) {
            color = context.getString(R.string.cg_yellow);
        }else if (v == iv_light_blue) {
            color = context.getString(R.string.cg_light_blue);
        }else if (v == iv_cyan) {
            color = context.getString(R.string.cg_cyan);
        }else if (v == iv_peach) {
            color = context.getString(R.string.cg_peach);
        }else if (v == iv_dark_blue) {
            color = context.getString(R.string.cg_dark_blue);
        }else if (v == iv_light_green) {
            color = context.getString(R.string.cg_light_green);
        }else if (v == iv_coffee) {
            color = context.getString(R.string.cg_coffee);
        }else if (v == iv_dark_grey) {
            color = context.getString(R.string.cg_dark_grey);
        }else if (v == iv_dark_brown) {
            color = context.getString(R.string.cg_dark_brown);
        }else if (v == iv_light_brown) {
            color = context.getString(R.string.cg_light_brown);
        }else if (v == iv_dark_green) {
            color = context.getString(R.string.cg_dark_green);
        }
        color = color.trim();
        if (!TextUtil.isEmpty(color)) {
            mVideoBean.setTextColor(color);
            mTextView.setTextColor(Color.parseColor(color));
            tvCG.setTextColor(Color.parseColor(color));
        }
    }

    public void init(ViewTitleOnePart videoBean, TextView textView, TextView tvCGShow) {
        this.mVideoBean = videoBean;
        this.mTextView = textView;
        tvCG = tvCGShow;
    }
}
