package com.khmelenko.lab.varis.repodetails

import android.app.Activity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [(RepoDetailsActivitySubcomponent::class)])
abstract class RepoDetailsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(RepoDetailsActivity::class)
    internal abstract fun bindRepoDetailsActivityInjectorFactory(
            builder: RepoDetailsActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent
interface RepoDetailsActivitySubcomponent : AndroidInjector<RepoDetailsActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<RepoDetailsActivity>()
}
