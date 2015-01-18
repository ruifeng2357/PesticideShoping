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
import com.damy.datatypes.STStatisticShopInfo;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;
import com.damy.nongyao.SaleRejectActivity;

public class StatisticShopAdapter extends ArrayAdapter<STStatisticShopInfo> {
	
	public StatisticShopAdapter(Activity activity, List<STStatisticShopInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_statistic_shop, null);
		ResolutionSet._instance.iterateChild(rowView);
		STStatisticShopInfo anItem = getItem(position);
		
		AutoSizeTextView txt_region = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticshop_region);
		AutoSizeTextView txt_shop = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticshop_shop);
		AutoSizeTextView txt_lawman = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticshop_lawman);
		AutoSizeTextView txt_salecount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticshop_salecount);
		AutoSizeTextView txt_remaincount = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_statisticshop_remaincount);
		
		
		txt_region.setText(anItem.region_name);
		txt_shop.setText(anItem.shop_name);
		txt_lawman.setText(anItem.shop_lawman);
		txt_salecount.setText(String.valueOf(anItem.sale_count));
		txt_remaincount.setText(String.valueOf(anItem.remain_count));
		
		return rowView;
	}
}
