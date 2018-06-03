package com.khmelenko.lab.varis.about

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.webkit.WebView

import com.khmelenko.lab.varis.R

/**
 * Dialog with licenses
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class LicensesDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_licenses, null) as WebView
        view.loadUrl("file:///android_asset/opensource_licenses.html")
        return AlertDialog.Builder(activity!!)
                .setTitle(getString(R.string.dialog_licenses_title))
                .setView(view, 0, 25, 0, 0)
                .create()
    }

    companion object {

        @JvmStatic
        fun newInstance(): LicensesDialogFragment {
            return LicensesDialogFragment()
        }
    }
}
