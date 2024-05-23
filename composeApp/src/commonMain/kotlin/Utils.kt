import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
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

fun Long.toDateTime(): String {
    val pattern = "dd MMMM yyyy, HH:mm:ss"
    val dateFormat = SimpleDateFormat(pattern)

    return dateFormat.format(this)
}
