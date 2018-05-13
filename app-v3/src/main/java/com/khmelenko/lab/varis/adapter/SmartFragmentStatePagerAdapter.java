package com.khmelenko.lab.varis.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Extension of FragmentStatePagerAdapter which intelligently caches
 * all active fragments and manages the fragment lifecycles.
 * Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Returns the fragment for the position (if instantiated)
     */
    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }
}
