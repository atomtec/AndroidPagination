package com.f11.githubusersearch.ui.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import androidx.paging.map
import com.f11.githubusersearch.TestData
import com.f11.githubusersearch.data.UserRepository
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.AppUserDetails
import com.f11.githubusersearch.data.domain.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {

    @Mock
    private lateinit var userRepo: UserRepository

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var appUserObserver: Observer<PagingData<AppUser>>


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var pagingData: PagingData<AppUser>


    @Before
    fun setup() {
        // 1
        Dispatchers.setMain(testDispatcher)
        pagingData = PagingData.from(TestData.getListOfUsers())
        val pageDataflow = flowOf(pagingData)
        Mockito.doReturn(pageDataflow)
            .`when`(userRepo)
            .getUserResultStream()
    }

    @Test
    fun `Load the user detail correctly`() = runBlockingTest {
        //setup
        val userDetails = TestData.getUserDetails()
        Mockito.doReturn(SearchResult.Success(userDetails))
            .`when`(userRepo)
            .getUserDetails(anyString())
        val viewModel = UserViewModel(userRepo)
        var expecteduserDetails:AppUserDetails? = null

        //operation
        val expectedsearchResult = viewModel.getSelectedUserDetails("test")
        when(expectedsearchResult) {
            is SearchResult.Success -> expecteduserDetails = expectedsearchResult.data
        }

        //assert
        assert(expectedsearchResult is SearchResult.Success)
        Mockito.verify(userRepo,times(1)).getUserDetails("test")
        Assertions.assertThat(userDetails.equals(expecteduserDetails))
    }

    @Test
    fun `Load the user detail error`() = runBlockingTest {
        //setup
        Mockito.doReturn(SearchResult.Error<AppUser>(Throwable("GenericException")))
            .`when`(userRepo)
            .getUserDetails(anyString())
        val viewModel = UserViewModel(userRepo)
        var error:Throwable? = null

        //operation
        val expectedsearchResult = viewModel.getSelectedUserDetails("test")
        when(expectedsearchResult) {
            is SearchResult.Error -> error = expectedsearchResult.exception
        }

        //assert
        assert(expectedsearchResult is SearchResult.Error)
        Mockito.verify(userRepo,times(1)).getUserDetails("test")
        Assertions.assertThat(error?.message.equals("GenericException"))
    }

    @Test
    fun `Observe the user list success`() = runBlockingTest {
        //setup
        val viewModel = UserViewModel(userRepo)

        //operation
       viewModel.pagingDataFlow.observeForever(appUserObserver)

        //assert
        verify(appUserObserver, times(1)).onChanged(isA(PagingData.empty<AppUser>().javaClass))
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }
}