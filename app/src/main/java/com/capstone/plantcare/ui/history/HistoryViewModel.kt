package com.capstone.plantcare.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.plantcare.data.remote.api.ApiConfig
import com.capstone.plantcare.data.remote.response.HistoryResponseItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {
    private val _historyList = MutableLiveData<List<HistoryResponseItem>>()
    val historyList: LiveData<List<HistoryResponseItem>> get() = _historyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchHistoryData(userId: String) {
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
                    _errorMessage.postValue("Belum ada data history")
                }
            }

            override fun onFailure(call: Call<List<HistoryResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.postValue("Terjadi kesalahan: ${t.message}")
            }
        })
    }

}