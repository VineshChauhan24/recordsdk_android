package com.qukan.qkrecordupload.fileCut.processFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.qukan.qkrecordupload.BaseFragment;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.fileCut.customView.VerticalSeekBar;
import com.qukan.qkrecordupload.fileCut.processActivity.NewVideoProcessActivity;
import com.qukan.qkrecordupload.qkCut.QKColorFilterGroup;

import org.droidparts.annotation.inject.InjectView;

public class ColorFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener{
    // 亮度 ranges from -1.0 to 1.0, with 0.0 as the normal level

    private VerticalSeekBar fmBrightness;
    /**
     * 对比度 from 0.0 to 4.0 (max contrast), with 1.0 as the normal level
     */
    private VerticalSeekBar fmContrast;

    //饱和度 0.0 (fully desaturated) to 2.0 (max saturation), with 1.0 as the normal level
    private VerticalSeekBar fmSaturation;

    //锐度 -4.0 to 4.0, with 0.0 as the normal level

    private VerticalSeekBar fmSharpness;

    //色调设置色调 0 ~ 360
    private VerticalSeekBar fmHue;

    private QKColorFilterGroup filterGroup = null;



    private  ColorChangeCallback callback = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_new, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        fmBrightness = (VerticalSeekBar) view.findViewById(R.id.fm_brightness);

        fmContrast = (VerticalSeekBar) view.findViewById(R.id.fm_contrast);

        fmSaturation = (VerticalSeekBar) view.findViewById(R.id.fm_saturation);

        fmSharpness = (VerticalSeekBar) view.findViewById(R.id.fm_sharpness);

        fmHue = (VerticalSeekBar) view.findViewById(R.id.fm_hue);
    }

    @Override
    public void onClick(View v) {

    }


    public void init(QKColorFilterGroup filterGroup) {
        this.filterGroup = filterGroup;
        fmBrightness.setMax(200);
        fmContrast.setMax(200);
        fmSaturation.setMax(200);
        fmSharpness.setMax(800);
        fmHue.setMax(360);

        fmBrightness.setProgress((int)((filterGroup.getBrightness()+1.0)*100));

        double contrast = filterGroup.getContrast();
        int setContrast = 0;
        if(contrast > 1){
            setContrast = (int)((contrast - 1)/3.0*100) + 100;
        }else{
            setContrast = (int)(contrast*100);
        }

        fmContrast.setProgress(setContrast);
        fmSaturation.setProgress((int)(filterGroup.getSaturation()*100));
        fmSharpness.setProgress((int)((filterGroup.getSharpness()+4.0)*100));
        fmHue.setProgress(((int)filterGroup.getHue()+ 180)%360);

        fmBrightness.setOnSeekBarChangeListener(this);
        fmContrast.setOnSeekBarChangeListener(this);
        fmSaturation.setOnSeekBarChangeListener(this);
        fmSharpness.setOnSeekBarChangeListener(this);
        fmHue.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == fmBrightness) {
            if(progress > 95 && progress <105){
                progress = 100;
            }
            filterGroup.setBrightness(progress/100.0 - 1.0);
            Log.d("onProgressChanged","fmBrightness:" + filterGroup.getBrightness());
        } else if (seekBar == fmContrast) {
            if(progress > 95 && progress <105){
                progress = 100;
            }
            double contrast = 0;
            if(progress > 100){
                contrast = (progress - 100)/100.0*3.0 + 1;
            }else{
                contrast = progress/100.0;
            }
            filterGroup.setContrast(contrast);
            Log.d("onProgressChanged","double contrast = 0;:" + filterGroup.getContrast());
        } else if (seekBar == fmSaturation) {
            if(progress > 95 && progress <105){
                progress = 100;
            }
            filterGroup.setSaturation(progress/100.0);
            Log.d("onProgressChanged","fmSaturation:" + filterGroup.getSaturation());
        }
        else if (seekBar == fmSharpness) {
            if(progress > 380 && progress < 420){
                progress = 400;
            }
            filterGroup.setSharpness((progress - 400)/100.0);
            Log.d("onProgressChanged","fmSharpness:" + filterGroup.getSharpness());
        }
        else if (seekBar == fmHue) {
            if(progress >170 && progress <190){
                progress = 180;
            }
            filterGroup.setHue((progress + 180)%360);
            Log.d("onProgressChanged","fmHue:" + filterGroup.getHue());
        }

        if (callback != null) {
            callback.onColorChange(filterGroup);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ColorChangeCallback {

        void onColorChange(QKColorFilterGroup filterGroup);

    }

    public ColorChangeCallback getCallback() {
        return callback;
    }

    public void setCallback(ColorChangeCallback callback) {
        this.callback = callback;
    }
}
