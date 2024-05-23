package view.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TaskListTopLayout(
    isSortByTitle: MutableState<Boolean>,
    isSortByDueDate: MutableState<Boolean>,
    isSortByDoneState: MutableState<Boolean>,
    isHideDoneTask: MutableState<Boolean>
) {
    val isShowMore = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box {
            IconButton(
                onClick = { isShowMore.value = true },
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Task Settings"
                    )
                }
            )

            DropdownMenu(
                expanded = isShowMore.value,
                onDismissRequest = { isShowMore.value = false },
                content = {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        SelectSortMethod(
                            isSortByTitle,
                            isSortByDueDate,
                            isSortByDoneState
                        )

                        Divider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isHideDoneTask.value,
                                onCheckedChange = {
                                    isHideDoneTask.value = it
                                }
                            )
                            
                            Text("Hide done task")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SelectSortMethod(
    isSortByTitle: MutableState<Boolean>,
    isSortByDueDate: MutableState<Boolean>,
    isSortByDoneState: MutableState<Boolean>
) {
    Column {
        Text("Sort Method")

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSortByTitle.value,
                onCheckedChange = {
                    isSortByTitle.value = it
                }
            )

            Text("Title")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSortByDueDate.value,
                onCheckedChange = {
                    isSortByDueDate.value = it
                }
            )

            Text("Due date")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSortByDoneState.value,
                onCheckedChange = {
                    isSortByDoneState.value = it
                }
            )

            Text("Is done")
        }
    }
}