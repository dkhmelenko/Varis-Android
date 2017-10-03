package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.activity.RepoDetailsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Subcomponent
public interface RepoDetailsActivitySubcomponent extends AndroidInjector<RepoDetailsActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RepoDetailsActivity> {
    }
}