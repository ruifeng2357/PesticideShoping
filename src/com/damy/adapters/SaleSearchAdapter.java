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
import com.damy.datatypes.STSaleSearchInfo;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;
import com.damy.nongyao.SaleRejectActivity;

public class SaleSearchAdapter extends ArrayAdapter<STSaleSearchInfo> {
	
	public SaleSearchAdapter(Activity activity, List<STSaleSearchInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_sale_search, null);
		ResolutionSet._instance.iterateChild(rowView);
		STSaleSearchInfo anItem = getItem(position);
		
		AutoSizeTextView txt_saletype = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_saletype);
		AutoSizeTextView txt_ticketnum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_ticketnum);
		AutoSizeTextView txt_customer = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_customer);
		AutoSizeTextView txt_saledate = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_saledate);
		AutoSizeTextView txt_mustgetmoney = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_mustgetmoney);
		AutoSizeTextView txt_realgetmoney = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_salesearch_realgetmoney);
		
		
		txt_saletype.setText(anItem.saletype);
		txt_ticketnum.setText(anItem.ticketnum);
		txt_customer.setText(anItem.customer);
		txt_saledate.setText(anItem.saledate);
		txt_mustgetmoney.setText(String.valueOf(anItem.totalmoney));
		txt_realgetmoney.setText(String.valueOf(anItem.realmoney));
		
		return rowView;
	}
}
