package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyBankInfo;
import com.damy.datatypes.STMoneyOtherpayInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyBankActivity;
import com.damy.nongyao.R;

public class MoneyBankAdapter extends ArrayAdapter<STMoneyBankInfo> {

	private MoneyBankActivity parentAct;
	public MoneyBankAdapter(Activity activity, List<STMoneyBankInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (MoneyBankActivity) activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.item_money_bank, null);
		ResolutionSet._instance.iterateChild(rowView);
		STMoneyBankInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybank_date);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybank_money);
		AutoSizeTextView txt_bank = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybank_bank);
		AutoSizeTextView txt_sum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybank_sum);
		AutoSizeTextView txt_detail = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybank_detail);
		
		txt_date.setText(anItem.date);
		txt_money.setText(String.valueOf(anItem.money) + activity.getResources().getString(R.string.common_yuan));
		txt_bank.setText(String.valueOf(anItem.bank) + activity.getResources().getString(R.string.common_yuan));
		txt_sum.setText(String.valueOf(anItem.sum) + activity.getResources().getString(R.string.common_yuan));
        
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
	
	public void onClickDetail(int pos, View v)
	{
		parentAct.onClickDetail(pos, v);
	}
}
