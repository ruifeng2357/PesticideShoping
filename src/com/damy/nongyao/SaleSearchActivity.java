package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.BaseStoreAdapter;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.adapters.SaleSearchAdapter;
import com.damy.adapters.SaleSearchDetailAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STBaseStoreInfo;
import com.damy.datatypes.STSaleSearchDetailInfo;
import com.damy.datatypes.STSaleSearchInfo;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class SaleSearchActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_SALEHISTORY, REQ_SALEDETAIL};
	
	
	private PopupWindow 					dialog_type;
	private PopupWindow 					dialog_datepicker;
    private LinearLayout					m_MaskLayer;
	
	private PullToRefreshListView			m_lvSaleSearchListView;
	private ArrayList<STSaleSearchInfo> 	m_SaleSearchList = new ArrayList<STSaleSearchInfo>();
	private SaleSearchAdapter				m_SaleSearchAdapter = null;
	private ListView						mRealListView;
	
	private ListView							mSaleSearchDetailListView;
	private ArrayList<STSaleSearchDetailInfo> 	m_SaleSearchDetailList = new ArrayList<STSaleSearchDetailInfo>();
	private SaleSearchDetailAdapter				m_SaleSearchDetailAdapter = null;
	
	private ArrayList<String> 				m_typeList = new ArrayList<String>();
	private int								m_curSelSaleType;
	
	private int								m_nCurPageNumber = 1;
	private AutoSizeTextView				txt_saletype;
	private AutoSizeTextView				txt_startdate;
	private AutoSizeTextView				txt_enddate;
	private AutoSizeEditText				edit_ticketnumcustomer;
	private AutoSizeTextView				txt_detail_ticketnum;
	private AutoSizeTextView				txt_detail_customer;
	
	private LinearLayout					ll_SaleSearDetail;
	
	private DatePicker						m_DatePicker;
	private int								m_curSelDateType;
	
	private long							m_curDetailItem_TicketId;
	private String							m_curDetailItem_TicketNum;
	private String							m_curDetailItem_Customer;
	
	private REQ_TYPE						m_reqType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_search);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_salesearch));
		
		initContents();
		setDialogTypeAdapter();
		setSaleSearchAdapter();
	}
	/*
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_reqType = REQ_TYPE.REQ_SALEHISTORY;
		new LoadResponseThread(SaleSearchActivity.this).start();
	}
	*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void initContents()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_salesearch_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_salesearch_homebtn);
		
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
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_salesearch_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_salesearch_enddate);
		
		ll_startdate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStartDate();
        	}
        });
		
		ll_enddate.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickEndDate();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_salesearch_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
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
		
		m_lvSaleSearchListView = (PullToRefreshListView)findViewById(R.id.anSaleSearchContentView);
		
		txt_saletype = (AutoSizeTextView)findViewById(R.id.txt_salesearch_saletype);
		edit_ticketnumcustomer = (AutoSizeEditText)findViewById(R.id.edit_salesearch_searchtxt);
		txt_startdate = (AutoSizeTextView)findViewById(R.id.txt_salesearch_startdate);
		txt_enddate = (AutoSizeTextView)findViewById(R.id.txt_salesearch_enddate);
		
		txt_saletype.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSaleType();
        	}
        });
		
		txt_saletype.setText(getResources().getString(R.string.common_all));
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_enddate.setText(strDate);
		strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
		txt_startdate.setText(strDate);
		
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_salesearch_searchbtn);
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSearchBtn();
        	}
        });
		
		m_curSelSaleType = 4;
		
		txt_detail_ticketnum = (AutoSizeTextView)findViewById(R.id.txt_salesearch_detail_ticketnum);
		txt_detail_customer = (AutoSizeTextView)findViewById(R.id.txt_salesearch_detail_customer);
		FrameLayout fl_detailclosebtn = (FrameLayout)findViewById(R.id.fl_salesearch_detail_close);
		fl_detailclosebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailCloseBtn();
        	}
        });
		ll_SaleSearDetail = (LinearLayout)findViewById(R.id.ll_salesearch_detail);
		ll_SaleSearDetail.setVisibility(View.INVISIBLE);
		mSaleSearchDetailListView = (ListView)findViewById(R.id.anSaleSearchDetailContentView);
	}

	private void onClickBack()
	{
		finish();
		//onClickHome();
	}
	
	private void onClickHome()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onClickSearchBtn()
	{
		ll_SaleSearDetail.setVisibility(View.INVISIBLE);
		
		m_nCurPageNumber = 1;
		m_SaleSearchList.clear();
		m_reqType = REQ_TYPE.REQ_SALEHISTORY;
		new LoadResponseThread(SaleSearchActivity.this).start();
	}
	
	private void onClickDetailCloseBtn()
	{
		ll_SaleSearDetail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = txt_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_salesearch_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = txt_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_salesearch_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickPopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
			txt_startdate.setText(strDate);
		else
			txt_enddate.setText(strDate);
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void onClickPopupCancel()
	{
		dialog_datepicker.dismiss();
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void onClickSaleType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_salesearch_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_saletype));

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

		m_typeList.add(getResources().getString(R.string.common_all));
		m_typeList.add(getResources().getString(R.string.common_sale1));
		m_typeList.add(getResources().getString(R.string.common_salereject));
		
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
	
	private void onClickTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
		
		txt_saletype.setText(m_typeList.get(pos));
		if ( pos == 0 )
			m_curSelSaleType = 4;
		else if ( pos == 1 )
			m_curSelSaleType = 2;
		else if ( pos == 2 )
			m_curSelSaleType = 3;
	}
	
	private void setSaleSearchAdapter() {
		m_SaleSearchList = new ArrayList<STSaleSearchInfo>();
		m_lvSaleSearchListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvSaleSearchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_SALEHISTORY;
                new LoadResponseThread(SaleSearchActivity.this).start();
            }
        });

        mRealListView = m_lvSaleSearchListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        //mRealListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mRealListView.setDrawSelectorOnTop(true);
        
        m_SaleSearchAdapter = new SaleSearchAdapter(SaleSearchActivity.this, m_SaleSearchList);
		mRealListView.setAdapter(m_SaleSearchAdapter);
		
		
		mRealListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
        	}
		});
	}
	
	private void onLongClickItem(View view, int position)
	{
		m_curDetailItem_TicketId = m_SaleSearchList.get(position - 1).ticket_id;
		m_curDetailItem_TicketNum = m_SaleSearchList.get(position - 1).ticketnum;
		m_curDetailItem_Customer = m_SaleSearchList.get(position - 1).customer;
		
		txt_detail_ticketnum.setText(m_curDetailItem_TicketNum);
		txt_detail_customer.setText(m_curDetailItem_Customer);
		
		m_SaleSearchDetailList.clear();
		m_reqType = REQ_TYPE.REQ_SALEDETAIL;
		new LoadResponseThread(SaleSearchActivity.this).start();
	}
	
	private void setSaleSearchDetailAdapter() {
		mSaleSearchDetailListView.setCacheColorHint(Color.TRANSPARENT);
		mSaleSearchDetailListView.setDividerHeight(0);
		
        m_SaleSearchDetailAdapter = new SaleSearchDetailAdapter(SaleSearchActivity.this, m_SaleSearchDetailList);
        mSaleSearchDetailListView.setAdapter(m_SaleSearchDetailAdapter);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_SALEHISTORY )
		{
			m_SaleSearchAdapter.notifyDataSetChanged();
			m_lvSaleSearchListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_SALEDETAIL )
		{
			setSaleSearchDetailAdapter();
			ll_SaleSearDetail.setVisibility(View.VISIBLE);
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_SALEHISTORY )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_SALEHISTORY;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&type=" + Integer.toString(m_curSelSaleType);
				strRequest += "&start=" + txt_startdate.getText().toString();
				strRequest += "&end=" + txt_enddate.getText().toString();
				strRequest += "&search=" + EncodeToUTF8(edit_ticketnumcustomer.getText().toString());
				strRequest += "&pagenum=" + Integer.toString(m_nCurPageNumber);
				
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
			            STSaleSearchInfo itemInfo = new STSaleSearchInfo();
	
						itemInfo.saletype = tmpObj.getString("type");
						itemInfo.ticket_id = tmpObj.getLong("ticket_id");
						itemInfo.ticketnum = tmpObj.getString("ticket_num");
						itemInfo.customer = tmpObj.getString("customer_name");
						itemInfo.saledate = tmpObj.getString("saledate");
						itemInfo.totalmoney = tmpObj.getDouble("totalmoney");
						itemInfo.realmoney = tmpObj.getDouble("realmoney");
						
						m_SaleSearchList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_SALEDETAIL )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_SALEDETAIL;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&ticket_id=" + String.valueOf(m_curDetailItem_TicketId);
				
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
		            
		            m_SaleSearchDetailList.clear();
		            
		            for (int i = 0; i < count; i++) {
		            	JSONObject tmpObj = (JSONObject) dataList.get(i);
			            STSaleSearchDetailInfo itemInfo = new STSaleSearchDetailInfo();

						itemInfo.nongyao_name = tmpObj.getString("catalog_name");
						itemInfo.standard = tmpObj.getString("standard");
						itemInfo.price = Double.valueOf(tmpObj.getString("price"));
						itemInfo.count = Integer.valueOf(tmpObj.getString("count"));
						itemInfo.total = Double.valueOf(tmpObj.getString("total"));
						
						m_SaleSearchDetailList.add(itemInfo);
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
