package com.strictlock.app.data.repository

import com.strictlock.app.data.db.RuleDao
import com.strictlock.app.data.model.RuleEntity
import com.strictlock.app.data.model.RuleMode
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.UUID

class RuleRepository(private val ruleDao: RuleDao) {

    val activeRules: Flow<List<RuleEntity>> = ruleDao.getActiveRulesFlow()
    val allRules: Flow<List<RuleEntity>> = ruleDao.getAllRulesFlow()

    suspend fun getActiveRules(): List<RuleEntity> = ruleDao.getActiveRules()

    fun getActiveRulesSync(): List<RuleEntity> = ruleDao.getActiveRulesSync()

    suspend fun getActiveRuleForPackage(packageName: String): RuleEntity? {
        val rule = ruleDao.getActiveRuleForPackage(packageName) ?: return null
        return if (!rule.isExpired()) rule else null
    }

    fun getActiveRuleForPackageSync(packageName: String): RuleEntity? {
        val rule = ruleDao.getActiveRuleForPackageSync(packageName) ?: return null
        return if (!rule.isExpired()) rule else null
    }

    suspend fun hasActiveStrictRules(): Boolean =
        ruleDao.getActiveRules().any { it.isStrict && !it.isExpired() }

    fun hasActiveStrictRulesSync(): Boolean =
        ruleDao.getActiveRulesSync().any { it.isStrict && !it.isExpired() }

    /**
     * Creates a new rule.
     * Days are calendar days. The rule expires when the last day ends (23:59:59.999).
     * Usage before rule creation is NOT counted; counting starts at startEpochMs.
     */
    suspend fun createRule(
        packageName: String,
        appName: String,
        limitMinutes: Long,
        totalDays: Int,
        mode: RuleMode
    ): Long {
        val now = System.currentTimeMillis()
        val endEpochMs = calculateRuleEndTimestamp(now, totalDays)

        // Prevent overriding an active STRICT rule
        val existing = ruleDao.getActiveRuleForPackage(packageName)
        if (existing != null && existing.isStrict && !existing.isExpired(now)) {
            return -1L
        }

        // Remove any existing active casual rule or expired rule for this package first
        ruleDao.deleteRuleByPackage(packageName)

        val rule = RuleEntity(
            packageName = packageName,
            appName = appName,
            dailyLimitMinutes = limitMinutes,
            mode = mode.name,
            startEpochMs = now,
            endEpochMs = endEpochMs,
            totalDays = totalDays,
            isActive = true
        )
        return ruleDao.insertRule(rule)
    }

    /**
     * Initiates casual unlock request.
     * Generates a 12-character random string and records timestamp.
     */
    suspend fun startCasualUnlock(ruleId: Long): String? {
        val rule = ruleDao.getRuleById(ruleId) ?: return null
        if (!rule.isCasual) return null

        val randomCode = generateRandomVerificationString()
        val updated = rule.copy(
            casualUnlockRequestedAt = System.currentTimeMillis(),
            casualUnlockVerificationCode = randomCode
        )
        ruleDao.updateRule(updated)
        return randomCode
    }

    /**
     * Cancels pending casual unlock request.
     */
    suspend fun cancelCasualUnlock(ruleId: Long) {
        val rule = ruleDao.getRuleById(ruleId) ?: return
        val updated = rule.copy(
            casualUnlockRequestedAt = null,
            casualUnlockVerificationCode = null
        )
        ruleDao.updateRule(updated)
    }

    /**
     * Confirms casual unlock: must be after 10 minutes wait AND code matches exactly.
     */
    suspend fun confirmCasualUnlock(ruleId: Long, typedCode: String): UnlockResult {
        val rule = ruleDao.getRuleById(ruleId) ?: return UnlockResult.NotFound
        if (!rule.isCasual) return UnlockResult.NotCasual

        val requestedAt = rule.casualUnlockRequestedAt ?: return UnlockResult.NotRequested
        val expectedCode = rule.casualUnlockVerificationCode ?: return UnlockResult.NotRequested

        val now = System.currentTimeMillis()
        val elapsedMs = now - requestedAt
        val requiredWaitMs = 10 * 60 * 1000L // 10 minutes

        if (elapsedMs < requiredWaitMs) {
            val remainingMs = requiredWaitMs - elapsedMs
            return UnlockResult.TimeNotElapsed(remainingMs)
        }

        if (typedCode.trim() != expectedCode.trim()) {
            return UnlockResult.CodeMismatch
        }

        // Successfully verified, delete rule completely as required
        ruleDao.deleteRule(rule)
        return UnlockResult.Success
    }

    val allActiveRules: Flow<List<RuleEntity>> get() = activeRules

    suspend fun startCasualCooldown(ruleId: Long): String? = startCasualUnlock(ruleId)

    suspend fun verifyAndUnlockCasual(ruleId: Long, typedCode: String): UnlockResult =
        confirmCasualUnlock(ruleId, typedCode)

    /**
     * Hidden emergency exit: immediately terminates ALL rules (strict and casual).
     */
    suspend fun emergencyTerminateAllRules() {
        ruleDao.deleteAllRules()
    }

    companion object {
        fun calculateRuleEndTimestamp(startEpochMs: Long, totalDays: Int): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = startEpochMs
                add(Calendar.DAY_OF_YEAR, maxOf(1, totalDays) - 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            return calendar.timeInMillis
        }

        fun calculateDaysLeft(endEpochMs: Long, now: Long = System.currentTimeMillis()): Int {
            if (now > endEpochMs) return 0
            val startOfToday = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val diffMs = endEpochMs - startOfToday
            if (diffMs <= 0) return 0
            return (diffMs / (24 * 60 * 60 * 1000L)).toInt() + 1
        }

        private fun generateRandomVerificationString(): String {
            val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz"
            return (1..12).map { chars.random() }.joinToString("")
        }
    }
}

sealed class UnlockResult {
    data object Success : UnlockResult()
    data object NotFound : UnlockResult()
    data object NotCasual : UnlockResult()
    data object NotRequested : UnlockResult()
    data class TimeNotElapsed(val remainingMs: Long) : UnlockResult()
    data object CodeMismatch : UnlockResult()
    data object IncorrectCode : UnlockResult()
}
