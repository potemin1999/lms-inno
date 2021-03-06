package com.awesprojects.innolib.fragments.home;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.awesprojects.innolib.InnolibApplication;
import com.awesprojects.innolib.R;
import com.awesprojects.innolib.adapters.CheckedOutDocsAdapter;
import com.awesprojects.innolib.fragments.home.abstracts.AbstractProfileFragment;
import com.awesprojects.innolib.managers.DocumentManager;
import com.awesprojects.lmsclient.api.ResponsableContainer;
import com.awesprojects.lmsclient.api.data.documents.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by ilya on 2/4/18.
 */

public class PatronProfileFragment extends AbstractProfileFragment {

    public static final String TAG = "PatronProfileFragment";
    public static Logger log = Logger.getLogger(TAG);

    public static PatronProfileFragment mBindedInstance;

    public static void refreshCheckedoutList(){
            //mBindedInstance.updateCheckedOutDocs();
        mPendingUpdate = true;
    }

    RecyclerView mCheckedOutDocsView;
    CheckedOutDocsAdapter mCheckedOutDocsAdapter;
    SwipeRefreshLayout mCheckedOutSwipeRefreshLayout;
    NestedScrollView mNestedScrollView;
    ArrayList<Document> mCheckedOutDocuments;
    OnDetailsShowListener mDetailsShowListener;
    static boolean mPendingUpdate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckedOutDocuments = new ArrayList<>();
        mNestedScrollView = getContentView().findViewById(R.id.fragment_home_profile_nested_scroll_view);
        mCheckedOutSwipeRefreshLayout = getContentView().findViewById(R.id.fragment_home_profile_list_swiperefresh);
        mCheckedOutDocsView = getContentView().findViewById(R.id.fragment_home_profile_checkedout_recyclerview);
        mCheckedOutDocsAdapter = new CheckedOutDocsAdapter(this);
        mCheckedOutDocsView.setAdapter(mCheckedOutDocsAdapter);
        mCheckedOutDocsView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mCheckedOutSwipeRefreshLayout.setOnRefreshListener(() -> updateCheckedOutDocs());
        if (savedInstanceState==null){
            updateCheckedOutDocs();
        }else{
            //noinspection unchecked
            mCheckedOutDocuments = (ArrayList<Document>)savedInstanceState.getSerializable("CHECKED_OUT_DOCUMENTS");
            onUpdatedCheckedOutDocs();
        }
        mBindedInstance = this;
        mDetailsShowListener = new OnDetailsShowListener(this);
        mCheckedOutDocsAdapter.setOnShowCheckedOutDocumentDetailsListener(mDetailsShowListener);
       // mNestedScrollView.addView(mCheckedOutDocsView);
    }

    public void updateCheckedOutDocs(){
        mCheckedOutSwipeRefreshLayout.setRefreshing(true);
        DocumentManager.getCheckedOutDocuments(InnolibApplication.getAccessToken(), (documents) -> {
            log.finest("checkout docs update completed");
            if (documents instanceof ResponsableContainer) {
                //noinspection unchecked
                onUpdatedCheckedOutDocs( ((ResponsableContainer<Document[]>)documents).get());
            }else{
                //TODO: second case
            }
        });
    }

    public void onUpdatedCheckedOutDocs(Document[] documents){
        updateArrayList(documents);
        onUpdatedCheckedOutDocs();
    }

    public void onUpdatedCheckedOutDocs(){
        mCheckedOutDocsAdapter.setDocuments(mCheckedOutDocuments);
        mCheckedOutDocsAdapter.notifyDataSetChanged();
        mCheckedOutSwipeRefreshLayout.setRefreshing(false);
    }

    public void updateArrayList(Document[] documents) {
        mCheckedOutDocuments.clear();
        Collections.addAll(mCheckedOutDocuments, documents);
    }

    @Override
    public void onDestroy() {
        if (mBindedInstance==this)
            mBindedInstance = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        log.finest("profile on resume");
        if (mPendingUpdate){
            log.finer("pending update completing...");
            updateCheckedOutDocs();
            mPendingUpdate = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("CHECKED_OUT_DOCUMENTS",mCheckedOutDocuments);
        super.onSaveInstanceState(outState);
    }

    public void onDocumentRenew(Document document,boolean renewed){
        if (renewed){
            updateCheckedOutDocs();
        }
    }

    public void onShowDocumentInfo(View documentHolder, Document document) {
        DocumentInfoFragment documentDetailFragment = new DocumentInfoFragment();
        documentDetailFragment.setOnRenewListener(this::onDocumentRenew);
        documentDetailFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        documentDetailFragment.setExitTransition(new Slide(Gravity.BOTTOM));
        Bundle args = new Bundle();
        args.putSerializable("DOCUMENT", document);
        documentDetailFragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .addToBackStack("DetailsShow")
                .add(getHomeActivity().getHomeInterface().getOverlayContainer().getId(), documentDetailFragment, "DocumentInfoFragment")
                .commit();
    }



    public static class OnDetailsShowListener implements CheckedOutDocsAdapter.OnShowCheckedOutDocumentDetailsListener{

        private PatronProfileFragment mPatronProfileFragment;

        public OnDetailsShowListener(PatronProfileFragment fragment){
            mPatronProfileFragment = fragment;
        }
        @Override
        public void onShow(View holderRootView, Document document) {
            mPatronProfileFragment.onShowDocumentInfo(holderRootView, document);
        }

    }

}
