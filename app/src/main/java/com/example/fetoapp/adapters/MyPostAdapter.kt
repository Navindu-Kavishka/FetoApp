package com.example.fetoapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fetoapp.R
import com.example.fetoapp.modal.Posts
import com.example.fetoapp.mvvm.ViewModel

class MyPostAdapter(private val viewModel: ViewModel) : RecyclerView.Adapter<PostHolder>() {

    private var mypostlist = listOf<Posts>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postitems, parent, false)
        return PostHolder(view)
    }

    override fun getItemCount(): Int {
        return mypostlist.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = mypostlist[position]
        Glide.with(holder.itemView.context).load(post.image).into(holder.image)

        holder.deleteBtn.setOnClickListener {
            viewModel.deletePost(post.postid!!)
        }
    }

    fun setPostList(list: List<Posts>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(mypostlist, list))
        mypostlist = list
        diffResult.dispatchUpdatesTo(this)
    }
}

class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.postImage)
    val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
}

class MyDiffCallback(
    private val oldList: List<Posts>,
    private val newList: List<Posts>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].postid == newList[newItemPosition].postid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
