package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STMoneyReportInfo;
import com.damy.datatypes.STCatalogInfo;
import com.damy.datatypes.STSaleRejectInfo;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;
import com.damy.nongyao.SaleRejectActivity;

public class SaleRejectAdapter extends ArrayAdapter<STSaleRejectInfo> {	
	
	public SaleRejectAdapter(Activity activity, List<STSaleRejectInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_sale_reject, null);
		ResolutionSet._instance.iterateChild(rowView);
		STSaleRejectInfo anItem = getItem(position);
		
		AutoSizeTextView txt_catalogname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_catalogname);
		AutoSizeTextView txt_standard = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_standard);
		AutoSizeTextView txt_largenumber = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_largenumber);
		AutoSizeTextView txt_oneprice = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_oneprice);
		AutoSizeTextView txt_quantity = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_amount);
		AutoSizeTextView txt_totalprice = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salereject_totalprice);
		
		txt_catalogname.setText(anItem.catalog_name);
		txt_standard.setText(anItem.standard_name);
		txt_largenumber.setText(anItem.largenumber);
		txt_oneprice.setText(String.valueOf(anItem.oneprice));
		txt_quantity.setText(String.valueOf(anItem.quantity));
		txt_totalprice.setText(String.valueOf(anItem.totalprice));
		
		return rowView;
	}
}
