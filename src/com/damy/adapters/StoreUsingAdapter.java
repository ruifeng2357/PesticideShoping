package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STStoreUsingInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class StoreUsingAdapter extends ArrayAdapter<STStoreUsingInfo> {

	public StoreUsingAdapter(Activity activity, List<STStoreUsingInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_store_using, null);
		ResolutionSet._instance.iterateChild(rowView);
		STStoreUsingInfo anItem = getItem(position);
		
		AutoSizeTextView txt_catalognum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storeusing_catalognum);
		AutoSizeTextView txt_catalogname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storeusing_catalogname);
		AutoSizeTextView txt_reason = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storeusing_reason);
		AutoSizeTextView txt_amount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storeusing_amount);
		
		txt_catalognum.setText(anItem.catalog_num);
		txt_catalogname.setText(anItem.catalog_name);
		txt_reason.setText(anItem.reason);
		txt_amount.setText(String.valueOf(anItem.quantity));

		return rowView;
	}
}
