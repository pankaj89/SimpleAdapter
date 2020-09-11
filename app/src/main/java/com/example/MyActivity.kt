package com.example

import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import coil.api.load
import com.example.databinding.Item1Binding
import com.example.databinding.Item2Binding
import com.example.databinding.ItemBinding
import com.simpleadapter.SimpleAdapter
import java.util.*

class MyActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<User>()
        val names= arrayOf("pankaj","ami","SUMIT Kumar","Aryan", "Anirudh","Pinky")
        for (i in 0..100) {
            list.add(User("${names.get(i % names.size)} $i"))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MyActivity)

        //Create adapter
        val adapter2 = SimpleAdapter.with<User, ItemBinding>(R.layout.item) { adapterPosition, model, binding ->
            binding.text.text = model.name
            binding.ivImageView2.load("https://picsum.photos/id/${adapterPosition}/200/200")
        }

        /*//multiple view holder
        adapter2.addViewType<Item1Binding>(R.layout.item_1, binder = { adapterPosition, model, binding ->
            binding.text1.text = model.name
        }, viewTypeLogic = { model ->
            model.name.toInt() % 5 == 0
        })

        //multiple view holder
        adapter2.addViewType<Item2Binding>(R.layout.item_2, binder = { adapterPosition, model, binding ->
            binding.text2.text = model.name
        }, viewTypeLogic = { model ->
            model.name.toInt() % 7 == 0
        })*/

        //clickable views
        adapter2.setClickableViews({ view, model, adapterPosition ->
            Toast.makeText(this@MyActivity, "${model.name} clicked", Toast.LENGTH_SHORT).show()
        }, R.id.text, R.id.text1, R.id.text2)

        //load more
        adapter2.enableLoadMore(recyclerView) {
            Handler().postDelayed({
                adapter2.addAll(list)
                adapter2.notifyDataSetChanged()
                adapter2.setLoadMoreComplete()
            },5000)
            true
        }

        //for filtering
        /*adapter2.performFilter(text = "", filterLogic = { text, model ->
            model.name.contains(text)
        })*/

        adapter2.addAll(list)
        adapter2.notifyDataSetChanged()
        recyclerView.adapter = adapter2

        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter2.performFilter(p0.toString()) { text, model ->
                    model.name.startsWith(text)
                }
            }
        })

        val swipeToRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshLayout)
        swipeToRefreshLayout.setOnRefreshListener {
            Handler().postDelayed({
                adapter2.clear()
                adapter2.addAll(list)
                adapter2.notifyDataSetChanged()
                swipeToRefreshLayout.isRefreshing=false
            },1000)
        }
    }
}