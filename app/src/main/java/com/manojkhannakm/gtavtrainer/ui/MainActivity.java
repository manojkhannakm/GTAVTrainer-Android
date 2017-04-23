package com.manojkhannakm.gtavtrainer.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.manojkhannakm.gtavtrainer.R;
import com.manojkhannakm.gtavtrainer.net.client.Client;
import com.manojkhannakm.gtavtrainer.net.client.ClientAsyncTask;
import com.manojkhannakm.gtavtrainer.ui.about.AboutFragment;
import com.manojkhannakm.gtavtrainer.ui.help.HelpFragment;
import com.manojkhannakm.gtavtrainer.ui.trainer.ConnectionFragment;

/**
 * @author Manoj Khanna
 */

public class MainActivity extends AppCompatActivity {

    private static final String TITLE_STATE = "title";
    private static final String CLIENT_STATE = "client";

    private ActionBarDrawerToggle mDrawerToggle;
    private Client mClient;
    private ClientAsyncTask mClientAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //noinspection ConstantConditions
                getSupportActionBar().setTitle(item.getTitle());

                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.trainer_navigation_view_main:
                        fragment = ConnectionFragment.newInstance();
                        break;

                    case R.id.help_navigation_view_main:
                        fragment = HelpFragment.newInstance();
                        break;

                    case R.id.about_navigation_view_main:
                        fragment = AboutFragment.newInstance();
                        break;
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_layout_main, fragment)
                        .commit();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer_layout_main, R.string.close_drawer_layout_main);
        drawerLayout.addDrawerListener(mDrawerToggle);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    tabLayout.setupWithViewPager(null);
                    tabLayout.setVisibility(View.GONE);

                    mClientAsyncTask = (ClientAsyncTask) new DisconnectAsyncTask().execute();
                }
            }

        });

        if (savedInstanceState == null) {
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(R.string.trainer_navigation_view_main);

            fragmentManager.beginTransaction()
                    .replace(R.id.container_layout_main, ConnectionFragment.newInstance())
                    .commit();
        } else {
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(savedInstanceState.getString(TITLE_STATE));

            mClient = savedInstanceState.getParcelable(CLIENT_STATE);
            if (mClient != null) {
                mClientAsyncTask = (ClientAsyncTask) new ReconnectAsyncTask().execute();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //noinspection ConstantConditions
        outState.putString(TITLE_STATE, getSupportActionBar().getTitle().toString());
        outState.putParcelable(CLIENT_STATE, mClient);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mClientAsyncTask != null) {
            mClientAsyncTask.cancel(true);
        }
    }

    public Client getClient() {
        return mClient;
    }

    public void setClient(Client client) {
        mClient = client;
    }

    private class ReconnectAsyncTask extends ClientAsyncTask<Void, Void, Boolean> {

        public ReconnectAsyncTask() {
            super(MainActivity.this, getString(R.string.connection_progress_connection), getString(R.string.reconnecting_progress_connection));
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return mClient.connect();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Snackbar.make(findViewById(R.id.coordinator_layout_main),
                        R.string.connected_progress_connection, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(R.id.coordinator_layout_main),
                        R.string.could_not_connect_progress_connection, Snackbar.LENGTH_LONG).show();

                getSupportFragmentManager().popBackStack();
            }
        }

    }

    private class DisconnectAsyncTask extends ClientAsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mClient.disconnect();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            setClient(null);
        }

    }

}
