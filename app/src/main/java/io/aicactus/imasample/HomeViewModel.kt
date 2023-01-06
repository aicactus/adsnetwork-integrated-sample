package io.aicactus.imasample

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// import io.aiactiv.adnetwork.internal.AiactivAdNetwork
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    fun initAdsNetworkSDK(context: Context, containerID: String?) {
        _loading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
//            AiactivAdNetwork.setup(context, containerID) { _, errorMessage ->
//                errorMessage?.let {
//                    _errorMessage.value = it
//                }
//            }
            _loading.value = false
        }
    }
}