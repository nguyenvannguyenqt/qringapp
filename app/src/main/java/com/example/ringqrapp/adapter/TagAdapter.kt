package com.example.ringqrapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.databinding.LayoutItemTagBinding

class TagAdapter ( private val mListTag:List<String>?)
    : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {


    inner class TagViewHolder(private val itemTagBinding:LayoutItemTagBinding?)
        : RecyclerView.ViewHolder(itemTagBinding!!.root)
    {
            fun bindData(tag:String)
            {
                itemTagBinding?.txtNameTag?.text = tag
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemTagBinding = LayoutItemTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TagViewHolder(itemTagBinding)
    }

    override fun getItemCount(): Int {
        return mListTag?.size ?: 0
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val currentTag = mListTag?.get(position)
        currentTag?.let {
            holder.bindData(tag = currentTag)
        }
    }
}