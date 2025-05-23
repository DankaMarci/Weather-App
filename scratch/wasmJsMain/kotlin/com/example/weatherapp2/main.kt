import androidx.compose.ui.window.CanvasBasedWindow
import com.example.weatherapp2.App
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        CanvasBasedWindow("My WASM App") {
            App()
        }
    }
}