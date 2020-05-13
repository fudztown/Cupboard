package com.asktown.cupboard.data.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.Ingredient

//TODO: Move me, also can we use bindings here instead of "R"?
//TODO: inflating here, might impact performance by doing this every time to page opens...?

class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mIngName: TextView? = null
    private var mIngType: TextView? = null
    //TODO: add image here

    init {
        mIngName = itemView.findViewById(R.id.ing_list_item_name)
        mIngType = itemView.findViewById(R.id.ing_list_item_name2)
    }

    fun bind(ing: Ingredient) {
        mIngName?.text = ing.name
        mIngType?.text = ing.imgLocation
    }

}