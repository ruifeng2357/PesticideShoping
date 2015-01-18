package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STBuyCatalogInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;

public class BuyCatalogAdapter extends ArrayAdapter<STBuyCatalogInfo> {

	public BuyCatalogAdapter(Activity activity, List<STBuyCatalogInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_buy_catalog, null);
		ResolutionSet._instance.iterateChild(rowView);
		STBuyCatalogInfo anItem = getItem(position);
		
		AutoSizeTextView txt_name = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_buycata_name);
		AutoSizeTextView txt_standard = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_buycata_standard);
		AutoSizeTextView txt_price = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_buycata_price);
		AutoSizeTextView txt_count = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_buycata_amount);
		AutoSizeTextView txt_total = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_buycata_totalprice);
		
		txt_name.setText(anItem.catalogname);
		txt_standard.setText(anItem.standard);
		txt_price.setText(anItem.price);
		txt_count.setText(anItem.count);
		txt_total.setText(anItem.totalprice);
        
		return rowView;
	}
}