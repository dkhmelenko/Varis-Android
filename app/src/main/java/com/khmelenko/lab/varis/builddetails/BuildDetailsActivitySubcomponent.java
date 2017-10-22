package com.khmelenko.lab.varis.builddetails;

import com.khmelenko.lab.varis.builddetails.BuildDetailsActivity;

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