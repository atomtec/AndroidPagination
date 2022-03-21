package com.f11.githubusersearch.data.source.remote

import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails


/*
 *This file consists of all remote data
 * that will be transmuted to app data
 */

data class RemoteUser (
        val id:Long,
        val login : String,
        val avatar_url: String,
        val gravatar_id : String,
        val url : String,
        val html_url : String,
        val followers_url : String,
        val following_url : String,
        val gists_url : String,
        val subscriptions_url : String,
        val organizations_url : String,
        val repos_url : String,
        val events_url : String,
        val received_events_url : String,
        val type : String,
        val starred_url : String


)


data class RemoteUserDetails(
        val name: String,
        val avatar_url: String?,
        val company: String?,
        val blog: String?,
        val location: String?,
        val email: String?,
        val bio: String?,
        val twitter_username: String?,
        val followers: Long,
        val following: Long,
        val created_at: String
)



data class RemoteUserResponse(
        val total_count: Long,
        val incomplete_results: Boolean,
        val items: List<RemoteUser>
)

fun RemoteUserDetails.asUserDetailAppModel() = AppUserDetails(
        name = name,
        email = email,
        twitter = twitter_username,
        bio = bio,
        location = location,
        followers = followers,
        following = following,
        organisation = company,
        avatarUrl = avatar_url
)




fun List<RemoteUser>.asUserAppModel(): List<AppUser>{
    return map{
        AppUser(
                id = it.id,
                userName = it.login,
                avatarUrl = it.avatar_url
        )
    }
}