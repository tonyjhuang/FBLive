package com.tonyjhuang.fblive.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.ref.WeakReference

class SimplePermissionsChecker(private val context: WeakReference<Context>) {
    fun getPermissions(
        permissions: Collection<String>,
        onSuccessListener: () -> Unit,
        onFailedListener: () -> Unit
    ) {
        val context = this.context.get() ?: return
        Dexter.withContext(context)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        onSuccessListener()
                    } else {
                        onFailedListener()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun hasPermissions(permissions: Collection<String>): Boolean {
        val context = this.context.get() ?: return false
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}