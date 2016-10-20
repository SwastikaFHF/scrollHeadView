package com.aitangba.testproject.lightadapter.viewmodel;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitangba.testproject.lightadapter.ItemFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2016/10/20.
 */

public class LightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TypeArray mTypeArray = new TypeArray();
    private ItemFactory mItemFactory;
    private Items mItems = new Items();

    public void setItemFactory(ItemFactory itemFactory) {
        mItemFactory = itemFactory;
    }

    public void setData(List<?> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemFactory.getViewType(mTypeArray, mItems.get(position));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        RecyclerView.ViewHolder viewHolder = null;
        try {
            Constructor<? extends RecyclerView.ViewHolder> constructor =  mTypeArray.get(viewType).getConstructor(View.class);
            viewHolder = constructor.newInstance(contentView);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class Items extends ArrayList<Object> {
    }

    public static class TypeArray extends SparseArray<Class<? extends RecyclerView.ViewHolder>> {

        public int bind(int key, Class<? extends RecyclerView.ViewHolder> value) {
            super.append(key, value);
            return key;
        }
    }
}
