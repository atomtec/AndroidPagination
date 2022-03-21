package com.f11.githubusersearch.di



import com.f11.githubusersearch.data.DefaultRepository
import com.f11.githubusersearch.data.UserRepository
import com.f11.githubusersearch.data.source.UserDataSource
import com.f11.githubusersearch.data.source.remote.UserRemoteDatasource
import com.f11.githubusersearch.data.source.remote.api.GitHubAPIService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideUserRemoteDataSource(
       gitHubAPIService: GitHubAPIService
    ): UserDataSource {
        return UserRemoteDatasource(gitHubAPIService)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideGitHubAPIService(): GitHubAPIService {
        return GitHubAPIService.create()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @JvmStatic
    @Singleton
    @Provides
    fun provideUserRepository(
        remoteDataSource: UserDataSource,
        ioDispatcher: CoroutineDispatcher
    ): UserRepository {
        return DefaultRepository(remoteDataSource, ioDispatcher)
    }

}

