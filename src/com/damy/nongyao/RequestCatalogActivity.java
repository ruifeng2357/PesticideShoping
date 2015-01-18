package com.damy.nongyao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.damy.Utils.Base64;
import com.damy.Utils.AutoSizeEditText;
import com.damy.Utils.AutoSizeTextView;
import com.damy.Utils.ResolutionSet;
import com.damy.adapters.DialogSelectAdapter;
import com.damy.backend.HttpConnUsingJSON;
import com.damy.backend.LoadResponseThread;
import com.damy.backend.ResponseData;
import com.damy.backend.ResponseRet;
import com.damy.common.Global;
import com.damy.datatypes.STUnitInfo;
import com.damy.datatypes.STNongYaoInfo;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class RequestCatalogActivity extends BaseActivity {

	private int get_method = 1;
	private int level_check = 0;
	private boolean request_flag = false;
	private boolean isCheck1 = true;
	private boolean isCheck2 = false;
	private ArrayList<STUnitInfo> m_UnitList;
	private ArrayList<STNongYaoInfo> m_NongYaoList;
	private LinearLayout m_MaskLayer;
	private PopupWindow dialog_unit;
	private PopupWindow dialog_kind;

	private AutoSizeEditText txt_cata_permit_id;
	private AutoSizeEditText txt_cata_register_id;
	private AutoSizeEditText txt_cata_sample;
	private AutoSizeEditText txt_cata_name;
	private AutoSizeEditText txt_cata_nickname;
	private AutoSizeEditText txt_cata_shape;
	private AutoSizeEditText txt_cata_material;
	private AutoSizeEditText txt_cata_content;
	private AutoSizeEditText txt_cata_product;
	private AutoSizeEditText txt_cata_product_area;
	private AutoSizeEditText txt_cata_description;
	private AutoSizeTextView txt_cata_username;
	private AutoSizeTextView txt_cata_regtime;

	private ImageView img_cata_photo;
	private ImageView img_catalevel_check1;
	private ImageView img_catalevel_check2;

	private String m_szSelPath = "";
	private Uri m_szSelUri = null;
	private int REQUEST_PHOTO = 0;
	
	private int pass = 0;
	private int dup_username = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_catalog);
		
		ResolutionSet._instance.iterateChild(findViewById(R.id.fl_requestcatalog));
		
		FrameLayout fl_backbtn = (FrameLayout)findViewById(R.id.fl_request_backbtn);
		FrameLayout fl_homebtn = (FrameLayout)findViewById(R.id.fl_request_homebtn);
		FrameLayout fl_requestcatalog_savebtn = (FrameLayout)findViewById(R.id.fl_requestcatalog_save);
		FrameLayout fl_requestcatalog_closebtn = (FrameLayout)findViewById(R.id.fl_requestcatalog_close);		
				
		txt_cata_permit_id = (AutoSizeEditText)findViewById(R.id.txt_catalog_permit_id);
		txt_cata_register_id = (AutoSizeEditText)findViewById(R.id.txt_catalog_register_id);
		txt_cata_name = (AutoSizeEditText)findViewById(R.id.txt_catalog_name);
		txt_cata_shape = (AutoSizeEditText)findViewById(R.id.txt_catalog_shape);
		txt_cata_product = (AutoSizeEditText)findViewById(R.id.txt_catalog_product);
		txt_cata_sample = (AutoSizeEditText)findViewById(R.id.txt_catalog_sample);
		txt_cata_product_area = (AutoSizeEditText)findViewById(R.id.txt_catalog_productarea);
		txt_cata_description = (AutoSizeEditText)findViewById(R.id.txt_catalog_description);
		txt_cata_content = (AutoSizeEditText)findViewById(R.id.txt_catalog_content);
		txt_cata_material = (AutoSizeEditText)findViewById(R.id.txt_catalog_material);
		txt_cata_username = (AutoSizeTextView)findViewById(R.id.txt_catalog_username);
		txt_cata_nickname = (AutoSizeEditText)findViewById(R.id.txt_catalog_nickname);
		txt_cata_regtime = (AutoSizeTextView)findViewById(R.id.txt_catalog_regtime);	

		img_catalevel_check1 = (ImageView)findViewById(R.id.img_cata_level_no);
		img_catalevel_check2 = (ImageView)findViewById(R.id.img_cata_level_yes);
		img_cata_photo = (ImageView)findViewById(R.id.img_catalog_photo);
		
		txt_cata_username.setEnabled(false);
		txt_cata_username.setText(Global.Cur_UserName);
		
		Date curDate = new Date();
		String strDate = String.valueOf(curDate.getYear() + 1900) + "-" + String.valueOf(curDate.getMonth() + 1) + "-" + String.valueOf(curDate.getDate());
		txt_cata_regtime.setText(strDate);
		
		AutoSizeTextView check_register_id = (AutoSizeTextView)findViewById(R.id.txt_catalog_check_register_id);
		
		check_register_id.setOnClickListener(new OnClickListener() {
						
			public void onClick(View v) {
				
				if (txt_cata_register_id.getText().toString().length() == 0) {
					showToastMessage(getResources().getString(
							R.string.error_required_register_id));
					return;
				}
				request_flag = false;
				new LoadResponseThread(RequestCatalogActivity.this).start();
			}
		});
		
		//readContent();
		
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
		
		fl_requestcatalog_savebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
        		onClickSave();
        	}			
		});		
		
		fl_requestcatalog_closebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
        		onClickClose();
        	}			
		});	
				
		img_catalevel_check1.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(true);        	
        	}
        });	
		
		img_catalevel_check2.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickCheck(false);        	
        	}
        });
				
		
		img_cata_photo.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		onClickSelectPhoto();        	
        	}
        });		

		
		m_MaskLayer = (LinearLayout)findViewById(R.id.ll_request_catalog_masklayer);
		m_MaskLayer.setVisibility(View.INVISIBLE);

        Thread.UncaughtExceptionHandler mUEHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(mUEHandler);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onClickBack();
			return true;
		}
		return false;
	}


	private void onClickBack() {
		// finish();
		onClickHome();
	}

	private void onClickHome() {
		Intent main_activity = new Intent(this, MainActivity.class);
		startActivity(main_activity);
		finish();
	}

	private void onClickSave() {
		request_flag = true;
		
		if (txt_cata_register_id.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_register_id));
			return;
		}
		
		if (txt_cata_permit_id.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_permit_id));
			return;
		}
		
		if (txt_cata_sample.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_sample));
			return;
		}
		
		if (txt_cata_name.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_cata_name));
			return;
		}
		
		if (txt_cata_nickname.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_nickname));
			return;
		}
		
		if (txt_cata_product.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_product));
			return;
		}

		if (txt_cata_shape.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_shape));
			return;
		}
		
		if (txt_cata_material.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_material));
			return;
		}
		
		if (txt_cata_content.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_content));
			return;
		}
		
		if (txt_cata_product_area.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_product_area));
			return;
		}

		if (txt_cata_description.getText().toString().length() == 0) {
			showToastMessage(getResources().getString(
					R.string.error_required_description));
			return;
		}
		
		if ((isCheck1 && isCheck2) | (!isCheck1 && !isCheck2)) {
			showToastMessage(getResources().getString(
					R.string.error_select_role));
			return;
		}
		if (m_szSelPath.equals("") && m_szSelUri == null) {
			showToastMessage(getResources().getString(
					R.string.error_select_image));
			return;
		}

		new LoadResponseThread(RequestCatalogActivity.this).start();
	}

	private void onClickClose() {
		// finish();
		onClickHome();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); // To change body
																// of overridden
																// methods use
																// File |
																// Settings |
																// File
																// Templates.

		if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK)
			updateUserImage(data);
	}

	private void updateUserImage(Intent data) {
		if (data.getIntExtra(SelectPhotoActivity.szRetCode, -999) == SelectPhotoActivity.nRetSuccess) {
			Object objPath = data.getExtras().get(SelectPhotoActivity.szRetPath);
			Object objUri = data.getExtras().get(SelectPhotoActivity.szRetUri);

			if (objPath != null) {
				m_szSelPath = (String) objPath;
				updateUserImageWithPath(m_szSelPath);
			}

			if (objUri != null) {
				m_szSelUri = (Uri) objUri;
				img_cata_photo.setImageURI(m_szSelUri);
			}
		}
	}

	private void updateUserImageWithPath(String szPath) {
		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(szPath, options);
            options.inSampleSize = computeSampleSize(options, SelectPhotoActivity.IMAGE_WIDTH, SelectPhotoActivity.IMAGE_HEIGHT * SelectPhotoActivity.IMAGE_WIDTH);
            options.inJustDecodeBounds = false;
            options.inInputShareable = true;
            options.inPurgeable = true;

            bitmap = BitmapFactory.decodeFile(szPath, options);

			img_cata_photo.setImageBitmap(bitmap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

	private void onClickSelectPhoto() {
		Intent intent = new Intent(this, SelectPhotoActivity.class);
		startActivityForResult(intent, REQUEST_PHOTO);
	}

	private void onClickCheck(boolean check) {
		if (isCheck1 == false && check == true) {
			level_check = 0;
			img_catalevel_check1
					.setImageResource(R.drawable.baseuser_add_checkbox2);
			img_catalevel_check2
					.setImageResource(R.drawable.baseuser_add_checkbox1);
			isCheck1 = true;
			isCheck2 = false;
		} else if (isCheck2 == false && check == false) {
			level_check = 1;
			img_catalevel_check1
					.setImageResource(R.drawable.baseuser_add_checkbox1);
			img_catalevel_check2
					.setImageResource(R.drawable.baseuser_add_checkbox2);
			isCheck1 = false;
			isCheck2 = true;
		}

	}
	
	public void refreshUI() {
		super.refreshUI();

		if (request_flag) {			
		
			if (m_nResponse == ResponseRet.RET_SUCCESS) {
				showToastMessage(getResources().getString(
						R.string.common_success));
				onClickClose();
			}
		}
		else
		{
			if (pass == 0)
				showToastMessage(getResources().getString(R.string.error_registerid_nopass));
			else if (pass == 1)
				showToastMessage(getResources().getString(R.string.error_registerid_duplicate));
			else
				showToastMessage(getResources().getString(R.string.error_registerid_reject));
		}
	}

	public void getResponseJSON() {
		try {
			m_nResponse = ResponseRet.RET_SUCCESS;
			JSONObject response;

			if (request_flag) {
				String strUrl = "";
				strUrl = HttpConnUsingJSON.REQ_REQUESTADDCATALOG;
				response = m_HttpConnUsingJSON.getPostJSONObject(strUrl);

				if (response == null) {
					m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
					return;
				}
				m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
				if (m_nResponse == ResponseRet.RET_SUCCESS) {

				}				
					
			} else {				
					String strRequest = HttpConnUsingJSON.REQ_CHECKREGISTERID;
					strRequest += "?register_id=" + txt_cata_register_id.getText().toString();
					strRequest += "&shop_id=" + Global.Cur_ShopId;
					
					response = m_HttpConnUsingJSON.getGetJSONObject(strRequest);

					if (response == null) {
						m_nResponse = ResponseRet.RET_INTERNAL_EXCEPTION;
						return;
					}
					
					m_nResponse = response.getInt(ResponseData.RESPONSE_RET);
					if (m_nResponse == ResponseRet.RET_SUCCESS) {
						JSONObject dataObject = response
								.getJSONObject(ResponseData.RESPONSE_DATA);
						
						pass = dataObject.getInt("pass");
					}
				
			}

		} catch (JSONException e) {
			e.printStackTrace();
			m_nResponse = ResponseRet.RET_JSON_EXCEPTION;
		}
	}

	public JSONObject makeRequestJSON() throws JSONException {
		JSONObject requestObj = new JSONObject();

		requestObj.put("uid", String.valueOf(Global.Cur_UserId));
		requestObj.put("shop_id", String.valueOf(Global.Cur_ShopId));
		requestObj.put("permit_id", txt_cata_permit_id.getText().toString());
		requestObj.put("register_id", txt_cata_register_id.getText().toString());
		requestObj.put("sample_id", txt_cata_sample.getText().toString());
		requestObj.put("shape", txt_cata_shape.getText().toString());
		requestObj.put("material", txt_cata_material.getText().toString());
		requestObj.put("content", txt_cata_content.getText().toString());
		requestObj.put("catalog_nickname", txt_cata_nickname.getText().toString());
		requestObj.put("catalog_usingname", txt_cata_name.getText().toString());
		requestObj.put("product", txt_cata_product.getText().toString());
		requestObj.put("level", Integer.toString(level_check));
		requestObj.put("product_area", txt_cata_product_area.getText().toString());
		requestObj.put("description", txt_cata_description.getText().toString());
		

		try {

			if (m_szSelPath != null && !m_szSelPath.equals("")) {
				File file = new File(m_szSelPath);
				FileInputStream fis = new FileInputStream(file);
				byte[] buff = new byte[(int) file.length()];
				fis.read(buff, 0, (int) file.length());
				String imageURI = Base64.encodeBytes(buff);
				requestObj.put("image", imageURI);
				fis.close();
			} else if (m_szSelUri != null) {
				InputStream fis = null;
				Bitmap bmp = null;
				fis = getContentResolver().openInputStream(m_szSelUri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bmp = BitmapFactory.decodeStream(fis, null, options);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				String imgEncoded = Base64.encodeBytes(byteArray);
				requestObj.put("image", imgEncoded);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return requestObj;
	}
}
