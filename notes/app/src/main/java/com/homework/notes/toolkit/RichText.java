package com.homework.notes.toolkit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

public class RichText extends AppCompatEditText {


    public RichText(Context context){
        super(context);
    }

    public RichText(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void insertImage(String imagePath){
        String url = "<img src='" + imagePath + "'/>";
        final SpannableString s = new SpannableString(url);
        //Log.i("aa",imagePath);
        Drawable img = Drawable.createFromPath(imagePath);
        //Log.i("aa",img.toString());
        img.setBounds(0,0,img.getIntrinsicWidth(),img.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(img, ImageSpan.ALIGN_BASELINE);
        s.setSpan(span,0,url.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        append(s);
    }
}

