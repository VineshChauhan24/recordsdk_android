package com.qukan.qkrecordupload.fileCut.CGEditChild;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.TextUtil;
import com.qukan.qkrecordupload.bean.ViewTitleOnePart;

import org.droidparts.Injector;

import lombok.Getter;

public class CGEditTextView implements View.OnClickListener {

    @Getter
    private EditText etEdit;

    ViewTitleOnePart mVideoBean;
    Context context;
    View inflate;
    private TextView mTextView;
    private TextView tvCG;

    public CGEditTextView(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        inflate = LayoutInflater.from(context).inflate(R.layout.fragment_cgedit_text, null);
        Injector.inject(inflate, this);

        etEdit = inflate.findViewById(R.id.et_edit);

        etEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mVideoBean.setText(s.toString());
                mTextView.setText(s.toString());
                tvCG.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public View getPageView() {
        return this.inflate;
    }

    @Override
    public void onClick(View v) {

    }

    public void init(ViewTitleOnePart videoBean, TextView textView, TextView tvCGShow) {
        mVideoBean = videoBean;
        this.mTextView = textView;
        this.tvCG = tvCGShow;
        etEdit.setText(videoBean.getText());
    }

    public boolean isTextEmpty() {
        return TextUtil.isEmpty(etEdit.getText().toString());
    }

}
