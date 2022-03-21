package com.f11.githubusersearch.di


import com.f11.githubusersearch.ui.user.UserViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
        //ContextModule::class
    ]
)
interface AppComponent {
    fun viewModelsFactory(): UserViewModelFactory
}
