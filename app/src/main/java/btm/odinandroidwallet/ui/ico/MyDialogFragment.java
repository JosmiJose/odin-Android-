package btm.odinandroidwallet.ui.ico;

import android.app.DialogFragment;
import android.os.Bundle;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.networking.retrofit.pojo.TeamDetail;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyDialogFragment extends DialogFragment {
    int mNum;
     TeamDetail detail;
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static MyDialogFragment newInstance(TeamDetail detail)
    {
        MyDialogFragment f = new MyDialogFragment();
        f.detail=detail;

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_team_details, container, false);
        TextView name=v.findViewById(R.id.name);
        CircleImageView itemView=v.findViewById(R.id.circle_image_profile_main);
        name.setText(detail.employeeName);
        TextView about=v.findViewById(R.id.about);
        TextView linkedin=v.findViewById(R.id.linked_in);
        about.setText(detail.about);
        linkedin.setText(detail.linkedinProfile);
        linkedin.setMovementMethod(LinkMovementMethod.getInstance());

        Picasso.with(getActivity())
                .load(detail.picture)
                .resize(128, 128)
                .centerCrop()
                .into(itemView);

        return v;
    }
}