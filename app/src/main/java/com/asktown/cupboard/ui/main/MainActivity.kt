package com.asktown.cupboard.ui.main

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.paging.PagedList
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.ChefIngredient
import com.asktown.cupboard.data.model.Ingredient
import com.asktown.cupboard.databinding.ActivityMainBinding
import com.asktown.cupboard.ui.BaseActivity
import com.asktown.cupboard.ui.ingredients.FragmentSpiceRack
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.delay

class MainActivity : BaseActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    private val mFireBaseUser = FirebaseAuth.getInstance().currentUser
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mIngCollection =
        mFireStore.collection("Chef").document(mFireBaseUser?.uid.toString())
            .collection("ChefIngredients")
    private val mQuery = mIngCollection.orderBy("name", Query.Direction.ASCENDING)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get user auth
        auth = Firebase.auth

        //Setup bindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        //navigation listener
        navigationView = binding.navigationView
        navigationView.setNavigationItemSelectedListener(this)
        //Set view (default view)
        setContentView(binding.root)
        //Check user logged in
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d(TAG, "Current User: " + user.email)
        }

        //Setup Toolbar
        toolbar = binding.toolbar2.toolbar
        setSupportActionBar(toolbar)

        //Add the menu hamburger
        drawerLayout = binding.welcomeLayout
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        actionBarDrawerToggle.syncState()

        //Load default fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(binding.fragmentLoad.fragment_container.id, FragmentNewsFeed())
        fragmentTransaction.commit()

    }

    //TODO: not sure if i need this atm?
    override fun onClick(v: View) {
    }

    companion object {
        private const val TAG = "GoogleActivity"
        //private const val RC_SIGN_IN = 9001
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        drawerLayout.closeDrawer(GravityCompat.START)

        //Open spice rack
        if (p0.itemId == R.id.spiceRack) {
            fragmentManager = supportFragmentManager
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                binding.fragmentLoad.fragment_container.id,
                FragmentSpiceRack.newInstance()
            )
            fragmentTransaction.commit()
        }
        //Open news feed
        if (p0.itemId == R.id.home) {
            fragmentManager = supportFragmentManager
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                binding.fragmentLoad.fragment_container.id,
                FragmentNewsFeed()
            )
            fragmentTransaction.commit()
        }
        return true
    }

    suspend fun syncIngredients(): Boolean {
        val chefIng: HashMap<String, ChefIngredient>
        val masterIng: ArrayList<Ingredient>
        //Show Sync Progress icon

        //Get current user's Ingredients
        chefIng = getChefIngredients()
        //Get the mast Ingredients
        masterIng = getMasterIngredients()
        //Compare both sets and build update master

        //Update user with new Ingredients

        //Show Sync completed
        delay(100)
        return false
    }

    suspend fun getChefIngredients(): HashMap<String, ChefIngredient> {
        val chefIng: HashMap<String, ChefIngredient> = HashMap()
        //Do something
        mQuery
            .whereLessThan("age", "ageCondition")
            .get()
            .addOnSuccessListener() { documents ->
                try {
                    if (documents != null) {
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            //chefIng.set(document.id, document.data.)
                        }
                    } else {
                        Log.d(TAG, "Fail")
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, ex.message)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error writing document", e)
            }

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        val options = FirestorePagingOptions.Builder<ChefIngredient>()
            .setLifecycleOwner(this)
            .setQuery(mQuery, config) { snapshot ->
                val ing: ChefIngredient = snapshot.toObject(ChefIngredient::class.java)!!
                ing.guid = snapshot.id
                ing
            }
            .build()


        return chefIng
    }

    suspend fun getMasterIngredients(): ArrayList<Ingredient> {
        val masterIng: ArrayList<Ingredient> = ArrayList()
        //Do something

        return masterIng
    }


    suspend fun checkChefAccount(): Boolean {
        //Get current user's
        return false
    }


}