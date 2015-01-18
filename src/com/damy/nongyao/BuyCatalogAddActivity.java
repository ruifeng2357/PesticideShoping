package com.damy.nongyao;


import java.util.ArrayList;
import java.util.Date;

import android.text.TextWatcher;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STUnitInfo;
import com.google.zxing.client.android.CaptureActivity;

public class BuyCatalogAddActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETUNITLIST, REQ_GETCATALOGINFOFROMBARCODE};
	
	private final static int			REQUEST_BARCODE = 0;

	private REQ_TYPE					m_reqType;
	
	private AutoSizeEditText 			txt_barcode;
	//private AutoSizeTextView 			txt_barcode;
	private AutoSizeEditText 			txt_cata_num;
	private AutoSizeEditText 			edit_largenumber;
	private AutoSizeTextView 			txt_productdate;
	private AutoSizeEditText 			edit_quantity;
	private AutoSizeTextView 			txt_mass;
	private AutoSizeTextView 			txt_unit;
	private AutoSizeEditText 			txt_count;
	private AutoSizeEditText 			txt_price;
	private AutoSizeEditText			txt_total;
	
	private ArrayList<STUnitInfo> 		m_UnitList;
	private ArrayList<String> 			m_MassList;
	
	private PopupWindow 				dialog_unit;
	private PopupWindow 				dialog_mass;
	private PopupWindow 				dialog_datepicker;
	
	private DatePicker					m_DatePicker;
	
	private LinearLayout				m_MaskLayer;
	private String 						m_catalogname = "";
	private String 						m_catalognum = "";
	private int 						m_catalogid = 0;
	
	private long						m_curUnitId = 0;
	private int							m_curMassId = 0;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            onKeyDownMoney();
        }
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_catalog_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_buycatalog_add));
		
		//txt_barcode = (AutoSizeTextView)findViewById(R.id.txt_cata_barcode);
		txt_barcode = (AutoSizeEditText)findViewById(R.id.txt_cata_barcode);
		txt_cata_num = (AutoSizeEditText)findViewById(R.id.txt_cata_num);
		edit_largenumber = (AutoSizeEditText)findViewById(R.id.txt_cata_largenumber);
		txt_productdate = (AutoSizeTextView)findViewById(R.id.txt_cata_productdate);
		edit_quantity = (AutoSizeEditText)findViewById(R.id.txt_cata_quantity);
		txt_mass = (AutoSizeTextView)findViewById(R.id.txt_cata_mass);
		txt_count = (AutoSizeEditText)findViewById(R.id.txt_cata_amount);
		txt_price = (AutoSizeEditText)findViewById(R.id.txt_cata_price);
		txt_total = (AutoSizeEditText)findViewById(R.id.txt_cata_money);
		txt_unit = (AutoSizeTextView)findViewById(R.id.txt_cata_unit);
		
		txt_cata_num.setEnabled(false);
		txt_total.setEnabled(false);
		
		m_UnitList = new ArrayList<STUnitInfo>();
		readContent();
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_buycatalogadd_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_buycatalogadd_homebtn);
		FrameLayout fl_buycatalog_addbtn = (FrameLayout)findViewById(R.id.fl_cata_addbtn);		
		FrameLayout fl_barcodebtn = (FrameLayout)findViewById(R.id.fl_barcode_btn);		
		
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
		
		fl_barcodebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBarcode();
        	}
        });
		
		txt_mass.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectMass();
        	}
        });
		
		txt_unit.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectUnit();
        	}
        });
		
//		txt_price.setOnKeyListener( new OnKeyListener(){
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				onKeyDownMoney();
//				return false;
//			}
//		});
        txt_price.addTextChangedListener(watcher);
		
//		txt_count.setOnKeyListener( new OnKeyListener(){
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				onKeyDownMoney();
//				return false;
//			}
//		});
        txt_count.addTextChangedListener(watcher);
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_buycataadd_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		txt_productdate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectDate();
        	}
        });
		
		View popupview = View.inflate(this, R.layout.dialog_datepicker, null);
		ResolutionSet._instance.iterateChild(popupview);
		dialog_datepicker = new PopupWindow(popupview, R.dimen.common_datepicker_dialog_width, R.dimen.common_datepicker_dialog_height, true);
		dialog_datepicker.setAnimationStyle(-1);
		
		FrameLayout fl_popupok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_datepicker_okbtn);
		FrameLayout fl_popupcancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_datepicker_cancelbtn);
		m_DatePicker = (DatePicker)popupview.findViewById(R.id.dp_dialog_datepicker_date);
		
		fl_popupok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupOk();
        	}
        });
		
		fl_popupcancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPopupCancel();
        	}
        });
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_productdate.setText(strDate);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
            case REQUEST_BARCODE:
            	if ( data != null )
            	{
					String strReturn = data.getStringExtra(CaptureActivity.RESULTSTR);
					txt_barcode.setText(strReturn);
					 
					m_reqType = REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE;
					new LoadResponseThread(BuyCatalogAddActivity.this).start();
            	}
                return;
        }
    }

	private void onClickBack()
	{
		finish();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void readContent()
	{	
		m_reqType = REQ_TYPE.REQ_GETUNITLIST;
		m_UnitList.clear();
		new LoadResponseThread(BuyCatalogAddActivity.this).start();
		
		setDialogMassAdapter();
	}
	
	private void onClickSelectUnit()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_unit.showAtLocation(findViewById(R.id.ll_buycataadd_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);		
	}
	
	private void setDialogUnitAdapter()	
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_unit));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_unit != null && dialog_unit.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_unit.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_UnitList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_UnitList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickUnitItem(position);
        	}
		});
		
		dialog_unit = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_unit.setAnimationStyle(-1);
	}
	
	void onClickUnitItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_unit.dismiss();
		txt_unit.setText(m_UnitList.get(pos).name);
		
		m_curUnitId = m_UnitList.get(pos).id;
	}
	
	private void onClickSelectMass()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_mass.showAtLocation(findViewById(R.id.ll_buycataadd_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);		
	}
	
	private void setDialogMassAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_standard));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_mass != null && dialog_mass.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_mass.dismiss();
                }
            }
        });
		
		m_MassList = new ArrayList<String>();
		
		m_MassList.add(getResources().getString(R.string.common_mass1));
		m_MassList.add(getResources().getString(R.string.common_mass2));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, m_MassList);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickMassItem(position);
        	}
		});
		
		dialog_mass = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_mass.setAnimationStyle(-1);
	}
	
	void onClickMassItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_mass.dismiss();	
		txt_mass.setText(m_MassList.get(pos));
		
		m_curMassId = pos;
	}
	
	private void onClickSelectDate()
	{
		String strDate = txt_productdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(m_MaskLayer, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickPopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		txt_productdate.setText(strDate);
	}
	
	private void onClickPopupCancel()
	{
		dialog_datepicker.dismiss();
	}
	
	private void onKeyDownMoney()
	{
		double price = 0;
		int count = 0;	
		String p = txt_price.getText().toString();
		String c = txt_count.getText().toString();
		
		if(!p.equals("") && !c.equals("")){
			try{
				price = Double.valueOf(p);
				count = Integer.parseInt(c);
				
				double total = price * count;
				
				String a = String.format("%.2f", total);
				
				txt_total.setText(a);	
			}catch (Exception e) {
		      	e.printStackTrace();        	
		    }
		}
	}
	
	private void onClickAdd()
	{
		if ( txt_barcode.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_barcode));
			return;
		}
		
		if ( txt_cata_num.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_catalog));
			return;
		}
		
		if ( edit_largenumber.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_largenumber));
			return;
		}
		
		if ( txt_count.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_amount));
			return;
		}
		
		if ( edit_quantity.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_standard));
			return;
		}
		
		if ( Integer.valueOf(edit_quantity.getText().toString()) == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_standard));
			return;
		}
		
		if ( txt_unit.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_standard));
			return;
		}
		
		if ( txt_mass.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_standard));
			return;
		}	
		
		if ( txt_price.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_price));
			return;
		}
		
		if ( Integer.valueOf(txt_count.getText().toString()) == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_zeroquantity));
			return;
		}
		
		Global.BuyCatalog_SelectedItem.catalogid= m_catalogid;
		Global.BuyCatalog_SelectedItem.catalogname = m_catalogname;
		Global.BuyCatalog_SelectedItem.catalognum = m_catalognum;
		Global.BuyCatalog_SelectedItem.largenumber = edit_largenumber.getText().toString();
		Global.BuyCatalog_SelectedItem.product_date = txt_productdate.getText().toString();
		Global.BuyCatalog_SelectedItem.quantity = Integer.valueOf(edit_quantity.getText().toString());
		Global.BuyCatalog_SelectedItem.mass_id = m_curMassId;
		Global.BuyCatalog_SelectedItem.unit_id = m_curUnitId;
		Global.BuyCatalog_SelectedItem.standard = edit_quantity.getText().toString() + txt_mass.getText().toString() + "/" + txt_unit.getText().toString();
		Global.BuyCatalog_SelectedItem.price = txt_price.getText().toString();
		Global.BuyCatalog_SelectedItem.count = txt_count.getText().toString();
		Global.BuyCatalog_SelectedItem.totalprice = txt_total.getText().toString();
		Global.BuyCatalog_isSelected = true;
		finish();
	}

	public void onClickBarcode() {
		Intent intent = new Intent(BuyCatalogAddActivity.this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_BARCODE);
		/*
		m_reqType = REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE;
		new LoadResponseThread(BuyCatalogAddActivity.this).start();
		*/
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if( m_reqType == REQ_TYPE.REQ_GETUNITLIST )
		{
			setDialogUnitAdapter();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE )
		{
			 txt_cata_num.setText(m_catalognum);
		}
	}
	
	public void getResponseJSON() {
	
		try {
				m_nResponse = ResponseRet.RET_SUCCESS;
				JSONObject response;			
				
				if ( m_reqType == REQ_TYPE.REQ_GETUNITLIST )
				{
					String strRequest = HttpConnUsingJSON.REQ_GETUNITLIST;					
									
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
				            STUnitInfo itemInfo = new STUnitInfo();
	
							itemInfo.id = tmpObj.getInt("unit_id");								
							itemInfo.name = tmpObj.getString("unit");
							
							m_UnitList.add(itemInfo);
			            }
					}
				}
				else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMBARCODE )
				{
					String strRequest = HttpConnUsingJSON.REQ_GETCATALOGINFOFROMBARCODE;
					strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
					strRequest += "&barcode=" + txt_barcode.getText().toString();
					
					response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
					
					if (response == null) {
						m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
						return;					
					}
					m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
		            if (m_nResponse == ResponseRet.RET_SUCCESS) {
		            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
						   
						   m_catalogname = dataObject.getString("catalog_name");
						   m_catalognum = dataObject.getString("catalog_num");	
						   m_catalogid = dataObject.getInt("catalog_id");
					}
				}
		
			} catch (JSONException e) {
				e.printStackTrace();
				m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
			}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();	
		return requestObj;
	}
	
}
