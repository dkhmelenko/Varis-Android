package com.khmelenko.lab.varis.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.util.SparseArray
import android.view.ViewGroup

/**
 * Extension of FragmentStatePagerAdapter which intelligently caches
 * all active fragments and manages the fragment lifecycles.
 * Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
abstract class SmartFragmentStatePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    // Sparse array to keep track of registered fragments in memory
    private val mRegisteredFragments = SparseArray<Fragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        mRegisteredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        mRegisteredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    /**
     * Returns the fragment for the position (if instantiated)
     */
    fun getRegisteredFragment(position: Int): Fragment {
        return mRegisteredFragments.get(position)
    }
}
