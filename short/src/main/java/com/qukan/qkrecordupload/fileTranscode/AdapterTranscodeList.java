package com.qukan.qkrecordupload.fileTranscode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecorduploadsdk.RecordSdk;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-05-18.
 */
public class AdapterTranscodeList extends RecyclerView.Adapter<AdapterTranscodeList.TranscodeHolder>{

    Context context;
    ArrayList<FileTranscodeStatus> videoList=new ArrayList<FileTranscodeStatus>();                  //正在上传&等待上传的任务列表

    public AdapterTranscodeList(Context context)
    {
        this.context=context;
    }

    public void refreshData(ArrayList<String> videoList)
    //增加文件列表
    {
        for (String filename : videoList)
        {
            File file = new File(filename);
            FileTranscodeStatus status = new FileTranscodeStatus();
            status.setName(file.getPath());
            status.setTranscodeName(changeFileName(file.getPath()));
            status.setPercent(0);
            status.setStatus("init");

            synchronized (this.videoList)
            {
                this.videoList.add(status);
            }
        }

        notifyDataSetChanged();
    }

    public String changeFileName(String path)
    {
        int pos=path.lastIndexOf(".");
        if (pos!=-1)
        {
            String file = path.substring(0,pos);

            String Temp = file + "_transcode.mp4";
            return Temp;
        }
        else
        {
            return "file";
        }
    }

    public void refreshPercent(String fileName , long percent)
    //更新文件上传进度
    {
        synchronized (videoList)
        {
            for (FileTranscodeStatus info : videoList)
            {
                if (info.getName().equals(fileName))
                {
                    info.setPercent(percent);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void refreshEnd(String fileName)
    //更新文件上传进度
    {
        synchronized (videoList)
        {
            for (FileTranscodeStatus info : videoList)
            {
                if (info.getName().equals(fileName))
                {
                    info.setStatus("end");
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public TranscodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new TranscodeHolder(convertView);
    }

    @Override
    public void onBindViewHolder(TranscodeHolder holder, final int position) {
        FileTranscodeStatus status = this.videoList.get(position);
        holder.tvName.setText(status.getName());
        holder.tvStatus.setText(status.getTranscodeName());

        if (status.getStatus().equals("init"))
        {
            onStatusInit(holder,status);
        }
        else if (status.getStatus().equals("transcode"))
        {
            onStatusSyncing(holder,status);
        }
        else if (status.getStatus().equals("end"))
        {
            onStatusSynced(holder,status);
        }
    }

    private void onStatusInit(TranscodeHolder holder, final FileTranscodeStatus status)
    {
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("转码");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               int result= RecordSdk.newFileTranscode(status.getName(), status.getTranscodeName(), TranscodeInfo.instance().getWidth(), TranscodeInfo.instance().getHeight(), TranscodeInfo.instance().getBitRate());

                if (result != 0) {
                    Toast.makeText(context,"文件不支持转码",Toast.LENGTH_LONG).show();
                }
                status.setStatus("transcode");
                notifyDataSetChanged();
            }
        });
    }

    private void onStatusSyncing(TranscodeHolder holder, final FileTranscodeStatus status)
    {
        holder.tvPercent.setVisibility(View.VISIBLE);
        holder.tvPercent.setText(String.valueOf(status.getPercent()));
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("取消");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecordSdk.cancelFileTranscode(status.getName());

                status.setStatus("init");
                status.setPercent(0);
                notifyDataSetChanged();
            }
        });
    }

    private void onStatusSynced(TranscodeHolder holder, final FileTranscodeStatus status)
    {
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("删除");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                synchronized (videoList)
                {
                    videoList.remove(status);
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class TranscodeHolder extends RecyclerView.ViewHolder
    {
        LinearLayout llItem;
        ImageView ivFile;
        TextView tvName;
        TextView tvStatus;
        TextView tvPercent;
        TextView tvCancel;
        TextView tvPause;

        public TranscodeHolder(View itemView) {
            super(itemView);

            llItem=(LinearLayout)itemView.findViewById(R.id.ll_item);
            ivFile=(ImageView)itemView.findViewById(R.id.iv_fileImage);
            tvName=(TextView)itemView.findViewById(R.id.tv_name);
            tvPercent=(TextView)itemView.findViewById(R.id.tv_percent);
            tvCancel=(TextView)itemView.findViewById(R.id.tv_cancel);
            tvPause=(TextView)itemView.findViewById(R.id.tv_pause);
            tvStatus=(TextView)itemView.findViewById(R.id.tv_status);

//            ImageLoader.getInstance().displayImage("", this.ivFile, Utils.getDisplayImageOptions(R.mipmap.iv_video_image_bg));
        }
    }
}
