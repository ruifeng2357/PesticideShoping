package com.damy.nongyao;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MoneyOtherpayAddActivity extends BaseActivity {
	
	public static String MONEYOTHERPAY_ADD_ID = "MONEYOTHERPAY_ADD_ID";
	public static String MONEYOTHERPAY_ADD_TYPE = "MONEYOTHERPAY_ADD_TYPE";
	public static String MONEYOTHERPAY_ADD_MONEY = "MONEYOTHERPAY_ADD_MONEY";
	public static String MONEYOTHERPAY_ADD_REASON = "MONEYOTHERPAY_ADD_REASON";
	
	private int m_itemId = 0;
	private int m_itemType = 0;
	private float m_itemMoney = 0;
	private String m_itemReason = "";
	
	private AutoSizeTextView txt_Type;
	private AutoSizeEditText edit_Money;
	private AutoSizeEditText edit_Reason;
	private AutoSizeTextView txt_otherpay_add_paytype;
	
	private PopupWindow dialog_type;
	private PopupWindow dialog_paytype;
	private LinearLayout m_MaskLayer;
	
	private int m_CurType = 0;
	private ArrayList<String>	m_typeList = new ArrayList<String>();
    private int	m_CurPayType = 0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_otherpay_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_moneyotherpay_add));
		
		txt_Type = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_add_type);
		edit_Money = (AutoSizeEditText)findViewById(R.id.edit_moneyotherpay_add_money);
		edit_Reason = (AutoSizeEditText)findViewById(R.id.edit_moneyotherpay_add_reason);
		txt_otherpay_add_paytype = (AutoSizeTextView)findViewById(R.id.txt_moneyotherpay_add_paytype);
		
		m_itemId = getIntent().getIntExtra(MONEYOTHERPAY_ADD_ID, 0);
		
		if ( m_itemId > 0 )
		{
			m_itemId = getIntent().getIntExtra(MONEYOTHERPAY_ADD_ID, 0);
			m_itemType = getIntent().getIntExtra(MONEYOTHERPAY_ADD_TYPE, 0);
			m_itemMoney = getIntent().getFloatExtra(MONEYOTHERPAY_ADD_MONEY, 0);
			m_itemReason = getIntent().getStringExtra(MONEYOTHERPAY_ADD_REASON);
			m_CurType = m_itemType;
			
			txt_Type.setText((m_itemType == 0 ? getResources().getString(R.string.common_outgo) : getResources().getString(R.string.common_income) ));
			edit_Money.setText(Float.toString(m_itemMoney));
			if (m_itemReason != null && m_itemReason != "null")
				edit_Reason.setText(m_itemReason);
		}
		else
		{
			txt_otherpay_add_paytype.setText(getResources().getString(R.string.common_actual));
			txt_Type.setText(getResources().getString(R.string.common_outgo));
		}

		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_add_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_add_homebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_moneyotherpay_add_closebtn);

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
		
		fl_savebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSave();
        	}
        });
		
		fl_closebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickClose();
        	}
        });
		
		LinearLayout ll_typeselect = (LinearLayout)findViewById(R.id.ll_moneyotherpay_add_typeselect);
		ll_typeselect.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectType();
        	}
        });
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_moneyotherpay_add_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);
				
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_incomeoutgo));

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
		
		ArrayList<String> arGeneral = new ArrayList<String>();
		
		arGeneral.add(getResources().getString(R.string.common_outgo));
		arGeneral.add(getResources().getString(R.string.common_income));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, arGeneral);
		
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
		
		setDialogPayTypeAdapter();
		
		
		LinearLayout ll_moneyotherpay_paytypesel = (LinearLayout)findViewById(R.id.ll_moneyotherpay_add_paytypeselect);
		
		ll_moneyotherpay_paytypesel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickPayTypeSel();
        	}
        });
		
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
	
	private void onClickSave()
	{
		if ( edit_Money.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_money));
			return;
		}
		
		if ( Float.valueOf(edit_Money.getText().toString()) == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_zeromoney));
			return;
		}
		
		if ( edit_Reason.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_reason));
			return;
		}
		
		new LoadResponseThread(MoneyOtherpayAddActivity.this).start();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void onClickSelectType()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_type.showAtLocation(findViewById(R.id.ll_moneyotherpay_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void onClickTypeItem(int pos)
	{
		m_CurType = pos;
		txt_Type.setText((m_CurType == 0 ? getResources().getString(R.string.common_outgo) : getResources().getString(R.string.common_income) ));
		
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_type.dismiss();
	}
	
	private void setDialogPayTypeAdapter()
	{
		View popupview = View.inflate(this, R.layout.dialog_select, null);
		ResolutionSet._instance.iterateChild(popupview);
		
		AutoSizeTextView txt_title = (AutoSizeTextView)popupview.findViewById(R.id.txt_dialog_title);
		txt_title.setText(getResources().getString(R.string.dialog_select_takemoneytype));

        ImageView img_cancel = (ImageView) popupview.findViewById(R.id.img_dialog_cancel);
        img_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog_paytype != null && dialog_paytype.isShowing() )
                {
                    m_MaskLayer.setVisibility(View.INVISIBLE);
                    dialog_paytype.dismiss();
                }
            }
        });

		m_typeList.add(getResources().getString(R.string.common_actual));		
		m_typeList.add(getResources().getString(R.string.common_bankpayment));
		
		DialogSelectAdapter Adapter = new DialogSelectAdapter(this, m_typeList);
		
		ListView list = (ListView)popupview.findViewById(R.id.lv_dialog_listview);
		list.setAdapter(Adapter);
		list.setDrawSelectorOnTop(true);
		list.setDivider(new ColorDrawable(getResources().getColor(R.color.dialog_line)));
		list.setCacheColorHint(Color.TRANSPARENT);
        list.setDividerHeight(1);
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onClickPayTypeItem(position);
        	}
		});
		
		dialog_paytype = new PopupWindow(popupview, R.dimen.common_popup_dialog_width, R.dimen.common_popup_dialog_height,true);
		dialog_paytype.setAnimationStyle(-1);
	}
	
	private void onClickPayTypeItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_paytype.dismiss();
		
		txt_otherpay_add_paytype.setText(m_typeList.get(pos));
		m_CurPayType = pos;
	}
	
	private void onClickPayTypeSel()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_paytype.showAtLocation(findViewById(R.id.ll_moneyotherpay_add_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	
	public void refreshUI() {
		super.refreshUI();

		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			finish();
		}
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strUrl = "";
			if ( m_itemId == 0 )
				strUrl = HttpConnUsingJSON.REQ_ADDOTHERPAYLOG;
			else
				strUrl = HttpConnUsingJSON.REQ_EDITOTHERPAYLOG;
			
			JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strUrl);
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            

		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		if ( m_itemId > 0 )
			requestObj.put("otherpay_id", Integer.toString(m_itemId));
    	
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("type", Integer.toString(m_CurType));
		requestObj.put("paytype", Integer.toString(m_CurPayType == 0? 0: 2));
		requestObj.put("price", edit_Money.getText().toString());
		requestObj.put("reason", edit_Reason.getText().toString());
		

		return requestObj;
	}

}
