package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.activity.SearchResultsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Subcomponent
public interface SearchResultsActivitySubcomponent extends AndroidInjector<SearchResultsActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<SearchResultsActivity> {
    }
}