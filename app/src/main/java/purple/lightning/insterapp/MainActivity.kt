package purple.lightning.insterapp

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import purple.lightning.insterapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding?= null
    private var user: LoggedInUsersEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val animDrawable = binding?.clMainActivity?.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(1000)
        animDrawable.start()

        val timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Timer","to start next activity")
            }
            override fun onFinish() {
                val loggedInUsersDao = (application as RoomApp).db.loggedInUsersDao()
                lifecycleScope.launch {
                    user = loggedInUsersDao.fetchUserByLogStatus(true)
                }
                if(user == null) gotoLoginActivity() else gotoHomeActivity()

            }
        }
        timer.start()
    }

    private fun gotoLoginActivity(){
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun gotoHomeActivity(){
        val intent = Intent(this@MainActivity, InsterActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}