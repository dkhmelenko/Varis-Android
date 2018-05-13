package com.khmelenko.lab.varis.auth;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Module(subcomponents = AuthActivitySubcomponent.class)
public abstract class AuthActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(AuthActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindAuthActivityInjectorFactory(AuthActivitySubcomponent.Builder builder);
}