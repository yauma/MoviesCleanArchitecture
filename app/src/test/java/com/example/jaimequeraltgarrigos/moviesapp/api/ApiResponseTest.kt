package com.example.jaimequeraltgarrigos.moviesapp.api

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ApiResponseTest {
    @Test
    fun error() {
        val error = Throwable("error")
        val apiResponse = ApiResponse.create<String>(error)
        assertSame(apiResponse.errorMessage, "error")
    }

    @Test
    fun success() {
        val apiResponse = ApiResponse.create(Response.success("success"))
        assertTrue(apiResponse is ApiSuccessResponse)
        val apiSuccessResponse = apiResponse as ApiSuccessResponse<String>
        assertSame(apiSuccessResponse.body, "success")
        val apiResponseHeaders = ApiSuccessResponse<String>("body", "header")
        assertNotNull(apiResponseHeaders)
    }

    @Test
    fun empty() {
        val apiResponseBodyNull = ApiResponse.create(Response.success(null))
        assertTrue(apiResponseBodyNull is ApiEmptyResponse)
    }

    @Test
    fun error400() {
        val errorResponse = Response.error<String>(
            400,
            ResponseBody.create(MediaType.parse("application/txt"), "blah")
        )
        val (errorMessage) = ApiResponse.create<String>(errorResponse) as ApiErrorResponse<String>
        MatcherAssert.assertThat<String>(errorMessage, CoreMatchers.`is`("blah"))
    }
}