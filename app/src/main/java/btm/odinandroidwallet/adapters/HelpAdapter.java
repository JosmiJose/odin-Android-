package btm.odinandroidwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import btm.odinandroidwallet.R;


public class HelpAdapter extends ArrayAdapter<String> {

    private ArrayList<String> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        TextView catText;
    }

    public HelpAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.help_item_row, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LayoutInflater inflater = LayoutInflater.from(getContext());
        String cat = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();


            convertView = inflater.inflate(R.layout.help_item_row, parent, false);
            viewHolder.catText = (TextView) convertView.findViewById(R.id.cat_text);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        viewHolder.catText.setText(cat);


        // Return the completed view to render on screen
        return convertView;
    }
}