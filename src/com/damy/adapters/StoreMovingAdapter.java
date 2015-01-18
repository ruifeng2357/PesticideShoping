package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STCatalogDetailInfo;
import com.damy.datatypes.STMoneyReportInfo;
import com.damy.datatypes.STCatalogInfo;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import com.damy.nongyao.R;
import com.damy.nongyao.StoreMovingActivity;

public class StoreMovingAdapter extends ArrayAdapter<STCatalogDetailInfo> {
	
	private StoreMovingActivity parentAct;
	
	public StoreMovingAdapter(Activity activity, List<STCatalogDetailInfo> detailInfos) {
		super(activity, 0, detailInfos);
		parentAct = (StoreMovingActivity) activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_store_moving, null);
		ResolutionSet._instance.iterateChild(rowView);
		STCatalogDetailInfo anItem = getItem(position);
		
		AutoSizeTextView txt_catalognum = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storemoving_catalognum);
		AutoSizeTextView txt_catalogname = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storemoving_name);
		txt_catalognum.setText(anItem.catalog_num);
		txt_catalogname.setText(anItem.catalog_name);
		
		AutoSizeEditText edit_quantity = (AutoSizeEditText) rowView.findViewById(R.id.edit_item_storemoving_amount);
		edit_quantity.setText(String.valueOf(anItem.quantity));
		if ( (Integer)edit_quantity.getTag() == null )
		{
			edit_quantity.setTag(position);
			edit_quantity.setOnKeyListener( new OnKeyListener(){
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					int pos = (Integer)v.getTag();
					onKeyDownMoney(pos, v);
					return false;
				}			
			});
		}
		
		
		AutoSizeTextView txt_delete = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_storemoving_operation);
		if ( (Integer)txt_delete.getTag() == null )
		{
			txt_delete.setTag(position);
			txt_delete.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
	        		int pos = (Integer)v.getTag();
	        		onClickDelete(pos);
	        	}
	        });
		}
		
		return rowView;
	}
	
	public void onKeyDownMoney(int pos, View v)
	{
		parentAct.onKeyDownQuantityEdit(pos, v);
	}
	
	public void onClickDelete(int pos)
	{
		parentAct.onClickDelete(pos);
	}
}
