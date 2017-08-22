package com.nidhi.demoproject.tasks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;

import com.nidhi.demoproject.R;
import com.nidhi.demoproject.cache.CacheManager;
import com.nidhi.demoproject.callbacks.IItemHandler;
import com.nidhi.demoproject.common.AppPreferences;
import com.nidhi.demoproject.common.Item;
import com.nidhi.demoproject.utils.LogUtils;
import com.nidhi.demoproject.utils.TraceUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class HTTPTask {

    private Context context = null;

    private IItemHandler callback = null;

    private int requestId = -1;

    private boolean progressFlag = true;

    private double timeTaken = 0;

    private GetConnection getConn = null;

    private Item mHeaders;

    private Object obj = null;

    private int cacheType = 0;

    private CacheManager cManager = null;

    private Dialog dialog = null;

    private int deleteFileCount = -1;

    private boolean isRequestCancelled = false;

    public HTTPTask(Context context, IItemHandler callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setHeaders(Item aHeaders) {
        this.mHeaders = aHeaders;
    }

    public void setCacheType(int aCacheType) {
        cacheType = aCacheType;
    }

    public void disableProgress() {
        progressFlag = false;
    }

    public void setDeleteCacheFiles(int pageCount) {
        deleteFileCount = pageCount;
    }

    public void userRequest(String progressMsg, int requestId, final String url) {
        this.requestId = requestId;

        if (progressFlag)
            showProgress(progressMsg, context);

        if (!isNetworkAvailable()) {
            showUserActionResult(-1, context.getString(R.string.nipcyns));
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;
                InputStream inputStream = null;
                ByteArrayOutputStream baos = null;

                try {

                    String requestUrl = urlEncode(url);

                    if (cacheType != 0) {
                        cManager = CacheManager.getInstance();

                        if (deleteFileCount > -1) {
                            //cManager.deleteCacheFiles(deleteFileCount, requestUrl);
                        }

                        obj = cManager.getCache(context, requestUrl, cacheType);
                        if (obj != null) {
                            postUserAction(0, new JSONObject(obj.toString()));
                            return;
                        }
                    }

                    long startTime = System.currentTimeMillis();

                    TraceUtils.logE("request URL: ", url + "");

                    getConn = new GetConnection(context);
                    getConn.setRequestMethod("GET");
                    getConn.setRequestHeaders(mHeaders);
                    conn = getConn.getHTTPConnection(requestUrl, AppPreferences.getInstance(context).getStringFromStore("X_IMI_OAUTHKEY"));

                    long endTime = System.currentTimeMillis();

                    timeTaken = endTime - startTime;

                    timeTaken = timeTaken / 1000.0;

                    if (conn == null) {
                        postUserAction(-1, context.getString(R.string.isr));
                        return;
                    }

                    inputStream = conn.getInputStream();

                    if (isRequestCancelled)
                        return;

                    byte[] bytebuf = new byte[0x1000];

                    baos = new ByteArrayOutputStream();
                    for (; ; ) {
                        int len = inputStream.read(bytebuf);
                        if (len < 0)
                            break;
                        baos.write(bytebuf, 0, len);
                    }

                    bytebuf = baos.toByteArray();

                    obj = new JSONObject(new String(bytebuf, "UTF-8"));

                    //(final Context context, final String url, final String response, final Item headers, final int result) {
                    LogUtils.logRequestLocally(context, url, "", obj.toString(), getConn.getRequestHeaders(), "-1");

                    if (cacheType != 0) {
                        if((!((JSONObject)obj).optString("status").equalsIgnoreCase("Failed")))
                            cManager.setCache(context, requestUrl, cacheType, new String(bytebuf, "UTF-8"));
                    }

                    TraceUtils.logE("response-=-=--=-=-", obj.toString() + "");

                    postUserAction(0, obj);

                } catch (MalformedURLException me) {
                    TraceUtils.logException(me);
                    postUserAction(-1, context.getString(R.string.iurl));
                } catch (ConnectException e) {
                    TraceUtils.logException(e);
                    postUserAction(-1, context.getString(R.string.snr1));
                } catch (SocketException se) {
                    TraceUtils.logException(se);
                    postUserAction(-1, context.getString(R.string.snr2));
                } catch (SocketTimeoutException stex) {
                    TraceUtils.logException(stex);
                    postUserAction(-1, context.getString(R.string.sct));
                } catch (Exception ex) {
                    TraceUtils.logException(ex);
                    postUserAction(-1, context.getString(R.string.snr3));
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    try {

                        if (baos != null) {
                            baos.reset();
                            baos.flush();
                            baos = null;
                        }

                    } catch (Exception e) {
                        TraceUtils.logException(e);
                        baos = null;
                    }

                    if (conn != null)
                        conn.disconnect();
                    conn = null;
                }
            }
        }).start();
    }

    private void postUserAction(final int status, final Object response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (isRequestCancelled)
                    return;

                showUserActionResult(status, response);
            }
        });
    }

    private void showUserActionResult(int status, Object response) {

        dismissProgress();

        if (isRequestCancelled)
            return;

        switch (status) {
            case 0:
                callback.onFinish(response, requestId);
                break;

            case -1:
                callback.onError((String) response, requestId);
                break;

            default:
                break;
        }

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private String urlEncode(String sUrl) {
        int i = 0;
        String urlOK = "";
        while (i < sUrl.length()) {
            if (sUrl.charAt(i) == ' ') {
                urlOK = urlOK + "%20";
            } else {
                urlOK = urlOK + sUrl.charAt(i);
            }
            i++;
        }
        return (urlOK);
    }

    private void showProgress(String title, Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        View view = View.inflate(context, R.layout.dialog_progress, null);

        dialog.setContentView(view);
        dialog.show();
    }

    private void dismissProgress() {
        if (dialog != null)
            dialog.dismiss();
        dialog = null;
    }

    public void cancelRequest() {
        disableProgress();
        isRequestCancelled = true;
    }

}
