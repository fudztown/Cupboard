package com.asktown.cupboard.ui.main

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.databinding.IngredientDataBinding


//TODO: Rename and move me! FIX ME!
public class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var mDatabinding: IngredientDataBinding
    public fun ViewHolder(databinding: IngredientDataBinding) {
        // super(mDatabinding.root)
    }
}