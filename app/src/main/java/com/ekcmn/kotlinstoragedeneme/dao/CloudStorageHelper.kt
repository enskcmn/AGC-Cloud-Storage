package com.ekcmn.kotlinstoragedeneme.dao

import android.content.Context
import android.os.Environment
import android.util.Log
import com.ekcmn.kotlinstoragedeneme.viewmodel.UploadFileViewModel
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement
import com.huawei.agconnect.cloud.storage.core.OnProgressListener
import com.huawei.agconnect.cloud.storage.core.StorageReference
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hmf.tasks.Tasks
import java.io.File


object CloudStorageHelper {

    //All Cloud Storage functions can be located in this helper class
    private lateinit var storageManagement: AGCStorageManagement

    //initialize helper class with the instance of storage object
    init {
        initStorage()
    }

    //TODO initializing Storage object
    private fun initStorage() {
        storageManagement = AGCStorageManagement.getInstance()
    }

    /*get all the file list from storage area
    Call your-storage-reference.listAll*/
    fun listAllFile(): List<StorageReference> {
        var ref = storageManagement.getStorageReference("images/")
        var fileStorageList = ref.listAll()
        return Tasks.await(fileStorageList).fileList
    }

    //TODO delete chosen file from your storage
    fun delete(fileName: String) {
        val ref = storageManagement.storageReference.child("images/$fileName")
        val deleteTask = ref.delete()
        deleteTask.addOnSuccessListener {
            Log.d("TAG", "delete: Operation was successfull ")
        }.addOnFailureListener {
            Log.e("TAG", "delete: ", it)
        }
    }

    /*Upload a file from your phone storage
    Call your-storage-reference.putFile*/
    fun uploadFile(
        uri: String,
        fileName: String,
        fileExtension: String,
        listener: UploadFileViewModel
    ) {

        var ref = storageManagement.storageReference.child(
            "images/" + "IMG_" + fileName
                    + "." + fileExtension
        )
        var uploadTask = ref.putFile(File(uri))
        uploadTask.addOnSuccessListener {
            Log.i("TAG", "uploadFile: $it")
            listener.onSuccess(it)
        }.addOnFailureListener {
            Log.e("TAG", "uploadFile: ", it)
            listener.onFailure(it)
        }.addOnProgressListener(OnProgressListener {
            var progress = (100.0 * it.bytesTransferred / it.totalByteCount)
            listener.onProgress(progress)
        })
    }

    /* download the item selected from the recyclerview
    Call your-storage-reference.getFile*/
    fun downloadFile(fileName: String, context: Context) {

        val ref = storageManagement.storageReference.child("images/$fileName")
        val downloadPath =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + "/Huawei-Storage"
            )
        val fileDir = File(downloadPath.toString())
        //if can't found the folder then create
        if (!fileDir.exists()){
            fileDir.mkdir()
        }
        val file = File(downloadPath, fileName)

        val downloadTask = ref.getFile(file)
        downloadTask.addOnSuccessListener(OnSuccessListener {
            Log.d("TAG", "downloadFile: success")

        }).addOnFailureListener(OnFailureListener {
            Log.e("TAG", "downloadFile: ", it)
        })
    }

    //TODO get the metadata of the item selected from the recyclerview
    fun getMetaData(fileName: String): Long? {
        var ref = storageManagement.storageReference.child("images/$fileName")
        val metadataTask = ref.fileMetadata
        return Tasks.await(metadataTask).size
    }

    interface IGenericListener<T> {
        fun onSuccess(t: T)
        fun onFailure(e: Exception)
        fun onProgress(progress: Double)
    }
}