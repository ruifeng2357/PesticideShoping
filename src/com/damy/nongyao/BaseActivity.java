package com.damy.nongyao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.ResponseRet;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends Activity {
	
	HttpConnUsingJSON	m_HttpConnUsingJSON;
	int					m_nResponse = ResponseRet.RET_SUCCESS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_HttpConnUsingJSON = new HttpConnUsingJSON(this);
	}
	
	public JSONObject makeRequestJSON() throws JSONException{return null;}
	public void getResponseJSON() {}
	public void refreshUI() {
		if (isNetworkConnected() || isWiFiConnected()) {
			switch (m_nResponse) {
			case ResponseRet.RET_SUCCESS:
				//showToastMessage(getResources().getString(R.string.common_success));
				break;
			case ResponseRet.RET_FAILURE:
				showToastMessage(getResources().getString(R.string.common_fail));
				break;
            case ResponseRet.RET_TRIAL_VERSION:
                showToastMessage(getResources().getString(R.string.error_trialversion));
                break;
			case ResponseRet.RET_NOUSER:
				showToastMessage(getResources().getString(R.string.error_nouser));
				break;
			case ResponseRet.RET_DUPLICATEUSERID:
				showToastMessage(getResources().getString(R.string.error_duplicate_userid));
				break;
			case ResponseRet.RET_INTERNAL_EXCEPTION:
				showToastMessage(getResources().getString(R.string.common_fail));
				break;
			case ResponseRet.RET_JSON_EXCEPTION:
				showToastMessage(getResources().getString(R.string.common_fail));
				break;
			case ResponseRet.RET_NO_IMAGE:
				showToastMessage(getResources().getString(R.string.common_no_image));
				break;
			case ResponseRet.RET_CATALOGOVERFLOW:
				showToastMessage(getResources().getString(R.string.error_remaininsufficient));
				break;
			case ResponseRet.RET_TICKETNUMUSED:
				showToastMessage(getResources().getString(R.string.error_ticketnumused));
				break;
			case ResponseRet.RET_NOCATALOG:
				showToastMessage(getResources().getString(R.string.error_nocatalog));
				break;
			case ResponseRet.RET_NOCUSTOMER:
				showToastMessage(getResources().getString(R.string.error_nocustomer));
				break;
			case ResponseRet.RET_DUPLICATE_LARGENUMBER:
				showToastMessage(getResources().getString(R.string.error_duplicatelargenumber));
				break;
			case ResponseRet.RET_REMAIN_INSUFFICIENT:
				showToastMessage(getResources().getString(R.string.error_remaininsufficient));
				break;
			case ResponseRet.RET_NO_SALECATALOG:
				showToastMessage(getResources().getString(R.string.error_no_salecatalog));
				break;
			case ResponseRet.RET_SALECATALOGOVERFLOW:
				showToastMessage(getResources().getString(R.string.error_salecatalogoverflow));
				break;
			case ResponseRet.RET_DUPLICATEREGISTERID:
				showToastMessage(getResources().getString(R.string.error_registerid_duplicate));
				break;
			case ResponseRet.RET_DUPLICATEUSERNAME:
				showToastMessage(getResources().getString(R.string.error_duplicate_username));
				break;
			default:
				break;
			}
		} else {
			showToastMessage(getResources().getString(R.string.common_fail));
		}
	}
	
	public void showToastMessage(String strMsg) {
		Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
	}
	
	public void hideSoftKeyboard(/*View view*/) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null)
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken()/*view.getWindowToken()*/, 0);
	}
	
	public boolean isNetworkConnected()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null)
		{
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnected())
				return true;
		}
		
		return false;
	}
	
	public boolean isWiFiConnected()
	{
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null)
		{
			NetworkInfo networkinfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if ((networkinfo != null) && (networkinfo.isAvailable() == true) && (networkinfo.getState() == NetworkInfo.State.CONNECTED))
				return true;
		}
		
		return false;
	}
	
	public String EncodeToUTF8(String str)
	{
		String tmp;
		
		try {
			tmp = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			tmp = str;
		}
		
		return tmp;
	}

}
