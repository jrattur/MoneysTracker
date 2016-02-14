package com.nomadicmonkeyapps.moneys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.nomadicmonkeyapps.moneys.R;

public class Act_History extends ListActivity {

	TextView moneySpentOnDay;
	TextView moneyInOnDay;
	TextView moneyDifferenceOnDay;
	
	Spinner dateSpinner;
	HistoryListViewArrayAdaptor recordsListAdaptor;
	ArrayList<TransactionRecord> allTransactions;
	List<TransactionRecord> transactionsToDisplay;
	ArrayList<String> allDatesStringArrayList;
	private static String currentDisplayDate;
	
	Map<String, Integer> datePosLocations;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_history);

		Log.d("zzz", "Act_History");
		
		currentDisplayDate = new TransactionRecord(this).getItemDateTime_displayDateFormat();
		
		moneySpentOnDay = (TextView) findViewById(R.id.HistoryMoneySpent);
		moneyInOnDay = (TextView) findViewById(R.id.HistoryMoneyIn);
		moneyDifferenceOnDay = (TextView) findViewById(R.id.HistoryMoneyDifference);
				
	}

	@Override
	protected void onResume() {

		super.onResume();
		
		DBManager db = new DBManager(this);
		if (DBManager.getDbUpdated() == 1 && db.getTransactionRowCount() > 0) 
		{
			DBManager.clearDbUpdated();
			allTransactions = getTransactions();
			
			datePosLocations = setupDatePositions(allTransactions);
			setupDateSpinner();
			
			TransactionRecord arrayDate = new TransactionRecord(this);
			TransactionRecord dateToShow = new TransactionRecord(this);
			
			dateToShow.setItemDateTime_parsedisplayDateFormat(currentDisplayDate);
			arrayDate.setItemDateTime_parsedisplayDateFormat(allDatesStringArrayList.get(0));
					
			int i=0;
			while(i < allDatesStringArrayList.size() - 1 && !arrayDate.isAfterOrEqual(dateToShow))
			{
				i++;
				arrayDate.setItemDateTime_parsedisplayDateFormat(allDatesStringArrayList.get(i));		
			}
			
			if(i < allDatesStringArrayList.size())
			{
				dateSpinner.setSelection(i);
			}
			else
			{
				dateSpinner.setSelection(allDatesStringArrayList.size() - 1);
			}
			
			currentDisplayDate = new TransactionRecord(this).getItemDateTime_displayDateFormat();
						
		} 
		else if(db.getTransactionRowCount() == 0)
		{
			transactionsToDisplay = new ArrayList<TransactionRecord>();			
			setUpRecordsArrayAdaptor(transactionsToDisplay);
		}
		db.close();

	};

	private ArrayList<TransactionRecord> getTransactions() {

		ArrayList<TransactionRecord> transactionsArray = new ArrayList<TransactionRecord>();
		DBManager db = new DBManager(this);
		Cursor transactionsCursor = db.getAllTransactionsTableDataCursor();
		
		float runningBalance = 0f;

		transactionsCursor.moveToFirst();

		while (transactionsCursor.isAfterLast() == false) {
			TransactionRecord tempTransactionRec = new TransactionRecord(this);

			tempTransactionRec.setItemDateTime_parseDBDateTimeString(transactionsCursor.getString(0));
			tempTransactionRec.setItemName(transactionsCursor.getString(1));
			tempTransactionRec.setItemType(transactionsCursor.getString(2));
			tempTransactionRec.setItemPrice_parseString(this,transactionsCursor.getString(3));
			tempTransactionRec.setID(transactionsCursor.getLong(4));
			tempTransactionRec.setHasPhoto(transactionsCursor.getInt(5));
			tempTransactionRec.setHasSound(transactionsCursor.getInt(6));
			tempTransactionRec.setNote(transactionsCursor.getString(7));
			
			runningBalance = runningBalance + tempTransactionRec.getItemPriceFloat();
			tempTransactionRec.setRunningBalance(runningBalance);

			tempTransactionRec.setNeedsUpdate();

			transactionsArray.add(tempTransactionRec);
			transactionsCursor.moveToNext();
		}

		db.close();
		return transactionsArray;
	}

	private Map<String, Integer> setupDatePositions(ArrayList<TransactionRecord> allTransactions) {
		TransactionRecord currentTransactionRecord;

		Map<String, Integer> datePosLocationsToRet = new HashMap<String, Integer>();

		String prevDate = "";

		int allTransactionsSize = allTransactions.size();

		currentTransactionRecord = allTransactions.get(0);
		String date = currentTransactionRecord.getItemDateTime_displayDateFormat();

		datePosLocationsToRet.put(new String(date + "StartPosition"), 0);
		prevDate = date;

		for (int i = 1; i < allTransactionsSize; i++) {
			currentTransactionRecord = allTransactions.get(i);

			date = currentTransactionRecord.getItemDateTime_displayDateFormat();

			if (date.equals(prevDate) == false) {
				datePosLocationsToRet.put(new String(prevDate + "EndPosition"), i);
				datePosLocationsToRet.put(new String(date + "StartPosition"), i);
				prevDate = date;
			}

		}

		datePosLocationsToRet.put(new String(date + "EndPosition"), allTransactionsSize);

		return datePosLocationsToRet;
	}

	private void setupDateSpinner() {
		dateSpinner = (Spinner) findViewById(R.id.HistoryDateSpinner);

		DBManager db = new DBManager(this);
		allDatesStringArrayList = db.getAllDatesArrayList(this);
		db.close();

		ArrayAdapter<String> dateSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allDatesStringArrayList);

		dateSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		dateSpinner.setAdapter(dateSpinnerArrayAdapter);

		dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String dateToDisplay = dateSpinner.getSelectedItem().toString();

				int startPosition = datePosLocations.get(dateToDisplay + "StartPosition");
				int endPosition = datePosLocations.get(dateToDisplay + "EndPosition");

				transactionsToDisplay = allTransactions.subList(startPosition, endPosition);

				setUpRecordsArrayAdaptor(transactionsToDisplay);
				
				setMoneyTextviews(transactionsToDisplay.get(0));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void setUpRecordsArrayAdaptor(List<TransactionRecord> transactionsToDisplay) {

		recordsListAdaptor = new HistoryListViewArrayAdaptor(this, android.R.id.list, transactionsToDisplay);

		ListView recordsListView = getListView();
		recordsListView.setAdapter(recordsListAdaptor);
	}
	
	private void setMoneyTextviews(TransactionRecord date) {
		
		DBManager db = new DBManager(this);
		
		Float moneySpentToday = Float.valueOf(db.getMoneySpentOnDay(this, date));
		Float moneyInToday = Float.valueOf(db.getMoneyInOnDay(this, date));
		db.close();
		Float moneyDifference = moneySpentToday + moneyInToday;
		
		moneySpentOnDay.setText("$" + String.format("%.2f",moneySpentToday));
		moneyInOnDay.setText("$" + String.format("%.2f",moneyInToday));
		moneyDifferenceOnDay.setText("$" + String.format("%.2f",moneyDifference));
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		
		Intent intent = new Intent(this, Act_EnterItem.class);
		
		currentDisplayDate = transactionsToDisplay.get(position).getItemDateTime_displayDateFormat();
		
		intent.putExtra(DBManager.field_TransactionsID, transactionsToDisplay.get(position).getID());
		startActivity(intent);
	}

	public void prevButtonClick(View v) {
		if (dateSpinner.getSelectedItemPosition() == 0)
			dateSpinner.setSelection(dateSpinner.getCount() - 1);
		else
			dateSpinner.setSelection(dateSpinner.getSelectedItemPosition() - 1);
	}

	public void nextButtonClick(View v) {
		if (dateSpinner.getSelectedItemPosition() == dateSpinner.getCount() - 1)
			dateSpinner.setSelection(0);
		else
			dateSpinner.setSelection(dateSpinner.getSelectedItemPosition() + 1);

	}

}
