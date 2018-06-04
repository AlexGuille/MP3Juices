package smarttechnologi.mp3juices;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MP3 extends AppCompatActivity {
String url= "https://www.mp3juices.cc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Verifica permisos para Android 6.0+
            isStoragePermissionGranted();
        }
        setContentView(R.layout.activity_mp3);

        WebView web= (WebView) findViewById(R.id.William);

        web.setWebViewClient(new MyWebViewClient());
        WebSettings Setting = web.getSettings();
        Setting.setJavaScriptEnabled(true);
        web.loadUrl(url);

        //descargar archivo
        web.setDownloadListener(new DownloadListener()
        {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //for downloading directly through download manager
                final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();

                request.setMimeType(mimetype);
                //------------------------COOKIE------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                final DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                new Thread("Browser download") {
                    public void run() {
                        dm.enqueue(request);
                    }
                }.start();
            }
        });


    }
       private class MyWebViewClient extends WebViewClient{

        public boolean shouldOverrideUrlLoading(WebView view,String url){

           view.loadUrl(url);
           return true;
        }

       }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


}
