package com.ekcmn.kotlinstoragedeneme.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ekcmn.kotlinstoragedeneme.R
import com.google.android.material.snackbar.Snackbar
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_login.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private val REQUEST_SIGN_IN_LOGIN_CODE = 123
    private val PERMISSION_REQ = 101
    private var currentUser:AGConnectUser? = AGConnectAuth.getInstance().currentUser
    //Read and Write permissions should be granted from user to be able to reach storage
    private val mPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    //Check if the permissions granted from user
    private fun hasPermissions(permissions : Array<String>) = permissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       if (hasPermissions(mPermissions)){
           initUI()
       }else{
            requestPermissions(mPermissions,PERMISSION_REQ)
       }
    }
    //Init user interface if permissions granted.
    private fun initUI(){
        if (currentUser != null){
            hideOrShowButtons(currentUser)
            displayUserInfo()
        }else{
            hideOrShowButtons(currentUser)
        }
        hwIdLoginBtn.setOnClickListener {
            login()
        }
        btn_file_upload.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_uploadFileFragment)
        })
        btn_logout.setOnClickListener {
            logout()
        }
        btn_list_files.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_listObjectsFragment)
        })
    }
    //Login with Huawei-Id or with other login methods
    private fun login() {
        val mAuthParam = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
            .setIdToken()
            .setAccessToken()
            .setProfile()
            .createParams()
        val mAuthManager = HuaweiIdAuthManager.getService(context, mAuthParam)
        startActivityForResult(
            mAuthManager.signInIntent,
            REQUEST_SIGN_IN_LOGIN_CODE
        )
    }
    //Huawei-Id log out operation
    private fun logout() {
        AGConnectAuth.getInstance().signOut()
        clearUserInfo()
        val user = AGConnectAuth.getInstance().currentUser
        hideOrShowButtons(user)
    }

    //Clears user-account information, in case of user not signed in or signed out
    private fun clearUserInfo() {
        user_name.text = ""
        user_avatar.setImageResource(R.drawable.avatar)
        onSnack("Log out operation has completed succesfully")
    }
    //Applying for user permission in run-time
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ && grantResults.isNotEmpty()){
            if (hasPermissions(mPermissions)){
                initUI()
            }
        }
    }
    //Login operation is handling in this override method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN_LOGIN_CODE) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
                    // onSuccess
                    displayUserInfo()
                    currentUser = it.user
                    hideOrShowButtons(currentUser)
                }.addOnFailureListener {
                    // onFail
                    Log.e("LoginFragment", it.toString())
                }
            } else {
                Log.i(
                    "Signin failed",
                    "signIn get code failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        logout()
        onSnack("Logged out")
    }
    private fun displayUserInfo() {
        var currentUser = AGConnectAuth.getInstance().currentUser
        if (currentUser != null){
            user_name.visibility = View.VISIBLE
            user_name.text = currentUser.displayName
            Picasso.with(context)
                .load(currentUser.photoUrl)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .fit()
                .into(user_avatar)
            onSnack("Welcome ${currentUser.displayName}!")
        }
    }
    //hides or shows buttons, according to login state of user
    private fun hideOrShowButtons(user:AGConnectUser?){
        if (user!=null){
            hwIdLoginBtn.visibility = View.GONE
            user_name.visibility = View.VISIBLE
            btn_list_files.visibility = View.VISIBLE
            btn_file_upload.visibility = View.VISIBLE
            btn_logout.visibility = View.VISIBLE
        }else{
            hwIdLoginBtn.visibility = View.VISIBLE
            user_name.visibility = View.GONE
            btn_list_files.visibility = View.GONE
            btn_file_upload.visibility = View.GONE
            btn_logout.visibility = View.GONE
        }
    }
    //displays Snackbar messages in fragment
    private fun onSnack(msg: String){
        Snackbar.make(rootLayout, msg, Snackbar.LENGTH_LONG).show()
    }
}