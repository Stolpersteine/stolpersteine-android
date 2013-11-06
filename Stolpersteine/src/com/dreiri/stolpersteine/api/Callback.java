package com.dreiri.stolpersteine.api;

import java.util.List;

import com.dreiri.stolpersteine.api.model.Stolperstein;


public interface Callback {
    public void handle(List<Stolperstein> stolpersteine);
}
