package com.example.cossettenavigation;

import android.app.SearchManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.example.cossettenavigation.map.DatabaseHelper;
import com.example.cossettenavigation.map.Map;

import java.util.Timer;
import java.util.TimerTask;


public class SearchActivity extends AppCompatActivity {

    //0);

    private DatabaseHelper dbHelper;
    public static SQLiteDatabase db;

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
        final SearchActivity activity = this;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra(MainActivity.INTENT_KEY_PATH, Map.testPath);
                startActivity(intent);
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

}
