package com.asktown.cupboard.ui.main

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.Ingredient
import com.asktown.cupboard.data.viewholder.IngredientViewHolder
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_spicerack.*

class FragmentSpiceRack : Fragment() {

    private lateinit var mAdapter: FirestorePagingAdapter<Ingredient, IngredientViewHolder>
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mPostsCollection = mFireStore.collection("Ingredients")
    private val mQuery = mPostsCollection.orderBy("Name", Query.Direction.DESCENDING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_spicerack, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ingredientList: RecyclerView = view.findViewById(R.id.ingredientList)
        ingredientList.setHasFixedSize(true)
        ingredientList.layoutManager = LinearLayoutManager(activity)

        setupAdapter(ingredientList)
        // Refresh Action on Swipe Refresh Layout

        var swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    private fun setupAdapter(ingredientList: RecyclerView) {

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<Ingredient>()
            .setLifecycleOwner(this)
            .setQuery(mQuery, config, Ingredient::class.java)
            .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<Ingredient, IngredientViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): IngredientViewHolder {
                val view = layoutInflater.inflate(R.layout.ingredient_list_item, parent, false)
                return IngredientViewHolder(
                    view
                )
            }

            override fun onBindViewHolder(
                viewHolder: IngredientViewHolder,
                position: Int,
                ing: Ingredient
            ) {
                // Bind to ViewHolder
                viewHolder.bind(ing)
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("MainActivity", e.message)
            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                            Activity().applicationContext,
                            "Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }

        }

        // Finally Set the Adapter to RecyclerView
        ingredientList.adapter = mAdapter

    }


    // TODO: Confirm what this is? "companion object"
    companion object {
        fun newInstance(): FragmentSpiceRack = FragmentSpiceRack()
    }

}
