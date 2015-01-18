package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyBankDetailInfo;
import com.damy.datatypes.STStatisticNongyaoDetailInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.MoneyBankActivity;
import com.damy.nongyao.R;

public class StatisticNongyaoDetailAdapter extends ArrayAdapter<STStatisticNongyaoDetailInfo> {
	
	public StatisticNongyaoDetailAdapter(Activity activity, List<STStatisticNongyaoDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.item_statistic_nongyao_detail, null);
		ResolutionSet._instance.iterateChild(rowView);
		STStatisticNongyaoDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_largenumber = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_largenumber);
		AutoSizeTextView txt_availdate = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_availdate);
		AutoSizeTextView txt_remaincount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_remaincount);
		AutoSizeTextView txt_salecount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_salecount);
		AutoSizeTextView txt_totalcount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_sum);
		AutoSizeTextView txt_shop = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticnongyao_detail_shop);
		
		txt_largenumber.setText(anItem.largenumber);
		txt_availdate.setText(String.valueOf(anItem.avail_date));
		txt_remaincount.setText(String.valueOf(anItem.remain_count));
		txt_salecount.setText(String.valueOf(anItem.sale_count));
		txt_totalcount.setText(String.valueOf(anItem.total_count));
		txt_shop.setText(anItem.shop_name);
		
		return rowView;
	}	

}
