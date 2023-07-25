package com.stevenyambos.capotageapp.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.stevenyambos.capotageapp.R

interface LogoutListener {
    fun onLogout()
}

class Profile : Fragment() {
    private var logoutListener: LogoutListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setLogoutListener(listener: LogoutListener) {
        logoutListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val logoutButton: ImageButton = view.findViewById(R.id.buttonLogout)
        logoutButton.setOnClickListener {
            // Appeler la fonction de déconnexion de l'interface
            logoutListener?.onLogout()
        }

        return view
    }
}
