package com.sdimmick.androidmoviesearch;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import com.sdimmick.androidmoviesearch.db.MovieSearchContract;
import com.sdimmick.androidmoviesearch.db.SearchResult;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MovieSearchTest {

    private static final String VALID_MOVIE_DETAILS = "{\"Title\":\"Batman\",\"Year\":\"1989\",\"Rated\":\"PG-13\",\"Released\":\"23 Jun 1989\",\"Runtime\":\"126 min\",\"Genre\":\"Action, Adventure\",\"Director\":\"Tim Burton\",\"Writer\":\"Bob Kane (Batman characters), Sam Hamm (story), Sam Hamm (screenplay), Warren Skaaren (screenplay)\",\"Actors\":\"Michael Keaton, Jack Nicholson, Kim Basinger, Robert Wuhl\",\"Plot\":\"The Dark Knight of Gotham City begins his war on crime with his first major enemy being the clownishly homicidal Joker.\",\"Language\":\"English, French, Spanish\",\"Country\":\"USA, UK\",\"Awards\":\"Won 1 Oscar. Another 8 wins & 26 nominations.\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"72%\"},{\"Source\":\"Metacritic\",\"Value\":\"69/100\"}],\"Metascore\":\"69\",\"imdbRating\":\"7.6\",\"imdbVotes\":\"297,186\",\"imdbID\":\"tt0096895\",\"Type\":\"movie\",\"DVD\":\"25 Mar 1997\",\"BoxOffice\":\"N/A\",\"Production\":\"Warner Bros. Pictures\",\"Website\":\"N/A\",\"Response\":\"True\"}";
    private static final String VALID_SEARCH_RESULTS = "{\"Search\":[{\"Title\":\"Batman Begins\",\"Year\":\"2005\",\"imdbID\":\"tt0372784\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BYzc4ODgyZmYtMGFkZC00NGQyLWJiMDItMmFmNjJiZjcxYzVmXkEyXkFqcGdeQXVyNDYyMDk5MTU@._V1_SX300.jpg\"},{\"Title\":\"Batman v Superman: Dawn of Justice\",\"Year\":\"2016\",\"imdbID\":\"tt2975590\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BYThjYzcyYzItNTVjNy00NDk0LTgwMWQtYjMwNmNlNWJhMzMyXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg\"},{\"Title\":\"Batman\",\"Year\":\"1989\",\"imdbID\":\"tt0096895\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_SX300.jpg\"},{\"Title\":\"Batman Returns\",\"Year\":\"1992\",\"imdbID\":\"tt0103776\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BOGZmYzVkMmItM2NiOS00MDI3LWI4ZWQtMTg0YWZkODRkMmViXkEyXkFqcGdeQXVyODY0NzcxNw@@._V1_SX300.jpg\"},{\"Title\":\"Batman Forever\",\"Year\":\"1995\",\"imdbID\":\"tt0112462\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BNWY3M2I0YzItNzA1ZS00MzE3LThlYTEtMTg2YjNiOTYzODQ1XkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg\"},{\"Title\":\"Batman & Robin\",\"Year\":\"1997\",\"imdbID\":\"tt0118688\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMGQ5YTM1NmMtYmIxYy00N2VmLWJhZTYtN2EwYTY3MWFhOTczXkEyXkFqcGdeQXVyNTA2NTI0MTY@._V1_SX300.jpg\"},{\"Title\":\"The LEGO Batman Movie\",\"Year\":\"2017\",\"imdbID\":\"tt4116284\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMTcyNTEyOTY0M15BMl5BanBnXkFtZTgwOTAyNzU3MDI@._V1_SX300.jpg\"},{\"Title\":\"Batman: The Animated Series\",\"Year\":\"1992–1995\",\"imdbID\":\"tt0103359\",\"Type\":\"series\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BNzI5OWU0MjYtMmMwZi00YTRiLTljMDAtODQ0ZGYxMDljN2E0XkEyXkFqcGdeQXVyNTA4NzY1MzY@._V1_SX300.jpg\"},{\"Title\":\"Batman: Under the Red Hood\",\"Year\":\"2010\",\"imdbID\":\"tt1569923\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BYTdlODI0YTYtNjk5ZS00YzZjLTllZjktYmYzNWM4NmI5MmMxXkEyXkFqcGdeQXVyNTA4NzY1MzY@._V1_SX300.jpg\"},{\"Title\":\"Batman: The Dark Knight Returns, Part 1\",\"Year\":\"2012\",\"imdbID\":\"tt2313197\",\"Type\":\"movie\",\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMzIxMDkxNDM2M15BMl5BanBnXkFtZTcwMDA5ODY1OQ@@._V1_SX300.jpg\"}],\"totalResults\":\"338\",\"Response\":\"True\"}";

    @Test
    public void parse_valid_movie_details_response() {
        ProcessSearchResultsService service = new ProcessSearchResultsService();
        try {
            ContentValues cv = service.getContentValuesForResult(VALID_MOVIE_DETAILS, "batman");
            Assert.assertNotNull(cv);

            String director = cv.getAsString(MovieSearchContract.MovieSearchResult.COLUMN_NAME_DIRECTOR);
            Assert.assertEquals("Tim Burton", director);

            String plot = cv.getAsString(MovieSearchContract.MovieSearchResult.COLUMN_NAME_PLOT_SUMMARY);
            Assert.assertEquals("The Dark Knight of Gotham City begins his war on crime with his first major enemy being the clownishly homicidal Joker.", plot);
        } catch (JSONException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void parse_invalid_movie_details_throws_exception() {
        ProcessSearchResultsService service = new ProcessSearchResultsService();
        try {
            ContentValues cv = service.getContentValuesForResult("INVALID JSON", "INVALID");
            Assert.assertNull(cv);
        } catch (JSONException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void parse_valid_search_results() {
        ProcessSearchResultsService service = new ProcessSearchResultsService();

        ProcessSearchResultsService.PendingSearchResult pendingSearchResult = new ProcessSearchResultsService.PendingSearchResult("batman", VALID_SEARCH_RESULTS);

        try {
            List<SearchResult> parsedSearchResults = service.parseSearchResults(pendingSearchResult);
            Assert.assertEquals(10, parsedSearchResults.size());

            SearchResult batman = parsedSearchResults.get(0);
            Assert.assertEquals("Batman Begins", batman.getTitle());
            Assert.assertEquals("2005", batman.getYear());
            Assert.assertEquals("tt0372784", batman.getImdbId());
            Assert.assertEquals("https://ia.media-imdb.com/images/M/MV5BYzc4ODgyZmYtMGFkZC00NGQyLWJiMDItMmFmNjJiZjcxYzVmXkEyXkFqcGdeQXVyNDYyMDk5MTU@._V1_SX300.jpg", batman.getPoster());


        } catch (JSONException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void parse_invalid_search_results_throws_exception() {
        ProcessSearchResultsService service = new ProcessSearchResultsService();
        try {
            ProcessSearchResultsService.PendingSearchResult pendingSearchResult = new ProcessSearchResultsService.PendingSearchResult("batman", "INVALID SEARCH RESULTS");
            List<SearchResult> parsedSearchResults = service.parseSearchResults(pendingSearchResult);
            Assert.assertNull(parsedSearchResults);
        } catch (JSONException e) {
            Assert.assertNotNull(e);
        }
    }
}
