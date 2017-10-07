package com.khmelenko.lab.varis.dagger.component;

import android.app.Activity;

import com.khmelenko.lab.varis.activity.RepoDetailsActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = RepoDetailsActivitySubcomponent.class)
abstract class RepoDetailsActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(RepoDetailsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindRepoDetailsActivityInjectorFactory(RepoDetailsActivitySubcomponent.Builder builder);
}