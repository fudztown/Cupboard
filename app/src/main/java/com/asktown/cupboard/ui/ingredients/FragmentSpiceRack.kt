package com.asktown.cupboard.ui.ingredients

import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Color
import android.media.MediaPlayer
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
import com.asktown.cupboard.databinding.IngredientDataBinding
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_spicerack.*

class FragmentSpiceRack : Fragment() {

    private lateinit var mAdapter: FirestorePagingAdapter<Ingredient, IngredientViewHolder>
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mIngCollection = mFireStore.collection("Ingredients")


    private val mQuery = mIngCollection.orderBy("Name", Query.Direction.ASCENDING)

    private lateinit var mMediaPlayer: MediaPlayer

    private fun getChef() {
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d(TAG, "Current User: " + user.uid)
            val mChefCollection = mFireStore.collection("Chef")
                .whereEqualTo("chef_id", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }


        };
    }


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
        getChef()
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

                //Changed this to databinding.. not sure what will happen now!
                //val view = layoutInflater.inflate(R.layout.ingredient_list_item, parent, false)
                val db: IngredientDataBinding =
                    IngredientDataBinding.inflate(layoutInflater, parent, false)


                return IngredientViewHolder(
                    db
                )
            }

            override fun onBindViewHolder(
                viewHolder: IngredientViewHolder,
                position: Int,
                ing: Ingredient
            ) {
                // Bind to ViewHolder (no position selected)
                viewHolder.bind(ing)
                //Setup Media player for click noises!

                //Setup on click event
                var clickerDataBinding: IngredientDataBinding? = viewHolder.mIngredientDataBinding



                if (clickerDataBinding != null) {
                    clickerDataBinding.handler = object : IngredientClickHandler {
                        override fun onImgClick() {
                            Log.d("TAG", "Click recorded")
                            if (clickerDataBinding.ingListItemImg.colorFilter != null) {
                                mMediaPlayer = MediaPlayer.create(context, R.raw.popdeselected)
                                mMediaPlayer.start()
                                clickerDataBinding.ingListItemImg.clearColorFilter()
                            } else {
                                mMediaPlayer = MediaPlayer.create(context, R.raw.popselected)
                                mMediaPlayer.start()
                                clickerDataBinding.ingListItemImg.setColorFilter(
                                    Color.argb(
                                        125,
                                        0,
                                        0,
                                        0
                                    )
                                )
                            }
                        }
                    }
                }


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
        fun newInstance(): FragmentSpiceRack =
            FragmentSpiceRack()
    }

}
