package purple.lightning.insterapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import purple.lightning.insterapp.databinding.GridItemBinding
import purple.lightning.insterapp.databinding.LocaluserRvItemCustomRowBinding
import java.io.File

class GridUserPostsAdapter(private var list: List<String>, private val GridonUserClickListener: GridOnUserClickListener, private val username:String):
    RecyclerView.Adapter<GridUserPostsAdapter.ViewHolder>() {
    private var storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private lateinit var bitmap: Bitmap

    class ViewHolder(binding: GridItemBinding): RecyclerView.ViewHolder(binding.root){
        val ivUserPost = binding.ivUserPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GridItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postId = list[position]
        val ref = storageReference?.child("${username}/POSTID_${postId}.jpg")
        var filename = File.createTempFile("tempImg", "jpg")
        ref?.getFile(filename)
            ?.addOnSuccessListener{
                bitmap = BitmapFactory.decodeFile(filename.absolutePath)
                holder.ivUserPost.setImageBitmap(bitmap)
            }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface GridOnUserClickListener{
        fun GridonUserClickListener(position: Int)
    }
}