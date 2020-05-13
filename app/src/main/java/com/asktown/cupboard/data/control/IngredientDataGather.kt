package com.asktown.cupboard.data.control

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class IngredientDataGather {

    //TODO: Confirm if this is good practice or should this be called each time?
    var db: FirebaseFirestore = Firebase.firestore;

    /**
     * returns ingredients filtered by type !!WARNING, could be a lot!!
     */
    public fun getIngredients(): Task<QuerySnapshot> {
        return db.collection("Ingredients")
            .get()
    }

    /**
     * returns ingredients filtered by type
     */
    public fun getIngredientsByType(type: String): Task<QuerySnapshot> {
        return db.collection("Ingredients").whereEqualTo("Type", type)
            .get()
    }

    //TODO: Functions for new ingredient (further down the line!)
    public fun createNewIngredient(): Boolean {
        return false;
    }

}