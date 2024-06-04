import android.content.Context
import android.content.SharedPreferences

class AndroidPref(
    private val myContext: MyContext?
): MyPrefImpl {
    override val PREFERENCES_NAME: String
        get() = "com.iamhabi.mytask"
    
    private fun getPref(): SharedPreferences? {
        myContext ?: return null
        
        return myContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    
    private fun getEditor(): SharedPreferences.Editor? {
        return getPref()?.edit()
    }

    override fun save(key: String, value: String) {
        getEditor()?.run {
            putString(key, value)

            apply()
        }
    }

    override fun save(key: String, value: Boolean) {
        getEditor()?.run {
            putBoolean(key, value)
            
            apply()
        }
    }

    override fun save(key: String, value: Float) {
        getEditor()?.run {
            putFloat(key, value)
            
            apply()
        }
    }

    override fun save(key: String, value: Int) {
        getEditor()?.run {
            putInt(key, value)
            
            apply()
        }
    }

    override fun save(key: String, value: Long) {
        getEditor()?.run {
            putLong(key, value)
            
            apply()
        }
    }

    override fun getString(key: String): String {
        return getPref()?.getString(key, "") ?: ""
    }

    override fun getBoolean(key: String): Boolean {
        return getPref()?.getBoolean(key, false) ?: false
    }

    override fun getFloat(key: String): Float {
        return getPref()?.getFloat(key, 0F) ?: 0F
    }

    override fun getInt(key: String): Int {
        return getPref()?.getInt(key, 0) ?: 0
    }

    override fun getLong(key: String): Long {
        return getPref()?.getLong(key, 0L) ?: 0L
    }
}

actual fun getPrefImpl(myContext: MyContext?): MyPrefImpl = AndroidPref(myContext)