package com.f11.githubusersearch.ui.user

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.f11.githubusersearch.R
import com.f11.githubusersearch.Utils
import com.f11.githubusersearch.getAppComponent
import com.f11.githubusersearch.getRetryTime
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UserListFragment : Fragment() {

    private lateinit var listView : RecyclerView
    private val listAdapter = UserListAdapter { name ->
        val action = UserListFragmentDirections.actionListFragmentToDetailFragment(name)
        findNavController().navigate(action)
    }
    private lateinit var retryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyList: TextView

    private val viewModel: UserViewModel by activityViewModels{
        getAppComponent().viewModelsFactory()
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = requireActivity().layoutInflater.inflate(R.layout.user_list_fragment, null)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setListToTop()
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.list_view)
        progressBar = view.findViewById(R.id.progress_bar)
        retryButton = view.findViewById(R.id.retry_button)
        emptyList = view.findViewById(R.id.emptyList)
        retryButton.setOnClickListener {
            if(Utils.isNetworkAvailable(requireContext())) {
                listAdapter.retry()
            }
            else{
                Utils.showNetworkError(requireContext())
            }
        }
        val swipeView = view.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        swipeView.setDistanceToTriggerSync(SwipeRefreshLayout.LARGE)
        swipeView.setOnRefreshListener {
            swipeView.isRefreshing = false
            listAdapter.refresh() //Launches  new paging
        }
        listView.adapter = listAdapter.withLoadStateFooter(
            footer = UserLoadStateAdapter { listAdapter.retry() }
        )

        listView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        viewModel.pagingDataFlow.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                listAdapter.submitData(it)
                progressBar.visibility = View.GONE //Let subscriber count load without progress bar
            }
        })
        listAdapter.loadStateFlow.asLiveData().observe(viewLifecycleOwner, Observer {
                loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && listAdapter.itemCount == 0
            // show empty list
            emptyList.isVisible = isListEmpty
            // Only show the list if refresh succeeds.
            listView.isVisible = !isListEmpty
            // Show loading spinner during initial load or refresh.
            progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            retryButton.isVisible = loadState.source.refresh is LoadState.Error




            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                val ex = errorState.error
                if (ex is HttpException && ex.code() == 403) {
                    ex.getRetryTime()?.let {
                        Snackbar.make(listView,"Rate limit exceeded please try at $it ",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK",{})  // action text on the right side
                            .setActionTextColor(Color.GREEN)
                            .show();
                    }
                } else {
                    ex.let {
                        Toast.makeText(
                            requireContext(),
                            ex.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
        if(!Utils.isNetworkAvailable(requireContext())) Utils.showNetworkError(requireContext())
    }


   private fun setListToTop(){
       listView.scrollToPosition(0)
   }

}

