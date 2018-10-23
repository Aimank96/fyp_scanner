package com.example.acer.qrcodeadmin;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aiman on 12/04/2017.
 */
public class PopulateSpinner {
    List<String> spinnerArray =  new ArrayList<String>();
    List<String> typeArray =  new ArrayList<String>();

    public void type(MainActivity mainActivity, Spinner type) {
        typeArray.add("boarding");
        typeArray.add("ending");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mainActivity , android.R.layout.simple_spinner_item, typeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

    }
}
