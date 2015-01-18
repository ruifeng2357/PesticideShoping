package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STSaleSearchDetailInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;
import com.damy.nongyao.SaleRejectActivity;

public class SaleSearchDetailAdapter extends ArrayAdapter<STSaleSearchDetailInfo> {
	
	public SaleSearchDetailAdapter(Activity activity, List<STSaleSearchDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_sale_search_detail, null);
		ResolutionSet._instance.iterateChild(rowView);
		STSaleSearchDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_nongyaoname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_detail_nongyaoname);
		AutoSizeTextView txt_standard = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_detail_standard);
		AutoSizeTextView txt_oneprice = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_detail_oneprice);
		AutoSizeTextView txt_amount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_detail_amount);
		AutoSizeTextView txt_totalprice = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_detail_totalprice);
		
		txt_nongyaoname.setText(anItem.nongyao_name);
		txt_standard.setText(anItem.standard);
		txt_oneprice.setText(String.valueOf(anItem.price));
		txt_amount.setText(String.valueOf(anItem.count));
		txt_totalprice.setText(String.valueOf(anItem.total));
		
		return rowView;
	}
}
