package com.khmelenko.lab.varis.builddetails;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = BuildDetailsActivitySubcomponent.class)
public abstract class BuildDetailsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(BuildDetailsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindBuildDetailsActivityInjectorFactory(BuildDetailsActivitySubcomponent.Builder builder);
}