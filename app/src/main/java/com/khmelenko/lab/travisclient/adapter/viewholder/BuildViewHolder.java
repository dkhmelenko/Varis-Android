package com.khmelenko.lab.travisclient.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.adapter.OnListItemListener;
import com.khmelenko.lab.travisclient.widget.BuildView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Viewholder class
 */
public class BuildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Bind(R.id.item_build_card_view)
    View mParent;

    @Bind(R.id.item_build_data)
    public BuildView mBuildView;

    private final OnListItemListener mListener;

    public BuildViewHolder(View itemView, OnListItemListener mListener) {
        super(itemView);
        this.mListener = mListener;
        ButterKnife.bind(this, itemView);
        itemView.setClickable(true);
        mParent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            mListener.onItemSelected(getLayoutPosition());
        }
    }
}
