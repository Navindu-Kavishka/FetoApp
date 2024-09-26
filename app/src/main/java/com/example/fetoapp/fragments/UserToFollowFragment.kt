package com.example.fetoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.fetoapp.R
import com.example.fetoapp.adapters.OnFriendClicked
import com.example.fetoapp.adapters.UsersAdapter
import com.example.fetoapp.databinding.FragmentUserToFollowBinding
import com.example.fetoapp.modal.Users
import com.example.fetoapp.mvvm.ViewModel


class UserToFollowFragment : Fragment(), OnFriendClicked {

    private lateinit var adapter : UsersAdapter
    private lateinit var vm : ViewModel
    private lateinit var binding: FragmentUserToFollowBinding
    var clickedOn: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_to_follow, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)

        adapter = UsersAdapter()

        binding.backBtn.setOnClickListener {

            view.findNavController().navigate(R.id.action_userToFollowFragment_to_profileFragment)

        }

        vm.getAllUsers().observe(viewLifecycleOwner, Observer {

            adapter.setUserLIST(it)

            binding.rvFollow.adapter = adapter

        })

        adapter.setListener(this)

    }

    override fun onFriendListener(position: Int, user: Users) {

    }

}