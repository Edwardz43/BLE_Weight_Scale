package org.android.edlo.ble_weight_scale.java_class.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EdLo on 2018/2/8.
 */

public class UserDAO {
    // 表格名稱
    public static final String TABLE_NAME = "users";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "uid";

    // 其它表格欄位名稱
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String BIRTH_DATE = "birth_date";
    public static final String HEIGHT_IN = "height_in";
    public static final String HEIGHT_FT = "height_ft";
    public static final String HEIGHT_CM = "height_cm";
    public static final String WEIGHT_LB = "weight_lb";
    public static final String WEIGHT_KG = "height_kg";
    public static final String GENDER = "gender";
    public static final String UNIT_TYPE = "unit_type";
    public static final String ACTIVITY_LEVEL = "activity_level";

    // 建立表格
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EMAIL + " TEXT NOT NULL, " +
                    PASSWORD + " TEXT NOT NULL, " +
                    FIRST_NAME + " TEXT NOT NULL, " +
                    LAST_NAME + " TEXT NOT NULL, " +
                    BIRTH_DATE + " TEXT NOT NULL, "+
                    HEIGHT_IN + " REAL , " +
                    HEIGHT_FT + " INTEGER , " +
                    HEIGHT_CM + " REAL , " +
                    WEIGHT_LB + " REAL , " +
                    WEIGHT_KG + " REAL , "+
                    GENDER + " TEXT NOT NULL, "+
                    UNIT_TYPE + " INTEGER NOT NULL, "+
                    ACTIVITY_LEVEL + " INTEGER NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        db = UserDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public UserItem insert(UserItem item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        cv.put(EMAIL, item.getEmail());
        cv.put(PASSWORD, item.getPassword());
        cv.put(FIRST_NAME, item.getFisrtname());
        cv.put(LAST_NAME, item.getLastname());
        cv.put(BIRTH_DATE, item.getBirthdate());
        cv.put(ACTIVITY_LEVEL, item.getActivity_level());
        cv.put(GENDER, item.getGender());
        cv.put(UNIT_TYPE, item.getUnit_type());

        if(item.getHeight_in()!= null){cv.put(HEIGHT_IN, item.getHeight_in());}
        if(item.getHeight_ft()!= null){cv.put(HEIGHT_IN, item.getHeight_ft());}
        if(item.getHeight_cm()!= null){cv.put(HEIGHT_IN, item.getHeight_cm());}
        if(item.getWeight_lb()!= null){cv.put(HEIGHT_IN, item.getWeight_lb());}
        if(item.getWeight_kg()!= null){cv.put(HEIGHT_IN, item.getWeight_kg());}

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        item.setId(id);
        // 回傳結果
        return item;
    }

    // 修改參數指定的物件
    public boolean update(UserItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        cv.put(EMAIL, item.getEmail());
        cv.put(PASSWORD, item.getPassword());
        cv.put(FIRST_NAME, item.getFisrtname());
        cv.put(LAST_NAME, item.getLastname());
        cv.put(BIRTH_DATE, item.getBirthdate());
        cv.put(ACTIVITY_LEVEL, item.getActivity_level());

        if(item.getHeight_in()!= null){cv.put(HEIGHT_IN, item.getHeight_in());}
        if(item.getHeight_ft()!= null){cv.put(HEIGHT_IN, item.getHeight_ft());}
        if(item.getHeight_cm()!= null){cv.put(HEIGHT_IN, item.getHeight_cm());}
        if(item.getWeight_lb()!= null){cv.put(HEIGHT_IN, item.getWeight_lb());}
        if(item.getWeight_kg()!= null){cv.put(HEIGHT_IN, item.getWeight_kg());}

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 讀取所有資料
    public List<UserItem> getAll() {
        List<UserItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }

    // 取得指定編號的User
    public UserItem get(String email) {
        // 準備回傳結果用的物件
        UserItem item = null;
        // 使用編號為查詢條件
        String where = EMAIL + "='" + email+"'";
        // 執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            item = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return item;
    }

    // 把Cursor目前的資料包裝為物件
    public UserItem getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        UserItem result = new UserItem();

        result.setId(cursor.getLong(0));
        result.setEmail(cursor.getString(1));
        result.setPassword(cursor.getString(2));
        result.setFisrtname(cursor.getString(3));
        result.setLastname(cursor.getString(4));
        result.setBirthdate(cursor.getString(5));
        result.setHeight_in(cursor.getString(6));
        result.setHeight_ft(cursor.getString(7));
        result.setHeight_cm(cursor.getString(8));
        result.setWeight_lb(cursor.getString(9));
        result.setWeight_kg(cursor.getString(10));
        result.setGender(cursor.getString(11));
        result.setUnit_type(cursor.getInt(12));
        result.setActivity_level(cursor.getInt(13));

        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }
}
