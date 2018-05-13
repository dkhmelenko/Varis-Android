package com.khmelenko.lab.varis.repodetails;

import com.khmelenko.lab.varis.repodetails.RepoDetailsActivity;

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