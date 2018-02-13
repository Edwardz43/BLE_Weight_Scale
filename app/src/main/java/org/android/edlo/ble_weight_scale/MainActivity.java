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

import org.android.edlo.ble_weight_scale.java_class.Data.UserItem;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_main);

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

        timerExit = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };
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

    public void signIn(View view){
        EditText login_email = (EditText) findViewById(R.id.login_email);
        EditText login_password = (EditText) findViewById(R.id.login_password);
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();
        //confirm email
//        if(email != null && email.length() > 0){
//            Log.i("ble_weight_scale", "OK");
//        }else {
//            Toast.makeText(this, "Email is Invalid !", Toast.LENGTH_SHORT).show();
//            Log.i("ble_weight_scale", "NO");
//        }

        Log.i("ble_weight_scale", "Login Email : " + email);
        Intent signInIntent = new Intent(this, LastWeightActivity.class);
        startActivity(signInIntent);
    }

    public void register(View view){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void rememberMe(View view){
        CheckBox rememberCheckBox = (CheckBox) view;
        boolean checked = rememberCheckBox.isChecked();
        Log.i("BLE_Weight_Scale", "IsChecked : " + checked);
    }

    public void test(View view){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        Log.i("ble_weight_scale", "ok");
    }

    @Override
    public void onDestroy() {
        Log.i("ble_weight_scale", "Destroy");
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 判斷是否按下Back
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否要退出
            if(isExit == false ) {
                isExit = true;

                Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
                if(!hasTask) {
                    this.timerExit.schedule(task, 2000);
                }
            } else {
                accessTokenTracker.stopTracking();
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    public void forgot_password(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://mycloudfitness.com/forgetpassword/"));
        startActivity(intent);
    }
}
