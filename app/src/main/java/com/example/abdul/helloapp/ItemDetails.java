package com.example.abdul.helloapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.abdul.helloapp.Entities.Item;
import com.example.abdul.helloapp.Entities.NetworkManager;
import com.example.abdul.helloapp.Utils.Constants;

public class ItemDetails extends AppCompatActivity {
    Item mItem;
    TextView mTitle;
    TextView mDescription;
    NetworkImageView mImage;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        setUpUI();
        Intent intent = getIntent();
        if(intent != null){
            mItem = intent.getParcelableExtra(Constants.BUNDLE_ITEM);
            if(mItem ==null){
                finish();
            } else {
                setItemDetails();
            }
        } else {
            finish();
        }
    }

    private void setItemDetails() {
        if(mItem !=null){
            mTitle.setText(mItem.title);
            mDescription.setText(mItem.description);
            mImage.setImageUrl(mItem.image, mImageLoader);
        }
    }

    private void setUpUI() {
        mTitle = (TextView) findViewById(R.id.title);
        mDescription = (TextView) findViewById(R.id.description);
        mImage = (NetworkImageView) findViewById(R.id.image);
        mImageLoader = NetworkManager.getInstance(ItemDetails.this).getImageLoader();
    }
}
