package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.activity.BuildDetailsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Subcomponent
public interface BuildDetailsActivitySubcomponent extends AndroidInjector<BuildDetailsActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<BuildDetailsActivity> {
    }
}