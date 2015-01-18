package com.damy.nongyao;

import java.util.ArrayList;

import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.StoreMovingAdapter;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STCatalogDetailInfo;
import com.damy.datatypes.STCatalogInfo;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STSaleCatalogStandardInfo;
import com.damy.datatypes.STShopInfo;
import com.damy.datatypes.STStandardInfo;
import com.damy.datatypes.STStoreInfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class StoreMovingActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSTORELIST, REQ_GETCATALOGLISTFROMSTORE, REQ_GETREMAINCATALOGSTANDARDLIST, REQ_GETLARGENUMBERLIST, REQ_MOVINGCATALOG};
	
	private PopupWindow 				dialog_store;
	private PopupWindow 				dialog_catalog;
	private PopupWindow 				dialog_standard;
	private PopupWindow 				dialog_largenumber;
	private PopupWindow 				popup_delconfirm;
    private LinearLayout				m_MaskLayer;
    
    private AutoSizeTextView			txt_curstore;
    private AutoSizeTextView			txt_catalogname;
    private AutoSizeTextView			txt_deststore;
    private AutoSizeTextView			txt_standard;
    private AutoSizeTextView			txt_largenumber;
    private AutoSizeEditText			edit_searchtext;
    
    private ArrayList<STStoreInfo>	 				m_StoreList = new ArrayList<STStoreInfo>();
    private ArrayList<STCatalogInfo> 				m_CatalogList = new ArrayList<STCatalogInfo>();
    private ArrayList<STStandardInfo>				m_StandardList = new ArrayList<STStandardInfo>();
    private ArrayList<String>						m_LargenumberList = new ArrayList<String>();
    private ArrayList<STCatalogDetailInfo> 			m_StoreMovingList = new ArrayList<STCatalogDetailInfo>();
    
    
    private StoreMovingAdapter	 		m_StoreMovingAdapter;
    private ListView					m_lvStoreMovingListView;
    
    private int							m_CurStorePos = 0;
    private int							m_DestStorePos = 0;
    private int							m_CatalogPos = 0;
    private int							m_StandardPos = 0;
    private int							m_LargenumberPos = 0;
    private int							m_StoreType = 0;
    
    private int							m_MovingCount = 0;
    private String						m_MovingArray = "";
	
	private REQ_TYPE 					m_reqType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_moving);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_storemoving));
		
		initControls();
		setStoreMovingAdapter();
		readContents();
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
		
		txt_curstore = (AutoSizeTextView)findViewById(R.id.txt_storemoving_curstore);
		edit_searchtext = (AutoSizeEditText)findViewById(R.id.edit_storemoving_search);
		txt_catalogname = (AutoSizeTextView)findViewById(R.id.txt_storemoving_catalogname);
		txt_standard = (AutoSizeTextView)findViewById(R.id.txt_storemoving_standard);
		txt_largenumber = (AutoSizeTextView)findViewById(R.id.txt_storemoving_largenumber);
		txt_deststore = (AutoSizeTextView)findViewById(R.id.txt_storemoving_deststore);
		
		LinearLayout ll_curstoresel = (LinearLayout)findViewById(R.id.ll_storemoving_curstoresel);
		LinearLayout ll_catalogsel = (LinearLayout)findViewById(R.id.ll_storemoving_catalogsel);
		LinearLayout ll_standardsel = (LinearLayout)findViewById(R.id.ll_storemoving_standard);
		LinearLayout ll_largenumbersel = (LinearLayout)findViewById(R.id.ll_storemoving_largenumber);
		LinearLayout ll_deststoresel = (LinearLayout)findViewById(R.id.ll_storemoving_deststoresel);
		
		ll_curstoresel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCurStoreSelect();
        	}
        });
		
		ll_catalogsel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCatalogSelect();
        	}
        });
		
		ll_standardsel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStandardSelect();
        	}
        });
		
		ll_largenumbersel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLargenumberSelect();
        	}
        });
		
		ll_deststoresel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDestStoreSelect();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_storemoving_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_storemoving_addbtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_storemoving_savebtn);
		FrameLayout fl_cancelbtn = (FrameLayout)findViewById(R.id.fl_storemoving_cancelbtn);
		
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
		
		m_lvStoreMovingListView = (ListView)findViewById(R.id.anStoreMovingContentView);
		
		AutoSizeTextView txt_searchbtn = (AutoSizeTextView)findViewById(R.id.txt_storemoving_searchbtn);
		txt_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCatalogSearch();
        	}
        });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	onClickBack();
        	return true;
        }
        return false;
    }
	
	private void readContents()
	{
		m_reqType = REQ_TYPE.REQ_GETSTORELIST;
		new LoadResponseThread(StoreMovingActivity.this).start();
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
	}
	
	private void onClickSearch()
	{
		Intent search_activity = new Intent(this, StoreSearchActivity.class);
		startActivity(search_activity);
		finish();
	}
	
	private void onClickHistory()
	{
		Intent history_activity = new Intent(this, StoreUsingActivity.class);
		startActivity(history_activity);	
		finish();
	}
	
	private void onClickCurStoreSelect()
	{
		if ( m_StoreList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			m_StoreType = 0; 
			dialog_store.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickCatalogSelect()
	{
		if ( m_CatalogList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_catalog.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickStandardSelect()
	{
		if ( m_StandardList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_standard.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickLargenumberSelect()
	{
		if ( m_LargenumberList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			dialog_largenumber.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickDestStoreSelect()
	{
		if ( m_StoreList.size() > 0 )
		{
			m_MaskLayer.setVisibility(View.VISIBLE);
			m_StoreType = 1;
			dialog_store.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}
	
	private void onClickCatalogSearch()
	{
		m_reqType = REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE;
		new LoadResponseThread(StoreMovingActivity.this).start();
	}
	
	private void onClickAdd()
	{
		if ( m_CatalogList.size() <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_selectemptystore));
			return;
		}
		
		STCatalogDetailInfo newItem = new STCatalogDetailInfo();
		STCatalogInfo curItem = m_CatalogList.get(m_CatalogPos);
		
		newItem.catalog_id = curItem.catalog_id;
		newItem.catalog_num = curItem.catalog_num;
		newItem.catalog_name = curItem.catalog_name;
		newItem.standard = m_StandardList.get(m_StandardPos).standard;
		newItem.standard_id = m_StandardList.get(m_StandardPos).standard_id;
		newItem.largenumber = m_LargenumberList.get(m_LargenumberPos);
		
		m_StoreMovingList.add(newItem);
		m_StoreMovingAdapter.notifyDataSetChanged();
	}
	
	private void onClickSave()
	{
		m_MovingCount = m_StoreMovingList.size();
		
		if ( m_MovingCount <= 0 )
		{
			showToastMessage(getResources().getString(R.string.error_nomovingcatalog));
			return;
		}
		
		if ( m_CurStorePos == m_DestStorePos )
		{
			showToastMessage(getResources().getString(R.string.error_srcdeststoresame));
			return;
		}
		
		m_MovingArray = "";
		
		long start_store = m_StoreList.get(m_CurStorePos).id;
		long dest_store = m_StoreList.get(m_DestStorePos).id;
		
		for ( int i = 0; i < m_MovingCount; i++ )
		{
			STCatalogDetailInfo tmp = m_StoreMovingList.get(i);
			
			if ( tmp.quantity == 0 )
			{
				showToastMessage(getResources().getString(R.string.error_zeroquantity));
				return;
			}
			
			m_MovingArray += String.valueOf(start_store) + "," + String.valueOf(dest_store) + "," + String.valueOf(tmp.catalog_id) + "," + String.valueOf(tmp.standard_id) + "," + String.valueOf(tmp.largenumber) + "," + String.valueOf(tmp.quantity) + "@";
		}
		
		m_reqType = REQ_TYPE.REQ_MOVINGCATALOG;
		new LoadResponseThread(StoreMovingActivity.this).start();
	}
	
	private void onClickCancel()
	{
		m_StoreMovingList.clear();
		m_StoreMovingAdapter.notifyDataSetChanged();
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
		
		int cnt = m_StoreList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_StoreList.get(i).name);
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
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
		
		if ( m_StoreList.size() > 0 )
		{
			m_CurStorePos = 0;
			m_DestStorePos = 0;
			txt_curstore.setText(m_StoreList.get(m_CurStorePos).name);
			txt_deststore.setText(m_StoreList.get(m_DestStorePos).name);
		}
		else
		{
			txt_curstore.setText("");
			txt_deststore.setText("");
			txt_catalogname.setText("");
			txt_standard.setText("");
			txt_largenumber.setText("");
		}
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
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
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
			m_CatalogPos = 0;
			txt_catalogname.setText(m_CatalogList.get(m_CatalogPos).catalog_name);
		}
		else
		{
			txt_catalogname.setText("");
			txt_standard.setText("");
			txt_largenumber.setText("");
		}
	}
	
	private void setDialogStandardAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_standard));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_standard != null && dialog_standard.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_standard.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_StandardList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_StandardList.get(i).standard);
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickStandardItem(position);
        	}
		});
		
		dialog_standard = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_standard.setAnimationStyle(-1);
		if ( cnt > 0 )
		{
			m_StandardPos = 0;
			txt_standard.setText(m_StandardList.get(m_StandardPos).standard);
		}
		else
			txt_standard.setText("");
	}
	
	private void setDialogLargenumberAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_largenumber));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_largenumber != null && dialog_largenumber.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_largenumber.dismiss();
                }
            }
        });
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		int cnt = m_LargenumberList.size();
		for ( int i = 0; i < cnt; i++ )
			arGeneral.add(m_LargenumberList.get(i));
		
		DialogSelectAdapter Adpater = new DialogSelectAdapter(this, arGeneral);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adpater);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickLargenumberItem(position);
        	}
		});
		
		dialog_largenumber = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_largenumber.setAnimationStyle(-1);
		if ( cnt > 0 )
		{
			m_LargenumberPos = 0;
			txt_largenumber.setText(m_LargenumberList.get(m_LargenumberPos));
		}
		else
			txt_largenumber.setText("");
	}
	
	private void setStoreMovingAdapter() {

        m_lvStoreMovingListView.setCacheColorHint(Color.TRANSPARENT);
        m_lvStoreMovingListView.setDividerHeight(0);
        m_lvStoreMovingListView.setDrawSelectorOnTop(true);

        m_StoreMovingAdapter = new StoreMovingAdapter(StoreMovingActivity.this, m_StoreMovingList);
        m_lvStoreMovingListView.setAdapter(m_StoreMovingAdapter);
	}
	
	private void onClickStoreItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_store.dismiss();
		
		if ( m_StoreType == 0 )
		{
			txt_curstore.setText(m_StoreList.get(pos).name);
			
			if ( m_CurStorePos != pos )
				onClickCancel();
			
			m_reqType = REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE;
			new LoadResponseThread(StoreMovingActivity.this).start();
			
			m_CurStorePos = pos;
		}
		else
		{
			txt_deststore.setText(m_StoreList.get(pos).name);
			
			m_DestStorePos = pos;
		}
	}
	
	private void onClickCatalogItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_catalog.dismiss();
		
		txt_catalogname.setText(m_CatalogList.get(pos).catalog_name);
		m_CatalogPos = pos;
		
		m_reqType = REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST;
		new LoadResponseThread(StoreMovingActivity.this).start();
	}
	
	private void onClickStandardItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_standard.dismiss();
		
		txt_standard.setText(m_StandardList.get(pos).standard);
		m_StandardPos = pos;
		
		m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
		new LoadResponseThread(StoreMovingActivity.this).start();
	}
	
	private void onClickLargenumberItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_largenumber.dismiss();
		
		txt_largenumber.setText(m_LargenumberList.get(pos));
		m_LargenumberPos = pos;
	}
	
	public void onKeyDownQuantityEdit(int pos, View v)
	{
		String tmp = ((AutoSizeEditText)v).getText().toString();
		if ( tmp.length() > 0 )
			m_StoreMovingList.get(pos).quantity = Integer.valueOf(tmp);	
	}
	
	public void onClickDelete(int pos)
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		
		View popupview = View.inflate(this, R.layout.dialog_delconfirm, null);
		ResolutionSet._instance.iterateChild(popupview);
		popup_delconfirm = new PopupWindow(popupview, R.dimen.common_delconfirm_dialog_width, R.dimen.common_delconfirm_dialog_height,true);
		popup_delconfirm.setAnimationStyle(-1);
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_storemoving_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_storemoving));
		
		FrameLayout fl_delconfirm_ok = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_okbtn);
		FrameLayout fl_delconfirm_cancel = (FrameLayout)popupview.findViewById(R.id.fl_dialog_delconfirm_cancelbtn);
		
		fl_delconfirm_ok.setTag(pos);
		fl_delconfirm_ok.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmOk((Integer)v.getTag());
        	}
        });
		fl_delconfirm_cancel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickDelConfirmCancel();
        	}
        });
	}
	
	private void onClickDelConfirmOk(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
		
		m_StoreMovingList.remove(pos);
		m_StoreMovingAdapter.notifyDataSetChanged();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETSTORELIST )
		{
			setDialogStoreAdapter();
			
			if ( m_StoreList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE;
				new LoadResponseThread(StoreMovingActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE )
		{
			setDialogCatalogAdapter();
			
			if ( m_CatalogList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST;
				new LoadResponseThread(StoreMovingActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
		{
			setDialogStandardAdapter();
			
			if ( m_StandardList.size() > 0 )
			{
				m_reqType = REQ_TYPE.REQ_GETLARGENUMBERLIST;
				new LoadResponseThread(StoreMovingActivity.this).start();
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETLARGENUMBERLIST )
		{
			setDialogLargenumberAdapter();
		}
		else if ( m_reqType == REQ_TYPE.REQ_MOVINGCATALOG )
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
			if ( m_reqType == REQ_TYPE.REQ_GETSTORELIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSTORELIST;
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
					
					for (int i = 0; i < count; i++)
					{
						STStoreInfo itemInfo = new STStoreInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.id = tmpObj.getInt("store_id");
						itemInfo.name = tmpObj.getString("name");
						
						m_StoreList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETCATALOGLISTFROMSTORE )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETCATALOGLISTFROMSTORE;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&search_name=" + EncodeToUTF8(edit_searchtext.getText().toString());
				
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
					
					m_CatalogList.clear();
					
					for (int i = 0; i < count; i++)
					{
						STCatalogInfo itemInfo = new STCatalogInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.catalog_id = tmpObj.getInt("catalog_id");
						itemInfo.catalog_num = tmpObj.getString("catalog_num");
						itemInfo.catalog_name = tmpObj.getString("catalog_name");
						
						m_CatalogList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETREMAINCATALOGSTANDARDLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETREMAINCATALOGSTANDARDLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogList.get(m_CatalogPos).catalog_id);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					int count = dataObject.getInt("standard_count");
					JSONArray dataList = dataObject.getJSONArray("standard_data");
					
					m_StandardList.clear();
					
					for (int i = 0; i < count; i++)
					{
						STStandardInfo itemInfo = new STStandardInfo();
						JSONObject tmpObj = dataList.getJSONObject(i);
						
						itemInfo.standard_id = tmpObj.getInt("standard_id");
						itemInfo.standard = tmpObj.getString("standard");
						
						m_StandardList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETLARGENUMBERLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETLARGENUMBERLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
				strRequest += "&store_id=" + String.valueOf(m_StoreList.get(m_CurStorePos).id);
				strRequest += "&catalog_id=" + String.valueOf(m_CatalogList.get(m_CatalogPos).catalog_id);
				strRequest += "&standard_id=" + String.valueOf(m_StandardList.get(m_StandardPos).standard_id);
				
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
					
					m_LargenumberList.clear();
					
					for (int i = 0; i < count; i++)
					{
						String itemInfo = dataList.getString(i);
						
						m_LargenumberList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_MOVINGCATALOG )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_MOVINGCATALOG;
				
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
		requestObj.put("count", Integer.toString(m_MovingCount));
		requestObj.put("cataloglist", m_MovingArray);

		return requestObj;
	}
	
}
