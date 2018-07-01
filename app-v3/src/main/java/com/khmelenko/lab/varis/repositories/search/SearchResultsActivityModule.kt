package com.khmelenko.lab.varis.repositories.search

import android.app.Activity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = arrayOf(SearchResultsActivitySubcomponent::class))
abstract class SearchResultsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(SearchResultsActivity::class)
    internal abstract fun bindSearchResultsActivityInjectorFactory(
            builder: SearchResultsActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent
interface SearchResultsActivitySubcomponent : AndroidInjector<SearchResultsActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SearchResultsActivity>()
}
