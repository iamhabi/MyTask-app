import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
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