package com.damy.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyOtherpayInfo;
import com.damy.datatypes.STMoneyPaymentDetailInfo;
import com.damy.datatypes.STMoneyPaymentInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyPaymentActivity;
import com.damy.nongyao.R;

public class MoneyPaymentDetailAdapter extends ArrayAdapter<STMoneyPaymentDetailInfo> {

	private MoneyPaymentActivity parentAct;
	
	public MoneyPaymentDetailAdapter(Activity activity, ArrayList<STMoneyPaymentDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (MoneyPaymentActivity)activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_payment_detail, null);
		ResolutionSet._instance.iterateChild(rowView);
		
		STMoneyPaymentDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypaymentdetail_date);
		AutoSizeTextView txt_ticketnum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypaymentdetail_ticketnum);
		AutoSizeTextView txt_content = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypaymentdetail_content);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypaymentdetail_money);
		AutoSizeTextView txt_remark = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypaymentdetail_remark);
		
		if (anItem.date != null && anItem.date != "null")
			txt_date.setText(anItem.date);
		if (anItem.ticketnum != null && anItem.ticketnum != "null")
			txt_ticketnum.setText(anItem.ticketnum);
		String strMoney = (anItem.type == 0 ? activity.getResources().getString(R.string.common_givemoney) : activity.getResources().getString(R.string.common_takemoney)) + ": " + String.valueOf(anItem.price) + activity.getResources().getString(R.string.common_yuan);
		if (strMoney != null && strMoney != "null")
			txt_money.setText(strMoney);
		if (anItem.content != null && anItem.content != "null")
			txt_content.setText(anItem.content);
		if (anItem.reason != null && anItem.reason != "null")
			txt_remark.setText(anItem.reason);		
        
		return rowView;
	}	

}
