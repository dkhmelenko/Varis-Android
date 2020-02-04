package com.khmelenko.lab.varis.about

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.text.Html
import android.text.method.LinkMovementMethod
import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.activity.BaseActivity
import com.khmelenko.lab.varis.util.PackageUtils
import kotlinx.android.synthetic.main.activity_about.aboutGithubLink
import kotlinx.android.synthetic.main.activity_about.aboutVersion

/**
 * About screen
 *
 * @author Dmytro Khmelenko
 */
class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initToolbar()

        val appVersion = getString(R.string.about_version, PackageUtils.appVersion)
        aboutVersion.text = appVersion

        val link = Html.fromHtml(getString(R.string.about_github_link))
        aboutGithubLink.text = link
        aboutGithubLink.movementMethod = LinkMovementMethod.getInstance()
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
