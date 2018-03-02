package org.android.edlo.ble_weight_scale;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.android.edlo.ble_weight_scale.java_class.Algorithm;
import org.android.edlo.ble_weight_scale.java_class.Data.UserDAO;
import org.android.edlo.ble_weight_scale.java_class.Data.UserItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean isMale, isImperial;
    private Button male, female, imperial, metric;
    private String email, password, confirmPassword, firstname, lastName,
                    birthdate, gender, height_in, height_ft, height_cm, weight_lb, weight_kg;
    private Integer unit_type, activity_level;
    private UserDAO userDAO;
    private UserItem userItem;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); //隱藏狀態
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        res = getResources();
        userDAO = new UserDAO(getApplicationContext());
        userItem = new UserItem();

        //預設性別 : 男性
        isMale = true;
        this.gender = res.getString(R.string.gender_male);
        setSex();

        //預設單位 : 公制
        isImperial = true;
        this.unit_type = res.getInteger(R.integer.IMPERIAL);
        setUnit();

        //初始化下拉選單
        init_spinner();
    }

    public void setMale(View view){
        isMale = true;
        this.gender = "male";
        setSex();
    }

    public void setFemale(View view){
        isMale = false;
        this.gender = "female";
        setSex();
    }

    private void setSex(){
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        if(isMale){
            male.setBackgroundResource(R.color.colorSelectedButton);
            female.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            female.setBackgroundResource(R.color.colorSelectedButton);
            male.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    public void setImperialUnit(View view){
        isImperial = true;
        this.unit_type = res.getInteger(R.integer.IMPERIAL);
        setUnit();
    }

    public void setMetricUnit(View view){
        isImperial = false;
        this.unit_type = res.getInteger(R.integer.METRIC);
        setUnit();
    }

    private void setUnit(){
        imperial = findViewById(R.id.imperial_btn);
        metric = findViewById(R.id.metric_btn);
        LinearLayout unitImperialLayout = findViewById(R.id.imperialUnitLayout);
        LinearLayout unitMetricLayout = findViewById(R.id.metricUnitLayout);
        if(isImperial){
            unitImperialLayout.setVisibility(View.VISIBLE);
            unitMetricLayout.setVisibility(View.GONE);
            imperial.setBackgroundResource(R.color.colorSelectedButton);
            metric.setBackgroundResource(R.color.colorUnselectedButton);
        }else {
            unitImperialLayout.setVisibility(View.GONE);
            unitMetricLayout.setVisibility(View.VISIBLE);
            metric.setBackgroundResource(R.color.colorSelectedButton);
            imperial.setBackgroundResource(R.color.colorUnselectedButton);
        }
    }

    public void back(View view){
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    public void register(View view){
        this.email = editableToString(findViewById(R.id.user_email));
        this.firstname = editableToString(findViewById(R.id.user_firstName));
        this.lastName = editableToString(findViewById(R.id.user_lastName));
        this.password = editableToString(findViewById(R.id.user_password));
        this.confirmPassword = editableToString(findViewById(R.id.confirm_password));
        this.birthdate = editableToString(findViewById(R.id.user_birthDate));

        // 身高公制/英制顯示的轉換
        // 英制
        if(this.unit_type == res.getInteger(R.integer.IMPERIAL)){
            this.height_in = editableToString(findViewById(R.id.height_in));
            this.height_ft = editableToString(findViewById(R.id.height_ft));
            this.height_cm = Algorithm.imperialToMetric(height_ft, height_in);
        // 公制
        }else if(this.unit_type == res.getInteger(R.integer.METRIC)){
            this.height_cm = editableToString(findViewById(R.id.height_cm));
            String[] result = Algorithm.metricToImperial(this.height_cm);
            this.height_ft = result[0];
            this.height_in = result[1];
        }

        userItem.setEmail(this.email);
        userItem.setPassword(this.password);
        userItem.setFisrtname(this.firstname);
        userItem.setLastname(this.lastName);
        userItem.setBirthdate(this.birthdate);
        userItem.setGender(this.gender);
        userItem.setHeight_ft(this.height_ft);
        userItem.setHeight_in(this.height_in);
        userItem.setHeight_cm(this.height_cm);
        userItem.setWeight_lb(this.weight_lb);
        userItem.setWeight_kg(this.weight_kg);
        userItem.setUnit_type(this.unit_type);
        userItem.setActivity_level(this.activity_level);
        userDAO.insert(userItem);
        Log.i("DBTest", "Register : "+new Gson().toJson(userItem));

        Intent it = new Intent(this, MainActivity.class);
        it.putExtra("user", userItem);
        startActivity(it);
    }

    //下拉式選單
    private void init_spinner(){
        Spinner spinner = findViewById(R.id.activity_level);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setPrompt("Activity Level");
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        //Log.i("ble_weight_scale", "spinner select item : " + position);
        String selectedItemText = (String) parent.getItemAtPosition(position);
        // If user change the default selection
        // First item is disable and it is used for hint
        if (position > 0) {
            // Notify the selected item text
            this.activity_level = position;
            Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //驗證email by regex
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private String editableToString(View view){
        return ((EditText)view).getText().toString();
    }
}
