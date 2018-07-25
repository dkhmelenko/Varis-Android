package com.khmelenko.lab.varis.builddetails

import android.app.Activity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = [(BuildDetailsActivitySubcomponent::class)])
abstract class BuildDetailsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(BuildDetailsActivity::class)
    internal abstract fun bindBuildDetailsActivityInjectorFactory(
            builder: BuildDetailsActivitySubcomponent.Builder): AndroidInjector.Factory<out Activity>
}

@Subcomponent
interface BuildDetailsActivitySubcomponent : AndroidInjector<BuildDetailsActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BuildDetailsActivity>()
}
