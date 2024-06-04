package task

import MyPref
import TaskClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import toDateTime
import view.task.TaskListTopLayout

@Composable
fun TaskList(
    taskItems: SnapshotStateList<TaskItem>,
    onItemClick: (TaskItem) -> Unit
) {
    val listState = rememberLazyListState()

    val isSortByTitle = remember { mutableStateOf(false) }
    val isSortByDueDate = remember { mutableStateOf(false) }
    val isSortByDoneState = remember { mutableStateOf(false) }

    val isHideDonetask = remember { mutableStateOf(false) }

    LaunchedEffect(Dispatchers.IO) {
        isSortByTitle.value = MyPref.myPref?.get<Boolean>(MyPref.PrefSortByTitle) ?: false
        isSortByDueDate.value = MyPref.myPref?.get<Boolean>(MyPref.PrefSortByDueDate) ?: false
        isSortByDoneState.value = MyPref.myPref?.get<Boolean>(MyPref.PrefSortByDoneState) ?: false

        isHideDonetask.value = MyPref.myPref?.get<Boolean>(MyPref.PrefHideDoneTask) ?: false
    }

    Column {
        TaskListTopLayout(
            isSortByTitle = isSortByTitle,
            isSortByDueDate = isSortByDueDate,
            isSortByDoneState = isSortByDoneState,
            isHideDoneTask = isHideDonetask
        )

        Divider()

        LazyColumn(state = listState) {
            items(
                taskItems
                    .sortedByDescending { it.id }
                    .sortedWith(
                        compareBy(
                            { if (isSortByDoneState.value) it.isDone else false },
                            { if (isSortByTitle.value) it.title else false },
                            { if (isSortByDueDate.value) it.dueDate else false }
                        )
                    )
                    .filter {
                        if (isHideDonetask.value) {
                            return@filter !it.isDone
                        }

                        true
                    },
                key = { it.id }
            ) { taskItem ->
                Column(
                    modifier = Modifier
                        .clickable {
                            onItemClick(taskItem)
                        }
                ) {
                    TaskListItem(
                        taskItem = taskItem,
                        onDeleteItem = {
                            TaskClient.deleteTask(
                                taskItem = taskItem,
                                onSuccess = {
                                    taskItems.remove(taskItem)
                                },
                                onFailed = {

                                }
                            )
                        }
                    )

                    Divider()
                }
            }
        }
    }
}

@Composable
fun TaskListItem(
    taskItem: TaskItem,
    onDeleteItem: () -> Unit
) {
    val isChecked = remember { mutableStateOf(taskItem.isDone) }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = {
                val copiedTaskItem = taskItem.copy(
                    isDone = it
                )

                TaskClient.updateTask(
                    taskItem = copiedTaskItem,
                    onSuccess = {
                        isChecked.value = it
                        taskItem.isDone = it
                    },
                    onFailed = {

                    }
                )
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = taskItem.title,
                fontSize = 18.sp
            )

            if (taskItem.dueDate != 0L) {
                Text(
                    text = taskItem.dueDate.toDateTime(),
                    fontSize = 14.sp
                )
            }

            if (!taskItem.description.equals("")) {
                Text(
                    text = taskItem.description,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onDeleteItem,
            content = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = Color.Red
                )
            }
        )
    }
}