package com.android.udrink.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.udrink.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var inputString: EditText
    private lateinit var storeButton: Button
    private lateinit var searchString: EditText
    private lateinit var searchButton: Button

    // Firestore database reference
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize views
        inputString = binding.inputString
        storeButton = binding.storeButton
        searchString = binding.searchString
        searchButton = binding.searchButton

        // Store button click listener
        storeButton.setOnClickListener {
            val str = inputString.text.toString().trim()
            if (str.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    // Using a HashMap to create a document
                    val data = hashMapOf("string" to str)

                    // Add a new document with a generated ID
                    db.collection("strings").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "String stored in Firestore", Toast.LENGTH_SHORT).show()
                            inputString.text.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to store string in Firestore", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a string to store", Toast.LENGTH_SHORT).show()
            }
        }

        // Search button click listener
        searchButton.setOnClickListener {
            val searchStr = searchString.text.toString().trim()
            if (searchStr.isNotEmpty()) {
                db.collection("strings").whereEqualTo("string", searchStr)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            Toast.makeText(requireContext(), "String found in Firestore", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "String not found in Firestore", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to search string in Firestore", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please enter a string to search", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
