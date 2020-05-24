package com.asktown.cupboard.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChefIngredients(
    var Amount: Int? = null,
    var IngID: String? = null,
    var UsedBy: Int? = null
)