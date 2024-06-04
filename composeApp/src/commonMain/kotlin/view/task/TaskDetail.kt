package task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import toDateTime

@Composable
fun TaskDetail(
    taskItem: TaskItem,
    onUpdateItem: (TaskItem) -> Unit,
    onDeleteItem: () -> Unit,
) {
    val showDatePicker = remember { mutableStateOf(false) }

    val title = remember {
        mutableStateOf(
            TextFieldValue(
                text = taskItem.title,
                selection = TextRange(taskItem.title.length)
            )
        )
    }

    val description = remember {
        val description = taskItem.description

        mutableStateOf(
            TextFieldValue(
                text = description,
                selection = TextRange(description.length)
            )
        )
    }

    val dueDate = remember {
        mutableStateOf(taskItem.dueDate)
    }

    Column(
        modifier = Modifier
            .background(color = Color.White)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val updatedItem = taskItem.copy(
                        title = title.value.text,
                        description = description.value.text,
                        dueDate = dueDate.value
                    )

                    onUpdateItem(updatedItem)
                },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Close detail"
                    )
                }
            )

            Spacer(Modifier.weight(1F))

            IconButton(
                onClick = {
                    onDeleteItem()
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete task",
                        tint = Color.Red
                    )
                }
            )
        }

        OutlinedTextField(
            value = title.value,
            onValueChange = {
                title.value = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Title")
            },
            singleLine = true
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Due Date")

            Spacer(Modifier.weight(1F))

            TextButton(
                onClick = {
                    showDatePicker.value = true
                },
                content = {
                    val dueDateText = if (dueDate.value == 0L) {
                        "Not Setted"
                    } else {
                        dueDate.value.toDateTime()
                    }

                    Text(dueDateText)
                }
            )
        }

        OutlinedTextField(
            value = description.value,
            onValueChange = {
                description.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            label = {
                Text("Description")
            }
        )
    }

    if (showDatePicker.value) {
        DueDatePicker(showDatePicker) { selectedDueDate ->
            dueDate.value = selectedDueDate
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDatePicker(
    showDatePicker: MutableState<Boolean>,
    onSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        content = { DatePicker(datePickerState) },
        onDismissRequest = {
            datePickerState.selectedDateMillis = null
            showDatePicker.value = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onSelected(it)
                    }

                    showDatePicker.value = false
                },
                content = { Text("OK") }
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis = null
                    showDatePicker.value = false
                },
                content = { Text("Cancel") }
            )
        }
    )
}