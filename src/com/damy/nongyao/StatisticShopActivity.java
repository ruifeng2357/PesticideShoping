package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.damy.adapters.DialogSelectAdapter;
import com.damy.adapters.StatisticShopAdapter;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STShopInfo;
import com.damy.datatypes.STStatisticShopInfo;
import com.damy.datatypes.STShopSimpleInfo;
import com.damy.datatypes.STStatisticShopInfo;
import com.damy.backend.*;

public class StatisticShopActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSHOPINFOFROMNICKNAME, REQ_GETLASTREGIONLIST, REQ_GETSHOPSTATISTIC, REQ_GETSHOPINFO};
	
	private REQ_TYPE							m_reqType;
	
	
	private PullToRefreshListView				m_lvStatisticShopListView;
	private ArrayList<STStatisticShopInfo> 		m_StatisticShopList;
	private StatisticShopAdapter				m_StatisticShopAdapter = null;
	private ListView							mRealListView;
	
	private ArrayList<STRegionInfo>				m_RegionList = new ArrayList<STRegionInfo>();
	private ArrayList<STShopSimpleInfo>			m_ShopList = new ArrayList<STShopSimpleInfo>();
	
	private AutoSizeTextView					m_txtRegion;
	private AutoSizeTextView					m_txtShop;
	private AutoSizeTextView					m_txtStartdate;
	private AutoSizeTextView					m_txtEnddate;
	private AutoSizeEditText					m_editShopSearch;
	private ImageView							m_imgLevel;
	
	private AutoSizeTextView					m_txtShopName;
	private AutoSizeTextView					m_txtShopPermit;
	private AutoSizeTextView					m_txtShopPhone;
	
	
	private PopupWindow 						dialog_region;
	private PopupWindow 						dialog_shop;
	private PopupWindow 						dialog_datepicker;
	
	private int									m_curSelDateType;
	private DatePicker							m_DatePicker;
	
	private int									m_curClickedItem = 0;
	private STShopInfo							m_curClickedShopInfo = new STShopInfo();
	
	private int									m_nCurPageNumber = 1;
	private int									m_curSelRegion = 0;
	private int									m_curSelShop = 0;
	private int									m_curLevel = 0;
	
	private LinearLayout						ll_main;
	private LinearLayout						ll_detail;
	private LinearLayout						m_MaskLayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_shop);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticshop));
		
		initControls();
		readRegionContents();
		setStatisticShopAdapter();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_statistic_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_statistic_homebtn);
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_statisticshop_shopsearchbtn);
		FrameLayout fl_shopbtn = (FrameLayout)findViewById(R.id.fl_statistic_shopbtn);
		FrameLayout fl_nongyaobtn = (FrameLayout)findViewById(R.id.fl_statistic_nongyaobtn);
		FrameLayout fl_policybtn = (FrameLayout)findViewById(R.id.fl_statistic_policybtn);
		
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
		
		fl_nongyaobtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNongyao();
        	}
        });
		
		fl_policybtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPolicy();
        	}
        });
		
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickShopSearch();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_statisticshop_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		
		m_txtRegion = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_region);
		m_editShopSearch = (AutoSizeEditText)findViewById(R.id.edit_statisticshop_shopsearch);
		m_txtShop = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_shop);
		m_txtStartdate = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_startdate);
		m_txtEnddate = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_enddate);
		
		LinearLayout ll_Region = (LinearLayout)findViewById(R.id.ll_statisticshop_region);
		LinearLayout ll_Shop = (LinearLayout)findViewById(R.id.ll_statisticshop_shop);
		
		ll_Region.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRegionSelect();
        	}
        });
		
		ll_Shop.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickShopSelect();
        	}
        });
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_statisticshop_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_statisticshop_enddate);
		
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
		
		View popupdateview = View.inflate(this, R.layout.dialog_datepicker, null);
		ResolutionSet._instance.iterateChild(popupdateview);
		dialog_datepicker = new PopupWindow(popupdateview, R.dimen.common_datepicker_dialog_width, R.dimen.common_datepicker_dialog_height, true);
		dialog_datepicker.setAnimationStyle(-1);
		
		FrameLayout fl_popupok = (FrameLayout)popupdateview.findViewById(R.id.fl_dialog_datepicker_okbtn);
		FrameLayout fl_popupcancel = (FrameLayout)popupdateview.findViewById(R.id.fl_dialog_datepicker_cancelbtn);
		m_DatePicker = (DatePicker)popupdateview.findViewById(R.id.dp_dialog_datepicker_date);
		
		fl_popupok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDatePopupOk();
        	}
        });
		
		fl_popupcancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDatePopupCancel();
        	}
        });
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		m_txtEnddate.setText(strDate);
		strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1";
		m_txtStartdate.setText(strDate);
		
		m_lvStatisticShopListView = (PullToRefreshListView)findViewById(R.id.anStatisticShopContentView);
		
		LinearLayout ll_level = (LinearLayout)findViewById(R.id.ll_statisticshop_level);
		ll_level.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLevel();
        	}
        });
		
		m_imgLevel = (ImageView)findViewById(R.id.img_statisticshop_level);
		
		ll_main = (LinearLayout)findViewById(R.id.ll_statisticshop_main);
		ll_detail = (LinearLayout)findViewById(R.id.ll_statisticshop_detail);
		
		ll_detail.setVisibility(View.INVISIBLE);
		
		m_txtShopName = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_detail_shopname);
		m_txtShopPermit = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_detail_permit);
		m_txtShopPhone = (AutoSizeTextView)findViewById(R.id.txt_statisticshop_detail_phonenum);
		
		FrameLayout fl_detail_close = (FrameLayout)findViewById(R.id.fl_statisticshop_detail_close);
		fl_detail_close.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailClose();
        	}
        });
		
	}
	
	private void readRegionContents()
	{
		m_RegionList.clear();
		
		STRegionInfo itemInfo = new STRegionInfo();
		itemInfo.id = Global.Cur_AdminRegionId;
		itemInfo.name = getResources().getString(R.string.common_all);
		m_RegionList.add(itemInfo);
		
		m_reqType = REQ_TYPE.REQ_GETLASTREGIONLIST;
		new LoadResponseThread(StatisticShopActivity.this).start();
	}
	
	private void readShopContents()
	{
		m_ShopList.clear();
		
		STShopSimpleInfo itemInfo = new STShopSimpleInfo();
		itemInfo.shop_id = 0;
		itemInfo.shop_name = getResources().getString(R.string.common_all);
		m_ShopList.add(itemInfo);
		
		m_reqType = REQ_TYPE.REQ_GETSHOPINFOFROMNICKNAME;
		new LoadResponseThread(StatisticShopActivity.this).start();
	}
	
	private void readMainContents()
	{
		m_nCurPageNumber = 1;
		
		m_StatisticShopList.clear();
		m_reqType = REQ_TYPE.REQ_GETSHOPSTATISTIC;
		new LoadResponseThread(StatisticShopActivity.this).start();
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
		onClickHome();
	}
	
	private void onClickHome()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}
	
	private void onClickShop()
	{
	}
	
	private void onClickNongyao()
	{
		Intent nongyao_activity = new Intent(this, StatisticNongyaoActivity.class);
		startActivity(nongyao_activity);
		finish();
	}
	
	private void onClickPolicy()
	{
		Intent policy_activity = new Intent(this, StatisticPolicyActivity.class);
		startActivity(policy_activity);	
		finish();
	}
	
	private void onClickShopSearch()
	{
		readShopContents();
	}
	
	private void onClickLevel()
	{
		if ( m_curLevel == 1 )
		{
			m_curLevel = 0;
			m_imgLevel.setImageResource(R.drawable.baseuser_add_checkbox1);
		}
		else
		{
			m_curLevel = 1;
			m_imgLevel.setImageResource(R.drawable.baseuser_add_checkbox2);
		}
		
		readMainContents();
	}
	
	private void showDetailLayout()
	{
		m_txtShopName.setText(m_curClickedShopInfo.name);
		m_txtShopPermit.setText(m_curClickedShopInfo.permit_id);
		m_txtShopPhone.setText(m_curClickedShopInfo.mobile_phone);
		
		ll_detail.setVisibility(View.VISIBLE);
		ll_main.setVisibility(View.INVISIBLE);
	}
	
	private void onClickDetailClose()
	{
		ll_main.setVisibility(View.VISIBLE);
		ll_detail.setVisibility(View.INVISIBLE);
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = m_txtStartdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_statisticshop_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = m_txtEnddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_statisticshop_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickDatePopupOk()
	{
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
		{
			m_txtStartdate.setText(strDate);
		}
		else
		{
			if ( strDate.compareTo(m_txtEnddate.getText().toString()) < 0 )
				showToastMessage(getResources().getString(R.string.error_date_startend));
			else
				m_txtEnddate.setText(strDate);
		}
		
		
		m_MaskLayer.setVisibility(View.INVISIBLE);		
		readMainContents();
	}
	
	private void onClickDatePopupCancel()
	{
		dialog_datepicker.dismiss();
		m_MaskLayer.setVisibility(View.INVISIBLE);
	}
	
	private void onClickRegionSelect()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_region.showAtLocation(findViewById(R.id.ll_statisticshop_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogRegionAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_region));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_region != null && dialog_region.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_region.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_RegionList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_RegionList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickRegionItem(position);
        	}
		});
		
		dialog_region = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_region.setAnimationStyle(-1);
		
		if ( cnt > 0 )
		{
			m_curSelRegion = 0;
			m_txtRegion.setText(m_RegionList.get(0).name);
		}
	}
	
	private void onClickRegionItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_region.dismiss();
		
		m_curSelRegion = pos;
		
		m_txtRegion.setText(m_RegionList.get(pos).name);
		
		readShopContents();
	}

	private void onClickShopSelect()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_shop.showAtLocation(findViewById(R.id.ll_statisticshop_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogShopAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_shop));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_shop != null && dialog_shop.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_shop.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_ShopList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_ShopList.get(i).shop_name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickShopItem(position);
        	}
		});
		
		dialog_shop = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_shop.setAnimationStyle(-1);
		
		if ( cnt > 0 )
		{
			m_curSelShop = 0;
			m_txtShop.setText(m_ShopList.get(0).shop_name);
		}
	}
	
	private void onClickShopItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_shop.dismiss();
		
		m_curSelShop = pos;
		
		m_txtShop.setText(m_ShopList.get(pos).shop_name);
		
		readMainContents();
	}
	
	private void setStatisticShopAdapter() {
		m_StatisticShopList = new ArrayList<STStatisticShopInfo>();
		m_lvStatisticShopListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvStatisticShopListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETSHOPSTATISTIC;
                new LoadResponseThread(StatisticShopActivity.this).start();
            }
        });

        mRealListView = m_lvStatisticShopListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mRealListView.setDrawSelectorOnTop(true);
        
        m_StatisticShopAdapter = new StatisticShopAdapter(StatisticShopActivity.this, m_StatisticShopList);
		mRealListView.setAdapter(m_StatisticShopAdapter);
		
		mRealListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				onLongClickItem(parent, position);
				return true;
	    	}
		});
	}
	
	private void onLongClickItem(View view, int position)
	{
		m_curClickedItem = position - 1;
		
		m_reqType = REQ_TYPE.REQ_GETSHOPINFO;
		new LoadResponseThread(StatisticShopActivity.this).start();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETLASTREGIONLIST )
		{
			setDialogRegionAdapter();
			readShopContents();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFOFROMNICKNAME )
		{
			setDialogShopAdapter();
			readMainContents();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETSHOPSTATISTIC )
		{
			m_StatisticShopAdapter.notifyDataSetChanged();
			m_lvStatisticShopListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFO )
		{
			showDetailLayout();
		}
	}
	
	public void getResponseJSON() {
		try {
			
			if ( m_reqType == REQ_TYPE.REQ_GETLASTREGIONLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETLASTREGIONLIST;
				strRequest += "?role=" + String.valueOf(Global.Cur_AdminRole);
				strRequest += "&region_id=" + String.valueOf(Global.Cur_AdminRegionId);
				
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
					
					for (int i = 0; i < count; i++)
					{
						STRegionInfo itemInfo = new STRegionInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.id = tmpObj.getInt("region_id");
						itemInfo.name = tmpObj.getString("region_name");
						
						m_RegionList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFOFROMNICKNAME )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSHOPINFOFROMNICKNAME;
				strRequest += "?nickname=" + EncodeToUTF8(m_editShopSearch.getText().toString());
				strRequest += "&region_id=" + String.valueOf(m_RegionList.get(m_curSelRegion).id);
				
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
					
					for (int i = 0; i < count; i++)
					{
						STShopSimpleInfo itemInfo = new STShopSimpleInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.shop_id = tmpObj.getInt("shop_id");
						itemInfo.shop_name = tmpObj.getString("shop_name");
						
						m_ShopList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETSHOPSTATISTIC )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSHOPSTATISTIC;
				strRequest += "?region_id=" + String.valueOf(m_RegionList.get(m_curSelRegion).id);
				strRequest += "&level=" + String.valueOf(m_curLevel);
				strRequest += "&shop_id=" + String.valueOf(m_ShopList.get(m_curSelShop).shop_id);
				strRequest += "&search_key=" + EncodeToUTF8(m_editShopSearch.getText().toString());
				strRequest += "&start_date=" + m_txtStartdate.getText().toString();
				strRequest += "&end_date=" + m_txtEnddate.getText().toString();
				strRequest += "&pagenum=" + String.valueOf(m_nCurPageNumber);
				
				
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
					
					for (int i = 0; i < count; i++)
					{
						STStatisticShopInfo itemInfo = new STStatisticShopInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.region_name = tmpObj.getString("region_name");
						itemInfo.shop_id = tmpObj.getLong("shop_id");
						itemInfo.shop_name = tmpObj.getString("shop_name");
						itemInfo.shop_lawman = tmpObj.getString("shop_lawman");
						itemInfo.sale_count = tmpObj.getDouble("sale_count");
						itemInfo.remain_count = tmpObj.getDouble("remain_count");
						
						m_StatisticShopList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFO )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSHOPINFO;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&shop_id=" + String.valueOf(m_StatisticShopList.get(m_curClickedItem).shop_id);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					m_curClickedShopInfo.name = dataObject.getString("name");
					m_curClickedShopInfo.permit_id = dataObject.getString("permit_id");
					m_curClickedShopInfo.mobile_phone = dataObject.getString("mobile_phone");
					m_curClickedShopInfo.longitude = dataObject.getDouble("longitude");
					m_curClickedShopInfo.latitude = dataObject.getDouble("latitude");
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
