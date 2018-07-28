package com.khmelenko.lab.varis.auth

import android.app.Activity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [(AuthActivitySubcomponent::class)])
abstract class AuthActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(AuthActivity::class)
    internal abstract fun bindAuthActivityInjectorFactory(
            builder: AuthActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent
interface AuthActivitySubcomponent : AndroidInjector<AuthActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AuthActivity>()
}
