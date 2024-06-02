import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import group.GroupViewBigLayout
import group.GroupViewSmallLayout
import group.TaskGroup
import kotlinx.coroutines.Dispatchers
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

    val selectedGroupIndex = remember { mutableStateOf(-1) }

    val isShowGroup = remember { mutableStateOf(false) }

    val detailTaskIndex = remember { mutableStateOf(-1) }
    val isOpenDetail = remember { mutableStateOf(false) }

    LaunchedEffect(Dispatchers.IO) {
        TaskClient.getGroups { groups ->
            taskGroups.addAll(groups)

            selectedGroupIndex.value = 0
        }
    }

    if (selectedGroupIndex.value != -1) {
        isShowGroup.value = false
    } else {
        tasks.clear()
    }

    if (selectedGroupIndex.value >= 0) {
        val groupId = taskGroups[selectedGroupIndex.value].id

        tasks.clear()

        TaskClient.getTasks(groupId) {
            tasks.addAll(it)
        }

        isOpenDetail.value = false
    }

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .disableChilds(isShowGroup.value)
        ) {
            val isLarge = maxWidth >= 600.dp

            Row {
                if (isLarge) {
                    Box(modifier = Modifier.weight(4F)) {
                        GroupViewBigLayout(taskGroups, selectedGroupIndex)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(7F)
                ) {
                    if (!isLarge) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isShowGroup.value = true },
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

        AnimatedVisibility(
            visible = isShowGroup.value,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                GroupViewSmallLayout(
                    taskGroups = taskGroups,
                    selectedGroupIndex = selectedGroupIndex,
                    onClose = { isShowGroup.value = false }
                )
            }
        }
    }
}