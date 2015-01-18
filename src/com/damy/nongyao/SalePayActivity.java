package com.damy.nongyao;

import com.damy.Utils.ResolutionSet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class SalePayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_pay);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.ll_salepay));
		
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
}
