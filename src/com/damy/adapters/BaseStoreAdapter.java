package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STBaseStoreInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class BaseStoreAdapter extends ArrayAdapter<STBaseStoreInfo> {

	public BaseStoreAdapter(Activity activity, List<STBaseStoreInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_base_store, null);
		ResolutionSet._instance.iterateChild(rowView);
		STBaseStoreInfo anItem = getItem(position);
		
		AutoSizeTextView txt_name = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_basestore_name);
		AutoSizeTextView txt_manager = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_basestore_manager);
		
		txt_name.setText(anItem.name);
		txt_manager.setText(anItem.uname);
        
		return rowView;
	}
}
