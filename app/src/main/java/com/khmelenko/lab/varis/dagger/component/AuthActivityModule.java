package com.khmelenko.lab.varis.dagger.component;

import android.app.Activity;

import com.khmelenko.lab.varis.activity.AuthActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = AuthActivitySubcomponent.class)
abstract class AuthActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(AuthActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindAuthActivityInjectorFactory(AuthActivitySubcomponent.Builder builder);
}