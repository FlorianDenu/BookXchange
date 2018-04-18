package com.denuinc.bookxchange.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.denuinc.bookxchange.api.utils.LiveDataTestUtils;
import com.denuinc.bookxchange.utils.LiveDataCallAdapterFactory;
import com.denuinc.bookxchange.vo.Book;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@RunWith(JUnit4.class)
public class GoogleBookServiceTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private GoogleBookService googleBookService;

    private MockWebServer mockWebServer;

    @Before
    public void createService() throws IOException {
        mockWebServer = new MockWebServer();
        googleBookService = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GoogleBookService.class);
    }

    @After
    public void stopService() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getBooks() throws IOException, InterruptedException {
        enqueueResponse("elon-search.json");
        BookSearchResponse bookSearchResponse = LiveDataTestUtils.getValue(googleBookService.searchBook("elon musk+intitle")).body;

        RecordedRequest request = mockWebServer.takeRequest();
        Assert.assertEquals(request.getPath(),"/volumes?q=elon%20musk%2Bintitle");
        Assert.assertNotEquals(bookSearchResponse, null);
        Assert.assertEquals(bookSearchResponse.getItems().get(0).volumeInfo.title, "Elon Musk");
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
