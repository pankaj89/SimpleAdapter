package com.simpleadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class SimpleAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<T> list;
    int layout;

    public static <M, B> SimpleAdapter with(int res, ArrayList<M> list, final Binder<M, B> binder) {
        return new SimpleAdapter<M>(list, res) {

            @Override
            public void onBind(int position, M model, ViewDataBinding binding) {
                binder.onBind(position, model, (B) binding);
            }
        };
    }

    public interface Binder<M, B> {
        void onBind(int position, M model, B binding);
    }

    public SimpleAdapter(ArrayList<T> list, int layout) {
        this.list = list;
        this.layout = layout;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleViewHolder simpleViewHolder = new SimpleViewHolder(parent, layout);
        return simpleViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBind(holder.getAdapterPosition(), list.get(position), ((SimpleViewHolder) holder).binding);
    }


    public abstract void onBind(int position, T model, ViewDataBinding binding);

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public ViewDataBinding binding;

        public SimpleViewHolder(ViewGroup parent, int res) {
            super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
            binding = DataBindingUtil.bind(itemView);

        }
    }
}