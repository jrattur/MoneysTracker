package com.nomadicmonkeyapps.moneys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class TransactionRecord {

	private static final SimpleDateFormat dbDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
	private static final SimpleDateFormat dbDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
	private static final SimpleDateFormat displayTimeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);

	private long TRNS_ID = 0;
	private Calendar itemDateTime;
	private String itemName = "";
	private String itemType = "";
	private Float itemPrice = 0f;
	private int hasPhoto = 0;
	private int hasSound = 0;
	private String note = "";
	private Float runningBalance = 0f;
	private String accountName = "";
	private int needsUpdate = 0;

	public TransactionRecord(Context context) {
		itemDateTime = Calendar.getInstance();
		
		SharedPreferences sharedPrefReader = context.getSharedPreferences("Prefs", 0);
		sharedPrefReader.getString("CurrentAccount", "");
	}

	public String getRunningBalanceString() {
		return String.format("%.2f",runningBalance);
	}
	public void setRunningBalance(Float runningBalance) {
		this.runningBalance = runningBalance;
	}

	public String getItemDateTime_dbDateTimeString() {

		return dbDateTime.format(itemDateTime.getTime());
	}
	public String getItemDateTime_dbDateOnlyString() {

		return dbDateOnlyFormat.format(itemDateTime.getTime());
	}
	public String getItemDateTime_displayDateFormat() {

		return displayDateFormat.format(itemDateTime.getTime());
	}
	public String getItemDateTime_displayTimeFormat() {

		return displayTimeFormat.format(itemDateTime.getTime());
	}
	public int getItemDateTime_minute() {
		return itemDateTime.get(Calendar.MINUTE);
	}
	public int getItemDateTime_hour() {
		return itemDateTime.get(Calendar.HOUR_OF_DAY);
	}
	public int getItemDateTime_day() {
		return itemDateTime.get(Calendar.DAY_OF_MONTH);
	}
	public int getItemDateTime_month() {
		return itemDateTime.get(Calendar.MONTH);
	}
	public int getItemDateTime_year() {
		return itemDateTime.get(Calendar.YEAR);
	}	
	public void setItemDateTime_Minute_Hour_Day_Month_Year(int minute, int hour, int day, int month, int year) {

		if (minute != -1)
			this.itemDateTime.set(Calendar.MINUTE, minute);

		if (hour != -1)
			this.itemDateTime.set(Calendar.HOUR_OF_DAY, hour);

		if (day != -1)
			this.itemDateTime.set(Calendar.DAY_OF_MONTH, day);

		if (month != -1)
			this.itemDateTime.set(Calendar.MONTH, month);

		if (year != -1l)
			this.itemDateTime.set(Calendar.YEAR, year);
	}
	public void setItemDateTime_parseDBDateTimeString(String dbitemDateTime) {

		Calendar dateTime = Calendar.getInstance();

		try {
			dateTime.setTime(dbDateTime.parse(dbitemDateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.itemDateTime = dateTime;
	}
	public void setItemDateTime_parseDBDateOnlyString(String dbDateOnlyString) {

		Calendar dateTime = Calendar.getInstance();

		try {
			dateTime.setTime(dbDateOnlyFormat.parse(dbDateOnlyString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.itemDateTime = dateTime;

	}
	public void setItemDateTime_parsedisplayDateFormat(String displayDateFormatString) {

		Calendar dateTime = Calendar.getInstance();

		try {
			dateTime.setTime(displayDateFormat.parse(displayDateFormatString));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.itemDateTime = dateTime;

	}
	public void setItemDateTime_milliseconds(long milliseconds) {

		Calendar dateTime = Calendar.getInstance();

		dateTime.setTimeInMillis(milliseconds);

		this.itemDateTime = dateTime;

	}
	public void setItemDateTime_nextday() {
		itemDateTime.add(Calendar.DAY_OF_MONTH, 1);
	}
	public void setItemDateTime_prevday() {
		itemDateTime.add(Calendar.DAY_OF_MONTH, -1);
	}
	
	public String getItemNameString() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Float getItemPriceFloat() {
		return itemPrice;
	}
	public String getItemPriceDisplayString() {
		return String.format("%.2f",itemPrice);
	}
	public void setItemPrice_parseString(Context context, String itemPriceString) {

		itemPriceString = itemPriceString.replace(',', '.');
		
		try {
			this.itemPrice = Float.valueOf(itemPriceString);
		} catch (NumberFormatException e) {
			
			this.itemPrice = 0f;
		}
		
	}
	public void setItemPrice_Float(Float itemPrice) {

		this.itemPrice = itemPrice;
	}
	
	public int getHasPhoto() {
		return hasPhoto;
	}

	public void setHasPhoto(int hasPhoto) {
		this.hasPhoto = hasPhoto;
	}
	
	public void setHasPhoto_parseString(String hasPhoto) {
		this.hasPhoto = Integer.valueOf(hasPhoto);
	}

	public int getHasSound() {
		return hasSound;
	}
	public void setHasSound(int hasSound) {
		this.hasSound = hasSound;
	}
	public void setHasSound_parseString(String hasSound) {
		this.hasSound = Integer.valueOf(hasSound);
	}
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
	public String getAccountName() {
		return accountName;
	}

	public int getNeedsUpdate() {
		return needsUpdate;
	}
	public void setNeedsUpdate() {
		
		if(itemName.equals("") || Math.abs(itemPrice - 0.0f) < 0.00000001)
		{
			needsUpdate = 1;			
		}
		
	}

	public long getID() {
		return TRNS_ID;
	}
	public void setID(long rowID) {
		TRNS_ID = rowID;
	}
	
	public boolean isAfterOrEqual(TransactionRecord date) {
		
		if(itemDateTime.compareTo(date.getCalendarObject()) >=0)
		{
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public Calendar getCalendarObject() {
		return itemDateTime;
	}

}
