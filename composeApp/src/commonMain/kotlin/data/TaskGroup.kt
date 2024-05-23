package group

import TaskClient
import kotlinx.serialization.Serializable
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class TaskGroup(
    val id: Long,
    var title: String
) {
    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(secretKey: SecretKey): TaskGroup {
        val cipher = Cipher.getInstance(TaskClient.CIPHER_TRANSFORMATION)

        val ivParameterSpec = IvParameterSpec(ByteArray(16))

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

        val encryptedTitle = cipher.doFinal(title.toByteArray())

        val title = Base64.encode(encryptedTitle)

        return TaskGroup(id, title)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(secretKey: SecretKey): TaskGroup {
        val cipher = Cipher.getInstance(TaskClient.CIPHER_TRANSFORMATION)

        val ivParameterSpec = IvParameterSpec(ByteArray(16))

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)

        val decodedTitle = Base64.decode(title)

        val title = cipher.doFinal(decodedTitle).decodeToString()

        return TaskGroup(id, title)
    }
}
