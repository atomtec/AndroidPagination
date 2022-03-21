package com.f11.githubusersearch.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.f11.githubusersearch.R
import com.f11.githubusersearch.data.domain.AppUser


class UserListAdapter(private val onItemClicked: (String) -> Unit):
    PagingDataAdapter<AppUser, RecyclerView.ViewHolder>(DiffCallback){



    class AppUserViewHolder(postItem: View, onItemClicked: (Int) -> Unit):RecyclerView.ViewHolder(postItem){
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val coverImage = itemView.findViewById<ImageView>(R.id.media_image)


        fun bind(user: AppUser){
            title.text = user.userName
            user.avatarUrl?.let {
                Glide.with(this.itemView.context)
                        .load(user.avatarUrl)
                        .placeholder(R.drawable.dp_default)
                        .into(coverImage)
            }
        }

        init {
           itemView.setOnClickListener {
               onItemClicked(absoluteAdapterPosition)
            }
        }

        companion object{
            fun from(parent:ViewGroup,onItemClicked: (Int) -> Unit): AppUserViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.user_list_item,
                        parent,
                        false
                )
                return AppUserViewHolder(view,onItemClicked)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return AppUserViewHolder.from(parent) {position ->
           onItemClicked(getItem(position)!!.userName)
       }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { (holder as AppUserViewHolder).bind(it) }
    }

    /*
     * Allows the RecyclerView to determine which items have changed when the list
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<AppUser>() {
        override fun areItemsTheSame(oldItem: AppUser, newItem: AppUser): Boolean {
            return oldItem.id == newItem.id
        }


        override fun areContentsTheSame(oldItem: AppUser, newItem: AppUser): Boolean {
             return oldItem == newItem  //Auto generated equality check from data classes
        }
    }


}