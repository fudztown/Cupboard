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
import com.asktown.cupboard.data.model.Chef
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSpiceRack : Fragment() {

    private lateinit var mAdapter: FirestorePagingAdapter<Ingredient, IngredientViewHolder>
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mIngCollection = mFireStore.collection("Ingredients")
    private val mQuery = mIngCollection.orderBy("Name", Query.Direction.ASCENDING)

    //Media player for sound files
    private lateinit var mMediaPlayer: MediaPlayer
    private var mChefIngredients: ArrayList<String> = ArrayList()

    //TODO add into its own class - as we'll need this stuff quite a bit
    //Pull user and match to firebase record
    private suspend fun getChef(): Chef {
        var chef = Chef()
        var user = FirebaseAuth.getInstance().currentUser
            val mChefCollection = mFireStore.collection("Chef")
                .whereEqualTo("chef_id", user?.uid)
                .get()
                .addOnSuccessListener { documents ->
                    chef = documents.first().toObject(Chef::class.java)
                    chef.Doc_id = documents.first().id
                    //Output for debugging
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    Log.d(
                        TAG,
                        "Chef's id is: ${chef.chef_id} => their username is ${chef.Username} : ${Thread.currentThread().name}"
                    )
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        delay(1000)
        return chef
    }

    private suspend fun getChefIngredient(chef: Chef, ingGuid: String): ArrayList<String> {

        var ingList = ArrayList<String>()
        Log.d(
            TAG,
            "About to try with: chef's id is: ${chef.Doc_id} => ing id ${ingGuid} : ${Thread.currentThread().name}"
        )
        val chefIngCollection = mFireStore.collection("Chef").document(chef.Doc_id.toString())
            .collection("ChefIngredients")
            .whereEqualTo("IngGuid", ingGuid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "FOUND ONE!  ${document.id} -> ${Thread.currentThread().name}")
                    ingList.add(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        delay(1000)
        return ingList
    }

    private suspend fun isChefIngredients(ing: String): Boolean {
        var chef = getChef()
        var ingList = getChefIngredient(chef, ing)

        if (ingList.size > 0) {
            Log.d(TAG, "SUCCESS!!! The user has : " + ing)
            writeIngredientsToMain(ing)
            return true
        } else {
            Log.d(TAG, "BOO!!! The no have! : " + ing)
            return false
        }
    }

    private suspend fun writeIngredientsToMain(ing: String) {
        withContext(Main) {
            Log.d(TAG, "Adding " + ing)
            mChefIngredients.add(ing)
        }
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
            .setQuery(
                mQuery, config
            ) { snapshot ->
                var ingred: Ingredient = snapshot.toObject(Ingredient::class.java)!!
                ingred.Guid = snapshot.id
                ingred
            }
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
                var chef: Chef = Chef()

                //TODO: Can pull each item now and do a check if the Chef has them or not
                //Coroutine running to find the answer.
                //Query will return, will now need to go and update the adapter. Maybe using main thread?
                //IO network, Main, Default (Heavy)
                CoroutineScope(IO).launch {
                    isChefIngredients(ing.Guid.toString())
                }
                // Bind to ViewHolder (no position selected)

                viewHolder.bind(ing)
                //Setup Media player for click noises!

                //Setup on click event
                var clickerDataBinding: IngredientDataBinding? = viewHolder.mIngredientDataBinding
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
                    clickerDataBinding.handler = object : IngredientClickHandler {
                        override fun onImgClick() {
                            Log.d(TAG, "Click recorded")
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
