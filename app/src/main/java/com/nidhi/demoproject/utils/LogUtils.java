package com.nidhi.demoproject.utils;

import android.content.Context;
import com.nidhi.demoproject.common.Item;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

public class LogUtils {

    public static void logRequestLocally(final Context context, final String url, final String body, final String response, final Item headers, final String result) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    TraceUtils.logE("log path", context.getExternalFilesDir(null).getAbsolutePath() + "/API.log");

                    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(context.getExternalFilesDir(null).getAbsolutePath() + "/dtacAPI.log", true)));
                    pw.append("----" + getCustomSystemTime() + "----\n");
                    pw.append("Request API ::");
                    pw.append("\n");
                    pw.append(url);
                    pw.append("\n");
                    pw.append("Request body ::");
                    pw.append("\n");
                    pw.append(body);
                    pw.append("\n");
                    pw.append("Request Headers :: ");
                    pw.append("\n");
                    if (headers != null) {
                        Hashtable requestProperty = headers.getAllAttributes();
                        Enumeration keys = requestProperty.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = requestProperty.get(key).toString();
                            pw.append(key + " : " + value);
                            pw.append("\n");
                        }
                    }
                    pw.append("\n");
                    pw.append("Response ::");
                    pw.append("\n");
                    pw.append(response);
                    pw.append("\n");
                    pw.append("Result ::");
                    pw.append("\n");
                    pw.append(result + "");
                    pw.append("\n");
                    pw.append("------------------------------\n");
                    pw.flush();
                    pw.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }).start();

    }

    public static void logErrorRequestLocally(final Context context, final String url, final String body, final Item headers, final Exception ex) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {

                    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(context.getExternalFilesDir(null).getAbsolutePath() + "/dtacAPI.log", true)));
                    pw.append("----" + getCustomSystemTime() + "----\n");
                    pw.append("Request API ::");
                    pw.append("\n");
                    pw.append(url);
                    pw.append("\n");
                    pw.append("Request body ::");
                    pw.append("\n");
                    pw.append(body);
                    pw.append("\n");
                    pw.append("Request Headers :: ");
                    pw.append("\n");
                    if (headers != null) {
                        Hashtable requestProperty = headers.getAllAttributes();
                        Enumeration keys = requestProperty.keys();
                        while (keys.hasMoreElements()) {
                            String key = keys.nextElement().toString();
                            String value = requestProperty.get(key).toString();
                            pw.append(key + " : " + value);
                            pw.append("\n");
                        }
                    }
                    pw.append("\n");
                    pw.append("Exception ::");
                    pw.append("\n");
                    ex.printStackTrace(pw);
                    pw.append("\n");

                    pw.append("------------------------------\n");
                    pw.flush();
                    pw.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }).start();

    }

    private static String getCustomSystemTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyy hh:mm:ss", Locale.ENGLISH);
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

}
