package com.asktown.cupboard.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

//Purpose of this class is to connect to firestore and gather information on ingredients available
// also update information in the collection
@IgnoreExtraProperties
data class Ingredient(
    var Name: String? = null,
    var Type: String? = null,
    var ImgLocation: String? = null,
    var Guid: String? = null
)