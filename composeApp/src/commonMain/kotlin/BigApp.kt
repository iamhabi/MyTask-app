import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import group.AddTaskGroup
import group.TaskGroup
import kotlinx.coroutines.Dispatchers
import task.AddTask
import task.TaskDetail
import task.TaskItem
import task.TaskList

@Composable
fun BigApp() {
    val taskGroups = remember { mutableStateListOf<TaskGroup>() }
    val taskItems = remember { mutableStateListOf<TaskItem>() }

    var currentGroup by remember { mutableStateOf<TaskGroup?>(null) }

    var detailTaskIndex by remember { mutableStateOf(-1) }
    var isOpenDetail by remember { mutableStateOf(false) }
    
    LaunchedEffect(Dispatchers.IO) {
        TaskClient.getGroups { groups ->
            taskGroups.addAll(groups)
        }
    }

    MaterialTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .disableChilds(isOpenDetail)
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1F)
                    ) {
                        TaskGroupList(
                            taskGroups = taskGroups,
                            onGroupSelected = { selectedGroup ->
                                currentGroup = selectedGroup

                                taskItems.clear()

                                TaskClient.getTasks(selectedGroup.id) {
                                    taskItems.addAll(it)
                                }
                            },
                            onGroupDeleted = { deletedGroup ->
                                if (deletedGroup.id == currentGroup?.id) {
                                    taskItems.clear()
                                }
                            }
                        )
                    }
                    
                    AddTaskGroup { title ->
                        TaskClient.createGroup(
                            title = title,
                            onSuccess = { createdGroup ->
                                taskGroups.add(createdGroup)
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
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1F)
                    ) {
                        TaskList(
                            taskItems = taskItems,
                            onItemClick = { taskItem ->
                                detailTaskIndex = taskItems.indexOf(taskItem)
                                
                                isOpenDetail = true
                            }
                        )
                    }
                    
                    AddTask { title ->
                        val groupId = currentGroup?.id ?: return@AddTask
                        
                        TaskClient.createTask(
                            groupId = groupId,
                            title = title,
                            onSuccess = { createdTask ->
                                taskItems.add(createdTask)
                            },
                            onFailed = {
                                
                            }
                        )
                    }
                }
            }
            
            AnimatedVisibility(
                visible = isOpenDetail,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                val detailTaskItem = taskItems[detailTaskIndex].copy()
                
                TaskDetail(
                    taskItem = detailTaskItem,
                    onUpdateItem = { updatedItem ->
                        TaskClient.updateTask(
                            taskItem = updatedItem,
                            onSuccess = {
                                taskItems[detailTaskIndex] = updatedItem
                                
                                isOpenDetail = false
                            },
                            onFailed = {
                                
                            }
                        )
                    },
                    onDeleteItem = {
                        TaskClient.deleteTask(
                            taskItem = detailTaskItem,
                            onSuccess = {
                                taskItems.removeAt(detailTaskIndex)
                                
                                isOpenDetail = false
                            },
                            onFailed = {
                                
                            }
                        )
                    }
                )
            }
        }
    }
}