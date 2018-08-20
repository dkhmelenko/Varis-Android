package com.khmelenko.lab.varis

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Rules for RxJava tests
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class RxJavaRules : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return SchedulerStatement(base)
    }

    private class SchedulerStatement internal constructor(private val statement: Statement) : Statement() {

        override fun evaluate() {
            try {
                setSchedulers()
                statement.evaluate()
            } finally {
                resetSchedulers()
            }
        }

        private fun resetSchedulers() {
            RxAndroidPlugins.reset()
            RxJavaPlugins.reset()
        }

        private fun setSchedulers() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { scheduler -> Schedulers.trampoline() }
        }
    }
}
