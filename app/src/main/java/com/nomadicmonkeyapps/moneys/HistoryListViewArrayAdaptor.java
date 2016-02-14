package com.nomadicmonkeyapps.moneys;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nomadicmonkeyapps.moneys.R;

public class HistoryListViewArrayAdaptor extends ArrayAdapter<TransactionRecord> {

	private Activity activity;
	public List<TransactionRecord> transactionRecords;

	public HistoryListViewArrayAdaptor(Activity activity, int textViewResourceId, List<TransactionRecord> transactionRecords) {

		super(activity, textViewResourceId, transactionRecords);
		this.transactionRecords = transactionRecords;
		this.activity = activity;
	}

	public static class ViewHolder {
		public TextView itemName;
		public TextView itemDate;
		public TextView itemPrice;
		public TextView itemTime;
		public TextView itemType;
		public ImageView incExpbtn;
		public TextView balance;
		public ImageView hasPhotoLabel;
		public ImageView hasSoundLabel;
		public ImageView hasNoteLabel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder viewHolder;

		if (view == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.historylisttemplate, null);

			viewHolder = new ViewHolder();
			viewHolder.itemDate = (TextView) view.findViewById(R.id.HistoryListTemplateDate);
			viewHolder.itemTime = (TextView) view.findViewById(R.id.HistoryListTemplateTime);
			viewHolder.itemName = (TextView) view.findViewById(R.id.HistoryListTemplateName);
			viewHolder.itemType = (TextView) view.findViewById(R.id.HistoryListTemplateType);
			viewHolder.incExpbtn = (ImageView) view.findViewById(R.id.HistoryListTemplateIncomeExpense);
			viewHolder.itemPrice = (TextView) view.findViewById(R.id.HistoryListTemplatePrice);
			viewHolder.balance = (TextView) view.findViewById(R.id.HistoryListTemplateBalance);
			viewHolder.hasPhotoLabel = (ImageView) view.findViewById(R.id.HistoryListTemplatePhotoLabel);
			viewHolder.hasSoundLabel = (ImageView) view.findViewById(R.id.HistoryListTemplateSoundLabel);
			viewHolder.hasNoteLabel = (ImageView) view.findViewById(R.id.HistoryListTemplateNoteLabel);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		final TransactionRecord currentPositionTransactionRecord = transactionRecords.get(position);

		viewHolder.itemName.setText(currentPositionTransactionRecord.getItemNameString());
		viewHolder.itemDate.setText(currentPositionTransactionRecord.getItemDateTime_displayDateFormat());
		viewHolder.itemTime.setText(currentPositionTransactionRecord.getItemDateTime_displayTimeFormat());
		viewHolder.itemType.setText(currentPositionTransactionRecord.getItemType());
		viewHolder.balance.setText(currentPositionTransactionRecord.getRunningBalanceString());

		if (currentPositionTransactionRecord.getItemPriceFloat() < 0.0) {
			viewHolder.incExpbtn.setImageResource(R.drawable.minusncon5);
			viewHolder.itemPrice.setText(String.format("%.2f",currentPositionTransactionRecord.getItemPriceFloat() * -1.0));
		} else {
			viewHolder.incExpbtn.setImageResource(R.drawable.plusincon5);
			viewHolder.itemPrice.setText(String.format("%.2f", currentPositionTransactionRecord.getItemPriceFloat()));
		}

		if (currentPositionTransactionRecord.getNeedsUpdate() == 1) {
			view.setBackgroundResource(R.drawable.bg_needsupdate);
		} else {
			view.setBackgroundResource(R.drawable.bg_main);
		}

		if (currentPositionTransactionRecord.getHasPhoto() == 1)
		{
			viewHolder.hasPhotoLabel.setVisibility(View.VISIBLE);
		}
		else 
		{
			viewHolder.hasPhotoLabel.setVisibility(View.INVISIBLE);
		}
		
		if (currentPositionTransactionRecord.getHasSound() == 1)
		{
			viewHolder.hasSoundLabel.setVisibility(View.VISIBLE);
		}
		else 
		{
			viewHolder.hasSoundLabel.setVisibility(View.INVISIBLE);
		}
		
		if (!currentPositionTransactionRecord.getNote().equals(""))
		{
			viewHolder.hasNoteLabel.setVisibility(View.VISIBLE);
		}
		else 
		{
			viewHolder.hasNoteLabel.setVisibility(View.INVISIBLE);
		}
		
		return view;
	}

}
