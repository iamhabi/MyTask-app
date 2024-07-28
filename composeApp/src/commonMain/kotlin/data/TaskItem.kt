package task

import androidx.compose.runtime.MutableState
import data.EncryptedTaskItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import taskclient.TaskClientKey
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class TaskItem(
    val id: Long,
    var title: String,
    var isDone: MutableState<Boolean>,
    var groupId: Long,
    var description: String = "",
    var created: Long,
    var dueDate: Long = 0L
) {
    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(secretKey: SecretKey): EncryptedTaskItem {
        val cipher = Cipher.getInstance(TaskClient.CIPHER_TRANSFORMATION)

        val ivParameterSpec = IvParameterSpec(ByteArray(TaskClientKey.KEY.length))

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

        val encryptedTitle = cipher.doFinal(title.toByteArray())
        val encryptedIsDone = cipher.doFinal(isDone.toString().toByteArray())
        val encryptedDescription = cipher.doFinal(description.toByteArray())
        val encryptedCreated = cipher.doFinal(created.toString().toByteArray())
        val encryptedDueDate = cipher.doFinal(dueDate.toString().toByteArray())

        val title = Base64.encode(encryptedTitle)
        val isDone = Base64.encode(encryptedIsDone)
        val description = Base64.encode(encryptedDescription)
        val created = Base64.encode(encryptedCreated)
        val dueDate = Base64.encode(encryptedDueDate)

        return EncryptedTaskItem(
            id,
            title,
            isDone,
            groupId,
            description,
            created,
            dueDate
        )
    }
}
