package com.qukan.qkrecordupload.compop;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qukan.qkrecordupload.QkApplication;
import com.qukan.qkrecordupload.R;
import com.qukan.qkrecordupload.RnToast;

import org.droidparts.annotation.inject.InjectView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Setter;

/**
 * Created by Administrator on 2017/2/14 0014.
 * 通用的带一个输入框的pop
 */
public class CommonInputPop extends BasePopupWindow {

    TextView tvOk;

    TextView tvCancel;

    TextView tvTitle;

    EditText edtTitle;

    @Setter
    ListenerPop onClickListenerPup;

    Context mContext;



    public CommonInputPop(Context context)
    {
        super(context);
        setEmojiFilter(edtTitle);
        setFocusable(true);
        mContext = context;
    }

    public interface ListenerPop {
        public void onSureClick(CommonInputPop popupWindow);
        public void onCancelClick(CommonInputPop popupWindow);
    }
    @Override
    public View setPopview()
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_input, null);
        initView(view);
        return view;
    }

    private void initView(View view){
        tvOk = (TextView) view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(this);

        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);

        edtTitle = (EditText) view.findViewById(R.id.edt_title);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvCancel(String cancel) {
        tvCancel.setText(cancel);
    }

    public void setTvSure(String sure) {
        tvOk.setText( sure);
    }

    public void setEditText(String text) {
        edtTitle.setText(text);
    }

    public void setEditHint(String text) {
        edtTitle.setHint(text);
    }

    public String getEditText() {
        return edtTitle.getText().toString();
    }

    @Override
    public void onClick(View v)
    {
        if (v == tvCancel)
        {
            if (onClickListenerPup != null) {
                onClickListenerPup.onCancelClick(this);
            }
        } else if (v == tvOk) {
            if (onClickListenerPup != null) {
                onClickListenerPup.onSureClick(this);
            }
        }
    }
    public void setEmojiFilter(EditText et) {
        InputFilter emojiFilter = new InputFilter() {
            Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher matcher = pattern.matcher(source);
                if(matcher.find()){
                    RnToast.showToast(QkApplication.getContext(),mContext.getString(R.string.input_check_char));

                    return "";
                }
                return null;
            }
        };
        et.setFilters(new InputFilter[]{emojiFilter});
    }
}
