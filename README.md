![alt text](https://github.com/pankaj89/SimpleAdapter/blob/master/banner-readme-header.svg)

# SimpleAdapter (build with/for kotlin) 
####  #2 Line code for adapter

[![N|Solid](https://img.shields.io/badge/Android%20Arsenal-Simpler%20Recycler%20View%20Adapter-brightgreen.svg)](https://android-arsenal.com/details/1/5354)

#### SimpleAdapter is used to eliminate boilerplate code for create different class for default RecyclerView.Adapter with help for databinding.

# Features
- ##### Easy to use
- ##### No Need to create different adapter class(java/kotlin) each time for different screens
- ##### Offline Searching
- ##### Multiple views support
- ##### Load more for pagination implementation
- ##### Click listeners

### Setup
Include the following dependency in your apps build.gradle file.
```
implementation 'com.master.android:simpleadapter:3.1'
```
# How to use (kotlin)

#### Create Adapter
```kotlin
val adapter = SimpleAdapter.with<User, ItemBinding>(R.layout.item) { adapterPosition, model, binding ->
    binding.text.text = model.name
}
recyclerView.adapter = adapter
```
You will get adapter position, model and binding simply bind your model with binding

#### Add list to adapter
```kotlin
val list = ArrayList<User>()
for (i in 0..100) {
    list.add(User("$i"))
}
adapter2.addAll(list)
adapter2.notifyDataSetChanged()
```

#### Add Multiple Views
```kotlin
adapter.addViewType<Item1Binding>(R.layout.item_1, binder = { adapterPosition, model, binding ->
    binding.text1.text = model.name
}, viewTypeLogic = { model ->
    //Return true if received model is your view type
    model.name.toInt() % 5 == 0
})
```
 Here ViewTypeLogic is callback where you need to return true/false based on your type
 
 #### Add click listener for your views
```kotlin
adapter.setClickableViews({ view, model, adapterPosition ->
    Toast.makeText(this@MyActivity, "${model.name} clicked", Toast.LENGTH_SHORT).show()
}, R.id.text, R.id.text1, R.id.text2)
```
 #### Load more for pagination
```kotlin
adapter.enableLoadMore(recyclerView) {
    //Call WS here to load more item after items added call setLoadMoreComplete(), If there are more data to load return `true` else `false`
    true
}
```
- Return true when you need to load more items.
- When subsequent page are added to adapter call setLoadMoreComplete()

#### Filtering based on you keyword
```kotlin
adapter.performFilter(text = "", filterLogic = { text, model ->
    //Return true if this model is applicable for your filtering
    model.name.contains(text)
})
```
- Return true when you need to load more items.

### License
```
Copyright 2017 Pankaj Sharma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
