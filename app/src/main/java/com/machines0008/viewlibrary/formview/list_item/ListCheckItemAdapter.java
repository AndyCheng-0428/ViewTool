package com.machines0008.viewlibrary.formview.list_item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.machines0008.viewlibrary.databinding.ItemListAdapterBinding;
import com.machines0008.viewlibrary.databinding.ItemListAdapterTcBinding;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

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
        View view;
        switch (viewType) {

            case ItemViewType.TYPE_TC_VERTICAL:
                ItemListAdapterTcBinding binding = ItemListAdapterTcBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                view = binding.getRoot();
                view.setTag(binding);
                break;
            case ItemViewType.TYPE_CT_LINEAR:
            default:
                ItemListAdapterBinding binding1 = ItemListAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                view = binding1.getRoot();
                view.setTag(binding1);
                break;
        }
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (CollectionUtils.get(list, position).getViewType()) {
            case ItemViewType.TYPE_CT_LINEAR:
                ItemListAdapterBinding binding = (ItemListAdapterBinding) holder.itemView.getTag();
                binding.setItemVo(CollectionUtils.get(list, position));
                break;
            case ItemViewType.TYPE_TC_VERTICAL:
                ItemListAdapterTcBinding binding1 = (ItemListAdapterTcBinding) holder.itemView.getTag();
                binding1.setItemVo(CollectionUtils.get(list, position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(list);
    }

    @Override
    public int getItemViewType(int position) {
        return CollectionUtils.get(list, position).getViewType();
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }
}
