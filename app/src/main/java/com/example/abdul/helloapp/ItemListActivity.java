package com.example.abdul.helloapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ItemListActivity extends AppCompatActivity {
    public static final String ITEM_URL = "https://gist.githubusercontent.com/maclir/f715d78b49c3b4b3b77f/raw/8854ab2fe4cbe2a5919cea97d71b714ae5a4838d/items.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
    }
}
