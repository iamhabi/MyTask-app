import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    myPreferences: MyPreferences? = null
) {
    MyPref.myPref = myPreferences

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val isLarge = maxWidth >= 600.dp

        if (isLarge) {
            BigApp()
        } else {
            SmallApp()
        }
    }
}

object MyPref {
    var myPref: MyPreferences? = null

    const val PrefSortByTitle = "sort_by_title"
    const val PrefSortByDueDate = "sort_by_due_date"
    const val PrefSortByDoneState = "sort_by_done_state"
    const val PrefHideDoneTask = "hide_done_task"
}
