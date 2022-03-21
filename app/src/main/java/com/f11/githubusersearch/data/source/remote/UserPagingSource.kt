package com.f11.githubusersearch.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.source.UserDataSource
import com.f11.githubusersearch.data.source.remote.api.GitHubAPIService
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource (private val remoteDatasource: UserDataSource):
    PagingSource<Int, AppUser>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AppUser> {
        val position = params.key ?: GitHubAPIService.GITHUB_STARTING_PAGE_INDEX
        val apiQuery = "users"
        return try {
            val users = remoteDatasource.getAllUsers(apiQuery, position, params.loadSize).asUserAppModel()
            val nextKey = if (users.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / GitHubAPIService.NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = users,
                prevKey = if (position == GitHubAPIService.GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    /**
     * To force refresh from start(as per spec)  returing null
     */
    override fun getRefreshKey(state: PagingState<Int, AppUser>): Int? {
        return null
    }
}