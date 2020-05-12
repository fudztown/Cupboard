package com.asktown.cupboard.ui.main

import android.content.ContentValues
import android.os.Bundle;
import android.util.Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.Ingredient
import com.asktown.cupboard.data.model.IngredientCard
import com.asktown.cupboard.databinding.FragmentSpicerackBinding

public class FragmentSpiceRack : Fragment() {

    private lateinit var ingRecyclerView: RecyclerView
    private lateinit var binding: FragmentSpicerackBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_spicerack, container, false);
    }

    //Load Ingredients into the cards on the fragment
    fun loadIngredients() {
        var ing: Ingredient = Ingredient()
        var cards: ArrayList<IngredientCard> = ArrayList<IngredientCard>()


        //TODO: Loading in spice only for now
        ing.getIngredientsByType("Spice")
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    Log.d(ContentValues.TAG, "Name: ${document.data["Name"]}")
                    cards.add(
                        IngredientCard(
                            "${document.data["Name"]}",
                            "${document.data["Type"]}",
                            "${document.data["ImgLocation"]}"
                        )
                    )
                }
            }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ingRecyclerView = binding.ingredientList
        ingRecyclerView.layoutManager = LinearLayoutManager(activity)
        ingRecyclerView.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )


    }

}
