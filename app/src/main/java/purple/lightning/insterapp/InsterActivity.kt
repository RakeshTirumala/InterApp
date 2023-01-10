package purple.lightning.insterapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import purple.lightning.insterapp.databinding.ActivityInsterBinding
import java.io.File

class InsterActivity : AppCompatActivity(), GridUserPostsAdapter.GridOnUserClickListener, LinearUserPostsAdapter.LinearOnUserClickListener  {
    private var binding: ActivityInsterBinding?= null
    private lateinit var user: LoggedInUsersEntity
    private var storage = FirebaseStorage.getInstance()
    private var bitmap: Bitmap? = null
    private var database = FirebaseDatabase.getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/").getReference("Users")
    private var postsMap: MutableMap<String, String>? = null
    private var postsList: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /*FETCHING LOGGEDIN USER FROM LOCAL DATABASE*/
        fetchLoggedInUser()


        /*INITIAL LOAD*/
        loadHomeActivity()

        // FETCHING USER PROFILE PIC FROM FIREBASE STORAGE
        val storageReference = storage.reference
        val ref = storageReference?.child("images/${user!!.username}.jpg")

        var filename = File.createTempFile("tempImg", "jpg")
        ref?.getFile(filename)
            ?.addOnSuccessListener{
                bitmap = BitmapFactory.decodeFile(filename.absolutePath)
                binding?.civAccount?.setImageBitmap(bitmap)
                binding?.civAccUserProfilePic?.setImageBitmap(bitmap)
            }
            ?.addOnFailureListener{
                Log.d("[CODE]:", "FAILED TO RETRIEVE IMAGE FROM FIREBASE")
            }

        /*FETCHING USER POSTS FROM THE DATABASE*/
        database.child(user.username).child("posts").get().addOnSuccessListener {
            // Log.d("[CODE]:", "USER POST lIST ${it.value}")
            if(it.value == null){
                binding?.tvpostsCount?.text = "0"
            }else{
                // Log.d("[CODE]:", "USER POSTS: ${it.value}")
                postsMap = it.value as MutableMap<String, String>
                Log.d("[CODE]:", "USER POSTS SIZE: ${postsMap!!.count()}")
                binding?.tvpostsCount?.text = postsMap!!.count().toString()
                postsList = postsMap!!.keys.toList()

                /*USER POSTS GRID VIEW*/
                binding?.rvGridUserPosts?.layoutManager = GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
                var rvGridUserPostsAdapter = GridUserPostsAdapter(postsList!!, this, user.username)
                binding?.rvGridUserPosts?.adapter = rvGridUserPostsAdapter

                /*USER POSTS LINEAR VIEW*/
                binding?.rvLinearUserPosts?.layoutManager = LinearLayoutManager(this@InsterActivity)
                val usernames:List<String> = MutableList(postsList!!.size) {user.username}
                var rvLinearUserPostsAdapter = LinearUserPostsAdapter(postsList!!, this, usernames, user.username)
                binding?.rvLinearUserPosts?.adapter = rvLinearUserPostsAdapter
            }
        }.addOnFailureListener {
            Log.d("[CODE]:", "ISSUE REGARDING FOLLOWERS ${it.message}")
        }



        /*ONCLICKS*/
        binding?.ibHome?.setOnClickListener {
            loadHomeActivity()
        }
        binding?.civAccount?.setOnClickListener {
            loadAccountActivity()
        }
        binding?.civsettings?.setOnClickListener {
            val intent = Intent(this@InsterActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding?.ibAddPost?.setOnClickListener {
            val intent = Intent(this@InsterActivity, NewPostActivity::class.java)
            startActivity(intent)
        }

        binding?.ibSearch?.setOnClickListener {
            val intent = Intent(this@InsterActivity, ExploreActivity::class.java)
            startActivity(intent)
        }

        binding?.iblinear?.setOnClickListener {
            setUpLinearPostsView()
        }
        binding?.ibgrid?.setOnClickListener {
            setupGridViewOfPosts()
        }

    }

    private fun fetchLoggedInUser(){
        val loggedInUsersDao = (application as RoomApp).db.loggedInUsersDao()
        user = loggedInUsersDao.fetchUserByLogStatus(true)
    }

    private fun loadHomeActivity(){
        /*HOME ACTIVITY VISIBILITY*/
        binding?.llTopNavBarHome?.visibility = View.VISIBLE

        /*ACCOUNT ACTIVITY VISIBILITY*/
        binding?.lltopNavAcc?.visibility = View.GONE
        binding?.llUser?.visibility = View.GONE
        binding?.llPostsViews?.visibility = View.GONE
        binding?.rvGridUserPosts?.visibility = View.GONE
        binding?.rvLinearUserPosts?.visibility = View.GONE
        binding?.tvNoPosts?.visibility = View.GONE

    }

    private fun loadAccountActivity(){
        /*HOME ACTIVITY & VISIBILITY*/
        binding?.llTopNavBarHome?.visibility = View.GONE
        binding?.tvusernameAcc?.text = user.username
        binding?.tvusernameAcc?.visibility = View.VISIBLE

        /*ACCOUNT ACTIVITY & VISIBILITY*/
        binding?.lltopNavAcc?.visibility = View.VISIBLE
        binding?.llUser?.visibility = View.VISIBLE
        binding?.llPostsViews?.visibility = View.VISIBLE
        binding?.rvGridUserPosts?.visibility = View.VISIBLE

        // GETTING THE USERS POST DATA FROM FIREBASE
        setupGridViewOfPosts()
    }

    private fun setupGridViewOfPosts(){
        binding?.rvLinearUserPosts?.visibility = View.INVISIBLE
        binding?.rvGridUserPosts?.visibility = View.VISIBLE
        binding?.tvNoPosts?.visibility = View.GONE

    }

    private fun setUpLinearPostsView(){
        binding?.rvLinearUserPosts?.visibility = View.VISIBLE
        binding?.rvGridUserPosts?.visibility = View.INVISIBLE
        binding?.tvNoPosts?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun GridonUserClickListener(position: Int) {
        TODO("Not yet implemented")
    }

    override fun LinearonUserClickListener(position: Int) {
        TODO("Not yet implemented")
    }


}