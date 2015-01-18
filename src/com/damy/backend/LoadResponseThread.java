package com.damy.backend;

import android.app.ProgressDialog;
import android.os.Handler;

import com.damy.nongyao.BaseActivity;
import com.damy.nongyao.R;

public class LoadResponseThread extends Thread{
	private ProgressDialog	mProgressDialg = null;
	private BaseActivity	mActivity = null;
	private Handler 		mhandler = new Handler();
	
	public LoadResponseThread(BaseActivity activity){
		mActivity = activity;
		
		mProgressDialg = new ProgressDialog(mActivity);
		mProgressDialg.setCancelable(false);
		mProgressDialg.setCanceledOnTouchOutside(false);
		mProgressDialg.setMessage(mActivity.getResources().getString(R.string.common_waiting));
		mProgressDialg.show();
	}
	
	public void run() {
		mActivity.getResponseJSON();
		
		mhandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressDialg.dismiss();
				
				mActivity.refreshUI();
			}
		});
	}
}
