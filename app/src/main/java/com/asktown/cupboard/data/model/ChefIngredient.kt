package com.asktown.cupboard.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChefIngredient(
    var Amount: Int? = null,
    var IngID: String? = null,
    var UsedBy: Timestamp? = null,
    var HasIng: Boolean? = null,
    var ImgLocation: String? = null,
    var Guid: String? = null,
    var Name: String? = null,
    var Type: String? = null
)