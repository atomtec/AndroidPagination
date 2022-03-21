package com.f11.githubusersearch.data

import androidx.paging.PagingData
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import kotlinx.coroutines.flow.Flow


/*
 * Interface for data layer
 */
interface UserRepository {
    fun getUserResultStream(): Flow<PagingData<AppUser>>
    suspend fun getUserDetails(userName: String): SearchResult<AppUserDetails>
}