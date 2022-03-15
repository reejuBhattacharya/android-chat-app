package com.example.reejubhai

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.reejubhai.databinding.ActivitySignupBinding
import com.example.reejubhai.util_classes.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class SignupActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignupActivity"
        const val GSIGNIN_REQUEST_CODE = 0
    }

    private lateinit var binding : ActivitySignupBinding

    private var profilePhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goToLogin()
        selectProfilePhoto()
        signUp()
        googleSignin()
    }


    // go to the Login Screen for users already signed up to the database
    private fun goToLogin()
    {
        binding.alreadysignedinTextview.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    // Sign Up with email and password
    private fun signUp()
    {
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmailEdittext.text.toString()
            val password = binding.signupPasswordEdittext.text.toString()

            Log.d(TAG, "email : $email")
            Log.d(TAG, "password : $password")

            // validate the email and password values entered into the text fields    
            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this,
                    "Please fill all the details!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            // add the validated password to Firebase Authentication    
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful){
                        return@addOnCompleteListener
                    }
                    Log.d(TAG, "The user is: ${it.result?.user?.uid}")
                    
                    // starts process of adding the signed-in user to the database
                    uploadProfilePhotoToFirebase()
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message!!)
                }
        }
    }

    // helper function for uploading profile photo to database
    private fun selectProfilePhoto()
    {
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            profilePhotoUri = it
            binding.signupProfilepicButton.setImageBitmap(bitmap)
            binding.onSignupButton.text = ""
            Log.d(TAG, it.path!!)
        }

        binding.signupProfilepicButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    // main function for uploading profile photo to database. also sets in motion adding
    // user data to database after photo is uploaded to Firebase Storage.
    private fun uploadProfilePhotoToFirebase(){
        if(profilePhotoUri==null)   return

        val uid = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid")
        ref.putFile(profilePhotoUri!!).addOnSuccessListener {
            Log.d(TAG, "did it. ${it.metadata?.path}")

            // add download link of profile photo to the user database
            ref.downloadUrl.addOnSuccessListener {
                Log.d(TAG, "successfully got downloadUrl")

                // makes call to add user info to database
                saveProfileInDatabase(it.toString())
            }
        }
            .addOnFailureListener{
            Log.d(TAG, "exception: ${it.message}")
        }
    }

    // add user object to firebase database
    private fun saveProfileInDatabase(uri: String) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        if(uri=="") return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val username = binding.signupUsernameEdittext.text.toString().trim()

        val user = User(uid, username, uri)
        ref.setValue(user).addOnSuccessListener {
            Log.d(TAG, "successfully added user")

            //make sure the back button does not return back to the Sign-In page
            val intent = Intent(this, NewestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            Log.d(TAG, "exception: ${it.message}")
        }

    }

    // implements Google Sign-In functionality
    private fun googleSignin() {
        binding.googleSigninFab.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_login_id))
                .requestEmail()
                .build()
            val signInIntent =  GoogleSignIn.getClient(this, options).signInIntent
            startActivityForResult(signInIntent, GSIGNIN_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GSIGNIN_REQUEST_CODE) {
            // get google account and pass onto Firebase Authentication for sign-in
            val googleAccount = GoogleSignIn.getSignedInAccountFromIntent(data).result ?: return
            firebaseAuthWithGoogle(googleAccount)
        }
    }

    // uses google account from Google Sign-in to sign-in using Firebase Authenticaton
    private fun firebaseAuthWithGoogle(googleAccount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "succesfully signed in with google credentials")
                    // take user details (name, profile-img) from google account to
                    // add to Firebase Database
                    addUserFromGoogle(googleAccount)
                } else {
                    Log.w(TAG, "failure while google credential signin: ${task.exception}")
                }
            }
    }

    // function to add user details from Google account to database 
    private fun addUserFromGoogle(googleAccount: GoogleSignInAccount) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        val user = User(
            uid,
            googleAccount.displayName ?: "username",
            googleAccount.photoUrl.toString()
        )
        FirebaseDatabase.getInstance().getReference("/users/$uid")
            .setValue(user).addOnSuccessListener {
                Log.d(TAG, "successfully added user through Google")
                // ensures back button does not take user back to sign-up page
                val intent = Intent(this, NewestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }.addOnFailureListener {
                Log.d(TAG, "exception: ${it.message}")
            }
    }
}