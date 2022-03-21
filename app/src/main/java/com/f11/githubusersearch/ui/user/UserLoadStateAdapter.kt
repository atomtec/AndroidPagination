package com.f11.githubusersearch.ui.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.f11.githubusersearch.R


class UserLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<UserLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: UserLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): UserLoadStateViewHolder {
        return UserLoadStateViewHolder.create(parent, retry)
    }
}

class UserLoadStateViewHolder(
    postItem: View,
    retry: () -> Unit
) : RecyclerView.ViewHolder(postItem) {
    private val errorMsg = itemView.findViewById<TextView>(R.id.error_msg)
    private val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar)
    private val retryButton = itemView.findViewById<Button>(R.id.retry_button)



    init {
        retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): UserLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_load_state_footer_view_item, parent, false)
            return UserLoadStateViewHolder(view, retry)
        }
    }
}
