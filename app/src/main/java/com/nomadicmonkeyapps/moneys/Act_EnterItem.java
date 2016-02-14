package com.nomadicmonkeyapps.moneys;

import java.io.File;
import java.io.IOException;

import com.nomadicmonkeyapps.moneys.R;
import com.nomadicmonkeyapps.moneys.SoundManager.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Act_EnterItem extends Activity {

	private static final int Photo_Intent_Requestcode = 100;
	private static final int Sound_Intent_Requestcode = 200;

	TransactionRecord itemTransactionRecord;

	Bundle extras;

	EditText itemDateBtn;
	Button buyOrUpdateButton;
	Button deleteRecordButton;

	ImageView hasPhotoindicator;
	ImageView hasNoteindicator;
	ImageView hasSoundindicator;

	EditText itemName;
	EditText itemPrice;
	EditText itemQty;
	Button incomeOrExpenseText;
	RadioGroup timeOfDay;
	RadioButton morning, afternoon, night;
	RadioButton[] itemType;
	private EditText itemTimeBtn;

	ImageButton plusMinusIcon;

	SoundManager soundman;

	int deletePhotoOnUpdate;
	int updateSoundOnUpdate;
	int updatePhoto;
	int updateNote;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_enteritem);

		Log.d("zzz", "Act_EnterItem");

		deletePhotoOnUpdate = 0;
		updatePhoto = 0;
		updateNote = 0;
		updateSoundOnUpdate = 0;

		InitialiseScreenViews();

		setupTransactionRecord();

		itemPrice.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
				String price = itemPrice.getText().toString();
				
				if(price.equals("") || price.equals(".") || price.equals(","))
				{
					price = "0";
				}
				
				itemTransactionRecord.setItemPrice_parseString(getApplicationContext(),price);
			}
		});

	}

	void InitialiseScreenViews() {

		itemTimeBtn = (EditText) findViewById(R.id.newbuyscreen_TIME_Button);
		itemDateBtn = (EditText) findViewById(R.id.newbuyscreen_DATE_Button);
		itemName = (EditText) findViewById(R.id.newbuyscreen_ITEMNAME_autocompletetextbox);
		itemPrice = (EditText) findViewById(R.id.newbuyscreen_PRICE_textbox);
		incomeOrExpenseText = (Button) findViewById(R.id.newbuyscreen_INCENC_textbox);
		buyOrUpdateButton = (Button) findViewById(R.id.newbuyscreen_BUY_or_UPDATE);
		deleteRecordButton = (Button) findViewById(R.id.newbuyscreen_deleteTransaction);
		hasPhotoindicator = (ImageView) findViewById(R.id.newbuyscreen_hasPhoto);
		hasNoteindicator = (ImageView) findViewById(R.id.newbuyscreen_hasNote);
		hasSoundindicator = (ImageView) findViewById(R.id.newbuyscreen_hasSound);
		plusMinusIcon = (ImageButton) findViewById(R.id.newbuyscreen_plusMinus_icon);
	}

	private void setupTransactionRecord() {

		itemTransactionRecord = new TransactionRecord(this);

		extras = getIntent().getExtras();

		if (extras.containsKey(DBManager.field_TransactionsID)) {

			deleteRecordButton.setVisibility(View.VISIBLE);
			buyOrUpdateButton.setText("UPDATE");

			DBManager db = new DBManager(this);
			itemTransactionRecord = db.getTransaction(this, extras.getLong(DBManager.field_TransactionsID));
			db.close();

			if (itemTransactionRecord.getItemPriceFloat() > 0.0) {
				incomeOrExpenseText.setText("INCOME");
				plusMinusIcon.setImageResource(R.drawable.plusincon4);
			}

			if (itemTransactionRecord.getItemPriceFloat() < 0.0) {
				itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() * -1);
			}

			itemName.setText(itemTransactionRecord.getItemNameString());

			if (itemTransactionRecord.getHasPhoto() == 1)
				hasPhotoindicator.setVisibility(View.VISIBLE);
			else
				hasPhotoindicator.setVisibility(View.INVISIBLE);

			if (!itemTransactionRecord.getNote().equals(""))
				hasNoteindicator.setVisibility(View.VISIBLE);
			else
				hasNoteindicator.setVisibility(View.INVISIBLE);

			if (itemTransactionRecord.getHasSound() == 1)
				hasSoundindicator.setVisibility(View.VISIBLE);
			else
				hasSoundindicator.setVisibility(View.INVISIBLE);

			itemPrice.setText(itemTransactionRecord.getItemPriceDisplayString());

			Log.d("zzz",
					"Transaction Displayed, " + "ID = " + extras.getLong(DBManager.field_TransactionsID) + " Name = "
							+ itemTransactionRecord.getItemNameString() + " DateTime = " + itemTransactionRecord.getItemDateTime_dbDateTimeString()
							+ " Type = " + itemTransactionRecord.getItemType() + " Price = " + itemTransactionRecord.getItemPriceDisplayString() + " HasPhoto = "
							+ itemTransactionRecord.getHasPhoto() + " HsaSound = " + itemTransactionRecord.getHasSound() + " Note = "
							+ itemTransactionRecord.getNote());

		} else {

			incomeOrExpenseText.setText(extras.getString("INCOME/EXPENSE"));

			if (incomeOrExpenseText.getText().equals("INCOME")) {
				plusMinusIcon.setImageResource(R.drawable.plusincon4);
			}
			if (incomeOrExpenseText.getText().equals("EXPENSE")) {
				plusMinusIcon.setImageResource(R.drawable.minusncon4);
			}
			hasPhotoindicator.setVisibility(View.INVISIBLE);
			hasNoteindicator.setVisibility(View.INVISIBLE);
			hasSoundindicator.setVisibility(View.INVISIBLE);
		}

		itemDateBtn.setText(itemTransactionRecord.getItemDateTime_displayDateFormat());
		itemTimeBtn.setText(itemTransactionRecord.getItemDateTime_displayTimeFormat());

	}

	public void dateButton(View v) {
		showDialog(DATE_DIALOG_ID);
	}

	public void timeButton(View v) {
		showDialog(TIME_DIALOG_ID);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, itemTransactionRecord.getItemDateTime_year(), itemTransactionRecord.getItemDateTime_month(),
					itemTransactionRecord.getItemDateTime_day());
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, itemTransactionRecord.getItemDateTime_hour(), itemTransactionRecord.getItemDateTime_minute(),
					false);

		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			itemTransactionRecord.setItemDateTime_Minute_Hour_Day_Month_Year(-1, -1, dayOfMonth, monthOfYear, year);
			itemDateBtn.setText(itemTransactionRecord.getItemDateTime_displayDateFormat());
		}
	};
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			itemTransactionRecord.setItemDateTime_Minute_Hour_Day_Month_Year(minute, hourOfDay, -1, -1, -1);
			itemTimeBtn.setText(itemTransactionRecord.getItemDateTime_displayTimeFormat());
		}
	};

	public void nextDateButton(View v) {
		itemTransactionRecord.setItemDateTime_nextday();
		itemDateBtn.setText(itemTransactionRecord.getItemDateTime_displayDateFormat());
	}

	public void prevDateButton(View v) {
		itemTransactionRecord.setItemDateTime_prevday();
		itemDateBtn.setText(itemTransactionRecord.getItemDateTime_displayDateFormat());
	}

	public void addPrice1(View v) {
		itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() + 1f);
		itemPrice.setText(itemTransactionRecord.getItemPriceDisplayString());
	}

	public void addPrice2(View v) {
		itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() + 5f);
		itemPrice.setText(itemTransactionRecord.getItemPriceDisplayString());

	}

	public void addPrice3(View v) {
		itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() + 10f);
		itemPrice.setText(itemTransactionRecord.getItemPriceDisplayString());

	}

	public void addPrice4(View v) {
		itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() + 100f);
		itemPrice.setText(itemTransactionRecord.getItemPriceDisplayString());

	}

	public void incEncButton(View v) {

		if (incomeOrExpenseText.getText().equals("EXPENSE")) {
			incomeOrExpenseText.setText("INCOME");
			plusMinusIcon.setImageResource(R.drawable.plusincon4);
		} else {
			incomeOrExpenseText.setText("EXPENSE");
			plusMinusIcon.setImageResource(R.drawable.minusncon4);
		}
	}

	public void deleteTransaction(View v) {

		DBManager db = new DBManager(this);

		db.deleteItem(this, itemTransactionRecord, extras.getLong(DBManager.field_TransactionsID));

		db.close();

		finish();

	}

	public void onNoteButton(View v) {

		AlertDialog.Builder noteDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		View layout = inflater.inflate(R.layout.dialog_note, null);

		noteDialog.setTitle("Note");
		noteDialog.setView(layout);

		final EditText note = (EditText) layout.findViewById(R.id.notedialogtext);

		note.setText(itemTransactionRecord.getNote());

		noteDialog.setNegativeButton("Cancel", null);
		noteDialog.setPositiveButton("Ok", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				itemTransactionRecord.setNote(note.getText().toString());
				Toast displayMessage = Toast.makeText(getApplicationContext(), "Note will be saved on Update", Toast.LENGTH_SHORT);
				displayMessage.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
				displayMessage.show();

				if (note.getText().toString().equals(""))
					hasNoteindicator.setVisibility(View.INVISIBLE);
				else
					hasNoteindicator.setVisibility(View.VISIBLE);

			}
		});

		noteDialog.show();

	}

	public void onPhotoButton(View v) {

		if (itemTransactionRecord.getHasPhoto() == 1) {

			final Dialog photoDialog = new Dialog(this);
			photoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

			photoDialog.setContentView(R.layout.dialog_openeditphoto);

			Button newPhoto = (Button) photoDialog.findViewById(R.id.dialog_openeditphoto_takenewphoto);
			Button openPhotobtn = (Button) photoDialog.findViewById(R.id.dialog_openeditphoto_openphoto);
			Button deletePhoto = (Button) photoDialog.findViewById(R.id.dialog_openeditphoto_deletephoto);

			newPhoto.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					takeNewPhoto(v);

					photoDialog.dismiss();
				}
			});

			openPhotobtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openPhoto(v);

				}
			});

			deletePhoto.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					hasPhotoindicator.setVisibility(View.INVISIBLE);
					deletePhotoOnUpdate = 1;
					updatePhoto = 0;

					if (extras.containsKey(DBManager.field_TransactionsID)) {
						{
							Toast displayMessage = Toast.makeText(getApplicationContext(), "Photo will be deleted on Update", Toast.LENGTH_SHORT);
							displayMessage.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
							displayMessage.show();
						}
						photoDialog.dismiss();
					}
				}
			});

			photoDialog.show();

		} else {
			takeNewPhoto(v);
		}
	}

	public void takeNewPhoto(View v) {

		updatePhoto = 1;
		deletePhotoOnUpdate = 0;

		File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/" + "temp");
		try {
			photoFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Uri photoFileUri = Uri.fromFile(photoFile);
		Intent photoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
		startActivityForResult(photoIntent, Photo_Intent_Requestcode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Photo_Intent_Requestcode:

			if (resultCode == RESULT_OK) {

				itemTransactionRecord.setHasPhoto(1);
				hasPhotoindicator.setVisibility(View.VISIBLE);

			}
			break;

		case Sound_Intent_Requestcode:

			if (resultCode == 1) {

				itemTransactionRecord.setHasSound(1);
				hasSoundindicator.setVisibility(View.VISIBLE);

			}
			break;

		}

	}

	public void openPhoto(View v) {

		String photoFilePath = Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/"
				+ extras.getLong(DBManager.field_TransactionsID) + ".jpg";

		Intent i = new Intent(this, Act_ImageViewer.class);

		i.putExtra("photopath", photoFilePath);

		startActivity(i);
	}

	public void deletePhoto(View v) {

	}

	public void onSoundButton(View v) {

		final Dialog soundDialog = new Dialog(this);
		soundDialog.setContentView(R.layout.dialog_audiorecorder);
		soundDialog.setTitle("Record Sound");

		final ImageButton recordButton = (ImageButton) soundDialog.findViewById(R.id.audioRecorderRecordStopButton);
		final ImageButton playStopButton = (ImageButton) soundDialog.findViewById(R.id.audioRecorderPlayStopButton);
		final Button cancelButton = (Button) soundDialog.findViewById(R.id.audioRecorderCancelButton);
		final Button okButton = (Button) soundDialog.findViewById(R.id.audioRecorderOKButton);
		final TextView mediaState = (TextView) soundDialog.findViewById(R.id.audioRecorderState);

		if (itemTransactionRecord.getHasSound() == 1 && updateSoundOnUpdate == 0) {
			soundman = new SoundManager(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.soundDir + "/"
					+ extras.getLong(DBManager.field_TransactionsID) + ".3gp");
			playStopButton.setVisibility(View.VISIBLE);
			playStopButton.setTag("PLAY");
		} else if (updateSoundOnUpdate == 1) {
			soundman = new SoundManager(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.soundDir + "/" + "temp.3gp");
			playStopButton.setVisibility(View.VISIBLE);
			playStopButton.setTag("PLAY");
		} else
			soundman = new SoundManager();

		soundman.getActiveMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				int soundButtonsToDisplay = soundman.state_machine(events.STOP);

				setAudioDisplays(soundButtonsToDisplay, recordButton, playStopButton, cancelButton, okButton, mediaState);

			}
		});

		recordButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				int soundButtonsToDisplay = soundman.state_machine(events.REC);
				setAudioDisplays(soundButtonsToDisplay, recordButton, playStopButton, cancelButton, okButton, mediaState);

			}
		});

		playStopButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				int soundButtonsToDisplay = 0;

				if (playStopButton.getTag().toString().equals("PLAY"))
					soundButtonsToDisplay = soundman.state_machine(events.PLAY);

				if (playStopButton.getTag().toString().equals("STOP"))
					soundButtonsToDisplay = soundman.state_machine(events.STOP);

				setAudioDisplays(soundButtonsToDisplay, recordButton, playStopButton, cancelButton, okButton, mediaState);
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				soundman.state_machine(events.CANCEL);

				soundDialog.dismiss();

			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				soundman.state_machine(events.OK);
				updateSoundOnUpdate = 1;
				soundDialog.dismiss();

			}
		});

		soundDialog.show();

		/*
		 * Intent i = new Intent(getBaseContext(), Act_RecordSound.class);
		 * 
		 * if (extras.containsKey(DBManager.field_TransactionsID)) {
		 * i.putExtra(DBManager.field_TransactionsID,
		 * extras.getLong(DBManager.field_TransactionsID)); }
		 * 
		 * i.putExtra(DBManager.field_TransactionsHasSound,
		 * itemTransactionRecord.getHasSound());
		 * 
		 * startActivityForResult(i, Sound_Intent_Requestcode);
		 */
	}

	public void setAudioDisplays(int soundButtonsToDisplay, ImageButton recordButton, ImageButton playStopButton, Button cancelButton, Button okButton,
			TextView mediaState) {

		if ((soundButtonsToDisplay & 0x10000) == 0x10000)
			recordButton.setVisibility(View.VISIBLE);
		else
			recordButton.setVisibility(View.INVISIBLE);

		if ((soundButtonsToDisplay & 0x00100) == 0x00100) {
			playStopButton.setImageResource(R.drawable.ic_audio_play);
			playStopButton.setVisibility(View.VISIBLE);
			playStopButton.setTag("PLAY");
		}

		if ((soundButtonsToDisplay & 0x01000) == 0x01000) {
			playStopButton.setImageResource(R.drawable.ic_audio_stop);
			playStopButton.setVisibility(View.VISIBLE);
			playStopButton.setTag("STOP");
		}

		if ((soundButtonsToDisplay & 0x01100) == 0x01100) {
			playStopButton.setVisibility(View.INVISIBLE);
		}

		if ((soundButtonsToDisplay & 0x00010) == 0x00010)
			cancelButton.setVisibility(View.VISIBLE);
		else
			cancelButton.setVisibility(View.INVISIBLE);

		if ((soundButtonsToDisplay & 0x00001) == 0x00001)
			okButton.setVisibility(View.VISIBLE);
		else
			okButton.setVisibility(View.INVISIBLE);

		if ((soundButtonsToDisplay & 0x0100000) == 0x0100000)
			mediaState.setText("RECORDING");
		if ((soundButtonsToDisplay & 0x1000000) == 0x1000000)
			mediaState.setText("STOPPED");
		if ((soundButtonsToDisplay & 0x1100000) == 0x1100000)
			mediaState.setText("PLAYING");

	}

	public void buyOrUpdate(View v) {

		long rowid;

		itemTransactionRecord.setItemName(itemName.getText().toString());

		if (incomeOrExpenseText.getText().equals("EXPENSE")) {
			itemTransactionRecord.setItemPrice_Float(itemTransactionRecord.getItemPriceFloat() * -1);
		}

		DBManager db = new DBManager(this);

		if (extras.containsKey(DBManager.field_TransactionsID)) {

			if (deletePhotoOnUpdate == 1) {
				File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/"
						+ extras.getLong(DBManager.field_TransactionsID) + ".jpg");

				photoFile.delete();

				itemTransactionRecord.setHasPhoto(0);
			}

			rowid = db.updateItem(this, itemTransactionRecord, extras.getLong(DBManager.field_TransactionsID));
			db.close();

			if (updatePhoto == 1) {
				File oldPhotoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/" + rowid + ".jpg");

				if (oldPhotoFile.exists())
					oldPhotoFile.delete();

				File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/" + "temp");
				photoFile.renameTo(new File(photoFile.getParent() + "/" + rowid + ".jpg"));
			}

			if (updateSoundOnUpdate == 1)
				soundman.saveSound(this, rowid);

		} else {
			rowid = db.purchaseItem(this, itemTransactionRecord);

			db.close();

			if (updatePhoto == 1) {
				File oldPhotoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/" + rowid + ".jpg");

				if (oldPhotoFile.exists())
					oldPhotoFile.delete();

				File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.photoDir + "/" + "temp");
				photoFile.renameTo(new File(photoFile.getParent() + "/" + rowid + ".jpg"));
			}

			if (updateSoundOnUpdate == 1)
				soundman.saveSound(this, -1);
		}

		finish();

	}
	
	@Override
	public void onBackPressed() {
		
		if(soundman != null)
			soundman.state_machine(events.CANCEL);
		
		super.onBackPressed();
		
		
	}

}
