package com.capstone.plantcare.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.plantcare.data.remote.api.ApiConfig
import com.capstone.plantcare.data.remote.response.HistoryResponseItem
import com.capstone.plantcare.data.remote.response.NewsItem
import com.capstone.plantcare.data.remote.response.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _historyList = MutableLiveData<List<HistoryResponseItem>>()
    val historyList: LiveData<List<HistoryResponseItem>> get() = _historyList

    private val _newsList = MutableLiveData<List<NewsItem>>()
    val newsList: LiveData<List<NewsItem>> get() = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isLoading2 = MutableLiveData<Boolean>()
    val isLoading2: LiveData<Boolean> = _isLoading2

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchRecentData(userId: String) {
        _isLoading.value = true
        ApiConfig.getApiService().getData(userId).enqueue(object :
            Callback<List<HistoryResponseItem>> {
            override fun onResponse(
                call: Call<List<HistoryResponseItem>>,
                response: Response<List<HistoryResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val historyList = response.body()
                    if (historyList != null && historyList.isNotEmpty()) {
                        _historyList.postValue(historyList!!)
                    } else {
                        _errorMessage.postValue("Tidak ada data history untuk user ini.")
                    }
                } else {
                    _errorMessage.postValue("Gagal memuat data history")
                }
            }

            override fun onFailure(call: Call<List<HistoryResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.postValue("Terjadi kesalahan: ${t.message}")
            }
        })
    }

    fun fetchNewsData() {
        _isLoading2.value = true
        ApiConfig.getApiService().getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                _isLoading2.value = false
                if (response.isSuccessful) {
                    val newsList = response.body()?.news?.filterNotNull()
                    if (newsList != null && newsList.isNotEmpty()) {
                        _newsList.postValue(newsList!!)
                    } else {
                        _errorMessage.postValue("Tidak ada berita saat ini")
                    }
                } else {
                    _errorMessage.postValue("gagal memuat data berita")
                }
            }

            override fun onFailure(call: Call<NewsResponse>, response: Throwable) {
                _isLoading2.value = false
                _errorMessage.postValue("Terjadi masalah: ${response.message}")
            }

        })
    }
}