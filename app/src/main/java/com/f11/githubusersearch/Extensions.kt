package com.f11.githubusersearch

import androidx.fragment.app.Fragment
import com.f11.githubusersearch.di.AppComponent
import retrofit2.HttpException

fun HttpException.getRetryTime(): String?{
    return response()?.let {
        val headers = it.headers()
        Utils.convertEpoc(headers["X-RateLimit-Reset"]!!)
    }

}

fun Fragment.getAppComponent(): AppComponent =
    (requireContext().applicationContext as GitHubUserApp).appComponent