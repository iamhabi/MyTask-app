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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import group.EditTaskGroup
import group.TaskGroup

@Composable
fun TaskGroupList(
    taskGroups: SnapshotStateList<TaskGroup>,
    selectedGroupIndex: MutableState<Int>
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        itemsIndexed(taskGroups) { index, taskGroup ->
            Column(
                modifier = Modifier
                    .background(
                        if(selectedGroupIndex.value == index) {
                            Color.Blue.copy(alpha = 0.2F)
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        selectedGroupIndex.value = index
                    }
            ) {
                TaskGroupItem(
                    taskGroup = taskGroup,
                    onDeleteItem = {
                        TaskClient.deleteGroup(
                            taskGroup,
                            onSuccess = {
                                taskGroups.remove(taskGroup)

                                if (index == selectedGroupIndex.value) {
                                    selectedGroupIndex.value = -1
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