package org.android.edlo.ble_weight_scale;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.android.edlo.ble_weight_scale.java_class.Data.UserDAO;
import org.android.edlo.ble_weight_scale.java_class.Data.UserItem;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private Button fbLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private UserItem userItem;
    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    private Timer timerExit;
    private TimerTask task;
    protected UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        //隱藏標題
        getSupportActionBar().hide();
        //隱藏狀態
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED ) {
            Log.i("ed43", "B");
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1);
        }else {
            Log.i("ed43", "A");
            init();
        }

        //FB登入
        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (Button) findViewById(R.id.login_button);

        fbLoginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends"));
            }
        });

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();

                Log.d("ble_weight_scale","access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                //讀出姓名 ID FB個人頁面連結
                                String fb_id = object.optString("id");
                                userItem = userDAO.getByFBId(fb_id);
                                if(userItem == null){
                                    Log.d("FB Login Test","no useritem");
                                    userItem = new UserItem();
                                }else {
                                    Log.d("FB Login Test","got useritem");
                                }
                                userItem.setFb_id(Long.parseLong(fb_id));
                                Intent lastWeightIntent = new Intent(getApplicationContext(), LastWeightActivity.class);
                                lastWeightIntent.putExtra("user", userItem);
                                Log.d("FB Login Test", "FB ID " + userItem.getFb_id());
                                getApplicationContext().startActivity(lastWeightIntent);
                                //Log.d("FB Test", "FB ID " + userItem.getFb_id());
                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
                //Log.i("FB Test","loginResult : " + loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("FB Test","CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FB Test",exception.toString());
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        //init
        userDAO = new UserDAO(getApplicationContext());
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("FB Test", "resultCode : "+ resultCode);
//        Log.i("FB Test", "data : " + data);
    }

    private void init(){
        //test
        SharedPreferences preference = getApplicationContext().getSharedPreferences("",0);

        //List<UserItem> userItems = userDAO.getAll();
        //Log.d("DB_Test", new Gson().toJson(userItems));

        //edit_text init
        userItem = (UserItem) getIntent().getSerializableExtra("user");
        if(userItem != null){
            EditText login_email = (EditText)findViewById(R.id.login_email);
            EditText login_password = (EditText)findViewById(R.id.login_password);
            login_email.setText(userItem.getEmail());
            login_password.setText(userItem.getPassword());
        }

        //init exit app
        timerExit = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };
    }

    //登入
    public void signIn(View view){
        EditText login_email = (EditText) findViewById(R.id.login_email);
        EditText login_password = (EditText) findViewById(R.id.login_password);
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();
        //confirm email & password
        if(email.length() > 0 && password.length() > 0){
            //confirm user
            if(confirm_user(email, password)){
                //Log.i("ble_weight_scale", "Login Email : " + email);
                Intent signInIntent = new Intent(this, LastWeightActivity.class);
                if(userItem == null){
                    userItem = (UserItem) getIntent().getSerializableExtra("user");
                    Log.i("DBTest", "MainActivity null : "+new Gson().toJson(userItem));
                }
                if(userItem != null){
                    signInIntent.putExtra("user", userItem);
                    Log.i("DBTest", "MainActivity not null : "+new Gson().toJson(userItem));
                }


                startActivity(signInIntent);
            }else{
                Toast.makeText(this, "Incorrect Email or Password !", Toast.LENGTH_SHORT).show();
            }
        }else if(email.length() == 0){
            Toast.makeText(this, "Please Enter Email !", Toast.LENGTH_SHORT).show();
        }else if(email.length() > 0 && password.length() == 0){
            Toast.makeText(this, "Please Enter Password !", Toast.LENGTH_SHORT).show();
        }

    }

    //註冊
    public void register(View view){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    //記住我
    public void rememberMe(View view){
        CheckBox rememberCheckBox = (CheckBox) view;
        boolean checked = rememberCheckBox.isChecked();
        Log.i("BLE_Weight_Scale", "IsChecked : " + checked);
    }

    //fb登入
    public void fb_login(View view){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        Log.i("ble_weight_scale", "ok");
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "Destroy");
        super.onDestroy();
        accessTokenTracker.stopTracking();
        LoginManager.getInstance().logOut();
    }

    //離開app
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判斷是否按下Back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if(isExit == false ) {
                isExit = true;
                //Log.d("Exit_app", "Stay");
                Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
                if(!hasTask) {
                    this.timerExit.schedule(task, 2000);
                }
            } else {
                //Log.d("Exit_app", "Leave");
                accessTokenTracker.stopTracking();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }
        return false;
    }

    //忘記密碼
    public void forgot_password(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://mycloudfitness.com/forgetpassword/"));
        startActivity(intent);
    }

    //驗證User
    private boolean confirm_user(String email, String password){
        userItem = userDAO.get(email);
        if(userItem != null){
            String user_password = userItem.getPassword();
            Log.d("USER_TEST", "user_password : " + user_password);
            Log.d("USER_TEST", "password : " + password);
            if(user_password.equals(password)){
                Log.d("USER_TEST", "OOOOOOO");
                return true;
            }else {
                Log.d("USER_TEST", "XXXXXX");
                return false;
            }
        }else {
            Log.d("USER_TEST", "GGGGG");
            return false;
        }
    }
}
