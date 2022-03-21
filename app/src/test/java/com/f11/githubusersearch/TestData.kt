package com.f11.githubusersearch

import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.source.remote.RemoteUser
import com.f11.githubusersearch.data.source.remote.RemoteUserDetails

object  TestData {
    fun getUserDetails(): AppUserDetails {
        return AppUserDetails("Test",null,null,null,
            "Hello bio",0,10,null,null)
    }

    fun getListOfUsers(): List<AppUser>{
        return listOf(AppUser(1,"Cyankahn",null),AppUser(2,"ms",null)
        , AppUser
        (3,"qwerty",null))
    }

    fun getRemoteListOfUsers(): List<RemoteUser>{
        return listOf(
            RemoteUser(1,"Test","","","",""
        ,"","","","","","","",
        "","",""),
            RemoteUser(2,"Cyankahn","","","",""
                ,"","","","","","","",
                "","",""))
    }

    fun getRemoteUserDetails():RemoteUserDetails {
        return RemoteUserDetails("test",null,null,null,null,null,
        null,null,0,0,"jk")
    }
}