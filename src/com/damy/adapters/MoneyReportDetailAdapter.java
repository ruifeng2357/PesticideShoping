package com.damy.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.damy.datatypes.STMoneyReportDetailInfo;
import com.damy.datatypes.STMoneyReportInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyReportActivity;
import com.damy.nongyao.R;
import com.damy.nongyao.R.id;
import com.damy.nongyao.SaleCatalogActivity;

public class MoneyReportDetailAdapter extends ArrayAdapter<STMoneyReportDetailInfo> {
	
	private MoneyReportActivity parentAct;

	public MoneyReportDetailAdapter(Activity activity, ArrayList<STMoneyReportDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (MoneyReportActivity) activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_money_report_detail, null);
		ResolutionSet._instance.iterateChild(rowView);
		STMoneyReportDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_no = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail_catalognum);
		AutoSizeTextView txt_description = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail_description);
		AutoSizeTextView txt_money = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail_money);
		AutoSizeTextView txt_username = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail_operator);
		AutoSizeTextView txt_remark = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_moneyreport_detail_remark);		
		
				
		txt_no.setText(String.valueOf(anItem.no));
		txt_description.setText(anItem.description);
		txt_money.setText(String.valueOf(anItem.money) + activity.getResources().getString(R.string.common_yuan));
		txt_username.setText(anItem.username);	
		txt_remark.setText(anItem.reason);

		return rowView;
	}	

}
