package com.khmelenko.lab.varis.repositories;

import com.khmelenko.lab.varis.repositories.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Subcomponent
public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}