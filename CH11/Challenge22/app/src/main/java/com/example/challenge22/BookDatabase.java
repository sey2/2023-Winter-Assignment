package com.example.challenge22;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class BookDatabase {

    public static final String TAG = "BookDatabase";

    // 싱글톤 인스턴스 데이터베이스 오브젝트 입니다.
    private static BookDatabase database;

    // 데이터베이스 이름
    public static String DATABASE_NAME = "book.db";

    // 테이블 이름
    public static String TABLE_BOOK_INFO = "BOOK_INFO";

    public static int DATABASE_VERSION = 1;

    // 헬퍼 클래스 오브젝트 선언
    private DatabaseHelper dbHelper;

    // 데이터베이스 관련 작업을 하기 위한 오브젝트 입니다.
    private SQLiteDatabase db;

    // Application에 관한 정보를 얻기 위한 오브젝트
    private Context context;


    // 생성자
    private BookDatabase(Context context) {
        this.context = context;
    }

    // 싱글톤 인스턴스 생성 함수
    public static BookDatabase getInstance(Context context) {
        if (database == null) {
            database = new BookDatabase(context);
        }

        return database;
    }

    // 데이터베에스 open 함수
    public boolean open() {
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    // 데이터베이스를 닫는 함수
    public void close() {
        println("closing database [" + DATABASE_NAME + "].");
        db.close();
        database = null;
    }

    /**
     * raw 단위로 sql문을 실행 시켜주는 함수
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

    // sql 실행 메소드
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
     SQLiteOpenHelper를 사용하면 기존에 테이블이 있는지 없느지 판단하여 각각에 알맞게
     데이터베이스 테이블을 업그레이드 시킨다는가 새로 생성할 수 있습니다.
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /* 테이블을 생성하는 메소드  */
        public void onCreate(SQLiteDatabase _db) {
            // TABLE_BOOK_INFO
            println("creating table [" + TABLE_BOOK_INFO + "].");

            // 테이블이 이미 존재하면 기존에 존재하는 테이블을 지웁니다.
            String DROP_SQL = "drop table if exists " + TABLE_BOOK_INFO;
            try {
                _db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }

            // 테이블 생성 SQL문
            String CREATE_SQL = "create table " + TABLE_BOOK_INFO + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  NAME TEXT, "
                    + "  AUTHOR TEXT, "
                    + "  CONTENTS TEXT, "
                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    + ")";
            try {
                _db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            // insert 5 book records
            insertRecord(_db, "Do it! 안드로이드 앱 프로그래밍", "정재곤", "안드로이드 기본서로 이지스퍼블리싱 출판사에서 출판했습니다.");
            insertRecord(_db, "Programming Android", "Mednieks, Zigurd", "Oreilly Associates Inc에서 2011년 04월에 출판했습니다.");
            insertRecord(_db, "센차터치 모바일 프로그래밍", "이병옥,최성민 공저", "에이콘출판사에서 2011년 10월에 출판했습니다.");
            insertRecord(_db, "시작하세요! 안드로이드 게임 프로그래밍", "마리오 제흐너 저", "위키북스에서 2011년 09월에 출판했습니다.");
            insertRecord(_db, "실전! 안드로이드 시스템 프로그래밍 완전정복", "박선호,오영환 공저", "DW Wave에서 2010년 10월에 출판했습니다.");

        }

        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");

            if (oldVersion < 2) {   // version 1

            }

        }

        // 레코드 추가 함수
        private void insertRecord(SQLiteDatabase _db, String name, String author, String contents) {
            try {
                _db.execSQL( "insert into " + TABLE_BOOK_INFO + "(NAME, AUTHOR, CONTENTS) values ('" + name + "', '" + author + "', '" + contents + "');" );
            } catch(Exception ex) {
                Log.e(TAG, "Exception in executing insert SQL.", ex);
            }
        }

    }

    public void insertRecord(String name, String author, String contents) {
        try {
            // insert into 테이블 이름 (레코드) values (업데이트할 내용들)
            //  레코드 내용을 추가할 때는 위와 같이 사용합니다.
            // 따라서 해당 SQL문을 만들어 execSQL 메소드를 이용해 SQL문을 실행하면 테이블에 레코드가 추가 됩니다.
            db.execSQL( "insert into " + TABLE_BOOK_INFO + "(NAME, AUTHOR, CONTENTS) values ('" + name + "', '" + author + "', '" + contents + "');" );
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
    }


    // 레코드 수정 함수
    public void updateRecord(String bookName, String author, String content, BookDTO prevBook){
        try{
            // 선택된 아이템 (책 정보)의 기본 키 값을 찾음
            String id = findId(prevBook.name);

            // Update 테이블 이름 Set 수정하고 싶은 컬럼 = '내용' Where 컬럼='';
            // 레코드 내용을 수정할 때는 위와 같이 사용합니다.
            // 따라서 해당 SQL문을 만들어 execSQL 메소드를 이용해 SQL문을 실행하면 레코드가 업데이트 됩니다.
            db.execSQL("UPDATE " + TABLE_BOOK_INFO + " SET " + "NAME='" + bookName + "',"
                    +"AUTHOR='" + author +"',"
                    +"CONTENTS='" + content
                    +"' WHERE _id=" + id);
        }catch (Exception e){
            Log.e(TAG, "Exception in excuting update SQL");
        }
    }

    // 레코드 삭제 함수
    public void deleteRecord(BookDTO bookDTO){
        try{
            // DELETE FROM 테이블 이름 wehre 삭제할 레코드 ='';
            // 데이터베이스에서 레코드를 삭제할시 위와 같이 사용합니다.
            // 따라서 해당 SQL문을 만들어 execSQL 메소드를 이용해 SQL문을 실행하면 레코드가 삭제 됩니다.
            String id = findId(bookDTO.name);
            db.execSQL("DELETE FROM " + TABLE_BOOK_INFO + " where _id=" + id);
        }catch (Exception e){
            Log.e(TAG, "Exception in excuting update SQL");
        }
    }

    // 책 이름을 통해 id 값을 찾아주는 메소드
    public String findId(String bookName) {
        ArrayList<BookDTO> result = new ArrayList<BookDTO>();

        try {
            Cursor cursor = db.rawQuery("select _id, NAME from " + TABLE_BOOK_INFO, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String id = cursor.getString(0);
                String name = cursor.getString(1);

                if(name.equals(bookName))
                    return id;
            }

        } catch(Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }

        return "-1";
    }

    public ArrayList<BookDTO> selectAll() {
        ArrayList<BookDTO> result = new ArrayList<BookDTO>();

        try {
            // Cursor 객체는 해당 테이블에 커서를 가져다 댄다고 생각할 수 있습니다.
            // 처음에 커서를 가져다 되면 1행에 커서가 가져가고 테이블 1행의 내용에 접근할 수 있게 됩니다.
            // 커서를 다음으로 옮기게 되면 2행 내용에 접근 할 수 있게 되고 이런식으로 테이블의 내용을 접근할 때 Cursor를 이용합니다.
            Cursor cursor = db.rawQuery("select NAME, AUTHOR, CONTENTS from " + TABLE_BOOK_INFO, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String author = cursor.getString(1);
                String contents = cursor.getString(2);

                BookDTO info = new BookDTO(name, author, contents);
                result.add(info);
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