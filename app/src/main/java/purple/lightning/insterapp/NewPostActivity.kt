package purple.lightning.insterapp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import purple.lightning.insterapp.databinding.ActivityNewPostBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
class NewPostActivity : AppCompatActivity() {
    private var binding: ActivityNewPostBinding? = null
    private var imageUri: Uri? = null
    private var storage = FirebaseStorage.getInstance()
    private var database = FirebaseDatabase.getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/").getReference("IndividualUserPosts")
    private var database2 = FirebaseDatabase.getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/").getReference("Users")
    private var dbToAllPosts = FirebaseDatabase.getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/").getReference("AllPosts")
    private var user: LoggedInUsersEntity? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // SETTING THE  CUSTOM TOOLBAR
        setSupportActionBar(binding?.toolbarNewPost)
        supportActionBar!!.title = "New Post"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //TOOLBAR BACK BUTTON
        binding?.toolbarNewPost?.setNavigationOnClickListener {
            onBackPressed()
        }

        // ADD POST TO THE IMAGEVIEW
        binding?.ivNewPost?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        // POST BUTTON FUNCTIONALITY
        binding?.btnPost?.setOnClickListener {
            PostTask1()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun PostTask1(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Posting...\uD83D\uDE80 \uD83E\uDDD1\u200D\uD83D\uDE80")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val loggedInUsersDao = (application as RoomApp).db.loggedInUsersDao()
        lifecycleScope.launch {
            user = loggedInUsersDao.fetchUserByLogStatus(true)
        }

        val storageReference = storage.reference
        val postId = UUID.randomUUID().toString()
        val ref = storageReference?.child("${user!!.username}/" + "POSTID_${postId}" + ".jpg")

        val caption: String = binding?.etCaption!!.text.toString()

        ref?.putFile(imageUri!!)
            ?.addOnSuccessListener {taskSnapshot->
                Log.d("[CODE]:", "POST IS UPLOADED")
                Log.d("[CODE]:", "CALLING POSTTASK2()")
                PostTask2( caption, user!!.username, progressDialog, postId)
            }
            ?.addOnFailureListener{
                Log.d("[CODE]:", "${it.message}")
            }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun PostTask2(caption:String, username:String, progressDialog: ProgressDialog, postId:String){

        Log.d("[CODE]:", "INSIDE POST TASK2()")
        database.child(username).get().addOnSuccessListener {
            if(it.exists()){
                Log.d("[CODE]:", "INSIDE IF CONDITION")
                database.child(username).child("captionMap").updateChildren(mutableMapOf(postId to caption) as Map<String, Any>)

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
                val formatted = current.format(formatter)

                database2.child(username).child("posts").updateChildren(mutableMapOf(postId to formatted.toString()) as Map<String, Any>)
                    .addOnSuccessListener {
                    Log.d("[CODE]:", "USER POST'S MAP IS UPDATED")
                }
                    .addOnFailureListener {
                        Log.d("[CODE]:", "FAILED TO UPDATE USER POST'S MAP")
                    }

                progressDialog.dismiss()
                finish()

            }else{
                Log.d("[CODE]:", "INSIDE ELSE CONDITION")
                var captionMap = mutableMapOf(postId to caption)
                var commentsList: List<UserPostResponseModel> = listOf()
                var likesList: List<UserPostResponseModel> = listOf()
                var commentsMap = mutableMapOf(postId to commentsList)
                var likesMap = mutableMapOf(postId to likesList)
                Log.d("[CODE]:", "MUTABLE MAPS ARE CREATED")
                var newUpload = PostModel(username=username, captionMap=captionMap, commentsMap=commentsMap, likesMap=likesMap)
                Log.d("[CODE]:", "MODEL IS CREATED")
                Log.d("[CODE]:", "${newUpload}")
                database.child(username).setValue(newUpload).addOnSuccessListener {
                    Log.d("[CODE]:", "NEW UPLOAD SUCCESSFUL!!")
                }.addOnFailureListener {
                    Log.d("[CODE]:","${it.message}")
                }

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")
                val formatted = current.format(formatter)
                database2.child(username).child("posts").updateChildren(mutableMapOf(postId to formatted.toString()) as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("[CODE]:", "USER POST'S MAP IS UPDATED")
                    }
                    .addOnFailureListener {
                        Log.d("[CODE]:", "FAILED TO UPDATE USER POST'S MAP")
                    }
                progressDialog.dismiss()
                finish()
            }

            dbToAllPosts.child(postId).setValue(user!!.username)
                .addOnSuccessListener {
                    Log.d("[CODE]:", "SUCCESSFULLY UPDATED ALLPOSTS")
                }
                .addOnFailureListener {
                    Log.d("[CODE]:", "FAILED TO UPDATE ALLPOSTS")
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
            Log.d("NewPostActivity", "photo was selected")
            imageUri = data?.data!!
            Log.d("updated Image uri:", "${imageUri}")
            binding?.ivNewPost!!.setImageURI(imageUri)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}