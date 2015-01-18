package com.damy.nongyao;

import com.damy.Utils.ResolutionSet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SaleMainActivity extends Activity {

	ImageView img_salebtn;
	ImageView img_rejectbtn;
	ImageView img_searchbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_main);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.ll_salemain));
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_salemain_backbtn);
		fl_backbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickBack();
        	}
        });
		
		img_salebtn =  (ImageView)findViewById(R.id.img_sale_nongyao);		
		img_salebtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSaleNongYao();
        	}
		});
		
		img_rejectbtn =  (ImageView)findViewById(R.id.img_reject_catalog);		
		img_rejectbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickRejectCatalog();
        	}
		});
		
		img_searchbtn =  (ImageView)findViewById(R.id.img_sale_search);		
		img_searchbtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSaleSearch();
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
		//finish();
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);	
		finish();
	}

	private void onClickSaleNongYao()
	{
		Intent sale_activity = new Intent(this, SaleCatalogActivity.class);
		startActivity(sale_activity);
	}
	
	private void onClickRejectCatalog()
	{
		Intent reject_activity = new Intent(this, SaleRejectActivity.class);
		startActivity(reject_activity);
	}
	
	private void onClickSaleSearch()
	{
		Intent search_activity = new Intent(this, SaleSearchActivity.class);
		startActivity(search_activity);
	}
}
