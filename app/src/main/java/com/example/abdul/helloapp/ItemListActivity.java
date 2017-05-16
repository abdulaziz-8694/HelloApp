package com.example.abdul.helloapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.abdul.helloapp.Entities.Item;
import com.example.abdul.helloapp.Entities.NetworkManager;
import com.example.abdul.helloapp.Utils.GsonRequest;

/**
 * Main Activity that fetches the data from the network once and
 * shows the mItems as a list.
 */
public class ItemListActivity extends AppCompatActivity implements Response.Listener<Item[]>,
        Response.ErrorListener {
    public static final String ITEM_URL = "https://gist.githubusercontent.com/maclir/f715d78b49c3b4b3b77f/raw/8854ab2fe4cbe2a5919cea97d71b714ae5a4838d/items.json";
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private Context mContext;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        mContext = this;
        setupUIControls();
        getItems();
    }

    private void getItems() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        GsonRequest<Item[]> gsonRequest = new GsonRequest<>(Request.Method.GET, ITEM_URL,
                Item[].class, this, this);
        NetworkManager.getInstance(mContext).addToRequestQueue(gsonRequest);
    }

    private void setItems(Item[] items){
        mAdapter.setData(items);
    }

    private void setupUIControls() {
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
        mRecyclerView.setVisibility(View.VISIBLE);
        error.printStackTrace();
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
                mItems[position].image = mItems[position].image.replace("http:", "https:");
                final Item mSelectedItem = mItems[position];
                ((ItemViewHolder)holder).mTitle.setText(mSelectedItem.title);
                ((ItemViewHolder)holder).mDescription.setText(mSelectedItem.description);
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
    }
}
