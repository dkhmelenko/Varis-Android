package com.khmelenko.lab.varis.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.util.PackageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * About screen
 *
 * @author Dmytro Khmelenko
 */
public final class AboutActivity extends BaseActivity {

    @BindView(R.id.about_version)
    TextView mVersion;

    @BindView(R.id.about_github_link)
    TextView mGithubLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initToolbar();

        String appVersion = getString(R.string.about_version, PackageUtils.getAppVersion());
        mVersion.setText(appVersion);

        Spanned link = Html.fromHtml(getString(R.string.about_github_link));
        mGithubLink.setText(link);
        mGithubLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }
}
