package com.khmelenko.lab.travisclient.mvp;

import android.app.ProgressDialog;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.khmelenko.lab.travisclient.R;

/**
 * Base activity for MVP architecture
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public abstract class MvpActivity<T extends MvpPresenter> extends AppCompatActivity {

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
