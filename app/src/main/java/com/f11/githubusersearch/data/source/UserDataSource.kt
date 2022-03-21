package com.f11.githubusersearch.data.source

import com.f11.githubusersearch.data.source.remote.RemoteUser
import com.f11.githubusersearch.data.source.remote.RemoteUserDetails


/*
 * Interface for DataSource
 * could be local or remote
 */
interface UserDataSource{
    suspend fun getAllUsers(apiQuery: String, position: Int, loadSize: Int): List<RemoteUser>
    suspend fun getUserDetails(userName: String): RemoteUserDetails
}