package com.stevenyambos.capotageapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.stevenyambos.capotageapp.controllers.ProductListAdapter
import com.stevenyambos.capotageapp.models.ProductsCard

class CreateProduct : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var addImageButton: Button
    private var selectedImageUri: Uri? = null

    private lateinit var productList: MutableList<ProductsCard>
    private lateinit var adapter: ProductListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        addImageButton = findViewById(R.id.addImageButton)
        nameEditText = findViewById(R.id.editTextProductTitle)
        descriptionEditText = findViewById(R.id.editTextProductDescription)
        priceEditText = findViewById(R.id.editTextProductPrice)
        saveButton = findViewById(R.id.addProductButton)

        addImageButton.setOnClickListener {
            openGalleryForImage()
        }

        saveButton.setOnClickListener {
            // Récupérer les valeurs saisies par l'utilisateur
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val price = priceEditText.text.toString()

            // Télécharger l'image sélectionnée dans Firebase Storage
            if (selectedImageUri != null) {
                val storageReference = FirebaseStorage.getInstance().reference
                val imageRef = storageReference.child("images/${selectedImageUri!!.lastPathSegment}")

                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Récupérer l'URL de téléchargement de l'image
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Ici, vous pouvez stocker l'URI de téléchargement de l'image (uri) dans la variable selectedImageUri
                            // Vous pouvez ensuite l'utiliser pour associer l'URL de l'image à la fiche produit lors de la sauvegarde dans Firestore.
                            selectedImageUri = uri

                            // Ajouter les informations dans Firestore avec l'URL de l'image mise à jour :
                            val db = FirebaseFirestore.getInstance()
                            val product = hashMapOf(
                                "name" to name,
                                "description" to description,
                                "price" to price,
                                "imageUrl" to selectedImageUri.toString() // Ajouter l'URL de l'image dans le document Firestore
                            )

                            db.collection("products")
                                .add(product)
                                .addOnSuccessListener { documentReference ->
                                    // Fiche produit créée avec succès
                                    // Vous pouvez retourner à l'écran précédent ou effectuer une autre action

                                    // Créer un nouvel Intent pour renvoyer les résultats à l'activité appelante (dans ce cas, le Fragment Home)
                                    val resultIntent = Intent()
                                    resultIntent.putExtra("name", name)
                                    resultIntent.putExtra("description", description)
                                    resultIntent.putExtra("price", price)
                                    resultIntent.putExtra("imageUrl", selectedImageUri.toString())

                                    // Définir le code de résultat comme RESULT_OK pour indiquer que l'opération s'est terminée avec succès
                                    setResult(Activity.RESULT_OK, resultIntent)

                                    // Terminer l'activité en cours pour revenir à l'activité appelante (le Fragment Home)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Une erreur s'est produite lors de la création de la fiche produit
                                    // Gérer l'erreur en conséquence
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Une erreur s'est produite lors du téléchargement de l'image
                        // Gérer l'erreur en conséquence
                    }
            } else {
                // Si aucune image n'a été sélectionnée, ajouter les autres informations sans imageUrl
                val db = FirebaseFirestore.getInstance()
                val product = hashMapOf(
                    "name" to name,
                    "description" to description,
                    "price" to price
                )

                db.collection("products")
                    .add(product)
                    .addOnSuccessListener { documentReference ->
                        // Fiche produit créée avec succès
                        // Vous pouvez retourner à l'écran précédent ou effectuer une autre action

                        // Créer un nouvel Intent pour renvoyer les résultats à l'activité appelante (dans ce cas, le Fragment Home)
                        val resultIntent = Intent()
                        resultIntent.putExtra("name", name)
                        resultIntent.putExtra("description", description)
                        resultIntent.putExtra("price", price)

                        // Définir le code de résultat comme RESULT_OK pour indiquer que l'opération s'est terminée avec succès
                        setResult(Activity.RESULT_OK, resultIntent)

                        // Terminer l'activité en cours pour revenir à l'activité appelante (le Fragment Home)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Une erreur s'est produite lors de la création de la fiche produit
                        // Gérer l'erreur en conséquence
                    }
            }
        }
    }

    // Dans la fonction openGalleryForImage(), après avoir obtenu l'URI de l'image sélectionnée
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    // Déclarer le resultLauncher en tant que propriété de classe
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // Le code pour gérer le résultat de l'activité se trouve dans le callback ici
            handleActivityResult(data)
        }
    }

    // Ajouter une méthode pour gérer le résultat de l'activité
    private fun handleActivityResult(data: Intent?) {
        if (data != null) {
            // Récupérer les informations du nouvel élément ajouté depuis l'écran CreateProduct
            val name = data.getStringExtra("name")
            val description = data.getStringExtra("description")
            val price = data.getStringExtra("price")
            val imageUrl = data.getStringExtra("imageUrl")

            // Ajouter les informations à la liste de produits
            if (name != null && description != null && price != null && imageUrl != null) {
                val product = ProductsCard(name, description, price.toInt(), imageUrl)
                productList.add(product)

                // Mettre à jour l'affichage de la liste des produits en notifiant à l'adaptateur que les données ont changé
                adapter.notifyDataSetChanged()

                // Ajoutez un message de journalisation pour vérifier si le nouveau produit est ajouté avec succès
                println("Nouveau produit ajouté : ${product.productTitle}")
            }
        }
    }
}
