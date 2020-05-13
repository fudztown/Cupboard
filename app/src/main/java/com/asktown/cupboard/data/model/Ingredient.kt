package com.asktown.cupboard.data.model

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

//Purpose of this class is to connect to firestore and gather information on ingredients available
// also update information in the collection
public data class Ingredient(var name: String, var type: String, var imgLocation: String) 