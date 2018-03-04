package com.example.ireos.testemail.senteditmail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ireos.testemail.Injection;
import com.example.ireos.testemail.R;
import com.example.ireos.testemail.util.UtilsActivity;

/**
 * Created by tav on 28/02/2018.
 */

public class SentEditActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_MAIL = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    private SentEditPresenter mSentEditPresenter;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_edit);

        //Set up the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        SentEditFragment sentEditFragment = (SentEditFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        String mailId = getIntent().getStringExtra(SentEditFragment.ARGUMENT_EDIT_MAIL_ID);

        setToolbarTitle(mailId);

        if (sentEditFragment == null){
            sentEditFragment = SentEditFragment.newInstance();

            if (getIntent().hasExtra(SentEditFragment.ARGUMENT_EDIT_MAIL_ID)){
                Bundle bundle = new Bundle();
                bundle.putString(SentEditFragment.ARGUMENT_EDIT_MAIL_ID, mailId);
                sentEditFragment.setArguments(bundle);
            }

            UtilsActivity.sentFragmentToActivity(getSupportFragmentManager(),
                    sentEditFragment, R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;

        //Prevent the Presenter from loading data from the repository if this config change.
        if (savedInstanceState != null){
            //Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

        //Create Presenter
        mSentEditPresenter = new SentEditPresenter(Injection.provideUseCaseHandler(),
                mailId,
                sentEditFragment,
                Injection.provideGetMail(getApplicationContext()),
                Injection.provideSentMail(getApplicationContext()),
                shouldLoadDataFromRepo);
    }

    private void setToolbarTitle(@Nullable String mailId){
        if (mailId == null){
            mActionBar.setTitle("Compose Email");
        } else{
            mActionBar.setTitle("Email");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mSentEditPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
