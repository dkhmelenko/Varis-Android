package com.khmelenko.lab.varis.auth;

import com.khmelenko.lab.varis.auth.AuthActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Subcomponent
public interface AuthActivitySubcomponent extends AndroidInjector<AuthActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<AuthActivity> {}
}