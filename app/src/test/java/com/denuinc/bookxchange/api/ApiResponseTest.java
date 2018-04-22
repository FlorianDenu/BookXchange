package com.denuinc.bookxchange.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class ApiResponseTest {

    @Test
    public void exception() {
        Exception exception = new Exception("test");
        ApiResponse<String> apiResponse = new ApiResponse<>(exception);
        Assert.assertEquals(apiResponse.errorMessage, "test");
        Assert.assertEquals(apiResponse.body, null);
        Assert.assertEquals(apiResponse.code, 500);
    }

    @Test
    public void success() {
        ApiResponse<String> apiResponse = new ApiResponse<String>(Response.success("test"));
        Assert.assertEquals(apiResponse.errorMessage, null);
        Assert.assertEquals(apiResponse.body, "test");
        Assert.assertEquals(apiResponse.code, 200);
    }

    @Test
    public void error() {
        ApiResponse<String> response = new ApiResponse<String>(Response.error(400, ResponseBody.create(MediaType.parse("application/txt"), "test")));
        Assert.assertEquals(response.code, 400);
        Assert.assertEquals(response.errorMessage, "test");
    }
}
