package com.strictlock.app.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.strictlock.app.data.db.StrictLockDatabase
import com.strictlock.app.data.repository.RuleRepository

class StrictLockDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "StrictLock: Device Admin enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        val db = StrictLockDatabase.getInstance(context)
        val repository = RuleRepository(db.ruleDao())
        val hasStrict = repository.hasActiveStrictRulesSync()

        return if (hasStrict) {
            "WARNING: StrictLock has active STRICT rules! Deactivating device admin is prohibited while strict rules exist."
        } else {
            "StrictLock Device Admin deactivation requested."
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "StrictLock: Device Admin disabled", Toast.LENGTH_SHORT).show()
    }
}
