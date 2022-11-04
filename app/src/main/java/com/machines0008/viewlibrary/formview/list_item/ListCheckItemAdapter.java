package com.machines0008.viewlibrary.formview.list_item;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.machines0008.viewlibrary.databinding.ItemListAdapterBinding;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Project Name: WLTApplication
 * Created By: user
 * Created On: 2022/10/28
 * Usage:
 **/
public class ListCheckItemAdapter<T extends ListCheckItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<T> list;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListAdapterBinding binding = ItemListAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        View view = binding.getRoot();
        view.setTag(binding);
        return new RecyclerView.ViewHolder(view){};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemListAdapterBinding binding = (ItemListAdapterBinding)holder.itemView.getTag();
        binding.setItemVo(CollectionUtils.get(list, position));
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(list);
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }
}
