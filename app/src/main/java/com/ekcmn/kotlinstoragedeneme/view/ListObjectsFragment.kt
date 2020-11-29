package com.ekcmn.kotlinstoragedeneme.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekcmn.kotlinstoragedeneme.R
import com.ekcmn.kotlinstoragedeneme.adapter.RecyclerViewAdapter
import com.ekcmn.kotlinstoragedeneme.dao.CloudStorageHelper
import com.ekcmn.kotlinstoragedeneme.viewmodel.ListObjectsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.list_items.*
import kotlinx.android.synthetic.main.list_objects_fragment.*
import kotlinx.android.synthetic.main.upload_file_fragment.*

class ListObjectsFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ListObjectsFragment()
        private const val TAG = "ListObjectsFragment"
    }

    private lateinit var viewModel: ListObjectsViewModel
    private lateinit var listAdapter: RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_objects_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ListObjectsViewModel::class.java)
        //setting recyclerview settings
        setRecyclerView()
        //call the getFiles method to list files when user navigated to this fragment
        getFiles()

    }
    //Get files from viewModel by observing regarding value
    private fun getFiles() {
        list_progress_bar.visibility = View.VISIBLE
        Log.i(TAG, "getFiles: ")
        //Display progress bar to user until whole data operation progress finishes
        viewModel.fileList.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.updateImageList(it)
            }
            //TODO -> Progress bar'ı acikla...
            viewModel.loading.observe(viewLifecycleOwner, Observer { result ->
                if (list_progress_bar != null) {
                    if (result) {
                        list_progress_bar.visibility = View.VISIBLE
                    } else {
                        list_progress_bar.visibility = View.GONE
                    }
                }
            })
        })
    }
    //default settings of recyclerview
    private fun setRecyclerView() {
        listAdapter = RecyclerViewAdapter(arrayListOf(), this)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.adapter = listAdapter
    }
    //when clicked on an item in recyclerview proceed the operation using with this function
    override fun onItemClick(position: Int, downloadIcon: View, removeIcon: View, infoIcon: View) {
        downloadIcon.setOnClickListener {
            //In case of user clicks on download icon in list
            CloudStorageHelper.downloadFile(listAdapter.getFileName(position), requireContext())
            viewModel.listAllFiles()
            Toast.makeText(context,"File downloaded succesfully: DCIM/Huawei-Storage",Toast.LENGTH_SHORT).show()
        }
        removeIcon.setOnClickListener {
            //In case of user clicks on remove icon in list
            Log.d(TAG, "onItemClick: Remove Icon clicked")
            CloudStorageHelper.delete(listAdapter.getFileName(position))
            viewModel.listAllFiles()
        }
        infoIcon.setOnClickListener {
            //In case of user clicks on info icon in list
            viewModel.getMetaData(listAdapter.getFileName(position))
            viewModel.metaDataResult.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(context,"File size: $it",Toast.LENGTH_SHORT).show()
                    Log.d(
                        TAG, "onItemClick: $it"
                    )
                } else {
                    Toast.makeText(context,"Oops! Something went wrong $it",Toast.LENGTH_SHORT).show()
                    Log.e("TAG", "onItemClick: Something went wrong!")
                }
            })
        }
    }
}