import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import group.AddTaskGroup
import group.TaskGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import task.AddTask
import task.TaskDetail
import task.TaskItem
import task.TaskList
import kotlin.math.roundToInt

enum class DragValue { Start, End }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallApp() {
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

                        if (currentGroup != null) {
                            Text(
                                text = currentGroup!!.title,
                                maxLines = 1
                            )
                        }
                    }
                    
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
                            taskGroups = taskGroups,
                            onGroupSelected = { taskGroup ->
                                currentGroup = taskGroup

                                taskItems.clear()

                                TaskClient.getTasks(taskGroup.id) {
                                    taskItems.addAll(it)
                                }

                                close()
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
            }
            
            AnimatedVisibility(
                visible = isOpenDetail
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