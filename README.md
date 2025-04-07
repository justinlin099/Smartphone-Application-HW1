# Smartphone Application HW1
 Smartphone Application Design and Implementation Homework 1

## 1. 讓 Gemini 進入角色扮演的狀況
> 你現在是一位資工系的大學生，要完成一個 Android 程式的作業

![圖片](https://github.com/user-attachments/assets/c77760ea-5830-46f2-9aa2-3f69a191db1a)

## 2. 將作業內容告訴 AI
> 我接下來會給你作業的要求，請你一步一步的說出你的做法：  
> 參考網址：https://developer.android.com/codelabs/basic-android-kotlin-compose-art-space?hl=zh-tw#0  
> 作業要求：作業一  
> • 請參考以下的 Codelab 內容  
> • https://developer.android.com/codelabs/basic-android-kotlin-compose-art-space  
> • 請達到以下功能要求  
> • 完成 1-3  
> • 至少要有十張圖  
> • 每張圖要有說明  
> • 前一張後一張要能夠運作  
> • 如果在第一張圖按 Previous，會變回前一張圖  
> • 如果在最後一張圖按 Next，會跳到第一張圖  
> • Swiping 功能 (滑動手勢)  
> • 長按按鈕會有些文字說明 (tooltip)  
> • 可參考 https://material.io/blog/material-3-compose-1-1   

好的，學長/學姐，這個作業的要求我理解了。我會一步一步地說明我的做法：

**第一步：專案設定與環境準備**

1.  **確認開發環境：** 我會先確認我的 Android Studio 環境已經設定好，並且可以正常建立 Compose 專案。
2.  **建立新專案：** 在 Android Studio 中，我會建立一個新的 Android 專案，並選擇 "Empty Compose Activity" 範本。
3.  **加入必要的依賴：** 雖然這個作業主要參考 Codelab，但為了實現滑動手勢和 Tooltip，我可能會需要檢查 `build.gradle (app)` 檔案中是否有包含 Compose 相關的依賴，例如 `androidx.compose.material3`。如果沒有，我會根據 Material Design 3 的文件加入相關依賴。

**第二步：資料模型的建立**

1.  **定義資料類別：** 我會建立一個 data class 來表示每一張藝術品的資訊。這個類別至少會包含圖片的資源 ID (Int) 和圖片的說明文字 (String)。

    ```kotlin
    data class ArtPiece(val imageResource: Int, val description: String)
    ```

2.  **準備藝術品資料：** 我會在專案的 `res/drawable` 資料夾下準備至少十張圖片，並為每一張圖片撰寫說明文字。然後，我會在程式碼中建立一個 `List<ArtPiece>` 來存放這些藝術品的資料。

    ```kotlin
    val artPieces = listOf(
        ArtPiece(R.drawable.image1, "說明文字 1"),
        ArtPiece(R.drawable.image2, "說明文字 2"),
        // ... 總共十張
        ArtPiece(R.drawable.image10, "說明文字 10")
    )
    ```

**第三步：UI 介面的設計 (參考 Codelab Part 1-3)**

1.  **基本佈局：** 我會參考 Codelab 的做法，使用 Compose 建立基本的 UI 結構，包含顯示圖片的 `Image` 元件、顯示標題和作者的 `Column` 元件，以及 "Previous" 和 "Next" 的 `Button` 元件。我會使用 `Column` 來垂直排列這些元素。
2.  **狀態管理：** 我會使用 `remember` 和 `mutableStateOf` 來追蹤目前顯示的藝術品索引。這樣當索引改變時，UI 會自動更新。

    ```kotlin
    var currentArtIndex by remember { mutableStateOf(0) }
    val currentArt = artPieces[currentArtIndex]
    ```

3.  **顯示圖片與說明：** 在 `Image` 元件中，我會使用 `painterResource(id = currentArt.imageResource)` 來載入目前索引對應的圖片。在 `Column` 中，我會使用 `Text` 元件來顯示 `currentArt.description`。

**第四步：實作 "前一張" 和 "後一張" 功能**

1.  **Next 按鈕的邏輯：** 當按下 "Next" 按鈕時，我會將 `currentArtIndex` 加 1。為了實現循環播放，我會判斷如果 `currentArtIndex` 已經是最後一張圖片的索引，就將它重設為 0。

    ```kotlin
    Button(onClick = {
        currentArtIndex = (currentArtIndex + 1) % artPieces.size
    }) {
        Text("Next")
    }
    ```

2.  **Previous 按鈕的邏輯：** 當按下 "Previous" 按鈕時，我會將 `currentArtIndex` 減 1。同樣地，為了實現循環播放，我會判斷如果 `currentArtIndex` 是 0，就將它設定為最後一張圖片的索引。

    ```kotlin
    Button(onClick = {
        currentArtIndex = (currentArtIndex - 1 + artPieces.size) % artPieces.size
    }) {
        Text("Previous")
    }
    ```

**第五步：實作 Swiping 功能 (滑動手勢)**

1.  **引入 `Modifier.swipeable`：** 我會使用 Compose 的 `Modifier.swipeable` 來為圖片添加滑動手勢。
2.  **定義滑動狀態：** 我需要定義一個 `SwipeableState` 來追蹤滑動的狀態。
3.  **設定滑動方向和錨點：** 我會設定滑動方向為水平，並定義滑動的錨點，例如滑動到一定距離後觸發切換圖片的動作。
4.  **處理滑動事件：** 在滑動結束時，我會根據滑動的方向來更新 `currentArtIndex`，實現切換到前一張或後一張圖片的功能。

    ```kotlin
    val swipeableState = rememberSwipeableState(initialValue = 0) { newValue ->
        if (newValue != 0) {
            // 滑動結束，根據 newValue 的正負判斷方向
            if (newValue > 0) {
                currentArtIndex = (currentArtIndex - 1 + artPieces.size) % artPieces.size
            } else {
                currentArtIndex = (currentArtIndex + 1) % artPieces.size
            }
            true // 表示狀態已處理
        } else {
            true
        }
    }
    val anchors = mapOf(0f to 0, -500f to -1, 500f to 1) // 根據實際情況調整錨點值

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                orientation = Orientation.Horizontal,
                resistance = null // 可選，設定滑動阻力
            )
    ) {
        Image(
            painter = painterResource(id = currentArt.imageResource),
            contentDescription = currentArt.description,
            modifier = Modifier.offset(x = swipeableState.offset.value.dp)
        )
    }
    ```

**第六步：實作長按按鈕的 Tooltip**

1.  **引入 `androidx.compose.material.ExperimentalMaterialApi` 和 `androidx.compose.material.TooltipBox`：** 雖然 Codelab 沒有直接提到 Tooltip，但根據作業要求，我會使用 Compose Material Design 提供的 `TooltipBox` 來實現這個功能。
2.  **使用 `rememberTooltipState`：** 我會使用 `rememberTooltipState` 來管理 Tooltip 的顯示狀態。
3.  **包裹按鈕：** 我會將 "Previous" 和 "Next" 按鈕包裹在 `TooltipBox` 中。
4.  **設定觸發方式：** `TooltipBox` 預設會在長按時顯示 Tooltip。
5.  **提供 Tooltip 內容：** 在 `TooltipBox` 的 `tooltip` lambda 中，我會使用 `Text` 元件來顯示說明文字。

    ```kotlin
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MyButtonWithTooltip(text: String, tooltipText: String, onClick: () -> Unit) {
        val tooltipState = rememberTooltipState()
        TooltipBox(
            state = tooltipState,
            tooltip = {
                Text(tooltipText)
            }
        ) {
            Button(onClick = onClick) {
                Text(text)
            }
        }
    }

    // 在 UI 中使用
    MyButtonWithTooltip(
        text = "Previous",
        tooltipText = "點擊顯示前一張圖片",
        onClick = { /* ... */ }
    )

    MyButtonWithTooltip(
        text = "Next",
        tooltipText = "點擊顯示後一張圖片",
        onClick = { /* ... */ }
    )
    ```

**第七步：測試與調整**

1.  **逐步測試：** 我會逐步測試每個功能，確保 "前一張"、"後一張"、循環播放、滑動手勢和 Tooltip 都能正常運作。
2.  **UI 優化：** 如果 UI 顯示效果不理想，我會調整佈局、樣式和動畫效果，使其更符合 Material Design 的規範。

**總結**

以上就是我完成這個 Android 作業的步驟。我會先建立基本的 UI 結構，然後逐步實作各項功能，並在最後進行測試和優化。在實作過程中，我會仔細參考 Codelab 的內容，並查閱 Compose Material Design 的相關文件，確保程式碼的正確性和可讀性。

學長/學姐，你覺得這樣的做法如何呢？有沒有什麼地方需要補充或修改的？我很樂意聽取你的意見！

## 3. 照著 AI 給的步驟開始實作
a. 準備十張照片放進 /res/drawables
![圖片](https://github.com/user-attachments/assets/378b8d0b-765e-41fe-b64c-0db86ea09809)
b. 發現用 Windows 自動命名的圖片會有空格，Android 程式不能使用，只能手動重新命名
![圖片](https://github.com/user-attachments/assets/637854fa-5294-4249-b1d7-04a0275e8aff)
![圖片](https://github.com/user-attachments/assets/37e0af9c-3d23-40c0-a00b-02b531f2f307)
c. 編輯各個圖片的說明
![圖片](https://github.com/user-attachments/assets/c6a63ed4-cb7d-41e0-b846-e08d95953026)

## 4. 因為我沒耐心一個一個對著改寫了，所以我請 AI 直接把檔案名稱跟程式碼給我
> 請生成出各個檔案的完整程式碼

好的，學長/學姐，這就為你生成各個檔案的完整程式碼。

**1. `app/src/main/java/你的包名/MainActivity.kt`**

請將 `你的包名` 替換成你專案實際的包名。

```kotlin
package 你的包名

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
import androidx.compose.material.*
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
import 你的包名.ui.theme.ArtSpaceTheme
import androidx.compose.material3.Button as Material3Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon as Material3Icon
import androidx.compose.material3.Text as Material3Text
import androidx.compose.material3.TooltipBox as Material3TooltipBox
import androidx.compose.material3.TooltipState as Material3TooltipState
import androidx.compose.material3.rememberTooltipState as rememberMaterial3TooltipState

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
            ArtPiece(R.drawable.image1, "星夜", "文森·梵高", 1889),
            ArtPiece(R.drawable.image2, "蒙娜麗莎", "列奧納多·達文西", 1517),
            ArtPiece(R.drawable.image3, "吶喊", "愛德華·孟克", 1893),
            ArtPiece(R.drawable.image4, "戴珍珠耳環的少女", "約翰內斯·維米爾", 1665),
            ArtPiece(R.drawable.image5, "記憶的永恆", "薩爾瓦多·達利", 1931),
            ArtPiece(R.drawable.image6, "神奈川沖浪裏", "葛飾北齋", 1831),
            ArtPiece(R.drawable.image7, "向日葵", "文森·梵高", 1888),
            ArtPiece(R.drawable.image8, "最後的晚餐", "列奧納多·達文西", 1498),
            ArtPiece(R.drawable.image9, "美國哥德式", "葛蘭特·伍德", 1930),
            ArtPiece(R.drawable.image10, "夜巡", "林布蘭", 1642)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0) { artPieces.size }

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
            key = { artPieces[it].imageResource } // 確保在切換時重新組合
        ) { page ->
            val artPiece = artPieces[page]
            androidx.compose.foundation.Image(
                painter = painterResource(id = artPiece.imageResource),
                contentDescription = stringResource(R.string.art_piece_content_description, artPiece.title),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
            )
        }

        // 藝術品資訊
        val currentArtPiece by remember { derivedStateOf { artPieces[pagerState.currentPage] } }
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
                    if (pagerState.currentPage > 0) {
                        pagerState.currentPage -= 1
                    } else {
                        pagerState.currentPage = artPieces.size - 1
                    }
                }
            )
            NextButton(
                onClick = {
                    if (pagerState.currentPage < artPieces.size - 1) {
                        pagerState.currentPage += 1
                    } else {
                        pagerState.currentPage = 0
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousButton(onClick: () -> Unit) {
    val tooltipState = rememberMaterial3TooltipState()
    Material3TooltipBox(
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
```

**2. `app/src/main/java/你的包名/ui/theme/Theme.kt`**

這個檔案通常在建立專案時就已經存在，用於定義應用程式的主題。如果你的專案沒有做過多的修改，這個檔案可以保持預設。如果需要，你可以根據 Material Design 3 的規範進行調整。

```kotlin
package 你的包名.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ArtSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

**3. `app/src/main/res/values/strings.xml`**

在這個檔案中，你可以定義應用程式中使用的字串資源。

```xml
<resources>
    <string name="app_name">ArtSpace</string>
    <string name="previous">Previous</string>
    <string name="next">Next</string>
    <string name="art_piece_content_description">Artwork: %s</string>
</resources>
```

**4. 在 `app/src/main/res/drawable` 資料夾中新增至少十張圖片**

請將你的圖片檔案（例如 `image1.jpg`, `image2.png` 等）放入 `app/src/main/res/drawable` 資料夾中。並在 `MainActivity.kt` 的 `artPieces` 列表中正確引用這些圖片的資源 ID (`R.drawable.你的圖片檔名`).

**注意事項：**

* 請確保將 `你的包名` 替換為你專案實際的包名。
* 你需要至少準備十張圖片，並將它們添加到 `res/drawable` 資料夾中。
* 在 `MainActivity.kt` 的 `artPieces` 列表中，你需要為每一張圖片提供正確的資源 ID、標題、藝術家和年份。
* 這個程式碼使用了 `androidx.compose.foundation.pager.HorizontalPager` 來實現滑動切換圖片的功能，你需要確保你的專案已經添加了相關的依賴。通常在建立 Compose 專案時會自動添加。

完成以上步驟後，你就可以在你的 Android 裝置或模擬器上運行這個應用程式了。這個程式碼應該能夠滿足你的作業要求。如果你在運行過程中遇到任何問題，歡迎隨時提出！

## 5. 執行並測試，發現錯誤
看來 AI 搞錯 index Value 只能 get 不能手動修改的部分了
![圖片](https://github.com/user-attachments/assets/3b3a4eb1-c2fa-4428-913f-255951d971da)

> 我遇到以下錯誤：
> e: file:///C:/Users/justi/Documents/GitHub/Smartphone-Application-HW1/ArtSpace/app/src/main/java/com/justinlin099/artspace/MainActivity.kt:122:36 'val' cannot be reassigned.

好的，學長/學姐，這個錯誤很常見，是因為你在 `onClick` 裡面直接嘗試修改用 `val` 宣告的變數。在 Compose Pager 中，`pagerState.currentPage` 是用來**讀取**目前頁面的狀態，而**不能直接賦值**來改變頁面。

你需要使用 `PagerState` 提供的函數來切換頁面。以下是修正後的程式碼片段：

將 `MainActivity.kt` 中 `PreviousButton` 和 `NextButton` 的 `onClick` 邏輯修改如下：

**修改 PreviousButton 的 onClick：**

```kotlin
PreviousButton(
    onClick = {
        if (pagerState.currentPage > 0) {
            pagerState.scrollToPage(pagerState.currentPage - 1)
        } else {
            pagerState.scrollToPage(artPieces.size - 1)
        }
    }
)
```

**修改 NextButton 的 onClick：**

```kotlin
NextButton(
    onClick = {
        if (pagerState.currentPage < artPieces.size - 1) {
            pagerState.scrollToPage(pagerState.currentPage + 1)
        } else {
            pagerState.scrollToPage(0)
        }
    }
)
```

**完整修改後的 `ArtSpaceScreen` 函式：**

```kotlin
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtSpaceScreen() {
    val artPieces = remember {
        listOf(
            ArtPiece(R.drawable.image1, "星夜", "文森·梵高", 1889),
            ArtPiece(R.drawable.image2, "蒙娜麗莎", "列奧納多·達文西", 1517),
            ArtPiece(R.drawable.image3, "吶喊", "愛德華·孟克", 1893),
            ArtPiece(R.drawable.image4, "戴珍珠耳環的少女", "約翰內斯·維米爾", 1665),
            ArtPiece(R.drawable.image5, "記憶的永恆", "薩爾瓦多·達利", 1931),
            ArtPiece(R.drawable.image6, "神奈川沖浪裏", "葛飾北齋", 1831),
            ArtPiece(R.drawable.image7, "向日葵", "文森·梵高", 1888),
            ArtPiece(R.drawable.image8, "最後的晚餐", "列奧納多·達文西", 1498),
            ArtPiece(R.drawable.image9, "美國哥德式", "葛蘭特·伍德", 1930),
            ArtPiece(R.drawable.image10, "夜巡", "林布蘭", 1642)
        )
    }

    val pagerState = rememberPagerState(initialPage = 0) { artPieces.size }

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
            key = { artPieces[it].imageResource } // 確保在切換時重新組合
        ) { page ->
            val artPiece = artPieces[page]
            androidx.compose.foundation.Image(
                painter = painterResource(id = artPiece.imageResource),
                contentDescription = stringResource(R.string.art_piece_content_description, artPiece.title),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
            )
        }

        // 藝術品資訊
        val currentArtPiece by remember { derivedStateOf { artPieces[pagerState.currentPage] } }
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
                    if (pagerState.currentPage > 0) {
                        pagerState.scrollToPage(pagerState.currentPage - 1)
                    } else {
                        pagerState.scrollToPage(artPieces.size - 1)
                    }
                }
            )
            NextButton(
                onClick = {
                    if (pagerState.currentPage < artPieces.size - 1) {
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                    } else {
                        pagerState.scrollToPage(0)
                    }
                }
            )
        }
    }
}
```

**原因解釋：**

`rememberPagerState` 返回的 `pagerState` 物件，其 `currentPage` 屬性是一個 `val`，代表它是一個狀態（State），你應該透過 `PagerState` 提供的 `scrollToPage()` 或 `animateScrollToPage()` 函數來改變這個狀態，而不是直接賦值。

請將你的 `MainActivity.kt` 檔案中的 `ArtSpaceScreen` 函式內的 `PreviousButton` 和 `NextButton` 的 `onClick` 邏輯按照上面的程式碼進行修改，應該就能解決這個錯誤了。

如果還有其他問題，隨時都可以再問我喔！加油！💪

## 6. 接下來又遇到更多奇怪的錯誤，一直跟 AI 來來回回，AI 請我降級 Compose 版本但是他給我的版本不存在，後來我只能嘗試升級到最新版，結果就解決了

> e: file:///C:/Users/justi/Documents/GitHub/Smartphone-Application-HW1/ArtSpace/app/src/main/java/com/justinlin099/artspace/MainActivity.kt:153:13 @Composable invocations can only happen from the context of a @Composable function

好的，學長/學姐，這個錯誤非常令人困惑，因為它指向的程式碼位置看起來是完全合法的 Compose 呼叫。@Composable invocations can only happen from the context of a @Composable function 這個錯誤通常發生在我們嘗試在非 Composable 的函數或 lambda 中呼叫 Composable 函數時。

然而，你提供的錯誤指向 HorizontalPager 的 pageContent lambda 內的 androidx.compose.foundation.Image 或是 Material3Text，而這個 lambda 本身就是 Composable 的。

這時候，我們需要考慮一些比較隱晦的可能性：

1. Build 問題或 IDE 錯誤：

這是最常見導致這種看似不合理錯誤的原因之一。請嘗試以下步驟：

    Clean Project： 在 Android Studio 中，選擇 Build > Clean Project。
    Rebuild Project： 清潔完成後，選擇 Build > Rebuild Project。
    Invalidate Caches and Restart： 如果 Rebuild 後問題仍然存在，選擇 File > Invalidate Caches / Restart...，然後選擇 Invalidate and Restart。

這些操作可以清除 Android Studio 的快取和重新編譯專案，有時候能解決一些奇怪的編譯錯誤。

2. 檢查 Import 語句：

雖然看起來你的 import 語句是正確的，但還是請仔細檢查 MainActivity.kt 檔案頂部的 import 語句，確保沒有遺漏或錯誤的 import。特別是 androidx.compose.foundation.Image 和 androidx.compose.material3.Text 是否正確 import。

3. Compose 庫版本衝突：

如果你的專案中使用了多個 Compose 相關的庫，並且它們的版本不一致，有時候也會導致這種錯誤。請檢查你的 app/build.gradle 檔案中的 dependencies 部分，確保所有 Compose 相關的庫（例如 androidx.compose.ui, androidx.compose.material3, androidx.compose.foundation, androidx.compose.runtime, androidx.compose.pager 等）使用了相容的版本。建議使用 Android Studio 推薦的最新穩定版本，或者使用 compose-bom 來管理 Compose 的版本。

4. 資源檔案問題：

如果錯誤指向的是 androidx.compose.foundation.Image，那麼問題可能與圖片資源的載入有關。請確保：

    你的圖片檔案 (例如 image1.jpg, image2.png 等) 確實存在於 app/src/main/res/drawable 資料夾中。
    你在 artPieces 列表中使用的 R.drawable.你的圖片檔名 是正確的，並且與實際的圖片檔名（不包含副檔名）一致。
    嘗試 Clean 和 Rebuild 專案，以確保 R 檔案被正確生成。

5. 程式碼邏輯錯誤（可能性較低，但仍需檢查）：

仔細檢查你的程式碼邏輯，特別是在 HorizontalPager 的 pageContent lambda 中，看看是否有任何不尋常的操作或狀態存取可能導致 Composable 上下文失效。但就你目前提供的程式碼來看，這個可能性較低。

請先嘗試執行上述的 Build 問題解決步驟（Clean, Rebuild, Invalidate Caches / Restart）。 這通常能解決很多類似的編譯錯誤。

如果問題仍然存在，請提供你的 app/build.gradle 檔案的 dependencies 部分，以及你使用的 Android Studio 和 Compose 庫的版本資訊，這樣我能更精確地判斷問題所在。

不要擔心，我們一定能找到問題的！ 😊


![圖片](https://github.com/user-attachments/assets/a772de4f-43a2-4299-8f46-bc3ac8ffb56f)
更新到最新版 2.1.20 才會動，AI給的根本沒用

## 7. 程式成功執行，但是捲到特定一張圖片就會當機
![圖片](https://github.com/user-attachments/assets/7d10928c-d891-4629-aa2a-d00bf0743177)
從 LogCat 查看後發現是圖片檔案太大
於是我把圖片縮成原本的一半大小，就可以正常執行了
![圖片](https://github.com/user-attachments/assets/ce3ae663-d65f-41a3-a104-bd733d93ca84)

## 8. 人工細微調整
原本圖片沒有置中，我稍微調整了參數就正常了，圖片正常填滿
![圖片](https://github.com/user-attachments/assets/f0f79829-a3df-4a9a-962a-1d6da78429de)


