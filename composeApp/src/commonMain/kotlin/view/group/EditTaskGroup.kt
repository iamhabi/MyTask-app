import TaskClient
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import group.TaskGroup
import onKeyUp

@Composable
fun EditTaskGroup(
    taskGroup: TaskGroup,
    isOpenEdit: MutableState<Boolean>
) {
    fun editGroup(title: String) {
        TaskClient.updateGroup(
            TaskGroup(taskGroup.id, title),
            onSuccess = {
                taskGroup.title = title
                isOpenEdit.value = false
            },
            onFailed = {
                isOpenEdit.value = false
            }
        )
    }

    val focusRequester = remember { FocusRequester() }
    
    val originalTitle = taskGroup.title
    
    val input = remember {
        mutableStateOf(
            TextFieldValue(
                text = taskGroup.title,
                selection = TextRange(taskGroup.title.length)
            )
        )
    }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        title = { Text("Edit group") },
        text = {
            OutlinedTextField(
                value = input.value,
                onValueChange = { input.value = it },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onKeyUp(
                        Key.Enter,
                        action = { editGroup(input.value.text) }
                    ),
                label = { Text("$originalTitle ->") },
                singleLine = true
            )
        },
        onDismissRequest = { isOpenEdit.value = false },
        confirmButton = {
            TextButton(
                onClick = { editGroup(input.value.text) },
                content = { Text("OK") }
            )
        },
        dismissButton = {
            TextButton(
                onClick = { isOpenEdit.value = false },
                content = { Text("Cancel") }
            )
        }
    )
}