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
import com.asktown.cupboard.R
import com.asktown.cupboard.data.model.ChefIngredient
import com.asktown.cupboard.data.model.Ingredient
import com.asktown.cupboard.databinding.ActivityMainBinding
import com.asktown.cupboard.ui.BaseActivity
import com.asktown.cupboard.ui.ingredients.FragmentSpiceRack
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.tasks.await

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

        CoroutineScope(IO).launch{
            syncIngredients()
        }

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

    private suspend fun syncIngredients(): Boolean {
        val masterIng: HashMap<String, Ingredient>
        //Show Sync Progress icon

        //Get current user's Ingredients
            val chefIng: HashMap<String, ChefIngredient> = getChefIngredients()
            Log.d(TAG, "Found : ${chefIng.size} Chef Ingredients")

        //Get the mast Ingredients
        masterIng = getMasterIngredients()
        //Compare both sets and build update master
        for (ing in masterIng){
            if(!chefIng.containsKey(ing.key)){
                //TODO add to the list push to chefIng as new ing.
            }
        }
        //Update user with new Ingredients
        //TODO can we just upload chefIng into here?
        //Show Sync completed
        delay(100)
        return false
    }

    private suspend fun getChefIngredients(): HashMap<String, ChefIngredient> {
        val chefIng: HashMap<String, ChefIngredient> = HashMap()
        val data =  try {
            mQuery
                .get()
                .await()
        }catch (e : Exception){
            null
        }
        data
        if(data!=null){
            for( document in data.documents){
                Log.d(TAG, "Found : ${document.data} Chef Ingredients")
                chefIng[document.id] = document.toObject(ChefIngredient::class.java)!!
            }
        }
       return chefIng
    }

    suspend fun getMasterIngredients(): HashMap<String,Ingredient> {
        //TODO ensure the values or Ingredient and ChefIngredient are the same
        //Will probably do a Hashmap here too. We'll see.

        val masterIng: HashMap<String, Ingredient> = HashMap()
        val data =  try {
            mQuery
                .get()
                .await()
        }catch (e : Exception){
            null
        }
        data
        if(data!=null){
            for( document in data.documents){
                Log.d(TAG, "Found : ${document.data} Chef Ingredients")
                masterIng[document.id] = document.toObject(Ingredient::class.java)!!
            }
        }
        return masterIng
    }


    suspend fun checkChefAccount(): Boolean {
        //Get current user's
        return false
    }


}