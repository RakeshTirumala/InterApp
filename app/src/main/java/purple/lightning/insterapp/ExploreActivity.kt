package purple.lightning.insterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import purple.lightning.insterapp.databinding.ActivityExploreBinding


class ExploreActivity : AppCompatActivity(), LinearUserPostsAdapter.LinearOnUserClickListener {
    private var binding: ActivityExploreBinding?= null
    private lateinit var user: LoggedInUsersEntity
    private var postsList: List<String>? = null
    private var databaseToAllPosts = FirebaseDatabase
        .getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/")
        .getReference("AllPosts")
    private var map:MutableMap<String, String> = mutableMapOf()
    private var postIdList: List<String>? = null
    private var usernames: List<String>?=null
    private var users : List<String>? = null
    private var databaseToUsers = FirebaseDatabase
        .getInstance("https://insterapp-ac6ef-default-rtdb.firebaseio.com/")
        .getReference("Users")
    private var usersAdapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        /*FETCHING LOGGEDIN USER FROM LOCAL DATABASE*/
        fetchLoggedInUser()

//      RANKING THE POSTS
        databaseToAllPosts.get().addOnSuccessListener {
            map = it.value as MutableMap<String, String>
            Log.d("[CODE]:",  "Map ${map}")
            postIdList = map!!.keys.toList()
            usernames = map!!.values.toList()
            Log.d("[CODE]:", "POSTS LIST ${postIdList!!}")
            Log.d("[CODE]:", "USERNAMES LIST ${usernames!!}")

            if(postIdList!!.isEmpty()){
                binding?.ivMoodBad?.visibility = View.VISIBLE
                binding?.tvNothing?.visibility = View.VISIBLE
            }else{

                binding?.ivMoodBad?.visibility = View.GONE
                binding?.tvNothing?.visibility = View.GONE
                setupLinearViewOfPosts(postIdList!!, usernames!!)
            }


        }.addOnFailureListener {
            Log.d("[CODE]:", "ACCESS FAILED!")
        }

        // SEARCH USERS

        //FETCHING USERS
        databaseToUsers.get().addOnSuccessListener {
            var temp = it.value as MutableMap<String, MutableMap<Any, Any>>
            users = temp.keys.toList()
            Log.d("[CODE]:", "THE USERS ARE: ${users!!}")
//
//            Log.d("[CODE]:", "INITIALIZING THE ADAPATER")
//            usersAdapter= ArrayAdapter(
//                this, android.R.layout.simple_list_item_1, users!!)
//
//            binding?.lvUsers?.adapter = usersAdapter
//            Log.d("[CODE]:", "INITIALIZED THE ADAPATER")


        }.addOnFailureListener {
            Log.d("[CODE]:", "ACCESS FAILED!")
        }

        Log.d("[CODE]:", "INSIDE THE SETONQUERYLISTENER")
        binding?.svSearch?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                usersAdapter= ArrayAdapter(
//                    this@ExploreActivity, android.R.layout.simple_list_item_1, users!!)
//                binding?.lvUsers?.adapter = usersAdapter

//                binding?.svSearch?.clearFocus()
                if(users!!.contains(query)){
                    usersAdapter!!.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding?.lvUsers?.visibility = View.VISIBLE
                binding?.rvLinearExploreActivityPosts?.visibility = View.GONE

                usersAdapter= ArrayAdapter(
                    this@ExploreActivity, android.R.layout.simple_list_item_1, users!!)
                usersAdapter!!.filter.filter(newText)
                if(newText == ""){
                    usersAdapter= ArrayAdapter(
                        this@ExploreActivity, android.R.layout.simple_list_item_1)
                }
                binding?.lvUsers?.adapter = usersAdapter
                return false
            }
        })

        binding?.svSearch?.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                binding?.rvLinearExploreActivityPosts?.visibility = View.VISIBLE
                binding?.lvUsers?.visibility = View.GONE
                return false
            }
        })

    }


    private fun setupLinearViewOfPosts(postIdList: List<String>, usernames:List<String>){
        /*USER POSTS LINEAR VIEW*/
        binding?.rvLinearExploreActivityPosts?.visibility = View.VISIBLE
        binding?.rvLinearExploreActivityPosts?.layoutManager = LinearLayoutManager(this@ExploreActivity)
        var rvLinearExploreActivityPostsAdapter = LinearUserPostsAdapter(postIdList, this, usernames, user.username)
        binding?.rvLinearExploreActivityPosts?.adapter = rvLinearExploreActivityPostsAdapter
    }

    private fun fetchLoggedInUser(){
        Log.d("[CODE]:", "INSIDE FETCH METHOD")
        val loggedInUsersDao = (application as RoomApp).db.loggedInUsersDao()
        user = loggedInUsersDao.fetchUserByLogStatus(true)
        Log.d("[CODE]:", "LOGGED IN USER ${user}")
    }


    override fun LinearonUserClickListener(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

