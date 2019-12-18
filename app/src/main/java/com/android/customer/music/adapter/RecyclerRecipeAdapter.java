package com.android.customer.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.model.RecipeSearchModel;

import java.util.List;

/**
 * author:Administrator
 * time:2018/11/12
 * decription:
 **/
public class RecyclerRecipeAdapter extends RecyclerView.Adapter<RecyclerRecipeAdapter.MyViewHolder> {
    private List<RecipeSearchModel.ResultBean.ListBean> list;
    private Context context;

    public RecyclerRecipeAdapter(List<RecipeSearchModel.ResultBean.ListBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_recipe_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final RecipeSearchModel.ResultBean.ListBean listBean = list.get(i);
        myViewHolder.tv_method.setText(listBean.getName());
        Glide.with(context).load(listBean.getThumbnail()).into(myViewHolder.iv_thumb);
        myViewHolder.iv_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewListener.click(listBean.getMenuId());
            }
        });
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public interface OnRecyclerViewListener {
        void click(String cid);
    }

    public void setList(List<RecipeSearchModel.ResultBean.ListBean> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumb;
        TextView tv_method;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_thumb = itemView.findViewById(R.id.iv_thumb);
            tv_method = itemView.findViewById(R.id.tv_method);
        }
    }
}
