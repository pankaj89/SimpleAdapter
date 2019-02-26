package com.simpleadapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import kotlin.collections.ArrayList

class SimpleAdapter<M> private constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val filteredList: ArrayList<M> = ArrayList()
    private val backupList: ArrayList<M> = ArrayList()

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0

    private var loading = false
    private var isLoadMoreEnabled = false
    private var loadMoreRes = -1

    internal var onItemViewClick: ((View, M, Int) -> Unit?)? = null
    internal var clickableIds: IntArray? = null

    //=======FILTER RELATED=======
    private var isFilterApplied = false

    private var filterText: String? = null

    val all: ArrayList<M>
        get() = if (isFilterApplied) {
            filteredList
        } else backupList

    override fun getItemViewType(position: Int): Int {
        return if (isLoadMoreEnabled && loading) {
            if (position == itemCount - 1) {
                VIEW_PROG
            } else {
                getViewType(all[position])
            }
        } else {
            getViewType(all[position])
        }
    }


    fun setClickableViews(onItemViewClick: (view: View, model: M, adapterPosition: Int) -> Unit, vararg ids: Int): SimpleAdapter<M> {
        this.onItemViewClick = onItemViewClick
        this.clickableIds = ids
        return this
    }

    private fun getProgressView(context: Context): View {
        val view = FrameLayout(context)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleSmall)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER_HORIZONTAL
        progressBar.layoutParams = lp
        (view as ViewGroup).addView(progressBar)
        return view
    }

    fun refreshData() {
        this.filteredList.clear()
        this.filteredList.addAll(backupList)
    }

    private fun getViewType(model: M): Int {
        for (triple in viewTypeList.reversed()) {
            if (triple.third(model)) {
                return triple.first
                break
            }
        }
        return 0
    }

    private fun getBinder(viewType: Int): ((Int, M, ViewDataBinding) -> Unit)? {
        for (triple in viewTypeList.reversed()) {
            if (triple.first == viewType) {
                return triple.second
                break
            }
        }
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == VIEW_PROG) {
            val view: View
            if (loadMoreRes == -1) {
                view = getProgressView(parent.context)
            } else {
                view = LayoutInflater.from(parent.context).inflate(loadMoreRes, parent, false)
            }
            return ProgressViewHolder(view)
        } else {
            val viewHolder = SimpleViewHolder(parent, viewType, getBinder(viewType))

            if (viewHolder.binder != null) {
                if (clickableIds != null && onItemViewClick != null) {
                    val clickListener = View.OnClickListener { view ->
                        val model: M

                        if (isFilterApplied)
                            model = filteredList[viewHolder.adapterPosition]
                        else
                            model = backupList[viewHolder.adapterPosition]

                        onItemViewClick?.invoke(view, model, viewHolder.adapterPosition)
                    }
                    for (clickableId in clickableIds!!) {
                        viewHolder.itemView.findViewById<View>(clickableId)?.setOnClickListener(clickListener)
                    }
                }
            }

            return viewHolder
        }
    }

    /*override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>?) {
        var isHandled = false

        if (holder !is ProgressViewHolder) {
            if (holder is SimpleAdapter<M>.SimpleViewHolder) {
                (holder as SimpleAdapter<M>.SimpleViewHolder).binder?.invoke(holder.adapterPosition, if (isFilterApplied) filteredList!![position] else backupList!![position], holder.binding)
            }
        }
        if (!isHandled) {
            super.onBindViewHolder(holder, position, payloads)
        }
    }*/

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is ProgressViewHolder) {
            if (holder is SimpleAdapter<*>.SimpleViewHolder) {
                (holder as SimpleAdapter<M>.SimpleViewHolder).binder?.invoke(holder.adapterPosition, if (isFilterApplied) filteredList!![position] else backupList!![position], holder.binding)
            }
        }
    }

    override fun getItemCount(): Int {
        if (isFilterApplied) {
            if (filteredList == null) return 0
            return if (isLoadMoreEnabled && loading) filteredList.size + 1 else filteredList.size
        } else {
            if (backupList == null) return 0
            return if (isLoadMoreEnabled && loading) backupList.size + 1 else backupList.size
        }
    }

    //=======LOAD MORE RELATED=======
    fun setLoadMoreComplete() {
        if (loading == true) {
            loading = false
            android.os.Handler().post { notifyItemRemoved(itemCount - 1) }
        }
    }

    fun enableLoadMore(recyclerView: RecyclerView, loadMoreRes: Int = -1, onLoadMoreListener: () -> Boolean): SimpleAdapter<M> {
        if (onLoadMoreListener != null) {

            val layoutManager = recyclerView.layoutManager

            if (layoutManager is GridLayoutManager) {
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (getItemViewType(position) == VIEW_PROG) layoutManager.spanCount else 1
                    }
                }
            }

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (layoutManager != null) {
                        val totalItemCount = layoutManager.itemCount - 1
                        var lastVisibleItem = 0

                        if (layoutManager is StaggeredGridLayoutManager) {
                            val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                            // get maximum element within the list
                            lastVisibleItem = getLastVisibleItem(lastVisibleItemPositions)
                            //                        firstVisibleItem = getFirstVisibleItem(((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null));
                        } else if (layoutManager is GridLayoutManager) {
                            lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                            //                        firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        } else if (layoutManager is LinearLayoutManager) {
                            lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                        }

                        if (!loading && totalItemCount <= lastVisibleItem) {
                            android.os.Handler().post {
                                val previous = loading
                                loading = onLoadMoreListener()
                                if (loading != previous) {
                                    if (previous == false && loading) {
                                        notifyItemInserted(itemCount - 1)
                                    } else if (previous == true && loading == false) {
                                        notifyItemRemoved(itemCount - 1)
                                    }
                                }
                            }
                        }
                    }
                }
            })

            android.os.Handler().postDelayed({
                //                    loading = onLoadMoreListener.onLoadMore();
                //                    notifyDataSetChanged();
            }, 2000)
            isLoadMoreEnabled = true
            this.loadMoreRes = loadMoreRes
        }
        return this
    }

    /**
     * Used for staggered
     *
     * @param lastVisibleItemPositions
     * @return
     */
    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (i in 0..lastVisibleItemPositions.size - 1) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    private fun clearFilter() {
        filteredList.clear()
        filteredList.addAll(backupList)
        isFilterApplied = false
    }

    interface FilterLogic<M> {
        fun filter(text: String, model: M): Boolean
    }

    private var filterLogic: ((String, M) -> Boolean)? = null
    fun performFilter(text: String?, filterLogic: ((text: String, model: M) -> Boolean)?) {
        this.filterText = text
        this.filterLogic = filterLogic
        if (text!!.length <= 0) {
            clearFilter()
        } else {
            filteredList.clear()
            for (model in backupList) {

                if (filterLogic?.invoke(text, model) == true) {
                    filteredList.add(model)
                }

            }
            isFilterApplied = true
        }
        notifyDataSetChanged()
    }

    private open inner class SimpleViewHolder(parent: ViewGroup, res: Int, var binder: ((Int, M, ViewDataBinding) -> Unit)?) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(res, parent, false)) {
        var binding: ViewDataBinding = DataBindingUtil.bind(itemView)
    }

    //--------NEW VIEW TYPE IMPLEMENTATION--------


    private val viewTypeList: ArrayList<Triple<Int, (adapterPosition: Int, model: M, binding: ViewDataBinding) -> Unit, (model: M) -> Boolean>> = ArrayList()
    fun <B : ViewDataBinding> addViewType(res: Int, binder: (adapterPosition: Int, model: M, binding: B) -> Unit, viewTypeLogic: (model: M) -> Boolean) {
        viewTypeList.add(Triple(res, binder as (Int, M, ViewDataBinding) -> Unit, viewTypeLogic))
    }
    //--------NEW VIEW TYPE IMPLEMENTATION--------

    private class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v)

    private fun ifFilterAppliedThenFilterList() {
        if (isFilterApplied) {
            performFilter(filterText, filterLogic)
        }
    }

    //ArrayListFunctions
    fun add(e: M): Boolean {
        val r = backupList.add(e)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun add(index: Int, element: M) {
        backupList.add(index, element)
        ifFilterAppliedThenFilterList()
    }

    operator fun get(index: Int): M {
        return backupList[index]
    }

    fun addAll(c: List<M>): Boolean {
        val r = backupList.addAll(c)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun addAll(c: Collection<M>): Boolean {
        val r = backupList.addAll(c)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun addAll(index: Int, c: Collection<M>): Boolean {
        val r = backupList.addAll(index, c)
        if (isFilterApplied) {
            performFilter(filterText, filterLogic)
        }
        return r
    }

    operator fun set(index: Int, e: M): M {
        val r = backupList.set(index, e)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun remove(index: Int): M {
        val r = backupList.removeAt(index)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun remove(o: M): Boolean {
        val r = backupList.remove(o)
        ifFilterAppliedThenFilterList()
        return r
    }

    fun clear() {
        backupList.clear()
        ifFilterAppliedThenFilterList()
    }

    fun removeAll(c: Collection<M>): Boolean {
        val r = backupList!!.removeAll(c)
        ifFilterAppliedThenFilterList()
        return r
    }


    /*lateinit var testB: (adapaterPosition: Int, model: M, binding: ViewDataBinding) -> Unit
    fun <B : ViewDataBinding> addViewType(res: Int, binder: (adapaterPosition: Int, model: M, binding: B) -> Unit) {
        testB = binder as (Int, M, ViewDataBinding) -> Unit
    }*/

    companion object {

        fun <M, B : ViewDataBinding> with(res: Int, binder: (adapaterPosition: Int, model: M, binding: B) -> Unit): SimpleAdapter<M> {
            val simpleAdapter = SimpleAdapter<M>()
            simpleAdapter.addViewType(res, binder, viewTypeLogic = { true })
            return simpleAdapter
        }
    }
}