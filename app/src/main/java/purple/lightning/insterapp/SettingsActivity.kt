package purple.lightning.insterapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import purple.lightning.insterapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), SettingsAdapter.OnUserClickListener{
    private var binding: ActivitySettingsBinding?= null
    private lateinit var user: LoggedInUsersEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // SETTING THE  CUSTOM TOOLBAR
        setSupportActionBar(binding?.toolbarSettings)
        supportActionBar!!.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //TOOLBAR BACK BUTTON
        binding?.toolbarSettings?.setNavigationOnClickListener {
            onBackPressed()
        }

        //RECYCLERVIEW
        val settingsList = Constants.getSettingsList() as List<SettingsListItemModel>
        binding?.rvSettings?.layoutManager = LinearLayoutManager(this@SettingsActivity)

        var rvSettingsAdapter = SettingsAdapter(settingsList, this)
        binding?.rvSettings?.adapter = rvSettingsAdapter

    }

    override fun onUserClickListener(position: Int) {
        when(position){
            3->{
                val loggedInUsersDao = (application as RoomApp).db.loggedInUsersDao()
                lifecycleScope.launch {
                    user = loggedInUsersDao.fetchUserByLogStatus(true)
                    loggedInUsersDao.updateLoggedInStatus(user.username, false)
                }
                finishAffinity()
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}