@file:Suppress("DEPRECATION")

package com.example.fetoapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.fetoapp.R
import com.example.fetoapp.Utils
import com.example.fetoapp.databinding.FragmentCreatePostBinding
import com.example.fetoapp.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class CreatePostFragment : Fragment() {

    private lateinit var binding : FragmentCreatePostBinding
    private lateinit var pd : ProgressDialog
    private lateinit var vm : ViewModel
    private lateinit var storageRef : StorageReference
    private lateinit var storage : FirebaseStorage
    private var uri : Uri? = null
    private lateinit var firestore : FirebaseFirestore
    private lateinit var bitmap : Bitmap

    var postid : String = ""
    var imageUserPoster : String = ""
    var nameUserPoster : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        postid = UUID.randomUUID().toString()

        binding.lifecycleOwner = viewLifecycleOwner

        pd = ProgressDialog(requireContext())
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        vm.name.observe(viewLifecycleOwner, Observer { it ->
            nameUserPoster = it!!
        })

        vm.image.observe(viewLifecycleOwner, Observer {
            imageUserPoster = it!!
        })

        binding.imageToPost.setOnClickListener {
            addPostDialog()
        }

        binding.postBtn.setOnClickListener {
            val caption = binding.addCaption.text.toString()
            if (caption.isNotEmpty()) {
                firestore.collection("Posts").document(postid)
                    .update("caption", caption)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Caption updated successfully!", Toast.LENGTH_SHORT).show()
                        view.findNavController().navigate(R.id.action_createPostFragment_to_profileFragment)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to update caption: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Caption cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addPostDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose Your Profile Picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    takePhotoWithCamera()
                }
                options[item] == "Choose from Gallery" -> {
                    pickImageFromGallery()
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {
        val pickpictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickpictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickpictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {
        val takepictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takepictureIntent, Utils.REQUEST_IMAGE_CAPTURE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        bitmap = imageBitmap!!

        binding.imageToPost.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)

        uploadTask.addOnSuccessListener {
            val task = it.metadata?.reference?.downloadUrl
            task?.addOnSuccessListener { uri ->
                this.uri = uri
                postImage(uri)
                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postImage(uri: Uri?) {
        val likers = ArrayList<String>() // Create an empty ArrayList as the initial value

        val hashMap = hashMapOf<Any, Any>(
            "image" to uri.toString(),
            "postid" to postid,
            "userid" to Utils.getUidLogged(),
            "likers" to likers,
            "time" to Utils.getTime(),
            "caption" to "default",
            "likes" to 0,
            "username" to nameUserPoster,
            "imageposter" to imageUserPoster
        )

        firestore.collection("Posts").document(postid).set(hashMap)
            .addOnSuccessListener {
//                Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to create post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
