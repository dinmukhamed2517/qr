package com.example.qrattendance.data.storage


import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.qrattendance.data.models.User
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore
    private val LANGUAGE_KEY = stringPreferencesKey("language")

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_LOGIN = stringPreferencesKey("user_login")
    }



    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }
    suspend fun saveUserLogin(login: String) {
        dataStore.edit { preferences ->
            preferences[USER_LOGIN] = login
        }
    }
    fun getUserLogin(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_LOGIN]
        }
    }


    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    }







    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(USER_LOGIN)
        }
    }
}