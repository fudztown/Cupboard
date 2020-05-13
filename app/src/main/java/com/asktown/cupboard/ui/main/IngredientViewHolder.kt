package com.asktown.cupboard.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.Ingredient

//TODO: Move me, also can we use bindings here instead of "R"?

class IngredientViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.ingredient_list_item, parent, false)) {
    private var mIngName: TextView? = null
    private var mIngImg: TextView? = null

    init {
        mIngName = itemView.findViewById(R.id.ing_list_item_name)
        mIngImg = itemView.findViewById(R.id.ing_list_item_name)
    }

    fun bind(ing: Ingredient) {
        mIngName?.text = ing.name
        mIngImg?.text = ing.imgLocation
    }

}