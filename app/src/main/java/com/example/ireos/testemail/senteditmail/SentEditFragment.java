package com.example.ireos.testemail.senteditmail;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ireos.testemail.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 28/02/2018.
 */

public class SentEditFragment extends Fragment implements SentEditContract.View{

    public static final String ARGUMENT_EDIT_MAIL_ID = "EDIT_MAIL_ID";

    private SentEditContract.Presenter mPresenter;

    private TextView mTitle;

    private TextView mSubject;

    private TextView mDescription;

    public static SentEditFragment newInstance(){
        return new SentEditFragment();
    }

    public SentEditFragment(){
        //Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull SentEditContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_mail_done);
        fab.setImageResource(R.drawable.ic_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.sentEmail(mTitle.getText().toString(),mSubject.getText().toString(),
                        mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sent_edit, container, false);
        mTitle = (TextView) root.findViewById(R.id.add_mail_to);
        mSubject = (TextView) root.findViewById(R.id.add_mail_subject);
        mDescription = (TextView) root.findViewById(R.id.add_mail_desc);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void showEmptyMailError() {
        Snackbar.make(mTitle, getString(R.string.empty_mail_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMailList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setSubject(String subject) {
        mSubject.setText(subject);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
