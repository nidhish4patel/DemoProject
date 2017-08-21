package com.nidhi.demoproject.tasks;

import android.content.Context;

import com.nidhi.demoproject.common.Item;
import com.nidhi.demoproject.common.MixUpValue;
import com.nidhi.demoproject.common.ProjectHeaders;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;

class GetConnection {

   private Context context = null;

   private Item headers = null;

   private String requestMethod = "GET";

   private String mData;

   public GetConnection(Context context) {
       this.context = context;
   }

   public void setRequestHeaders(Item item) {
       this.headers = item;
   }

    public Item getRequestHeaders() {
        return headers;
    }

   public void setRequestMethod(String requestMethod) {
       this.requestMethod = requestMethod;
   }

   public void setHashingData(String aData) {
       this.mData = aData;
   }

   public HttpURLConnection getHTTPConnection(String url, String val) throws Exception {

       HttpURLConnection _conn;
       URL serverAddress;
       int socketExepCt = 0;
       int ExepCt = 0;
       int numRedirect = 0;

       //url = urlEncode(url);

       serverAddress = new URL(url);
       String host = "http://" + serverAddress.getHost() + "/";
       String qvalue =  serverAddress.getQuery();

       for (int i = 0; i <= 2; i++) {

           try {
               //serverAddress = new URL(url);

               _conn = (HttpURLConnection) serverAddress.openConnection();
               if (_conn != null) {
                   _conn.setRequestMethod(requestMethod);
                   _conn.setReadTimeout(30000);
                   _conn.setConnectTimeout(2500); //10000
                   _conn.setInstanceFollowRedirects(false);
                   _conn.setDoOutput(false);

                   if(requestMethod.equalsIgnoreCase("POST")) {
                       _conn.setRequestMethod("POST");
                       _conn.setDoInput(true);
                       _conn.setDoOutput(true);
                       _conn.setUseCaches(false);
                       //_conn.setChunkedStreamingMode(4*1024);
                       _conn.setRequestProperty("Connection", "close");
                       _conn.setRequestProperty("Connection", "Keep-Alive");
                       _conn.setReadTimeout(120000);
                       _conn.setConnectTimeout(120000);
                       _conn.setRequestProperty("Content-Type", "application/json");
                   }

                   if(mData != null) {
                       qvalue = mData.trim();
                   }

                   if(val.length() == 0)
                       val = System.currentTimeMillis()+"";

                   if(qvalue != null) {

                       MixUpValue value = new MixUpValue();
                       qvalue = qvalue+"&salt="+value.getValues(val);
                       _conn.setRequestProperty("X-IMI-HASH", value.encryption(qvalue));

                   }

                   _conn.setRequestProperty("X-IMI-OAUTHKEY", val+"");
                   _conn.setRequestProperty("X-IMI-ACCESSTOKEN", val+"");

                   Item defaultItem = (Item) ProjectHeaders.getInstance().getHeaders(context).clone();


                   if (defaultItem != null) {
                       //TraceUtils.logE("Headers:", defaultItem.toString());
                       Enumeration keys = defaultItem.keys();
                       while (keys.hasMoreElements()) {
                           String key = keys.nextElement().toString();
                           String value = defaultItem.get(key).toString();
                            _conn.setRequestProperty(key, value);
                       }
                   }

                   if (headers != null) {
                       //TraceUtils.logE("Headers: ", headers.toString());
                       Enumeration keys = headers.keys();
                       while (keys.hasMoreElements()) {
                           String key = keys.nextElement().toString();
                           String value = headers.get(key).toString();
                           _conn.setRequestProperty(key, value);
                       }
                   }

                   int RESCODE;
                   _conn.connect();

                   if(requestMethod.equalsIgnoreCase("POST"))
                       return _conn;

                   RESCODE = _conn.getResponseCode();
                   if (RESCODE == HttpURLConnection.HTTP_OK || RESCODE == HttpURLConnection.HTTP_PARTIAL) {
                       return _conn;
                   } else if (RESCODE == HttpURLConnection.HTTP_MOVED_TEMP
                           || RESCODE == HttpURLConnection.HTTP_MOVED_PERM) {
                       if (numRedirect > 15) {
                           _conn.disconnect();
                           _conn = null;
                           break;
                       }

                       numRedirect++;
                       i = 0;
                       url = _conn.getHeaderField("Location");
                       if (!url.startsWith("http")) {
                           url = host + url;
                       }

                       _conn.disconnect();
                       _conn = null;

                   } else {
                       _conn.disconnect();
                       _conn = null;
                   }
               }
           }

           catch (MalformedURLException me) {
               throw me;
           }

           catch (SocketTimeoutException se) {
               _conn = null;
               if (i >= 2)
                   throw se;
           }

           catch (SocketException s) {
               if (socketExepCt > 2) {
                   _conn = null;
                   throw s;
               }
               socketExepCt++;
               i = 0;
           }

           catch (Exception e) {
               if (ExepCt > 2) {
                   _conn = null;
                   throw e;
               }
               ExepCt++;
               i = 0;
           }
       }
       return null;
   }

   public void clearConn() {

       if (headers != null)
           headers.clear();
       headers = null;

       requestMethod = null;

   }

}
