package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bakikocak.challenge_wafer.R;

import java.util.ArrayList;

import model.Country;

public class CountriesAdapter extends ArrayAdapter<Country> {

    View.OnTouchListener mTouchListener;

    public CountriesAdapter(Context context, ArrayList<Country> countries, View.OnTouchListener listener)
    {
        super(context, R.layout.list_item, countries);
        mTouchListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View v = inflater.inflate(R.layout.list_item, parent, false);

        TextView b = (TextView) v.findViewById(R.id.list_tv);
        b.setText(getItem(position).getName());

        v.setOnTouchListener(mTouchListener);

        return v;
    }
}
