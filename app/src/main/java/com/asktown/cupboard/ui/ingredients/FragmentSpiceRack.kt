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
import com.asktown.cupboard.data.model.ChefIngredient
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

    private lateinit var mAdapter: FirestorePagingAdapter<ChefIngredient, IngredientViewHolder>
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mFireBaseUser = FirebaseAuth.getInstance().currentUser
    private val mIngCollection =
        mFireStore.collection("Chef").document(mFireBaseUser?.uid.toString())
            .collection("ChefIngredients")
    private val mQuery = mIngCollection.orderBy("Name", Query.Direction.ASCENDING)
    //Media player for sound files
    private lateinit var mMediaPlayer: MediaPlayer
    private val mChefIngredients: ArrayList<String> = ArrayList()

    //TODO: Probably on main load, will populate user with the latest ingredients in Firebase

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
        val ingredientList: RecyclerView = view.findViewById(R.id.ingredientList)
        ingredientList.setHasFixedSize(true)
        ingredientList.layoutManager = LinearLayoutManager(activity)

        setupAdapter(ingredientList)

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

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

        val query = mIngCollection.orderBy("Name", Query.Direction.ASCENDING)

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<ChefIngredient>()
            .setLifecycleOwner(this)
            .setQuery(query, config) { snapshot ->
                val ing: ChefIngredient = snapshot.toObject(ChefIngredient::class.java)!!
                ing.Guid = snapshot.id
                ing
            }
            .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<ChefIngredient, IngredientViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): IngredientViewHolder {

                val db: IngredientDataBinding =
                    IngredientDataBinding.inflate(layoutInflater, parent, false)

                return IngredientViewHolder(
                    db
                )
            }

            override fun onBindViewHolder(
                viewHolder: IngredientViewHolder,
                position: Int,
                ing: ChefIngredient
            ) {

                viewHolder.bind(ing)


                //Setup on click event
                val clickerDataBinding: IngredientDataBinding? = viewHolder.mIngredientDataBinding
                if (clickerDataBinding != null) {
                    Log.d(TAG, "Checking size " + mChefIngredients.size)
                    if (mChefIngredients.contains(ing.Guid.toString())) {
                        Log.d(TAG, "EPIC SUCCESS WE'RE HERE!!! The user has : " + ing.Name)
                        clickerDataBinding.ingListItemImg.setColorFilter(
                            Color.argb(
                                125,
                                0,
                                0,
                                0
                            )
                        )
                    }
                    if (ing.HasIng!!) {
                        Log.d(TAG, "The Chef has " + ing.Name)
                        clickerDataBinding.ingListItemImg.setColorFilter(
                            Color.argb(
                                125,
                                0,
                                0,
                                0
                            )
                        )
                    }
                    clickerDataBinding.handler = object : IngredientClickHandler {
                        override fun onImgClick() {
                            Log.d(TAG, "Click recorded")
                            if (clickerDataBinding.ingListItemImg.colorFilter != null) {
                                //Setup Media player for click noises!
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
                                //TODO update record to show the Chef has it!
                            }
                        }
                    }
                }


            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("MainActivity", e.message.toString())
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
