package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.StoreSearchAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STCatalogRemainInfo;
import com.google.zxing.client.android.CaptureActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

public class StoreSearchActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETCATALOGREMAINLISTWITHBARCODE};
	
	private final static int				REQUEST_BARCODE = 0;
	
	private ArrayList<STCatalogRemainInfo>	m_StoreSearchList;
	private StoreSearchAdapter				m_StoreSearchAdapter = null;
	private ListView						m_lvStoreSearchListView;
	
	private REQ_TYPE						m_reqType;
	
	//private AutoSizeTextView				txt_barcode;
	private AutoSizeEditText				txt_barcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_search);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.ll_storesearch));
		
		initControls();
		setStoreSearchAdapter();
		readContents();
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
					 
					m_StoreSearchList.clear();
					m_reqType = REQ_TYPE.REQ_GETCATALOGREMAINLISTWITHBARCODE;
					new LoadResponseThread(StoreSearchActivity.this).start();
            	}
                return;
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
		
		FrameLayout fl_barcodebtn = (FrameLayout)findViewById(R.id.fl_storesearch_barcodebtn);
		fl_barcodebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBarCode();
        	}
        });
		
		m_lvStoreSearchListView = (ListView)findViewById(R.id.anStoreSearchContentView);
		
		//txt_barcode = (AutoSizeTextView)findViewById(R.id.txt_storesearch_barcode);
		txt_barcode = (AutoSizeEditText)findViewById(R.id.txt_storesearch_barcode);
	}
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETCATALOGREMAINLISTWITHBARCODE;
		new LoadResponseThread(StoreSearchActivity.this).start();
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
		
	}
	
	private void onClickHistory()
	{
		Intent history_activity = new Intent(this, StoreUsingActivity.class);
		startActivity(history_activity);	
		finish();
	}
	
	private void onClickBarCode()
	{
		Intent intent = new Intent(StoreSearchActivity.this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_BARCODE);
		/*
		m_StoreSearchList.clear();
		m_reqType = REQ_TYPE.REQ_GETCATALOGREMAINLISTWITHBARCODE;
		new LoadResponseThread(StoreSearchActivity.this).start();
		*/
	}
	
	private void setStoreSearchAdapter() {
		m_StoreSearchList = new ArrayList<STCatalogRemainInfo>();

        m_lvStoreSearchListView.setCacheColorHint(Color.TRANSPARENT);
        m_lvStoreSearchListView.setDividerHeight(0);
        //m_lvStoreSearchListView.setDrawSelectorOnTop(true);

        m_StoreSearchAdapter = new StoreSearchAdapter(StoreSearchActivity.this, m_StoreSearchList);
        m_lvStoreSearchListView.setAdapter(m_StoreSearchAdapter);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETCATALOGREMAINLISTWITHBARCODE )
		{
			m_StoreSearchAdapter.notifyDataSetChanged();
		}
		
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETCATALOGREMAINLISTWITHBARCODE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGREMAINLISTWITHBARCODE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&barcode=" + txt_barcode.getText().toString();
				
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
			            STCatalogRemainInfo itemInfo = new STCatalogRemainInfo();
	
						itemInfo.catalog_num = tmpObj.getString("catalog_num");
						itemInfo.catalog_name = tmpObj.getString("catalog_name");
						itemInfo.store_name = tmpObj.getString("storename");
						itemInfo.quantity = tmpObj.getInt("quantity");
						itemInfo.standard_name = tmpObj.getString("standard_name");
						itemInfo.largenumber = tmpObj.getString("largenumber");
						
						m_StoreSearchList.add(itemInfo);
		            }
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		return null;
	}
	

}
