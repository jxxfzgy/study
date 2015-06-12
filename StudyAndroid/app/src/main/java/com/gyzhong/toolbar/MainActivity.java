package com.gyzhong.toolbar;

import android.annotation.TargetApi;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private EditText mEditText ;
    private Toolbar mToolbar ;
    private ImageView mImageView ;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(mToolbar);
//        mToolbar.setVisibility(View.GONE);
        mToolbar = (Toolbar) findViewById(R.id.id_toolBar);
        ImageView imageView = (ImageView) findViewById(R.id.id_img_show);
        mImageView = (ImageView) findViewById(R.id.id_anim);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim() ;
            }
        });
        try {
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(),getResources().getAssets().open("bg.jpg"));
            drawable.setCornerRadius(drawable.getIntrinsicHeight());
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void startAnim() {
        TranslateAnimation animation = new TranslateAnimation(0,300,0,0) ;
        animation.setDuration(2000);
        Path path = new Path() ;
        path.moveTo(0,0);
        path.lineTo(0.4f, 0.4f);
        path.lineTo(0.5f, 0.5f);
        path.lineTo(1.0f, 1.0f);
        path.lineTo(0.9f, 0.9f);
        path.lineTo(1.0f, 1.0f);
        path.lineTo(0.5f, 0.5f);
        path.lineTo(1.0f, 1.0f);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearOutSlowInInterpolator());
        mImageView.startAnimation(animation);
    }


}
