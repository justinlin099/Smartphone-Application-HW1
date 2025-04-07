package com.justinlin099.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justinlin099.artspace.ui.theme.ArtSpaceTheme
import androidx.compose.material3.Button as Material3Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.Icon as Material3Icon
import androidx.compose.material3.Text as Material3Text
import androidx.compose.material3.TooltipBox as Material3TooltipBox
import androidx.compose.material3.TooltipState as Material3TooltipState
import androidx.compose.material3.rememberTooltipState as rememberMaterial3TooltipState
import kotlinx.coroutines.launch



data class ArtPiece(val imageResource: Int, val title: String, val artist: String, val year: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceApp()
        }
    }
}

@Composable
fun ArtSpaceApp() {
    ArtSpaceTheme {
        ArtSpaceScreen()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtSpaceScreen() {
    val artPieces = remember {
        listOf(
            ArtPiece(R.drawable.image1, "自然小屋" , "pexels.com" , 2025),
            ArtPiece(R.drawable.image2, "白色的漂亮廚房", "pexels.com" , 2025),
            ArtPiece(R.drawable.image3, "漂亮的灣區房子", "pexels.com" , 2025),
            ArtPiece(R.drawable.image4, "溫暖的休閒空間", "pexels.com" , 2025),
            ArtPiece(R.drawable.image5, "現代時尚客廳", "pexels.com" , 2025),
            ArtPiece(R.drawable.image6, "夢想中的房子", "pexels.com" , 2025),
            ArtPiece(R.drawable.image7, "有著兩個車庫的大房子", "pexels.com" , 2025),
            ArtPiece(R.drawable.image8, "有大庭院的豪宅", "pexels.com" , 2025),
            ArtPiece(R.drawable.image9, "現代中島廚房", "pexels.com" , 2025),
            ArtPiece(R.drawable.image10, "超酷的房子", "pexels.com" , 2025)
        )
    }

    val initialPage = 1000 / 2 // 設定初始頁面在中間位置
    val pagerState = rememberPagerState(initialPage = initialPage) { 1000 } // 將 pageCount 設定為無限大
    val scope = rememberCoroutineScope() // 取得 CoroutineScope

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        // 藝術品展示區域
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 32.dp),
            key = { page -> page } // 使用實際索引作為 key
        ) { page ->
            val actualIndex = page % artPieces.size // 使用模數運算取得實際索引
            val artPiece = artPieces[actualIndex]
            androidx.compose.foundation.Image(
                painter = painterResource(id = artPiece.imageResource),
                contentDescription = stringResource(R.string.art_piece_content_description, artPiece.title),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1f)
            )
        }

        // 藝術品資訊
        val currentArtPiece by remember { derivedStateOf { artPieces[pagerState.currentPage % artPieces.size] } }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Material3Text(text = currentArtPiece.title, fontSize = 24.sp)
            Material3Text(text = "${currentArtPiece.artist} (${currentArtPiece.year})", fontSize = 16.sp)
        }

        // 控制按鈕
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            PreviousButton(
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage - 1)
                    }
                }
            )
            NextButton(
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PreviousButton(onClick: () -> Unit) {
    val tooltipState = rememberMaterial3TooltipState()
    Material3TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            Material3Text("顯示前一張")
        },
        state = tooltipState
    ) {
        Material3Button(onClick = onClick) {
            Material3Icon(Icons.Filled.ArrowBack, contentDescription = "Previous")
            Spacer(modifier = Modifier.width(4.dp))
            Material3Text("Previous")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextButton(onClick: () -> Unit) {
    val tooltipState = rememberMaterial3TooltipState()
    Material3TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            Material3Text("顯示後一張")
        },
        state = tooltipState
    ) {
        Material3Button(onClick = onClick) {
            Material3Text("Next")
            Spacer(modifier = Modifier.width(4.dp))
            Material3Icon(Icons.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArtSpaceApp()
}

