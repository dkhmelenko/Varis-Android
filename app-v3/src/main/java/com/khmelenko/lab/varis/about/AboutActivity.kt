package com.khmelenko.lab.varis.about

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.util.PackageUtils

/**
 * About screen
 *
 * @author Dmytro Khmelenko
 */
class AboutActivity : BaseActivity() {

    @BindView(R.id.about_version)
    lateinit var version: TextView

    @BindView(R.id.about_github_link)
    lateinit var githubLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        ButterKnife.bind(this)

        initToolbar()

        val appVersion = getString(R.string.about_version, PackageUtils.getAppVersion())
        version.text = appVersion

        val link = Html.fromHtml(getString(R.string.about_github_link))
        githubLink.text = link
        githubLink.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Initializes toolbar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { _ -> onBackPressed() }
    }
}
