package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STCatalogRemainInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class StoreSearchAdapter extends ArrayAdapter<STCatalogRemainInfo> {

	public StoreSearchAdapter(Activity activity, List<STCatalogRemainInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_store_search, null);
		ResolutionSet._instance.iterateChild(rowView);
		STCatalogRemainInfo anItem = getItem(position);
		
		AutoSizeTextView txt_catalognum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_catalognum);
		AutoSizeTextView txt_storename = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_storename);
		AutoSizeTextView txt_catalogname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_catalogname);
		AutoSizeTextView txt_amount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_amount);
		AutoSizeTextView txt_standardname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_standard);
		AutoSizeTextView txt_largenumber = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storesearch_largenumber);
		
		txt_catalognum.setText(anItem.catalog_num);
		txt_storename.setText(anItem.store_name);
		txt_catalogname.setText(anItem.catalog_name);
		txt_standardname.setText(anItem.standard_name);
		txt_largenumber.setText(anItem.largenumber);
		txt_amount.setText(String.valueOf(anItem.quantity));

		return rowView;
	}
}
