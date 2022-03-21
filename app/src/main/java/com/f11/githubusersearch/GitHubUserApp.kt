package com.f11.githubusersearch

import android.app.Application
import com.f11.githubusersearch.di.AppComponent
import com.f11.githubusersearch.di.DaggerAppComponent


class GitHubUserApp : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.builder().build()
    }


}