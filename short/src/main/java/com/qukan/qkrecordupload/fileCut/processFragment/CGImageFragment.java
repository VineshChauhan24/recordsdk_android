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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.VideoTitleBean;
import com.qukan.qkrecordupload.bean.VideoTitleUse;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;
import com.qukan.qkrecordupload.fileCut.CGEditChild.CGImageEditPropertyView;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import org.droidparts.annotation.inject.InjectParentActivity;
import org.droidparts.annotation.inject.InjectView;

import lombok.Setter;

public class CGImageFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private NewVideoProcessActivity activity;

    private LinearLayout llTabProperty;

    private ImageView ivPropertyIcon;

    private ImageView ivPropertyLine;

    private TextView tvProperty;

    private Button btnSure;

    private ViewPager vpEdit;

    // 用于拦截点击事件，不然会透传到下层界面
    private LinearLayout llRoot;

    CGImageEditPropertyView cgEditPropertyView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_edit_new, null);
        initView(view);
        return view;
    }

    private void initView(View view){
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

        cgEditPropertyView = new CGImageEditPropertyView(activity);
        vpEdit.setOffscreenPageLimit(0);
    }

    ViewTitleOnePart videoBean;
    VideoTitleUse videoTitleUse;
    VideoTitleBean videoTitleBean;
    ImageView imageView;
    public void init(VideoTitleUse videoTitleUse, ViewTitleOnePart videoBean,ImageView imageView,VideoTitleBean videoTitleBean) {
        this.videoBean = videoBean;
        this.videoTitleUse = videoTitleUse;
        this.videoTitleBean = videoTitleBean;
        this.imageView = imageView;
        showTabProperty();
        vpEdit.setCurrentItem(0);
        // 这是给新华移动做的拖动控件，我们自己的不需要
//        VideoDragBar videoDragBar = new VideoDragBar(activity, activity.getVideoDataLists(),videoTitleUse,videoTitleBean);
//        rlTimeControl.addView(videoDragBar.getPageView());
    }

    @Override
    public void onClick(View v) {
        if (v == llTabProperty) {
            vpEdit.setCurrentItem(0);
        } else if (v == btnSure) {
            activity.setFragmentVisible(false, this);
            if (cgEditCallback != null) {
                cgEditCallback.onClose();
            }
        }
    }

    @Setter
    CGEditCallback cgEditCallback;

    public interface CGEditCallback {
        void onClose();
    }


    private void showTabProperty() {
        ivPropertyIcon.setImageResource(R.mipmap.cg_property_in);
        ivPropertyLine.setVisibility(View.VISIBLE);
        tvProperty.setTextColor(getResources().getColor(R.color.white));
        cgEditPropertyView.init(videoTitleUse, videoBean, videoTitleBean,imageView, activity);
    }

    private void resetAll() {

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
        showTabProperty();
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
            container.addView(cgEditPropertyView.getPageView());
            return cgEditPropertyView.getPageView();
        }

    }
}
