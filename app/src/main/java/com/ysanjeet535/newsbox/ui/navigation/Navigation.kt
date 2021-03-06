package com.ysanjeet535.newsbox.ui.navigation

import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ysanjeet535.newsbox.ui.view.explore.ExploreScreenContent
import com.ysanjeet535.newsbox.ui.view.home.HomeScreenContent
import com.ysanjeet535.newsbox.ui.view.profile.ProfileScreenContent
import com.ysanjeet535.newsbox.viewmodel.MainViewModel

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun NavigationComponent(
    navController: NavController,paddingValues: Dp,mainViewModel: MainViewModel
){
    NavHost(navController = navController as NavHostController, startDestination = Screens.Home.name){
        composable(Screens.Home.name){
            HomeScreenContent(paddingValues = paddingValues,mainViewModel = mainViewModel)
        }

        composable(Screens.Explore.name){
            ExploreScreenContent(paddingValues = paddingValues,mainViewModel = mainViewModel)
        }

        composable(Screens.Profile.name){
            ProfileScreenContent(paddingValues = paddingValues,mainViewModel = mainViewModel)
        }
    }
}
