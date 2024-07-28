import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import group.TaskGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import task.TaskItem

@Composable
fun BigApp() {
    val taskGroups = remember { mutableStateListOf<TaskGroup>() }
    val taskItems = remember { mutableStateListOf<TaskItem>() }

    var currentGroup by remember { mutableStateOf<TaskGroup?>(null) }

    var detailTaskIndex by remember { mutableStateOf(-1) }
    var isOpenDetail by remember { mutableStateOf(false) }

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

                                TaskClient.getTasks(selectedGroup.id) {
                                    taskItems.clear()
                                    taskItems.addAll(it)
                                }
                            },
                            onGroupDeleted = { deletedGroup ->
                                if (deletedGroup.id == currentGroup?.id) {
                                    taskItems.clear()
                                    currentGroup = null
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
                
                VerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentGroup?.title ?: "",
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1F),
                        )

                        Options(
                            isSortByTitle = isSortByTitle,
                            isSortByDueDate = isSortByDueDate,
                            isSortByDoneState = isSortByDoneState,
                            isHideDoneTask = isHideDonetask
                        )
                    }

                    HorizontalDivider()

                    Box(
                        modifier = Modifier
                            .weight(1F)
                    ) {
                        TaskList(
                            taskItems = taskItems,
                            onItemClick = { taskItem ->
                                detailTaskIndex = taskItems.indexOf(taskItem)
                                
                                isOpenDetail = true
                            },
                            isSortByTitle = isSortByTitle,
                            isSortByDueDate = isSortByDueDate,
                            isSortByDoneState = isSortByDoneState,
                            isHideDonetask = isHideDonetask
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