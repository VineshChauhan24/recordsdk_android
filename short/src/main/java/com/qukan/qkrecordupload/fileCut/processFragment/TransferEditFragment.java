package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.bean.TransferEffect;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;

import lombok.Setter;

public class TransferEditFragment extends BaseFragment {

    private NewVideoProcessActivity activity;

    private Button btn_all;

    private ImageView ivTransfer0;

    private ImageView ivTransfer1;

    private ImageView ivTransfer2;

    private ImageView ivTransfer3;

    private ImageView ivTransfer4;

    private ImageView ivTransfer5;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewVideoProcessActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_transfer_edit, null);
        initView(view);
        return view;
    }

    private void initView(View view) {

        btn_all = (Button) view.findViewById(R.id.btn_all);
        btn_all.setOnClickListener(this);

        ivTransfer0 = (ImageView) view.findViewById(R.id.iv_transfer_0);
        ivTransfer0.setOnClickListener(this);

        ivTransfer1 = (ImageView) view.findViewById(R.id.iv_transfer_1);
        ivTransfer1.setOnClickListener(this);

        ivTransfer2 = (ImageView) view.findViewById(R.id.iv_transfer_2);
        ivTransfer2.setOnClickListener(this);

        ivTransfer3 = (ImageView) view.findViewById(R.id.iv_transfer_3);
        ivTransfer3.setOnClickListener(this);

        ivTransfer4 = (ImageView) view.findViewById(R.id.iv_transfer_4);
        ivTransfer4.setOnClickListener(this);

        ivTransfer5 = (ImageView) view.findViewById(R.id.iv_transfer_5);
        ivTransfer5.setOnClickListener(this);
    }

    @Override
    protected void onPostActivityCreated() {
        super.onPostActivityCreated();
    }

    @Override
    public void onClick(View v) {
        if (v == ivTransfer0) {
            unSelected();
            ivTransfer0.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(0);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == ivTransfer1) {
            unSelected();
            ivTransfer1.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(1);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == ivTransfer2) {
            unSelected();
            ivTransfer2.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(2);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == ivTransfer3) {
            unSelected();
            ivTransfer3.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(3);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == ivTransfer4) {
            unSelected();
            ivTransfer4.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(4);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == ivTransfer5) {
            unSelected();
            ivTransfer5.setBackgroundResource(R.drawable.border_bg_orange);
            if (transferCallback != null) {
                transferEffect.setType(5);
                transferCallback.onSelected(transferEffect, position);
            }

        } else if (v == btn_all) {
            if (transferCallback != null) {
                transferCallback.onUseAll(transferEffect);
            }
        }

    }

    private void unSelected() {
        ivTransfer0.setBackgroundResource(R.drawable.border_bg);
        ivTransfer1.setBackgroundResource(R.drawable.border_bg);
        ivTransfer2.setBackgroundResource(R.drawable.border_bg);
        ivTransfer3.setBackgroundResource(R.drawable.border_bg);
        ivTransfer4.setBackgroundResource(R.drawable.border_bg);
        ivTransfer5.setBackgroundResource(R.drawable.border_bg);

    }

    TransferEffect transferEffect;
    int position;

    public void fullData(TransferEffect transferEffect, int position) {
        this.transferEffect = transferEffect.copy();
        this.position = position;
        int type = transferEffect.getType();
        unSelected();
        switch (type) {
            case 0:
                ivTransfer0.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 1:
                ivTransfer1.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 2:
                ivTransfer2.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 3:
                ivTransfer3.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 4:
                ivTransfer4.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 5:
                ivTransfer5.setBackgroundResource(R.drawable.border_bg_orange);
                break;
            case 6:
                break;
        }
    }

    @Setter
    TransferCallback transferCallback;

    // 选择了哪个转场会回调给使用者 0：无效果 1：左边飞入  2：右边飞入  3：淡入淡出 4 闪白 5 闪黑 6 模糊(暂不支持)
    public interface TransferCallback {

        void onSelected(TransferEffect transferEffect, int position);

        void onUseAll(TransferEffect transferEffect);

    }

}
