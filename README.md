# Popular Movies
-----

This is the second project of the Udacity's Android Nanodegree, that is basically an Android application that consumes the services exposed by [TMDb](https://www.themoviedb.org).\
Built using Dagger2, RxJava, MVP pattern, Retrofit and so on.

Adding your DB Movie API Key
-----
 
To add your key, you need to create a **gradle.properties** file on root folder, and 
add the following line:

API_KEY = "YOUR_API_KEY"

### Features

The application was divided in four tabs:

  * Home
    * :white_check_mark: List of the first twenty movies ordered by popularity;
    * :white_check_mark: List of the first twenty movies ordered by rating;
    * :soon: Option to see all movies ordered by popularity (infinite scroll);
    * :soon: Option to see all movies ordered by popularity (infinite scroll);
    
  * Browse
  * Cinema
  * Favorite
    * :x: List of your favorite movies
    
 * Movie detail screen   
   * :x: Option to favorite the movie;
   * :white_check_mark: Show the backdrop and poster;
   * :white_check_mark: Show the user's average rating;
   * :white_check_mark: Show the first two reviews;
   * :white_check_mark: Show all reviews;
   * :white_check_mark: Show the first two trailers;
   * :white_check_mark: Show all trailers;
   * :x: Show the cast;
   
Some screenshots:

<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1496981404.png" width="250">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1496981408.png" width="250">
\
<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1496981411.png" width="250">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://raw.github.com/luanalbineli/popularmovies/master/screenshots/Screenshot_1496981418.png" width="250">


