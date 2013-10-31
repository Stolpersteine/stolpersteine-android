package com.dreiri.stolpersteine.callback;

import java.util.ArrayList;

import com.dreiri.stolpersteine.models.Stolperstein;

public interface Callback {
    public void handle(ArrayList<Stolperstein> stolpersteine);
}
