package com.example.jaimequeraltgarrigos.moviesapp.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.example.github.util.LiveDataCallAdapterFactory
import com.example.jaimequeraltgarrigos.moviesapp.util.LiveDataTestUtil
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.Assert.assertThat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: MoviesService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(MoviesService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getPopularMovies() {
        enqueueResponse("popular-movies.json")
        val popularMovies = (LiveDataTestUtil.getValue(service.getPopularMovies("",1)) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/movie/popular?api_key=&page=1"))

        assertThat(popularMovies.movies.size, CoreMatchers.`is`(2))

        val movie = popularMovies.movies[0]
        Assert.assertThat(movie.title, CoreMatchers.`is`("Suicide Squad"))

        val movie2 = popularMovies.movies[1]
        Assert.assertThat(movie2.title, CoreMatchers.`is`("Jason Bourne"))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
            .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}