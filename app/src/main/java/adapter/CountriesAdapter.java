package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bakikocak.challenge_wafer.R;

import java.util.ArrayList;

import model.Country;

public class CountriesAdapter extends ArrayAdapter<Country> {

    private Context context;
    private View.OnTouchListener mTouchListener;
    private ArrayList<Country> countries;

    public CountriesAdapter(Context context, ArrayList<Country> countries, View.OnTouchListener listener) {
        super(context, R.layout.list_item, countries);
        this.context = context;
        this.mTouchListener = listener;
        this.countries = countries;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        /*
            // Get the data item for this position
                User user = getItem(position);
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
       }
       // Lookup view for data population
       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
       // Populate the data into the template view using the data object
       tvName.setText(user.name);
       tvHome.setText(user.hometown);
       // Return the completed view to render on screen
       return convertView;

         */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView nameTv = (TextView) convertView.findViewById(R.id.tv_name);
        TextView languageTv = (TextView) convertView.findViewById(R.id.tv_language);
        TextView currencyTv = (TextView) convertView.findViewById(R.id.tv_currency);
        RelativeLayout swipeableRl = (RelativeLayout) convertView.findViewById(R.id.rl_main);
        ImageView bombImage = (ImageView) convertView.findViewById(R.id.iv_bomb);

        nameTv.setText(getItem(position).getName());
        languageTv.setText(context.getString(R.string.language_title) + " " + getItem(position).getLanguage());
        currencyTv.setText(context.getString(R.string.currency_title) + " " + getItem(position).getCurrency());
        swipeableRl.bringToFront();
        swipeableRl.setOnTouchListener(mTouchListener);


        final View finalConvertView = convertView;
        bombImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRow(finalConvertView, position);
            }
        });

        return convertView;
    }

    private void deleteRow(final View v, final int position) {
        v.setEnabled(false); //disable the view to run the animation
        v.animate().setDuration(300).translationX(-v.getWidth()).withEndAction(new Runnable() {
            @Override
            public void run() {
                countries.remove(position);
                notifyDataSetChanged();
            }
        });
    }
}
