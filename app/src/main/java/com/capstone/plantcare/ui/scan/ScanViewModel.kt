package com.capstone.plantcare.ui.scan

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.plantcare.data.remote.api.ApiConfig
import com.capstone.plantcare.data.remote.response.UploadResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ScanViewModel : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _uploadResponse = MutableLiveData<UploadResponse>()
    val uploadResponse: MutableLiveData<UploadResponse> get() =_uploadResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: MutableLiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun prepareRequest(context: Context, fileUri: Uri?, plantType: String, userId: String) {
        fileUri?.let { uri ->
            val file = uriToFile(uri, context).reduceFileImage()
            val requestFile = RequestBody.create("image/*".toMediaType(), file)
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val plantTypeBody = RequestBody.create("text/plain".toMediaType(), plantType)
            val userIdBody = RequestBody.create("text/plain".toMediaType(), userId)

            val apiService = ApiConfig.getApiService()
            _isLoading.postValue(true)
            val call = apiService.predictPlant(multipartBody, plantTypeBody, userIdBody)

            call.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    _isLoading.postValue(false)
                    if (response.isSuccessful) {
                        _uploadResponse.postValue(response.body())
                    } else {
                        _errorMessage.postValue("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    Log.e("API Error", "onFailure: ${t.message}", t)
                    _errorMessage.postValue("Failure: ${t.message}")
                }
            })
        }
    }
}