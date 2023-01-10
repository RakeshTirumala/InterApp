package purple.lightning.insterapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import purple.lightning.insterapp.databinding.GridItemBinding
import purple.lightning.insterapp.databinding.LinearItemBinding
import java.io.File

class LinearUserPostsAdapter(private var list: List<String>,
                             private val LinearonUserClickListener: LinearOnUserClickListener,
                             private val usernames:List<String>,
                             private val activeUser:String):
    RecyclerView.Adapter<LinearUserPostsAdapter.ViewHolder>(){
    private var storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private lateinit var bitmap: Bitmap
    private lateinit var profileBitmap: Bitmap
//    private var CaptionMap:MutableMap<String, String>? = null
    private var LikesMap:MutableMap<String, List<UserPostResponseModel>>? = null
//    private var CaptionMapBoolean:Boolean = false



//    INITIALIZING CAPTION DATABASE
    private var databaseToCapnLike = FirebaseDatabase
        .getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/")
        .getReference("IndividualUserPosts")
////        .child(username).child("captionMap")
////    INITIALIZING LIKES DATABASE
//    private var databaseToLike = FirebaseDatabase
//        .getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/")
//        .getReference("IndividualUserPosts")
////        .child(username).child("likesMap")



    class ViewHolder(binding:LinearItemBinding): RecyclerView.ViewHolder(binding.root){
        val ivUserSPost = binding.ivUserSPost
        val civUserPostProfilePic = binding.civUserPostProfilePic
        val tvPostUserName = binding.tvPostUserName
        var tvUserPostCaption = binding.tvUserPostCaption
        var ivUserPostLike = binding.ivUserPostLike
//        var tvLikesCount = binding.tvLikesCount

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LinearItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postId = list[position]
        val username = usernames[position]
//        var LikeMapBoolean:Boolean = false
//        var CaptionMapBoolean = false
        var CaptionMap:MutableMap<String, String>? = null

        Log.d("[CODE]:", "THE USERNAME ${username} ")

        databaseToCapnLike
            .child("${username}")
            .child("captionMap")
            .get()
            .addOnSuccessListener {
                CaptionMap = it.value as MutableMap<String, String>
                Log.d("[CODE]:", "GOT ACCESS TO THE DATABASE ${CaptionMap}")
                holder.tvUserPostCaption.text = "[${username}] ${CaptionMap!!.getValue(postId)}"
                Log.d("[CODE]:", "THE CAPTION IS ${CaptionMap!!.getValue(postId)} ")
        }.addOnFailureListener {
                Log.d("[CODE]:", "COULDN'T ACCESS THE DATABASE")
        }


        val ref = storageReference?.child("${username}/POSTID_${postId}.jpg")
        val userProfPic = storageReference?.child("images/${username}.jpg")
        var filename = File.createTempFile("tempImg", "jpg")
        var filename2 = File.createTempFile("tempImg", "jpg")

        // SETTING THE CAPTION
        holder.tvPostUserName.text = username
//        Log.d("[CODE]:", "THE CAPTION BOOLEAN VALUE IS ${CaptionMapBoolean} ")

        // CHECKING WHETHER THE USER ALREADY LIKED THE POST OR NOT AND SETTING THE LIKE BACKGROUND
        databaseToCapnLike
            .child(username)
            .child("likesMap")
            .child("${postId}").get().addOnSuccessListener {
                if(it.value != null){
                    var likedUsers:MutableList<HashMap<String, String>> = it.value as MutableList<HashMap<String, String>>
                    Log.d("[CODE]:", "${likedUsers.size}")
//                    holder.tvLikesCount.text = "${likedUsers.size}"
                    for(user in likedUsers){
                        if(user["username"] == activeUser){
                            holder.ivUserPostLike.setBackgroundResource(R.drawable.ic_baseline_liked)
                        }else{
                            continue
                        }
                    }
                }else{
//                    holder.tvLikesCount.text = "0"
                    holder.ivUserPostLike.setBackgroundResource(R.drawable.ic_baseline_recommend_24)
                }
            }


        // SETTING THE PICTURE
        ref?.getFile(filename)
            ?.addOnSuccessListener{
                bitmap = BitmapFactory.decodeFile(filename.absolutePath)
                holder.ivUserSPost.setImageBitmap(bitmap)
            }
        userProfPic?.getFile(filename2)
            ?.addOnSuccessListener {
                profileBitmap = BitmapFactory.decodeFile(filename2.absolutePath)
                holder.civUserPostProfilePic.setImageBitmap(profileBitmap)
            }

        // SETTING THE LIKES COUNT
//        val postLikesListener = object: ValueEventListener{
//            override fun onDataChange(it: DataSnapshot) {
//                if(it.value != null){
//                    var likedUsers:MutableList<HashMap<String, String>> = it.value as MutableList<HashMap<String, String>>
//                    //Log.d("[CODE]:", "${likedUsers.size}")
//                    holder.tvLikesCount.text = "${likedUsers.size}"
//                }else{
//                    holder.tvLikesCount.text = "0"
//                    //holder.ivUserPostLike.setBackgroundResource(R.drawable.ic_baseline_recommend_24)
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//        val postLikeRef = databaseToCapnLike.child(username).child("likesMap").child("${postId}")
//        postLikeRef.addListenerForSingleValueEvent(postLikesListener)



        // SETTING LIKE FUNCTIONALITY
        holder.ivUserSPost.setOnClickListener {
            holder.ivUserPostLike.setBackgroundResource(R.drawable.ic_baseline_liked)

            databaseToCapnLike
                .child(username).child("likesMap")
                .child("${postId}").get().addOnSuccessListener {
                Log.d("[CODE]:", "GOT ACCESS TO ${postId}('s) LIKES MAP!! ${it.value}")
                if(it.exists()){
                    var likedUsers:MutableList<HashMap<String, String>> = it.value as MutableList<HashMap<String, String>>
                    Log.d("[CODE]:", " ${likedUsers}")

                    var alreadyExists = false
                    for(user in likedUsers){
                        if(user["username"] == activeUser){
                            alreadyExists = true
                        }else{
                            continue
                        }
                    }
                    Log.d("[CODE]:", "Exists: ${alreadyExists}")
                    if(alreadyExists == false){
                        val newLikedUser = HashMap<String, String>()
                        newLikedUser.put("username", "${username}")
                        newLikedUser.put("userProfilePicLink", "images/${activeUser}.jpg")
                        newLikedUser.put("comment", "")
                        likedUsers.add(likedUsers.size,newLikedUser)
                        databaseToCapnLike
                            .child(username).child("likesMap")
                            .child("${postId}").setValue(likedUsers)
                    }

                }else{
                    Log.d("[CODE]:", "THE LIKES MAP IS EMPTY!!")
                    var postLikedUsers = mutableListOf<UserPostResponseModel>()
                    postLikedUsers.add(UserPostResponseModel("${activeUser}", "images/${activeUser}.jpg", ""))

                     databaseToCapnLike
                        .child(username).child("likesMap")
                        .child("${postId}").setValue(postLikedUsers)
                }
            }.addOnFailureListener {
                Log.d("[CODE!!]:","${it.message}")
            }

        }

        holder.ivUserPostLike.setOnClickListener {
            holder.ivUserPostLike.setBackgroundResource(R.drawable.ic_baseline_recommend_24)
            databaseToCapnLike
                .child(username)
                .child("likesMap")
                .child("${postId}").get().addOnSuccessListener {
                    var likedUsers:MutableList<HashMap<String, String>> = it.value as MutableList<HashMap<String, String>>
                    for(user in likedUsers){
                        if(user["username"] == activeUser){
                            likedUsers.remove(user)
                            break
                        }else{
                            continue
                        }
                    }
                    databaseToCapnLike
                        .child(username).child("likesMap")
                        .child("${postId}").setValue(likedUsers)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface LinearOnUserClickListener{
        fun LinearonUserClickListener(position: Int)
    }
}

