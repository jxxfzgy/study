package com.gyzhong.toolbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private EditText mEditText ;
    private Toolbar mToolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(mToolbar);
        mToolbar.setVisibility(View.GONE);
        mToolbar = (Toolbar) findViewById(R.id.id_toolBar);

    }


}
