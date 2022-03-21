package com.f11.githubusersearch.data.source.remote

import com.f11.githubusersearch.data.source.UserDataSource
import com.f11.githubusersearch.data.source.remote.api.GitHubAPIService
import javax.inject.Inject


/**
 * Ideally  split the paging and remote data source
 */
class  UserRemoteDatasource @Inject constructor(val gitHubApiService: GitHubAPIService):
    UserDataSource {

    override suspend fun getAllUsers(apiQuery:String,position:Int,loadSize:Int): List<RemoteUser> {
       val remotePostsResponse : RemoteUserResponse =
           gitHubApiService.getUsers(apiQuery, position, loadSize)
       return remotePostsResponse.items
    }

    override suspend fun getUserDetails(userName: String): RemoteUserDetails {
        return gitHubApiService.getUserDetail(userName)
    }

}
