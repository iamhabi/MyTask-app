class MyPreferences(
    myContext: MyContext? = null
) {
    private val myPrefImpl = getPrefImpl(myContext)
    
    fun save(key: String, value: Any) {
        when (value) {
            is String -> myPrefImpl.save(key, value)
            is Boolean -> myPrefImpl.save(key, value)
            is Float -> myPrefImpl.save(key, value)
            is Int -> myPrefImpl.save(key, value)
            is Long -> myPrefImpl.save(key, value)
        }
    }
    
    inline fun <reified T> get(key: String): T? {
        return when (T::class) {
            String::class -> getString(key) as T
            Boolean::class -> getBoolean(key) as T
            Float::class -> getFloat(key) as T
            Int::class -> getInt(key) as T
            Long::class -> getLong(key) as T
            else -> null
        }
    }
    
    fun getString(key: String): String = myPrefImpl.getString(key)
    fun getBoolean(key: String): Boolean = myPrefImpl.getBoolean(key)
    fun getFloat(key: String): Float = myPrefImpl.getFloat(key)
    fun getInt(key: String): Int = myPrefImpl.getInt(key)
    fun getLong(key: String): Long = myPrefImpl.getLong(key)
}