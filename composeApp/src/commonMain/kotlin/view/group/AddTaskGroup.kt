package group

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import onKeyUp

@Composable
fun AddTaskGroup(
    onCreateGroup: (String) -> Unit
) {
    val input = remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun createGroup(title: String) {
        if (title == "") {
            return
        }

        onCreateGroup(title)

        input.value = ""

        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = input.value,
            onValueChange = { input.value = it },
            modifier = Modifier
                .weight(1F)
                .onKeyUp(
                    key = Key.Enter,
                    action = {
                        createGroup(input.value)
                    }
                ),
            label = {
                Text("New group")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                createGroup(input.value)
            }
        )

        Spacer(modifier = Modifier.width(8.dp))
        
        IconButton(
            enabled = input.value != "",
            onClick = { createGroup(input.value) },
            content = {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Create new task"
                )
            }
        )
    }
}
