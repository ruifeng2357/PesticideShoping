package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.BaseStoreAdapter;
import com.damy.adapters.StoreUsingAdapter;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STBaseStoreInfo;
import com.damy.datatypes.STStoreUsingInfo;
import com.damy.datatypes.STRegionInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class StoreUsingActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_ADDCATALOGUSINGLOG};
	
	private StoreUsingAdapter				m_StoreUsingAdapter;
	private ArrayList<STStoreUsingInfo> 	m_StoreUsingList;
	private ListView						m_lvStoreUsingListView;
	
	private int 							m_CurClickedItem = -1;
	private PopupWindow 					popup_delconfirm;
	private LinearLayout					m_MaskLayer;
	
	private REQ_TYPE 						m_reqType;
	
	private int								m_UsingCount = 0;
	private String							m_UsingArray = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_using);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_storeusing));
		
		Global.SotreUsing_isSelected = false;
		
		initControls();
		setStoreUsingAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if ( Global.SotreUsing_isSelected )
		{
			STStoreUsingInfo newItem = new STStoreUsingInfo();
			newItem.catalog_id = Global.StoreUsing_SelectItem.catalog_id;
			newItem.catalog_num = Global.StoreUsing_SelectItem.catalog_num;
			newItem.catalog_name = Global.StoreUsing_SelectItem.catalog_name;
			newItem.standard_id = Global.StoreUsing_SelectItem.standard_id;
			newItem.largenumber = Global.StoreUsing_SelectItem.largenumber;
			newItem.store_id = Global.StoreUsing_SelectItem.store_id;
			newItem.store_name = Global.StoreUsing_SelectItem.store_name;
			newItem.quantity = Global.StoreUsing_SelectItem.quantity;
			newItem.reason = Global.StoreUsing_SelectItem.reason;
			
			m_StoreUsingList.add(newItem);
			m_StoreUsingAdapter.notifyDataSetChanged();
		}
	}
	
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_store_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_store_homebtn);
		FrameLayout fl_movingbtn = (FrameLayout)findViewById(R.id.fl_storemoving_btn);
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_storesearch_btn);
		FrameLayout fl_historybtn = (FrameLayout)findViewById(R.id.fl_storehistory_btn);
		
		fl_backbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		fl_homebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickHome();
        	}
        });
		
		fl_movingbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickMoving();
        	}
        });
		
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSearch();
        	}
        });
		
		fl_historybtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickHistory();
        	}
        });
		
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_storeusing_addbtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_storeusing_savebtn);
		FrameLayout fl_cancelbtn = (FrameLayout)findViewById(R.id.fl_storeusing_cancelbtn);
		
		fl_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		fl_savebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		fl_cancelbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCancel();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_storeusing_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvStoreUsingListView = (ListView)findViewById(R.id.anStoreUsingContentView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void onClickBack()
	{
		//finish();
		onClickHome();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickMoving()
	{		
		Intent moving_activity = new Intent(this, StoreMovingActivity.class);
		startActivity(moving_activity);
		finish();				
	}
	
	private void onClickSearch()
	{
		Intent search_activity = new Intent(this, StoreSearchActivity.class);
		startActivity(search_activity);
		finish();
	}
	
	private void onClickHistory()
	{
		
	}
	
	private void onClickAdd()
	{
		Global.SotreUsing_isSelected = false;
		Intent add_activity = new Intent(this, StoreUsingAddActivity.class);
		startActivity(add_activity);
	}
	
	private void onClickSave()
	{		
		m_UsingCount = m_StoreUsingList.size();
		m_UsingArray = "";
		
		if ( m_UsingCount <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_nostoreusing));
			return;
		}
		
		for ( int i = 0; i < m_UsingCount; i++ )
		{
			STStoreUsingInfo tmp = m_StoreUsingList.get(i);
			
			m_UsingArray += String.valueOf(tmp.store_id) + "," + String.valueOf(tmp.catalog_id) + "," + String.valueOf(tmp.standard_id) + "," + String.valueOf(tmp.largenumber) + "," + String.valueOf(tmp.quantity) + "," + tmp.reason + "@";
		}
		
		m_reqType = REQ_TYPE.REQ_ADDCATALOGUSINGLOG;
		new LoadResponseThread(StoreUsingActivity.this).start();
	}
	
	private void onClickCancel()
	{
		m_StoreUsingList.clear();
		m_StoreUsingAdapter.notifyDataSetChanged();
	}
	
	
	private void setStoreUsingAdapter()
	{
		m_StoreUsingList = new ArrayList<STStoreUsingInfo>();

        m_lvStoreUsingListView.setCacheColorHint(Color.TRANSPARENT);
        m_lvStoreUsingListView.setDividerHeight(0);
        m_lvStoreUsingListView.setDrawSelectorOnTop(true);

        m_StoreUsingAdapter = new StoreUsingAdapter(StoreUsingActivity.this, m_StoreUsingList);
        m_lvStoreUsingListView.setAdapter(m_StoreUsingAdapter);
        
        m_lvStoreUsingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
        	}
		});
	}
	
	public STStoreUsingInfo getItem(int position)
	{
		if (position < 0 || position >= m_StoreUsingList.size())
			return null;
		
		return m_StoreUsingList.get(position);
	}
	
	private void onLongClickItem(View view, int position)
	{
		m_CurClickedItem = position;
		
		m_MaskLayer.setVisibility(View.VISIBLE);

		View popupview = View.inflate(this, R.layout.dialog_delconfirm, null);
		ResolutionSet._instance.iterateChild(popupview);
		popup_delconfirm = new PopupWindow(popupview, R.dimen.common_delconfirm_dialog_width, R.dimen.common_delconfirm_dialog_height,true);
		popup_delconfirm.setAnimationStyle(-1);
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_storeusing_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_storeusing));
		
		FrameLayout fl_delconfirm_ok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_okbtn);
		FrameLayout fl_delconfirm_cancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_cancelbtn);
		
		fl_delconfirm_ok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmOk();
        	}
        });
		fl_delconfirm_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmCancel();
        	}
        });
	}
	
	private void onClickDelConfirmOk()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
		
		m_StoreUsingList.remove(m_CurClickedItem);
		m_StoreUsingAdapter.notifyDataSetChanged();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_ADDCATALOGUSINGLOG )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS)
			{
				showToastMessage(getResources().getString(R.string.common_success));
				onClickCancel();
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			
			if ( m_reqType == REQ_TYPE.REQ_ADDCATALOGUSINGLOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_ADDCATALOGUSINGLOG;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("catalogcount", Integer.toString(m_UsingCount));
		requestObj.put("cataloglist", m_UsingArray);

		return requestObj;
	}
	
}
