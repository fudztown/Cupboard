package com.asktown.cupboard.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chef(
    var Firstname: String? = null,
    var Surname: String? = null,
    var Username: String? = null,
    var chef_id: String? = null,
    var Level: Int? = null,
    var Doc_id: String? = null
)