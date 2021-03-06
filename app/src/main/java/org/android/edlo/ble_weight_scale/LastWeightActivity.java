package org.android.edlo.ble_weight_scale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;

import org.android.edlo.ble_weight_scale.java_class.Data.UserDAO;
import org.android.edlo.ble_weight_scale.java_class.Data.UserItem;

public class LastWeightActivity extends AppCompatActivity {
    private int CONNECT_STATE;
    private UserDAO userDAO;
    private UserItem userItem;
    private Button weight_btn, graph_btn, hist_btn, signout_btn, myProfile_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_last_weight);
        init();
    }

    private void init(){
        userItem = (UserItem) getIntent().getSerializableExtra("user");
        if(userItem != null && userItem.getEmail() == null){
            disableBtn();
        }
    }

    private void disableBtn() {
        weight_btn = findViewById(R.id.weight_btn);
        graph_btn = findViewById(R.id.graph_btn);
        hist_btn = findViewById(R.id.hist_btn);
        signout_btn = findViewById(R.id.signOut_btn);

        weight_btn.setClickable(false);
        graph_btn.setClickable(false);
        hist_btn.setClickable(false);
        signout_btn.setClickable(false);
    }

    public void myProfile(View view){
        Intent myProfileIntent = new Intent(this, MyProfileActivity.class);
        if(userItem != null){
            Log.d("FB_Test", "user fb id : "+userItem.getFb_id());
            myProfileIntent.putExtra("user",userItem);
        }
        startActivity(myProfileIntent);
    }

    public void scale(View view){
        CONNECT_STATE = 0;
        Intent scaleIntent;
        if(CONNECT_STATE == R.integer.CONNECTION_SUCCESS){
            scaleIntent = new Intent(this, ResultActivity.class);
        }else  if(CONNECT_STATE == R.integer.CONNECTION_DEVICE_NOT_FOUND){
            scaleIntent = new Intent(this, ErrorActivity.class);
        }else if (CONNECT_STATE == R.integer.CONNECTION_LOST){
            scaleIntent = new Intent(this, ErrorActivity.class);
        } else{
            scaleIntent = new Intent(this, ResultActivity.class);
        }
        startActivity(scaleIntent);
    }

    public void graph(View view){
        Intent intent = new Intent(this, GraphActivity.class);
        if(userItem != null){
            intent.putExtra("user", userItem);
        }
        startActivity(intent);
    }

    public void signOut(View view){
        Intent intent = new Intent(this, MainActivity.class);
        userItem = null;
        userDAO = null;
        LoginManager.getInstance().logOut();
        startActivity(intent);
    }
}
