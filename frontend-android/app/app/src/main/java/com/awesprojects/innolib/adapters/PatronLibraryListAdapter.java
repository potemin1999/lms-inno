package com.awesprojects.innolib.adapters;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesprojects.innolib.R;
import com.awesprojects.innolib.fragments.home.PatronLibraryFragment;
import com.awesprojects.innolib.managers.DocumentManager;
import com.awesprojects.lmsclient.api.data.documents.Article;
import com.awesprojects.lmsclient.api.data.documents.Book;
import com.awesprojects.lmsclient.api.data.documents.Document;
import com.awesprojects.lmsclient.api.data.documents.EMaterial;

import java.util.ArrayList;

/**
 * Created by Ilya on 3/23/2018.
 */

public class PatronLibraryListAdapter extends RecyclerView.Adapter<PatronLibraryListAdapter.PatronLibraryListItemHolder> {

    public interface OnShowDocumentDetailsListener {
        void onShow(View holderRootView, Document document);
    }

    PatronLibraryFragment mPatronLibraryFragment;

    ArrayList<Document> mCurrentDocuments;
    ArrayList<Document> mDocuments;
    ArrayList<Document> mBookDocuments;
    ArrayList<Document> mArticleDocuments;
    ArrayList<Document> mAVDocuments;
    String mCheckoutDisabledReference;
    String mCheckoutDisabledMagazine;
    View.OnClickListener mOnHolderClickListener;
    OnShowDocumentDetailsListener mOnShowDocumentDetailsListener;
    int mCurrentDocumentsType = -1;

    public PatronLibraryListAdapter(PatronLibraryFragment patronLibraryFragment) {
        super();
        mPatronLibraryFragment = patronLibraryFragment;
        mCheckoutDisabledReference = patronLibraryFragment.getString(R.string.home_library_list_document_checkout_unavailable_reference);
        mCheckoutDisabledMagazine = patronLibraryFragment.getString(R.string.home_library_list_document_checkout_unavailable_magazine);
        mOnHolderClickListener = new OnShowDetailsListener(this);
        mBookDocuments = new ArrayList<>();
        mArticleDocuments = new ArrayList<>();
        mAVDocuments = new ArrayList<>();
    }

    public void setDocuments(ArrayList<Document> documents) {
        mDocuments = documents;
        mBookDocuments.clear();
        mArticleDocuments.clear();
        mAVDocuments.clear();
        for ( Document d : documents) {
            switch (d.getType()){
                case 0: { mBookDocuments.add(d); break; }
                case 1: { mArticleDocuments.add(d); break; }
                case 2: { mAVDocuments.add(d); break; }
            }
        }
        setDocumentsType(mCurrentDocumentsType);
    }

    public void setDocumentsType(int type){
        ArrayList<Document> previousDocuments = mCurrentDocuments;
        if (type==-1)
            mCurrentDocuments = mDocuments;
        if (type==0)
            mCurrentDocuments = mBookDocuments;
        if (type==1)
            mCurrentDocuments = mArticleDocuments;
        if (type==2)
            mCurrentDocuments = mAVDocuments;
        notifyDataSetChanged();
    }

    public void setOnShowDocumentDetailsListener(OnShowDocumentDetailsListener listener) {
        mOnShowDocumentDetailsListener = listener;
    }

    @Override
    public PatronLibraryListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mPatronLibraryFragment.getCompatLayoutInflater().inflate(R.layout.home_library_list_element, parent, false);
        return new PatronLibraryListItemHolder(v);
    }




    @Override
    public void onBindViewHolder(PatronLibraryListItemHolder holder, int position) {
        if (mCurrentDocuments == null) return;
        Document doc = mCurrentDocuments.get(position);
        holder.setDocumentType(doc.getType());
        holder.setItemOnClickListener(doc, mOnHolderClickListener);
        //holder.setCheckoutListener(doc.getId(), mOnCheckoutClickListener);
        holder.setTitle(doc.getTitle());
        holder.setAuthors(doc.getAuthors());
       // holder.setStockCount(doc.getInstockCount());
        holder.setKeywords(DocumentManager.preparedKeywords(doc));

        //holder.setCheckoutAvailable(true, null);
        if (doc instanceof Book) {
            holder.setImageResource(R.drawable.ic_library_books_black_36dp);
            holder.setBestseller(((Book) doc).isBestseller());
            // if (((Book) doc).isReference())
            //holder.setCheckoutAvailable(false, mCheckoutDisabledReference.toUpperCase());
            holder.setTopInfo(((Book) doc).getEdition() + " edition");
            holder.setBottomInfo("Published by " + ((Book) doc).getPublisher() + " in " + ((Book) doc).getPublicationYear());
        } else {
            holder.setBestseller(false);
        }
        if (doc instanceof Article) {
            holder.setImageResource(R.drawable.ic_insert_drive_file_black_36dp);
            holder.setTopInfo("Edited by " + ((Article) doc).getJournalIssueEditors());
            holder.setBottomInfo("Published in " + ((Article) doc).getJournalTitle() + " in " + ((Article) doc).getJournalIssuePublicationDate());
        }
        if (doc instanceof EMaterial) {
            holder.setImageResource(R.drawable.ic_video_library_black_36dp);
            holder.setTopInfo(null);
            holder.setBottomInfo(null);
        }
    }



    @Override
    public int getItemCount() {
        if (mCurrentDocuments != null)
            return mCurrentDocuments.size();
        return 0;
    }

    public static class PatronLibraryListItemHolder extends RecyclerView.ViewHolder {

        View mRootView;
        ImageView mDocumentImageView;
        TextView mTitleTextView;
        TextView mAuthorsTextView;
        TextView mStockTextView;
        //TextView mCheckoutUnavailableTextView;
        TextView mDocumentTypeTextView;
        TextView mInfoTopTextView;
        TextView mInfoBottomTextView;
        // Button mCheckoutButton;
        View mBestsellerLayout;
        TextView mKeywordsTextView;


        @SuppressWarnings("deprecated")
        public PatronLibraryListItemHolder(View itemView) {
            super(itemView);

            mRootView = itemView.findViewById(R.id.home_library_list_element_root_view);
            mDocumentImageView = itemView.findViewById(R.id.home_library_list_element_preview_imageview);
            mKeywordsTextView = itemView.findViewById(R.id.home_library_list_element_keywords_textview);
            mBestsellerLayout = itemView.findViewById(R.id.home_library_list_element_bestseller_layout);
            mDocumentTypeTextView = itemView.findViewById(R.id.home_library_list_element_type_textview);
            // mCheckoutButton = itemView.findViewById(R.id.home_library_list_element_checkout_button);
            // mCheckoutUnavailableTextView = itemView.findViewById(R.id.home_library_list_element_checkout_unavailable_reason_textview);
            mTitleTextView = itemView.findViewById(R.id.home_library_list_element_title_textview);
            mAuthorsTextView = itemView.findViewById(R.id.home_library_list_element_authors_textview);
            mInfoTopTextView = itemView.findViewById(R.id.home_library_list_element_info_top_textview);
            mInfoBottomTextView = itemView.findViewById(R.id.home_library_list_element_info_bottom_textview);
            mStockTextView = itemView.findViewById(R.id.home_library_list_element_stock_textview);
            //mStockColorNormal = mStockTextView.getTextColors().getDefaultColor();
        }

        public void setItemOnClickListener(Document tag, View.OnClickListener listener) {
            mRootView.setOnClickListener(listener);
            mRootView.setTag(tag);
        }

        /*public void setCheckoutAvailable(boolean available, String reason) {
            mCheckoutButton.setVisibility(available ? View.VISIBLE : View.INVISIBLE);
            mCheckoutButton.setText(R.string.home_library_list_document_checkout);
            mCheckoutUnavailableTextView.setVisibility(available ? View.INVISIBLE : View.VISIBLE);
            if (!available) {
                mCheckoutUnavailableTextView.setText(reason);
            } else {
                if (reason != null)
                    mCheckoutButton.setText(reason);
            }
        }*/

        public void setImageResource(@DrawableRes int img){
            mDocumentImageView.setImageResource(img);
        }

        public void setTopInfo(String topInfo) {
            mInfoTopTextView.setVisibility(topInfo == null ? View.GONE : View.VISIBLE);
            if (topInfo != null) {
                mInfoTopTextView.setText(topInfo);
            }
        }

        public void setBottomInfo(String bottomInfo) {
            mInfoBottomTextView.setVisibility(bottomInfo == null ? View.GONE : View.VISIBLE);
            if (bottomInfo != null) {
                mInfoBottomTextView.setText(bottomInfo);
            }
        }

        public void setDocumentType(int type) {
            String typeStr = DocumentManager.getInstance().getDocumentType(type, itemView.getContext());
            mDocumentTypeTextView.setText(typeStr.toUpperCase());
        }

        public void setTitle(String title) {
            mTitleTextView.setText(title);
        }

        public void setAuthors(String authors) {
            mAuthorsTextView.setText("By " + authors);
        }

        /*public void setCheckoutListener(int id, View.OnClickListener onCheckoutClickListener) {
            mCheckoutButton.setOnClickListener(onCheckoutClickListener);
            mCheckoutButton.setTag(id);
        }*/

        public void setBestseller(boolean is) {
            if (is) {
                mBestsellerLayout.setVisibility(View.VISIBLE);
            } else {
                mBestsellerLayout.setVisibility(View.GONE);
            }
        }

        public void setKeywords(String[] keywords) {
            if (keywords == null || keywords.length == 0) {
                mKeywordsTextView.setVisibility(View.GONE);
                mKeywordsTextView.setText("");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < keywords.length; i++) {
                sb.append("#").append(keywords[i]);
                if (i != keywords.length - 1)
                    sb.append(" ");
            }
            mKeywordsTextView.setVisibility(View.VISIBLE);
            mKeywordsTextView.setText(sb.toString());
        }



    }

    public class OnShowDetailsListener implements View.OnClickListener {

        PatronLibraryListAdapter mAdapter;

        public OnShowDetailsListener(PatronLibraryListAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onClick(View view) {
            Document document = (Document) view.getTag();
            if (mAdapter.mOnShowDocumentDetailsListener!=null)
                mAdapter.mOnShowDocumentDetailsListener.onShow(view,document);
        }

    }
}