package org.android.edlo.ble_weight_scale;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_error);
    }
}
