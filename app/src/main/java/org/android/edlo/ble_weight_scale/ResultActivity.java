package org.android.edlo.ble_weight_scale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
       //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_result);
    }

    public void save(View view){
        Intent it = new Intent(this, LastWeightActivity.class);
        startActivity(it);
    }

    public void discard(View view){
        Intent it = new Intent(this, LastWeightActivity.class);
        startActivity(it);
    }
}
