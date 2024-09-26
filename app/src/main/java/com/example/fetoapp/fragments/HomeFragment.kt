package com.example.fetoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.fetoapp.R
import com.example.fetoapp.Utils
import com.example.fetoapp.adapters.MyFeedAdapter
import com.example.fetoapp.adapters.onDoubleTapClickListener
import com.example.fetoapp.databinding.FragmentHomeBinding
import com.example.fetoapp.modal.Feed
import com.example.fetoapp.mvvm.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment(), onDoubleTapClickListener {



    private lateinit var vm : ViewModel
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter : MyFeedAdapter

    private var postid : String = ""


    private var userwholiked : String = ""

    private var idofpostowner : String = ""




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil. inflate(inflater,R.layout.fragment_home, container, false)

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)
        adapter = MyFeedAdapter()


        binding.lifecycleOwner = viewLifecycleOwner

        vm.loadMyFeed().observe(viewLifecycleOwner, Observer {


            adapter.setFeedList(it)
            binding.feedRecycler.adapter = adapter


        })

        adapter.setListener(this)


        binding.imageViewBottom.setOnClickListener {

            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)

        }

        vm.image.observe(viewLifecycleOwner, Observer {

            Glide.with(requireContext()).load(it).into(binding.imageViewBottom)


        })


    }





    override fun onDoubleTap(feed: Feed) {

        val currentuserid = Utils.getUidLogged()
        val postId = feed.postid

        val  firestore = FirebaseFirestore.getInstance()

        val postRef = firestore.collection("Posts").document(postId!!)


        postRef.get().addOnSuccessListener { document->

            if (document!=null && document.exists() ){


                val likes = document.getLong("likes")?.toInt()?:0
                val likers = document.get("likers") as? List<String>


                //not allow to relike
                if (!likers!!.isEmpty() && likers.contains(currentuserid)){

                    Toast.makeText(requireContext(), "You Liked already...", Toast.LENGTH_SHORT).show()


                } else {


                    postRef.update("likes",likes+1, "likers" , FieldValue.arrayUnion(currentuserid))
                        .addOnSuccessListener {

                            Toast.makeText(requireContext(),"Liked", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener{ exception->

                        println("Failed to Update Like $exception")

                    }


                }


            }

        }


    }


}