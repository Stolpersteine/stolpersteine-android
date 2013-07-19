package com.dreiri.stolpersteine.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;


public class UIEnhancer {

    public static void insertImageIntoTextView(Activity activity, int imageResource, int textViewResource) {
        TextView textView = (TextView) activity.findViewById(textViewResource);
        CharSequence text = " " + textView.getText();
        addImageIntoTextView(activity, imageResource, textView, text, 0, 1);
    }
    
    public static void appendImageIntoTextView(Activity activity, int imageResource, int textViewResource) {
        TextView textView = (TextView) activity.findViewById(textViewResource);
        CharSequence text = textView.getText() + " ";
        addImageIntoTextView(activity, imageResource, textView, text, text.length() - 1, text.length());
    }
    
    private static void addImageIntoTextView(Activity activity, int imageResource, TextView textView, CharSequence text, int start, int end) {
        SpannableString spannableString = new SpannableString(text);
        Drawable image = activity.getResources().getDrawable(imageResource);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        spannableString.setSpan(new ImageSpan(image, ImageSpan.ALIGN_BASELINE), start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

}
