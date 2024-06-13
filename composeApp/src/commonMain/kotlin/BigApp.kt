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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import task.TaskItem

@Composable
fun BigApp() {
    val taskMaps = hashMapOf<Long, MutableList<TaskItem>>()

    val taskGroups = remember { mutableStateListOf<TaskGroup>() }

    val currentTaskGroupIndex = remember { mutableStateOf(-1) }
    var currentTaskGroup by remember { mutableStateOf<TaskGroup?>(null) }
    val currentTaskItems = remember { mutableStateListOf<TaskItem>() }

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

        while (true) {
            TaskClient.getGroups(
                callback = { taskGroup ->
                    if (!taskGroups.contains(taskGroup)) {
                        taskGroups.add(taskGroup)
                    }

                    if (!taskMaps.containsKey(taskGroup.id)) {
                        taskMaps[taskGroup.id] = mutableListOf()
                    }

                    TaskClient.getTasks(
                        groupId = taskGroup.id,
                        callback = { taskItem ->
                            taskMaps[taskGroup.id]?.run {
                                if (!contains(taskItem)) {
                                    add(taskItem)
                                }
                            }
                        }
                    )
                },
                onFinish = {
                    if (currentTaskGroupIndex.value != -1) {
                        return@getGroups
                    }

                    val lastGroupId = MyPref.myPref?.get<Long>(MyPref.PrefLastShowGroup) ?: -1L

                    currentTaskGroupIndex.value = taskGroups.indexOfFirst {
                        it.id == lastGroupId
                    }

                    if (currentTaskGroupIndex.value != -1) {
                        currentTaskGroup = taskGroups.first {
                            it.id == lastGroupId
                        }

                        val items = taskMaps.getValue(currentTaskGroup!!.id)

                        if (items.isEmpty()) {
                            TaskClient.getTasks(
                                groupId = lastGroupId,
                                callback = { taskItem ->
                                    currentTaskItems.run {
                                        if (!contains(taskItem)) {
                                            add(taskItem)
                                        }
                                    }
                                }
                            )
                        } else {
                            currentTaskItems.run {
                                clear()
                                addAll(items)
                            }
                        }
                    }
                }
            )

            delay(60 * 1000L)
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
                            currentGroupIndex = currentTaskGroupIndex,
                            taskGroups = taskGroups,
                            onGroupSelected = { selectedGroup ->
                                currentTaskGroup = selectedGroup

                                currentTaskItems.clear()

                                if (taskMaps.containsKey(selectedGroup.id)) {
                                    val items = taskMaps.getValue(selectedGroup.id)

                                    currentTaskItems.addAll(items)
                                } else {
                                    TaskClient.getTasks(
                                        groupId = selectedGroup.id,
                                        callback = { taskItem ->
                                            currentTaskItems.add(taskItem)
                                        }
                                    )
                                }
                            },
                            onGroupDeleted = { deletedGroup ->
                                taskMaps.remove(deletedGroup.id)

                                if (deletedGroup.id == currentTaskGroup?.id) {
                                    currentTaskItems.clear()
                                    currentTaskGroup = null
                                }
                            }
                        )
                    }
                    
                    AddTaskGroup { title ->
                        TaskClient.createGroup(
                            title = title,
                            onSuccess = { createdGroup ->
                                taskGroups.add(createdGroup)

                                if (!taskMaps.containsKey(createdGroup.id)) {
                                    taskMaps[createdGroup.id] = mutableListOf()
                                }
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
                            text = currentTaskGroup?.title ?: "",
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
                            taskItems = currentTaskItems,
                            onItemClick = { taskItem ->
                                detailTaskIndex = currentTaskItems.indexOf(taskItem)
                                
                                isOpenDetail = true
                            },
                            isSortByTitle = isSortByTitle,
                            isSortByDueDate = isSortByDueDate,
                            isSortByDoneState = isSortByDoneState,
                            isHideDonetask = isHideDonetask
                        )
                    }
                    
                    AddTask { title ->
                        val groupId = currentTaskGroup?.id ?: return@AddTask
                        
                        TaskClient.createTask(
                            groupId = groupId,
                            title = title,
                            onSuccess = { createdTask ->
                                currentTaskItems.add(createdTask)

                                taskMaps[currentTaskGroup?.id]?.run {
                                    if (!contains(createdTask)) {
                                        add(createdTask)
                                    }
                                }
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
                val detailTaskItem = currentTaskItems[detailTaskIndex].copy()

                TaskDetail(
                    taskItem = detailTaskItem,
                    onUpdateItem = { updatedItem ->
                        TaskClient.updateTask(
                            taskItem = updatedItem,
                            onSuccess = {
                                currentTaskItems[detailTaskIndex] = updatedItem
                                
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
                                currentTaskItems.removeAt(detailTaskIndex)
                                
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