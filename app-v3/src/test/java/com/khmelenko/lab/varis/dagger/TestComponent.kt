package com.khmelenko.lab.varis.dagger

import com.khmelenko.lab.varis.dagger.module.ApplicationModule
import com.khmelenko.lab.varis.viewmodel.*
import com.khmelenko.lab.varis.viewmodel.TestSearchResultsViewModel

import javax.inject.Singleton

import dagger.Component

/**
 * Component for providing test modules
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
@Singleton
@Component(modules = [(TestNetworkModule::class), (TestStorageModule::class), (ApplicationModule::class)])
interface TestComponent {

    fun inject(test: TestSearchResultsViewModel)

    fun inject(test: TestRepositoriesViewModel)

    fun inject(test: TestRepoDetailsPresenter)

    fun inject(test: TestBuildDetailsPresenter)

    fun inject(test: TestAuthPresenter)
}
