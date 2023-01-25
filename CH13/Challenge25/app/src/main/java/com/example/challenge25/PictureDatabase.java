package com.example.challenge25;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * 사진 데이터베이스
 */
public class PictureDatabase {
    private static final String TAG = "PictureDatabase";

    /**
     * 싱글톤 인스턴스
     */
    private static PictureDatabase database;

    /**
     * table name for PICTURE
     */
    public static String TABLE_NAME = "PICTURE";

    public static String DATABASE_NAME = "PICTUREDB";

    /**
     * version
     */
    public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    private DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 인스턴스
     */
    private SQLiteDatabase db;

    /**
     * 컨텍스트 객체
     */
    private Context context;

    /**
     * 생성자
     */
    private PictureDatabase(Context context) {
        this.context = context;
    }

    /**
     * 인스턴스 가져오기
     */
    public static PictureDatabase getInstance(Context context) {
        if (database == null) {
            database = new PictureDatabase(context);
        }

        return database;
    }

    /**
     * 데이터베이스 열기
     */
    public boolean open() {
        println("opening database [" + TABLE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
        println("closing database [" + TABLE_NAME + "].");
        db.close();

        database = null;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }

        return c1;
    }

    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }

        return true;
    }



    /**
     * Database Helper inner class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + DATABASE_NAME + "].");

            // TABLE_NOTE
            println("creating table [" + TABLE_NAME + "].");

            // drop existing table
            String DROP_SQL = "drop table if exists " + TABLE_NAME;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // create table
            String CREATE_SQL = "create table " + TABLE_NAME + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  PICTURE TEXT DEFAULT '' )";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    public void updatePicture(PictureInfo pi, String path){
        String UPDATE_SQL = "update " + TABLE_NAME + " set PICTURE ='" + path + "' where _id=" + pi._id + ";";
        try {
            db.execSQL(UPDATE_SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }
    }

    public void deleteRaw(PictureInfo pi){
        String DELETE_SQL = "delete from " + TABLE_NAME + " where _id=" + pi._id + ";";
        try {
            db.execSQL(DELETE_SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }
    }

    // 테이블에 존재하는 모든 레코드를 조회
    public ArrayList<PictureInfo> selectAll() {
        ArrayList<PictureInfo> result = new ArrayList<PictureInfo>();

        try {
            // Cursor 객체는 해당 테이블에 커서를 가져다 댄다고 생각할 수 있습니다.
            // 처음에 커서를 가져다 되면 1행에 커서가 가져가고 테이블 1행의 내용에 접근할 수 있게 됩니다.
            // 커서를 다음으로 옮기게 되면 2행 내용에 접근 할 수 있게 되고 이런식으로 테이블의 내용을 접근할 때 Cursor를 이용합니다.
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String id = cursor.getString(0);
                String picture = cursor.getString(1);;

                PictureInfo pictureDTO = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    pictureDTO = new PictureInfo(picture, id, LocalDate.now().toString());
                    pictureDTO.setId(MainActivity.idCnt++);
                    pictureDTO.setPrimaryKey(Integer.parseInt(id));
                }
                result.add(pictureDTO);
            }

        } catch(Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }

        return result;
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }


}