import androidx.compose.runtime.mutableStateOf
import data.EncryptedTaskItem
import group.TaskGroup
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import task.TaskItem
import taskclient.TaskClientKey
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object TaskClient {
    const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"

    private const val BASE_URL = "http://${TaskClientKey.URL}/api/"
    private const val BASE_GROUP = "groups/"
    private const val BASE_TASK = "tasks/"

    private val client: HttpClient = HttpClient {
        defaultRequest { url(BASE_URL) }
    }

    private fun getAESKey(): SecretKey {
        return SecretKeySpec(TaskClientKey.KEY.toByteArray(), "AES")
    }

    fun getGroups(callback: (TaskGroup) -> Unit, onFinish: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.get(BASE_GROUP)

                response.groups(callback)

                onFinish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createGroup(title: String, onSuccess: (TaskGroup) -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val key = getAESKey()
                val taskGroup = TaskGroup(-1, title).encrypt(key)

                val response = client.post(BASE_GROUP) {
                    parameter("title", taskGroup.title)
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    val body = response.bodyAsText()
                    val createdGroup = Json.decodeFromString<TaskGroup>(body)

                    onSuccess(createdGroup.decrypt(key))
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteGroup(group: TaskGroup, onSuccess: () -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.delete(BASE_GROUP) {
                    parameter("id", group.id)
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateGroup(newGroup: TaskGroup, onSuccess: () -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encryptedGroup = newGroup.encrypt(getAESKey())

                val response = client.put(BASE_GROUP) {
                    parameter("id", newGroup.id)
                    parameter("title", encryptedGroup.title)
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var jobGetTasks: Job? = null

    fun getTasks(groupId: Long, callback: (List<TaskItem>) -> Unit) {
        jobGetTasks?.cancel()
        jobGetTasks = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.get(BASE_TASK) {
                    parameter("group_id", groupId)
                }

                response.getTasks(callback)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createTask(groupId: Long, title: String, onSuccess: (TaskItem) -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val key = getAESKey()

                val now = Clock.System.now()

                val created: Long = now.epochSeconds * 1000

                val encryptedTask = TaskItem(
                    0,
                    title,
                    mutableStateOf(false),
                    groupId,
                    "",
                    created,
                    0L
                ).encrypt(key)

                val response = client.post(BASE_TASK) {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("title", encryptedTask.title)
                                append("is_done", encryptedTask.isDone)
                                append("group_id", encryptedTask.groupId)
                                append("description", encryptedTask.description)
                                append("created", encryptedTask.created)
                                append("due_date", encryptedTask.dueDate)
                            }
                        )
                    )
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    val body = response.bodyAsText()
                    val createdTask = Json.decodeFromString<EncryptedTaskItem>(body)

                    onSuccess(createdTask.decrypt(key))
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTask(taskItem: TaskItem, onSuccess: () -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.delete(BASE_TASK) {
                    parameter("id", taskItem.id)
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTask(taskItem: TaskItem, onSuccess: () -> Unit, onFailed: (HttpStatusCode) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encryptedTask = taskItem.encrypt(getAESKey())

                val response = client.put(BASE_TASK) {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("id", taskItem.id)
                                append("title", encryptedTask.title)
                                append("is_done", encryptedTask.isDone)
                                append("description", encryptedTask.description)
                                append("due_date", encryptedTask.dueDate)
                            }
                        )
                    )
                }

                val status = response.status

                if (status == HttpStatusCode.OK) {
                    onSuccess()
                } else {
                    onFailed(status)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun HttpResponse.groups(onGetGroup: (TaskGroup) -> Unit) {
        val body = bodyAsText()

        val groups = Json.decodeFromString<List<TaskGroup>>(body)

        for (group in groups) {
            onGetGroup(group.decrypt(getAESKey()))
        }
    }

    private suspend fun HttpResponse.getTasks(onGetTasks: (List<TaskItem>) -> Unit) {
        val body = bodyAsText()

        val encryptedTasks = Json.decodeFromString<List<EncryptedTaskItem>>(body)
        val decryptedTasks = mutableListOf<TaskItem>()

        for (encryptedTask in encryptedTasks) {
            val task = encryptedTask.decrypt(getAESKey())
            val index = decryptedTasks.getSortedIndex(task)

            decryptedTasks.add(index, task)
        }

        onGetTasks(decryptedTasks)
    }
}