import java.util.prefs.Preferences

class JVMPref: MyPrefImpl {
    override val PREFERENCES_NAME: String
        get() = "com.iamhabi.mytask"

    private fun getPref() = Preferences.userRoot().node(PREFERENCES_NAME)
    
    override fun save(key: String, value: String) {
        getPref().put(key, value)
    }

    override fun save(key: String, value: Boolean) {
        getPref().putBoolean(key, value)
    }

    override fun save(key: String, value: Float) {
        getPref().putFloat(key, value)
    }

    override fun save(key: String, value: Int) {
        getPref().putInt(key, value)
    }

    override fun save(key: String, value: Long) {
        getPref().putLong(key, value)
    }

    override fun getString(key: String): String {
        return getPref().get(key, "")
    }

    override fun getBoolean(key: String): Boolean {
        return getPref().getBoolean(key, false)
    }

    override fun getFloat(key: String): Float {
        return getPref().getFloat(key, 0F)
    }

    override fun getInt(key: String): Int {
        return getPref().getInt(key, 0)
    }

    override fun getLong(key: String): Long {
        return getPref().getLong(key, 0L)
    }
}

actual fun getPrefImpl(myContext: MyContext?): MyPrefImpl = JVMPref()