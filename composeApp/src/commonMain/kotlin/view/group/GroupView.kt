package group

import TaskClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import TaskGroupList
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

@Composable
fun GroupViewBigLayout(
    taskGroups: SnapshotStateList<TaskGroup>,
    selectedGroupIndex: MutableState<Int>
) {
    Row {
        Column(
            modifier = Modifier.weight(3.5F),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.weight(1F)) {
                TaskGroupList(
                    taskGroups = taskGroups,
                    selectedGroupIndex = selectedGroupIndex
                )
            }
    
            AddTaskGroup { title ->
                TaskClient.createGroup(
                    title,
                    onSuccess = { group ->
                        taskGroups.add(group)
                    },
                    onFailed = {

                    }
                )
            }
        }
    
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
    }
}

@Composable
fun GroupViewSmallLayout(
    taskGroups: SnapshotStateList<TaskGroup>,
    selectedGroupIndex: MutableState<Int>,
    onClose: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            onClick = onClose,
            content = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close group"
                )
            }
        )

        Box(modifier = Modifier.weight(1F)) {
            TaskGroupList(
                taskGroups = taskGroups,
                selectedGroupIndex = selectedGroupIndex
            )
        }

        AddTaskGroup { title ->
            TaskClient.createGroup(
                title,
                onSuccess = { group ->
                    taskGroups.add(group)
                },
                onFailed = {

                }
            )
        }
    }
}