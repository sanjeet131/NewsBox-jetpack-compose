package com.ysanjeet535.newsbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ysanjeet535.newsbox.data.db.NewsItemDatabase
import com.ysanjeet535.newsbox.data.model.NewsItem
import com.ysanjeet535.newsbox.data.model.Source
import com.ysanjeet535.newsbox.data.remote.NewsApi
import com.ysanjeet535.newsbox.data.remote.RetrofitHelper
import com.ysanjeet535.newsbox.data.repository.NewsArticleRepository
import com.ysanjeet535.newsbox.ui.custom_components.BottomNavBar
import com.ysanjeet535.newsbox.ui.navigation.NavigationComponent
import com.ysanjeet535.newsbox.ui.navigation.Screens
import com.ysanjeet535.newsbox.ui.theme.NewsBoxTheme
import com.ysanjeet535.newsbox.ui.view.home.HomeScreenContent
import com.ysanjeet535.newsbox.utils.Constants.API_KEY
import com.ysanjeet535.newsbox.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    @SuppressLint("UnrememberedMutableState", "CoroutineCreationDuringComposition")
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = NewsItemDatabase.getDatabase(this)
        val mainViewModel by viewModels<MainViewModel>()
        setContent{
            val navController = rememberNavController()
            val currentScreen = mutableStateOf<Screens>(Screens.Home)
            GlobalScope.launch {
                db.newsItemDao().insertNewsItem(NewsItem(
                    1,
                    "barney",
                    "no content",
                    "desp",
                    "334343s",
                    Source("3", "4"),
                    "gsgs",
                    "sfs",
                    "fsfs"
                ))
            }
            
            NewsBoxTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    
                    Scaffold(
                        bottomBar = {

                            BottomNavBar(currentScreen = currentScreen.value.name,navController){
                                currentScreen.value = it
                            }
                        }
                    ) {
                            NavigationComponent(navController = navController,it.calculateBottomPadding(),mainViewModel)
                        
                    }


                }
            }
        }
    }
}



