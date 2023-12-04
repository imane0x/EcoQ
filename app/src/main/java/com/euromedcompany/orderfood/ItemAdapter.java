package com.euromedcompany.orderfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<DataClass> itemList;
    private Context context;

    public ItemAdapter(Context context, List<DataClass> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataClass item = itemList.get(position);

        Glide.with(context).load(item.getImageURL()).into(holder.imageView);
        holder.titleTextView.setText(item.getTitle());
        holder.typeTextView.setText(item.getType());
        holder.descriptionTextView.setText(item.getDesc());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView typeTextView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ImageView);
            titleTextView = itemView.findViewById(R.id.TitleTextView);
            typeTextView = itemView.findViewById(R.id.TypeTextView);
            descriptionTextView = itemView.findViewById(R.id.DescriptionTextView);
        }
    }
}
