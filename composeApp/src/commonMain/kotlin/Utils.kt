import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import task.TaskItem
import java.text.SimpleDateFormat

internal fun Modifier.onKeyUp(key: Key, action: () -> Unit): Modifier =
    onKeyEvent { event ->
        if ((event.type == KeyEventType.KeyUp) && (event.key == key)) {
            action()
            true
        } else {
            false
        }
    }

// https://stackoverflow.com/a/69146178
internal fun Modifier.disableChilds(disabled: Boolean): Modifier =
    if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }

fun Long.toDateTime(): String {
    val pattern = "dd MMMM yyyy, HH:mm:ss"
    val dateFormat = SimpleDateFormat(pattern)

    return dateFormat.format(this)
}

fun List<TaskItem>.getSortedIndex(taskItem: TaskItem): Int {
    val comparator = Comparator<TaskItem> { o1, o2 ->
        o1.id.compareTo(o2.id)
    }

    var index = this.binarySearch(taskItem, comparator)

    if (index < 0) {
        index = -(index + 1)
    }

    return index
}