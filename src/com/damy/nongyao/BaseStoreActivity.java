package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.BaseStoreAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STBaseStoreInfo;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class BaseStoreActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSTOREDETAILLIST, REQ_DELSTORE};
	
	private ArrayList<STBaseStoreInfo> 	m_BaseStoreList;
	private BaseStoreAdapter			m_BaseStoreAdapter = null;
	private ListView					m_lvBaseStoreListView;

    private int 						m_CurClickedItem = -1;
    private PopupWindow 				popup_editdel;
    private PopupWindow 				popup_delconfirm;
    private LinearLayout				m_MaskLayer;
    
    private REQ_TYPE					m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_store);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_basestore));
		
		initControls();
		setBaseStoreAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_BaseStoreList.clear();
		m_reqType = REQ_TYPE.REQ_GETSTOREDETAILLIST;
		new LoadResponseThread(BaseStoreActivity.this).start();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_basestore_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_basestore_homebtn);
		FrameLayout fl_shopbtn = (FrameLayout)findViewById(R.id.fl_basestore_shopbtn);
		FrameLayout fl_userbtn = (FrameLayout)findViewById(R.id.fl_basestore_userbtn);
		FrameLayout fl_storebtn = (FrameLayout)findViewById(R.id.fl_basestore_storebtn);
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_basestore_addbtn);

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
		
		fl_shopbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickShop();
        	}
        });
		
		fl_userbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickUser();
        	}
        });
		
		fl_storebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStore();
        	}
        });
		
		fl_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_basestore_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvBaseStoreListView = (ListView)findViewById(R.id.anBaseStoreContentView);
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
	
	private void onClickShop()
	{
		Intent shop_activity = new Intent(this, BaseShopActivity.class);
		startActivity(shop_activity);
		finish();
	}
	
	private void onClickUser()
	{
		Intent user_activity = new Intent(this, BaseUserActivity.class);
		startActivity(user_activity);	
		finish();
	}
	
	private void onClickStore()
	{
		
	}
	
	private void onClickAdd()
	{
		Intent add_activity = new Intent(this, BaseStoreAddActivity.class);
		add_activity.putExtra(BaseStoreAddActivity.BASESTORE_ADD_ID, 0);
		startActivity(add_activity);
	}
	
	private void onClickPopupEdit()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		STBaseStoreInfo curItem = getItem(m_CurClickedItem);
		
		Intent add_activity = new Intent(this, BaseStoreAddActivity.class);
		add_activity.putExtra(BaseStoreAddActivity.BASESTORE_ADD_ID, curItem.store_id);
		add_activity.putExtra(BaseStoreAddActivity.BASESTORE_ADD_STORENAME, curItem.name);
		add_activity.putExtra(BaseStoreAddActivity.BASESTORE_ADD_MANAGERID, curItem.uid);
		add_activity.putExtra(BaseStoreAddActivity.BASESTORE_ADD_MANAGERNAME, curItem.uname);
		
		startActivity(add_activity);
		
		popup_editdel.dismiss();
	}
	
	private void onClickPopupDel()
	{
		popup_editdel.dismiss();
		
		View popupview = View.inflate(this, R.layout.dialog_delconfirm, null);
		ResolutionSet._instance.iterateChild(popupview);
		popup_delconfirm = new PopupWindow(popupview, R.dimen.common_delconfirm_dialog_width, R.dimen.common_delconfirm_dialog_height,true);
		popup_delconfirm.setAnimationStyle(-1);
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_basestore_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_basestore));
		
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
	
	private void onClickPopupCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_editdel.dismiss();
	}
	
	private void onClickDelConfirmOk()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
		
		m_reqType = REQ_TYPE.REQ_DELSTORE;
		new LoadResponseThread(BaseStoreActivity.this).start();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
	}
	
	public STBaseStoreInfo getItem(int position)
	{
		if (position < 0 || position >= m_BaseStoreList.size())
			return null;
		
		return m_BaseStoreList.get(position);
	}
	
	private void setBaseStoreAdapter() {
		m_BaseStoreList = new ArrayList<STBaseStoreInfo>();

        m_lvBaseStoreListView.setCacheColorHint(Color.TRANSPARENT);
        m_lvBaseStoreListView.setDividerHeight(0);
        m_lvBaseStoreListView.setDrawSelectorOnTop(true);

        m_BaseStoreAdapter = new BaseStoreAdapter(BaseStoreActivity.this, m_BaseStoreList);
        m_lvBaseStoreListView.setAdapter(m_BaseStoreAdapter);
        
        m_lvBaseStoreListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
        	}
		});
	}
	
	private void onLongClickItem(View view, int position)
	{
		m_CurClickedItem = position;
		
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		View popupview = View.inflate(this, R.layout.dialog_editdel, null);
		ResolutionSet._instance.iterateChild(popupview);
		popup_editdel = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		popup_editdel.setAnimationStyle(-1);
		
		popup_editdel.showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		FrameLayout fl_popup_edit = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_editbtn);
		fl_popup_edit.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupEdit();
        	}
        });
		FrameLayout fl_popup_del = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_delbtn);
		fl_popup_del.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupDel();
        	}
        });
		FrameLayout fl_popup_cancel = (FrameLayout)popupview.findViewById(R.id.fl_popup_editdel_cancelbtn);
		fl_popup_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupCancel();
        	}
        });
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETSTOREDETAILLIST )
		{
			m_BaseStoreAdapter = new BaseStoreAdapter(BaseStoreActivity.this, m_BaseStoreList);
	        m_lvBaseStoreListView.setAdapter(m_BaseStoreAdapter);
		}
		else if ( m_reqType == REQ_TYPE.REQ_DELSTORE )
		{
			m_BaseStoreList.clear();
			m_reqType = REQ_TYPE.REQ_GETSTOREDETAILLIST;
			new LoadResponseThread(BaseStoreActivity.this).start();
		}
		
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSTOREDETAILLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTOREDETAILLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("count");
		            JSONArray dataList = dataObject.getJSONArray("data");
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STBaseStoreInfo itemInfo = new STBaseStoreInfo();
	
						itemInfo.store_id = tmpObj.getInt("store_id");
						itemInfo.name = tmpObj.getString("name");
						itemInfo.uid = tmpObj.getString("uid");
						itemInfo.uname = tmpObj.getString("uname");
						
						m_BaseStoreList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_DELSTORE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_DELSTORE;
				
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
		
		STBaseStoreInfo curItem = getItem(m_CurClickedItem);

		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("store_id", String.valueOf(curItem.store_id));

		return requestObj;
	}
	


}
