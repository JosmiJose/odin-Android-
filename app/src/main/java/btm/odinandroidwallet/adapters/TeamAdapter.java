package btm.odinandroidwallet.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.TeamDetail;
import btm.odinandroidwallet.objects.TeamMember;
import btm.odinandroidwallet.ui.ico.MyDialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;


public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyView> {
    private List<TeamDetail> mTeamMembers;
    Context context;


    public TeamAdapter(List<TeamDetail> members,Context ctx) {
        this.mTeamMembers = members;
        this.context=ctx;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        TeamDetail item = mTeamMembers.get(position);
        holder.name.setText(item.employeeName);
        Picasso.with(context)
                .load(item.picture)
                .resize(128, 128)
                .centerCrop()
                .into(holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDialog(position);
            }
        });

    }
    void showDialog(int position) {

        FragmentTransaction ft = ((FragmentActivity)context).getFragmentManager().beginTransaction();
        Fragment prev = ((FragmentActivity)context).getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        MyDialogFragment newFragment = MyDialogFragment.newInstance(mTeamMembers.get(position));
        newFragment.show(ft, "dialog");
    }
    @Override
    public int getItemCount() {
        return mTeamMembers.size();
    }


    public class MyView extends RecyclerView.ViewHolder {

        public TextView name;
        public CircleImageView itemView;

        public MyView(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name);
            itemView= view.findViewById(R.id.circle_image_profile_main);


        }
    }
}