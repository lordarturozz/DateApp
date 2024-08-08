package com.example.dateapp.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dateapp.MainActivity
import com.example.dateapp.R
import com.example.dateapp.databinding.ActivityRegisterBinding
import com.example.dateapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dialog: AlertDialog
    private var imageUri: Uri? = null
    private lateinit var countrySpinner: Spinner
    private lateinit var citySpinner: Spinner

    private val countriesAndCities = mapOf(
        "España" to listOf("Madrid", "Barcelona", "Valencia"),
        "México" to listOf("Ciudad de México", "Guadalajara", "Monterrey"),
        "Argentina" to listOf("Buenos Aires", "Córdoba", "Rosario"),
        "Estados Unidos" to listOf("Nueva York", "Los Ángeles", "Chicago")
    )

    // ActivityResultLauncher for selecting an image
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si el usuario está autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            //startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else
            startActivity(Intent(this, MainActivity::class.java))

        dialog = AlertDialog.Builder(this)
            .setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()

        // Initialize spinners
        countrySpinner = binding.countrySpinner
        citySpinner = binding.citySpinner

        // Set up spinners
        setupSpinners()

        binding.userImage.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.saveData.setOnClickListener {
            validateData()
        }
    }

    private fun setupSpinners() {
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesAndCities.keys.toList())
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = countryAdapter

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCountry = countrySpinner.selectedItem.toString()
                val cities = countriesAndCities[selectedCountry] ?: emptyList()

                val cityAdapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_spinner_item, cities)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                citySpinner.adapter = cityAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle the case when no item is selected
            }
        }
    }

    private fun validateData() {
        with(binding) {
            when {
                userName.text.toString().isEmpty() ||
                        userNumber.text.toString().isEmpty() ||
                        imageUri == null -> {
                    showToast("Por favor introduce todos los datos")
                }

                !termsCondition.isChecked -> {
                    showToast("Por favor acepta los términos y condiciones")
                }

                else -> {
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        dialog.show()
        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profile.jpg")

        storageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    storeData(uri)
                }.addOnFailureListener { exception ->
                    handleUploadFailure(exception)
                }
            }.addOnFailureListener { exception ->
                handleUploadFailure(exception)
            }
    }

    private fun handleUploadFailure(exception: Exception) {
        dialog.dismiss()
        Log.e("RegisterActivity", "Upload failed", exception)
        showToast(exception.message)
    }

    private fun storeData(imageUrl: Uri?) {
        val data = UserModel(
            name = binding.userName.text.toString(),
            image = imageUrl.toString(),
            number = binding.userNumber.text.toString(),
            city = binding.citySpinner.selectedItem.toString() // Get selected city
        )

        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid) // Use UID instead of phone number
            .setValue(data)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    showToast("Datos guardados correctamente")
                } else {
                    Log.e("RegisterActivity", "Data saving failed", task.exception)
                    showToast(task.exception?.message)
                }
            }
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
    }
}









