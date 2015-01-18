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

import com.damy.nongyao.R;

public class BaseUserAdapter extends ArrayAdapter<STBaseUserInfo> {

	public BaseUserAdapter(Activity activity, List<STBaseUserInfo> detailInfos) {
		super(activity, 0, detailInfos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		View rowView = inflater.inflate(R.layout.item_base_user, null);
		ResolutionSet._instance.iterateChild(rowView);
		STBaseUserInfo anItem = getItem(position);
		
		AutoSizeTextView txt_name = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_baseuser_name);
		AutoSizeTextView txt_mobilephone = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_baseuser_mobilephone);
		AutoSizeTextView txt_role = (AutoSizeTextView) rowView.findViewById(R.id.txt_item_baseuser_role);
		
		txt_name.setText(anItem.username);
        txt_mobilephone.setText(anItem.phone);
        txt_role.setText(RoleEToC(anItem.role));
        
		return rowView;
	} 
	
	public String RoleEToC(String roleStr)
	{
		roleStr = roleStr.replace("buying", getContext().getResources().getString(R.string.common_buying));
		roleStr = roleStr.replace("sale", getContext().getResources().getString(R.string.common_sale));
		roleStr = roleStr.replace("store", getContext().getResources().getString(R.string.common_store));
		roleStr = roleStr.replace("account", getContext().getResources().getString(R.string.common_account));
		
		return roleStr;
	}
}
