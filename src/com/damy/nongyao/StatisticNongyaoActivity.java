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
import com.damy.adapters.MoneyBankDetailAdapter;
import com.damy.adapters.StatisticNongyaoAdapter;
import com.damy.adapters.StatisticNongyaoDetailAdapter;
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
import com.damy.datatypes.STCatalogInfo;
import com.damy.datatypes.STNongYaoInfo;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STShopInfo;
import com.damy.datatypes.STShopSimpleInfo;
import com.damy.datatypes.STStatisticNongyaoDetailInfo;
import com.damy.datatypes.STStatisticNongyaoInfo;
import com.damy.datatypes.STStatisticShopInfo;
import com.damy.backend.*;

public class StatisticNongyaoActivity extends BaseActivity {
	
private enum REQ_TYPE{REQ_GETLASTREGIONLIST, REQ_GETNONGYAOKINDLIST, REQ_GETCATALOGINFOFROMNICKNAME, REQ_GETCATALOGSTATISTIC, REQ_GETCATALOGDETAILSTATISTIC};
	
	private REQ_TYPE							m_reqType;
	
	
	private PullToRefreshListView				m_lvStatisticNongyaoListView;
	private ArrayList<STStatisticNongyaoInfo> 	m_StatisticNongyaoList;
	private StatisticNongyaoAdapter				m_StatisticNongyaoAdapter = null;
	private ListView							mRealListView;
	
	private StatisticNongyaoDetailAdapter				m_StatisticNongyaoDetailAdapter;
	private ListView									m_lvStatisticNongyaoDetailListView;
	private ArrayList<STStatisticNongyaoDetailInfo> 	m_StatisticNongyaoDetailList= new ArrayList<STStatisticNongyaoDetailInfo>();;
	
	private ArrayList<STRegionInfo>				m_RegionList = new ArrayList<STRegionInfo>();
	private ArrayList<STNongYaoInfo>			m_NongyaoList = new ArrayList<STNongYaoInfo>();
	private ArrayList<STCatalogInfo>			m_CatalogList = new ArrayList<STCatalogInfo>();
	
	private AutoSizeTextView					m_txtRegion;
	private AutoSizeTextView					m_txtNongyao;
	private AutoSizeTextView					m_txtStartdate;
	private AutoSizeTextView					m_txtEnddate;
	private AutoSizeEditText					m_editCatalogSearch;
	private AutoSizeTextView					m_txtCatalog;
	
	private AutoSizeTextView					m_txtDeatilInfo;
	
	private PopupWindow 						dialog_region;
	private PopupWindow 						dialog_nongyao;
	private PopupWindow 						dialog_catalog;
	private PopupWindow 						dialog_datepicker;
	
	private int									m_curSelDateType;
	private DatePicker							m_DatePicker;
	
	private int									m_curClickedItem = 0;
	
	private int									m_nCurPageNumber = 1;
	private int									m_curSelRegion = 0;
	private int									m_curSelNongyao = 0;
	private int									m_curSelCatalog = 0;

	
	private LinearLayout						ll_main;
	private LinearLayout						ll_detail;
	private LinearLayout						m_MaskLayer;
	
	private String								m_DetailStartDate;
	private String								m_DetailEndDate;
	private long								m_DetailRegionId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistic_nongyao);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_statisticnongyao));
		
		initControls();
		readRegionContents();
		setStatisticNongyaoAdapter();
		setStatisticNongyaoDetailAdapter();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_statistic_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_statistic_homebtn);
		FrameLayout fl_searchbtn = (FrameLayout)findViewById(R.id.fl_statisticnongyao_searchbtn);
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
		
		fl_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCatalogSearch();
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
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_statisticnongyao_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_txtRegion = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_region);
		m_editCatalogSearch = (AutoSizeEditText)findViewById(R.id.edit_statisticnongyao_catalogsearch);
		m_txtNongyao = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_nongtype);
		m_txtCatalog = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_catalog);
		m_txtStartdate = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_startdate);
		m_txtEnddate = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_enddate);
		
		LinearLayout ll_Region = (LinearLayout)findViewById(R.id.ll_statisticnongyao_region);
		LinearLayout ll_Nongyao = (LinearLayout)findViewById(R.id.ll_statisticnongyao_nongtype);
		LinearLayout ll_Catalog = (LinearLayout)findViewById(R.id.ll_statisticnongyao_catalog);
		
		ll_Region.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRegionSelect();
        	}
        });
		
		ll_Nongyao.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickNongyaoSelect();
        	}
        });
		
		ll_Catalog.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCatalogSelect();
        	}
        });
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_statisticnongyao_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_statisticnongyao_enddate);
		
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
		
		m_lvStatisticNongyaoListView = (PullToRefreshListView)findViewById(R.id.anStatisticNongyaoContentView);
		
		m_lvStatisticNongyaoDetailListView = (ListView)findViewById(R.id.anStatisticNongyaoDetailContentView);
		
		ll_main = (LinearLayout)findViewById(R.id.ll_statisticnongyao_main);
		ll_detail = (LinearLayout)findViewById(R.id.ll_statisticnongyao_detail);
		
		ll_detail.setVisibility(View.INVISIBLE);
		
		FrameLayout ll_detailclose = (FrameLayout)findViewById(R.id.fl_statisticnongyao_detail_close);
		ll_detailclose.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDetailClose();
        	}
        });
		
		m_txtDeatilInfo = (AutoSizeTextView)findViewById(R.id.txt_statisticnongyao_detailinfo);
	}
	
	private void readRegionContents()
	{
		m_RegionList.clear();
		
		STRegionInfo itemInfo = new STRegionInfo();
		itemInfo.id = 0;
		itemInfo.name = getResources().getString(R.string.common_all);
		m_RegionList.add(itemInfo);
		
		m_reqType = REQ_TYPE.REQ_GETLASTREGIONLIST;
		new LoadResponseThread(StatisticNongyaoActivity.this).start();
	}
	
	private void readNongyaoContents()
	{
		m_NongyaoList.clear();
		
		STNongYaoInfo itemInfo = new STNongYaoInfo();
		itemInfo.id = 0;
		itemInfo.name = getResources().getString(R.string.common_all);
		m_NongyaoList.add(itemInfo);
		
		m_reqType = REQ_TYPE.REQ_GETNONGYAOKINDLIST;
		new LoadResponseThread(StatisticNongyaoActivity.this).start();
	}
	
	private void readCatalogContents()
	{
		m_CatalogList.clear();
		
		STCatalogInfo itemInfo = new STCatalogInfo();
		itemInfo.catalog_id = 0;
		itemInfo.catalog_name = getResources().getString(R.string.common_all);
		m_CatalogList.add(itemInfo);
		
		m_reqType = REQ_TYPE.REQ_GETCATALOGINFOFROMNICKNAME;
		new LoadResponseThread(StatisticNongyaoActivity.this).start();
	}
	
	private void readMainContents()
	{
		m_nCurPageNumber = 1;
		
		m_StatisticNongyaoList.clear();
		m_reqType = REQ_TYPE.REQ_GETCATALOGSTATISTIC;
		new LoadResponseThread(StatisticNongyaoActivity.this).start();
	}
	
	private void readDetailContents()
	{
		m_StatisticNongyaoDetailList.clear();
		
		m_reqType = REQ_TYPE.REQ_GETCATALOGDETAILSTATISTIC;
		new LoadResponseThread(StatisticNongyaoActivity.this).start();
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
		Intent shop_activity = new Intent(this, StatisticShopActivity.class);
		startActivity(shop_activity);
		finish();
	}
	
	private void onClickNongyao()
	{
	}
	
	private void onClickPolicy()
	{
		Intent policy_activity = new Intent(this, StatisticPolicyActivity.class);
		startActivity(policy_activity);	
		finish();
	}
	
	private void onClickCatalogSearch()
	{
		readCatalogContents();
	}
	
	private void onClickStartDate()
	{
		m_curSelDateType = 1;
		String strDate = m_txtStartdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_statisticnongyao_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		m_MaskLayer.setVisibility(View.VISIBLE);
	}
	
	private void onClickEndDate()
	{
		m_curSelDateType = 2;
		String strDate = m_txtEnddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_statisticnongyao_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
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
		dialog_region.showAtLocation(findViewById(R.id.ll_statisticnongyao_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
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
		
		readMainContents();
	}
	
	private void onClickNongyaoSelect()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_nongyao.showAtLocation(findViewById(R.id.ll_statisticnongyao_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogNongyaoAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_kind));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_nongyao != null && dialog_nongyao.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_nongyao.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_NongyaoList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_NongyaoList.get(i).name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickNongyaoItem(position);
        	}
		});
		
		dialog_nongyao = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_nongyao.setAnimationStyle(-1);
		
		if ( cnt > 0 )
		{
			m_curSelNongyao = 0;
			m_txtNongyao.setText(m_NongyaoList.get(0).name);
		}
	}
	
	private void onClickNongyaoItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_nongyao.dismiss();
		
		m_curSelNongyao = pos;
		
		m_txtNongyao.setText(m_NongyaoList.get(pos).name);
		
		readCatalogContents();
	}
	
	private void onClickCatalogSelect()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_catalog.showAtLocation(findViewById(R.id.ll_statisticnongyao_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void setDialogCatalogAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_catalog));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_catalog != null && dialog_catalog.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_catalog.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_CatalogList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_CatalogList.get(i).catalog_name);
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickCatalogItem(position);
        	}
		});
		
		dialog_catalog = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_catalog.setAnimationStyle(-1);
		
		if ( cnt > 0 )
		{
			m_curSelCatalog = 0;
			m_txtCatalog.setText(m_CatalogList.get(0).catalog_name);
		}
	}
	
	private void onClickCatalogItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_catalog.dismiss();
		
		m_curSelCatalog = pos;
		
		m_txtCatalog.setText(m_CatalogList.get(pos).catalog_name);
		
		readMainContents();
	}
	
	private void setStatisticNongyaoAdapter() {
		m_StatisticNongyaoList = new ArrayList<STStatisticNongyaoInfo>();
		m_lvStatisticNongyaoListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvStatisticNongyaoListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETCATALOGSTATISTIC;
                new LoadResponseThread(StatisticNongyaoActivity.this).start();
            }
        });

        mRealListView = m_lvStatisticNongyaoListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //mRealListView.setDrawSelectorOnTop(true);
        
        m_StatisticNongyaoAdapter = new StatisticNongyaoAdapter(StatisticNongyaoActivity.this, m_StatisticNongyaoList);
		mRealListView.setAdapter(m_StatisticNongyaoAdapter);
	}
	
	public void onClickDetail(int pos, View v)
	{
		m_curClickedItem = pos;
		
		m_DetailStartDate = m_txtStartdate.getText().toString();
		m_DetailEndDate = m_txtEnddate.getText().toString();
		m_DetailRegionId = m_RegionList.get(m_curSelRegion).id;
		
		readDetailContents();
	}
	
	private void onClickDetailClose()
	{
		ll_main.setVisibility(View.VISIBLE);
		ll_detail.setVisibility(View.INVISIBLE);
	}
	
	private void setStatisticNongyaoDetailAdapter()
	{ 
		m_lvStatisticNongyaoDetailListView.setCacheColorHint(Color.TRANSPARENT);
		m_lvStatisticNongyaoDetailListView.setDividerHeight(0);
		
		m_StatisticNongyaoDetailAdapter = new StatisticNongyaoDetailAdapter(StatisticNongyaoActivity.this, m_StatisticNongyaoDetailList);
		m_lvStatisticNongyaoDetailListView.setAdapter(m_StatisticNongyaoDetailAdapter);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETLASTREGIONLIST )
		{
			setDialogRegionAdapter();
			readNongyaoContents();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETNONGYAOKINDLIST )
		{
			setDialogNongyaoAdapter();
			readCatalogContents();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMNICKNAME )
		{
			setDialogCatalogAdapter();
			readMainContents();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGSTATISTIC )
		{
			m_StatisticNongyaoAdapter.notifyDataSetChanged();
			m_lvStatisticNongyaoListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGDETAILSTATISTIC )
		{
			setStatisticNongyaoDetailAdapter();
			
			ll_detail.setVisibility(View.VISIBLE);
			ll_main.setVisibility(View.INVISIBLE);
			
			STStatisticNongyaoInfo item = m_StatisticNongyaoList.get(m_curClickedItem);
			m_txtDeatilInfo.setText(item.catalog_name + "-" + item.standard);// + "-" + m_RegionList.get(m_curSelRegion).name);
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
			else if ( m_reqType == REQ_TYPE.REQ_GETNONGYAOKINDLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETNONGYAOKINDLIST;
				
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
						STNongYaoInfo itemInfo = new STNongYaoInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.id = tmpObj.getInt("nongyao_id");
						itemInfo.name = tmpObj.getString("nongyao_name");
						
						m_NongyaoList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGINFOFROMNICKNAME )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGINFOFROMNICKNAME;
				strRequest += "?nickname=" + EncodeToUTF8(m_editCatalogSearch.getText().toString());
				strRequest += "&nongyao_id=" + String.valueOf(m_NongyaoList.get(m_curSelNongyao).id);
				
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
						STCatalogInfo itemInfo = new STCatalogInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.catalog_id = tmpObj.getInt("catalog_id");
						itemInfo.catalog_name = tmpObj.getString("catalog_name");
						
						m_CatalogList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGSTATISTIC )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGSTATISTIC;
				strRequest += "?region_id=" + String.valueOf(m_RegionList.get(m_curSelRegion).id);
				strRequest += "&nongyao_id=" + String.valueOf(m_NongyaoList.get(m_curSelNongyao).id);
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogList.get(m_curSelCatalog).catalog_id);
				strRequest += "&search_key=" + EncodeToUTF8(m_editCatalogSearch.getText().toString());
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
						STStatisticNongyaoInfo itemInfo = new STStatisticNongyaoInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.catalog_id = tmpObj.getInt("catalog_id");
						itemInfo.catalog_name = tmpObj.getString("catalog_name");
						itemInfo.product = tmpObj.getString("product");
						itemInfo.standard = tmpObj.getString("standard");
						itemInfo.standard_id = tmpObj.getString("standard_id");
						itemInfo.remain_count = tmpObj.getDouble("remain_count");
						itemInfo.sale_count = tmpObj.getDouble("sale_count");
						
						m_StatisticNongyaoList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGDETAILSTATISTIC )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGDETAILSTATISTIC;
				strRequest += "?catalog_id=" + String.valueOf(m_StatisticNongyaoList.get(m_curClickedItem).catalog_id);
				strRequest += "&standard_id=" + String.valueOf(m_StatisticNongyaoList.get(m_curClickedItem).standard_id);
				strRequest += "&region_id=" + String.valueOf(m_DetailRegionId);
				strRequest += "&start_date=" + m_DetailStartDate;
				strRequest += "&end_date=" + m_DetailEndDate;
				
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
						STStatisticNongyaoDetailInfo itemInfo = new STStatisticNongyaoDetailInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.largenumber = tmpObj.getString("largenumber");
						itemInfo.product_date = tmpObj.getString("product_date");
						itemInfo.avail_date = tmpObj.getInt("avail_date");
						itemInfo.remain_count = tmpObj.getLong("remain_count");
						itemInfo.sale_count = tmpObj.getLong("sale_count");
						itemInfo.total_count = tmpObj.getLong("total_count");
						itemInfo.shop_name = tmpObj.getString("shop_name");
						
						m_StatisticNongyaoDetailList.add(itemInfo);
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
