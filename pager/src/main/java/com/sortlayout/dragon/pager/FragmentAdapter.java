package com.sortlayout.dragon.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

public class FragmentAdapter implements HVLPaper.Adapter {
    private static final String TAG = "FragmentAdapter";
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private SparseArray<Fragment> fragments = new SparseArray<>(4);

    public FragmentAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void startUpdate(ViewGroup paper) {
        Log.e(TAG, "startUpdate");
    }

    @Override
    public void finishUpdate(ViewGroup paper) {
        Log.e(TAG, "finishUpdate");
        if (transaction != null) {
            transaction.commitAllowingStateLoss();
            transaction = null;
        }
    }

    @Override
    public void onCreate(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onCreate position " + position);
        Log.e(TAG, "onCreate position %0x" + itemContainer.getId());
        TestFragment testFragment = new TestFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "" + position);
        testFragment.setArguments(bundle);
        fragments.put(itemContainer.getId(), testFragment);
        if (transaction == null) {
            transaction = fragmentManager.beginTransaction();
        }
        transaction.add(itemContainer.getId(), testFragment).hide(testFragment);
    }

    @Override
    public void onShow(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onShow position " + position);
        Fragment fragment = fragments.get(itemContainer.getId());
        if (transaction == null) {
            transaction = fragmentManager.beginTransaction();
        }
        transaction.show(fragment);
    }

    @Override
    public void onActive(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onActive position " + position);

    }

    @Override
    public void onInactive(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onInactive position " + position);

    }

    @Override
    public void onHide(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onHide position " + position);
        Fragment fragment = fragments.get(itemContainer.getId());
        if (transaction == null) {
            transaction = fragmentManager.beginTransaction();
        }
        transaction.hide(fragment);
    }

    @Override
    public void onDestroy(ViewGroup itemContainer, int position) {
        Log.e(TAG, "onDestroy position " + position);
        Fragment fragment = fragments.get(itemContainer.getId());
        if (transaction == null) {
            transaction = fragmentManager.beginTransaction();
        }
        transaction.hide(fragment).remove(fragment);
    }

    @Override
    public int getCount() {
        return 10;
    }
}
