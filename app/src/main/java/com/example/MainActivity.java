package com.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.databinding.ItemBinding;
import com.simpleadapter.SimpleAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<User> list = new ArrayList<>();

        list.add(new User("Pankaj"));
        list.add(new User("Sumit"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(SimpleAdapter.with(R.layout.item, list, new SimpleAdapter.Binder<User, ItemBinding>() {
            @Override
            public void onBind(int position, User model, ItemBinding binding) {
                binding.text.setText(model.name);
            }
        }));
        recyclerView.setAdapter(SimpleAdapter.with(R.layout.item, list, new SimpleAdapter.SmartBinder<User, ItemBinding>() {
            @Override
            public void onBind(int position, User model, ItemBinding binding) {
                binding.text.setText(model.name);
            }

            @Override
            public void onClick(View view, User model, int position) {
                super.onClick(view, model, position);
            }

            @Override
            public void onCheckedChange(View view, User model, int position) {
                super.onCheckedChange(view, model, position);
            }
        }).setClickableViews(R.id.text).setCheckableView(R.id.checkbox));
    }
}
