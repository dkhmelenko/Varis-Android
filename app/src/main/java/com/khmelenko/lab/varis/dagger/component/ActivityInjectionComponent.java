package com.khmelenko.lab.varis.dagger.component;

import com.khmelenko.lab.varis.activity.AuthActivity;
import com.khmelenko.lab.varis.activity.BuildDetailsActivity;
import com.khmelenko.lab.varis.activity.MainActivity;
import com.khmelenko.lab.varis.activity.RepoDetailsActivity;
import com.khmelenko.lab.varis.activity.SearchResultsActivity;
import com.khmelenko.lab.varis.dagger.scope.ActivityScope;

import dagger.Component;

/**
 * Network component
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@ActivityScope
@Component(dependencies = BaseComponent.class)
public interface ActivityInjectionComponent {

    void inject(MainActivity activity);
    void inject(AuthActivity activity);
    void inject(RepoDetailsActivity activity);
    void inject(BuildDetailsActivity activity);
    void inject(SearchResultsActivity activity);

}
