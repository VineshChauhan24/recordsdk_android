package com.qukan.qkrecordupload.fileUpload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qukan.qkrecordupload.PublicUtils;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.db.DBHelper;
import com.qukan.qkrecordupload.db.FileNameHelper;
import com.qukan.qkrecordupload.db.MegerFileMap;
import com.qukan.qkrecorduploadsdk.RecordSdk;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-04-15.
 */
public class AdapterFileList extends RecyclerView.Adapter<AdapterFileList.FileHolder>{

    Context context;
    ArrayList<FileInfoStatus> videoList=new ArrayList<FileInfoStatus>();                  //正在上传&等待上传的任务列表

    public AdapterFileList(Context context)
    {
        this.context=context;
    }

    public void refreshData()
    {
        ArrayList<FileInfoStatus> fileList = DBHelper.instance().selectUnfinished();

        synchronized (this.videoList)
        {
            this.videoList.clear();
            this.videoList.addAll(fileList);

            for (FileInfoStatus status : fileList)
            {
                if (status.getStatus().equals("uploading"))
                {
                    RecordSdk.fileUpload(status.getInfo());
                }
                else if(status.getStatus().equals("syncing"))
                {
                    RecordSdk.newFileSync(status.getInfo());
                }
                else if(status.getStatus().equals("merge"))
                {
                    ArrayList<String> list = MegerFileMap.instance().checkFileMergeInfo(status.getInfo().getUploadName());

                    if (!list.isEmpty())
                    {
                        RecordSdk.fileMerger(list);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    public void refreshData(ArrayList<String> videoList)
            //增加文件列表
    {
        for (String filename : videoList)
        {
            File file = new File(filename);
            FileInfoStatus status = new FileInfoStatus();

            status.getInfo().setFileName(file.getName());
            status.getInfo().setFilePath(file.getPath());
            status.getInfo().setFileLength(String.valueOf(file.length()));
            status.getInfo().setFileType("3");
            status.getInfo().setFlag(PublicUtils.getCurrentTime());

            String uploadName = FileNameHelper.getTimeAndRandom();
            String suffix = file.getName().substring(file.getName().indexOf("."));
            status.getInfo().setUploadName(uploadName + suffix);

            status.setStatus("init");

            synchronized (this.videoList)
            {
                this.videoList.add(status);
            }

            //插入数据库
            DBHelper.instance().insert(status);
        }

        notifyDataSetChanged();
    }

    public void refreshPercent(String fileName ,String flag, long percent)
            //更新文件上传进度
    {
        synchronized (videoList)
        {
            for (FileInfoStatus info : videoList)
            {
                if (info.getInfo().getFileName().equals(fileName)&&info.getInfo().getFlag().equals(flag))
                {
                    info.setPercent(percent);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void refreshComplete(String fileName ,String flag, String uploadPath , boolean result)
    {
        synchronized (videoList)
        {
            for (FileInfoStatus info : videoList)
            {
                if (info.getInfo().getFileName().equals(fileName)&&info.getInfo().getFlag().equals(flag))
                {
                    if (result)
                    {
                        info.setStatus("synced");
                        info.getInfo().setUploadPath(uploadPath);
                    }
                    else
                    {
                        info.setStatus("init");
                    }

                    //插入数据库
                    DBHelper.instance().update(info);

                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void refreshUpload(String fileName ,String flag, boolean result)
    {
        synchronized (videoList)
        {
            for (FileInfoStatus info : videoList)
            {
                if (info.getInfo().getFileName().equals(fileName)&&info.getInfo().getFlag().equals(flag))
                {
                    if (result)
                    {
                        info.setStatus("uploaded");
                    }
                    else
                    {
                        info.setStatus("synced");
                    }

                    //插入数据库
                    DBHelper.instance().update(info);

                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void refreshMerge(String fileName, boolean result)
    {
        synchronized (videoList)
        {
            for (FileInfoStatus info : videoList)
            {
                if (info.getInfo().getUploadName().equals(fileName))
                {
                    if (result)
                    {
                        info.setStatus("end");
                    }
                    else
                    {
                        info.setStatus("uploaded");
                    }

                    //插入数据库
                    DBHelper.instance().update(info);

                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void deleteFileInfo(String fileName,String flag)
    {
        synchronized (videoList)
        {
            for (FileInfoStatus info : videoList)
            {
                if (info.getInfo().getFileName().equals(fileName)&&info.getInfo().getFlag().equals(flag))
                {
                    videoList.remove(info);
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public FileHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView= LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
        return new FileHolder(convertView);
    }

    @Override
    public void onBindViewHolder(FileHolder holder, final int position) {
        FileInfoStatus status = this.videoList.get(position);
        holder.tvName.setText(status.getInfo().getFilePath());

//        ImageLoader.getInstance().displayImage("file://"+status.getInfo().getFilePath(), holder.ivFile, Utils.getDisplayImageOptions(R.mipmap.iv_video_image_bg));

        // init 未上传 ， syncing 上传中 ， synced 上传结束 ，uploading 提交中 ，uploaded 提交结束 ， merge 合并中 ， end 结束
        if (status.getStatus().equals("init"))
        {
            onStatusInit(holder,status);
        }
        else if (status.getStatus().equals("syncing"))
        {
            onStatusSyncing(holder,status);
        }
        else if (status.getStatus().equals("synced"))
        {
            onStatusSynced(holder,status);
        }
        else if (status.getStatus().equals("uploading"))
        {
            onStatusUploading(holder,status);
        }
        else if (status.getStatus().equals("uploaded"))
        {
            onStatusUploaded(holder,status);
        }
        else if (status.getStatus().equals("merge"))
        {
            onStatusMerge(holder,status);
        }
        else if (status.getStatus().equals("end"))
        {
            onStatusEnd(holder,status);
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void onStatusInit(FileHolder holder , final FileInfoStatus status)
            //初始状态
    {
        status.setPasuse(false);
        status.setPercent(0);

        holder.tvStatus.setText("未上传");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("上传");
        holder.tvPause.setVisibility(View.VISIBLE);
        holder.tvPause.setText("删除");

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordSdk.newFileSync(status.getInfo());
                status.setStatus("syncing");

                //插入数据库
                DBHelper.instance().update(status);

                notifyDataSetChanged();
            }
        });

        holder.tvPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除自己
                deleteFileInfo(status.getInfo().getFileName(),status.getInfo().getFlag());

                //插入数据库
                DBHelper.instance().delete(status.getInfo().getFilePath());
            }
        });
    }

    public void onStatusSyncing(final FileHolder holder , final FileInfoStatus status)
            //文件上传中
    {
        holder.tvStatus.setText("文件上传中");
        holder.tvPercent.setVisibility(View.VISIBLE);
        holder.tvPercent.setText(String.valueOf(status.getPercent()));
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("取消");
        holder.tvPause.setVisibility(View.GONE);

//        if (!status.getPasuse())
//        {
//            holder.tvPause.setText("暂停");
//        }
//        else
//        {
//            holder.tvPause.setText("继续");
//        }

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordSdk.deleteFileSync(status.getInfo());
                status.setStatus("init");

                //插入数据库
                DBHelper.instance().update(status);

                notifyDataSetChanged();
            }
        });

//        holder.tvPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (status.getPasuse())
//                {
//                    RecordSdk.newFileSync(status.getInfo());
//                    status.setPasuse(false);
//                    holder.tvPause.setText("暂停");
//                }
//                else
//                {
//                    RecordSdk.paussedFileSync(status.getInfo());
//                    status.setPasuse(true);
//                    holder.tvPause.setText("继续");
//                }
//            }
//        });
    }

    public void onStatusSynced(FileHolder holder , final FileInfoStatus status)
            //文件上传结束
    {
        status.setPasuse(false);

        holder.tvStatus.setText("文件未提交");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("提交");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordSdk.fileUpload(status.getInfo());
                status.setStatus("uploading");

                //插入数据库
                DBHelper.instance().update(status);

                notifyDataSetChanged();
            }
        });
    }

    public void onStatusUploading(FileHolder holder , FileInfoStatus status)
            //文件提交中
    {
        status.setPasuse(false);

        holder.tvStatus.setText("文件提交中");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.GONE);
        holder.tvPause.setVisibility(View.GONE);
    }

    public void onStatusUploaded(FileHolder holder , final FileInfoStatus status)
            //文件提交结束
    {
        status.setPasuse(false);

        holder.tvStatus.setText("文件未合并");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("合并");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<String> fileList = MegerFileMap.instance().checkFileMergeInfo(status.getInfo().getUploadName());

                if (!fileList.isEmpty())
                {
                    RecordSdk.fileMerger(fileList);
                }

                status.setStatus("merge");

                //插入数据库
                DBHelper.instance().update(status);

                notifyDataSetChanged();
            }
        });
    }

    public void onStatusMerge(FileHolder holder , FileInfoStatus status)
            //文件合并中
    {
        status.setPasuse(false);

        holder.tvStatus.setText("文件合并中");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.GONE);
        holder.tvPause.setVisibility(View.GONE);
    }

    public void onStatusEnd(FileHolder holder , final FileInfoStatus status)
            //文件结束
    {
        status.setPasuse(false);

        holder.tvStatus.setText("文件上传结束");
        holder.tvPercent.setVisibility(View.GONE);
        holder.tvCancel.setVisibility(View.VISIBLE);
        holder.tvCancel.setText("删除");
        holder.tvPause.setVisibility(View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //删除自己
                deleteFileInfo(status.getInfo().getFileName(),status.getInfo().getFlag());

                //插入数据库
                DBHelper.instance().delete(status.getInfo().getFilePath());
            }
        });
    }

    class FileHolder extends RecyclerView.ViewHolder
    {
        LinearLayout llItem;
        ImageView ivFile;
        TextView tvName;
        TextView tvStatus;
        TextView tvPercent;
        TextView tvCancel;
        TextView tvPause;

        public FileHolder(View itemView) {
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
