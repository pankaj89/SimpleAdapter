package com.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.databinding.ItemBinding
import com.simpleadapter.SimpleAdapter
import com.simpleadapter.SimpleAdapter.with
import java.util.*

class MyActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<User>()

        list.add(User("Pankaj"))
        list.add(User("Sumit"))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MyActivity)
        /*recyclerView.adapter = with(R.layout.item, list, { position: Int, model: User, binding: ItemBinding ->
            binding.text.setText(model.name)
        })*/
        recyclerView.adapter = with(R.layout.item, list, object : SimpleAdapter.SmartBinder<User, ItemBinding>() {
            override fun onBind(position: Int, model: User, binding: ItemBinding) {
                binding.text.setText(model.name)
            }

            override fun onClick(view: View?, model: User?, position: Int) {
                super.onClick(view, model, position)
            }

            override fun onCheckedChange(view: View?, model: User?, position: Int) {
                super.onCheckedChange(view, model, position)
            }
        }).setClickableViews(R.id.text).setCheckableView(R.id.checkbox)
    }
}