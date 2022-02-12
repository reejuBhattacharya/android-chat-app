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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class SignupActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignupActivity"
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

    }

    private fun goToLogin()
    {
        binding.alreadysignedinTextview.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUp()
    {
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmailEdittext.text.toString()
            val password = binding.signupPasswordEdittext.text.toString()

            Log.d(TAG, "email : $email")
            Log.d(TAG, "password : $password")

            if(email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this,
                    "Please fill all the details!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful){
                        return@addOnCompleteListener
                    }
                    Log.d(TAG, "The user is: ${it.result?.user?.uid}")
                    uploadProfilePhotoToFirebase()
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message!!)
                }
        }
    }

    private fun selectProfilePhoto()
    {
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            profilePhotoUri = it
//            val bitmapdrawable  = BitmapDrawable(bitmap)
//            val imageBitmap = binding.signupProfilepicButton.setImageBitmap(bitmap)
            //val compressedImage =
            binding.signupProfilepicButton.setImageBitmap(bitmap)
            binding.onSignupButton.text = ""
            Log.d(TAG, it.path!!)
        }

        binding.signupProfilepicButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun uploadProfilePhotoToFirebase(){
        if(profilePhotoUri==null)   return

        val uid = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$uid")
        ref.putFile(profilePhotoUri!!).addOnSuccessListener {
            Log.d(TAG, "did it. ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d(TAG, "successfully got downloadUrl")
                saveProfileInDatabase(it.toString())
            }
        }
            .addOnFailureListener{
            Log.d(TAG, "exception: ${it.message}")
        }
    }

    private fun saveProfileInDatabase(uri: String) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        if(uri=="") return
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val username = binding.signupUsernameEdittext.text.toString()

        val user = User(uid, username, uri)
        ref.setValue(user).addOnSuccessListener {
            Log.d(TAG, "successfully added user")
            val intent = Intent(this, NewestMessagesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }.addOnFailureListener {
            Log.d(TAG, "exception: ${it.message}")
        }

    }
}