package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyOtherpayInfo;
import com.damy.datatypes.STMoneyPaymentHistoryInfo;
import com.damy.datatypes.STMoneyPaymentInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class MoneyPaymentHistoryAdapter extends ArrayAdapter<STMoneyPaymentHistoryInfo> {

	public MoneyPaymentHistoryAdapter(Activity activity, List<STMoneyPaymentHistoryInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_payment_history, null);
		ResolutionSet._instance.iterateChild(rowView);
		STMoneyPaymentHistoryInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_money_payment_history_date);
		AutoSizeTextView txt_customer = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_money_payment_history_customer);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_money_payment_history_money);
		AutoSizeTextView txt_restmoney = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_money_payment_history_restmoney);
		AutoSizeTextView txt_remark = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_money_payment_history_remark);
		
		if (anItem.date != null && anItem.date != "null")
			txt_date.setText(anItem.date);
		if (anItem.customer_name != null && anItem.customer_name != "null")
			txt_customer.setText(anItem.customer_name);
		txt_money.setText(String.valueOf(anItem.price));
		txt_restmoney.setText(String.valueOf(anItem.change));
		if (anItem.remark != null && anItem.remark != "null")
			txt_remark.setText(anItem.remark);
		
		return rowView;
	}
}
