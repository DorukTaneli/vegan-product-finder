package com.example.mcateam6.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mcateam6.R
import com.example.mcateam6.activities.ProductInfoActivity
import com.example.mcateam6.database.RemoteDatabase
import com.example.mcateam6.datatypes.Attribute



class SearchItemAdapter(val context: Context, var productList: MutableList<RemoteDatabase.FirebaseProduct>, private val dietPref: String = "None") : RecyclerView.Adapter<SearchItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context

        val view = LayoutInflater.from(context).inflate(R.layout.search_listview_item, null)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position].toProduct()

        holder.tvNameKorean.text = product.koreanName
        holder.tvNameEnglish.text = product.englishName

        val vegan: Boolean = product.attributes[Attribute.VEGAN] == true
        val vegetarian: Boolean = product.attributes[Attribute.VEGETARIAN] == true

        when (dietPref) {
            "None" ->
                holder.ivAttribute.setImageResource(R.drawable.ic_check_green_24dp)
            "Vegetarian" ->
                if (vegetarian) {
                    holder.ivAttribute.setImageResource(R.drawable.ic_check_green_24dp)
                } else {
                    holder.ivAttribute.setImageResource(R.drawable.ic_warning_orange_24dp)
                }
            "Vegan" ->
                if (vegan) {
                    holder.ivAttribute.setImageResource(R.drawable.ic_check_green_24dp)
                } else {
                    holder.ivAttribute.setImageResource(R.drawable.ic_warning_orange_24dp)
                }
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val pos = adapterPosition
                val item = productList[pos]

                val intent = Intent(context, ProductInfoActivity::class.java).apply {
                    putExtra("id", item?.id)
                }
                context.startActivity(intent)
            }
        }
        var tvNameKorean: TextView = view.findViewById(R.id.tv_name_korean)
        var tvNameEnglish: TextView = view.findViewById(R.id.tv_name_english)
        var ivAttribute: ImageView = view.findViewById(R.id.iv_attribute)
    }

    class ItemDiffCallback(var oldItemList: List<RemoteDatabase.FirebaseProduct>?, var newItemList: List<RemoteDatabase.FirebaseProduct>?): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItemList?.get(oldItemPosition)?.id.equals(newItemList?.get(newItemPosition)?.id)
        }

        override fun getOldListSize(): Int {
            return oldItemList?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newItemList?.size ?: 0
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItemList?.get(oldItemPosition)?.name_korean.equals(newItemList?.get(newItemPosition)?.name_korean) &&
                    oldItemList?.get(oldItemPosition)?.name_english.equals(newItemList?.get(newItemPosition)?.name_english)
        }

    }
    fun updateItemList (newList: List<RemoteDatabase.FirebaseProduct>?) {
        val diffCallback = ItemDiffCallback(this.productList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.productList.clear()
        this.productList.addAll(newList as Iterable<RemoteDatabase.FirebaseProduct>)
        diffResult.dispatchUpdatesTo(this)
    }
    fun updateWholeData(newList: List<RemoteDatabase.FirebaseProduct>?) {
        productList.clear()
        productList.addAll(newList as MutableList)

        this.notifyDataSetChanged()
    }

}