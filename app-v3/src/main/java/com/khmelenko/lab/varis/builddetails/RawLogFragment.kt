package com.khmelenko.lab.varis.builddetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient

import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.log.LogEntryComponent

import kotlinx.android.synthetic.main.fragment_raw_log.progressbar
import kotlinx.android.synthetic.main.fragment_raw_log.raw_log_webview
import kotlinx.android.synthetic.main.view_empty.empty_text

/**
 * Shows raw logs
 *
 * @author Dmytro Khmelenko
 */
class RawLogFragment : Fragment() {

    private var listener: OnRawLogFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_raw_log, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgress(true)
        showError(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnRawLogFragmentListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnRawLogFragmentListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * Shows the loading progress of the fragment
     *
     * @param inProgress True, if the progress should be shown. False otherwise
     */
    fun showProgress(inProgress: Boolean) {
        if (inProgress) {
            progressbar.visibility = View.VISIBLE
            raw_log_webview.visibility = View.GONE
        } else {
            progressbar.visibility = View.GONE
            raw_log_webview.visibility = View.VISIBLE
        }
    }

    /**
     * Shows the error message
     *
     * @param isError True, if error text should be shown. False otherwise
     */
    fun showError(isError: Boolean) {
        if (isError) {
            empty_text.visibility = View.VISIBLE
            empty_text.setText(R.string.build_details_empty)
        } else {
            empty_text.visibility = View.GONE
        }
    }

    /**
     * Shows the log in the web view
     *
     * @param log Parsed log data
     */
    fun showLog(log: LogEntryComponent) {
        showError(false)

        raw_log_webview.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                listener?.onLogLoaded()

                showProgress(false)
            }
        }

        raw_log_webview.loadData(log.toHtml(), "text/html", "utf-8")
    }

    /**
     * Interface for communication with this fragment
     */
    interface OnRawLogFragmentListener {

        /**
         * Raised when raw log loaded
         */
        fun onLogLoaded()
    }

    companion object {

        /**
         * Creates an instance of the fragment
         *
         * @return Fragment instance
         */
        fun newInstance(): RawLogFragment {
            return RawLogFragment()
        }
    }
}
