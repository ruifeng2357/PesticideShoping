package com.damy.nongyao;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.LoadResponseThread;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.ResolutionSet;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BaseUserAddActivity extends BaseActivity {
	
	public static String BASEUSER_ADD_ID = "BASEUSER_ADD_ID";
	public static String BASEUSER_ADD_USERNAME = "BASEUSER_ADD_USERNAME";
	public static String BASEUSER_ADD_USERID = "BASEUSER_ADD_USERID";
	public static String BASEUSER_ADD_PASSWORD = "BASEUSER_ADD_PASSWORD";
	public static String BASEUSER_ADD_PHONE = "BASEUSER_ADD_PHONE";
	public static String BASEUSER_ADD_ROLE = "BASEUSER_ADD_ROLE";
	
	private long m_itemId = 0;
	private String m_itemName = "";
	private String m_itemUserId = "";
	private String m_itemPassword = "";
	private String m_itemPhone = "";
	private String m_itemRole = "";
	
	private AutoSizeEditText txt_Name;
	private AutoSizeEditText txt_UserId;
	private AutoSizeEditText txt_Password;
	private AutoSizeEditText txt_RPassword;
	private AutoSizeEditText txt_Phone;
	
	private ImageView img_check1;
	private ImageView img_check2;
	private ImageView img_check3;
	private ImageView img_check4;
	
	private boolean isCheck1 = false;
	private boolean isCheck2 = false;
	private boolean isCheck3 = false;
	private boolean isCheck4 = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_user_add);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.ll_baseuser_add));
		
		txt_Name = (AutoSizeEditText)findViewById(R.id.txt_baseuser_add_name);
		txt_UserId = (AutoSizeEditText)findViewById(R.id.txt_baseuser_add_userid);
		txt_Password = (AutoSizeEditText)findViewById(R.id.txt_baseuser_add_password);
		txt_RPassword = (AutoSizeEditText)findViewById(R.id.txt_baseuser_add_rpassword);
		txt_Phone = (AutoSizeEditText)findViewById(R.id.txt_baseuser_add_phone);
		
		img_check1 = (ImageView)findViewById(R.id.img_baseuser_add_check1);
		img_check2 = (ImageView)findViewById(R.id.img_baseuser_add_check2);
		img_check3 = (ImageView)findViewById(R.id.img_baseuser_add_check3);
		img_check4 = (ImageView)findViewById(R.id.img_baseuser_add_check4);
		
		img_check1.setImageResource(R.drawable.baseuser_add_checkbox1);
		img_check2.setImageResource(R.drawable.baseuser_add_checkbox1);
		img_check3.setImageResource(R.drawable.baseuser_add_checkbox1);
		img_check4.setImageResource(R.drawable.baseuser_add_checkbox1);
		
		m_itemId = getIntent().getLongExtra(BASEUSER_ADD_ID, 0);
		
		if ( m_itemId > 0 )
		{
			m_itemName = getIntent().getStringExtra(BASEUSER_ADD_USERNAME);
			m_itemUserId = getIntent().getStringExtra(BASEUSER_ADD_USERID);
			m_itemPassword = getIntent().getStringExtra(BASEUSER_ADD_PASSWORD);
			m_itemPhone = getIntent().getStringExtra(BASEUSER_ADD_PHONE);
			m_itemRole = getIntent().getStringExtra(BASEUSER_ADD_ROLE);
			
			txt_Name.setText(m_itemName);
			txt_UserId.setText(m_itemUserId);
			txt_Password.setText(m_itemPassword);
			txt_RPassword.setText(m_itemPassword);
			txt_Phone.setText(m_itemPhone);

			if ( m_itemRole.contains("buying") )
			{
				img_check1.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck1 = true;
			}
			
			if ( m_itemRole.contains("sale") )
			{
				img_check2.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck2 = true;
			}
			
			if ( m_itemRole.contains("store") )
			{
				img_check3.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck3 = true;
			}
			
			if ( m_itemRole.contains("account") )
			{
				img_check4.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck4 = true;
			}
		}
		else
		{
			img_check4.setImageResource(R.drawable.baseuser_add_checkbox2);
			isCheck4 = true;
		}
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_baseuser_add_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_baseuser_add_homebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_baseuser_add_savebtn);
		FrameLayout fl_closebtn = (FrameLayout)findViewById(R.id.fl_baseuser_add_closebtn);

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
		
		LinearLayout ll_check1 = (LinearLayout)findViewById(R.id.ll_baseuser_add_role1);
		LinearLayout ll_check2 = (LinearLayout)findViewById(R.id.ll_baseuser_add_role2);
		LinearLayout ll_check3 = (LinearLayout)findViewById(R.id.ll_baseuser_add_role3);
		LinearLayout ll_check4 = (LinearLayout)findViewById(R.id.ll_baseuser_add_role4);
		
		ll_check1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(1);
        	}
        });
		
		ll_check2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(2);
        	}
        });
		
		ll_check3.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(3);
        	}
        });
		
		ll_check4.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(4);
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
		if ( txt_Name.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_name));
			return;
		}
		
		if ( txt_UserId.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_useridfull));
			return;
		}
		
		if ( txt_Password.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}
		
		if ( txt_Phone.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_mobilephone));
			return;
		}
		
		if ( txt_Phone.getText().toString().length() != 11 )
		{
			showToastMessage(getResources().getString(R.string.error_mobilephonenum_charactercount));
			return;
		}
		
		if ( !txt_Password.getText().toString().equals(txt_RPassword.getText().toString()) )
		{
			showToastMessage(getResources().getString(R.string.error_notmach_password));
			return;
		}
		
		if ( !(isCheck1 | isCheck2 | isCheck3 | isCheck4) )
		{
			showToastMessage(getResources().getString(R.string.error_select_role));
			return;
		}
		
		m_itemRole = "";
		
		if ( isCheck1 )
			m_itemRole += ",buying";
		if ( isCheck2 )
			m_itemRole += ",sale";
		if ( isCheck3 )
			m_itemRole += ",store";
		if ( isCheck4 )
			m_itemRole += ",account";
		
		m_itemRole = m_itemRole.substring(1);
		
		new LoadResponseThread(BaseUserAddActivity.this).start();
	}
	
	private void onClickClose()
	{
		finish();
	}
	
	private void onClickCheck(int nId)
	{
		if ( nId == 1 )
		{
			if ( isCheck1 )
			{
				img_check1.setImageResource(R.drawable.baseuser_add_checkbox1);
				isCheck1 = false;
			}
			else
			{
				img_check1.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck1 = true;
			}
		}
		else if ( nId == 2 )
		{
			if ( isCheck2 )
			{
				img_check2.setImageResource(R.drawable.baseuser_add_checkbox1);
				isCheck2 = false;
			}
			else
			{
				img_check2.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck2 = true;
			}
		}
		else if ( nId == 3 )
		{
			if ( isCheck3 )
			{
				img_check3.setImageResource(R.drawable.baseuser_add_checkbox1);
				isCheck3 = false;
			}
			else
			{
				img_check3.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck3 = true;
			}
		}
		else if ( nId == 4 )
		{
			if ( isCheck4 )
			{
				img_check4.setImageResource(R.drawable.baseuser_add_checkbox1);
				isCheck4 = false;
			}
			else
			{
				img_check4.setImageResource(R.drawable.baseuser_add_checkbox2);
				isCheck4 = true;
			}
		}
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
				strUrl = HttpConnUsingJSON.REQ_ADDSHOPUSER;
			else
				strUrl = HttpConnUsingJSON.REQ_EDITSHOPUSER;
			
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
			requestObj.put("uid", String.valueOf(m_itemId));
    	
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("name", txt_Name.getText().toString());
		requestObj.put("userid", txt_UserId.getText().toString());
		requestObj.put("password", txt_Password.getText().toString());
		requestObj.put("phone", txt_Phone.getText().toString());
		requestObj.put("role", m_itemRole);

		return requestObj;
	}

}
