package com.dmitriib.challenge.fake

import com.dmitriib.challenge.ui.permissions.PermissionManager

class FakePermissionManager : PermissionManager {
    override fun getRequiredPermissions(): List<String> {
        return listOf()
    }

    override fun onPermissionResult(result: Map<String, Boolean>): Boolean {
        return true
    }
}
