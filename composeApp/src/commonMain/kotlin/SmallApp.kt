import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import group.TaskGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import task.TaskItem
import kotlin.math.roundToInt

enum class DragValue { Start, End }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallApp() {
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
            val scope = rememberCoroutineScope()
            
            val density = LocalDensity.current
            val defaultAnchorSize = with(density) { maxWidth.toPx() }
        
            val anchors = remember {
                DraggableAnchors {
                    DragValue.Start at -defaultAnchorSize
                    DragValue.End at 0F
                }
            }
            
            val anchoredDraggableState = remember {
                AnchoredDraggableState(
                    initialValue = DragValue.Start,
                    anchors = anchors,
                    positionalThreshold = { totalDistance: Float -> totalDistance * 0.5F },
                    velocityThreshold = { defaultAnchorSize },
                    animationSpec = tween()
                )
            }
            
            fun open() {
                scope.launch {
                    try {
                        anchoredDraggableState.animateTo(DragValue.End)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            fun close() {
                scope.launch {
                    try {
                        anchoredDraggableState.animateTo(DragValue.Start)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .disableChilds(isOpenDetail)
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                open()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open group"
                            )
                        }

                        Text(
                            text = currentTaskGroup?.title ?: "",
                            maxLines = 1,
                            modifier = Modifier
                                .weight(1F)
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
    
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        IntOffset(
                            x = anchoredDraggableState
                                .requireOffset()
                                .roundToInt(),
                            y = 0
                        )
                    }
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Horizontal
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(
                        onClick = {
                            close()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close groups"
                        )
                    }
                    
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

                                close()
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