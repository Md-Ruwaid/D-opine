package com.strictlock.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.strictlock.app.data.model.RuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Query("SELECT * FROM rules WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules WHERE isActive = 1")
    suspend fun getActiveRules(): List<RuleEntity>

    @Query("SELECT * FROM rules WHERE isActive = 1")
    fun getActiveRulesSync(): List<RuleEntity>

    @Query("SELECT * FROM rules ORDER BY id DESC")
    fun getAllRulesFlow(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules WHERE id = :id LIMIT 1")
    suspend fun getRuleById(id: Long): RuleEntity?

    @Query("SELECT * FROM rules WHERE packageName = :packageName AND isActive = 1 LIMIT 1")
    suspend fun getActiveRuleForPackage(packageName: String): RuleEntity?

    @Query("SELECT * FROM rules WHERE packageName = :packageName AND isActive = 1 LIMIT 1")
    fun getActiveRuleForPackageSync(packageName: String): RuleEntity?

    @Query("SELECT COUNT(*) > 0 FROM rules WHERE mode = 'STRICT' AND isActive = 1")
    suspend fun hasActiveStrictRules(): Boolean

    @Query("SELECT COUNT(*) > 0 FROM rules WHERE mode = 'STRICT' AND isActive = 1")
    fun hasActiveStrictRulesSync(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: RuleEntity): Long

    @Update
    suspend fun updateRule(rule: RuleEntity): Int

    @Delete
    suspend fun deleteRule(rule: RuleEntity): Int

    @Query("DELETE FROM rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long): Int

    @Query("DELETE FROM rules WHERE packageName = :packageName")
    suspend fun deleteRuleByPackage(packageName: String): Int

    @Query("DELETE FROM rules")
    suspend fun deleteAllRules(): Int
}
