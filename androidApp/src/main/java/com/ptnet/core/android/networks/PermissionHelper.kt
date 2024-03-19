package com.ptnet.core.android.networks

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

object PermissionHelper {
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
    }
    fun requestPermission(activity: Activity, permission: String, permissionCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            permissionCode
        )
    }

    fun showPermissionDialog(
        activity: Activity,
        permission: String,
        permissionCode: Int,
        callback: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(activity)
            .setTitle("Location Permission")
            .setMessage("PT Net needs access to your location to function properly.")
            .setPositiveButton("Allow") { dialogInterface: DialogInterface, i: Int ->
                // Nếu người dùng chấp nhận, gọi hàm callback với tham số true
                callback(true)
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    permissionCode
                )
                dialogInterface.dismiss()
            }
            .setNegativeButton("Deny") { dialogInterface: DialogInterface, i: Int ->
                // Nếu người dùng từ chối, gọi hàm callback với tham số false
                callback(false)
                dialogInterface.dismiss()
            }
            .setCancelable(false) // Prevent dismissing dialog by tapping outside of it
            .create()
            .show()
    }
}