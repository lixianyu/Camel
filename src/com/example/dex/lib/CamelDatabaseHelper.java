package com.example.dex.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CamelDatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "CamelDatabaseHelper";
    private static final boolean DEBUG = true;

    private static final String DATABASE_NAME = "Camel.cam";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_VERSION = "Camels";

    // Field of TABLE_VERSION
    private static final String VERSION_FIELD_Local = "CamelsL";//本地软件版本号
    private static final String VERSION_FIELD_Server = "CamelsS";//服务器的版本号
    private static final String VERSION_FIELD_DownSucess = "CamelsD";
    private static final String VERSION_FIELD_Setup = "CamelsSe";
    
    public CamelDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (DEBUG) LogCamel.logd(TAG, "Enter CamelDatabaseHelper()...000, DATABASE_VERSION = " + DATABASE_VERSION);
    }

    private CamelDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
    	super(context, name, factory, version);
    	if (DEBUG) LogCamel.logd(TAG, "Enter CamelDatabaseHelper()...001");
    }

    @SuppressWarnings("unused")
	private CamelDatabaseHelper(Context context, String name){
    	this(context, name, DATABASE_VERSION);
    	if (DEBUG) LogCamel.logd(TAG, "Enter CamelDatabaseHelper()...002");
    }
    
    private CamelDatabaseHelper(Context context, String name, int version){
    	this(context, name, null, version);
    	if (DEBUG) LogCamel.logd(TAG, "Enter CamelDatabaseHelper()...003");
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (DEBUG) LogCamel.logd(TAG, "Enter onCreate(), db = " + db);
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VERSION + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                VERSION_FIELD_Local + " LONG," +
                VERSION_FIELD_Server + " LONG," +
                VERSION_FIELD_DownSucess + " INTEGER," +
                VERSION_FIELD_Setup + " TEXT" +
                ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG) LogCamel.logd(TAG, "Enter onUpgrade(), db = " + db + ",oldVersion=" + oldVersion + ",newVersion=" + newVersion);
	}
	
	public synchronized static void initDB(Context context) {
		if (DEBUG) LogCamel.logd(TAG, "Enter initDB(), VERSION_SOFTWARE = " + TheConfig.VERSION_DEX);
		CamelDatabaseHelper mDBHelper = new CamelDatabaseHelper(context);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		String col[] = { VERSION_FIELD_Local };
		Cursor cur = db.query(TABLE_VERSION, col, null, null, null, null, null);
		if (cur.getCount() > 0) {
			if (DEBUG) LogCamel.logi(TAG, "TABLE_VERSION had been init.");
			cur.moveToNext();
			long iLocalVersion = cur.getLong(0);
			if (DEBUG) LogCamel.logv(TAG, "iLocalVersion = " + iLocalVersion);
			if (changeToLongVersion(TheConfig.VERSION_DEX) > iLocalVersion) {
				if (DEBUG) LogCamel.logd(TAG, "oh, the new software version is biger!!!");
				ContentValues cv = new ContentValues();
				cv.put(VERSION_FIELD_Local, changeToLongVersion(TheConfig.VERSION_DEX));
				int ret = db.update(TABLE_VERSION,
						  cv,
			  			  null,
			  			  null);
				if (DEBUG) LogCamel.logi(TAG, "db.update(VERSION_FIELD_Local) return " + ret);
			}
			cur.close();
			db.close();
			return;
		}
		
		ContentValues values = new ContentValues();
		long lVersion = changeToLongVersion(TheConfig.VERSION_DEX);
    	values.put(VERSION_FIELD_Local, lVersion);
    	values.put(VERSION_FIELD_Server, lVersion);
    	values.put(VERSION_FIELD_DownSucess, 0);
    	values.put(VERSION_FIELD_Setup, "");
    	long lInsertReturn = db.insert(TABLE_VERSION, null, values);
    	if (lInsertReturn != -1) {
    		if (DEBUG) LogCamel.logv(TAG, "db.insert sucess!!");
    	}
    	else {
    		if (DEBUG) LogCamel.loge(TAG, "db.insert error!!");
    	}
    	cur.close();
    	db.close();
    }
	
	/**
	 * @param sVersion 1.0.0.12113012
	 * @return 10012113012
	 */
	private static long changeToLongVersion(String sVersion) {
		if (DEBUG) LogCamel.logv(TAG, "Enter changeToLongVersion(), sVersion = " + sVersion);
		String[] strings = sVersion.split("\\.");
		if (DEBUG) LogCamel.logd(TAG, "strings = " + strings);
		String sLong = "";
		for (String snow : strings) {
			if (DEBUG) LogCamel.logv(TAG, "snow = " + snow);
			sLong += snow;
		}
		if (DEBUG) LogCamel.logd(TAG, "sInteger = " + sLong);
		long lRet = Long.valueOf(sLong);
		if (DEBUG) LogCamel.logd(TAG, "iRet = " + lRet);
		return lRet;
	}
}
