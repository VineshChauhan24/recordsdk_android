package com.qukan.qkrecordupload.fileCut.musicChild;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.MusicRes;

import java.util.List;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {

    private List<MusicRes> mMusicBeans;
    private OnItemClickListener mOnItemClickListener;

    public LocalMusicAdapter(List<MusicRes> localMusicBeans) {
        mMusicBeans = localMusicBeans;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_local_musci, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        MusicRes musicBean = mMusicBeans.get(i);
        viewHolder.tvName.setText(musicBean.getName());
        if (musicBean.isChoose()){
            viewHolder.ivChoose.setImageResource(R.drawable.short_ic_select);
        } else {
            viewHolder.ivChoose.setImageResource(R.drawable.short_ic_un_select);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(LocalMusicAdapter.this, v, viewHolder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChoose;
        TextView tvName;

        ViewHolder(View view) {
            super(view);
            ivChoose = view.findViewById(R.id.iv_choose);
            tvName = view.findViewById(R.id.tv_name);
        }

    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {

        void onItemClick(LocalMusicAdapter adapter, View view, int position);
    }

}
