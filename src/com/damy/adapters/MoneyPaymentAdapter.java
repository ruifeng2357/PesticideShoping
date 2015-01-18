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
import com.damy.datatypes.STMoneyPaymentInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyPaymentActivity;
import com.damy.nongyao.R;

public class MoneyPaymentAdapter extends ArrayAdapter<STMoneyPaymentInfo> {

	private MoneyPaymentActivity parentAct;
	
	public MoneyPaymentAdapter(Activity activity, ArrayList<STMoneyPaymentInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (MoneyPaymentActivity)activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_payment, null);
		ResolutionSet._instance.iterateChild(rowView);
		STMoneyPaymentInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypayment_date);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypayment_money);
		AutoSizeTextView txt_customername = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypayment_customername);
		AutoSizeTextView txt_detail = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneypayment_detail);
		
		if (anItem.date != null && anItem.date != "null")
			txt_date.setText(anItem.date);
		String strMoney = (anItem.type == 0 ? activity.getResources().getString(R.string.common_givemoney) : activity.getResources().getString(R.string.common_takemoney)) + ": " + String.valueOf(anItem.price) + activity.getResources().getString(R.string.common_yuan);
		if (strMoney != null && strMoney != "null")
			txt_money.setText(strMoney);
		if (anItem.customer_name != null && anItem.customer_name != "null")
			txt_customername.setText(anItem.customer_name);
		//txt_customerphone.setText(anItem.customer_phone);
		
		if ( (Integer)txt_detail.getTag() == null )
		{
			txt_detail.setTag(position);
			txt_detail.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		int pos = (Integer)v.getTag();
	        		onClickDetail(pos, v);
	        	}
	        });
		}	
		
        
		return rowView;
	}
	
	private void onClickDetail(int pos, View v)
	{		
		parentAct.onClickDetail(pos, v);
	}
}
