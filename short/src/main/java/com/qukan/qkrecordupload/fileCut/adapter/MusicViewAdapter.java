package com.qukan.qkrecordupload.fileCut.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.MusicRes;

import org.droidparts.adapter.holder.ViewHolder;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

// mp3音乐适配器
public class MusicViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<MusicRes> musicResList = new ArrayList<>();
    private Map<String, String> musicPathMap = new HashMap<>();

    public static final int UN_SELECTED = 3;
    public static final int SELECTED = 4;

    public MusicViewAdapter(Context context) {
        this.mContext = context;
        // 添加加号
        musicResList.add(new MusicRes());
        notifyDataSetChanged();
    }

    public void addNewMusic(List<MusicRes> list) {
        for (MusicRes bean : list) {
            String state = musicPathMap.get(bean.getPcmPath());
            if (state == null) {
                musicResList.add(bean);
                musicPathMap.put(bean.getPcmPath(), "seted");
            } else {
                Log.e("背景音设置", "请勿设置重复的音乐");
            }
        }
        notifyDataSetChanged();
    }


    // -1为未选中任何数据
    @Getter
    int selPosition = -1;

    public void setSelected(int selPosition) {
        this.selPosition = selPosition;
        notifyDataSetChanged();
    }

    public void setSelectedByPcmPath(String pcmPath) {
        if (TextUtil.isEmpty(pcmPath)) {
            return;
        }
        for (int i = 0; i < musicResList.size(); i++) {
            MusicRes bean = musicResList.get(i);
            if (pcmPath.equals(bean.getPcmPath())) {
                this.selPosition = i;
                notifyDataSetChanged();
                return;
            }
        }
        notifyDataSetChanged();
    }

    public void setSelectedByUrl(String url) {
        if (TextUtil.isEmpty(url)) {
            return;
        }
        for (int i = 0; i < musicResList.size(); i++) {
            MusicRes bean = musicResList.get(i);
            if (url.equals(bean.getUrl())) {
                this.selPosition = i;
                notifyDataSetChanged();
                return;
            }
        }

    }


    public MusicRes getSelected() {
        if (selPosition > 0) {
            return musicResList.get(selPosition);
        } else {
            return null;
        }
    }

    public void refresh() {
        // 从数据库中查出所有已经下载好的音乐，同时判断pcm文件是否已经存在
        List<MusicRes> userBeanList = DataSupport.where("state >= ?", "3").order("timeFlag asc").find(MusicRes.class);
        musicResList.clear();
        // 添加加号
        musicResList.add(new MusicRes());
        if (userBeanList != null && !userBeanList.isEmpty()) {
            for (MusicRes musicRes : userBeanList) {
                String pcmPath = musicRes.getPcmPath();
                File file = new File(pcmPath);
                if (!file.exists()) {
                    continue;
                }
                musicResList.add(musicRes);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return musicResList.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music_mp3, null);
            holder = new ViewHolderHlv(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderHlv) convertView.getTag();
        }

        if (position == 0) {
            holder.llRoot.setBackgroundResource(R.color.transparent);
            holder.tvName.setText("");
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.ivSelect.setImageResource(R.color.transparent);
            return convertView;
        }

        //holder.llRoot.setBackgroundResource(R.mipmap.mp3_bak);
        holder.position = position;
        MusicRes bean = musicResList.get(position);
        holder.ivAdd.setVisibility(View.GONE);
        holder.tvName.setText(bean.getName());
        if (selPosition == position) {
            holder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.short_color_4786));
            //holder.ivSelect.setImageResource(R.mipmap.video_title_selected);
        } else {
            holder.tvName.setTextColor(Color.WHITE);
            // holder.ivSelect.setImageResource(R.color.transparent);
        }
        return convertView;
    }


    class ViewHolderHlv extends ViewHolder {

        public TextView tvName;


        public ImageView ivSelect;

        public ImageView ivAdd;

        public LinearLayout llRoot;

        private int position;

        public ViewHolderHlv(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_mp3_name);
            ivSelect = view.findViewById(R.id.iv_select);
            ivAdd = view.findViewById(R.id.iv_add);
            llRoot = view.findViewById(R.id.ll_root);

        }
    }
}