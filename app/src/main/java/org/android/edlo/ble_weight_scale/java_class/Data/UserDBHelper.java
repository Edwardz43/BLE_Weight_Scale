package org.android.edlo.ble_weight_scale.java_class.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by EdLo on 2018/2/15.
 */

public class UserDBHelper extends SQLiteOpenHelper {

    // 資料庫名稱
    public static final String DATABASE_NAME = "mCloudFitness.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    public UserDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            Log.i("DBTest", "database == null");
            database = new UserDBHelper(context, DATABASE_NAME,null, VERSION).getWritableDatabase();
        }
        Log.i("DBTest", "database:" + database);
        Log.i("DBTest", "Create Table");
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 建立應用程式需要的表格
        Log.i("DBTest", "Create Table");
        sqLiteDatabase.execSQL(UserDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 刪除原有的表格
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(sqLiteDatabase);
    }
}
