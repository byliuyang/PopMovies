package com.example.android.popmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popmovies.Models.Trailer;

import java.util.List;

/**
 * Created by harryliu on 10/27/15.
 */
public class TrailerArrayAdapter extends ArrayAdapter<Trailer> {

    /**
     * Constructor
     *
     * @param context            The current context.
     * @param resource           The resource ID for a layout file containing a layout to use when
     *                           instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects            The objects to represent in the ListView.
     */
    public TrailerArrayAdapter(Context context, int resource, int textViewResourceId, List<Trailer> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = (Trailer)getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailers,parent, false);
        }

        TextView trailerTextView = (TextView)convertView.findViewById(R.id.trailer_text_view);
        trailerTextView.setText(trailer.getName());
        return trailerTextView;
    }
}
