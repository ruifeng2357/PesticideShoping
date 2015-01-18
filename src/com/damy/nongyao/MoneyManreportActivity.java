package com.damy.nongyao;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.adapters.MoneyBankAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STMoneyBankInfo;

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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MoneyManreportActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_BUYING, REQ_SALE, REQ_STOREREMAIN, REQ_LOST, REQ_STORE, REQ_GIVETAKE, REQ_MONEY};
	
	private PopupWindow 				dialog_datepicker;
    private LinearLayout				m_MaskLayer;
    private LinearLayout                m_StoreStatistic;
    private LinearLayout                m_StoreRemainStatistic;
    private LinearLayout                m_LostStatistic;
    private LinearLayout                m_GiveTakeStatistic;
    private LinearLayout                m_SaleStatistic;
    private LinearLayout                m_BuyingStatistic;
    private LinearLayout                m_MoneyStatistic;    
    
    private PullToRefreshListView		m_lvMoneyBankListView;
	private ArrayList<STMoneyBankInfo> 	m_MoneyBankList;
	private MoneyBankAdapter			m_MoneyBankAdapter = null;
	private ListView					mRealListView;
    
    private int							m_curSelDateType;
    private DatePicker					m_DatePicker;
    
    private int							m_nCurPageNumber = 1;
    
    private AutoSizeTextView			txt_startdate;
    private AutoSizeTextView			txt_enddate;
    private AutoSizeTextView            txt_real_startdate;
    private AutoSizeTextView            txt_real_enddate;
    
    private REQ_TYPE					m_reqType;
    
    private AutoSizeTextView            m_Buying_View1;
    private AutoSizeTextView            m_Buying_View2;
    private AutoSizeTextView            m_Buying_View3;
    private AutoSizeTextView            m_Buying_View4;
    private AutoSizeTextView            m_Buying_View5;
    private AutoSizeTextView            m_Buying_View6;
    
    private AutoSizeTextView            m_Sale_View1;
    private AutoSizeTextView            m_Sale_View2;
    private AutoSizeTextView            m_Sale_View3;
    private AutoSizeTextView            m_Sale_View4;
    private AutoSizeTextView            m_Sale_View5;
    private AutoSizeTextView            m_Sale_View6;
    
    private AutoSizeTextView            m_StoreRemain_View1;
    private AutoSizeTextView            m_StoreRemain_View2;
    
    private AutoSizeTextView            m_Lost_View1;
    private AutoSizeTextView            m_Lost_View2;
    private AutoSizeTextView            m_Lost_View3;
    private AutoSizeTextView            m_Lost_View4;
    
    private AutoSizeTextView            m_Store_View1;
    private AutoSizeTextView            m_Store_View2;
    
    private AutoSizeTextView            m_GiveTake_View1;
    private AutoSizeTextView            m_GiveTake_View2;
    
    private AutoSizeTextView            m_Money_View1;
    private AutoSizeTextView            m_Money_View2;
    private AutoSizeTextView            m_Money_View3;  
    
    private ArrayList<String>           m_BuyingData = new ArrayList<String>();
    private ArrayList<String>           m_SaleData = new ArrayList<String>();
    private ArrayList<String>           m_StoreRemainData = new ArrayList<String>();
    private ArrayList<String>           m_LostData = new ArrayList<String>();
    private ArrayList<String>           m_StoreData = new ArrayList<String>();
    private ArrayList<String>           m_GiveTakeData = new ArrayList<String>();
    private ArrayList<String>           m_MoneyData = new ArrayList<String>();
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_manreport);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneymanreport));
		
		initContents();
		//setMoneyBankAdapter();
	}
	/*
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_nCurPageNumber = 1;
		m_MoneyBankList.clear();
		m_reqType = REQ_TYPE.REQ_GETBANKCASHLOGLIST;
		new LoadResponseThread(MoneyManreportActivity.this).start();
	}
	*/
	private void initContents()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_money_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_money_homebtn);
		FrameLayout fl_reportbtn = (FrameLayout)findViewById(R.id.fl_moneyreport_btn);
		FrameLayout fl_paymentbtn = (FrameLayout)findViewById(R.id.fl_moneypayment_logbtn);
		FrameLayout fl_otherbtn = (FrameLayout)findViewById(R.id.fl_moneyother_paymentbtn);
		FrameLayout fl_cashbtn = (FrameLayout)findViewById(R.id.fl_moneycash_btn);
		FrameLayout fl_manreportbtn = (FrameLayout)findViewById(R.id.fl_moneymanrepot_btn);
		
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
		
		fl_reportbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickReport();
        	}
        });
		
		fl_paymentbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPayment();
        	}
        });
		
		fl_otherbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickOther();
        	}
        });
		
		fl_cashbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCash();
        	}
        });
		
		fl_manreportbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickManreport();
        	}
        });
		
		FrameLayout fl_manreport_buying_statistic = (FrameLayout)findViewById(R.id.fl_manreport_buying_statistic);
		FrameLayout fl_manreport_sale_statistic = (FrameLayout)findViewById(R.id.fl_manreport_sale_statistic);
		FrameLayout fl_manreport_storeremain_statistic = (FrameLayout)findViewById(R.id.fl_manreport_storeremain_statistic);
		FrameLayout fl_manreport_lost_statistic = (FrameLayout)findViewById(R.id.fl_manreport_lost_statistic);
		FrameLayout fl_manreport_store_statistic = (FrameLayout)findViewById(R.id.fl_manreport_store_statistic);
		FrameLayout fl_manreport_givetake_statistic = (FrameLayout)findViewById(R.id.fl_manreport_givetake_statistic);
		FrameLayout fl_manreport_money_statistic = (FrameLayout)findViewById(R.id.fl_manreport_money_statistic);
		FrameLayout fl_manreport_addbtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_addbtn);
		
		
		fl_manreport_buying_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBuying();
        	}
        });
		
		fl_manreport_sale_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSale();
        	}
        });
		
		fl_manreport_storeremain_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStoreRemain();
        	}
        });
		
		fl_manreport_lost_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLost();
        	}
        });
		
		fl_manreport_store_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStore();
        	}
        });
		
		fl_manreport_givetake_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickGiveTake();
        	}
        });
		
		fl_manreport_money_statistic.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickMoney();
        	}
        });
		
		fl_manreport_addbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickAdd();
        	}
        });
		
		LinearLayout ll_startdate = (LinearLayout)findViewById(R.id.ll_moneymanreport_startdate);
		LinearLayout ll_enddate = (LinearLayout)findViewById(R.id.ll_moneymanreport_enddate);
		
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
		
		FrameLayout fl_moneymanreport_buyingstatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_buyingstatistic_closebtn);
		FrameLayout fl_moneymanreport_salestatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_salestatistic_closebtn);
		FrameLayout fl_moneymanreport_storeremainstatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_storeremainstatistic_closebtn);
		FrameLayout fl_moneymanreport_loststatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_loststatistic_closebtn);
		FrameLayout fl_moneymanreport_storestatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_storestatistic_closebtn);
		FrameLayout fl_moneymanreport_givetakestatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_givetakestatistic_closebtn);
		FrameLayout fl_moneymanreport_moneystatistic_closebtn = (FrameLayout)findViewById(R.id.fl_moneymanreport_moneystatistic_closebtn);
		
		fl_moneymanreport_buyingstatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseBuying();
        	}
        });
		
		fl_moneymanreport_salestatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseSale();
        	}
        });
		
		fl_moneymanreport_storeremainstatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseStoreRemain();
        	}
        });
		
		fl_moneymanreport_loststatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseLost();
        	}
        });
		
		fl_moneymanreport_storestatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseStore();
        	}
        });
		
		fl_moneymanreport_givetakestatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseGiveTake();
        	}
        });
		
		fl_moneymanreport_moneystatistic_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onCloseMoney();
        	}
        });
		
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneymanreport_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_StoreStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_storestatistic);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		
		m_StoreRemainStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_storeremainstatistic);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		
		m_LostStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_loststatistic);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		
		m_GiveTakeStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_givetakestatistic);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		
		m_SaleStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_salestatistic);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		
		m_BuyingStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_buyingstatistic);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		
		m_MoneyStatistic = (LinearLayout)findViewById(R.id.ll_moneymanreport_moneystatistic);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);
		
		m_MaskLayer.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
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

		txt_startdate = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_startdate);
		txt_enddate = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_enddate);
		
		txt_real_startdate = (AutoSizeTextView)findViewById(R.id.txt_manreport_real_startdate);
		txt_real_enddate = (AutoSizeTextView)findViewById(R.id.txt_manreport_real_enddate);
		
		Date curDate = new Date();
		Date beforeDate = new Date();

		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		
		txt_enddate.setText(strDate);
		txt_real_enddate.setText(strDate);
		
		strDate = String.valueOf(beforeDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-1" ;
		
		txt_startdate.setText(strDate);		
		txt_real_startdate.setText(strDate);		
		
		m_Buying_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view1);
		m_Buying_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view2);
		m_Buying_View3 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view3);
		m_Buying_View4 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view4);
		m_Buying_View5 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view5);
		m_Buying_View6 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_buyingstatistic_view6);
		
		m_Sale_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view1);
		m_Sale_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view2);
		m_Sale_View3 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view3);
		m_Sale_View4 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view4);
		m_Sale_View5 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view5);
		m_Sale_View6 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_salestatistic_view6);
		
		m_StoreRemain_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_storeremainstatistic_view1);
		m_StoreRemain_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_storeremainstatistic_view2);
		
		m_Lost_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_loststatistic_view1);
		m_Lost_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_loststatistic_view2);
		m_Lost_View3 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_loststatistic_view3);
		m_Lost_View4 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_loststatistic_view4);
		
		m_Store_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_storestatistic_view1);
		m_Store_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_storestatistic_view2);
		
		m_GiveTake_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_givetakestatistic_view1);
		m_GiveTake_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_givetakestatistic_view2);
		
		m_Money_View1 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_moneystatistic_view1);
		m_Money_View2 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_moneystatistic_view2);
		m_Money_View3 = (AutoSizeTextView)findViewById(R.id.txt_moneymanreport_moneystatistic_view3);
		
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
	
	private void onClickReport()
	{
		Intent report_activity = new Intent(this, MoneyReportActivity.class);
		startActivity(report_activity);
		finish();	
	}
	
	private void onClickPayment()
	{
		Intent payment_activity = new Intent(this, MoneyPaymentActivity.class);
		startActivity(payment_activity);
		finish();
	}
	
	private void onClickOther()
	{
		Intent other_activity = new Intent(this, MoneyOtherpayActivity.class);
		startActivity(other_activity);	
		finish();
	}
	
	private void onClickCash()
	{
		Intent bank_activity = new Intent(this, MoneyBankActivity.class);
		startActivity(bank_activity);	
		finish();
	}
	
	private void onClickManreport()
	{
		/*
		Intent manreport_activity = new Intent(this, MoneyManreportActivity.class);
		startActivity(manreport_activity);
		finish();
		*/
	}
	
	private void onClickBuying()
	{		
		m_Buying_View1.setText("");
		m_Buying_View2.setText("");
		m_Buying_View3.setText("");
		m_Buying_View4.setText("");
		m_Buying_View5.setText("");
		m_Buying_View6.setText("");
		
		m_reqType = REQ_TYPE.REQ_BUYING;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowBuying()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.VISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onCloseBuying()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onClickSale()
	{

		m_Sale_View1.setText("");
		m_Sale_View2.setText("");
		m_Sale_View3.setText("");
		m_Sale_View4.setText("");
		m_Sale_View5.setText("");
		m_Sale_View6.setText("");
		
		m_reqType = REQ_TYPE.REQ_SALE;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowSale()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.VISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);		
	}
	
	private void onCloseSale()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
	}	
	
	private void onClickStoreRemain()
	{
		m_StoreRemain_View1.setText("");
		m_StoreRemain_View2.setText("");
		
		m_reqType = REQ_TYPE.REQ_STOREREMAIN;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowStoreRemain()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.VISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);		
	}
	
	private void onCloseStoreRemain()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
	}	
	
	private void onClickLost()
	{
		m_Lost_View1.setText("");
		m_Lost_View2.setText("");
		m_Lost_View3.setText("");
		m_Lost_View4.setText("");	
		
		m_reqType = REQ_TYPE.REQ_LOST;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowLost()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.VISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onCloseLost()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onClickStore()
	{
		m_Store_View1.setText("");
		m_Store_View2.setText("");
		
		m_reqType = REQ_TYPE.REQ_STORE;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowStore()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.VISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);			
	}
	
	private void onCloseStore()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onClickGiveTake()
	{		
		m_GiveTake_View1.setText("");
		m_GiveTake_View2.setText("");		
		
		m_reqType = REQ_TYPE.REQ_GIVETAKE;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowGiveTake()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.VISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onCloseGiveTake()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onClickMoney()
	{		
		m_Money_View1.setText("");
		m_Money_View2.setText("");
		m_Money_View3.setText("");
		
		m_reqType = REQ_TYPE.REQ_MONEY;
		new LoadResponseThread(MoneyManreportActivity.this).start();	
	}
	
	private void ShowMoney()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		m_BuyingStatistic.setVisibility(View.INVISIBLE);
		m_SaleStatistic.setVisibility(View.INVISIBLE);
		m_StoreRemainStatistic.setVisibility(View.INVISIBLE);
		m_LostStatistic.setVisibility(View.INVISIBLE);
		m_StoreStatistic.setVisibility(View.INVISIBLE);
		m_GiveTakeStatistic.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.VISIBLE);
	}
	
	private void onCloseMoney()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		m_MoneyStatistic.setVisibility(View.INVISIBLE);
	}
	
	private void onClickAdd()
	{
		txt_real_startdate.setText(txt_startdate.getText());
		txt_real_enddate.setText(txt_enddate.getText());
	}
	
	private void onClickStartDate()
	{		
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		m_curSelDateType = 1;
		String strDate = txt_startdate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneymanreport_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickEndDate()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		m_curSelDateType = 2;
		String strDate = txt_enddate.getText().toString();
		String[] dateStrArr = new String[3];
		dateStrArr = strDate.split("-");
		m_DatePicker.updateDate(Integer.parseInt(dateStrArr[0]), Integer.parseInt(dateStrArr[1]) - 1, Integer.parseInt(dateStrArr[2]));
		dialog_datepicker.showAtLocation(findViewById(R.id.ll_moneymanreport_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickPopupOk()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_datepicker.dismiss();
		String strDate = String.valueOf(m_DatePicker.getYear()) + "-" + String.valueOf(m_DatePicker.getMonth() + 1) + "-" + String.valueOf(m_DatePicker.getDayOfMonth());
		if ( m_curSelDateType == 1 )
		{
			txt_startdate.setText(strDate);
		}
		else
		{
			if ( strDate.compareTo(txt_startdate.getText().toString()) < 0 )
				showToastMessage(getResources().getString(R.string.error_date_startend));
			else
				txt_enddate.setText(strDate);
		}
	}
	
	private void onClickPopupCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_datepicker.dismiss();
	}
	
	public STMoneyBankInfo getItem(int position)
	{
		if (position < 0 || position >= m_MoneyBankList.size())
			return null;
		
		return m_MoneyBankList.get(position);
	}	
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_BUYING )
		{
			 m_Buying_View1.setText(m_BuyingData.get(0));
	         m_Buying_View2.setText(m_BuyingData.get(1));
	         m_Buying_View3.setText(m_BuyingData.get(2));
	         m_Buying_View4.setText(m_BuyingData.get(3));
	         m_Buying_View5.setText(m_BuyingData.get(4));
	         m_Buying_View6.setText(m_BuyingData.get(5)); 
	         ShowBuying();
		}
		else if (m_reqType == REQ_TYPE.REQ_SALE)
		{
			 m_Sale_View1.setText(m_SaleData.get(0));
			 m_Sale_View2.setText(m_SaleData.get(1));
	         m_Sale_View3.setText(m_SaleData.get(2));
	         m_Sale_View4.setText(m_SaleData.get(3));
	         m_Sale_View5.setText(m_SaleData.get(4));
	         m_Sale_View6.setText(m_SaleData.get(5));
	         ShowSale();
		}
		else if (m_reqType == REQ_TYPE.REQ_STOREREMAIN)
		{
			 m_StoreRemain_View1.setText(m_StoreRemainData.get(0));
			 m_StoreRemain_View2.setText(m_StoreRemainData.get(1));	
			 ShowStoreRemain();
		}
		else if (m_reqType == REQ_TYPE.REQ_LOST)
		{
			 m_Lost_View1.setText(m_LostData.get(0));
			 m_Lost_View2.setText(m_LostData.get(1));
	         m_Lost_View3.setText(m_LostData.get(2));
	         m_Lost_View4.setText(m_LostData.get(3));	  
	         ShowLost();
		}
		else if (m_reqType == REQ_TYPE.REQ_STORE)
		{
			 m_Store_View1.setText(m_StoreData.get(0));
			 m_Store_View2.setText(m_StoreData.get(1));
			 ShowStore();
		}
		else if (m_reqType == REQ_TYPE.REQ_GIVETAKE)
		{
			 m_GiveTake_View1.setText(m_GiveTakeData.get(0));
			 m_GiveTake_View2.setText(m_GiveTakeData.get(1));
			 ShowGiveTake();
		}
		else if (m_reqType == REQ_TYPE.REQ_MONEY)
		{
			 m_Money_View1.setText(m_MoneyData.get(0));
			 m_Money_View2.setText(m_MoneyData.get(1));
			 m_Money_View3.setText(m_MoneyData.get(2));
			 ShowMoney();
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_BUYING )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICBUYING;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_BuyingData.clear();
					 
					 m_BuyingData.add(dataList.getString("buying_count"));
			         m_BuyingData.add(dataList.getString("buying_price") + getResources().getString(R.string.common_yuan));
			         m_BuyingData.add(dataList.getString("buying_back_count"));
			         m_BuyingData.add(dataList.getString("buying_back_price") + getResources().getString(R.string.common_yuan));
			         m_BuyingData.add(dataList.getString("buying_totalcount"));
			         m_BuyingData.add(dataList.getString("buying_totalprice") + getResources().getString(R.string.common_yuan));           
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_SALE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICSALE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_SaleData.clear();
					 
					 m_SaleData.add(dataList.getString("sell_count"));
					 m_SaleData.add(dataList.getString("sell_price") + getResources().getString(R.string.common_yuan));
					 m_SaleData.add(dataList.getString("sell_back_count"));
					 m_SaleData.add(dataList.getString("sell_back_price") + getResources().getString(R.string.common_yuan));
					 m_SaleData.add(dataList.getString("sell_totalcount"));
					 m_SaleData.add(dataList.getString("sell_totalprice") + getResources().getString(R.string.common_yuan));           
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_STOREREMAIN )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICSTOREREMAIN;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_StoreRemainData.clear();
					 
					 m_StoreRemainData.add(dataList.getString("moving_in_count"));
					 m_StoreRemainData.add(dataList.getString("moving_out_count"));
					            
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_LOST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICLost;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_LostData.clear();
					 
					 m_LostData.add(dataList.getString("spending_loss_count"));
					 m_LostData.add(dataList.getString("spending_loss_price") + getResources().getString(R.string.common_yuan));
					 m_LostData.add(dataList.getString("spending_more_count"));
					 m_LostData.add(dataList.getString("spending_more_price") + getResources().getString(R.string.common_yuan));
			                    
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_STORE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICSTORE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_StoreData.clear();
					 
					 m_StoreData.add(dataList.getString("remain_count"));
					 m_StoreData.add(dataList.getString("remain_price") + getResources().getString(R.string.common_yuan));			                   
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_GIVETAKE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICGIVETAKE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_GiveTakeData.clear();
					 
					 m_GiveTakeData.add(dataList.getString("paying_in"));
					 m_GiveTakeData.add(dataList.getString("paying_out"));			                    
		        }
			}
			else if ( m_reqType == REQ_TYPE.REQ_MONEY )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTATISTICMONEY;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&start_date=" + txt_real_startdate.getText().toString();
				strRequest += "&end_date=" + txt_real_enddate.getText().toString();
								
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
	
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
										
					JSONObject dataList = dataObject.getJSONObject("data");
					
					 m_MoneyData.clear();
					 
					 m_MoneyData.add(dataList.getString("money_sell") + getResources().getString(R.string.common_yuan));
					 m_MoneyData.add(dataList.getString("money_buying") + getResources().getString(R.string.common_yuan));
					 m_MoneyData.add(dataList.getString("money_profit") + getResources().getString(R.string.common_yuan));
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
