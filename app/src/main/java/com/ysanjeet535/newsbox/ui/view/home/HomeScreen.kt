package com.ysanjeet535.newsbox.ui.view.home


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.glide.rememberGlidePainter
import com.ysanjeet535.newsbox.R
import com.ysanjeet535.newsbox.data.remote.dto.Article
import com.ysanjeet535.newsbox.data.remote.dto.Article.Companion.mapToNewsItem
import com.ysanjeet535.newsbox.ui.theme.GreenBoxDark
import com.ysanjeet535.newsbox.ui.theme.GreenBoxMedium
import com.ysanjeet535.newsbox.ui.theme.RedBoxDark
import com.ysanjeet535.newsbox.ui.theme.RedBoxMedium
import com.ysanjeet535.newsbox.ui.view.common.CardButtons
import com.ysanjeet535.newsbox.ui.view.common.LoadingCards
import com.ysanjeet535.newsbox.utils.ResponseHandler
import com.ysanjeet535.newsbox.utils.customToBottom
import com.ysanjeet535.newsbox.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@ExperimentalFoundationApi
@Composable
fun HomeScreenContent(paddingValues: Dp,mainViewModel: MainViewModel){

    val scrollState = rememberScrollState()
    val topHeadlines by mainViewModel.newsResponseLiveData.observeAsState()
    val context = LocalContext.current


    Column(modifier = Modifier
        .padding(bottom = paddingValues)
        .fillMaxSize()
        .background(Color.White)
        .verticalScroll(scrollState, enabled = true),
    ) {
        WelcomeText(
            Modifier.padding(16.dp).size(100.dp).align(Alignment.CenterHorizontally)
        )
        HeadingText(heading = "Top Headlines",Modifier.padding(16.dp))
        CountryCodeDropDown(mainViewModel)
        when(topHeadlines){
            is ResponseHandler.Success -> {
                val articles = topHeadlines?.data!!.articles
                LazyRow{
                    items(articles.size){
                        NewsItemCard(newsItem = articles[it],isOnProfileScreen = false){
                            mainViewModel.insertNewsItem(articles[it].mapToNewsItem(articles[it]))
                            Toast.makeText(context,"News added in read later Successfully!!",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            is ResponseHandler.Loading -> {
                LoadingCards()
            }
            is ResponseHandler.Error -> {
                NoInternetBox()
                Toast.makeText(LocalContext.current,"${topHeadlines?.message}",Toast.LENGTH_LONG).show()
            }
        }

        HeadingText(heading = "Topics",Modifier.padding(16.dp))
        //TopicGrid() //adding topic row instead to make this column vertical scroll possible
        TopicRow(mainViewModel)
    }
}

@Composable
fun WelcomeText(modifier: Modifier = Modifier){
    Box(modifier = modifier){
        Image(
            painter = rememberGlidePainter(request = R.mipmap.ic_launcher),
            contentDescription = "logo",
            modifier = Modifier.fillMaxSize().aspectRatio(0.8f)
        )
    }
}

@Composable
fun HeadingText(heading : String,modifier: Modifier = Modifier){
    Text(
        text = heading,
        style = MaterialTheme.typography.h2,
        color = Color.Black,
        modifier = modifier
    )
}

//@ExperimentalFoundationApi
//@Composable
//fun TopicGrid(){
//    val topic = listOf("Sports","Entertainment","Politics","Economy","Bollywood","Crypto","Movies")
//    LazyVerticalGrid(
//        cells = GridCells.Fixed(3) ){
//        items(topic.size){
//            itemIndex->
//            TopicCardItem(topic = topic[itemIndex],isTopicSelected = true,onTopicSelected = {})
//        }
//    }
//}


@Composable
fun TopicRow(mainViewModel: MainViewModel){
    val context = LocalContext.current
    val topic = listOf("business","entertainment","general","health","science","sports","technology")
    var topicIndexSelected by remember { mutableStateOf(0)}


    Column {


        LazyRow(modifier = Modifier.height(100.dp)) {
            items(topic.size) { itemIndex ->
                var isTopicSelected = topicIndexSelected == itemIndex
                TopicCardItem(
                    topic = topic[itemIndex].replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    },
                    aspectRatio = 2f,
                    isTopicSelected = isTopicSelected,
                    mainViewModel = mainViewModel,
                    onTopicSelected = { topicIndexSelected = itemIndex }
                )
            }
        }


        val topicHeadlines by mainViewModel.newsTopicResponseLiveData.observeAsState()

        when(topicHeadlines){
            is ResponseHandler.Success -> {
                val list = topicHeadlines?.data!!.articles
                LazyRow {

                    items(list.size) {
                        NewsItemCard(newsItem = list[it],isOnProfileScreen = false){
                            mainViewModel.insertNewsItem(list[it].mapToNewsItem(list[it]))
                            Toast.makeText(context,"News added in read later Successfully!!",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            is ResponseHandler.Loading -> {
                LoadingCards()
            }
            is ResponseHandler.Error -> {
                NoInternetBox()
                //Toast.makeText(LocalContext.current,"${topicHeadlines?.message}",Toast.LENGTH_LONG).show()
            }
        }

    }

}

@Composable
fun TopicCardItem(topic : String="Hello",aspectRatio : Float = 1f,isTopicSelected : Boolean, mainViewModel: MainViewModel,onTopicSelected :()-> Unit){

    val colorMedium by animateColorAsState(if(isTopicSelected) RedBoxMedium else GreenBoxMedium,animationSpec = tween(1500))
    val colorDark by animateColorAsState(if (isTopicSelected) RedBoxDark else GreenBoxDark,animationSpec = tween(1500))


        BoxWithConstraints(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(aspectRatio)
                .clip(RoundedCornerShape(10.dp))
                .background(colorDark)
                .clickable {
                    onTopicSelected()
                    mainViewModel.updateCategory(topic)
                    mainViewModel.getTopheadlinesOfTopic()
                }
        ) {
            val width = constraints.maxWidth
            val height = constraints.maxHeight

            //path points for medium shape path curve
            val mediumShapePoint1 = Offset(width.times(0.0f), height.times(0.3f))
            val mediumShapePoint2 = Offset(width.times(0.1f), height.times(0.35f))
            val mediumShapePoint3 = Offset(width.times(0.4f), height.times(0.05f))
            val mediumShapePoint4 = Offset(width.times(0.7f), height.times(075f))
            val mediumShapePoint5 = Offset(width.times(0.9f), height.times(0.5f))

            val mediumPath = Path().apply {
                moveTo(mediumShapePoint1.x, mediumShapePoint1.y)
                quadraticBezierTo(
                    mediumShapePoint2.x, mediumShapePoint2.y,
                    mediumShapePoint3.x, mediumShapePoint3.y

                )
                quadraticBezierTo(
                    mediumShapePoint3.x, mediumShapePoint3.y,
                    mediumShapePoint4.x, mediumShapePoint4.y
                )
                quadraticBezierTo(
                    mediumShapePoint4.x, mediumShapePoint4.y,
                    mediumShapePoint5.x, mediumShapePoint5.y
                )

                lineTo(width.toFloat(), height.toFloat())
                lineTo(-0f, height.toFloat())
                close()
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPath(mediumPath, colorMedium)
                drawCircle(
                    color = colorMedium,
                    radius = 5f,
                    center = Offset(width.toFloat().times(0.5f), height.toFloat().times(0.5f))
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = topic,
                    style = MaterialTheme.typography.h2,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }

        }

}



@Composable
fun NewsItemCard(newsItem: Article,isOnProfileScreen:Boolean,onDelete : ()-> Unit = {},onSaveLater : ()->Unit={}){
    //for web url
//    val context = LocalContext.current
//    val webIntent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.url)) }
    //this uri handler method is simpler than using traditional intents
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .padding(16.dp)
            .width(360.dp)
            .height(540.dp)
            .clip(
                RoundedCornerShape(
                    topEnd = 25.dp,
                    bottomStart = 25.dp,
                    topStart = 0.dp,
                    bottomEnd = 0.dp
                )
            ),
//            .clickable { uriHandler.openUri(newsItem.url) },
        elevation = 10.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        Column {
            Image(
                painter = rememberImagePainter(data = newsItem.urlToImage){
                      error(R.drawable.ic_error)
                },
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp)
                    .fillMaxWidth()
                ,
                contentScale = ContentScale.Crop
            )
            val title = if(newsItem.title.isNullOrBlank()) "Not available" else newsItem.title
            Text(

                text = title,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.h3
            )
            val description = if(newsItem.description.isNullOrBlank()) "Not available" else newsItem.description
            Text(
                text = description,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.body1,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            var sourceName = "Not available"
            newsItem.source?.let {
                sourceName = it.name?:"Not available"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sourceName,
                    modifier = Modifier.padding(8.dp)
                )

                val date = remember {
                    if(!newsItem.publishedAt.isNullOrEmpty()){
                        newsItem.publishedAt.dropLastWhile { it != 'T' }.dropLast(1)
                    } else
                        "Not available"
                }

                Text(
                    text = date,
                    modifier = Modifier.padding(8.dp),
                    fontStyle = FontStyle.Italic
                )
            }
            CardButtons(
                onOpen = { uriHandler.openUri(newsItem.url) },
                onSaveLater = { onSaveLater() },
                isOnProfileScreen = isOnProfileScreen,
                onDelete = onDelete,
                modifier = Modifier.customToBottom()
            )
        }
    }
}



@Composable
fun CountryCodeDropDown(mainViewModel: MainViewModel){
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("us","ca","in","ru","fr")
    var selectedIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .wrapContentSize(Alignment.TopStart)
        .background(Color.LightGray)

    ){
        Text(
            text = items[selectedIndex],
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })

        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        mainViewModel.updateCountryCode(s)
                        mainViewModel.getTopheadlines()
                    }
                ){
                    Text(text = s)
                }
            }
        }
        
    }

}







