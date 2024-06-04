import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import group.EditTaskGroup
import group.TaskGroup
import kotlinx.coroutines.Dispatchers

@Composable
fun TaskGroupList(
    taskGroups: SnapshotStateList<TaskGroup>,
    onGroupSelected: (TaskGroup) -> Unit,
    onGroupDeleted: (TaskGroup) -> Unit,
) {
    val listState = rememberLazyListState()

    var currentGroupIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(Dispatchers.IO) {
        TaskClient.getGroups { groups ->
            taskGroups.addAll(groups)

            val lastGroupId = MyPref.myPref?.get<Long>(MyPref.PrefLastShowGroup) ?: -1L

            currentGroupIndex = taskGroups.indexOfFirst {
                it.id == lastGroupId
            }

            if (currentGroupIndex != -1) {
                onGroupSelected(taskGroups[currentGroupIndex])
            }
        }
    }

    LazyColumn(state = listState) {
        itemsIndexed(taskGroups) { index, taskGroup ->
            Column(
                modifier = Modifier
                    .background(
                        if (currentGroupIndex == index) {
                            Color.Blue.copy(alpha = 0.2F)
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        currentGroupIndex = index

                        onGroupSelected(taskGroup)

                        MyPref.myPref?.save(MyPref.PrefLastShowGroup, taskGroup.id)
                    }
            ) {
                TaskGroupItem(
                    taskGroup = taskGroup,
                    onDeleteItem = {
                        TaskClient.deleteGroup(
                            taskGroup,
                            onSuccess = {
                                onGroupDeleted(taskGroup)

                                val currentGroup = taskGroups[currentGroupIndex]

                                taskGroups.remove(taskGroup)

                                currentGroupIndex = if (index == currentGroupIndex) {
                                    -1
                                } else {
                                    taskGroups.indexOf(currentGroup)
                                }
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

@Composable
fun TaskGroupItem(
    taskGroup: TaskGroup,
    onDeleteItem: () -> Unit
) {
    val isExpnaded = remember { mutableStateOf(false) }

    val isOpenEdit = remember { mutableStateOf(false) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(64.dp)
    ) {
        Text(
            text = taskGroup.title,
            modifier = Modifier.weight(1F).padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box {
            IconButton(
                onClick = { isExpnaded.value = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Task more click"
                    )
                }
            )
            
            DropdownMenu(
                expanded = isExpnaded.value,
                onDismissRequest = { isExpnaded.value = false },
                content = {
                    DeleteGroup {
                        onDeleteItem()

                        isExpnaded.value = false
                    }

                    EditGroup {
                        isExpnaded.value = false

                        isOpenEdit.value = true
                    }
                }
            )
        }
    }

    if (isOpenEdit.value) {
        EditTaskGroup(
            taskGroup = taskGroup,
            isOpenEdit = isOpenEdit
        )
    }
}

@Composable
fun DeleteGroup(
    onDeleteGroup: () -> Unit
) {
    DropdownMenuItem(
        onClick = onDeleteGroup,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = Color.Red
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Delete")
            }
        }
    )
}

@Composable
fun EditGroup(
    onDialogEditGroup: () -> Unit
) {
    DropdownMenuItem(
        onClick = onDialogEditGroup,
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit title"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Edit")
            }
        }
    )
}