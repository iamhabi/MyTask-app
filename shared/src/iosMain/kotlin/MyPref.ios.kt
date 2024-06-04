import platform.Foundation.NSUserDefaults

class IOSPref: MyPrefImpl {
    override val PREFRENCES_NAME: String
        get() = "com.iamhabi.mytask"

    override fun save(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }

    override fun save(key: String, value: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(value, key)
    }

    override fun save(key: String, value: Float) {
        NSUserDefaults.standardUserDefaults.setFloat(value, key)
    }

    override fun save(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }

    override fun save(key: String, value: Long) {
        NSUserDefaults.standardUserDefaults.setInteger(value, key)
    }

    override fun getString(key: String): String {
        return NSUserDefaults.standardUserDefaults.stringForKey(key) ?: ""
    }

    override fun getBoolean(key: String): Boolean {
        return NSUserDefaults.standardUserDefaults.boolForKey(key)
    }

    override fun getFloat(key: String): Float {
        return NSUserDefaults.standardUserDefaults.floatForKey(key)
    }

    override fun getInt(key: String): Int {
        return NSUserDefaults.standardUserDefaults.objectForKey(key) as Int
    }

    override fun getLong(key: String): Long {
        return NSUserDefaults.standardUserDefaults.integerForKey(key)
    }
}

actual fun getPrefImpl(myContext: MyContext?): MyPrefImpl = IOSPref()