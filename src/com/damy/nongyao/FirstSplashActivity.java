package com.damy.nongyao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.*;
import android.view.View;
import android.widget.Toast;
import com.damy.HttpConn.AsyncHttpClient;
import com.damy.HttpConn.AsyncHttpResponseHandler;
import com.damy.Utils.ResolutionSet;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import com.damy.backend.HttpConnUsingJSON;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FirstSplashActivity extends Activity {
    private String upgrade_url = "";
    private String local_file_path = "";

    private boolean bDownloadFlag = true;
    ProgressDialog progDialog = null;

    private AsyncHttpResponseHandler handlerNewVersion = new AsyncHttpResponseHandler()
    {
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            try {
                JSONObject result = new JSONObject(content);

                int nRetcode = result.getInt("SVCC_RET");
                upgrade_url = result.getString("SVCC_DATA");

                if (nRetcode == 0 && upgrade_url.length() > 0)
                {
                    String strAPKName = upgrade_url.substring(upgrade_url.length()-4, upgrade_url.length());
                    if (strAPKName.equals(".apk"))
                        upgradeApp();
                    else
                    {
                        //showToast(FirstSplashActivity.this, getString(R.string.apkpath_error));
                        handler.sendEmptyMessageDelayed(0, 2000);
                    }
                }
                else
                {
                    handler.sendEmptyMessageDelayed(0, 2000);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        }

        @Override
        public void onFailure(Throwable error, String content) {
            super.onFailure(error, content);    //To change body of overridden methods use File | Settings | File Templates.
        }
    };

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message message)
        {
            onClickNext();
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_firstsplash);
		
		try
		{
	        DisplayMetrics metrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(metrics);
	        
	        if (metrics.widthPixels > metrics.heightPixels)
	        	ResolutionSet._instance.setResolution(metrics.densityDpi,
	        			metrics.heightPixels, metrics.widthPixels);
	        else
	        	ResolutionSet._instance.setResolution(metrics.densityDpi,
	        			metrics.widthPixels, metrics.heightPixels);
	        
	        ResolutionSet._instance.iterateChild(findViewById(R.id.fl_firstsplash));
	        
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

        callWebService();
	}

	private void onClickNext()
	{
		Intent login_activity = new Intent(this, LoginActivity.class);
		startActivity(login_activity);
		finish();
	}

    private void callWebService()
    {
        String version = "";
        try {
            PackageInfo info = FirstSplashActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = info.versionName;
        }catch (Exception ex) {}

        String strURL = HttpConnUsingJSON.BASE_URL + "/Service.svc/GetNewVersion?version=" + version;

        try
        {
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(4000);
            client.get(strURL, handlerNewVersion);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void upgradeApp()
    {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(FirstSplashActivity.this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getString(R.string.oldversion_error) + "\n" + getResources().getString(R.string.updateapp));
        builder.setPositiveButton(getResources().getString(R.string.Dialog_Ok), click_UpgradeBtn_Listener);
        builder.setNegativeButton(getResources().getString(R.string.Dialog_Cancel), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                handler.sendEmptyMessageDelayed(0, 10);
            }
        });
        builder.show();
    }

    private DialogInterface.OnClickListener click_UpgradeBtn_Listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            InstallNewApp();
        }
    };

    private void InstallNewApp()
    {
        Thread thr = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    int nBytesRead = 0, nByteWritten = 0;
                    byte[] buf = new byte[1024];

                    URLConnection urlConn = null;
                    URL fileUrl = null;
                    InputStream inStream = null;
                    OutputStream outStream = null;

                    File dir_item = null, file_item = null;

                    runOnUiThread(runnable_showProgress);

                    fileUrl = new URL(upgrade_url);
                    urlConn = fileUrl.openConnection();
                    inStream = urlConn.getInputStream();
                    local_file_path = upgrade_url.substring(upgrade_url.lastIndexOf("/") + 1);
                    dir_item = new File(Environment.getExternalStorageDirectory(), "Download");
                    dir_item.mkdirs();
                    file_item = new File(dir_item, local_file_path);

                    outStream = new BufferedOutputStream(new FileOutputStream(file_item));

                    while ( bDownloadFlag && ((nBytesRead = inStream.read(buf)) != -1))
                    {
                        outStream.write(buf, 0, nBytesRead);
                        nByteWritten += nBytesRead;
                        UpdateProgress(nByteWritten);
                    }

                    if (bDownloadFlag == true)
                    {
                        UpdateProgress(getResources().getString(R.string.download_success));

                        inStream.close();
                        outStream.flush();
                        outStream.close();
                        /////////////////////////////////////////////////////////////////////////

                        runOnUiThread(runnable_hideProgress);
                        runOnUiThread(runnable_finish_download);
                    }
                    else
                    {
                        runOnUiThread(runnable_download_error);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    runOnUiThread(runnable_download_error);
                }
            }
        });

        thr.start();
    }


    private void UpdateProgress(int nValue)
    {
        String strValue;
        strValue = String.format("%.2fMB", nValue / 1024.0f / 1024.0f );
        UpdateProgress(strValue);
    }

    private void UpdateProgress(final String szMsg)
    {
        Runnable runnable_update = new Runnable() {
            @Override
            public void run() {
                progDialog.setMessage(szMsg);
            }
        };

        runOnUiThread(runnable_update);
    }

    private Runnable runnable_showProgress = new Runnable() {
        @Override
        public void run() {
            showProgress();
        }
    };

    private Runnable runnable_hideProgress = new Runnable() {
        @Override
        public void run() {
            showProgress();
        }
    };

    public void showProgress()
    {
        if (progDialog == null)
        {
            progDialog = new ProgressDialog(FirstSplashActivity.this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage(getResources().getString(R.string.waiting));
            progDialog.setCancelable(false);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    bDownloadFlag = false;
                }
            });
        }

        if (progDialog.isShowing())
            return;

        progDialog.show();
    }

    public void hideProgress()
    {
        if (progDialog != null)
            progDialog.dismiss();
    }

    Runnable runnable_finish_download = new Runnable()
    {
        public void run()
        {
            Intent intent_install = new Intent( Intent.ACTION_VIEW);
            intent_install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/download/" + local_file_path)), "application/vnd.android.package-archive");
            intent_install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_install);

            Intent intent1 = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", "com.damy.nongyao", null));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);

            FirstSplashActivity.this.finish();
        }
    };

    Runnable runnable_download_error = new Runnable() {
        @Override
        public void run() {
            showToast(FirstSplashActivity.this, getResources().getString(R.string.download_fail));
            hideProgress();

            handler.sendEmptyMessageDelayed(0, 2000);
        }
    };

    Toast toast = null;
    public void showToast(Context context, String toastStr)
    {
        if ((toast == null) || (toast.getView().getWindowVisibility() != View.VISIBLE))
        {
            toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
            toast.show();
        }

        return;
    }
}
