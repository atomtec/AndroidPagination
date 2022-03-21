package com.f11.githubusersearch.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.f11.githubusersearch.TestData
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import com.f11.githubusersearch.data.source.UserDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DefaultRepositoryTest {


    private lateinit var userRepo: UserRepository

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var remoteDataSource: UserDataSource

    @Before
    fun setup() {
        // 1
        Dispatchers.setMain(testDispatcher)
        userRepo = DefaultRepository(remoteDataSource,testDispatcher)
    }

    @Test
    fun `Load the user detail correctly`() = runBlockingTest {
        //setup
        val remoteUserDetails = TestData.getRemoteUserDetails()
        Mockito.doReturn(remoteUserDetails)
            .`when`(remoteDataSource)
            .getUserDetails(ArgumentMatchers.anyString())

        var userDetailsFromRepo: SearchResult<AppUserDetails> = userRepo.getUserDetails("test")

        var appuserDetails: AppUserDetails? = null

        when(userDetailsFromRepo) {
            is SearchResult.Success -> appuserDetails = userDetailsFromRepo.data
        }

        //assert
        assert(userDetailsFromRepo is SearchResult.Success)
        Mockito.verify(remoteDataSource, Mockito.times(1)).getUserDetails("test")
        Assertions.assertThat(appuserDetails?.name.equals(remoteUserDetails.name))
    }

    @Test
    fun `Load the user detail error`() = runBlockingTest {
        //setup
        val error = RuntimeException("GenericException")
        Mockito.doThrow(error)
            .`when`(remoteDataSource)
            .getUserDetails(ArgumentMatchers.anyString())

        var errorFromRepo: SearchResult<AppUserDetails> = userRepo.getUserDetails("test")

        var exception: Throwable? = null

        when(errorFromRepo) {
            is SearchResult.Error -> exception = errorFromRepo.exception
        }

        //assert
        assert(errorFromRepo is SearchResult.Error)
        Mockito.verify(remoteDataSource, Mockito.times(1)).getUserDetails("test")
        Assertions.assertThat(exception?.message.equals(error.message))
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }
}