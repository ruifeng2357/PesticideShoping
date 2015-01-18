package com.damy.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.nongyao.BaseActivity;

public class HttpConnUsingJSON {
	
	public final static String  BASE_URL = "http://218.25.54.56:10001";
	public final static String  REQ_PREFIX = BASE_URL + "/Service.svc/";
	public final static String  REQ_GETUNITLIST = REQ_PREFIX + "GetUnitList";
	public final static String  REQ_GETREGIONLIST = REQ_PREFIX + "GetRegionList";
	public final static String  REQ_GETSTORELIST = REQ_PREFIX + "GetStoreList";
	public final static String  REQ_GETUSERLIST = REQ_PREFIX + "GetUserList";
	public final static String  REQ_GETCUSTOMERLIST = REQ_PREFIX + "GetCustomerList";
	public final static String  REQ_LOGIN = REQ_PREFIX + "Login";
	public final static String  REQ_GETSHOPINFO = REQ_PREFIX + "GetShopInfo";
	public final static String  REQ_EDITSHOPINFO = REQ_PREFIX + "EditShopInfo";
	public final static String  REQ_EDITSHOPPOSITIONINFO = REQ_PREFIX + "EditShopPositionInfo";
	public final static String  REQ_GETSHOPUSERDETAILLIST = REQ_PREFIX + "GetShopUserDetailList";
	public final static String  REQ_ADDSHOPUSER = REQ_PREFIX + "AddShopUser";
	public final static String  REQ_EDITSHOPUSER = REQ_PREFIX + "EditShopUser";
	public final static String  REQ_DELSHOPUSER = REQ_PREFIX + "DelShopUser";
	public final static String  REQ_GETSTOREDETAILLIST = REQ_PREFIX + "GetStoreDetailList";
	public final static String  REQ_ADDSTORE = REQ_PREFIX + "AddStore";
	public final static String  REQ_EDITSTORE = REQ_PREFIX + "EditStore";
	public final static String  REQ_DELSTORE = REQ_PREFIX + "DelStore";
	public final static String  REQ_GETNONGYAOKINDLIST = REQ_PREFIX + "GetNongYaoKindList";
	public final static String  REQ_REQUESTADDCATALOG = REQ_PREFIX + "RequestAddCatalog";
	public final static String  REQ_CHECKREGISTERID = REQ_PREFIX + "CheckCatalogRegisterId";
	public final static String  REQ_GETINOUTLOG = REQ_PREFIX + "GetInOutLog";
	public final static String  REQ_GETINOUTTOTALPROFIT = REQ_PREFIX + "GetInOutTotalProfit";
	public final static String  REQ_GETINOUTLOGDETAIL = REQ_PREFIX + "GetInOutLogDetail"; 
	public final static String  REQ_GETOTHERPAYLIST = REQ_PREFIX + "GetOtherPayList";
	public final static String  REQ_ADDOTHERPAYLOG = REQ_PREFIX + "AddOtherpayLog";
	public final static String  REQ_EDITOTHERPAYLOG = REQ_PREFIX + "EditOtherpayLog";
	public final static String  REQ_DELOTHERPAYLOG = REQ_PREFIX + "DelOtherpayLog";
	public final static String  REQ_GETBANKCASHLOGLIST = REQ_PREFIX + "MoneybankList";
	public final static String  REQ_GETBANKDETAILLIST = REQ_PREFIX + "MoneybankInfo";
	public final static String  REQ_GETCATALOGINFOFROMBARCODE = REQ_PREFIX + "GetCatalogInfoFromBarcode";
	public final static String  REQ_GETCATALOGINFOFROMBARCODEANDSTORE = REQ_PREFIX + "GetCatalogInfoFromBarcodeAndStore";
	public final static String  REQ_GETSUPPLYLIST= REQ_PREFIX + "GetSupplyList";
	public final static String  REQ_GETTICKETNUMBER = REQ_PREFIX + "GetTicketNumber";
	public final static String  REQ_GETPAYMENTLOG = REQ_PREFIX + "GetPaymentLog";
	public final static String  REQ_GETPAYMENTDETAILLOG = REQ_PREFIX + "GetPaymentDetailLog";
	public final static String  REQ_ADDPAYMENTLOG = REQ_PREFIX + "AddPaymentLog";
	public final static String  REQ_EDITPAYMENTLOG = REQ_PREFIX + "EditPaymentLog";
	public final static String  REQ_DELPAYMENTLOG = REQ_PREFIX + "DelPaymentLog";
	public final static String  REQ_REALPAYMENTLIST = REQ_PREFIX + "RealPaymentList";
	public final static String  REQ_MOVINGCATALOG = REQ_PREFIX + "MovingCatalog";
	public final static String  REQ_GETCATALOGLISTFROMSTORE = REQ_PREFIX + "GetCatalogListFromStore";
	public final static String  REQ_GETCATALOGINFOFROMIDANDSTORE = REQ_PREFIX + "GetCatalogInfoFromIdAndStore";
	public final static String  REQ_GETCATALOGREMAINLIST = REQ_PREFIX + "GetCatalogRemainList";
	public final static String  REQ_GETCATALOGREMAINLISTWITHBARCODE = REQ_PREFIX + "GetCatalogRemainListWithBarcode";
	public final static String  REQ_ADDCATALOGUSINGLOG = REQ_PREFIX + "AddCatalogUsingLog";
	public final static String  REQ_BUYINGCATALOG = REQ_PREFIX + "BuyingCatalog";
	public final static String  REQ_SALECATALOG = REQ_PREFIX + "SaleCatalog";
	public final static String  REQ_REJECTCATALOG = REQ_PREFIX + "RejectCatalog";
	public final static String  REQ_SALEHISTORY = REQ_PREFIX + "SaleHistory";
	public final static String  REQ_SALEDETAIL = REQ_PREFIX + "SaleDetail";
	public final static String  REQ_GETREMAINCATALOGSTANDARDLIST = REQ_PREFIX + "GetRemainCatalogStandardList";
	public final static String  REQ_GETLARGENUMBERLIST = REQ_PREFIX + "GetLargenumberList";
	public final static String  REQ_GETSTATISTICBUYING = REQ_PREFIX + "BuyingDetailInfo";
	public final static String  REQ_GETSTATISTICSALE = REQ_PREFIX + "SaleDetailInfo";
	public final static String  REQ_GETSTATISTICSTOREREMAIN = REQ_PREFIX + "StoreMovingDetailInfo";
	public final static String  REQ_GETSTATISTICLost = REQ_PREFIX + "SpendingDetailInfo";
	public final static String  REQ_GETSTATISTICSTORE = REQ_PREFIX + "RemainDetailInfo";
	public final static String  REQ_GETSTATISTICGIVETAKE = REQ_PREFIX + "PayingDetailInfo";
	public final static String  REQ_GETSTATISTICMONEY = REQ_PREFIX + "MoneyDetailInfo";
	public final static String  REQ_GETBARGRAPH = REQ_PREFIX + "GetBarGraph";
	public final static String  REQ_GETLINEGRAPH = REQ_PREFIX + "GetLineGraph";
	public final static String  REQ_GETPIEGRAPH = REQ_PREFIX + "GetPieGraph";
	public final static String  REQ_GETSHOPINFOFROMNICKNAME = REQ_PREFIX + "GetShopInfoFromNickname";
	public final static String  REQ_GETLASTREGIONLIST = REQ_PREFIX + "GetLastRegionList";
	public final static String  REQ_GETSHOPSTATISTIC = REQ_PREFIX + "GetShopStatistic";
	public final static String  REQ_GETCATALOGINFOFROMNICKNAME = REQ_PREFIX + "GetCatalogInfoFromnickname";
	public final static String  REQ_GETCATALOGSTATISTIC = REQ_PREFIX + "GetCatalogStatistics";
	public final static String  REQ_GETCATALOGDETAILSTATISTIC = REQ_PREFIX + "GetCatalogDetailStatistics";
	public final static String  REQ_STATISTIC_POLICY = REQ_PREFIX + "GetStatistics";
	public final static String  REQ_GETCUSTOMERINFO = REQ_PREFIX + "GetCustomerInfo";

	private BaseActivity mActivity;

	
	public HttpConnUsingJSON(BaseActivity activity) {
		mActivity = activity;
	}
	
	public String getResponseData(String url) throws JSONException {
		String strResponse = "";
	    // Create a new HttpClient and Post Header
	    HttpParams myParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(myParams, 120000);
	    HttpConnectionParams.setSoTimeout(myParams, 120000);
	    HttpClient httpclient = new DefaultHttpClient(myParams);
	    String json = mActivity.makeRequestJSON().toString();

	    try {

	        HttpPost httppost = new HttpPost(url.toString());
	        httppost.setHeader("Content-Type", "application/json");

	        StringEntity se = new StringEntity(json, "utf-8"); 
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        
	        httppost.setEntity(se); 

	        HttpResponse response = httpclient.execute(httppost);
	        strResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
	    } catch (ClientProtocolException e) {

	    } catch (IOException e) {
	    }
	    
	    return strResponse;
	}

	public JSONObject getGetJSONObject(String strURL) {
		JSONObject object = null;
		try {
	
			URL url = new URL(strURL);
			InputStream is = url.openStream();
			
			int nReadNum = 0;
			String strJSON = "";
            while (nReadNum != -1) {
            	byte[] buffer = new byte[4096];
            	
            	nReadNum = is.read(buffer);
            	if (nReadNum != -1) {
            		strJSON += new String(buffer, 0, nReadNum);
            	}
            }
            
			object = new JSONObject(strJSON);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}

	public JSONObject getPostJSONObject(String strURL) {
		JSONObject object = null;
		try {
			String strJSON = getResponseData(strURL);
			object = new JSONObject(strJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}
