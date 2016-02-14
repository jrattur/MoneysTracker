package com.nomadicmonkeyapps.moneys;

import java.io.File;
import java.io.IOException;

import com.nomadicmonkeyapps.moneys.R;
import com.nomadicmonkeyapps.moneys.SoundManager.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Act_Main extends FragmentActivity {

	public static final String appDir = "MoneysApp";
	public static final String photoDir = "photos";
	public static final String soundDir = "sounds";

	private static final int Photo_Intent_Requestcode = 100;
	private static final int Sound_Intent_Requestcode = 200;

	SoundManager soundman;
	
	private Button expenseBtn;
	private Button incomeBtn;
	private TextView balanceLabel;
	private TextView balanceTextView;
	private TextView moneySpentTodayLabel;
	private TextView moneySpentTodayTextView;
	private TextView incomeTodayLabel;
	private TextView incomeTodayTextview;
	private TextView differenceTodayLabel;
	private TextView differenceTodayTextview;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);

		Log.d("zzz", "Act_Main");

		DBManager.setDbUpdated();

		initialiseScreenView();

		checkProgramDirectories();

		initialiseAndCheckdbIntegrity();

		checkIfFirstRun(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		DBManager db = new DBManager(this);
		balanceTextView.setText("$" + db.getBalance());

		Float moneySpentToday = Float.valueOf(db.getMoneySpentOnDay(this, new TransactionRecord(this)));
		Float moneyInToday = Float.valueOf(db.getMoneyInOnDay(this, new TransactionRecord(this)));
		db.close();
		Float moneyDifference = moneySpentToday + moneyInToday;

		moneySpentTodayTextView.setText("$" + String.format("%.2f",moneySpentToday));
		incomeTodayTextview.setText("$" + String.format("%.2f",moneyInToday));
		differenceTodayTextview.setText("$" + String.format("%.2f",moneyDifference));
	};

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			// launchSettingsMenu();
			return true;
		case R.id.accounts_manager:
			// launchSettingsMenu();
			return true;
			/*
			 * case R.id.help: showHelp(); return true;
			 */
	/*	default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	void initialiseScreenView() {
		expenseBtn = (Button) findViewById(R.id.MainScreenExpenseButton);
		incomeBtn = (Button) findViewById(R.id.MainScreenIncomeButton);

		balanceLabel = (TextView) findViewById(R.id.MainScreenbalanceLabel);
		balanceTextView = (TextView) findViewById(R.id.MainScreenBalance);

		moneySpentTodayLabel = (TextView) findViewById(R.id.MainScreenMoneySpentTodayLabel);
		moneySpentTodayTextView = (TextView) findViewById(R.id.MainScreenSpent);

		incomeTodayLabel = (TextView) findViewById(R.id.MainScreenIncomeTodayLabel);
		incomeTodayTextview = (TextView) findViewById(R.id.MainScreenIncome);

		differenceTodayLabel = (TextView) findViewById(R.id.MainsScreenDifferenceLabel);
		differenceTodayTextview = (TextView) findViewById(R.id.MainScreenDifference);

		//Typeface font = Typeface.createFromAsset(getAssets(), "IndieFlower.ttf");

		//incomeBtn.setTypeface(font);
		//expenseBtn.setTypeface(font);
		//incomeBtn.setTextSize(30);
		//expenseBtn.setTextSize(30);
/*
		balanceLabel.setTypeface(font);
		balanceLabel.setTextSize(22);

		moneySpentTodayLabel.setTypeface(font);
		moneySpentTodayLabel.setTextSize(22);

		incomeTodayLabel.setTypeface(font);
		incomeTodayLabel.setTextSize(22);

		differenceTodayLabel.setTypeface(font);
		differenceTodayLabel.setTextSize(22);
*/
	}

	void checkProgramDirectories() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			File photodirectory = new File(Environment.getExternalStorageDirectory() + "/" + appDir + "/" + photoDir);
			if ((photodirectory.exists() && photodirectory.isDirectory()) == false) {
				photodirectory.mkdirs();
			}

			File sounddirectory = new File(Environment.getExternalStorageDirectory() + "/" + appDir + "/" + soundDir);
			if ((sounddirectory.exists() && sounddirectory.isDirectory()) == false) {
				sounddirectory.mkdirs();
			}
		}
	}

	void initialiseAndCheckdbIntegrity() {
		DBManager db;
		db = new DBManager(this);
		db.getWritableDatabase();
		db.close();
	}

	private void checkIfFirstRun(Context context) {

		SharedPreferences sharedPrefReader;
		SharedPreferences.Editor sharedPrefWriter;

		sharedPrefReader = context.getSharedPreferences("Prefs", 0);
		sharedPrefWriter = sharedPrefReader.edit();

		if (sharedPrefReader.getBoolean("NotFirstRun", false) == false) {
			sharedPrefWriter.putBoolean("NotFirstRun", true);
			sharedPrefWriter.commit();
			sharedPrefWriter.putString("CurrentAccount", "Wallet");
			sharedPrefWriter.commit();
		}
	}

	public void makeNoteAndPurchase(View v) {

		AlertDialog.Builder noteDialog = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();

		View layout = inflater.inflate(R.layout.dialog_note, null);

		noteDialog.setTitle("Note");
		noteDialog.setView(layout);

		final EditText note = (EditText) layout.findViewById(R.id.notedialogtext);

		noteDialog.setNegativeButton("Cancel", null);
		noteDialog.setPositiveButton("Ok", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				Context appContext = getApplicationContext();

				TransactionRecord notePurchase = new TransactionRecord(appContext);

				notePurchase.setNote(note.getText().toString());

				DBManager db = new DBManager(appContext);
				db.purchaseItem(appContext, notePurchase);
				db.close();

			}
		});

		noteDialog.show();

	}

	public void takePhotoAndPurchase(View v) {

		File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + appDir + "/" + photoDir + "/" + "temp");
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
				File photoFile = new File(Environment.getExternalStorageDirectory() + "/" + appDir + "/" + photoDir + "/" + "temp");

				TransactionRecord itemToBuyRecord = new TransactionRecord(this);

				itemToBuyRecord.setHasPhoto(1);

				DBManager db = new DBManager(this);
				Long rowid = db.purchaseItem(this, itemToBuyRecord);
				db.close();

				photoFile.renameTo(new File(photoFile.getParent() + "/" + rowid + ".jpg"));
			}

			break;

		case Sound_Intent_Requestcode:

			break;
		default:
			break;
		}

	};

	public void recordSoundAndPurchase(View v) {

		final Dialog soundDialog = new Dialog(this);
		soundDialog.setContentView(R.layout.dialog_audiorecorder);
		soundDialog.setTitle("Record Sound");

		final ImageButton recordButton = (ImageButton) soundDialog.findViewById(R.id.audioRecorderRecordStopButton);
		final ImageButton playStopButton = (ImageButton) soundDialog.findViewById(R.id.audioRecorderPlayStopButton);
		final Button cancelButton = (Button) soundDialog.findViewById(R.id.audioRecorderCancelButton);
		final Button okButton = (Button) soundDialog.findViewById(R.id.audioRecorderOKButton);
		final TextView mediaState = (TextView) soundDialog.findViewById(R.id.audioRecorderState);

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
			
				soundman.saveSound(getApplicationContext(), -1);
				
				soundDialog.dismiss();

			}
		});

		soundDialog.show();
		
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

	public void startEnterItemScreen(View v) {

		Intent intent = new Intent(getBaseContext(), Act_EnterItem.class);
		intent.putExtra("INCOME/EXPENSE", v.getTag().toString());
		startActivity(intent);
	}

	public void oneClickBuy(View v) {
		DBManager db = new DBManager(this);
		db.purchaseItem(this, new TransactionRecord(this));
		db.close();
		finish();
	}

}






/*Performance Testing
 * long millis = 1351769391;
	TransactionRecord asdf = new TransactionRecord(this);
	
	for(int i = 0; i<200000;i++)
	{
		double rrr = Math.random();
		
		if(rrr <0.2)
		{
			asdf.setItemName("rrrrrrrrrr");
		}
		else if(rrr >0.2)
		{
			asdf.setItemName("wwwwwwww");
		}
		else if(rrr >0.4)
		{
			asdf.setItemName("sdrfgdhdsrhrsdhtsfhfhdfhfdhtdfhdf");
		}
		else if(rrr >0.6)
		{
			asdf.setItemName("bbbbbbb");
		}
		else if(rrr >0.8)
		{
			
		}
		
		asdf.setItemPrice_Float((float) (rrr * 100));
		
		millis = millis + 17280000;
		asdf.setItemDateTime_milliseconds(millis);
		
		db.purchaseItem(this, asdf);
	}
	
 */
