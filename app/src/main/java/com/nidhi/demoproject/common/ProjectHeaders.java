package com.nidhi.demoproject.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.view.WindowManager;
import com.nidhi.demoproject.R;
import com.nidhi.demoproject.utils.TraceUtils;

public class ProjectHeaders {

    private static ProjectHeaders headers = null;

    private Item item = null;

    public static ProjectHeaders getInstance() {
        if (headers == null)
            headers = new ProjectHeaders();
        return headers;
    }

    private ProjectHeaders() {

    }

    public Item getHeaders(Context context) {

        if (item != null && item.size() > 0) {

            if (item.getAttribute("User-Agent").length() == 0)
                item.setAttribute("User-Agent", AppPreferences.getInstance(context).getStringFromStore("User-Agent") + "");

            String lng = AppPreferences.getInstance(context).getStringFromStore("language").trim();
            item.setAttribute("X-IMI-LANGUAGE", lng.length() > 0 ? lng : "en");

            item.setAttribute("accesskey", AppPreferences.getInstance(context).getStringFromStore("accessToken"));
            item.setAttribute("csrftoken", AppPreferences.getInstance(context).getStringFromStore("browsertoken"));

            return item;
        }

        item = new Item();

        try {

            if (item.getAttribute("User-Agent").length() == 0)
                item.setAttribute("User-Agent", AppPreferences.getInstance(context).getStringFromStore("User-Agent") + "");

            item.setAttribute("X-IMI-App-User-Agent", "mobile/"
                    + applicationVName(context) + "/" + applicationVCode(context)
                    + "/" + context.getString(R.string.app_name));
            item.setAttribute("X-IMI-App-OEM",
                    Build.MANUFACTURER + "");
            item.setAttribute("X-IMI-App-Model",
                    Build.MODEL + "");
            item.setAttribute("X-IMI-App-OS", "Android");
            item.setAttribute("X-IMI-App-OSVersion",
                    Build.VERSION.RELEASE + "");
            item.setAttribute("X-IMI-App-Res", getResolution(context));
            item.setAttribute("X-IMI-VERSION", applicationVName(context) + "");
            item.setAttribute("X-IMI-CHANNEL", "APP");
            item.setAttribute("X-IMI-SERVICEKEY", "A7A45FE9-ECAC-4393-BB40-357F1E5491F7");
            item.setAttribute("Content-Type", "application/json");
            item.setAttribute("X-IMI-UNIQUEID", Build.SERIAL + "");

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        return item;
    }

    private String getResolution(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        return width + "x" + height;
    }

    public String applicationVName(Context context) {
        String versionName = "";
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            TraceUtils.logException(e);
        }
        return versionName;
    }

    private int applicationVCode(Context context) {
        int versionCode = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            TraceUtils.logException(e);
        }
        return versionCode;
    }

    public void onDestroy() {

    }
}
