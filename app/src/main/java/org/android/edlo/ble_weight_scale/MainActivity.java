package org.android.edlo.ble_weight_scale;

import android.content.Intent;
import android.net.Uri;
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

import org.android.edlo.ble_weight_scale.java_class.Data.UserDAO;
import org.android.edlo.ble_weight_scale.java_class.Data.UserItem;
import org.json.JSONObject;

import java.util.Arrays;
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
                                UserItem userItem = new UserItem();
                                userItem.setFisrtname(object.optString("name"));

                            }
                        });

                //包入你想要得到的資料 送出request
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
                Log.i("FB","loginResult : " + loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("FB","CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FB",exception.toString());
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
        Log.i("FB", "resultCode : "+ resultCode);
        Log.i("FB", "data : " + data);
        if(resultCode != 0){
            Intent intent = new Intent(this, LastWeightActivity.class);
            startActivity(intent);
        }
    }

    private void init(){
        //edit_text init
        UserItem userItem = (UserItem) getIntent().getSerializableExtra("user");
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
                Log.i("ble_weight_scale", "Login Email : " + email);
                Intent signInIntent = new Intent(this, LastWeightActivity.class);
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
        UserItem item = userDAO.get(email);
        if(item != null){
            String user_password = item.getPassword();
            Log.d("USER_TEST",item.toString());
            if(user_password == password){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}
