package com.example.ireos.testemail.sent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ireos.testemail.R;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.senteditmail.SentEditActivity;
import com.example.ireos.testemail.senteditmail.SentEditFragment;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class MailFragment extends Fragment implements MailContract.View{

    @NonNull
    private static final String ARGUMENT_MAIL_ID = "MAIL_ID";

    @NonNull
    private static final int REQUEST_EDIT_MAIL = 1;

    private MailContract.Presenter mPresenter;

    private MailAdapter mListAdapter;

    private View mNoMailView;

    private ImageView mNoMailIcon;

    private TextView mNoMailMainView;

    private TextView mNoMailAddView;

    private LinearLayout mMailView;

    private TextView mFilteringLabelView;

    public MailFragment(){
        //Requires empty public constructor
    }

    public static MailFragment newInstance(@Nullable String mailId){
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_MAIL_ID, mailId);
        MailFragment fragment = new MailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new MailAdapter(new ArrayList<Mail>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sent, container, false);

        //Set up mail view
        ListView listView = (ListView) root.findViewById(R.id.mail_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = (TextView) root.findViewById(R.id.filtering_label);
        mMailView = (LinearLayout) root.findViewById(R.id.mail_linear_layout);

        //Set up no mail view
        mNoMailView = root.findViewById(R.id.no_mail);
        mNoMailIcon = (ImageView) root.findViewById(R.id.no_mail_icon);
        mNoMailMainView = (TextView) root.findViewById(R.id.no_mail_main);
        mNoMailAddView = (TextView) root.findViewById(R.id.no_mail_add);
        mNoMailAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeMail();           }
        });

        //Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_compose_mail);
        fab.setImageResource(R.drawable.ic_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewMail();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        //Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadMail(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(MailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
        /*if (requestCode == REQUEST_EDIT_MAIL){
            // If the task was edited sucessfully, go back to the list.
            if (resultCode == Activity.RESULT_OK){
                getActivity().finish();
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadMail(true);
                break;
            case R.id.menu_delete:
                showAlertDeleteAll();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_mail, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.active:
                        mPresenter.setFiltering(MailFilterType.ACTIVE_MAIL);
                        break;
                    case R.id.favorite:
                        mPresenter.setFiltering(MailFilterType.FAVORITE_MAIL);
                        break;
                    default:
                        mPresenter.setFiltering(MailFilterType.ALL_MAIL);
                        break;
                }
                mPresenter.loadMail(false);
                return true;
            }
        });
        popup.show();
    }

    MailItemListener mItemListener = new MailItemListener() {
        @Override
        public void onMailClick(Mail clickedMail) {
            mPresenter.openEditMail(clickedMail);
        }

        @Override
        public void onLongMailClick(Mail clickedMail) {
            showAlertDelete(clickedMail);
        }

        @Override
        public void onFavoriteMailClick(Mail favoriteMail) {
            mPresenter.favoriteMail(favoriteMail);
        }

        @Override
        public void onActiveMailClick(Mail activeMail) {
            mPresenter.activeMail(activeMail);
        }
    };

    @Override
    public void setLoadingIndicatior(final boolean active) {
        if (getView() == null){
            return;
        }

        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        //Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showMail(List<Mail> mail) {
        mListAdapter.replaceData(mail);

        mMailView.setVisibility(View.VISIBLE);
        mNoMailView.setVisibility(View.GONE);
    }

    @Override
    public void showNoActiveMail() {
        showNoMailViews(
                getResources().getString(R.string.no_mail_all),
                R.drawable.ic_mail_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoMail() {
        showNoMailViews(
                getResources().getString(R.string.no_mail_all),
                R.drawable.ic_mail_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoFavoriteMail() {
        showNoMailViews(
                getResources().getString(R.string.no_mail_favorite),
                R.drawable.ic_verified_user,
                false
        );
    }

    @Override
    public void showSuccessfullySentMail() {
        showAlertSent();
    }

    private void showNoMailViews(String mainText, int iconRes, boolean showAddView){
        mMailView.setVisibility(View.GONE);
        mNoMailView.setVisibility(View.VISIBLE);

        mNoMailMainView.setText(mainText);
        mNoMailIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoMailAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showFavoriteFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_favorite));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showComposeMail() {
        Intent intent = new Intent(getContext(), SentEditActivity.class);
        startActivityForResult(intent, SentEditActivity.REQUEST_ADD_MAIL);
    }

    @Override
    public void showEditMail(String mailId) {
        Intent intent = new Intent(getContext(), SentEditActivity.class);
        intent.putExtra(SentEditFragment.ARGUMENT_EDIT_MAIL_ID, mailId);
        startActivityForResult(intent, REQUEST_EDIT_MAIL);
    }

    @Override
    public void showMailDeleted() {
        showMessage(getString(R.string.delete_mail));
    }

    @Override
    public void showMailMarkedFavorite() {
        showMessage(getString(R.string.mail_marked_favorite));
    }

    @Override
    public void showMailMarkedActive() {
        showMessage(getString(R.string.mail_marked_active));
    }

    @Override
    public void showActiveMailCleared() {
        showMessage(getString(R.string.delete_all_mail));
    }

    @Override
    public void showAlertSent() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.sent_mail));
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showAlertDelete(final Mail clickedMail) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.alert_message_delete));
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.deleteMail(clickedMail);
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.alert_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showAlertDeleteAll() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.alert_message_delete_all));
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.deleteActiveMail();
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.alert_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showLoadingMailError() {
        showMessage(getString(R.string.loading_mail_error));
    }

    private void showMessage(String msg){
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isFavorite() {
        return isAdded();
    }

    private static class MailAdapter extends BaseAdapter {

        private List<Mail> mMail;

        private MailItemListener mItemListener;

        public MailAdapter(List<Mail> mail, MailItemListener itemListener) {
            setList(mail);
            mItemListener = itemListener;
        }

        public void replaceData(List<Mail> mail) {
            setList(mail);
            notifyDataSetChanged();
        }

        private void setList(List<Mail> mail) {
            mMail = checkNotNull(mail);
        }

        @Override
        public int getCount() {
            return mMail.size();
        }

        @Override
        public Mail getItem(int i) {
            return mMail.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.item_mail, viewGroup, false);
            }

            final Mail mail = getItem(i);

            TextView subjectTV = (TextView) rowView.findViewById(R.id.title);
            TextView toTV = (TextView) rowView.findViewById(R.id.subject);
            subjectTV.setText(mail.getSubjectForList());
            toTV.setText(mail.getTitle());

            CheckBox favoriteCB = (CheckBox) rowView.findViewById(R.id.status_mail);

            // Active/completed mail UI
            favoriteCB.setChecked(mail.isFavorite());
            if (mail.isFavorite()) {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                rowView.setBackgroundDrawable(viewGroup.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }

            favoriteCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mail.isFavorite()) {
                        mItemListener.onFavoriteMailClick(mail);
                    } else {
                        mItemListener.onActiveMailClick(mail);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onMailClick(mail);
                }
            });

            rowView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mItemListener.onLongMailClick(mail);
                    return true;
                }
            });

            return rowView;
        }
    }

    public interface MailItemListener {
        void onMailClick(Mail clickedMail);

        void onLongMailClick(Mail clickedMail);

        void onFavoriteMailClick(Mail favoriteMail);

        void onActiveMailClick(Mail activeMail);
    }
}
