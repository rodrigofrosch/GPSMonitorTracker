package com.fd.gpsmonitortracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by frog on 27/12/14.
 */
public class SettingsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View backup = inflater.inflate(R.layout.fragment_settings, container, false);

        return backup;
    }
}
