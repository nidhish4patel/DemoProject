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
import com.nidhi.demoproject.cache.CacheManager;
import com.nidhi.demoproject.callbacks.IItemHandler;
import com.nidhi.demoproject.R;
import com.nidhi.demoproject.common.AppPreferences;
import com.nidhi.demoproject.common.Item;
import com.nidhi.demoproject.utils.LogUtils;
import com.nidhi.demoproject.utils.TraceUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class HTTPPostTask {

    private Context mContext = null;

    private IItemHandler callback = null;

    private int requestId = -1;

    private boolean progressFlag = true;

    private Item mHeaders = null;

    private Object obj = null;

    private Dialog dialog = null;

    private GetConnection getConn = null;

    private int cacheType = 0;

    private CacheManager mCacheManager = null;

    private boolean isRequestCancelled = false;

    public HTTPPostTask(Context aContext, IItemHandler callback) {
        this.mContext = aContext;
        this.callback = callback;
    }

    public void disableProgress() {
        progressFlag = false;
    }

    public void setHeaders(Item aItem) {
        mHeaders = aItem;
        TraceUtils.logE("Headers: ", "Headers: " + aItem);
    }

    public void setCacheType(int aCacheType) {
        cacheType = aCacheType;
    }

    private String networkType = "mobile";

    public void userRequest(String progressMsg, int requestId, final String url, final String postData) {

        this.requestId = requestId;

        if (progressFlag)
            showProgress(progressMsg, mContext);

        if (cacheType == 0)
            if (!isNetworkAvailable()) {
                showUserActionResult(-1, mContext.getString(R.string.nipcyns));
                return;
            }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;
                DataOutputStream outputStream = null;
                InputStream inputStream = null;

                try {

                    String requestUrl = urlEncode(url);

                    if (cacheType != 0) {
                        mCacheManager = CacheManager.getInstance();

                        obj = mCacheManager.getCache(mContext, requestUrl, cacheType);
                        if (obj != null) {
                            postUserAction(0, "");
                            return;
                        }
                    }

                    if (!isNetworkAvailable()) {
                        showUserActionResult(-1, mContext.getString(R.string.nipcyns));
                        return;
                    }

                    TraceUtils.logE("requestUrl", requestUrl + "");

                    getConn = new GetConnection(mContext);
                    getConn.setRequestMethod("POST");
                    getConn.setRequestHeaders(mHeaders);
                    //getConn.setNetworkType(networkType);
                    getConn.setHashingData(postData);
                    conn = getConn.getHTTPConnection(requestUrl, AppPreferences.getInstance(mContext).getStringFromStore("oauth").trim());

                    if (conn == null) {
                        postUserAction(-1, mContext.getString(R.string.isr));
                        return;
                    }

                    String endata = postData; //postData.length() > 0 ? odpRC4.encrypt(postData) : postData;

                    if (isRequestCancelled)
                        return;

                    inputStream = new ByteArrayInputStream(endata.getBytes()/*postData.getBytes()*/);

                    outputStream = new DataOutputStream(conn.getOutputStream());

                    byte[] data = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(data)) != -1) {
                        outputStream.write(data, 0, bytesRead);
                    }

                    int serverResponseCode = conn.getResponseCode();

                    TraceUtils.logE("Body", postData + "");
                    TraceUtils.logE("serverResponseCode", serverResponseCode + "");
                    TraceUtils.logE("Time", conn.getConnectTimeout() + "");

                    if (serverResponseCode == 200) {

                        inputStream = conn.getInputStream();

                        byte[] bytebuf = new byte[0x1000];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        for (; ; ) {
                            int len = inputStream.read(bytebuf);
                            if (len < 0)
                                break;
                            baos.write(bytebuf, 0, len);
                        }
                        bytebuf = baos.toByteArray();

                        obj = new String(bytebuf, "UTF-8");

                        if (cacheType != 0) {
                            mCacheManager.setCache(mContext, requestUrl, cacheType, obj);
                        }

                        TraceUtils.logE("response" + HTTPPostTask.this.requestId, obj.toString() + "");

                        LogUtils.logRequestLocally(mContext, url, endata, obj + "", getConn.getRequestHeaders(), "SC: " + serverResponseCode);

                        postUserAction(0, "");
                        return;

                    }

                    String serverResponseMessage = conn.getResponseMessage();

                    LogUtils.logRequestLocally(mContext, url, endata, obj + "", getConn.getRequestHeaders(), "SC: " + serverResponseCode + " SR:" + serverResponseMessage);

                    postUserAction(-1, serverResponseMessage);

                } catch (MalformedURLException me) {
                    postUserAction(-1, mContext.getString(R.string.iurl));
                } catch (ConnectException e) {
                    LogUtils.logErrorRequestLocally(mContext, url, postData, getConn.getRequestHeaders(), e);
                    postUserAction(-1, mContext.getString(R.string.snr1));
                } catch (SocketException se) {
                    LogUtils.logErrorRequestLocally(mContext, url, postData, getConn.getRequestHeaders(), se);
                    postUserAction(-1, mContext.getString(R.string.snr2));
                } catch (SocketTimeoutException stex) {
                    LogUtils.logErrorRequestLocally(mContext, url, postData, getConn.getRequestHeaders(), stex);
                    postUserAction(-1, mContext.getString(R.string.sct));
                } catch (Exception ex) {
//                    LogUtils.logErrorRequestLocally(mContext, url, postData, getConn.getRequestHeaders(), ex);
                    postUserAction(-1, mContext.getString(R.string.snr3));
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e) {
                            TraceUtils.logException(e);
                        }

                    if (outputStream != null)
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e) {
                            TraceUtils.logException(e);
                        }

                    if (conn != null)
                        conn.disconnect();
                    conn = null;

                    if (getConn != null)
                        getConn.clearConn();
                    getConn = null;

                }
            }
        }).start();
    }

    private void postUserAction(final int status, final String response) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (isRequestCancelled)
                    return;

                showUserActionResult(status, response);

            }
        });
    }

    private void showUserActionResult(int status, String response) {

        dismissProgress();

        if (isRequestCancelled)
            return;

        switch (status) {
            case 0:
                callback.onFinish(obj, requestId);
                break;

            case -1:
                callback.onError(response, requestId);
                break;

            default:
                break;
        }

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();

        if (net != null) {
            networkType = net.getTypeName();
            return net.isConnected();
        }

        return false;

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

        //((TextView) view.findViewById(R.id.tv_prog)).setText(title);

        dialog.setContentView(view);
        if (dialog != null)
            dialog.show();

    }

    private void dismissProgress() {
        try {

            if (dialog != null)
                dialog.dismiss();
            dialog = null;

        } catch (Exception e) {
            TraceUtils.logException(e);
        }
    }

    public void cancelRequest() {
        disableProgress();
        isRequestCancelled = true;
    }

}
