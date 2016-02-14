package com.nomadicmonkeyapps.moneys;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBManager extends SQLiteOpenHelper {

	Context context;

	static final String dbName = "MoneysDatabase.db";

	static final String table_Items = "ITEMS";
	static final String field_ITEM_NAME = "ITM_NAME";
	static final String field_ITEM_PRICE = "ITM_PRICE";
	static final String field_ITEM_TYPE = "ITM_TYPE";

	static final String table_Transactions = "TRANSACTIONS";
	static final String field_TransactionsID = "TRNS_ID";
	static final String field_TransactionsDateTime = "TRNS_DATETIME";
	static final String field_TransactionsTypeName = "TYPE_NAME";
	static final String field_TransactionsItemsName = "ITEM_NAME";
	static final String field_TransactionsHasPhoto = "TRNS_PHOTO";
	static final String field_TransactionsHasSound = "TRNS_SOUND";
	static final String field_TransactionsNote = "TRNS_NOTE";
	static final String field_TransactionsPrice = "TRNS_PRICE";
	static final String field_TransactionsACCName = "ACC_NAME";

	static final String table_ItemTypes = "ITEM_TYPES";
	static final String field_ItemTypesName = "TYPE_NAME";

	static final String table_Accounts = "ACCOUNTS";
	static final String field_Accounts_Name = "ACC_NAME";
	static final String field_Accounts_Currency = "ACC_CURRENCY";

	private static int dbUpdated;

	DBManager(Context context) {
		super(context, dbName, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String newTableQueryString = "create table " + table_Items + " (" + field_ITEM_NAME + " text," + field_ITEM_PRICE + " real," + field_ITEM_TYPE
				+ " text" + ");";

		db.execSQL(newTableQueryString);

		newTableQueryString =

		"create table " + table_Transactions + " (" + field_TransactionsID + " integer primary key not null," + field_TransactionsDateTime + " DATETIME,"
				+ field_TransactionsTypeName + " text," + field_TransactionsItemsName + " text," + field_TransactionsHasPhoto + " integer,"
				+ field_TransactionsHasSound + " integer," + field_TransactionsNote + " text," + field_TransactionsPrice + " real,"
				+ field_TransactionsACCName + " text" + ");";

		db.execSQL(newTableQueryString);

		newTableQueryString = "create table " + table_ItemTypes + " (" + field_ItemTypesName + " text primary key not null" + ");";

		db.execSQL(newTableQueryString);

		newTableQueryString = "create table " + table_Accounts + " (" + field_Accounts_Name + " text," + field_Accounts_Currency + " text);";

		db.execSQL(newTableQueryString);

		ContentValues fields = new ContentValues();
		fields.put(field_TransactionsACCName, "Wallet");
		db.insert(table_Accounts, null, fields);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	private long addrow(Context context, String TABLE_NAME, ContentValues FIELDS) {

		SQLiteDatabase database = this.getWritableDatabase();

		long rowid = 0;
		rowid = database.insert(TABLE_NAME, null, FIELDS);

		database.close();
		return rowid;
	}

	private long updateRow(Context context, String TABLE_NAME, ContentValues FIELDS, long rowID) {

		SQLiteDatabase database = this.getWritableDatabase();

		database.update(TABLE_NAME, FIELDS, field_TransactionsID + " = " + rowID, null);

		database.close();
		return rowID;
	}

	private long deleteRow(Context context, String TABLE_NAME, long rowID) {

		SQLiteDatabase database = this.getWritableDatabase();

		rowID = database.delete(TABLE_NAME, field_TransactionsID + " = " + rowID, null);

		database.close();
		return rowID;
	}

	public long purchaseItem(Context context, TransactionRecord dbRecord) {

		long rowID = 0;

		ContentValues fields = new ContentValues();

		fields.put(field_TransactionsDateTime, dbRecord.getItemDateTime_dbDateTimeString());
		fields.put(field_TransactionsItemsName, dbRecord.getItemNameString());
		fields.put(field_TransactionsTypeName, dbRecord.getItemType());
		fields.put(field_TransactionsPrice, dbRecord.getItemPriceDisplayString());
		fields.put(field_TransactionsHasPhoto, dbRecord.getHasPhoto());
		fields.put(field_TransactionsHasSound, dbRecord.getHasSound());
		fields.put(field_TransactionsNote, dbRecord.getNote());
		fields.put(field_TransactionsACCName, dbRecord.getAccountName());

		rowID = addrow(context, table_Transactions, fields);

		Log.d("zzz",
				"Transaction added, " + "ID = " + rowID + " Name = " + dbRecord.getItemNameString() + " DateTime = "
						+ dbRecord.getItemDateTime_dbDateTimeString() + " Type = " + dbRecord.getItemType() + " Price = " + dbRecord.getItemPriceDisplayString()
						+ " HasPhoto = " + dbRecord.getHasPhoto() + " HsaSound = " + dbRecord.getHasSound() + " Note = " + dbRecord.getNote());

		String toastMessage = "";

		if (!dbRecord.getItemNameString().equals("")) {
			toastMessage += dbRecord.getItemNameString();
		} else {
			toastMessage += "Item bought";
		}

		if (dbRecord.getHasPhoto() == 1) {
			toastMessage += " with photo";
		}

		if (!dbRecord.getNote().equals("")) {
			toastMessage += " with note";
		}

		if (dbRecord.getHasSound() == 1) {
			toastMessage += " with sound";
		}

		if (!(Math.abs(dbRecord.getItemPriceFloat() - 0.0f) < 0.00000001)) {
			toastMessage += "\n$" + dbRecord.getItemPriceFloat();
		}

		toastMessage += "\n" + dbRecord.getItemDateTime_displayDateFormat() + " " + dbRecord.getItemDateTime_displayTimeFormat();

		Toast displayMessage = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
		displayMessage.show();

		setDbUpdated();
		return rowID;
	}

	public long updateItem(Context context, TransactionRecord dbRecord, long rowID) {

		ContentValues fields = new ContentValues();

		fields.put(field_TransactionsDateTime, dbRecord.getItemDateTime_dbDateTimeString());
		fields.put(field_TransactionsItemsName, dbRecord.getItemNameString());
		fields.put(field_TransactionsTypeName, dbRecord.getItemType());
		fields.put(field_TransactionsPrice, dbRecord.getItemPriceDisplayString());
		fields.put(field_TransactionsHasPhoto, dbRecord.getHasPhoto());
		fields.put(field_TransactionsHasSound, dbRecord.getHasSound());
		fields.put(field_TransactionsNote, dbRecord.getNote());
		fields.put(field_TransactionsACCName, dbRecord.getAccountName());

		rowID = updateRow(context, table_Transactions, fields, rowID);

		Log.d("zzz",
				"Transaction updated, " + "ID = " + rowID + " Name = " + dbRecord.getItemNameString() + " DateTime = "
						+ dbRecord.getItemDateTime_dbDateTimeString() + " Type = " + dbRecord.getItemType() + " Price = " + dbRecord.getItemPriceDisplayString()
						+ " HasPhoto = " + dbRecord.getHasPhoto() + " HsaSound = " + dbRecord.getHasSound() + " Note = " + dbRecord.getNote());

		String toastMessage = "";

		if (!dbRecord.getItemNameString().equals("")) {
			toastMessage += dbRecord.getItemNameString();
		} else {
			toastMessage += "Item";
		}

		toastMessage += " Updated";

		int photoNoteSoundCount = 0;

		if (dbRecord.getHasPhoto() == 1) {
			photoNoteSoundCount++;
		}
		if (!dbRecord.getNote().equals("")) {
			photoNoteSoundCount++;
		}
		if (dbRecord.getHasSound() == 1) {
			photoNoteSoundCount++;
		}
		if (photoNoteSoundCount > 0) {
			toastMessage += "\nWith ";
			
			if(dbRecord.getHasPhoto() == 1)
			{
				toastMessage += "photo";
				photoNoteSoundCount--;
				
				if(photoNoteSoundCount == 1)
					toastMessage += " and ";
				
				if(photoNoteSoundCount == 2)
					toastMessage += ", ";
			}
			
			if(!dbRecord.getNote().equals(""))
			{
				toastMessage += "note";
				photoNoteSoundCount--;
				
				if(photoNoteSoundCount == 1)
					toastMessage += " and ";
			}
			
			if(dbRecord.getHasSound() == 1)
				toastMessage += "sound";
		}

		if (!(Math.abs(dbRecord.getItemPriceFloat() - 0.0f) < 0.00000001)) {
			toastMessage += "\n$" + dbRecord.getItemPriceFloat();
		}

		toastMessage += "\n" + dbRecord.getItemDateTime_displayDateFormat() + " " + dbRecord.getItemDateTime_displayTimeFormat();

		Toast displayMessage = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
		displayMessage.show();

		setDbUpdated();
		return rowID;
	}

	public long deleteItem(Context context, TransactionRecord dbRecord, long rowID) {

		rowID = deleteRow(context, table_Transactions, rowID);

		Log.d("zzz", "Transaction Delted, " + "ID = " + rowID);

		String toastMessage = "";
		
		if (!dbRecord.getItemNameString().equals("")) {
			toastMessage += dbRecord.getItemNameString();
		} else {
			toastMessage += "Item";
		}

		toastMessage += " deleted";

		if (!(Math.abs(dbRecord.getItemPriceFloat() - 0.0f) < 0.00000001)) {
			toastMessage += "\n$" + dbRecord.getItemPriceFloat();
		}

		toastMessage += "\n" + dbRecord.getItemDateTime_displayDateFormat() + " " + dbRecord.getItemDateTime_displayTimeFormat();

		Toast displayMessage = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
		displayMessage.show();

		
		setDbUpdated();
		return rowID;
	}

	public Cursor getAllTransactionsTableDataCursor() {
		Cursor dbCursor;

		SQLiteDatabase db = this.getReadableDatabase();

		dbCursor = db.query(table_Transactions, new String[] { field_TransactionsDateTime, field_TransactionsItemsName, field_TransactionsTypeName,
				field_TransactionsPrice, field_TransactionsID, field_TransactionsHasPhoto, field_TransactionsHasSound, field_TransactionsNote }, null, null,
				null, null, field_TransactionsDateTime + " ASC");

		return dbCursor;
	}

	public ArrayList<String> getAllDatesArrayList(Context context) {

		Cursor dbCursorAllDates;
		SQLiteDatabase db = this.getWritableDatabase();
		ArrayList<String> allDatesStringArray = new ArrayList<String>();
		TransactionRecord tempRecord = new TransactionRecord(context);

		dbCursorAllDates = db.rawQuery("SELECT DISTINCT DATE(SUBSTR(" + field_TransactionsDateTime + ",1,19)) " + "FROM " + table_Transactions + " ORDER BY "
				+ field_TransactionsDateTime + " ASC", null);

		dbCursorAllDates.moveToFirst();

		while (dbCursorAllDates.isAfterLast() == false) {
			tempRecord.setItemDateTime_parseDBDateOnlyString(dbCursorAllDates.getString(0));
			allDatesStringArray.add(tempRecord.getItemDateTime_displayDateFormat());
			dbCursorAllDates.moveToNext();
		}

		db.close();

		return allDatesStringArray;
	}

	public TransactionRecord getTransaction(Context context, long rowId) {

		TransactionRecord transaction = new TransactionRecord(context);
		Cursor dbCursor;
		SQLiteDatabase db = this.getReadableDatabase();

		dbCursor = db.query(table_Transactions, new String[] { field_TransactionsDateTime, field_TransactionsItemsName, field_TransactionsTypeName,
				field_TransactionsPrice, field_TransactionsHasPhoto, field_TransactionsHasSound, field_TransactionsNote },
				field_TransactionsID + " = " + rowId, null, null, null, null);

		dbCursor.moveToFirst();

		transaction.setItemDateTime_parseDBDateTimeString(dbCursor.getString(0));
		transaction.setItemName(dbCursor.getString(1));
		transaction.setItemType(dbCursor.getString(2));
		transaction.setItemPrice_parseString(context,dbCursor.getString(3));
		transaction.setHasPhoto_parseString(dbCursor.getString(4));
		transaction.setHasSound_parseString(dbCursor.getString(5));
		transaction.setNote(dbCursor.getString(6));

		return transaction;
	}

	public int getTransactionRowCount() {

		Cursor dbCursor;
		SQLiteDatabase db = this.getReadableDatabase();

		dbCursor = db.query(table_Transactions, new String[] { "count(*)" }, null, null, null, null, null);

		dbCursor.moveToFirst();

		return dbCursor.getInt(0);
	}

	public String getBalance() {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dbCursor;

		dbCursor = db.query(table_Transactions, new String[] { field_TransactionsPrice }, null, null, null, null, null);

		dbCursor.moveToFirst();

		float balance = 0.0f;

		while (!dbCursor.isAfterLast()) {
			balance = balance + dbCursor.getFloat(0);
			dbCursor.moveToNext();
		}

		db.close();
		return String.format("%.2f",balance);

	}

	public String getMoneySpentOnDay(Context context, TransactionRecord date) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dbCursor;

		dbCursor = db.query(table_Transactions, new String[] { field_TransactionsPrice },
				field_TransactionsDateTime + " LIKE \"" + date.getItemDateTime_dbDateOnlyString() + "%\"", null, null, null, null);

		dbCursor.moveToFirst();

		float moneySpentToday = 0.0f;

		while (!dbCursor.isAfterLast()) {
			if (dbCursor.getFloat(0) < 0.0f) {
				moneySpentToday = moneySpentToday + dbCursor.getFloat(0);
			}
			dbCursor.moveToNext();
		}

		db.close();
		return String.valueOf(moneySpentToday);

	}

	public String getMoneyInOnDay(Context context, TransactionRecord date) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor dbCursor;

		dbCursor = db.query(table_Transactions, new String[] { field_TransactionsPrice },
				field_TransactionsDateTime + " LIKE \"" + date.getItemDateTime_dbDateOnlyString() + "%\"", null, null, null, null);

		dbCursor.moveToFirst();

		float moneyInToday = 0.0f;

		while (!dbCursor.isAfterLast()) {
			if (dbCursor.getFloat(0) > 0.0f) {
				moneyInToday = moneyInToday + dbCursor.getFloat(0);
			}
			dbCursor.moveToNext();
		}

		db.close();
		return String.valueOf(moneyInToday);

	}

	public static int getDbUpdated() {
		return dbUpdated;
	}

	public static void setDbUpdated() {
		dbUpdated = 1;
	}

	public static void clearDbUpdated() {
		dbUpdated = 0;
	}

}
