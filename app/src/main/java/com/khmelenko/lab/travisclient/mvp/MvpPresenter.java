package com.khmelenko.lab.travisclient.mvp;

import android.support.annotation.NonNull;

/**
 * Base presenter interface for MVP architecture
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public abstract class MvpPresenter<T extends MvpView> {

    private T mView;

    /**
     * Notifies on attach presenter to view
     */
    public abstract void onAttach();

    /**
     * Notifies on detach presenter from view
     */
    public abstract void onDetach();

    /**
     * Attaches presenter
     */
    public void attach(@NonNull T view) {
        setView(view);
        onAttach();
    }

    /**
     * Detaches presenter
     */
    public void detach() {
        onDetach();
        mView = null;
    }

    /**
     * Sets the view for the presenter
     *
     * @param view View
     */
    protected void setView(@NonNull T view) {
        mView = view;
    }

    /**
     * Gets the view
     *
     * @return View
     */
    public T getView() {
        return mView;
    }
}
