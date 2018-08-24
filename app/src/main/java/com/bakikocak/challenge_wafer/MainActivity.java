package com.bakikocak.challenge_wafer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.CountriesAdapter;
import model.Country;
import utils.HttpHandler;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static String url = "https://restcountries.eu/rest/v2/all";
    private ProgressDialog pDialog;
    private ListView listView;
    ArrayList<Country> countriesList = new ArrayList<Country>();
    private CountriesAdapter countriesAdapter;

    //Swiping variables
    private boolean isSwiping = false; // detects if user is swiping on ACTION_UP
    private static final float FAST_SWIPE_VELOCITY = 10000; // needed for velocity implementation
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.lv_main);
        new GetCountries().execute();
    }

    /**
     * AsyncTask class to get JSON from the endpoint.
     */

    private class GetCountries extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching countries...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();
            String jsonString = httpHandler.initializeServiceCall(url);

            Log.e(TAG, "Response: " + jsonString);

            if (jsonString != null) {
                String countryName;
                String language = "";
                String currency = "";

                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        countryName = jsonObject.getString("name");

                        JSONArray languages = jsonObject.getJSONArray("languages");
                        if (languages.length() > 0) {
                            language = languages.getJSONObject(0).getString("name");
                        }

                        JSONArray currencies = jsonObject.getJSONArray("currencies");
                        if (currencies.length() > 0) {
                            currency = currencies.getJSONObject(0).getString("name");
                        }

                        countriesList.add(new Country(countryName, language, currency));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e(TAG, "Couldn't fetch JSON from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't fetch JSON from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             *  Updating listView with arrayList.
             */
            // Note that we have to register touchListener for a single row of the listView. Not the whole listView itself.
            countriesAdapter = new CountriesAdapter(MainActivity.this, countriesList, mTouchListener);
            listView.setAdapter(countriesAdapter);
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        float downX;  // event.getX() value when user starts to swipe.
        float deltaX;
        private int swipeSlop = -1;
        private VelocityTracker mVelocityTracker = null;
        private View interactedView;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            int index = event.getActionIndex();
            int pointerId = event.getPointerId(index); // pointerId to track swipe velocity
            float anchorPoint = v.getWidth() / 4;      // anchor point decided as width/4 for simplicity

            if (swipeSlop < 0) {
                //Distance in pixels a touch can wander
                swipeSlop = ViewConfiguration.get(MainActivity.this).getScaledTouchSlop();
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    if (interactedView != null && interactedView != v) {
                        // There is a interacted row already, we will release it before
                        swipeBack(interactedView, 0);
                    }
                    interactedView = v;

                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain(); // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    } else {
                        mVelocityTracker.clear(); // Reset the velocity tracker back to its initial state.
                    }
                    mVelocityTracker.addMovement(event); // Add a user's movement to the tracker.
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setTranslationX(0);
                    mVelocityTracker.clear(); // Clear VelocityTracker object back to be re-used by others.
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX() + v.getTranslationX();
                    deltaX = x - downX;
                    float deltaXAbs = Math.abs(deltaX);

                    // check whether user is swiping or just touching in sloppy manner
                    if (!isSwiping) {
                        if (deltaXAbs > swipeSlop) {
                            isSwiping = true;
                            listView.requestDisallowInterceptTouchEvent(true);
                        }
                    } else if (isSwiping) {
                        //Note that swiping to the right is disabled that means deltaX won't be greater than zero.
                        if (deltaX < 0) v.setTranslationX(deltaX); // moves the view while the user is swiping to left;

                        // deltaX gets negative value while swiping to the left.
                        if (deltaX < -1 * anchorPoint) { // we are interested in swipingVelocity once it hits to anchorPoint while swiping to the left.
                            mVelocityTracker.addMovement(event);
                            mVelocityTracker.computeCurrentVelocity(1000);
                            float swipingVelocity = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                            // delete interacted row if it is swiped fast.
                            if (swipingVelocity < -1 * FAST_SWIPE_VELOCITY) {
                                deleteRow(v);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // Check if user was swiping when the view was released. If not, it is detected as a click action.
                    if (isSwiping) {
                        if (deltaX < -1 * anchorPoint) {
                            swipeBack(v, -1 * anchorPoint);
                        } else {
                            swipeBack(v, 0);
                        }
                    } else {
                        listView.setEnabled(true);
                        int i = listView.getPositionForView(v);
                        Toast.makeText(MainActivity.this, countriesList.get(i).getName() + " selected!", Toast.LENGTH_LONG).show();
                        return false;
                    }

                default:
                    return false;
            }
            return true;
        }
    };

    private void swipeBack(final View v, float pointToSwipeBack) {
        v.animate().setDuration(300).translationX(pointToSwipeBack).withEndAction(new Runnable() {
            @Override
            public void run() {
                isSwiping = false;
                listView.setEnabled(true);
            }
        });
    }

    public void deleteRow(final View v) {
        v.setEnabled(false); //disable the view to run the animation
        v.animate().setDuration(300).translationX(-v.getWidth()).withEndAction(new Runnable() {
            @Override
            public void run() {
                isSwiping = false;
                updateListViewAfterRemove(listView, v);
            }
        });
    }


    private void updateListViewAfterRemove(final ListView listView, View viewToRemove) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        final ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        for (int i = 0; i < listView.getChildCount(); ++i) {
            View child = listView.getChildAt(i);
            if (child != viewToRemove) {
                int position = firstVisiblePosition + i;
                long itemId = listView.getAdapter().getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        adapter.remove(adapter.getItem(listView.getPositionForView(viewToRemove)));
    }

}
