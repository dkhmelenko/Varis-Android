package com.khmelenko.lab.varis.activity

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity
 *
 * @author Dmytro Khmelenko
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Adds new fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected fun addFragment(@IdRes containerViewId: Int,
                              fragment: Fragment,
                              fragmentTag: String) {
        if (!fragment.isAdded) {
            supportFragmentManager
                    .beginTransaction()
                    .add(containerViewId, fragment, fragmentTag)
                    .disallowAddToBackStack()
                    .commit()
        }
    }

    /**
     * Replaces fragment
     *
     * @param containerViewId    ID of the container view for fragment
     * @param fragment           Fragment instance
     * @param fragmentTag        Fragment tag
     * @param backStackStateName Name in back stack
     */
    protected fun replaceFragment(@IdRes containerViewId: Int,
                                  fragment: Fragment,
                                  fragmentTag: String,
                                  backStackStateName: String?) {
        supportFragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit()
    }

    /**
     * Replaces fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected fun replaceFragment(@IdRes containerViewId: Int,
                                  fragment: Fragment,
                                  fragmentTag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .commit()
    }

    /**
     * Detaches fragment
     *
     * @param fragment Fragment
     */
    protected fun detachFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .detach(fragment)
                .commit()
    }
}
