package com.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.databinding.ItemBinding
import com.simpleadapter.SimpleAdapter.with
import java.util.*

class MyActivity : AppCompatActivity() {

    val list = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list.add(User("Pankaj"))
        list.add(User("Sumit"))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MyActivity)
        recyclerView.adapter = with(R.layout.item, list, { position: Int, model: User, binding: ItemBinding ->
            binding.text.setText(model.name)
        })
    }
}