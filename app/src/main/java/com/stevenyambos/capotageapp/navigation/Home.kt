package com.stevenyambos.capotageapp.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.stevenyambos.capotageapp.CreateProduct
import com.stevenyambos.capotageapp.R
import com.stevenyambos.capotageapp.controllers.ProductListAdapter
import com.stevenyambos.capotageapp.models.ProductsCard

class Home : Fragment() {

    private val productList = arrayListOf<ProductsCard>()
    private lateinit var adapter: ProductListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val addButton = view.findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener {
            openCreateProductScreen()
        }

        // Afficher les produits existants dès la création de la vue
        getProductsData()

        recyclerView = view.findViewById(R.id.listViewProducts) // Assurez-vous que l'ID correspond au RecyclerView dans votre layout
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductListAdapter(requireContext(), productList)
        recyclerView.adapter = adapter

        return view
    }

    companion object {
        const val CREATE_PRODUCT_REQUEST_CODE = 1001
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            handleActivityResult(data)
        }
    }

    private fun openCreateProductScreen() {
        val intent = Intent(activity, CreateProduct::class.java)
        resultLauncher.launch(intent)
    }

    private fun handleActivityResult(data: Intent?) {
        if (data != null) {
            val name = data.getStringExtra("name")
            val description = data.getStringExtra("description")
            val price = data.getStringExtra("price")
            val imageUrl = data.getStringExtra("imageUrl")

            if (name != null && description != null && price != null && imageUrl != null) {
                val product = ProductsCard(name, description, price.toInt(), imageUrl)
                productList.add(product)

                // Mettre à jour l'affichage de la liste des produits en notifiant à l'adaptateur que les données ont changé
                adapter.notifyDataSetChanged()

                println("Nouveau produit ajouté : ${product.productTitle}")
            }
        }
    }

    private fun getProductsData() {
        val db = FirebaseFirestore.getInstance()
        val productsCollection = db.collection("products")

        productsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                // Effacer la liste existante avant de la remplir à nouveau

                for (document in querySnapshot) {
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val price = document.getString("price")
                    val imageUrl = document.getString("imageUrl")

                    if (name != null && description != null && price != null && imageUrl != null) {
                        val product = ProductsCard(name, description, price.toInt(), imageUrl)
                        productList.add(product)
                    }
                }

                // Mettre à jour l'affichage de la liste des produits en notifiant à l'adaptateur que les données ont changé
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Gérer l'erreur en cas d'échec de la récupération des données
            }
    }
}
