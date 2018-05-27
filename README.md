# Popular Movies

This is the second project of the Udacity's Android Nanodegree, that is basically an Android application that consumes the services exposed by [TMDb](https://www.themoviedb.org).

### Patters, libraries and technology:
  * MVP
  * Dagger 2.x
  * Kotlin
  * RxJava
  * Retrofit
  * And so on

##### Why architecture patterns?
Split the responsabilities. When you follow an architecture pattern,
I's easier to understand the flow of information, the responsabilities of each class/object, to maintain the code and test.


### Features

The application was divided into four tabs:

  * :white_check_mark: Home - The default tab of the application
    * :white_check_mark: List of the first twenty movies ordered by popularity;
    * :white_check_mark: List of the first twenty movies ordered by rating;
    * :white_check_mark: Option to see all movies ordered by popularity (infinite scroll);
    * :white_check_mark: Option to see all movies ordered by rating (infinite scroll);

  * :white_check_mark: Browse - Tab to the user search for new movies
    * :soon: Filter by genre
    * :x: More filters...

  * :white_check_mark: Cinema
    * :white_check_mark: List of the movies in theaters
    * :soon: Show the upcoming movies

  * :white_check_mark: Favorite
    * :white_check_mark: List your favorite movies
    * :soon: Option to select the sorted by (added order, release date)

 * :white_check_mark: Movie detail screen
   * :white_check_mark: Show the backdrop and poster;
   * :white_check_mark: Show the user's average rating;
   * :white_check_mark: Show the first two reviews;
   * :white_check_mark: Show all reviews;
   * :white_check_mark: Show the first two trailers;
   * :white_check_mark: Show all trailers;
   * :white_check_mark: Option to favorite the movie;
   * :white_check_mark: Show the cast;
   * :white_check_mark: Show recommendations (of movies, based on the movie you are seeing);
   
### Building yourself

To build/run this project, you I'll need [Android Studio 3.0](https://developer.android.com/studio/index.html).
Besides, you need to create a [TMDb account](https://www.themoviedb.org/account/signup) and get an API key.
Then, with your API key in hands, just create a `gradle.properties` file on root folder, and add the following line:

API_KEY = "YOUR_API_KEY"                                              
                                              
   
Some screenshots:

<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1514071118.png" width="250">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1514071175.png" width="250">
\
<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1514071183.png" width="250">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1514083364.png" width="250">
\
<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1514083379.png" width="250">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_20180526-234522.png" width="250">