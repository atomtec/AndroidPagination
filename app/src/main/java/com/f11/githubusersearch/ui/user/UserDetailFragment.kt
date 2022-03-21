package com.f11.githubusersearch.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.f11.githubusersearch.R
import com.f11.githubusersearch.Utils
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import com.f11.githubusersearch.getAppComponent
import kotlinx.coroutines.launch


class UserDetailFragment : Fragment() {

    private val args: UserDetailFragmentArgs by navArgs()

    private val viewModel: UserViewModel by activityViewModels{
        getAppComponent().viewModelsFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.user_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        if(Utils.isNetworkAvailable(requireContext())) {
            lifecycleScope.launch {
                val searchResult = viewModel.getSelectedUserDetails(args.userName)
                progressBar.isVisible = false
                when (searchResult) {
                    is SearchResult.Success -> bindResult(searchResult.data, view)
                    is SearchResult.Error -> displayError(searchResult.exception)
                }

            }
            progressBar.isVisible = true
        }else{
            Utils.showNetworkError(requireContext())
        }
    }

    private fun displayError(exception: Throwable) {
        Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun bindResult(userDetails: AppUserDetails, view: View){
        view.findViewById<TextView>(R.id.tv_name).text = userDetails.name
        val profileImage = view.findViewById<ImageView>(R.id.iv_profile)
        Glide.with(profileImage.context)
            .load(userDetails.avatarUrl)
            .circleCrop()
            .placeholder(R.drawable.dp_default)
            .into(profileImage)
        userDetails.email?.let {
            view.findViewById<TextView>(R.id.tv_email).text = it
        }
        userDetails.twitter?.let {
            view.findViewById<TextView>(R.id.tv_twitter).text = it
        }
        userDetails.bio?.let {
            view.findViewById<TextView>(R.id.tv_bio).text = it
        }
        userDetails.organisation?.let {
            view.findViewById<TextView>(R.id.tv_organisation).text = it
        }
        userDetails.location?.let {
            view.findViewById<TextView>(R.id.tv_address).text = it
        }
        view.findViewById<TextView>(R.id.tv_followers).text = userDetails.followers.toString()
        view.findViewById<TextView>(R.id.tv_following).text = userDetails.following.toString()
    }
    
}