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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.list_item, parent, false);

        TextView name = (TextView) v.findViewById(R.id.tv_name);
        TextView language = (TextView) v.findViewById(R.id.tv_language);
        TextView currency = (TextView) v.findViewById(R.id.tv_currency);
        RelativeLayout swipeableRl = (RelativeLayout) v.findViewById(R.id.rl_main);
        ImageView bombImage = (ImageView) v.findViewById(R.id.iv_bomb);

        name.setText(getItem(position).getName());
        language.setText(getItem(position).getLanguage());
        currency.setText(getItem(position).getCurrency());
        swipeableRl.bringToFront();
        swipeableRl.setOnTouchListener(mTouchListener);

        bombImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRow(view, position);
            }
        });

        return v;
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
