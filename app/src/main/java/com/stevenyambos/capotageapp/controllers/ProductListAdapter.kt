package com.stevenyambos.capotageapp.controllers
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stevenyambos.capotageapp.R
import com.stevenyambos.capotageapp.models.ProductsCard

class ProductListAdapter(private val context: Context, private val productList: ArrayList<ProductsCard>) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.productTitle.text = product.productTitle
        holder.productDescription.text = product.productDescription
        holder.productPrice.text = context.getString(R.string.price_format, product.productPrice)
        holder.productImage.setImageResource(product.productImage)
        // Ici, vous pouvez utiliser une bibliothèque d'image comme Glide ou Picasso pour charger l'image à partir de l'URL dans product.productImage
        // Exemple avec Glide :
        // Glide.with(context).load(product.productImage).into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    // ViewHolder spécifique à RecyclerView
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productTitle: TextView = view.findViewById(R.id.productTitle)
        val productDescription: TextView = view.findViewById(R.id.productDescription)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productImage: ImageView = view.findViewById(R.id.productImage)
    }
}

private fun ImageView.setImageResource(productImage: String) {

}
