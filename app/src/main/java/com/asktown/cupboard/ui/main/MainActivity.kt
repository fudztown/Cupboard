package com.asktown.cupboard.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import com.asktown.cupboard.R
import com.asktown.cupboard.databinding.ActivityMainBinding
import com.asktown.cupboard.databinding.ActivityMainToolbarBinding
import com.asktown.cupboard.ui.BaseActivity
import com.google.firebase.auth.FirebaseAuth


public class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: ActivityMainToolbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding2 = ActivityMainToolbarBinding.inflate(layoutInflater)
        Log.d(TAG, "Into Main Page");
        setContentView(binding.root)
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d(TAG, "Current User: " + user.email)
            binding2.textView2.text = getString(R.string.google_status_fmt, user.email)
        };
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


}