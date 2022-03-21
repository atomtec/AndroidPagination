package com.f11.githubusersearch.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import com.f11.githubusersearch.data.source.UserDataSource
import com.f11.githubusersearch.data.source.remote.UserPagingSource
import com.f11.githubusersearch.data.source.remote.api.GitHubAPIService.Companion.NETWORK_PAGE_SIZE
import com.f11.githubusersearch.data.source.remote.asUserDetailAppModel


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor (
    private val remoteDataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : UserRepository {


    override fun getUserResultStream(): Flow<PagingData<AppUser>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { UserPagingSource(remoteDataSource) }
        ).flow
    }

    override suspend fun getUserDetails(userName: String): SearchResult<AppUserDetails>
    = withContext(ioDispatcher){
        try {
            val data = remoteDataSource.getUserDetails(userName).asUserDetailAppModel()
            SearchResult.Success(data)
        }catch (ex: Exception){
            SearchResult.Error(ex)
        }

    }
}

