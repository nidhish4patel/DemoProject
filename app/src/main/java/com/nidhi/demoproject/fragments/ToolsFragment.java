package com.nidhi.demoproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nidhi.demoproject.R;

/**
 * Created by nidhi on 5/18/2017.
 */

public class ToolsFragment extends Fragment {
private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_tools,container,false);
        return view;
    }
}
