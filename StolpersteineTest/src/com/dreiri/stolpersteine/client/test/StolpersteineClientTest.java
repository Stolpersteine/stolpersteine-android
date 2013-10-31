package com.dreiri.stolpersteine.client.test;

import java.util.List;

import org.csdgn.util.KDTree;

import android.test.AndroidTestCase;


public class StolpersteineClientTest extends AndroidTestCase {
    public void testClient() {
        KDTree<Object> tree = new KDTree<Object>(2);
        double[] key = new double[] {52.5191710, 13.40609120}; 
        Object obj = new Object();
        tree.add(key, obj);
        double[] bottomLeft = new double[] {52.50, 13.40};
        double[] topRight = new double[] {52.52, 13.41};
        List<Object> list = tree.getRange(bottomLeft, topRight);
        assertEquals(1, list.size());
        assertTrue(list.contains(obj));
    }
}