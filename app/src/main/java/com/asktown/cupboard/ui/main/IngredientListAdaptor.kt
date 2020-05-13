package com.asktown.cupboard.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.data.model.Ingredient

class IngredientListAdaptor(private val list: ArrayList<Ingredient>)
    : RecyclerView.Adapter<IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return IngredientViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val mIngredient: Ingredient = list[position]
        holder.bind(mIngredient)
    }

    override fun getItemCount(): Int = list.size

}