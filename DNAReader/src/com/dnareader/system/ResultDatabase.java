package com.dnareader.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dnareader.data.Hit;
import com.dnareader.data.Hsp;
import com.dnareader.data.Result;

public class ResultDatabase {
	public static final String DATABASE_NAME = "db_Results";
	int DATABASE_VERSION = 1;
	final Context context;
	MyOpenHelper openHelper;
	SQLiteDatabase db;

	static String KEY_ID = "resultid";
	static String KEY_STATE = "state";
	static String KEY_OCR = "ocr";
	static String KEY_XML = "xml";
	static String KEY_CHECKED = "checked";

	static String KEY_RESULT_FOREIGN_ID = "result";
	static String KEY_HIT_ID = "hitid";
	static String KEY_HITNAME = "name";
	static String KEY_LEN = "len";

	static String KEY_HIT_FOREIGN_ID = "hit";
	static String KEY_EVALUE = "evalue";
	static String KEY_QUERY_FROM = "queryfrom";
	static String KEY_QUERY_TO = "queryto";
	static String KEY_HIT_FROM = "hitfrom";
	static String KEY_HIT_TO = "hitto";
	static String KEY_ALIGN_LEN = "alignlen";
	static String KEY_HSEQ = "hseq";
	static String KEY_QSEQ = "qseq";
	static String KEY_MIDLINE = "midline";
	static String KEY_GAPS = "gaps";

	static String TABLENAME_RESULT = "result";
	static String TABLENAME_HIT = "hit";
	static String TABLENAME_HSP = "hsp";

	String SQL_CREATE_TABLE_RESULT = "create table " + TABLENAME_RESULT + " ("
			+ KEY_ID + " INTEGER PRIMARY KEY autoincrement, " + KEY_STATE
			+ " INTEGER not null, " + KEY_OCR + " TEXT, " + KEY_XML + " TEXT, "
			+ KEY_CHECKED + " INTEGER not null);";
	String SQL_CREATE_TABLE_HITS = "create table " + TABLENAME_HIT + " ("
			+ KEY_HIT_ID + " INTEGER PRIMARY KEY autoincrement, " + KEY_HITNAME
			+ " TEXT not null, " + KEY_LEN + " INTEGER not null, "
			+ KEY_RESULT_FOREIGN_ID + " INTEGER not null, " + "FOREIGN KEY("
			+ KEY_RESULT_FOREIGN_ID + ") REFERENCES " + TABLENAME_RESULT + " ("
			+ KEY_ID + "));";
	String SQL_CREATE_TABLE_HSPS = "create table " + TABLENAME_HSP + " ("
			+ KEY_EVALUE + " INTEGER, " + KEY_QUERY_FROM + " INTEGER, "
			+ KEY_QUERY_TO + " INTEGER, " + KEY_HIT_FROM + " INTEGER, "
			+ KEY_HIT_TO + " INTEGER, " + KEY_ALIGN_LEN + " INTEGER, "
			+ KEY_HSEQ + " INTEGER, " + KEY_QSEQ + " INTEGER, " + KEY_MIDLINE
			+ " INTEGER, " + KEY_GAPS + " INTEGER, " + KEY_HIT_FOREIGN_ID
			+ " INTEGER not null, " + "FOREIGN KEY(" + KEY_HIT_FOREIGN_ID
			+ ") REFERENCES " + TABLENAME_HIT + " (" + KEY_HIT_ID + "));";

	public ResultDatabase(Context context) {
		this.context = context;
		openHelper = new MyOpenHelper(context);
	}

	private class MyOpenHelper extends SQLiteOpenHelper {

		public MyOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(SQL_CREATE_TABLE_RESULT);
				db.execSQL(SQL_CREATE_TABLE_HITS);
				db.execSQL(SQL_CREATE_TABLE_HSPS);
			} catch (SQLException e) {
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

	public ResultDatabase open() throws SQLException {
		db = openHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		openHelper.close();
	}

	public long insertResult(Result result) {
		ContentValues fields = new ContentValues();
		fields.put(KEY_STATE, result.getState());
		fields.put(KEY_OCR, result.getOcrText());
		fields.put(KEY_XML, result.getBlastXML());
		fields.put(KEY_CHECKED, result.isChecked());
		return db.insert(TABLENAME_RESULT, null, fields);
	}

	public long insertHit(Hit hit, long resId) {
		ContentValues fields = new ContentValues();
		fields.put(KEY_HITNAME, hit.getHit_def());
		fields.put(KEY_LEN, hit.getHit_len());
		fields.put(KEY_RESULT_FOREIGN_ID, resId);
		return db.insert(TABLENAME_HIT, null, fields);
	}

	public long insertHsp(Hsp hsp, long hitId) {
		ContentValues fields = new ContentValues();
		fields.put(KEY_HIT_FOREIGN_ID, hitId);
		fields.put(KEY_EVALUE, hsp.getHsp_evalue());
		fields.put(KEY_QUERY_FROM, hsp.getHsp_query_from());
		fields.put(KEY_QUERY_TO, hsp.getHsp_query_to());
		fields.put(KEY_HIT_FROM, hsp.getHsp_hit_from());
		fields.put(KEY_HIT_TO, hsp.getHsp_hit_to());
		fields.put(KEY_ALIGN_LEN, hsp.getHsp_align_len());
		fields.put(KEY_HSEQ, hsp.getHsp_hseq());
		fields.put(KEY_QSEQ, hsp.getHsp_qseq());
		fields.put(KEY_MIDLINE, hsp.getHsp_midline());
		fields.put(KEY_GAPS, hsp.getHsp_gaps());
		return db.insert(TABLENAME_HSP, null, fields);
	}

	public Cursor loadResults() {
		return db.query(TABLENAME_RESULT, new String[] { KEY_ID, KEY_STATE,
				KEY_OCR, KEY_XML, KEY_CHECKED }, null, null, null,
				null, null);
	}

	public Cursor loadHits(long id) {
		return db.query(TABLENAME_HIT, new String[] { KEY_HIT_ID, KEY_HITNAME,
				KEY_LEN, KEY_RESULT_FOREIGN_ID }, KEY_RESULT_FOREIGN_ID + "="
				+ id, null, null, null, null);
	}

	public Cursor loadHsps(long id) {
		return db.query(TABLENAME_HSP, new String[] { KEY_HIT_FOREIGN_ID,
				KEY_EVALUE, KEY_QUERY_FROM, KEY_QUERY_TO, KEY_HIT_FROM,
				KEY_HIT_TO, KEY_ALIGN_LEN, KEY_HSEQ, KEY_QSEQ, KEY_MIDLINE,
				KEY_GAPS }, KEY_HIT_FOREIGN_ID + "=" + id, null, null, null,
				null);
	}
	
	public boolean updateResult(Result r){
		ContentValues fields = new ContentValues();
		fields.put(KEY_OCR, r.getOcrText());
		fields.put(KEY_XML, r.getBlastXML());
		return db.update(TABLENAME_RESULT, fields, KEY_ID + "=" + r.getId(), null) > 0;
	}

	public boolean updateResultState(long id, int state) {
		ContentValues fields = new ContentValues();
		fields.put(KEY_STATE, state);
		return db.update(TABLENAME_RESULT, fields, KEY_ID + "=" + id, null) > 0;
	}

	public boolean updateResultChecked(long id) {
		ContentValues fields = new ContentValues();
		fields.put(KEY_CHECKED, true);
		return db.update(TABLENAME_RESULT, fields, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteResult(long id) {
		return db.delete(TABLENAME_RESULT, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteHits(long id) {
		return db.delete(TABLENAME_HIT, KEY_HIT_ID + "=" + id, null) > 0;
	}

	public boolean deleteHsps(long id) {
		return db.delete(TABLENAME_HSP, KEY_HIT_FOREIGN_ID + "=" + id, null) > 0;
	}

}
