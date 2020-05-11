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
import com.asktown.cupboard.databinding.ActivityMainBinding
import com.asktown.cupboard.ui.BaseActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.view.*


public class MainActivity : BaseActivity(), View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout;
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)


        //navigation listener
        navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this)





        Log.d(TAG, "Into Main Page");

        //Set view
        setContentView(binding.root)


        //Check user logged in
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d(TAG, "Current User: " + user.email)
            //binding2.textView2.text = getString(R.string.google_status_fmt, user.email)
        };

        //Setup Toolbar
        toolbar = binding.toolbar2.toolbar
        setSupportActionBar(toolbar)


        //Add the menu hamburger
        drawerLayout = binding.welcomeLayout;
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

    // [END onactivityresult]
    override fun onClick(v: View) {
        when (v.id) {
            //R.id.signInButton -> signIn()
            //R.id.signOutButton -> signOut()
            //R.id.disconnectButton -> revokeAccess()
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        drawerLayout.closeDrawer(GravityCompat.START)

        //Open spice rack
        if (p0.itemId == R.id.spiceRack) {
            fragmentManager = supportFragmentManager
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                binding.fragmentLoad.fragment_container.id,
                FragmentSpiceRack()
            )
            fragmentTransaction.commit()
        }
        if (p0.itemId == R.id.home) {
            fragmentManager = supportFragmentManager
            fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                binding.fragmentLoad.fragment_container.id,
                FragmentNewsFeed()
            )
            fragmentTransaction.commit()
        }
        return true;
    }


}