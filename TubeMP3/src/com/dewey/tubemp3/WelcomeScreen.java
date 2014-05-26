package com.dewey.tubemp3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.DownloadManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeScreen extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.welcome_screen, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            
        	// Grab the rootview
        	View rootView = inflater.inflate(R.layout.fragment_welcome_screen, container, false);
            
        	// Get intent, action and MIME type
            Intent intent = getActivity().getIntent();
            String action = intent.getAction();
            String type = intent.getType();
        	
            /* No need to number the fragments as they appear */
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            
            // Set up the mp3 name paste text box
            final EditText namePaste = (EditText) rootView.findViewById(R.id.editText2);
            
            // Set up the link paste text box
            final EditText urlPaste = (EditText) rootView.findViewById(R.id.editText1);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
            	if ("text/plain".equals(type)) {
            		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            		String finalUrl = sharedText.substring(sharedText.indexOf("http"));
            	    if (sharedText != null) {
            	        urlPaste.setText(finalUrl);
            	        namePaste.setText(sharedText.substring(0, sharedText.indexOf("http")));
            	    }
                }
            }
             
            // Set up the download button
            final Button button = (Button) rootView.findViewById(R.id.button1);
            button.setOnClickListener(new View.OnClickListener() {		
    			@Override
    			public void onClick(View v) {
    				Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_SHORT).show();
					performDownload(namePaste.getText().toString(), urlPaste.getText().toString());
    			}
    		});
            return rootView;
        }
        
        /* Actually performs the download using an external API
         * 
         */
        public void performDownload(String mp3Title, String youtubeUrl){
        	String vidCode = youtubeUrl.replace("http://youtu.be/","");
        	String directLink = "http://youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=" + vidCode;
        	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(directLink));
        	request.setDescription(youtubeUrl);
        	request.setTitle("TubeMP3 Downloading...");
        	// in order for this if to run, you must use the android 3.2 to compile your app
        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	    request.allowScanningByMediaScanner();
        	    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        	}
        	request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

        	// get download service and enqueue file
        	DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        	manager.enqueue(request);
        }
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((WelcomeScreen) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
