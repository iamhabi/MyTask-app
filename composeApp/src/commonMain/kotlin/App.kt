import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import group.TaskGroup
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview
import task.TaskItem

@Composable
@Preview
fun App() {
    val taskGroups = remember { mutableStateListOf<TaskGroup>() }
    val taskItems = remember { mutableStateListOf<TaskItem>() }

    val selectedGroupIndex = remember { mutableStateOf(-1) }

    LaunchedEffect(Dispatchers.IO) {
        TaskClient.getGroups { groups ->
            taskGroups.addAll(groups)
        }
    }

    if (selectedGroupIndex.value >= 0) {
        val groupId = taskGroups[selectedGroupIndex.value].id

        taskItems.clear()

        TaskClient.getTasks(groupId) {
            taskItems.addAll(it)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val isLarge = maxWidth >= 600.dp

        if (isLarge) {
            BigApp(
                taskGroups = taskGroups,
                selectedGroupIndex = selectedGroupIndex,
                taskItems =  taskItems
            )
        } else {
            SmallApp(
                taskGroups = taskGroups,
                selectedGroupIndex = selectedGroupIndex,
                taskItems = taskItems
            )
        }
    }
}