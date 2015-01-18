package com.damy.nongyao;

import java.net.URLEncoder;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.damy.backend.LoadResponseThread;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;
import com.damy.backend.*;

public class LoginActivity extends BaseActivity {
	
	AutoSizeEditText txt_userid;
	AutoSizeEditText txt_password;
	FrameLayout fl_loginbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_login));
		
		txt_userid = (AutoSizeEditText)findViewById(R.id.txt_login_userid);
		txt_password = (AutoSizeEditText)findViewById(R.id.txt_login_password);
		fl_loginbtn = (FrameLayout)findViewById(R.id.fl_login_btn);
		
		if ( Global.Cur_UserLoginId.length() > 0 )
			txt_userid.setText(Global.Cur_UserLoginId);
		
		fl_loginbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickLogin();
        	}
        });
	}

	void onClickLogin()
	{
		if ( txt_userid.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_useridfull));
			return;
		}
		
		if ( txt_password.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_password));
			return;
		}
		
		new LoadResponseThread(LoginActivity.this).start();
	}
	
	private void onSuccessLogin()
	{
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}
	
	private void onSuccessAdmin()
	{
		Intent statistic_activity = new Intent(this, StatisticShopActivity.class);
		startActivity(statistic_activity);	
		finish();
	}

	public void refreshUI() {
		super.refreshUI();
		
		if (m_nResponse == ResponseRet.RET_SUCCESS) {
			
			Global.Cur_UserLoginId = txt_userid.getText().toString();
			
			 if (Global.Cur_Type == 0)
				 onSuccessLogin();
			 else
				 onSuccessAdmin();
		 }
	}
	
	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			
			String strRequest = HttpConnUsingJSON.REQ_LOGIN;
			strRequest += "?userid=" + EncodeToUTF8(txt_userid.getText().toString());
			strRequest += "&password=" + txt_password.getText().toString();

			JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
			if (response == null) {
				m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
				return;
			}
			m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
            if (m_nResponse == ResponseRet.RET_SUCCESS) {
            	
            	JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
            	
            	Global.Cur_Type = dataObject.getInt("type");
            	Global.Cur_UserId = dataObject.getInt("uid");
            	Global.Cur_UserName = dataObject.getString("username");
            	
            	if ( Global.Cur_Type == 0 )
            	{
            		Global.Cur_ShopId = dataObject.getInt("shop_id");
	            	Global.Cur_ShopName = dataObject.getString("shop_name"); 
	            	Global.Cur_UserRole = dataObject.getString("role");
            	}
            	else
            	{
            		Global.Cur_AdminRole = dataObject.getInt("admin_role");
            		Global.Cur_AdminRegionId = dataObject.getLong("region_id");
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
