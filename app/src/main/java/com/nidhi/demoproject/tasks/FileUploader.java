package com.nidhi.demoproject.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.nidhi.demoproject.R;
import com.nidhi.demoproject.callbacks.IDownloadCallback;
import com.nidhi.demoproject.common.Item;
import com.nidhi.demoproject.common.MixUpValue;
import com.nidhi.demoproject.common.ProjectHeaders;
import com.nidhi.demoproject.utils.TraceUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;

public class FileUploader {

    private static final String TAG = "FileUploader";
    private Context context = null;
    private IDownloadCallback callback = null;
    private int requestId = -1;

    String fileName = "";

    String dummyName = "";

    private boolean cancelRequest = false;

    long mDownloadStartTime = 0;

    private Item mHeaders;

    public FileUploader(Context context, IDownloadCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setFileName(String fileName, String dummyName) {
        this.fileName = fileName;
        this.dummyName = dummyName;
    }

    public void setHeaders(Item aHeaders) {
        mHeaders = aHeaders;
    }

    public void userRequest(String progressMsg, int requestId, final String url, final String filePath, final String val) {
        this.requestId = requestId;

        if (!isNetworkAvailable()) {
            showUserActionResult(-1, context.getString(R.string.nipcyns));
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection conn = null;
                InputStream inputStream = null;

                DataOutputStream dos = null;

                FileInputStream fileInputStream = null;

                int bytesRead = 0, bytesAvailable;//, bufferSize;

                long totalLength = 0;

                try {

                    String link = urlEncode(url);

                    TraceUtils.logE("pic upload url", link);

                    conn = getHTTPConnection(link, val);

                    if (conn == null) {
                        postUserAction(-1, context.getString(R.string.isr));
                        return;
                    }

                    mDownloadStartTime = System.currentTimeMillis();

                    dos = new DataOutputStream(conn.getOutputStream());

                    fileInputStream = new FileInputStream(new File(filePath));

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    totalLength = bytesAvailable;

                    long totalRead = 0;

                    byte[] data = new byte[1024 * 2];
                    if (bytesAvailable != 0) {
                        while ((bytesRead = fileInputStream.read(data)) != -1 && cancelRequest == false) {
                            totalRead += bytesRead;

                            Long[] progress = new Long[5];
                            if (bytesAvailable > 0) {

                                progress[0] = (Long) ((totalRead * 100) / bytesAvailable);
                                progress[2] = (Long) totalLength;
                                progress[1] = (Long) totalRead;
                                double elapsedTimeSeconds = (System.currentTimeMillis() - mDownloadStartTime) / 1000.0;

                                double bytesPerSecond = totalRead / elapsedTimeSeconds;

                                long bytesRemaining = bytesAvailable - totalRead;

                                double timeRemainingSeconds;

                                if (bytesPerSecond > 0) {
                                    timeRemainingSeconds = bytesRemaining / bytesPerSecond;
                                } else {
                                    timeRemainingSeconds = -1.0;
                                }

                                progress[3] = (long) (elapsedTimeSeconds * 1000);

                                progress[4] = (long) (timeRemainingSeconds * 1000);

                            }

                            dos.write(data, 0, bytesRead);

                            publishProgress(progress);
                        }
                    }

                    if (cancelRequest)
                        return;

                    int serverResponseCode = conn.getResponseCode();

                    TraceUtils.logE("serverResponseCode", serverResponseCode + "");

                    String serverResponseMessage = conn.getResponseMessage();

                    TraceUtils.logE("serverResponseMessage", serverResponseMessage + "");

                    TraceUtils.logE(TAG, serverResponseCode + "   serverResponseCode");
                    TraceUtils.logE(TAG, serverResponseMessage + "   serverResponseMessage");

                    if (serverResponseCode != 200) {
                        postUserAction(-1, serverResponseMessage + "");
                        return;
                    }

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

                    String response = new String(bytebuf, "UTF-8");

                    TraceUtils.logE("file resp :::", response);

                    postUserAction(0, response);

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
                            TraceUtils.logException(e);
                        }
                    if (conn != null)
                        conn.disconnect();
                    conn = null;

                    if (dos != null) {

                        try {
                            dos.flush();
                            dos.close();
                            dos = null;
                        } catch (IOException e) {
                            TraceUtils.logException(e);
                        }
                    }

                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                            fileInputStream = null;
                        } catch (IOException e) {
                            TraceUtils.logException(e);
                        }
                    }
                }
            }
        }).start();
    }

    private void publishProgress(final Long... values) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onStateChange(1, 0, 0, values, requestId);
            }
        });
    }

    private void showUserActionResult(int status, String response) {

        switch (status) {
            case 0:
                callback.onStateChange(0, 0, 0, response, requestId);
                break;

            case -1:
                callback.onStateChange(-1, 0, 0, response, requestId);
                break;

            default:
                break;
        }

    }

    private void postUserAction(final int status, final String response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showUserActionResult(status, response);
            }
        });
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

    private HttpURLConnection getHTTPConnection(String url, String val) throws Exception {
        HttpURLConnection _conn = null;
        URL serverAddress = null;
        int socketExepCt = 0;
        int ExepCt = 0;

        for (int i = 0; i <= 2; i++) {

            try {

                serverAddress = new URL(url);

                String qvalue = serverAddress.getQuery();

                _conn = (HttpURLConnection) serverAddress.openConnection();

                if (_conn != null) {

                    _conn.setDoInput(true); // Allow Inputs
                    _conn.setDoOutput(true); // Allow Outputs
                    _conn.setUseCaches(false); // Don't use a Cached Copy
                    _conn.setRequestMethod("POST");
                    _conn.setRequestProperty("Connection", "Keep-Alive");
                    _conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    _conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****" /*+ boundary*/);
                    _conn.setRequestProperty("photo_file", dummyName);
                    _conn.setRequestProperty("X-IMI-AUTHKEY", "zq34nmvkl3ip");


                    Item defaultItem = (Item) ProjectHeaders.getInstance().getHeaders(context).clone();

                    if (defaultItem != null) {
                        Enumeration keys = defaultItem.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = defaultItem.get(key).toString();
                            TraceUtils.logE(key, value);
                            _conn.setRequestProperty(key, value);
                        }
                    }

                    if (mHeaders != null) {
                        Enumeration keys = mHeaders.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = mHeaders.get(key).toString();
                            _conn.setRequestProperty(key, value);
                        }
                    }

                    if (qvalue != null) {

                        if (val.length() == 0)
                            val = System.currentTimeMillis() + "";

                        MixUpValue value = new MixUpValue();
                        qvalue = qvalue + "&salt=" + value.getValues(val);
                        _conn.setRequestProperty("X-IMI-HASH", value.encryption(qvalue));
                        _conn.setRequestProperty("X-IMI-OAUTH", val);

                    }

                    _conn.connect();

                    return _conn;

                }
            } catch (MalformedURLException me) {
                TraceUtils.logException(me);
                throw me;
            } catch (SocketTimeoutException se) {
                TraceUtils.logException(se);
                _conn = null;
                if (i >= 2)
                    throw se;
            } catch (SocketException s) {
                TraceUtils.logException(s);
                if (socketExepCt > 2) {
                    _conn = null;
                    throw s;
                }
                socketExepCt++;
                i = 0;
                continue;
            } catch (Exception e) {
                TraceUtils.logException(e);

                if (ExepCt > 2) {
                    _conn = null;
                    throw e;
                }
                ExepCt++;
                i = 0;
                continue;
            }
        }
        return null;
    }

    /**
     * urlEncode -
     *
     * @return String
     */
    public static String urlEncode(String sUrl) {
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

    public void cancelrequest() {
        cancelRequest = true;
    }

}
