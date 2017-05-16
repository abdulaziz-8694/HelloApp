package com.example.abdul.helloapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.abdul.helloapp.Entities.Item;
import com.example.abdul.helloapp.Entities.NetworkManager;
import com.example.abdul.helloapp.Utils.Constants;
import com.example.abdul.helloapp.Utils.GsonRequest;

/**
 * Main Activity that fetches the data from the network once and
 * shows the mItems as a list.
 */
public class ItemListActivity extends AppCompatActivity implements Response.Listener<Item[]>,
        Response.ErrorListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private Context mContext;
    private ProgressBar mProgressBar;
    private Button mRetryButton;
    private View mInternetContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        mContext = this;
        setupUIControls();
        getItems();
    }

    private void getItems() {
        mInternetContainer.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        GsonRequest<Item[]> gsonRequest = new GsonRequest<>(Request.Method.GET, Constants.ITEM_URL,
                Item[].class, this, this);
        NetworkManager.getInstance(mContext).addToRequestQueue(gsonRequest);
    }

    private void setItems(Item[] items){
        mAdapter.setData(items);
    }

    private void setupUIControls() {
        mInternetContainer = findViewById(R.id.no_internet_container);
        mRetryButton = (Button) findViewById(R.id.btn_retry);
        mRetryButton.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_item);
        mAdapter = new ItemAdapter(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onResponse(Item[] response) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        setItems(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mInternetContainer.setVisibility(View.VISIBLE);
        error.printStackTrace();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_retry){
            getItems();
        }

    }

    private class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context mContext;
        private Item[] mItems;
        private ImageLoader mImageLoader;

        ItemAdapter(Context context) {
            mContext = context;
            mImageLoader = NetworkManager.getInstance(mContext).getImageLoader();
        }

        public void setData(Item[] itemList) {
            mItems = itemList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ItemViewHolder){
                //simple hack to prevent failure on redirect from http to https
                mItems[position].image = mItems[position].image.replace("http:", "https:");
                final Item mSelectedItem = mItems[position];
                ((ItemViewHolder)holder).mTitle.setText(mSelectedItem.title);
                ((ItemViewHolder)holder).mDescription.setText(Html.fromHtml(getResources()
                        .getString(R.string.summary, mSelectedItem.description)));
                ((ItemViewHolder)holder).mImage.setImageUrl(mSelectedItem.image,
                        mImageLoader);
                ((ItemViewHolder)holder).mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showItemDetails(mSelectedItem);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.length;
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {
            NetworkImageView mImage;
            TextView mTitle;
            TextView mDescription;
            View mView;

            ItemViewHolder(View itemView) {
                super(itemView);
                mView = itemView.findViewById(R.id.item_view);
                mImage = (NetworkImageView) itemView.findViewById(R.id.item_image);
                mTitle = (TextView) itemView.findViewById(R.id.item_title);
                mDescription = (TextView) itemView.findViewById(R.id.item_description);
            }
        }
    }

    private void showItemDetails(Item mSelectedItem) {
        Log.d("ITEM", mSelectedItem.title);
        Intent intent = new Intent(mContext, ItemDetails.class);
        intent.putExtra(Constants.BUNDLE_ITEM, mSelectedItem);
        startActivity(intent);
    }
}
