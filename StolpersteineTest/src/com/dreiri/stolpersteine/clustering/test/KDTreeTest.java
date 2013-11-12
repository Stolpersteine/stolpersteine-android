package com.dreiri.stolpersteine.clustering.test;

import java.util.List;

import org.csdgn.util.KDTree;

import android.test.AndroidTestCase;

public class KDTreeTest extends AndroidTestCase {
	private KDTree<Object> tree;
	
	public void setUp() {
		tree = new KDTree<Object>(2);
	}
	
    public void testContains() {
        double[] key = new double[] {52.5191710, 13.40609120}; 
        Object obj = new Object();
        tree.add(key, obj);
        double[] bottomLeft = new double[] {52.50, 13.40};
        double[] topRight = new double[] {52.52, 13.41};
        List<Object> list = tree.getRange(bottomLeft, topRight);
        assertEquals(1, list.size());
        assertTrue(list.contains(obj));
    }
    
    public void testContainsBorder() {
        double[] bottomLeft = new double[] {52.50, 13.40};
        double[] topRight = new double[] {52.52, 13.41};
        Object obj0 = new Object();
        tree.add(bottomLeft, obj0);
        Object obj1 = new Object();
        tree.add(topRight, obj1);
        List<Object> list = tree.getRange(bottomLeft, topRight);
        assertEquals(2, list.size());
        assertTrue(list.contains(obj0));
        assertTrue(list.contains(obj1));
    }

    public void testContainsSamePosition() {
		double[] key = new double[] { 52.5191710, 13.40609120 };
		Object obj0 = new Object();
		tree.add(key, obj0);
		Object obj1 = new Object();
		tree.add(key, obj1);
		double[] bottomLeft = new double[] { 52.50, 13.40 };
		double[] topRight = new double[] { 52.52, 13.41 };
		List<Object> list = tree.getRange(bottomLeft, topRight);
		assertEquals(2, list.size());
		assertTrue(list.contains(obj0));
		assertTrue(list.contains(obj1));
	}

    public void testDoesNotContain(){
        double[] key = new double[] {52.0, 13.40609120}; 
        Object obj = new Object();
        tree.add(key, obj);
        double[] bottomLeft = new double[] {52.50, 13.40};
        double[] topRight = new double[] {52.52, 13.41};
        List<Object> list = tree.getRange(bottomLeft, topRight);
        assertEquals(0, list.size());
        assertFalse(list.contains(obj));
    }
}