package com.example.myhappyplaces.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhappyplaces.R
import com.example.myhappyplaces.models.HappyPlaceModel
import org.w3c.dom.Text

open class HappyPlaceAdapter(
        val itemList : ArrayList<HappyPlaceModel>, val context : Context
) : RecyclerView.Adapter<HappyPlaceAdapter.viewHolder>() {

    class viewHolder(view : View) : RecyclerView.ViewHolder(view){
        val iv_place_image = view.findViewById<ImageView>(R.id.iv_place_image)
        val tv_title = view.findViewById<TextView>(R.id.tv_title)
        val tv_description = view.findViewById<TextView>(R.id.tv_description)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val model : HappyPlaceModel = itemList[position]
        holder.iv_place_image.setImageURI(Uri.parse(model.image))
        holder.tv_title.text = model.title
        holder.tv_description.text = model.description
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_happy_place, parent,false))
    }

}