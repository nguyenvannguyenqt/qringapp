package com.example.ringqrapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.LayoutItemFaqBinding
import com.example.ringqrapp.model.Faq

class FaqAdapter(private var mListFaq: MutableList<Faq>?) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val itemBinding: LayoutItemFaqBinding =
            LayoutItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(
            itemBinding
        )
    }

    override fun getItemCount(): Int {
        return mListFaq?.size ?: 0
    }

    override fun onBindViewHolder(
        holder: FaqViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == 0) {
            holder.collapseExpandedView()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val currentFaq = mListFaq?.get(position)
        currentFaq?.let {
            holder.bindData(currentFaq)
            val isExpanded: Boolean = currentFaq.isExpanded
            holder.itemBinding!!.txtFaqAnswer.visibility =
                if (isExpanded) View.VISIBLE else View.GONE
            holder.itemBinding.imgExpandedAnswerFaq.setImageResource(
                if (isExpanded) R.drawable.ic_expanded_arrow else R.drawable.ic_arrow_forward
            )

            holder.itemBinding.layoutFaq.setOnClickListener {
                isAnyItemExpanded(position)
                currentFaq.isExpanded = !currentFaq.isExpanded
                notifyItemChanged(position,Unit)
            }
        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val temp = mListFaq!!.indexOfFirst {
            it.isExpanded
        }
        if (temp >= 0 && temp != position) {
            mListFaq!![temp].isExpanded = false
            notifyItemChanged(temp, 0)
        }
    }

    inner class FaqViewHolder(val itemBinding: LayoutItemFaqBinding?) :
        RecyclerView.ViewHolder(itemBinding!!.root) {
        fun bindData(faq: Faq) {
            itemBinding?.let {
                it.txtFaq.text = faq.question
                it.txtFaqAnswer.text = faq.answer
            }
        }

        fun collapseExpandedView() {
            itemBinding?.txtFaqAnswer?.visibility = View.GONE
            itemBinding?.imgExpandedAnswerFaq?.setImageResource(R.drawable.ic_arrow_forward )
        }
    }

}