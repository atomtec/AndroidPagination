# GithubUser Search

An Android App that lists the github users sorted by most followers

![GitHubUser Search](demo/demo.gif)

## Resources

[GitHub API](https://docs.github.com/en/rest/reference/search)


## Building

1. Clone the git repository
2. Build  ./gradlew assembleDebug
3. Install ./gradlew installDebug


##  Task

Create an Android application that fetches the users sorted by the most followed ones
(GitHub Users API https://docs.github.com/en/rest/reference/search) and display the result as
a list


## Architecture 

This app uses the Google's [Paging3]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview
library to implement the paginated list with only **remote data source**

The architecture is a  standard **MVVM** with UI observing the live data for the main list as 
received from the ViewModel -> Repo->PagingSource->RemoteDataSource  .

The details view just calls the user api for every user via the Viewmodel and is "suspending" sequential in nature

The UI is made up of two fragments the List and Detail and is clubbed together using the **Navigation Component**' 

Every layer is (Repository , API ) is preceded by an interface for  separation and mocking.

Dependency is managed using **Dagger2**

### Rate Limit handling 

The Github API is is rate limited and when the limit is crossed it it gives a 403 response 
along with the time required for limit to be reset .
In this app whenever a rate limit is hit a snackbar shows when the user can retry to fetch the data 
and a footer with retry button is shown

### Pull To Refresh
Pull to refresh has been added and launches a new new pagination from start .


## Final Thoughts 
I thoroughly enjoyed this cleverly designed  exercise .
It was a great learning experience and that alone makes this worthwhile.