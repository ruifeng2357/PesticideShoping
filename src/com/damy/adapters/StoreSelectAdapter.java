package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.damy.datatypes.STBaseUserInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;

import com.damy.nongyao.BaseStoreAddActivity;
import com.damy.nongyao.R;

public class StoreSelectAdapter extends ArrayAdapter<String> {
	
	BaseStoreAddActivity parentAct;

	public StoreSelectAdapter(BaseStoreAddActivity activity, List<String> detailInfos) {
		super(activity, 0, detailInfos);
		
		parentAct = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_dialog_checkselect, null);
		ResolutionSet._instance.iterateChild(rowView);
		String anItem = getItem(position);
		
		CheckBox chk_text = (CheckBox)rowView.findViewById(R.id.chk_item_dialog_checkselect_check);
				
		chk_text.setText(anItem);
		
		chk_text.setChecked(parentAct.m_CheckList.get(position));
		
		if ( chk_text.getTag() == null )
		{
			chk_text.setTag(position);
			chk_text.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton view, boolean isCheck)
				{
					Integer pos = (Integer)view.getTag();
					
					onChangeCheckbox(pos, isCheck);
				}
			});
		}
		
		return rowView;
	}
	
	public void onChangeCheckbox(int pos, boolean isCheck)
	{
		parentAct.onChangeCheckbox(pos, isCheck);
	}
}
