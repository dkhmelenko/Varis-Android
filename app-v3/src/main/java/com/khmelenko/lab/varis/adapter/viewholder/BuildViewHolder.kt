package com.khmelenko.lab.varis.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.khmelenko.lab.varis.widget.BuildView
import kotlinx.android.synthetic.main.item_build_view.view.item_build_card_view
import kotlinx.android.synthetic.main.item_build_view.view.item_build_data

/**
 * View holder for the Build data
 *
 * @author Dmytro Khmelenko
 */
class BuildViewHolder(itemView: View, private val listener: (Int) -> Unit) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val parent: View

    val buildView: BuildView

    init {
        itemView.isClickable = true
        parent = itemView.item_build_card_view
        parent.setOnClickListener(this)

        buildView = itemView.item_build_data
    }

    override fun onClick(view: View) {
        listener(layoutPosition)
    }
}
