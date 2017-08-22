package com.nidhi.demoproject.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nidhi.demoproject.R;
import com.nidhi.demoproject.callbacks.IItemHandler;
import com.nidhi.demoproject.tasks.HTTPTask;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by nidhi on 5/17/2017.
 */

public class CameraFragment extends Fragment implements IItemHandler {

    private View view;
    
    private LinearLayout linearlayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_camera,container,false);

        linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);

//        getData();

        return view;
    }

    private void getData(){
        HTTPTask httpTask = new HTTPTask(getActivity(),this);
//        httpTask.disableProgress();
        httpTask.userRequest("Loading",1,"https://wise.strose.edu/rest/public/AllDirectoryEntries/json");
    }

    @Override
    public void onFinish(Object results, int requestId) {
        switch (requestId){
            case 1:
                parseJson(results);
                break;
        }
    }

    @Override
    public void onError(String errorCode, int requestId) {
        Toast.makeText(getActivity(),errorCode,Toast.LENGTH_SHORT);
    }

    private void parseJson(Object results){
        try {

            JSONArray jsonArray = (JSONArray) results;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                TextView tvId = new TextView(getActivity());
                tvId.setText("ID : "+jsonObject.getInt("ID"));
                linearlayout.addView(tvId);

                TextView tvFname = new TextView(getActivity());
                tvFname.setText("FIRSTNAME : "+jsonObject.getString("FIRSTNAME"));
                linearlayout.addView(tvFname);

                TextView tvLname = new TextView(getActivity());
                tvLname.setText("LASTNAME : "+jsonObject.getString("LASTNAME"));
                linearlayout.addView(tvLname);

                TextView tvbuilding = new TextView(getActivity());
                tvbuilding.setText("BUILDING : "+jsonObject.getString("BUILDING"));
                linearlayout.addView(tvbuilding);

                TextView tvphone = new TextView(getActivity());
                tvphone.setText("PHONE : "+jsonObject.getString("PHONE"));
                linearlayout.addView(tvphone);

                TextView tvemail = new TextView(getActivity());
                tvemail.setText("EMAIL : "+jsonObject.getString("EMAIL"));
                linearlayout.addView(tvemail);

                TextView tvdept = new TextView(getActivity());
                tvdept.setText("DEPT : "+jsonObject.getString("DEPT"));
                linearlayout.addView(tvdept);


                TextView tvposition = new TextView(getActivity());
                tvposition.setText("POSITION : "+jsonObject.getString("POSITION"));
                linearlayout.addView(tvposition);


                TextView tvurl = new TextView(getActivity());
                tvurl.setText("URL : "+jsonObject.getString("URL"));
                linearlayout.addView(tvurl);

                View view = new View(getActivity());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3));
                view.setBackgroundColor(Color.BLACK);
                linearlayout.addView(view);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
