package com.dreiri.stolpersteine.client;

import java.util.List;

import com.dreiri.stolpersteine.models.Stolperstein;

public interface Callback {
    public void handle(List<Stolperstein> stolpersteine);
}
