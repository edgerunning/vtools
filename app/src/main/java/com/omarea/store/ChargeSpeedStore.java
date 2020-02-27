package com.omarea.store;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.omarea.model.ChargeSpeedHistory;
import com.omarea.model.ChargeTimeHistory;

import java.util.ArrayList;

public class ChargeSpeedStore extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    public ChargeSpeedStore(Context context) {
        super(context, "charge_history2", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table charge_history(" +
                    "id INTEGER primary key AUTOINCREMENT, " +
                    "time INTEGER, " +
                    "io INTEGER, " +
                    "capacity INTEGER, " +
                    "temperature REAL" +
                    ")");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<ChargeSpeedHistory> statistics() {
        ArrayList<ChargeSpeedHistory> histories = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final Cursor cursor = sqLiteDatabase.rawQuery("select capacity, avg(io) as io from charge_history group by capacity", new String[]{});
            while (cursor.moveToNext()) {
                histories.add(new ChargeSpeedHistory() {{
                    capacity = cursor.getInt(cursor.getColumnIndex("capacity"));
                    io = cursor.getLong(cursor.getColumnIndex("io"));
                }});
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception ignored) {

        }
        return histories;
    }

    public ArrayList<ChargeSpeedHistory> getTemperature() {
        ArrayList<ChargeSpeedHistory> histories = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final Cursor cursor = sqLiteDatabase.rawQuery("select capacity, max(temperature) as temperature from charge_history group by capacity", new String[]{});
            while (cursor.moveToNext()) {
                histories.add(new ChargeSpeedHistory() {{
                    capacity = cursor.getInt(cursor.getColumnIndex("capacity"));
                    temperature = cursor.getLong(cursor.getColumnIndex("temperature"));
                }});
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception ignored) {

        }
        return histories;
    }

    public ArrayList<ChargeTimeHistory> chargeTime() {
        ArrayList<ChargeTimeHistory> histories = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final Cursor cursor = sqLiteDatabase.rawQuery("select capacity, min(time) as start_time, max(time) as end_time from charge_history group by capacity", new String[]{});
            while (cursor.moveToNext()) {
                histories.add(new ChargeTimeHistory() {{
                    startTime = cursor.getLong(cursor.getColumnIndex("start_time"));
                    endTime = cursor.getLong(cursor.getColumnIndex("end_time"));
                    capacity = cursor.getInt(cursor.getColumnIndex("capacity"));
                }});
            }
            cursor.close();
            sqLiteDatabase.close();
        } catch (Exception ex) {
            ex.getMessage();
        }
        return histories;
    }

    public boolean addHistory(long io, int capacity, float temperature) {
        SQLiteDatabase database = getWritableDatabase();
        getWritableDatabase().beginTransaction();
        try {
            database.execSQL("insert into charge_history(time, io, capacity, temperature) values (?, ?, ?, ?)", new Object[]{
                    System.currentTimeMillis(),
                    io,
                    capacity,
                    temperature
            });
            database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
            database.endTransaction();
        }
    }

    public boolean clearAll() {
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.execSQL("delete from  charge_history", new String[]{});
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
