package com.ekcmn.kotlinstoragedeneme.view

import RealPathUtil
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ekcmn.kotlinstoragedeneme.R
import com.ekcmn.kotlinstoragedeneme.viewmodel.UploadFileViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.upload_file_fragment.*


class UploadFileFragment : Fragment() {

    private val REQUEST_CODE = 101
    private var imageUri: Uri? = null

    companion object {
        fun newInstance() = UploadFileFragment()
    }

    private lateinit var viewModel: UploadFileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.upload_file_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadFileViewModel::class.java)
        // TODO: Use the ViewModel
        btn_choose_image.setOnClickListener {
            openFileChooser()
        }
        btn_upload.setOnClickListener {
            uploadFile()
        }
    }
    //When user select the file from phone storage that method calls regarding actions to upload file to storage.
    fun uploadFile(){

        var fileName = edit_text_file_name.text.trim().toString()
        //get file extension
        if(imageUri != null){
            var extensionUri = getMimeType(imageUri!!)
            //get the path of chosen file
            val file = context?.let { RealPathUtil.getRealPath(it, imageUri!!) }
            if (extensionUri != null && fileName.isNotEmpty()) {
                file?.let {
                    viewModel.uploadFiles(it, fileName, extensionUri)
                    viewModel.progressData.observe(viewLifecycleOwner, Observer { progress ->
                        progress_bar.progress = progress.toInt()
                    })
                    viewModel.successListener.observe(viewLifecycleOwner, Observer {
                        Toast.makeText(context,
                            "Upload Operation has been completed succesfully!",
                            Toast.LENGTH_LONG).show()
                        //Navigate users to start screen for further operations after upload operation successfully finishes
                        findNavController().navigate(R.id.action_uploadFileFragment_to_loginFragment)
                    })
                    viewModel.failureListener.observe(viewLifecycleOwner, Observer {
                        Toast.makeText(context,
                            "Oops! something went wrong while uploading your file..",
                            Toast.LENGTH_SHORT).show()
                    })
                }
            }else{
                Toast.makeText(context,
                    "Please give a name to your file",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Opens regarding intent to select the file that will be uploaded to store
    private fun openFileChooser() {
        val myIntent = Intent(Intent.ACTION_GET_CONTENT)
        myIntent.type = "image/*"
        startActivityForResult(myIntent, REQUEST_CODE)
    }
    //In case of file selection operation finish
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null){
            imageUri = data.data
            Picasso.with(context).load(imageUri)
                .into(upload_file_image_view)
        }
    }
    //Returns the extension of the file like .jpg/.png/etc.
    private fun getMimeType(uri: Uri): String? {
       val cR = requireContext().contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
}