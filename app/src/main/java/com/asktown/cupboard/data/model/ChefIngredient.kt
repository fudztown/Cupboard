package com.asktown.cupboard.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChefIngredient(
    var amount: Int? = null,
    var ingID: String? = null,
    var usedBy: Timestamp? = null,
    var hasIng: Boolean? = null,
    var imgLocation: String? = null,
    var guid: String? = null,
    var name: String? = null,
    var type: String? = null
)