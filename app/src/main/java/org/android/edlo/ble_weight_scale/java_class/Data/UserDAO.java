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
    public static final String DATETIME_COLUMN = "datetime";
    public static final String DATE_COLUMN = "date";
    public static final String TIME_COLUMN = "time";
    public static final String WEIGHT_COLUMN = "weight";
    public static final String USER_ID = "user_id";
    public static final String IS_LOGIN = "is_login";

    // 建立表格
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    DATE_COLUMN + " TEXT NOT NULL, " +
                    TIME_COLUMN + " TEXT NOT NULL, " +
                    WEIGHT_COLUMN + " REAL NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, "+
                    IS_LOGIN + " INTEGER NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public UserDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Item insert(Item item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        cv.put(DATETIME_COLUMN, item.getLocaleDatetime());
        cv.put(DATE_COLUMN, item.getLocaleDate());
        cv.put(TIME_COLUMN, item.getLocaleTime());
        cv.put(WEIGHT_COLUMN, item.getWeight());
        cv.put(USER_ID, item.getUserId());


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
    public boolean update(Item item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        cv.put(DATETIME_COLUMN, item.getLocaleDatetime());
        cv.put(DATE_COLUMN, item.getLocaleDate());
        cv.put(TIME_COLUMN, item.getLocaleTime());
        cv.put(WEIGHT_COLUMN, item.getWeight());
        cv.put(USER_ID, item.getUserId());

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
    public List<Item> getAll() {
        List<Item> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 讀取最新一筆資料(Last Weught)
    public Item getLastWeight() {
        Item result = new Item();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);
        while (cursor.moveToLast()) {
            result = getRecord(cursor);
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Item get(long id) {
        // 準備回傳結果用的物件
        Item item = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
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
    public Item getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Item result = new Item();

        result.setId(cursor.getLong(0));
        result.setDateTime(cursor.getLong(1));
        result.setWeight(cursor.getDouble(2));
        result.setUserId(cursor.getLong(3));

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
