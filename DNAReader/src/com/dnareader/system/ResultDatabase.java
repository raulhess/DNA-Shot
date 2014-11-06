package com.dnareader.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dnareader.data.Hit;
import com.dnareader.data.Result;

public class ResultDatabase {
	public static final String DATABASE_NAME = "db_Results";
	int DATABASE_VERSION = 1;
	final Context context;
	MyOpenHelper openHelper;
	SQLiteDatabase db;
	
	String KEY_ID = "resultid";
	String KEY_STATE = "state";
	String KEY_OCR = "ocr";
	String KEY_XML = "xml";
	String KEY_IMG = "img";
	
	String KEY_RESULT_FOREIGN_ID = "result";
	String KEY_HIT_ID = "hitid";
	String KEY_HITNAME = "name";
	String KEY_LEN = "len";
	
	String KEY_HIT_FOREIGN_ID = "hit";
	String KEY_EVALUE = "evalue";
	String KEY_QUERY_FROM = "queryfrom";
	String KEY_QUERY_TO = "queryto";
	String KEY_HIT_FROM = "hitfrom";
	String KEY_HIT_TO = "hitto";
	String KEY_ALIGN_LEN = "alignlen";
	String KEY_HSEQ = "hseq";
	String KEY_QSEQ = "qseq";
	String KEY_MIDLINE = "midline";
	String KEY_GAPS = "gaps";
	
	String TABLENAME_RESULT = "result";
	String TABLENAME_HIT = "hit";
	String TABLENAME_HSP = "hsp";
	
	String SQL_CREATE_TABLE_RESULT = "create table " + TABLENAME_RESULT + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY autoincrement, "
			+ KEY_STATE + " INTEGER not null, "
			+ KEY_OCR + " TEXT, "
			+ KEY_XML + " TEXT, "
			+ KEY_IMG + " BLOB);";
	String SQL_CREATE_TABLE_HITS = "create table " + TABLENAME_HIT + " ("
			+ KEY_HIT_ID + " INTEGER PRIMARY KEY autoincrement, "
			+ KEY_HITNAME + " TEXT not null, "
			+ KEY_LEN + " INTEGER not null, "
			+ KEY_RESULT_FOREIGN_ID + " INTEGER not null, "
			+ "FOREIGN KEY(" + KEY_RESULT_FOREIGN_ID + ") REFERENCES "
			+ TABLENAME_RESULT + " (" + KEY_ID + "));";
	String SQL_CREATE_TABLE_HSPS = "create table " + TABLENAME_HSP + " ("
			+ KEY_EVALUE + " INTEGER, "
			+ KEY_QUERY_FROM + " INTEGER, "
			+ KEY_QUERY_TO + " INTEGER, "
			+ KEY_HIT_FROM + " INTEGER, "
			+ KEY_HIT_TO + " INTEGER, "
			+ KEY_ALIGN_LEN + " INTEGER, "
			+ KEY_HSEQ + " INTEGER, "
			+ KEY_QSEQ + " INTEGER, "
			+ KEY_MIDLINE + " INTEGER, "
			+ KEY_GAPS + " INTEGER, "
			+ KEY_HIT_FOREIGN_ID + " INTEGER not null, "
			+ "FOREIGN KEY(" + KEY_HIT_FOREIGN_ID + ") REFERENCES "
			+ TABLENAME_HIT + " (" + KEY_HIT_ID + "));";
	
	public ResultDatabase(Context context){
		this.context = context;
		openHelper = new MyOpenHelper(context);
	}
	
	
	private class MyOpenHelper extends SQLiteOpenHelper{

		public MyOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(SQL_CREATE_TABLE_RESULT);
				db.execSQL(SQL_CREATE_TABLE_HITS);
				db.execSQL(SQL_CREATE_TABLE_HSPS);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_HSP);
			db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_HIT);
			db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_RESULT);
			onCreate(db);
		}
		
	}
	
	public ResultDatabase open() throws SQLException{
		db = openHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		openHelper.close();
	}
	
	public long insertResult(Result result){
		ContentValues fields = new ContentValues();
		fields.put(KEY_STATE, result.getState());
		fields.put(KEY_OCR, result.getOcrText());
		fields.put(KEY_XML, result.getBlastXML());
		fields.put(KEY_IMG, result.getImage());
		return db.insert(TABLENAME_RESULT, null, fields);
	}
	
	public long insertHit(Hit hit){
		ContentValues fields = new ContentValues();
		fields.put(KEY_HITNAME, hit.getHit_def());
		fields.put(KEY_ALIGN_LEN, hit.getHit_len());
		fields.put(KEY_RESULT_FOREIGN_ID, hit.getResult_id());
		return db.insert(TABLENAME_HIT, null, fields);
	}
	
}
