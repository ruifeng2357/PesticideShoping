package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyReportInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyReportActivity;
import com.damy.nongyao.R;

public class MoneyReportAdapter extends ArrayAdapter<STMoneyReportInfo> {
	
	private MoneyReportActivity parentAct;

	public MoneyReportAdapter(Activity activity, List<STMoneyReportInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (MoneyReportActivity) activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_report, null);
		ResolutionSet._instance.iterateChild(rowView);
		
		STMoneyReportInfo anItem = getItem(position);
		
		AutoSizeTextView txt_date = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_date);
		AutoSizeTextView txt_income = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_income);
		AutoSizeTextView txt_originprice = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_originprice);
		AutoSizeTextView txt_restmoney = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_restmoney);
		AutoSizeTextView txt_earn = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_earn);
		AutoSizeTextView txt_detail = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail);
		
		txt_date.setText(anItem.date);
		txt_income.setText(String.valueOf(anItem.incoming) + activity.getResources().getString(R.string.common_yuan));
		txt_originprice.setText(String.valueOf(anItem.originprice) + activity.getResources().getString(R.string.common_yuan));
		txt_restmoney.setText(String.valueOf(anItem.restmoney) + activity.getResources().getString(R.string.common_yuan));
		txt_earn.setText(String.valueOf(anItem.earn) + activity.getResources().getString(R.string.common_yuan));

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
