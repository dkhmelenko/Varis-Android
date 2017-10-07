package com.khmelenko.lab.varis.dagger.component;

import android.app.Activity;

import com.khmelenko.lab.varis.activity.BuildDetailsActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = BuildDetailsActivitySubcomponent.class)
abstract class BuildDetailsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(BuildDetailsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindBuildDetailsActivityInjectorFactory(BuildDetailsActivitySubcomponent.Builder builder);
}