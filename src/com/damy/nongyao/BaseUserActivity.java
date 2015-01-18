package com.damy.nongyao;

import java.util.ArrayList;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.damy.adapters.BaseUserAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STBaseUserInfo;
import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.PullToRefreshListView;
import com.damy.Utils.PullToRefreshBase;
import com.damy.Utils.PullToRefreshBase.Mode;
import com.damy.Utils.PullToRefreshBase.OnRefreshListener;
import com.damy.Utils.ResolutionSet;

public class BaseUserActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSHOPUSERDETAILLIST, REQ_DELSHOPUSER};

	private PullToRefreshListView		m_lvBaseUserListView;
	private ArrayList<STBaseUserInfo> 	m_BaseUserList;
	private BaseUserAdapter				m_BaseUserAdapter = null;
	private ListView					mRealListView;

    private int							m_nCurPageNumber = 1;
    private int 						m_CurClickedItem = -1;
    private PopupWindow 				popup_editdel;
    private PopupWindow 				popup_delconfirm;
    private LinearLayout				m_MaskLayer;
    
    private REQ_TYPE					m_reqType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_user);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_baseuser));
		
		initControls();
		setBaseUserAdapter();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		m_nCurPageNumber = 1;
		m_BaseUserList.clear();
		m_reqType = REQ_TYPE.REQ_GETSHOPUSERDETAILLIST;
		new LoadResponseThread(BaseUserActivity.this).start();
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
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_baseuser_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_baseuser_homebtn);
		FrameLayout fl_shopbtn = (FrameLayout)findViewById(R.id.fl_baseuser_shopbtn);
		FrameLayout fl_userbtn = (FrameLayout)findViewById(R.id.fl_baseuser_userbtn);
		FrameLayout fl_storebtn = (FrameLayout)findViewById(R.id.fl_baseuser_storebtn);
		FrameLayout fl_addbtn = (FrameLayout)findViewById(R.id.fl_baseuser_addbtn);

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

		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_baseuser_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		m_lvBaseUserListView = (PullToRefreshListView)findViewById(R.id.anBaseUserContentView);
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
		
	}
	
	private void onClickStore()
	{
		Intent store_activity = new Intent(this, BaseStoreActivity.class);
		startActivity(store_activity);
		finish();
	}
	
	private void onClickAdd()
	{
		Intent add_activity = new Intent(this, BaseUserAddActivity.class);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_ID, 0);
		startActivity(add_activity);
	}
	
	private void onClickPopupEdit()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		
		STBaseUserInfo curItem = getItem(m_CurClickedItem - 1);
		
		Intent add_activity = new Intent(this, BaseUserAddActivity.class);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_ID, curItem.uid);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_USERNAME, curItem.username);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_USERID, curItem.userid);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_PASSWORD, curItem.password);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_PHONE, curItem.phone);
		add_activity.putExtra(BaseUserAddActivity.BASEUSER_ADD_ROLE, curItem.role);
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
		
		popup_delconfirm.showAtLocation(findViewById(R.id.ll_baseuser_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		
		AutoSizeTextView txt_delconfirm_msg = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_delconfirm_msg);
		txt_delconfirm_msg.setText(getResources().getString(R.string.confirm_del_baseuser));
		
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
		
		m_reqType = REQ_TYPE.REQ_DELSHOPUSER;
		new LoadResponseThread(BaseUserActivity.this).start();
	}
	
	private void onClickDelConfirmCancel()
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		popup_delconfirm.dismiss();
	}
	
	public STBaseUserInfo getItem(int position)
	{
		if (position < 0 || position >= m_BaseUserList.size())
			return null;
		
		return m_BaseUserList.get(position);
	}
	
	private void setBaseUserAdapter() {
		m_BaseUserList = new ArrayList<STBaseUserInfo>();
		m_lvBaseUserListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        // Set a listener to be invoked when the list should be refreshed.
		m_lvBaseUserListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                m_nCurPageNumber = m_nCurPageNumber + 1;
                m_reqType = REQ_TYPE.REQ_GETSHOPUSERDETAILLIST;
                new LoadResponseThread(BaseUserActivity.this).start();
            }
        });

        mRealListView = m_lvBaseUserListView.getRefreshableView();
        registerForContextMenu(mRealListView);

        //mRealListView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mRealListView.setCacheColorHint(Color.TRANSPARENT);
        mRealListView.setDividerHeight(0);
        mRealListView.setDrawSelectorOnTop(true);

        m_BaseUserAdapter = new BaseUserAdapter(BaseUserActivity.this, m_BaseUserList);
        mRealListView.setAdapter(m_BaseUserAdapter);
        
        mRealListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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
		
		if ( m_reqType == REQ_TYPE.REQ_GETSHOPUSERDETAILLIST )
		{
			m_BaseUserAdapter.notifyDataSetChanged();
			m_lvBaseUserListView.onRefreshComplete();
		}
		else if ( m_reqType == REQ_TYPE.REQ_DELSHOPUSER )
		{
			m_BaseUserList.clear();
			m_reqType = REQ_TYPE.REQ_GETSHOPUSERDETAILLIST;
			m_nCurPageNumber = 1;
			new LoadResponseThread(BaseUserActivity.this).start();
		}
		
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETSHOPUSERDETAILLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSHOPUSERDETAILLIST;
				strRequest += "?shop_id=" + String.valueOf(Global.Cur_ShopId);
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
			            STBaseUserInfo itemInfo = new STBaseUserInfo();
	
						itemInfo.uid = tmpObj.getInt("uid");
						itemInfo.userid = tmpObj.getString("userid");
						itemInfo.username = tmpObj.getString("username");
						itemInfo.password = tmpObj.getString("password");
						itemInfo.phone = tmpObj.getString("phone");
						itemInfo.role = tmpObj.getString("role");
						
						m_BaseUserList.add(itemInfo);
		            }
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_DELSHOPUSER )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_DELSHOPUSER;
				
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
		
		STBaseUserInfo curItem = getItem(m_CurClickedItem - 1);

		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(curItem.uid));

		return requestObj;
	}

}
