package com.damy.nongyao;


import java.util.ArrayList;

import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;


import com.damy.datatypes.STBuyCatalogInfo;
import com.damy.datatypes.STStoreInfo;
import com.damy.datatypes.STSupplyInfo;
import com.damy.adapters.BuyCatalogAdapter;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

public class BuyCatalogActivity extends BaseActivity {		
	
	private int 							get_method = 1;	
	private boolean 						request_falg = false;
	
	private ArrayList<STSupplyInfo> 		m_supplyList;	
	private ArrayList<STStoreInfo> 		    m_storeList;	
	private ArrayList<String> 				m_typeList;
	
	private PopupWindow 					dialog_type;
	private PopupWindow 					dialog_supply;
	private PopupWindow 					dialog_store;
	
	private ArrayList<STBuyCatalogInfo> 	m_BuyCatalogList;
	private BuyCatalogAdapter				m_BuyCatalogAdapter = null;
	private ListView						mRealListView;	  
   
    private PopupWindow 					popup_delconfirm;
    private LinearLayout					m_MaskLayer;
    
    private AutoSizeTextView 				txt_cata_permitid;
	private AutoSizeTextView				txt_cata_supply;
	private AutoSizeTextView 				txt_cata_store;
	private AutoSizeTextView				txt_cata_type;	
	private AutoSizeEditText 				txt_cata_date;	
    private String 							m_ticket_number = "";
    private String 							m_date = "";
	private String 							m_buycatalogArray = "";
	private int 							m_longclicked;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_catalog);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_buycatalog));
		
		Global.BuyCatalog_isSelected = false;
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_buycatalog_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_buycatalog_homebtn);
		FrameLayout fl_buycatalog_addbtn = (FrameLayout)findViewById(R.id.fl_buycatalog_addbtn);
		FrameLayout fl_cata_addsavebtn = (FrameLayout)findViewById(R.id.fl_cata_addsavebtn);
		
		m_BuyCatalogList = new ArrayList<STBuyCatalogInfo>();
		m_BuyCatalogAdapter = new BuyCatalogAdapter(this, m_BuyCatalogList);
		
		mRealListView = (ListView)findViewById(R.id.anBuyCatalogContentView);
		mRealListView.setAdapter(m_BuyCatalogAdapter);
		mRealListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mRealListView.setDividerHeight(0);
		mRealListView.setDrawSelectorOnTop(true);
		
		txt_cata_permitid = (AutoSizeTextView)findViewById(R.id.txt_buy_ticket);
		txt_cata_supply = (AutoSizeTextView)findViewById(R.id.txt_buy_supply);
		txt_cata_store = (AutoSizeTextView)findViewById(R.id.txt_buy_store);
		txt_cata_type = (AutoSizeTextView)findViewById(R.id.txt_pay_mode);
		txt_cata_date = (AutoSizeEditText)findViewById(R.id.txt_buy_date);
		
		//txt_cata_permitid.setEnabled(false);
		txt_cata_date.setEnabled(false);
		
		m_supplyList = new ArrayList<STSupplyInfo>();
		m_storeList = new ArrayList<STStoreInfo>();
		m_typeList = new ArrayList<String>();
		readContent();
		
		mRealListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
        	}
		});
				
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
		
		fl_buycatalog_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		fl_cata_addsavebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		LinearLayout ll_supplysel = (LinearLayout)findViewById(R.id.ll_buycatalog_supplysel);
		LinearLayout ll_storesel = (LinearLayout)findViewById(R.id.ll_buycatalog_storesel);
		LinearLayout ll_typesel = (LinearLayout)findViewById(R.id.ll_buycatalog_typesel);
		
		ll_supplysel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectSupply();        	
        	}
        });	
		
		ll_storesel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectStore();        	
        	}
        });	
		
		ll_typesel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();        	
        	}
        });		
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_buycata_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();			
		setBuyCatalogAdapter();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void readContent()
	{	
		request_falg = false;
		get_method = 1;
		m_supplyList.clear();
		m_storeList.clear();	
		m_BuyCatalogList.clear();
		
		new LoadResponseThread(BuyCatalogActivity.this).start();
	}
	
	private void onClickClose()
	{
		readContent();
		
		m_BuyCatalogList.clear();
		m_BuyCatalogAdapter.notifyDataSetChanged();
		
		txt_cata_type.setText(getResources().getString(R.string.common_actual));
	}
	
	private void setBuyCatalogAdapter()
	{	
		STBuyCatalogInfo newitem = new STBuyCatalogInfo();
		if(Global.BuyCatalog_isSelected == true)
		{
			newitem.catalogid = Global.BuyCatalog_SelectedItem.catalogid;
			newitem.catalogname = Global.BuyCatalog_SelectedItem.catalogname;
			newitem.largenumber = Global.BuyCatalog_SelectedItem.largenumber;
			newitem.product_date = Global.BuyCatalog_SelectedItem.product_date;
			newitem.quantity = Global.BuyCatalog_SelectedItem.quantity;
			newitem.mass_id = Global.BuyCatalog_SelectedItem.mass_id;
			newitem.unit_id = Global.BuyCatalog_SelectedItem.unit_id;
			newitem.standard = Global.BuyCatalog_SelectedItem.standard;
			newitem.price = Global.BuyCatalog_SelectedItem.price;
			newitem.count = Global.BuyCatalog_SelectedItem.count;
			newitem.totalprice = Global.BuyCatalog_SelectedItem.totalprice;
					
			if( newitem.catalogname != null)
			{
				m_BuyCatalogList.add(newitem);
				m_BuyCatalogAdapter.notifyDataSetChanged();	
			
			}
		}
	}
	
	private void onLongClickItem(View view, int position)
	{		
		m_MaskLayer.setVisibility(View.VISIBLE);		
		m_longclicked = position; 
		
		View popupview = View.inflate(this, R.layout.dialog_delconfirm, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		popup_delconfirm = new PopupWindow(popupview, R.dimen.common_delconfirm_dialog_width, R.dimen.common_delconfirm_dialog_height,true);
		popup_delconfirm.setAnimationStyle(-1);
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_buycata_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_buycatalog));
		
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
		m_BuyCatalogList.remove(m_BuyCatalogList.get(m_longclicked));
		m_BuyCatalogAdapter.notifyDataSetChanged();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
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
	
	private void onClickAdd()
	{
		Global.BuyCatalog_isSelected = false;
		Intent add_activity = new Intent(this, BuyCatalogAddActivity.class);
		startActivity(add_activity);
	}	
	
	private void onClickSave()
	{
		if ( txt_cata_supply.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_supply));
			return;
		}
		
		if ( txt_cata_store.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_store));
			return;
		}	
		
		if ( txt_cata_type.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_type));
			return;
		}	
		
		int count = m_BuyCatalogList.size();
		
		if ( count <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_nobuyingcatalog));
			return;
		}
		
		for ( int i = 0; i < count; i++ )
		{
			STBuyCatalogInfo tmp = m_BuyCatalogList.get(i);
			m_buycatalogArray += String.valueOf(tmp.catalogid)+ "," + tmp.largenumber + "," + tmp.product_date + "," + String.valueOf(tmp.quantity) + "," + String.valueOf(tmp.mass_id) + "," + String.valueOf(tmp.unit_id) + "," + String.valueOf(tmp.price)+ "," + String.valueOf(tmp.count) + "@";
		}
		
		request_falg = true;
		new LoadResponseThread(BuyCatalogActivity.this).start();		
		
	}	
	
	private void setDialogTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_paytype));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_type != null && dialog_type.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_type.dismiss();
                }
            }
        });
		
		m_typeList.add(getResources().getString(R.string.common_actual));
		m_typeList.add(getResources().getString(R.string.common_bill));
		m_typeList.add(getResources().getString(R.string.common_bankpayment));
		m_typeList.add(getResources().getString(R.string.common_send));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, m_typeList);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickTypeItem(position);
        	}
		});
		
		dialog_type = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_type.setAnimationStyle(-1);
	}
		
	private void setDialogStoreAdapter()	
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_store));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_store != null && dialog_store.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_store.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_storeList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_storeList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickStoreItem(position);
        	}
		});
		
		dialog_store = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_store.setAnimationStyle(-1);
	}
	
	private void setDialogSupplyAdapter()	
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_supply));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_supply != null && dialog_supply.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_supply.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_supplyList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_supplyList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickSupplyItem(position);
        	}
		});
		
		dialog_supply = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_supply.setAnimationStyle(-1);
	}
		
	void onClickSupplyItem(int pos)
	{
		if ( m_supplyList.size() <= 0 )
			return;
		
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_supply.dismiss();
		txt_cata_supply.setText(m_supplyList.get(pos).name);
	}
	
	void onClickStoreItem(int pos)
	{
		if ( m_storeList.size() <= 0 )
			return;
		
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_store.dismiss();
		txt_cata_store.setText(m_storeList.get(pos).name);
	}
	
	void onClickTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
		txt_cata_type.setText(m_typeList.get(pos));
	}
	
	private void onClickSelectSupply()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_supply.showAtLocation(findViewById(R.id.ll_buycata_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);		
	}
	
	private void onClickSelectStore()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_store.showAtLocation(findViewById(R.id.ll_buycata_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);		
	}
	
	private void onClickSelectType()
	{
        m_MaskLayer.setVisibility(View.VISIBLE);
        dialog_type.showAtLocation(findViewById(R.id.ll_buycata_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private long getRidFromSupply(String rname)
	{
		int cnt = m_supplyList.size();
		int i;
		STSupplyInfo tmp;
		for ( i = 0; i < cnt; i++ )
		{
			tmp = m_supplyList.get(i);
			if ( tmp.name == rname )
				return tmp.supply_id;
		}		
		return 0;
	}
	
	private long getRidFromStore(String rname)
	{
		int cnt = m_storeList.size();
		int i;
		STStoreInfo tmp;
		for ( i = 0; i < cnt; i++ )
		{
			tmp = m_storeList.get(i);
			if ( tmp.name == rname )
				return tmp.id;
		}		
		return 0;
	}
	
	private int getRidFromType(String rname)
	{
		int cnt = m_typeList.size();
		int i;	
	
		for ( i = 0; i < cnt; i++ )
		{
			if(m_typeList.get(i) == rname)
				return i;
		}		
		return 0;
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( !request_falg )
		{
			if (get_method == 1 )
			{
				get_method = 2;
				new LoadResponseThread(BuyCatalogActivity.this).start();
				setDialogSupplyAdapter();
				setDialogTypeAdapter();
			}
			else if (get_method == 2 )
			{
				get_method = 3;
				new LoadResponseThread(BuyCatalogActivity.this).start();
				setDialogStoreAdapter();
			}
			else
			{				
				txt_cata_permitid.setText(m_ticket_number);	
				txt_cata_date.setText(m_date);	
				if ( m_supplyList.size() > 0 )
					txt_cata_supply.setText(m_supplyList.get(0).name);
				if ( m_storeList.size() > 0 )
					txt_cata_store.setText(m_storeList.get(0).name);
				txt_cata_type.setText(m_typeList.get(0));
			}
		}
		else
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS)
			{
				showToastMessage(getResources().getString(R.string.common_success));
				
				onClickClose();
				//finish();
			}
			else if ( m_nResponse == ResponseRet.RET_TICKETNUMUSED )
			{
				txt_cata_permitid.setText(m_ticket_number);
				txt_cata_date.setText(m_date);
			}
			else if (m_nResponse == ResponseRet.RET_OVER_AVAILDATE)
			{
				showToastMessage(getResources().getString(R.string.error_over_availdate));
			}
		}
			
	}
	
	public void getResponseJSON() {
		try {
				m_nResponse = ResponseRet.RET_SUCCESS;
				JSONObject response;				
				
				if(request_falg)
				{
					String strUrl ="";
					strUrl = HttpConnUsingJSON.REQ_BUYINGCATALOG;
					response = m_HttpConnUsingJSON.getPostJSONObject(strUrl);
					
					if (response == null) {
						m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
						return;					
					}
					m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
		            if (m_nResponse == ResponseRet.RET_SUCCESS) {
		            	
					}
		            else if ( m_nResponse == ResponseRet.RET_TICKETNUMUSED )
		            {
		            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
		            	JSONObject ticket_obj = dataObject.getJSONObject("ticket_num");
		            	m_ticket_number = ticket_obj.getString("data");
		            	m_date = ticket_obj.getString("date");
		            }
				}
				else
				{
					if(get_method == 1)
					{	
						String strRequest = HttpConnUsingJSON.REQ_GETSUPPLYLIST;					
						strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
						
						response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
						
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
					            STSupplyInfo itemInfo = new STSupplyInfo();

								itemInfo.supply_id = tmpObj.getInt("supply_id");								
								itemInfo.name = tmpObj.getString("name");							
								
								m_supplyList.add(itemInfo);
				            }
						}
					}
					else if(get_method == 2)
					{
						String strRequest = HttpConnUsingJSON.REQ_GETSTORELIST;
						strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
						
						response = m_HttpConnUsingJSON.getGetJSONObject(strRequest );
						
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
					            STStoreInfo itemInfo = new STStoreInfo();

								itemInfo.id = tmpObj.getInt("store_id");								
								itemInfo.name = tmpObj.getString("name");							
								
								m_storeList.add(itemInfo);
				            }
						}
					}
					else
					{
						String strRequest = HttpConnUsingJSON.REQ_GETTICKETNUMBER;
						strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
						strRequest += "&type=0";
						
						response = m_HttpConnUsingJSON.getGetJSONObject(strRequest );
						
						if (response == null) {
							m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
							return;					
						}
						m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
			            if (m_nResponse == ResponseRet.RET_SUCCESS) {
			            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
																												
							m_ticket_number = dataObject.getString("data");
							m_date = dataObject.getString("date");							
						}						
					}
				}	

			} catch (JSONException e) {
				e.printStackTrace();
				m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
			}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();		
	
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));    	
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("store_id", String.valueOf(getRidFromStore(txt_cata_store.getText().toString())));
		requestObj.put("ticketnum", m_ticket_number);		
		requestObj.put("supply_id", String.valueOf(getRidFromSupply(txt_cata_supply.getText().toString())));
		requestObj.put("paytype", Integer.toString(getRidFromType(txt_cata_type.getText().toString())));
		requestObj.put("catalogcount", Integer.toString(m_BuyCatalogList.size()));
		requestObj.put("cataloglist", m_buycatalogArray);

		return requestObj;
	}

}
