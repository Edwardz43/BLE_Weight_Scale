package org.android.edlo.ble_weight_scale.java_class.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by EdLo on 2018/2/2.
 */

public class ScaleRecordDAO extends DAO{
    // 表格名稱
    public static final String TABLE_NAME = "scale_record";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String DATETIME_COLUMN = "datetime";
    public static final String DATE_COLUMN = "date";
    public static final String YEAR_COLUMN = "year";
    public static final String MONTH_COLUMN = "month";
    public static final String DAY_COLUMN = "day";
    public static final String TIME_COLUMN = "time";
    public static final String WEIGHT_COLUMN = "weight";
    public static final String USER_ID = "user_id";

    private final int YEAR_DATA = 0;
    private final int MONTH_DATA = 1;
    private final int WEEK_DATA = 2;
    private final int DAY_DATA = 3;

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    DATE_COLUMN + " TEXT NOT NULL, " +
                    YEAR_COLUMN + " TEXT NOT NULL, " +
                    MONTH_COLUMN + " TEXT NOT NULL, " +
                    DAY_COLUMN + " TEXT NOT NULL, " +
                    TIME_COLUMN + " TEXT NOT NULL, " +
                    WEIGHT_COLUMN + " REAL NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL)";
    // 資料庫物件
    private SQLiteDatabase db;

    public ScaleRecordDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public RecordItem insert(RecordItem item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        cv.put(DATETIME_COLUMN, item.getDateTime());
        cv.put(DATE_COLUMN, item.getLocaleDate());
        cv.put(YEAR_COLUMN, item.getYear());
        cv.put(MONTH_COLUMN, item.getMonth());
        cv.put(DAY_COLUMN, item.getDayOfMonth());
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
    public boolean update(RecordItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        cv.put(DATETIME_COLUMN, item.getDateTime());
        cv.put(DATE_COLUMN, item.getLocaleDate());
        cv.put(YEAR_COLUMN, item.getYear());
        cv.put(MONTH_COLUMN, item.getMonth());
        cv.put(DAY_COLUMN, item.getDayOfMonth());
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
    public List<RecordItem> getAll() {
        List<RecordItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 讀取最新一筆資料(Last Weight)
    public RecordItem getLastWeight(long id) {
        RecordItem item = new RecordItem();
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
        while (cursor.moveToLast()) {
            item = getRecord(cursor);
        }

        cursor.close();
        return item;
    }

    // 依據查詢條件讀取資料 (年, 月, 周, 日)
    public List<RecordItem> getWeightByCondition(long id, int condition) {
        //String where = null;
        String orderBy = null;
        String limit = null;
        switch (condition){
            case YEAR_DATA:
                break;
            case MONTH_DATA:
                break;
            case WEEK_DATA:
                break;
            case DAY_DATA:
                orderBy = KEY_ID;
                limit = "7";
                break;
        }
        List<RecordItem> result = new ArrayList<>();
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, orderBy, limit);
        while (cursor.moveToLast()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public RecordItem get(long id) {
        // 準備回傳結果用的物件
        RecordItem item = null;
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
    public RecordItem getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        RecordItem result = new RecordItem();

        result.setId(cursor.getLong(0));
        result.setDateTime(cursor.getLong(1));
        result.setLocalDate(cursor.getString(2));
        result.setYear(cursor.getInt(3));
        result.setMonth(cursor.getInt(4));
        result.setDayOfMonth(cursor.getInt(5));
        result.setWeight(cursor.getInt(6));
        result.setWeight(cursor.getDouble(7));
        result.setUserId(cursor.getLong(8));

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

    // 建立範例資料
    public void sample() {
        RecordItem item =
                new RecordItem(0, 2017,12,30,12,0,0, 155.3, 1l);
        RecordItem item2 =
                new RecordItem(0, 2017,12,31,12,0,0, 0, 1l);
        RecordItem item3 =
                new RecordItem(0, 2018,1,1,12,0,0, 155.2, 1l);
        RecordItem item4 =
                new RecordItem(0, 2018,1,2,12,0,0, 155.3, 1l);
        RecordItem item5 =
                new RecordItem(0, 2018,1,3,12,0,0, 0, 1l);
        RecordItem item6 =
                new RecordItem(0, 2018,1,4,12,0,0, 155, 1l);
        RecordItem item7 =
                new RecordItem(0, 2018,1,5,12,0,0, 155.5, 1l);
        RecordItem item8 =
                new RecordItem(0, 2018,1,6,12,0,0, 0, 1l);
        RecordItem item9 =
                new RecordItem(0, 2018,1,7,12,0,0, 156.5, 1l);
        RecordItem item10 =
                new RecordItem(0, 2018,1,8,12,0,0, 156.3, 1l);
        RecordItem item11 =
                new RecordItem(0, 2018,1,9,12,0,0, 156.4, 1l);
        RecordItem item12 =
                new RecordItem(0, 2018,1,10,12,0,0, 157.1, 1l);
        RecordItem item13 =
                new RecordItem(0, 2018,1,11,12,0,0, 156.9, 1l);
        RecordItem item14 =
                new RecordItem(0, 2018,1,12,12,0,0, 156.3, 1l);
        RecordItem item15 =
                new RecordItem(0, 2018,1,13,12,0,0, 156.5, 1l);
        RecordItem item16 =
                new RecordItem(0, 2018,1,14,12,0,0, 156.7, 1l);
        RecordItem item17 =
                new RecordItem(0, 2018,1,15,12,0,0, 157.1, 1l);
        RecordItem item18 =
                new RecordItem(0, 2018,1,16,12,0,0, 157.3, 1l);
        RecordItem item19 =
                new RecordItem(0, 2018,1,17,12,0,0, 157.9, 1l);
        RecordItem item20 =
                new RecordItem(0, 2018,1,18,12,0,0, 157.6, 1l);
        RecordItem item21 =
                new RecordItem(0, 2018,1,19,12,0,0, 157.5, 1l);
        RecordItem item22 =
                new RecordItem(0, 2018,1,20,12,5,0, 157.3, 1l);
        RecordItem item23 =
                new RecordItem(0, 2018,1,21,12,5,0, 157.6, 1l);
        RecordItem item24 =
                new RecordItem(0, 2018,1,22,12,5,0, 157.8, 1l);
        RecordItem item25 =
                new RecordItem(0, 2018,1,23,12,0,0, 158.1, 1l);
        RecordItem item26 =
                new RecordItem(0, 2018,1,24,12,0,0, 158.3, 1l);
        RecordItem item27 =
                new RecordItem(0, 2018,1,25,13,0,0, 158.5, 1l);
        RecordItem item28 =
                new RecordItem(0, 2018,1,25,15,30,0, 158.4, 1l);
        RecordItem item29 =
                new RecordItem(0, 2018,1,25,19,30,0, 159, 1l);
        RecordItem item30 =
                new RecordItem(0, 2018,1,26,12,5,0, 159.5, 1l);

        insert(item);
        insert(item2);
        insert(item3);
        insert(item4);
        insert(item5);
        insert(item6);
        insert(item7);
        insert(item8);
        insert(item9);
        insert(item10);
        insert(item11);
        insert(item12);
        insert(item13);
        insert(item14);
        insert(item15);
        insert(item16);
        insert(item17);
        insert(item18);
        insert(item19);
        insert(item20);
        insert(item21);
        insert(item22);
        insert(item23);
        insert(item24);
        insert(item25);
        insert(item26);
        insert(item27);
        insert(item28);
        insert(item29);
        insert(item30);
    }
}
