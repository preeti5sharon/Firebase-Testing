package github.preeti5sharon.firebasetesting

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import github.preeti5sharon.firebasetesting.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    //to get fb auth functionality
    lateinit var auth: FirebaseAuth
    private var _binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(_binding?.root)

        auth = FirebaseAuth.getInstance()

        _binding?.btnRegister?.setOnClickListener {
            registerUser()
        }
        _binding?.btnLogin?.setOnClickListener {
            loginUser()
        }
        _binding?.btnUpdateProfile?.setOnClickListener {
            updateProfile()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun updateProfile() {
        auth.currentUser?.let { user ->
            val username = _binding?.etUsername?.text.toString()
            val photoURI =
                Uri.parse("android.resource://$packageName/${R.drawable.ic_channel_foreground}")
            val profileUpdate =
                UserProfileChangeRequest.Builder().setDisplayName(username).setPhotoUri(photoURI)
                    .build()


            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdate).await()
                    withContext(Dispatchers.Main) {
                        checkLoggedInState()
                        Toast.makeText(
                            this@MainActivity,
                            "Successfully Updated user profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


        private fun registerUser() {
            val email = _binding?.etEmailRegister?.text.toString()
            val password = _binding?.etPasswordRegister?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.createUserWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            checkLoggedInState()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        private fun loginUser() {
            val email = _binding?.etEmailLogin?.text.toString()
            val password = _binding?.etPasswordLogin?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        auth.signInWithEmailAndPassword(email, password).await()
                        withContext(Dispatchers.Main) {
                            checkLoggedInState()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        private fun checkLoggedInState() {
            val user = auth.currentUser
            if (user == null) {
                _binding?.tvLoggedIn?.text = "You are not logged in"
            } else {
                _binding?.tvLoggedIn?.text = "You are logged in"
                _binding?.etUsername?.setText(user.displayName)
                _binding?.imageView?.setImageURI(user.photoUrl)
            }
        }
    }