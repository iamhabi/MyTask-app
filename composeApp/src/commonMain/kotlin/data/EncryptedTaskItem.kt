package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import task.TaskItem
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class EncryptedTaskItem(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    var title: String,
    @SerialName("is_done")
    var isDone: String,
    @SerialName("group_id")
    var groupId: Long,
    @SerialName("description")
    var description: String,
    @SerialName("created")
    var created: String,
    @SerialName("due_date")
    var dueDate: String
) {
    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(secretKey: SecretKey): TaskItem {
        val cipher = Cipher.getInstance(TaskClient.CIPHER_TRANSFORMATION)

        val ivParameterSpec = IvParameterSpec(ByteArray(16))

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        
        val decodedTitle = Base64.decode(title)
        val decodedIsDone = Base64.decode(isDone)
        val decodedDescription = Base64.decode(description)
        val decodedCreated = Base64.decode(created)
        val decodedDueDate = Base64.decode(dueDate)

        val title = cipher.doFinal(decodedTitle).decodeToString()
        val isDone = cipher.doFinal(decodedIsDone).decodeToString().toBoolean()
        val description = cipher.doFinal(decodedDescription).decodeToString()
        val created = cipher.doFinal(decodedCreated).decodeToString().toLong()
        val dueDate = cipher.doFinal(decodedDueDate).decodeToString().toLong()

        return TaskItem(id, title, isDone, groupId, description, created, dueDate)
    }
}