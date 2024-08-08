package com.example.dateapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dateapp.databinding.ItemUserLayoutBinding
import com.example.dateapp.model.UserModel

class DatingAdapter(val context: Context, val list : List<UserModel>) : RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {
    inner class DatingViewHolder(val binding: ItemUserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DatingAdapter.DatingViewHolder {
        return DatingViewHolder(
            ItemUserLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DatingAdapter.DatingViewHolder, position: Int) {
        holder.binding.textView2.text = list[position].name
        holder.binding.textView3.text = list[position].email
        Glide.with(context).load(list[position].image)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}


