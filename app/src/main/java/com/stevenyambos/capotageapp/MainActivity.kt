package com.stevenyambos.capotageapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.stevenyambos.capotageapp.navigation.Home
import com.stevenyambos.capotageapp.navigation.LogoutListener
import com.stevenyambos.capotageapp.navigation.Messages
import com.stevenyambos.capotageapp.navigation.Profile

class MainActivity : AppCompatActivity(), LogoutListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = Home()
        val messagesFragment = Messages()
        val profileFragment = Profile()
        profileFragment.setLogoutListener(this)

        setCurrentFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    setCurrentFragment(homeFragment)
                }
                R.id.messages -> {
                    setCurrentFragment(messagesFragment)
                }
                R.id.profile -> {
                    setCurrentFragment(profileFragment)
                }
            }
            true
        }

    } // fin onCreate

    override fun onLogout() {
        // Déconnecter l'utilisateur
        Firebase.auth.signOut()
        // Rediriger vers l'écran de connexion (LoginFragment)
        val intent = Intent(this, Login::class.java)
        // Ajouter le drapeau pour effacer l'historique d'activité
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
}