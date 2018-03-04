package com.example.ireos.testemail.sent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ireos.testemail.Injection;
import com.example.ireos.testemail.R;
import com.example.ireos.testemail.util.UtilsActivity;

/**
 * Created by tav on 01/03/2018.
 */

public class MailActivity extends AppCompatActivity{

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    public static final String EXTRA_MAIL_ID = "MAIL_ID";

    private DrawerLayout mDrawerLayout;

    private MailPresenter mMailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent);

        //Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the requested mail id
        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);

        // Set up the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null){
            setupDrawerContent(navigationView);
        }

        MailFragment mailFragment =
                (MailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mailFragment == null){
            // Create the Fragment
            mailFragment = MailFragment.newInstance(mailId);

            UtilsActivity.sentFragmentToActivity(
                    getSupportFragmentManager(), mailFragment, R.id.contentFrame);
        }

        // Create the presenter
        mMailPresenter = new MailPresenter(
                Injection.provideUseCaseHandler(),
                mailId,
                mailFragment,
                Injection.provideGetMails(getApplicationContext()),
                Injection.provideReadMail(getApplicationContext()),
                Injection.provideUnreadMail(getApplicationContext()),
                Injection.provideDeleteMail(getApplicationContext()),
                Injection.provideDeleteActiveMail(getApplicationContext()));

        // Load previously saved state, if available.
        if (savedInstanceState != null){
            MailFilterType currentFiltering =
                    (MailFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mMailPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mMailPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.list_navigation_menu_item:
                                // Do nothing, we're already on that screen
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
