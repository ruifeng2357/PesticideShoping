package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyOtherpayInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class MoneyOtherpayAdapter extends ArrayAdapter<STMoneyOtherpayInfo> {

	public MoneyOtherpayAdapter(Activity activity, List<STMoneyOtherpayInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_otherpay, null);
		ResolutionSet._instance.iterateChild(rowView);
		
		STMoneyOtherpayInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyotherpay_date);
		AutoSizeTextView txt_type = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyotherpay_type);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyotherpay_money);
		AutoSizeTextView txt_reason = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyotherpay_reason);
		
		if (anItem.date != null && anItem.date != "null")
			txt_date.setText(anItem.date);
		txt_type.setText((anItem.type == 0 ? activity.getResources().getString(R.string.common_outgo) : activity.getResources().getString(R.string.common_income) ));
		txt_money.setText(Float.toString(anItem.price) + activity.getResources().getString(R.string.common_yuan));
		if (anItem.reason != null && anItem.reason != "null")
			txt_reason.setText(anItem.reason);
        
		return rowView;
	}
}
