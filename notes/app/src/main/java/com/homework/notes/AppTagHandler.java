package com.homework.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.graphics.drawable.Drawable;

import org.xml.sax.XMLReader;

import java.util.Locale;


public class AppTagHandler implements Html.TagHandler {
    private Context myContext;
    private PopupWindow popupWindow;
    private ImageView imageView;

    public AppTagHandler(Context context){
        myContext = context.getApplicationContext();
        View popView = LayoutInflater.from(context).inflate(R.layout.popwindow, null);
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x50000000);
        popupWindow.setBackgroundDrawable(dw);
        imageView = (ImageView) popView.findViewById(R.id.image_scale_image);

        popView.findViewById(R.id.image_popwindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader){
        if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
            // 获取长度
            int len = output.length();
            // 获取图片地址
            ImageSpan[] images = output.getSpans(len-1, len, ImageSpan.class);
            String imgURL = images[0].getSource();

            // 设置图片可点击并监听点击事件
            output.setSpan(new ClickableImage(myContext, imgURL), len-1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private class ClickableImage extends ClickableSpan {

        private String path;
        private Context context;

        public ClickableImage(Context context, String path) {
            this.context = context;
            this.path = path;
        }

        @Override
        public void onClick(View widget) {
            Log.i("clickImagePath", path);
            popupWindow.setAnimationStyle(R.style.pop_animation);
            popupWindow.showAtLocation(widget, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            Drawable d = Drawable.createFromPath(path);
            imageView.setImageDrawable(d);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }

}
