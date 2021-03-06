package com.homework.notes.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.homework.notes.persistence.datastructure.ClassItems;

import java.util.ArrayList;

public class ClassDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    private static final String DATABASE_NAME = "notes.db";

    public ClassDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertClass(String class_name)
    {
        this.open();
        Cursor cursor = database.rawQuery("select * from " + SQLiteHelper.TABLE_CLASS + " where " + SQLiteHelper.COLUMN_CLASS_NAME + " = ?",new String[]{class_name});
        // unique class_name
        if(cursor.getCount() > 0) {
            this.close();
            return -1;
        }

        ContentValues insertValues = new ContentValues();
        insertValues.put(SQLiteHelper.COLUMN_CLASS_NAME, class_name);
        int class_order = database.rawQuery("select * from "+SQLiteHelper.TABLE_CLASS,null).getCount();
        insertValues.put(SQLiteHelper.COLUMN_CLASS_ORDER, class_order);
        insertValues.put(SQLiteHelper.COLUMN_NOTES_NUM, 0);
        // ADD: class review time
        insertValues.put(SQLiteHelper.COLUMN_CLASS_REVIEW_TIME, 0);
        long val = database.insert(SQLiteHelper.TABLE_CLASS, null, insertValues);
        this.close();
        return val;
    }
    public long deleteOne(String class_name)
    {
        this.open();
        database.delete(SQLiteHelper.TABLE_CLASS, SQLiteHelper.COLUMN_CLASS_NAME + " = ?", new String[] { class_name });
        this.close();
        return 0;
    }
    public long incrementNotesNum(String class_name)
    {
        this.open();
        String sql = "UPDATE " + SQLiteHelper.TABLE_CLASS +
                " SET " + SQLiteHelper.COLUMN_NOTES_NUM + "=" + SQLiteHelper.COLUMN_NOTES_NUM + "+1" +
                " WHERE " + SQLiteHelper.COLUMN_CLASS_NAME + " = '" + class_name+"'";

        database.execSQL(sql);
        this.close();
        return 0;

    }

    public long decrementNotesNum(String class_name)
    {
        this.open();
        String sql = "UPDATE " + SQLiteHelper.TABLE_CLASS +
                " SET " + SQLiteHelper.COLUMN_NOTES_NUM + "=" + SQLiteHelper.COLUMN_NOTES_NUM + "-1" +
                " WHERE " + SQLiteHelper.COLUMN_CLASS_NAME + " = '" + class_name+"'";

        database.execSQL(sql);
        this.close();
        return 0;

    }

    public void updateClassReviewTime(long incre_review_time, String class_name) {
        this.open();

        String sql = "UPDATE " + SQLiteHelper.TABLE_CLASS +
                " SET " + SQLiteHelper.COLUMN_CLASS_REVIEW_TIME + "=" + SQLiteHelper.COLUMN_CLASS_REVIEW_TIME + "+" + incre_review_time +
                " WHERE " + SQLiteHelper.COLUMN_CLASS_NAME + " = '" + class_name+"'";

        database.execSQL(sql);

        this.close();
    }

    public ArrayList<ClassItems> getAllClass() {
        this.open();
        ArrayList<ClassItems> items = new ArrayList<ClassItems>();
        Cursor cursor = database.rawQuery("select * from "+SQLiteHelper.TABLE_CLASS+" order by class_order asc",null);

        if (cursor .moveToFirst()) {

            while (!cursor.isAfterLast()) {
                String class_name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CLASS_NAME));
                int notes_num = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_NOTES_NUM));
                // ADD: class review time
                String class_review_time = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CLASS_REVIEW_TIME));
                ClassItems item = new ClassItems(class_name,notes_num,class_review_time);
                items.add(item);
                cursor.moveToNext();
            }
        }
        this.close();
        return items;
    }

}
