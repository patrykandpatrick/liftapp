package pl.patrykgoworowski.mintlift.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import pl.patrykgoworowski.mintlift.core.navigation.Routes
import pl.patrykgoworowski.mintlift.core.ui.theme.LiftAppTheme
import pl.patrykgoworowski.mintlift.feature.main.ui.Main
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var windowInsetsControllerCompat: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

//        window.decorView.layoutParams = WindowManager.LayoutParams().apply {
//            fitInsetsTypes = android.view.WindowInsets.Type.navigationBars()
//            isFitInsetsIgnoringVisibility = true
//        }
//        window.addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        super.onCreate(savedInstanceState)

        setContent {
            LiftAppTheme {
                Navigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberAnimatedNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
    ) {

        AnimatedNavHost(
            navController = navController,
            startDestination = Routes.Menu.value,
        ) {

            composable(Routes.Menu.value) {
                Main(parentNavController = navController)
            }
        }
    }
}
