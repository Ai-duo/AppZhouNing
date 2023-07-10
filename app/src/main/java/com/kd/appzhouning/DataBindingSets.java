package com.kd.appzhouning;

import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class DataBindingSets {
    @BindingAdapter("setText")
    public static void setText(TextView view,String txt){
        if (TextUtils.isEmpty(txt))return;
        view.setText(Html.fromHtml(txt));
    }
}
