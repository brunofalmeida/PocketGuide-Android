package com.example.cossettenavigation;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cossettenavigation.beacons.ApplicationBeaconManager;
import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.DatabaseHelper;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.Zone;
import com.example.cossettenavigation.pathfinding.Path;
import com.example.cossettenavigation.pathfinding.Pathfinder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    //0);

    private DatabaseHelper dbHelper;
    public static SQLiteDatabase db;

    private ApplicationBeaconManager beaconManager;


    private SearchActivity activity=this;
    private SearchView searchView;

    private ListView searchSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper=new DatabaseHelper(this);
        db=dbHelper.getReadableDatabase();

        String query;
        Intent searchIntent = getIntent();

        if(Intent.ACTION_SEARCH.equals(searchIntent.getAction()))
        {
            query = searchIntent.getStringExtra(SearchManager.QUERY);
            //create listview of returned results
            //act as if top choice clicked
            //pull down keyboard to view suggestions
            //do nothing if no results
            Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*String[] location = getResources().getStringArray(R.array.locations);
        ArrayList<String> searchResults = new ArrayList<>();
        for (int i=0; i<location.length; i++)
            if (location[i].toLowerCase().contains(query.toLowerCase()))
                searchResults.add(location[i]);*/


        // TODO - remove test navigation (automatic switch to MainActivity)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startMainActivityNavigation(Map.testPath);
            }
        }, 1000000000);


        beaconManager = (ApplicationBeaconManager) getApplication();

        ArrayList<Zone> filteredZones = new ArrayList<>();
        for (Zone zone : Map.zones) {
            filteredZones.add(zone);
        }
        searchSuggestions=(ListView) findViewById(R.id.search_suggestions);
        searchSuggestions.setAdapter(new ZoneArrayAdapter(activity,filteredZones));

        searchSuggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Zone zone = (Zone) parent.getItemAtPosition(position);

                Beacon startBeacon = beaconManager.getNearestBeacon();

                if (startBeacon != null) {
                    double minTravelTime = Double.POSITIVE_INFINITY;
                    Path minPath = null;

                    for (AnchorBeacon testEndBeacon : zone.getAnchorBeacons()) {
                        Path testPath = Pathfinder.getShortestPath(startBeacon, testEndBeacon);

                        if (testPath != null && testPath.getTravelTime() < minTravelTime) {
                            minTravelTime = testPath.getTravelTime();
                            minPath = testPath;
                        }
                    }

                    if (minPath != null) {
                        startMainActivityNavigation(minPath);
                    } else {
                        Toast.makeText(SearchActivity.this, "Path not found", Toast.LENGTH_LONG).show();
                    }
                }

                else {
                    Toast.makeText(SearchActivity.this, "Nearest beacon not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Zone> filteredZones = new ArrayList<>();
                for (Zone zone : Map.zones) {
                    if (zone.getName().toLowerCase().contains(newText.toString().toLowerCase())) {
                        filteredZones.add(zone);
                    }
                }
                searchSuggestions.setAdapter(new ZoneArrayAdapter(activity,filteredZones));
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void startMainActivityNavigation(Path path) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.INTENT_KEY_PATH, path);
        startActivity(intent);
    }




    private class ZoneArrayAdapter extends ArrayAdapter<Zone> {

        public ZoneArrayAdapter(Context context, ArrayList<Zone> zones) {
            super(context, 0, zones);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final Zone zone = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            // Lookup view for data
            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

            // Populate the data into the template view using the data object
            text1.setText(zone.getName());
            text2.setText(zone.getZoneType().lowercaseDescription);

            // Return the completed view to render on screen
            return convertView;
        }

    }

}
