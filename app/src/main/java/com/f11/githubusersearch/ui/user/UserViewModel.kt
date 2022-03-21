package com.f11.githubusersearch.ui.user

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.f11.githubusersearch.data.UserRepository
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import javax.inject.Inject
import javax.inject.Provider

class UserViewModel @Inject constructor(private val userRepository: UserRepository)
    : ViewModel() {

    val pagingDataFlow: LiveData<PagingData<AppUser>> =
        userRepository.getUserResultStream().cachedIn(viewModelScope).asLiveData()

    suspend fun getSelectedUserDetails(userName: String): SearchResult<AppUserDetails> {
        return userRepository.getUserDetails(userName)
    }

}

class UserViewModelFactory @Inject constructor(
    userViewModelProvider: Provider<UserViewModel>
) : ViewModelProvider.Factory {

    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        UserViewModel::class.java to userViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}