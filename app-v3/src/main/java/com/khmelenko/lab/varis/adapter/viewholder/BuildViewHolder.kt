package com.khmelenko.lab.varis.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View

import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.widget.BuildView

import butterknife.BindView
import butterknife.ButterKnife

/**
 * View holder for the Build data
 *
 * @author Dmytro Khmelenko
 */
class BuildViewHolder(itemView: View, private val listener: (Int) -> Unit) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    @BindView(R.id.item_build_card_view)
    lateinit var parent: View

    @BindView(R.id.item_build_data)
    lateinit var buildView: BuildView

    init {
        ButterKnife.bind(this, itemView)
        itemView.isClickable = true
        parent.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        listener(layoutPosition)
    }
}
