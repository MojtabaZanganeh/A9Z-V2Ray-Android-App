package ir.a9z.v2rayconfig.data.api

import ir.a9z.v2rayconfig.data.model.ConfigResponse
import ir.a9z.v2rayconfig.data.model.LastUpdateResponse
import ir.a9z.v2rayconfig.data.model.SubResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("get-sub")
    suspend fun getSubLink(
        @Header("Api-Key") apiKey: String
    ): SubResponse

    @GET("get-config")
    suspend fun getConfig(
        @Header("Api-Key") apiKey: String
    ): ConfigResponse

    @GET("last-update")
    suspend fun getLastUpdate(
        @Header("Api-Key") apiKey: String
    ): LastUpdateResponse
} 