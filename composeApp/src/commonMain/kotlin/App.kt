import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import group.GroupViewBigLayout
import group.GroupViewSmallLayout
import group.TaskGroup
import org.jetbrains.compose.ui.tooling.preview.Preview
import task.AddTask
import task.TaskDetail
import task.TaskItem
import task.TaskList

@Composable
@Preview
fun App() {
    val taskGroups = remember { mutableStateListOf<TaskGroup>() }
    val tasks = remember { mutableStateListOf<TaskItem>() }

    val selectedGroupIndex = remember { mutableStateOf<Int>(-1) }

    val showGroup = remember { mutableStateOf(false) }

    val detailTaskIndex = remember { mutableStateOf<Int>(-1) }
    val isOpenDetail = remember { mutableStateOf(false) }

    if (selectedGroupIndex.value != -1) {
        showGroup.value = false
    } else {
        tasks.clear()
    }

    if (selectedGroupIndex.value >= 0) {
        val groupId = taskGroups[selectedGroupIndex.value].id

        tasks.clear()

        TaskClient.getTasks(groupId) { task ->
            tasks.add(task)
        }

        isOpenDetail.value = false
    }

    if (taskGroups.size == 0) {
        TaskClient.getGroups { group ->
            taskGroups.add(group)
        }
    }

    MaterialTheme {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val isLarge = maxWidth >= 600.dp

            Row {
                if (isLarge) {
                    Box(modifier = Modifier.weight(4F)) {
                        GroupViewBigLayout(taskGroups, selectedGroupIndex)
                    }
                }

                Column(
                    modifier = Modifier.weight(7F)
                ) {
                    if (!isLarge) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showGroup.value = true },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open group"
                                    )
                                }
                            )

                            if (selectedGroupIndex.value >= 0) {
                                Text(
                                    text = taskGroups[selectedGroupIndex.value].title,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1F)) {
                        TaskList(
                            taskItems = tasks,
                            onItemClick = { taskItem ->
                                detailTaskIndex.value = tasks.indexOf(taskItem)

                                isOpenDetail.value = true
                            }
                        )
                    }

                    AddTask { title ->
                        val groupId = taskGroups[selectedGroupIndex.value].id

                        TaskClient.createTask(
                            groupId = groupId,
                            title = title,
                            onSuccess = { createdTask ->
                                tasks.add(createdTask)
                            },
                            onFailed = {

                            }
                        )
                    }
                }
            }

            if (isOpenDetail.value) {
                val detailTaskItem = tasks[detailTaskIndex.value]

                TaskDetail(
                    taskItem = detailTaskItem,
                    onUpdateItem = { updatedItem ->
                        TaskClient.updateTask(
                            updatedItem,
                            onSuccess = {
                                tasks[detailTaskIndex.value] = updatedItem

                                isOpenDetail.value = false
                            },
                            onFailed = {
                                isOpenDetail.value = false
                            }
                        )
                    },
                    onDeleteItem = {
                        TaskClient.deleteTask(
                            taskItem = detailTaskItem,
                            onSuccess = {
                                tasks.removeAt(detailTaskIndex.value)

                                isOpenDetail.value = false
                            },
                            onFailed = {

                            }
                        )
                    }
                )
            }
        }
    }

    MaterialTheme {
        if (showGroup.value) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                GroupViewSmallLayout(
                    taskGroups = taskGroups,
                    selectedGroupIndex = selectedGroupIndex,
                    onClose = { showGroup.value = false }
                )
            }
        }
    }
}