package com.damy.adapters;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.damy.datatypes.STBaseUserInfo;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;

import com.damy.nongyao.R;

public class DialogSelectAdapter extends ArrayAdapter<String> {

	public DialogSelectAdapter(Activity activity, List<String> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_dialog_select, null);
		ResolutionSet._instance.iterateChild(rowView);
		String anItem = getItem(position);
		
		AutoSizeTextView txt_text = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_dialog_select_text);
		
		txt_text.setText(anItem);
        
		return rowView;
	}
}
