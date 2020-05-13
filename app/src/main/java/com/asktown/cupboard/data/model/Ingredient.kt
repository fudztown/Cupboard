package com.asktown.cupboard.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

//Purpose of this class is to connect to firestore and gather information on ingredients available
// also update information in the collection
@IgnoreExtraProperties
data class Ingredient(
    var name: String? = null,
    var type: String? = null,
    var imgLocation: String? = null
)