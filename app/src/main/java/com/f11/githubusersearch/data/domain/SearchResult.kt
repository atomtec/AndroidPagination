package com.f11.githubusersearch.data.domain

sealed class SearchResult<Value:Any>{
    data class Success<Value:Any>(val data:Value): SearchResult<Value>()
    data class Error<Value:Any>(val exception:Throwable): SearchResult<Value>()
}

