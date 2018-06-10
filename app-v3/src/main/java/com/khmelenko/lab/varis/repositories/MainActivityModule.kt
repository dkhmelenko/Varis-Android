package com.khmelenko.lab.varis.repositories

import android.app.Activity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [(MainActivitySubcomponent::class)])
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    internal abstract fun bindMainActivityInjectorFactory(builder: MainActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent
interface MainActivitySubcomponent : AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>()
}
