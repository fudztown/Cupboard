package com.asktown.cupboard.ui.main

import android.content.Intent
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
import com.asktown.cupboard.data.model.Chef
import com.asktown.cupboard.data.model.ChefIngredient
import com.asktown.cupboard.data.model.Ingredient
import com.asktown.cupboard.databinding.ActivityMainBinding
import com.asktown.cupboard.ui.BaseActivity
import com.asktown.cupboard.ui.ingredients.FragmentSpiceRack
import com.asktown.cupboard.ui.register.GoogleSignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
    private lateinit var googleSignInClient: GoogleSignInClient

    private val mFireBaseUser = FirebaseAuth.getInstance().currentUser
    private val mFireStore = FirebaseFirestore.getInstance()

    //Check
    private val mChefCollection = mFireStore.collection("Chef")

    //chef ings
    private val mIngCollection =
        mFireStore.collection("Chef").document(mFireBaseUser?.uid.toString())
            .collection("ChefIngredients")
    private val mQuery = mIngCollection.orderBy("name", Query.Direction.ASCENDING)

    //mast ings
    private val mIngCollection2 =
        mFireStore.collection("Ingredients")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(IO).launch {
            if (hasChefAccount()) {
                Log.d(TAG, "User account found")
            } else {
                Log.d(TAG, "No user account - trigger create user account")
            }
            syncIngredients()
        }
        //Get user auth
        auth = Firebase.auth
        //Get Google Sign sign in details
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
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

    private fun signOut() {
        // Firebase sign out
        auth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this, GoogleSignInActivity::class.java))
        }
    }

    //TODO: not sure if i need this atm?
    override fun onClick(v: View) {
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
        //Sign out
        if (p0.itemId == R.id.signOutMain) {
            signOut()
        }
        return true
    }

    private suspend fun syncIngredients(): Boolean {
        //Show Sync Progress icon
        var newChefIngredient: ChefIngredient
        //Get current user's Ingredients
            val chefIng: HashMap<String, ChefIngredient> = getChefIngredients()
            Log.d(TAG, "Found : ${chefIng.size} Chef Ingredients")

        //Get the mast Ingredients
        val masterIng: HashMap<String, Ingredient> = getMasterIngredients()
        //Compare both sets and build update master
        for (ing in masterIng){
            if (!chefIng.containsKey(ing.key)) {
                newChefIngredient = ChefIngredient()
                newChefIngredient.name = ing.value.Name
                newChefIngredient.type = ing.value.Type
                newChefIngredient.imgLocation = ing.value.ImgLocation
                newChefIngredient.ingID = ing.key
                newChefIngredient.hasIng = false
                chefIng[ing.key] = newChefIngredient
            }
        }
        //Update user with new Ingredients
        for (ing in chefIng) {
            mIngCollection.document(ing.key)
                .set(ing.value)
                .await()
        }
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
        if(data!=null){
            for( document in data.documents){
                Log.d(TAG, "Found : ${document.data} Chef Ingredients")
                chefIng[document.id] = document.toObject(ChefIngredient::class.java)!!
            }
        }
       return chefIng
    }

    private suspend fun getMasterIngredients(): HashMap<String, Ingredient> {
        //TODO ensure the values or Ingredient and ChefIngredient are the same
        //Will probably do a Hashmap here too. We'll see.

        val masterIng: HashMap<String, Ingredient> = HashMap()
        val data = try {
            mIngCollection2
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
        if (data != null) {
            for (document in data.documents) {
                Log.d(TAG, "Found : ${document.data} Master Ingredients")
                masterIng[document.id] = document.toObject(Ingredient::class.java)!!
            }
        }
        return masterIng
    }

    /*
     * Check user has an account, if not then:
     * As user to create an account.
     * Create username. trigger some introduction stuff.
     */
    private suspend fun hasChefAccount(): Boolean {
        //Has the user got an account?
        val chef: Chef
        val chefData = mChefCollection.document(mFireBaseUser?.uid.toString())
            .get()
            .await()
        try {
            chef = chefData.toObject(Chef::class.java)!!
        } catch (e: Exception) {
            Log.d(TAG, e.printStackTrace().toString())
            return false
        }
        return chef.Username != null
    }

    companion object {
        private const val TAG = "GoogleActivity"
        //private const val RC_SIGN_IN = 9001
    }
}