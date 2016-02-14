package com.nomadicmonkeyapps.moneys;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;

public class SoundManager {

	MediaRecorder mediaRecorder;
	MediaPlayer mediaPlayer;

	String audiofile;
	
	Bundle extras;
	
	private enum state {
		START, RECORDING, STOPPED, PLAYING
	}

	state currentstate;

	public static enum events {
		REC, STOP, PLAY, CANCEL, OK
	}

	public SoundManager() {

		currentstate = state.START;

		setupPlayerandRecorder();
		
		audiofile = Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.soundDir + "/" + "temp.3gp";
	}

	public SoundManager(String audiofile) {

		currentstate = state.STOPPED;

		setupPlayerandRecorder();
		
		this.audiofile = audiofile;
	}
	
	private void setupPlayerandRecorder() {
		mediaRecorder = new MediaRecorder();
		mediaPlayer = new MediaPlayer();
	}

	public int state_machine(events event) {

		int buttonsToDisplay=0; //hex code of buttons to display with order of record, play/stop, cancel, ok
		// record to be updated [x] state[ss] rec[r],play[01],stop[10],cancel[c],ok[o]
		// 00 START, 01 RECORDING, 10 STOPPED, 11 PLAYING
		// ussrppco
		
		switch (currentstate) {
		case START:

			if (event == events.REC) {
				startRecording();
				buttonsToDisplay = 0x0101010;
				currentstate = state.RECORDING;
			}

			if (event == events.CANCEL) {
				cancel();
			}

			break;

		case RECORDING:

			if (event == events.STOP) {
				stopRecording();
				buttonsToDisplay = 0x1010111;
				currentstate = state.STOPPED;
			}
			
			if (event == events.CANCEL) {
				cancel();
			}
			
			break;

		case STOPPED:

			if (event == events.REC) {
				startRecording();
				buttonsToDisplay = 0x0101010;
				currentstate = state.RECORDING;
			}

			if (event == events.PLAY) {
				startPlaying();
				buttonsToDisplay = 0x1101010;
				currentstate = state.PLAYING;
			}

			if (event == events.OK)
				ok();

			if (event == events.CANCEL)
				cancel();

			break;

		case PLAYING:

			if (event == events.STOP) {
				stopPLaying();
				buttonsToDisplay = 0x1010111;
				currentstate = state.STOPPED;
			}

			if (event == events.CANCEL) {
				cancel();
			}
			
			break;

		default:
			break;
		}

		return buttonsToDisplay;
		
	}

	private void startRecording() {

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		audiofile = Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.soundDir + "/" + "temp.3gp";
		mediaRecorder.setOutputFile(audiofile);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaRecorder.start();

	}

	private void stopRecording() {

		mediaRecorder.stop();

	}

	private void startPlaying() {

		try {
			mediaPlayer.setDataSource(audiofile);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaPlayer.start();

	}

	private void stopPLaying() {

		mediaPlayer.stop();
		mediaPlayer.reset();

	}

	/*
	 * private void initialiseAndCheckExtras() { extras =
	 * getIntent().getExtras();
	 * 
	 * if (extras.containsKey(DBManager.field_TransactionsID) &&
	 * extras.getInt(DBManager.field_TransactionsHasSound) == 1) { mFileName =
	 * Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" +
	 * Act_Main.soundDir + "/" +
	 * String.valueOf(extras.getLong(DBManager.field_TransactionsID)) + ".3gp";
	 * } else { mFileName = Environment.getExternalStorageDirectory() + "/" +
	 * Act_Main.appDir + "/" + Act_Main.soundDir + "/" + "temp.3gp"; }
	 * 
	 * if (extras.containsKey(DBManager.field_TransactionsID) &&
	 * extras.getInt(DBManager.field_TransactionsHasSound) == 1) {
	 * playStopButton.setImageResource(R.drawable.ic_audio_play);
	 * playStopButton.setTag(R.drawable.ic_audio_play);
	 * playStopButton.setVisibility(View.VISIBLE); } }
	 */

	public void cancel() {

		if (currentstate == state.RECORDING) {
			mediaRecorder.stop();
		}
		if (currentstate == state.PLAYING) {
			mediaPlayer.stop();
		}

		mediaRecorder.release();
		mediaPlayer.release();

	}

	public void ok() {

		mediaRecorder.release();
		mediaPlayer.release();
	}

	public void saveSound(Context context, long rowid) {

		TransactionRecord itemRecord = new TransactionRecord(context);

		DBManager db = new DBManager(context);
		if (rowid != -1) {

			itemRecord = db.getTransaction(context, rowid);
			itemRecord.setHasSound(1);
			rowid = db.updateItem(context, itemRecord, rowid);
		} else {
			itemRecord.setHasSound(1);
			rowid = db.purchaseItem(context, itemRecord);
		}
		db.close();

		String soundFileString = Environment.getExternalStorageDirectory() + "/" + Act_Main.appDir + "/" + Act_Main.soundDir + "/" + "temp.3gp";

		File soundFile = new File(soundFileString);

		soundFile.renameTo(new File(soundFile.getParent() + "/" + rowid + ".3gp"));

	}
	
	public MediaPlayer getActiveMediaPlayer() {
		return mediaPlayer;
	}
}
