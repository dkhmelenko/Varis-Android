package com.khmelenko.lab.varis.repositories.search;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = SearchResultsActivitySubcomponent.class)
public abstract class SearchResultsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(SearchResultsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindSearchResultsActivityInjectorFactory(SearchResultsActivitySubcomponent.Builder builder);
}