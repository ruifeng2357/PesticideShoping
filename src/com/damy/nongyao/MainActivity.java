package com.damy.nongyao;

import com.damy.Utils.ResolutionSet;
import com.damy.common.Global;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

	ImageView img_basedatabtn;
	ImageView img_buycatalogbtn;
	ImageView img_salecatalogbtn;
	ImageView img_storecatalogbtn;
	ImageView img_moneypaymentbtn;
	ImageView img_requestcatalogbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_main));
		
		img_basedatabtn =  (ImageView)findViewById(R.id.img_base_data);		
		img_basedatabtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBaseData();
        	}
		});
		
		img_buycatalogbtn =  (ImageView)findViewById(R.id.img_buy_catalog);		
		img_buycatalogbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBuyCatalog();
        	}
		});
		
		img_salecatalogbtn =  (ImageView)findViewById(R.id.img_sale_catalog);		
		img_salecatalogbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSaleCatalog();
        	}
		});
		
		img_storecatalogbtn =  (ImageView)findViewById(R.id.img_store_catalog);		
		img_storecatalogbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickStoreCatalog();
        	}
		});
		
		img_moneypaymentbtn =  (ImageView)findViewById(R.id.img_money_payment);		
		img_moneypaymentbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickMoneyPayment();
        	}
		});
		
		img_requestcatalogbtn =  (ImageView)findViewById(R.id.img_request_catalog);		
		img_requestcatalogbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRequestCatalog();
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

	private void onClickBack()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}
	
	private void onClickBaseData()
	{
		if ( Global.Cur_UserRole.contains("admin") )
		{
			Intent base_activity = new Intent(this, BaseShopActivity.class);
			startActivity(base_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
	private void onClickBuyCatalog()
	{
		if ( Global.Cur_UserRole.contains("admin") || Global.Cur_UserRole.contains("buying") )
		{
			Intent buy_activity = new Intent(this, BuyCatalogActivity.class);
			startActivity(buy_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
	private void onClickSaleCatalog()
	{
		if ( Global.Cur_UserRole.contains("admin") || Global.Cur_UserRole.contains("sale") )
		{
			Intent sale_activity = new Intent(this, SaleMainActivity.class);
			startActivity(sale_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
	private void onClickStoreCatalog()
	{
		if ( Global.Cur_UserRole.contains("admin") || Global.Cur_UserRole.contains("store") )
		{
			Intent store_activity = new Intent(this, StoreMovingActivity.class);
			startActivity(store_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
	private void onClickMoneyPayment()
	{
		if ( Global.Cur_UserRole.contains("admin") || Global.Cur_UserRole.contains("account") )
		{
			Intent payment_activity = new Intent(this, MoneyReportActivity.class);
			startActivity(payment_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
	private void onClickRequestCatalog()
	{
		if ( Global.Cur_UserRole.contains("admin") )
		{
			Intent request_activity = new Intent(this, RequestCatalogActivity.class);
			startActivity(request_activity);
			finish();
		}
		else
		{
			showToastMessage(getResources().getString(R.string.error_haventright));
			return;
		}
	}
	
}
