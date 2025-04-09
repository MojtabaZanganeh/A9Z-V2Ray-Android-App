package ir.a9z.v2rayconfig.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.a9z.v2rayconfig.data.api.ApiService
import ir.a9z.v2rayconfig.data.model.LastUpdateResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import ir.a9z.v2rayconfig.BuildConfig
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.net.ConnectException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {

    val baseUrl = BuildConfig.API_URL
    val apiKey = BuildConfig.API_KEY
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    private val _subLink = MutableStateFlow<String?>(null)
    val subLink: StateFlow<String?> = _subLink

    private val _config = MutableStateFlow<String?>(null)
    val config: StateFlow<String?> = _config

    private val _lastUpdate = MutableStateFlow<LastUpdateResponse?>(null)
    val lastUpdate: StateFlow<LastUpdateResponse?> = _lastUpdate

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private fun handleError(e: Exception): String {
        Log.e("MainViewModel", "Error: ${e.message}", e)
        Log.e("MainViewModel", "Error stack trace: ${e.stackTraceToString()}")
        return when (e) {
            is UnknownHostException -> "خطا در اتصال به سرور. لطفا اتصال اینترنت خود را بررسی کنید."
            is SocketTimeoutException -> "زمان اتصال به سرور به پایان رسید. لطفا دوباره تلاش کنید."
            is ConnectException -> "امکان اتصال به سرور وجود ندارد. لطفا دوباره تلاش کنید."
            is JsonSyntaxException -> {
                Log.e("MainViewModel", "JSON parsing error: ${e.message}")
                "خطا در پردازش اطلاعات دریافتی از سرور."
            }
            else -> "خطا در دریافت اطلاعات: ${e.message}"
        }
    }

    fun fetchSubLink() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = apiService.getSubLink(apiKey)
                if (response.sub.isNotEmpty()) {
                    _subLink.value = response.sub
                    fetchLastUpdate()
                } else {
                    _error.value = "پاسخ خالی از سرور دریافت شد"
                }
            } catch (e: Exception) {
                _error.value = handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchConfig() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val response = apiService.getConfig(apiKey)
                if (response.config.isNotEmpty()) {
                    _config.value = response.config[0]
                    fetchLastUpdate()
                } else {
                    _error.value = "پاسخ خالی از سرور دریافت شد"
                }
            } catch (e: Exception) {
                _error.value = handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchLastUpdate() {
        viewModelScope.launch {
            try {
                val response = apiService.getLastUpdate(apiKey)
                Log.d("MainViewModel", "LastUpdate Response: $response")
                if (response.count > 0 && response.timestamp.isNotEmpty()) {
                    _lastUpdate.value = response
                }
            } catch (e: Exception) {
                // Don't show error for last update as it's secondary information
                _lastUpdate.value = null
            }
        }
    }

    fun copyToClipboard(context: Context, text: String, successMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
    }
} 