package com.ekcmn.kotlinstoragedeneme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekcmn.kotlinstoragedeneme.dao.CloudStorageHelper
import com.huawei.agconnect.cloud.storage.core.FileMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListObjectsViewModel : ViewModel() {

    val fileList: LiveData<List<String>>
        get() = _listOfFiles
    val loading: LiveData<Boolean>
        get() = _loading
    val metaDataResult:LiveData<Long>
        get() = dataType


    private val _listOfFiles = MutableLiveData<List<String>>()
    private val _loading = MutableLiveData<Boolean>()
    private val dataType = MutableLiveData<Long>()

    //initializing viewModel by listing all items on storage
    init {
        listAllFiles()
    }
    //This function helps to get data from our helper class by using Coroutines
    fun listAllFiles() {
        _loading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val list = CloudStorageHelper.listAllFile()
            var fileNames = arrayListOf<String>()
            for (x in 1 until list.size) {
                fileNames.add(list[x].name)
            }
            _listOfFiles.postValue(fileNames)
            _loading.postValue(false)
        }
        _loading.value = false
    }
    //This function helps to get meta-data from our helper class by using Coroutines
    fun getMetaData(fileName: String) {
        var data: Long?
        CoroutineScope(Dispatchers.IO).launch {
            data = CloudStorageHelper.getMetaData(fileName)
            dataType.postValue(data)
        }
    }
}