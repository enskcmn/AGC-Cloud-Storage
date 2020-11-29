package com.ekcmn.kotlinstoragedeneme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ekcmn.kotlinstoragedeneme.dao.CloudStorageHelper
import com.huawei.agconnect.cloud.storage.core.UploadTask
import com.huawei.hmf.tasks.OnSuccessListener
import java.lang.Exception


class UploadFileViewModel : ViewModel(),
    CloudStorageHelper.IGenericListener<UploadTask.UploadResult> {
    // TODO: Implement the ViewModel

    private val _progressData: MutableLiveData<Double> = MutableLiveData()
    private val _successListener: MutableLiveData<UploadTask.UploadResult> = MutableLiveData()
    private val _failureListener: MutableLiveData<Exception> = MutableLiveData()

    val progressData: LiveData<Double> = _progressData
    val successListener: LiveData<UploadTask.UploadResult> = _successListener
    val failureListener: LiveData<Exception> = _failureListener

    //Uploading the file by giving uri from phone and extension of the file to store.
    fun uploadFiles(uri: String, filename: String, extension: String) {
        CloudStorageHelper.uploadFile(uri, filename, extension, this)
    }
    //Progress-bar value has been set to show file upload progress to user
    override fun onProgress(progress: Double) {
        _progressData.value = progress
    }
    //In order to handle the user navigation operation on success case in fragment, used a onSuccess callback
    override fun onSuccess(result: UploadTask.UploadResult) {
        _successListener.value = result
    }
    //In order to handle the user navigation operation on failure case in fragment used a onSuccess callback
    override fun onFailure(e: Exception) {
        _failureListener.value = e
    }


}