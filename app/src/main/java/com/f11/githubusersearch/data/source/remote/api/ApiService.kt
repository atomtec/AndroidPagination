package com.f11.githubusersearch.data.source.remote.api



import com.f11.githubusersearch.BuildConfig
import com.f11.githubusersearch.data.source.remote.RemoteUserDetails
import com.f11.githubusersearch.data.source.remote.RemoteUserResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query




interface GitHubAPIService {

    @GET("search/users?sort=followers")
    suspend fun getUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): RemoteUserResponse

    @GET("users/{user}")
    suspend fun getUserDetail(
        @Path("user") id: String
    ): RemoteUserDetails

    companion object {
        private const val BASE_URL = "https://api.github.com/"
        const val GITHUB_STARTING_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 30


        fun create(): GitHubAPIService {
            val logger = HttpLoggingInterceptor()
            if(BuildConfig.DEBUG)
                logger.level = HttpLoggingInterceptor.Level.BASIC
            else
                logger.level = HttpLoggingInterceptor.Level.NONE

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GitHubAPIService::class.java)
        }
    }

}