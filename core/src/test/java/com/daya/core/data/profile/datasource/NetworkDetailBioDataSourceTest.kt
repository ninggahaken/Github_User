package com.daya.core.data.profile.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.daya.core.data.profile.network.NetWorkBio
import com.daya.core.di.detail.NetWorkDetailBio
import com.daya.core.faker.FakeDataClasses
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class NetworkDetailBioDataSourceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var moshi: Moshi

    @Inject
    @NetWorkDetailBio
    lateinit var networkDetailBioDataSource : DetailBioDataSource

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getDetailBio should return list generalBio`() = runBlocking {
        val expectedResponse = FakeDataClasses.networkBio[0]
        val moshiAdapter = moshi.adapter(NetWorkBio::class.java)
        val expectedJsonResponse = moshiAdapter.toJson(expectedResponse)

        val mockResponse = MockResponse()
            .setBody(expectedJsonResponse)

        mockWebServer.enqueue(mockResponse)

        val actualResponse = networkDetailBioDataSource.getDetailBio(expectedResponse.name)

        assertThat(expectedResponse.name).isEqualTo(actualResponse.name)
        assertThat(expectedResponse.avatar_url).isEqualTo(actualResponse.avatar)
        assertThat(expectedResponse.company).isEqualTo(actualResponse.company)
        assertThat(expectedResponse.location).isEqualTo(actualResponse.location)
        assertThat(expectedResponse.public_repos).isEqualTo(actualResponse.repoCount)

    }
}