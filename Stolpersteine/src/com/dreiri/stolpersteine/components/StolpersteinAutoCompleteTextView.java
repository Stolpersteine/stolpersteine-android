package com.dreiri.stolpersteine.components;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.dreiri.stolpersteine.api.RetrieveStolpersteine.Callback;
import com.dreiri.stolpersteine.api.SearchData;
import com.dreiri.stolpersteine.api.StolpersteinNetworkService;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class StolpersteinAutoCompleteTextView extends AutoCompleteTextView implements TextWatcher {
    private final int LIST_SIZE = 10;
    private final int MIN_LENGTH = 3;

    private StolpersteinNetworkService networkService;
    
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public StolpersteinAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        addTextChangedListener(this);
        setThreshold(MIN_LENGTH);
        
        Drawable background = new ColorDrawable(Color.GRAY);
        background.setAlpha(128);
        if (android.os.Build.VERSION.SDK_INT < 16) {
            setBackgroundDrawable(background);
        } else {
            setBackground(background);
        }
    }
    
    public StolpersteinNetworkService getNetworkService() {
        return networkService;
    }

    public void setNetworkService(StolpersteinNetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        SearchData searchData = new SearchData();
        searchData.setKeyword(s.toString());
        networkService.retrieveStolpersteine(searchData, 0, LIST_SIZE, new Callback() {
            @Override
            public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
                if (stolpersteine != null) {
                    ArrayList<String> suggestions = new ArrayList<String>();
                    for (Stolperstein stolperstein : stolpersteine) {
                        String name = stolperstein.getPerson().getNameAsString();
                        String street = stolperstein.getLocation().getStreet();
                        suggestions.add(name + ", " + street);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
                    setAdapter(adapter);
                }
            }
        });
    }
}
