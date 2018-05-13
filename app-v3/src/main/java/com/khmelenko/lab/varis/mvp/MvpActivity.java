package com.khmelenko.lab.varis.mvp;

import android.app.ProgressDialog;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.Toast;

import com.khmelenko.lab.varis.R;

/**
 * Base activity for MVP architecture
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public abstract class MvpActivity<T extends MvpPresenter> extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ProgressDialog mProgressDialog;

    @Override
    protected void onResume() {
        super.onResume();
        attachPresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        showLoadingProgress(false);
        getPresenter().detach();
    }

    /**
     * Shows the progress of the loading
     *
     * @param isLoading True, if loading is in progress. False otherwise
     */
    protected void showLoadingProgress(boolean isLoading) {
        if (isLoading) {
            mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
        } else {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    /**
     * Shows a toast message
     *
     * @param message Message
     */
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a toast message
     *
     * @param resId Message
     */
    protected void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Adds new fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        if (!fragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(containerViewId, fragment, fragmentTag)
                    .disallowAddToBackStack()
                    .commit();
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
    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }

    /**
     * Replaces fragment
     *
     * @param containerViewId ID of the container view for fragment
     * @param fragment        Fragment instance
     * @param fragmentTag     Fragment tag
     */
    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .commit();
    }

    /**
     * Detaches fragment
     *
     * @param fragment Fragment
     */
    protected void detachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .commit();
    }

    /**
     * Gets attached presenter
     *
     * @return Presenter
     */
    protected abstract T getPresenter();

    /**
     * Does presenter attachment
     */
    protected abstract void attachPresenter();

}
