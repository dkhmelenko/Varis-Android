package com.khmelenko.lab.varis.util;

import com.khmelenko.lab.varis.mvp.MvpPresenter;

import java.util.HashMap;

/**
 * Keeps the presenter so it can be fetched
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public final class PresenterKeeper<P extends MvpPresenter> {

    private final HashMap<Class<?>, P> mKeeper;

    public PresenterKeeper() {
        mKeeper = new HashMap<>();
    }

    /**
     * Put a presenter for keeping
     *
     * @param type      Presenter type
     * @param presenter Presenter instance
     */
    public void put(Class<?> type, P presenter) {
        mKeeper.put(type, presenter);
    }

    /**
     * Fetches a presenter from keeper
     *
     * @param type Presenter type
     * @return Fetched presenter instance or null
     */
    public P get(Class<?> type) {
        return mKeeper.remove(type);
    }

}
