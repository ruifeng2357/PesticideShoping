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
import com.damy.datatypes.STStatisticNongyaoInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyPaymentActivity;
import com.damy.nongyao.R;
import com.damy.nongyao.StatisticNongyaoActivity;

public class StatisticNongyaoAdapter extends ArrayAdapter<STStatisticNongyaoInfo> {

	private StatisticNongyaoActivity parentAct;
	
	public StatisticNongyaoAdapter(Activity activity, ArrayList<STStatisticNongyaoInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (StatisticNongyaoActivity)activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_statistic_nongyao, null);
		ResolutionSet._instance.iterateChild(rowView);
		STStatisticNongyaoInfo anItem = getItem(position);
		
		AutoSizeTextView txt_nongyao = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_nongyaoname);
		AutoSizeTextView txt_product = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_product);
		AutoSizeTextView txt_standard = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_standard);
		AutoSizeTextView txt_remaincount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_remaincount);
		AutoSizeTextView txt_salecount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_salecount);
		AutoSizeTextView txt_detail = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail);
		
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
		
		txt_nongyao.setText(anItem.catalog_name);
		txt_product.setText(anItem.product);
		txt_standard.setText(anItem.standard);
		txt_remaincount.setText(String.valueOf(anItem.remain_count));
		txt_salecount.setText(String.valueOf(anItem.sale_count));
        
		return rowView;
	}
	
	private void onClickDetail(int pos, View v)
	{
		parentAct.onClickDetail(pos, v);
	}
}
