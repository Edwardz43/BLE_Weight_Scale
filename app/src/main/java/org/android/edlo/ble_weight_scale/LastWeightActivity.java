package org.android.edlo.ble_weight_scale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.android.edlo.ble_weight_scale.java_class.Resource.ResourceMapping;

public class LastWeightActivity extends AppCompatActivity {
    private int CONNECT_STATE;
    private ResourceMapping resourceMapping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_last_weight);
        resourceMapping = new ResourceMapping();
    }

    public void myProfile(View view){
        Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
        startActivity(myProfileIntent);
    }

    public void scale(View view){
        CONNECT_STATE = 0;
        Intent scaleIntent;
        if(CONNECT_STATE == resourceMapping.CONNECTION_SUCCESS){
            scaleIntent = new Intent(this, ResultActivity.class);
        }else  if(CONNECT_STATE == resourceMapping.CONNECTION_DEVICE_NOT_FOUND){
            scaleIntent = new Intent(this, ErrorActivity.class);
        }else if (CONNECT_STATE == resourceMapping.CONNECTION_LOST){
            scaleIntent = new Intent(this, ErrorActivity.class);
        } else{
            scaleIntent = new Intent(this, ResultActivity.class);
        }
        startActivity(scaleIntent);
    }

    public void graph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void signOut(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
