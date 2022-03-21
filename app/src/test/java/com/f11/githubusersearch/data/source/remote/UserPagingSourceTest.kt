package com.f11.githubusersearch.data.source.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.f11.githubusersearch.TestData
import com.f11.githubusersearch.data.UserRepository
import com.f11.githubusersearch.data.domain.AppUser
import com.f11.githubusersearch.data.domain.SearchResult
import com.f11.githubusersearch.data.source.UserDataSource
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserPagingSourceTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var userDataSource: UserDataSource

    lateinit var userPagingSource: UserPagingSource


    @Before
    fun setup() {
        // 1
        Dispatchers.setMain(testDispatcher)
        userPagingSource = UserPagingSource(userDataSource)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `user paging source load failure http error`() = runBlockingTest {
        //setup
        val error = HttpException(Response.error<ResponseBody>(403
            , "some content".toResponseBody("plain/text".toMediaTypeOrNull())
        ))
        Mockito.doThrow(error)
            .`when`(userDataSource)
            .getAllUsers(anyString(),anyInt(),anyInt())

        //trigger
        val expectedResult = PagingSource.LoadResult.Error<Int, AppUser>(error)

        //assert
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `user paging source load refresh success`() = runBlockingTest {
        //setup
        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .getAllUsers(anyString(),anyInt(),anyInt())

        //trigger
       val expectedResult = PagingSource.LoadResult.Page(
        data = users.asUserAppModel(),
        prevKey = null,
        nextKey = 2
    )

        //assert
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 30,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `user paging source load prepend success`() = runBlockingTest {
        //setup
        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .getAllUsers(anyString(),anyInt(),anyInt())

        //trigger
        val expectedResult = PagingSource.LoadResult.Page(
            data = users.asUserAppModel(),
            prevKey = null,
            nextKey = 2
        )

        //assert
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 1,//Key will not be zero it will either be one or more or null
                    loadSize = 30,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `user paging source load append success`() = runBlockingTest {
        //setup
        val users = TestData.getRemoteListOfUsers()

        Mockito.doReturn(users)
            .`when`(userDataSource)
            .getAllUsers(anyString(),anyInt(),anyInt())

        //trigger
        val expectedResult = PagingSource.LoadResult.Page(
            data = users.asUserAppModel(),
            prevKey = 1,
            nextKey = 3
        )

        //assert
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 2,
                    loadSize = 30,
                    placeholdersEnabled = false
                )
            )
        )
    }
}
