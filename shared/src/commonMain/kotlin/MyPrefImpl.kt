interface MyPrefImpl {
    val PREFRENCES_NAME: String

    fun save(key: String, value: String)
    fun save(key: String, value: Boolean)
    fun save(key: String, value: Float)
    fun save(key: String, value: Int)
    fun save(key: String, value: Long)
    
    fun getString(key: String): String
    fun getBoolean(key: String): Boolean
    fun getFloat(key: String): Float
    fun getInt(key: String): Int
    fun getLong(key: String): Long
}

expect fun getPrefImpl(myContext: MyContext?): MyPrefImpl