package btm.odinandroidwallet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import btm.odinandroidwallet.ui.listener.ClickListener;


public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final View mView;
    private ClickListener clickListener;

    public ViewHolder(View v) {
        super(v);
        mView = v;
        v.setOnClickListener(this);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        clickListener.onClick(v, getAdapterPosition(), false);
    }
}
