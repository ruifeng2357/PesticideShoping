package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyBankDetailInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyBankActivity;
import com.damy.nongyao.R;

public class MoneyBankDetailAdapter extends ArrayAdapter<STMoneyBankDetailInfo> {

	
	public MoneyBankDetailAdapter(Activity activity, List<STMoneyBankDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.item_money_bank_detail, null);
		ResolutionSet._instance.iterateChild(rowView);
		STMoneyBankDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_ticket = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybankdetail_ticket);
		AutoSizeTextView txt_paytype = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybankdetail_paytype);
		AutoSizeTextView txt_type = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybankdetail_type);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybankdetail_money);
		AutoSizeTextView txt_remark = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneybankdetail_remark);
		
		txt_ticket.setText(anItem.ticket);
		txt_paytype.setText(anItem.paytype);
		txt_type.setText(anItem.type);
		txt_money.setText(String.valueOf(anItem.money) + activity.getResources().getString(R.string.common_yuan));
		txt_remark.setText(anItem.remark);        
			
		
		return rowView;
	}	

}
