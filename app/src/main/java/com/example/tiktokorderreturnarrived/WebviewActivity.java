package com.example.tiktokorderreturnarrived;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.tiktokorderreturnarrived.databinding.ActivityWebviewBinding;

import org.w3c.dom.Document;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.webview.AdvancedWebView;

public class WebviewActivity extends AppCompatActivity implements AdvancedWebView.Listener {


    private ActivityWebviewBinding binding;

    //private SwipeRefreshLayout swipeRefreshLayout;
    private AdvancedWebView webView;
    private ProgressBar loading;
    private LinearLayout retry;
    private Button btnReload;

    private String TAG = "TOLATOLE";

    private String registrationId = "";
    private String baseUrl    = "https://azizahzi.id/admin/sc/#returnarrived-list";

    private String csrfToken  = "";
    private String csrfCookis = "";
    private String nama = "";
    //private String kode;

    int count_close = 1;
    int current_click = 0;

    Handler mHandlerClose = new Handler();
    public Runnable mUpdateClose = new Runnable() {
        public void run() {
            mHandlerClose.removeCallbacks(this);
            current_click = 0;
        }
    };


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        context = WebviewActivity.this;

        if (android.os.Build.VERSION.SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        webView   =  binding.webview;
        loading   =  binding.pgbarLoading;
        retry     =  binding.loadMask;
        btnReload =  binding.btnReload;

        btnReload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                webView.loadUrl(baseUrl);
            }
        });

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                webView.loadUrl(checkoutUrl);
            }
        });*/

        webView.clearCache(true);
        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.createInstance(context).startSync();
            CookieManager.getInstance().removeAllCookie();
            CookieManager.getInstance().removeSessionCookie();
            CookieSyncManager.createInstance(context).stopSync();
            CookieSyncManager.createInstance(context).sync();
        }
        CookieManager.getInstance().removeSessionCookie();

        /*boolean islogin = checkoutUrl.indexOf("/login")>0;
        if(!islogin) {
            String cookies = getCookies();
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                CookieManager.getInstance().setCookie(checkoutUrl, ar1);
            }
            CookieSyncManager.createInstance(context).sync();
        }*/

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(false);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");

        /*webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);*/

        webView.setCookiesEnabled(true);

        /*webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    //Log.e("BACK", "DO BACK");
                    webView.goBack();
                    return true;
                }

                return false;
            }
        });*/

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.i("PARDEDE URUL befload", url);

                /*if (kode!=null) {
                    kode = null;
                    webView.loadUrl("https://wepay.id/prabayar/pulsa");

                    return false;
                }*/

                /*if(url.indexOf("/login")>0) {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("default_url", loginUrl);
                    startActivity(i);
                    finish();

                    return false;
                }*/

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                baseUrl = url;
                loading.setVisibility(View.GONE);
                Log.i("PARDEDE URUL", url);

                if(url.indexOf("/login")>0) {
                    //unsetGcm_regId();
                } else {
                    //FirebaseInitial();
                }

                csrfCookis = CookieManager.getInstance().getCookie(url);
                //Log.i("PARPAR COOKIES", csrfCookis);
                setCookies();

                /*webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
                );*/

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("about:blank");
                webView.loadUrl("file:///android_asset/error_con.html");
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (url.contains("chrome-error")) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
                super.doUpdateVisitedHistory(view, url, isReload);
            }

        });

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                nama = URLUtil.guessFileName(url, contentDisposition, mimeType);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(nama);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nama);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();


            }
        });

        webView.addJavascriptInterface(new JavascriptHandler(context), "Android");
        webView.loadUrl(baseUrl);
    }

    private void setCookies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("csrfCookis", csrfCookis);
        editor.commit();
    }

    private String getCookies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("csrfCookis", csrfCookis);
    }

    private void setToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("csrfToken", csrfToken);
        editor.commit();
    }

    private String getToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("csrfToken", csrfToken);
    }

    private void setFcmRegId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fcmRegId", registrationId);
        editor.commit();
    }

    private String getFcmRegId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("fcmRegId", registrationId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();

        try {
            mHandlerClose.removeCallbacks(mUpdateClose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();

        try {
            mHandlerClose.removeCallbacks(mUpdateClose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }


    @Override
    public boolean onSupportNavigateUp() {

        return super.onSupportNavigateUp();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }

    /*private void closeHandler() {
        try {
            unregisterReceiver(mHandleLoadMainPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mHandleLoadMainPage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            webView.loadUrl(checkoutUrl);
        }
    };*/

    class MyJavaScriptInterface {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(String htmlcode) {

        }
    }

    public Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mHandlerClose.removeCallbacks(this);
            current_click = 0;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (current_click != count_close) {
            current_click++;
            Toast.makeText(context, "Tekan dua kali untuk keluar.", Toast.LENGTH_SHORT).show();
            mHandlerClose.postDelayed(mUpdateTimeTask, 1000);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

}