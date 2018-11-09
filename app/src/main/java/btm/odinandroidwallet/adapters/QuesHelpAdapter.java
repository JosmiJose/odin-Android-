package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import btm.odinandroidwallet.R;
import btm.odinandroidwallet.objects.questionEntity;


public class QuesHelpAdapter extends ArrayAdapter<questionEntity> {

    private ArrayList<questionEntity> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        TextView ques;
        TextView answer;
        LinearLayout mask;
        ImageView fab;
    }

    public QuesHelpAdapter(ArrayList<questionEntity> data, Context context) {
        super(context, R.layout.help_ques_row, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        if(position==0)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
    @Override
    public int getCount() {
        int count = super.getCount()+1;
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LayoutInflater inflater = LayoutInflater.from(getContext());
        questionEntity ques = null;
        int type=getItemViewType(position);
        if(type==1) {
             ques = getItem(position-1);
        }

        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            if(type==0)
            {
                convertView = inflater.inflate(R.layout.help_ques_header, parent, false);
            }
            else{
                convertView = inflater.inflate(R.layout.help_ques_row, parent, false);
            }
            viewHolder.ques = (TextView) convertView.findViewById(R.id.question);
            viewHolder.answer = (TextView) convertView.findViewById(R.id.answer);
            viewHolder.mask = (LinearLayout) convertView.findViewById(R.id.mask);
            viewHolder.fab = (ImageView) convertView.findViewById(R.id.fab);



            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(type==1) {
            viewHolder.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.mask.getVisibility() == View.GONE) {
                        viewHolder.mask.setVisibility(View.VISIBLE);
                        viewHolder.answer.setVisibility(View.VISIBLE);
                        viewHolder.fab.setImageResource(R.drawable.collapse);
                    } else {
                        viewHolder.mask.setVisibility(View.GONE);
                        viewHolder.answer.setVisibility(View.GONE);
                        viewHolder.fab.setImageResource(R.drawable.expand);
                    }

                }
            });
            viewHolder.ques.setText(ques.question);
            viewHolder.answer.setText(ques.answer);

        }

        // Return the completed view to render on screen
        return convertView;
    }
}