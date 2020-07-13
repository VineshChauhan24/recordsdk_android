package com.qukan.qkrecordupload.fileCut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.VideoProcessBean;

import org.droidparts.adapter.holder.ViewHolder;
import org.droidparts.annotation.inject.InjectView;
import org.droidparts.util.L;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

// 视频剪辑待处理视频list的适配器
public class VideoClipSourceAdapter extends BaseAdapter {
    List<VideoProcessBean> mList = new ArrayList<>();
    private Context mContext;
    DisplayImageOptions options;

    @Getter
    @Setter
    public int selectedPosition;
    public VideoClipSourceAdapter(Context context, List<VideoProcessBean> data) {
        this.mContext = context;
        refresh(data);
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.live_logo)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.live_logo)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.live_logo)       // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中
                .build();
    }

    public void refresh(List<VideoProcessBean> data) {
        mList.clear();
        mList.addAll(data);
        // 用于增加按钮
        VideoProcessBean videoProcessBean = new VideoProcessBean();
        mList.add(videoProcessBean);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolderHlv holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video_clip, null);
            holder = new ViewHolderHlv(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderHlv) convertView.getTag();
        }
        holder.position = position;
        VideoProcessBean videoProcessBean = mList.get(position);
        holder.tvTime.setVisibility(View.VISIBLE);
        // 如果是最后一个就是add按钮
        if (position == getCount() - 1) {
            holder.mImage.setImageResource(R.mipmap.cg_plus_size);
            holder.mImage.setBackgroundResource(R.drawable.item_border);
            holder.tvTime.setVisibility(View.GONE);
            return convertView;
        }

        if (position== selectedPosition) {
            holder.rlLlistItem.setBackgroundResource(R.color.orange);
        } else {
            holder.rlLlistItem.setBackgroundResource(R.color.transparent);
        }

        final String uri = "file://" + videoProcessBean.getPath();
        ImageLoader.getInstance().displayImage(uri, holder.mImage, options);
        String time = PublicUtils.msToHms(videoProcessBean.getEndTime());
        holder.tvTime.setText(time);
        L.d("selectedPosition=%s",selectedPosition);
        return convertView;
    }

    class ViewHolderHlv extends ViewHolder implements View.OnClickListener {

        private ImageView mImage;

        private RelativeLayout rlLlistItem;

        public TextView tvTime;

        private int position;

        public ViewHolderHlv(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.img_list_item);

            rlLlistItem = (RelativeLayout) view.findViewById(R.id.rl_list_item);

            tvTime = (TextView) view.findViewById(R.id.tv_time);
        }

        @Override
        public void onClick(View v) {

        }
    }
}