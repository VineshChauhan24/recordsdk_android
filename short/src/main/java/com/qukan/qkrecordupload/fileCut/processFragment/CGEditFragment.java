package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;
import com.qukan.qkrecordupload.fileCut.CGEditChild.CGEditColorView;
import com.qukan.qkrecordupload.fileCut.CGEditChild.CGEditPropertyView;
import com.qukan.qkrecordupload.fileCut.CGEditChild.CGEditTextView;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import lombok.Setter;

public class CGEditFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private NewVideoProcessActivity activity;

    private LinearLayout llTabText;

    private ImageView ivTextIcon;

    private TextView tvText;

    private ImageView ivTextLine;


    private LinearLayout llTabColor;

    private ImageView ivColorIcon;

    private TextView tvCgColor;
    private TextView tvCgShow;

    private ImageView ivColorLine;


    private LinearLayout llTabProperty;

    private ImageView ivPropertyIcon;

    private ImageView ivPropertyLine;

    private TextView tvProperty;

    private Button btnSure;

    private ViewPager vpEdit;

    // 用于拦截点击事件，不然会透传到下层界面
    private LinearLayout llRoot;

    CGEditColorView cgEditColorView;
    CGEditTextView cgEditTextView;
    CGEditPropertyView cgEditPropertyView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subtitle_edit_new, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llTabText = view.findViewById(R.id.ll_cg_tab_text);
        llTabText.setOnClickListener(this);

        ivTextIcon = view.findViewById(R.id.iv_cg_text_icon);

        tvText = view.findViewById(R.id.tv_cg_text);
        tvCgShow = view.findViewById(R.id.tv_cg);


        ivTextLine = (ImageView) view.findViewById(R.id.iv_cg_text_line);

        llTabColor = (LinearLayout) view.findViewById(R.id.ll_cg_tab_color);
        llTabColor.setOnClickListener(this);

        ivColorIcon = (ImageView) view.findViewById(R.id.iv_cg_color_icon);

        tvCgColor = (TextView) view.findViewById(R.id.tv_cg_color);

        ivColorLine = (ImageView) view.findViewById(R.id.iv_cg_color_line);

        llTabProperty = (LinearLayout) view.findViewById(R.id.ll_cg_tab_property);
        llTabProperty.setOnClickListener(this);

        ivPropertyIcon = (ImageView) view.findViewById(R.id.iv_cg_property_icon);

        ivPropertyLine = (ImageView) view.findViewById(R.id.iv_cg_property_line);

        tvProperty = (TextView) view.findViewById(R.id.tv_cg_property);

        btnSure = (Button) view.findViewById(R.id.btn_cg_sure);
        btnSure.setOnClickListener(this);

        vpEdit = (ViewPager) view.findViewById(R.id.vp_cg_edit);

        llRoot = (LinearLayout) view.findViewById(R.id.ll_root);
        llRoot.setOnClickListener(this);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
        vpEdit.setAdapter(new ViewPagerAdapter());
        vpEdit.addOnPageChangeListener(this);

        cgEditColorView = new CGEditColorView(activity);
        cgEditTextView = new CGEditTextView(activity);
        cgEditPropertyView = new CGEditPropertyView(activity);
        vpEdit.setOffscreenPageLimit(2);
    }

    ViewTitleOnePart videoBean;
    TextView textView;
    VideoTitleUse videoTitleUse;
    VideoTitleBean videoTitleBean;

    public void init(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean, TextView textView, VideoTitleBean videoTitleBean) {
        this.videoBean = videoBean;
        this.textView = textView;
        this.videoTitleUse = videoTitleUse;
        this.videoTitleBean = videoTitleBean;
        tvCgShow.setTextColor(textView.getTextColors());
        tvCgShow.setBackground(textView.getBackground());
        tvCgShow.setTextSize(videoBean.getFontSize());
        tvCgShow.setText(textView.getText());
        tvCgShow.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), textView.getPaddingBottom());

        showTabText();
        vpEdit.setCurrentItem(0);
        // 这是给新华移动做的拖动控件，我们自己的不需要
//        VideoDragBar videoDragBar = new VideoDragBar(activity, activity.getVideoDataLists(),videoTitleUse,videoTitleBean);
//        rlTimeControl.addView(videoDragBar.getPageView());

    }

    @Override
    public void onClick(View v) {
        if (v == llTabText) {
            vpEdit.setCurrentItem(0);
        } else if (v == llTabColor) {
            vpEdit.setCurrentItem(1);
        } else if (v == llTabProperty) {
            vpEdit.setCurrentItem(2);
        } else if (v == btnSure) {
            if (cgEditTextView.isTextEmpty()) {
                RnToast.showToast(getActivity(), "请输入字幕");
            } else {
                closeInput();
                activity.setFragmentVisible(false, this);
                if (cgEditCallback != null) {
                    cgEditCallback.onClose();
                }

            }

        }
    }

    @Setter
    CGEditCallback cgEditCallback;

    public interface CGEditCallback {
        void onClose();
    }

    public void closeInput() {
        activity.closeInput(cgEditTextView.getEtEdit());
    }

    private void showTabText() {
        ivTextIcon.setImageResource(R.mipmap.cg_text_in);
        ivTextLine.setVisibility(View.VISIBLE);
        tvText.setTextColor(getResources().getColor(R.color.white));
        cgEditTextView.init(videoBean, textView, tvCgShow);
    }

    private void showTabColor() {
        ivColorIcon.setImageResource(R.mipmap.cg_color_in);
        ivColorLine.setVisibility(View.VISIBLE);
        tvCgColor.setTextColor(getResources().getColor(R.color.white));
        cgEditColorView.init(videoBean, textView, tvCgShow);
    }

    private void showTabProperty() {
        ivPropertyIcon.setImageResource(R.mipmap.cg_property_in);
        ivPropertyLine.setVisibility(View.VISIBLE);
        tvProperty.setTextColor(getResources().getColor(R.color.white));
        cgEditPropertyView.init(videoTitleUse, videoBean, textView, videoTitleBean, activity, tvCgShow);
    }

    private void resetAll() {
        closeInput();
        ivTextIcon.setImageResource(R.mipmap.cg_text_out);
        ivTextLine.setVisibility(View.INVISIBLE);
        tvText.setTextColor(getResources().getColor(R.color.clip_text));

        ivColorIcon.setImageResource(R.mipmap.cg_color_out);
        ivColorLine.setVisibility(View.INVISIBLE);
        tvCgColor.setTextColor(getResources().getColor(R.color.clip_text));

        ivPropertyIcon.setImageResource(R.mipmap.cg_property_out);
        ivPropertyLine.setVisibility(View.INVISIBLE);
        tvProperty.setTextColor(getResources().getColor(R.color.clip_text));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        resetAll();
        if (position == 0) {
            showTabText();
        } else if (position == 1) {
            showTabColor();
        } else if (position == 2) {
            showTabProperty();
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                container.addView(cgEditTextView.getPageView());
                return cgEditTextView.getPageView();
            } else if (position == 1) {
                container.addView(cgEditColorView.getPageView());
                return cgEditColorView.getPageView();
            } else {
                container.addView(cgEditPropertyView.getPageView());
                return cgEditPropertyView.getPageView();
            }
        }

    }
}
