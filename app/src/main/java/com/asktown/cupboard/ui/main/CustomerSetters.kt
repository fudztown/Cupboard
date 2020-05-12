package com.asktown.cupboard.ui.main

import android.widget.ImageButton
import androidx.databinding.BindingAdapter

//TODO: Rename and move this
class CustomerSetters {


    //TODO: Customise this to add in URL icon
    @BindingAdapter("imgSrc")
    fun setImgSrc(view: ImageButton, resId: Int) {
        view.setImageDrawable(view.context.getDrawable(resId))
        //TODO: Glide or something else to improve performance?
    }


}