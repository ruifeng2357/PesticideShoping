package com.damy.nongyao;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.*;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.search.*;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.damy.HttpConn.AsyncHttpClient;
import com.damy.HttpConn.AsyncHttpResponseHandler;
import com.damy.Utils.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STBaseUserInfo;
import com.damy.datatypes.STRegionInfo;
import com.damy.datatypes.STShopInfo;

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
import android.util.Base64;

public class BaseShopActivity extends BaseActivity {
	
	private enum REQ_TYPE{REQ_GETSHOPINFO, REQ_GETREGIONLIST, REQ_EDITSHOPINFO, REQ_EDITSHOPPOSITIONINFO};
	
	AutoSizeEditText 					edit_name;
	AutoSizeEditText 					edit_nickname;
	AutoSizeEditText 					edit_addr;
	AutoSizeEditText 					edit_username;
	AutoSizeEditText 					edit_mobilephone;
	AutoSizeEditText 					edit_phone;
	AutoSizeTextView 					txt_region;
	
	private PopupWindow 				dialog_region;
    private LinearLayout				m_MaskLayer;
	
	private REQ_TYPE 					m_reqType;
	
	private ArrayList<STRegionInfo> 	m_RegionList = new ArrayList<STRegionInfo>();
	private STShopInfo					m_ShopInfo = null;
    private boolean bFirstReceiveMyPos = true;

    /*
    * Map View
     */
    public static String gBaiduKey = "0840FBEE3CF1DA22265009B31BDDF0BE4E31D01C";

    public BMapManager mapManager = null;
    private MapView mapView = null;
    private MapController mapController = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    public LocationClient mLocClient = null;

    MKSearch mSearch = null;
    MKSearchListener mSearchListener = null;

    public double cur_google_lat = 0.0f;
    public double cur_google_lon = 0.0f;

    private double shop_lat = 0.0f;
    private double shop_lon = 0.0f;
    private OverlayContainer mShopOverlays = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        initBaidu();
		setContentView(R.layout.activity_base_shop);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_baseshop));
		
		initControls();
		readContent();
        /*
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(intent, 0);
            onClickHome();
        }
        */
        /*
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider == null){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            onClickHome();
        }
        */

        initSearchManager();
	}
	
	private void initControls()
	{
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_baseshop_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_baseshop_homebtn);
		FrameLayout fl_shopbtn = (FrameLayout)findViewById(R.id.fl_baseshop_shopbtn);
		FrameLayout fl_userbtn = (FrameLayout)findViewById(R.id.fl_baseshop_userbtn);
		FrameLayout fl_storebtn = (FrameLayout)findViewById(R.id.fl_baseshop_storebtn);
		FrameLayout fl_savebtn = (FrameLayout)findViewById(R.id.fl_baseshop_savebtn);	
		FrameLayout fl_cancelbtn = (FrameLayout)findViewById(R.id.fl_baseshop_cancelbtn);
		FrameLayout fl_positionsavebtn = (FrameLayout)findViewById(R.id.fl_baseshop_positionsavebtn);
		
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
		
		fl_positionsavebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickPositionSave();
			}
		});
		
		edit_name = (AutoSizeEditText)findViewById(R.id.edit_baseshop_name);
		edit_nickname = (AutoSizeEditText)findViewById(R.id.edit_baseshop_nickname);
		edit_addr = (AutoSizeEditText)findViewById(R.id.edit_baseshop_addr);
		edit_username = (AutoSizeEditText)findViewById(R.id.edit_baseshop_username);
		edit_mobilephone = (AutoSizeEditText)findViewById(R.id.edit_baseshop_mobilephone);
		edit_phone = (AutoSizeEditText)findViewById(R.id.edit_baseshop_phone);
		
		
		txt_region = (AutoSizeTextView)findViewById(R.id.txt_baseshop_region);
		LinearLayout ll_regionsel = (LinearLayout)findViewById(R.id.ll_baseshop_regionsel);
		ll_regionsel.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectRegion();
        	}
        });
		
		edit_name.setEnabled(false);
		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_baseshop_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);

        /*
        * related to BaiDu map
         */
        mapView = (MapView) findViewById(R.id.viewBaseShop_Map);
        mapView.setBuiltInZoomControls(true);

        mapController = mapView.getController();
        mapController.enableClick(true);
        mapController.setZoom(16);

        //mapView.requestDisallowInterceptTouchEvent(true);
        mapView.regMapViewListener(mapManager, mapListener);
	}

    private void initBaidu()
    {
        if (mapManager == null)
        {
            mapManager = new BMapManager(getApplication());
            mapManager.init(gBaiduKey, new GeneralBaiduListener());
        }
        mapManager.start();

        initLocationManager();
        initSearchManager();
    }

    //requestDisallowInterceptTouchEvent
    private void initLocationManager()
    {
        mLocClient = new LocationClient(BaseShopActivity.this);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
    }

    private void initSearchManager()
    {
        if (mSearch == null)
        {
            mSearch = new MKSearch();
            mSearchListener = new MKSearchListener() {
                @Override
                public void onGetPoiResult(MKPoiResult mkPoiResult, int i, int i2) {}
                @Override
                public void onGetTransitRouteResult(MKTransitRouteResult mkTransitRouteResult, int i) {}
                @Override
                public void onGetDrivingRouteResult(MKDrivingRouteResult mkDrivingRouteResult, int i) {}
                @Override
                public void onGetWalkingRouteResult(MKWalkingRouteResult mkWalkingRouteResult, int i) {}
                @Override
                public void onGetAddrResult(MKAddrInfo mkAddrInfo, int i) {}
                @Override
                public void onGetBusDetailResult(MKBusLineResult mkBusLineResult, int i) {}
                @Override
                public void onGetSuggestionResult(MKSuggestionResult mkSuggestionResult, int i) {}
                @Override
                public void onGetPoiDetailSearchResult(int i, int i2) {}
            };
        }

        mSearch.init(mapManager, mSearchListener);
    }

    private MKMapViewListener mapListener = new MKMapViewListener() {
        @Override
        public void onMapMoveFinish() {}
        @Override
        public void onClickMapPoi(MapPoi mapPoi) {
            if (mapPoi.geoPt != null)
            {
                shop_lat = mapPoi.geoPt.getLatitudeE6() / 1E6;
                shop_lon = mapPoi.geoPt.getLongitudeE6() / 1E6;
                m_ShopInfo.latitude = shop_lat;
                m_ShopInfo.longitude = shop_lon;
                showOverlay();
            }
        }
        @Override
        public void onGetCurrentMap(Bitmap bitmap) {}
        @Override
        public void onMapAnimationFinish() {}
    };

    static class GeneralBaiduListener implements MKGeneralListener {
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {}
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {}
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError != 0) {}
            else {}
        }
    }

    public class MyLocationListenner implements BDLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation location)
        {
            if (location == null)
                return;

            cur_google_lat = location.getLatitude();
            cur_google_lon = location.getLongitude();

            if (bFirstReceiveMyPos == true)
            {
                shop_lat = cur_google_lat;
                m_ShopInfo.latitude = shop_lat;
                shop_lon = cur_google_lon;
                m_ShopInfo.longitude = shop_lon;
                showOverlay();

                bFirstReceiveMyPos = false;
            }
        }

        public void onReceivePoi(BDLocation poiLocation)
        {
            if (poiLocation == null)
            {
                return ;
            }
        }
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
	
	private void onClickShop()
	{
	}
	
	private void onClickUser()
	{
		Intent user_activity = new Intent(this, BaseUserActivity.class);
		startActivity(user_activity);
		finish();
	}
	
	private void onClickStore()
	{
		Intent store_activity = new Intent(this, BaseStoreActivity.class);
		startActivity(store_activity);	
		finish();
	}
	
	private void onClickSave()
	{		
		if ( edit_name.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_shopname));
			return;
		}
		
		if ( edit_nickname.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_nickname));
			return;
		}
		
		if ( !edit_nickname.getText().toString().matches("^[a-zA-Z]+$") )
		{
			showToastMessage(getResources().getString(R.string.error_alphabetic_nickname));
			return;
		}
		
		if ( edit_addr.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_address));
			return;
		}
		
		if ( edit_username.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_contactor));
			return;
		}
		
		if ( edit_mobilephone.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_mobile));
			return;
		}
		
		if ( edit_mobilephone.getText().toString().length() != 11 )
		{
			showToastMessage(getResources().getString(R.string.error_mobilephonenum_charactercount));
			return;
		}
		
		if ( edit_phone.getText().toString().length() == 0 )
		{
			showToastMessage(getResources().getString(R.string.error_required_phone));
			return;
		}
		
		if ( edit_phone.getText().toString().length() != 11 )
		{
			showToastMessage(getResources().getString(R.string.error_phonenum_charactercount));
			return;
		}
		
		if ( m_ShopInfo != null )
			m_ShopInfo = new STShopInfo();
		
		m_ShopInfo.name = edit_name.getText().toString();
		m_ShopInfo.nickname = edit_nickname.getText().toString();
		m_ShopInfo.address = edit_addr.getText().toString();
		m_ShopInfo.username = edit_username.getText().toString();
		m_ShopInfo.mobile_phone = edit_mobilephone.getText().toString();
		m_ShopInfo.phone = edit_phone.getText().toString();
		m_ShopInfo.region = getRidFromRegion(txt_region.getText().toString());
		
		m_reqType = REQ_TYPE.REQ_EDITSHOPINFO;
		new LoadResponseThread(BaseShopActivity.this).start();
	}
	
	private void onClickPositionSave()
	{
		m_reqType = REQ_TYPE.REQ_EDITSHOPPOSITIONINFO;
		new LoadResponseThread(BaseShopActivity.this).start();
	}
	
	private void onClickCancel()
	{
		if ( m_ShopInfo != null )
		{
			edit_name.setText(m_ShopInfo.name);
			edit_nickname.setText(m_ShopInfo.nickname);
			edit_addr.setText(m_ShopInfo.address);
			edit_username.setText(m_ShopInfo.username);
			edit_mobilephone.setText(m_ShopInfo.mobile_phone);
			edit_phone.setText(m_ShopInfo.phone);
			
			txt_region.setText(getRegionFromId(m_ShopInfo.region));
		}	
	}
	
	private void onClickSelectRegion()
	{
		m_MaskLayer.setVisibility(View.VISIBLE);
		dialog_region.showAtLocation(findViewById(R.id.ll_baseshop_masklayer), Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void readContent()
	{
		m_reqType = REQ_TYPE.REQ_GETREGIONLIST;
		new LoadResponseThread(BaseShopActivity.this).start();
	}
	
	private String getRegionFromId(long rid)
	{
		int cnt = m_RegionList.size();
		int i;
		STRegionInfo tmp;
		for ( i = 0; i < cnt; i++ )
		{
			tmp = m_RegionList.get(i);
			if ( tmp.id == rid )
				return tmp.name;
		}
		
		return "";
	}
	
	private long getRidFromRegion(String rname)
	{
		int cnt = m_RegionList.size();
		int i;
		STRegionInfo tmp;
		for ( i = 0; i < cnt; i++ )
		{
			tmp = m_RegionList.get(i);
			if ( tmp.name == rname )
				return tmp.id;
		}
		
		return 0;
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
	}
	
	private void onClickRegionItem(int pos)
	{
		m_MaskLayer.setVisibility(View.INVISIBLE);
		dialog_region.dismiss();
		txt_region.setText(m_RegionList.get(pos).name);
	}
	
	public void refreshUI() {
		super.refreshUI();
		
		if ( m_reqType == REQ_TYPE.REQ_GETREGIONLIST )
		{
			m_reqType = REQ_TYPE.REQ_GETSHOPINFO;
			new LoadResponseThread(BaseShopActivity.this).start();
			
			setDialogRegionAdapter();
		}
		else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFO )
		{
			if ( m_ShopInfo != null )
			{
				edit_name.setText(m_ShopInfo.name);
				edit_nickname.setText(m_ShopInfo.nickname);
				edit_addr.setText(m_ShopInfo.address);
				edit_username.setText(m_ShopInfo.username);
				edit_mobilephone.setText(m_ShopInfo.mobile_phone);
				edit_phone.setText(m_ShopInfo.phone);
				
				txt_region.setText(getRegionFromId(m_ShopInfo.region));
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_EDITSHOPINFO )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				showToastMessage(getResources().getString(R.string.common_success));
			}
		}
		else if ( m_reqType == REQ_TYPE.REQ_EDITSHOPPOSITIONINFO )
		{
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				showToastMessage(getResources().getString(R.string.common_success));
			}
		}
	}
	
	public void getResponseJSON() {
		try {
			if ( m_reqType == REQ_TYPE.REQ_GETREGIONLIST )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETREGIONLIST;
				
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
						itemInfo.name = tmpObj.getString("name");
						
						m_RegionList.add(itemInfo);
					}
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_GETSHOPINFO )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_GETSHOPINFO;
				strRequest += "?uid=" + String.valueOf(Global.Cur_UserId);
				strRequest += "&shop_id=" + String.valueOf(Global.Cur_ShopId);
				
				JSONObject response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					
					JSONObject dataObject = response.getJSONObject(ResponseData.RESPONSE_DATA);
					
					m_ShopInfo = new STShopInfo();
					
					m_ShopInfo.name = dataObject.getString("name");
					m_ShopInfo.nickname = dataObject.getString("nickname");
					m_ShopInfo.address = dataObject.getString("addr");
					m_ShopInfo.region = dataObject.getInt("region");
					m_ShopInfo.username = dataObject.getString("username");
					m_ShopInfo.mobile_phone = dataObject.getString("mobile_phone");
					m_ShopInfo.phone = dataObject.getString("phone");
					m_ShopInfo.longitude = dataObject.getDouble("longitude");
					m_ShopInfo.latitude = dataObject.getDouble("latitude");
				}
			}
			else if ( m_reqType == REQ_TYPE.REQ_EDITSHOPINFO )
			{
				m_nResponse = ResponseRet.RET_SUCCESS;
				
				String strRequest = HttpConnUsingJSON.REQ_EDITSHOPINFO;
				
				JSONObject response = m_HttpConnUsingJSON.getPostJSONObject(strRequest);
				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {
					//showToastMessage(getResources().getString(R.string.common_success));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}
	
	
	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();
		
		if ( m_reqType == REQ_TYPE.REQ_EDITSHOPINFO )
		{
			requestObj.put("uid", String.valueOf(Global.Cur_UserId));
			requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
			requestObj.put("nickname", m_ShopInfo.nickname);
			requestObj.put("address", m_ShopInfo.address);
			requestObj.put("region", m_ShopInfo.region);
			requestObj.put("username", m_ShopInfo.username);
			requestObj.put("mobile_phone", m_ShopInfo.mobile_phone);
			requestObj.put("phone", m_ShopInfo.phone);
		}
		else if ( m_reqType == REQ_TYPE.REQ_EDITSHOPPOSITIONINFO )
		{
			requestObj.put("uid", String.valueOf(Global.Cur_UserId));
			requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
			requestObj.put("longitude", String.valueOf(m_ShopInfo.longitude));
			requestObj.put("latitude", String.valueOf(m_ShopInfo.latitude));
		}

		return requestObj;
	}

    @Override
    public void onResume()
    {
        mapView.onResume();
        mLocClient.start();

        super.onResume();

        bFirstReceiveMyPos = true;
    }

    @Override
    protected void onPause()
    {
        mapView.onPause();
        mLocClient.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mLocClient.stop();
        mapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mapView.onRestoreInstanceState(savedInstanceState);
    }

    public void Google2Baidu(double fLatitude, double fLongitude, AsyncHttpResponseHandler handler)
    {
        String baidu_correct_url = "http://api.map.baidu.com/ag/coord/convert?";
        String url = baidu_correct_url + "from=2&to=4&x=" + fLongitude + "&y=" + fLatitude;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(4000);
        client.get(url, handler);
    }

    private AsyncHttpResponseHandler correct_google_listener = new AsyncHttpResponseHandler()
    {
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);    //To change body of overridden methods use File | Settings | File Templates.

            try
            {
                JSONObject jsonObj = new JSONObject(content);

                int nRetCode = jsonObj.getInt("error");
                if (nRetCode == 0)
                {
                    String x_base64 = jsonObj.getString("x");
                    String y_base64 = jsonObj.getString("y");

                    String xStr = new String(Base64.decode(x_base64, Base64.DEFAULT), "UTF-8");
                    String yStr = new String(Base64.decode(y_base64, Base64.DEFAULT), "UTF-8");

                    double fLon = Double.parseDouble(xStr);
                    double fLat = Double.parseDouble(yStr);

                    if (bFirstReceiveMyPos == true)
                    {
                        cur_google_lat = fLat;
                        cur_google_lon = fLon;

                        shop_lat = cur_google_lat;
                        m_ShopInfo.latitude = shop_lat;
                        shop_lon = cur_google_lon;
                        m_ShopInfo.longitude = shop_lon;
                        showOverlay();

                        bFirstReceiveMyPos = false;
                    }
                    else
                    {
                        shop_lat = fLat;
                        m_ShopInfo.latitude = shop_lat;
                        shop_lon = fLon;
                        m_ShopInfo.longitude = shop_lon;

                        showOverlay();
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);
        }
    };

    private void showOverlay()
    {
        try {
            if (mShopOverlays != null)
            {
                mapView.getOverlays().remove(mShopOverlays);
                mShopOverlays = null;
                mapView.refresh();
            }

            Drawable shop_image = getResources().getDrawable(R.drawable.shopposmark);
            mShopOverlays = new OverlayContainer(shop_image, mapView);

            OverlayItem item = new OverlayItem(new GeoPoint((int)(shop_lat * 1E6), (int)(shop_lon * 1E6)), "", "");
            mShopOverlays.addItem(item);

            mapView.getOverlays().add(mShopOverlays);
            mapView.refresh();

            GeoPoint point = new GeoPoint((int)(shop_lat * 1E6), (int)(shop_lon * 1E6));
            mapController.animateTo(point);
            mapController.setCenter(point);
            mapController.setZoom(16);

        } catch (Exception ex) {}

        return;
    }

    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0 && resultCode == 0){
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null){
                onClickHome();
            }else{
                //Users did not switch on the GPS
            }
        }
    }
    */
}
