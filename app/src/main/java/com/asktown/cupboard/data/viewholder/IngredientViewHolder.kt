package com.asktown.cupboard.data.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.Ingredient
import com.bumptech.glide.Glide


//TODO: Move me, also can we use bindings here instead of "R"?
//TODO: inflating here, might impact performance by doing this every time to page opens...?

class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mIngName: TextView? = null
    private var mIngType: TextView? = null
    private var mIngImgView: ImageView? = null
    //TODO: add image here

    init {
        mIngName = itemView.findViewById(R.id.ing_list_item_name)
        mIngType = itemView.findViewById(R.id.ing_list_item_name2)
        mIngImgView = itemView.findViewById(R.id.ing_list_item_img)
    }

    fun bind(ing: Ingredient) {
        mIngName?.text = ing.Name
        mIngType?.text = ing.Type
        mIngImgView?.let { Glide.with(itemView.context).load(ing.ImgLocation).into(it) }
    }

}