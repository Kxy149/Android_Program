package com.example.myfirstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkotlinapp.ui.theme.MyFirstKotlinAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstKotlinAppTheme {
                ComposePracticeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposePracticeApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Compose 组件练习") },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    Triple("布局", Icons.Filled.Home, 0),
                    Triple("组件", Icons.Filled.Star, 1),
                    Triple("列表", Icons.Filled.Person, 2),
                )
                items.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> LayoutTab(modifier = Modifier.padding(innerPadding))
            1 -> ComponentsTab(modifier = Modifier.padding(innerPadding))
            2 -> ListTab(modifier = Modifier.padding(innerPadding))
        }
    }
}

// ==================== 1. 布局练习 ====================
@Composable
fun LayoutTab(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 1.1 Text 文本样式
        item { SectionHeader("1. Text 文本样式") }
        item { TextStylesDemo() }

        // 1.2 Column 垂直布局
        item { SectionHeader("2. Column 垂直布局") }
        item { ColumnDemo() }

        // 1.3 Row 水平布局
        item { SectionHeader("3. Row 水平布局") }
        item { RowDemo() }

        // 1.4 Box 层叠布局
        item { SectionHeader("4. Box 层叠布局") }
        item { BoxDemo() }

        // 1.5 Spacer / weight
        item { SectionHeader("5. Spacer & Weight 分配空间") }
        item { SpacerDemo() }

        // 1.6 Modifier 链式调用
        item { SectionHeader("6. Modifier 修饰符链") }
        item { ModifierChainDemo() }
    }
}

// ==================== 2. 组件练习 ====================
@Composable
fun ComponentsTab(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { SectionHeader("1. Button 按钮样式") }
        item { ButtonDemo() }

        item { SectionHeader("2. TextField 文本输入") }
        item { TextFieldDemo() }

        item { SectionHeader("3. Card 卡片") }
        item { CardDemo() }

        item { SectionHeader("4. Checkbox / Switch / Slider") }
        item { ToggleDemo() }

        item { SectionHeader("5. ProgressIndicator 进度条") }
        item { ProgressDemo() }

        item { SectionHeader("6. Icon 图标") }
        item { IconDemo() }
    }
}

// ==================== 3. 列表练习 ====================
@Composable
fun ListTab(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { SectionHeader("1. LazyRow 横向滚动列表") }
        item { LazyRowDemo() }

        item { SectionHeader("2. LazyColumn 长列表（当前页本身就是）") }
        item {
            Text(
                text = "你正在看的这个页面就是用 LazyColumn 实现的！\n\n" +
                    "LazyColumn 只渲染屏幕上可见的 item，适合显示大量数据。",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        item { SectionHeader("3. 模拟联系人列表") }
        item { ContactListDemo() }
    }
}

// ============= 章节标题 =============
@Composable
fun SectionHeader(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// ============= 1.1 Text 文本样式 =============
@Composable
fun TextStylesDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("displayLarge", style = MaterialTheme.typography.displayLarge)
        Text("headlineMedium", style = MaterialTheme.typography.headlineMedium)
        Text("titleLarge", style = MaterialTheme.typography.titleLarge)
        Text("bodyLarge (默认)", style = MaterialTheme.typography.bodyLarge)
        Text("bodyMedium", style = MaterialTheme.typography.bodyMedium)
        Text("labelSmall", style = MaterialTheme.typography.labelSmall)
        Text(
            text = "自定义颜色和粗细的文字，超长内容会被截断处理显示省略号",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ============= 1.2 Column 垂直布局 =============
@Composable
fun ColumnDemo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(60.dp).background(Color.Red, RoundedCornerShape(8.dp)))
        Text("Arrangement.spacedBy(12.dp)")
        Box(modifier = Modifier.size(60.dp).background(Color.Green, RoundedCornerShape(8.dp)))
        Text("Alignment.CenterHorizontally")
        Box(modifier = Modifier.size(60.dp).background(Color.Blue, RoundedCornerShape(8.dp)))
    }
}

// ============= 1.3 Row 水平布局 =============
@Composable
fun RowDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 等分
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow).forEach { color ->
                Box(modifier = Modifier.size(50.dp).background(color, RoundedCornerShape(8.dp)))
            }
        }
        Text("Arrangement.SpaceEvenly", style = MaterialTheme.typography.bodySmall)

        HorizontalDivider()

        // 两端对齐
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("左对齐", fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
            Text("右对齐", fontWeight = FontWeight.Bold)
        }
        Text("Arrangement.SpaceBetween + Alignment.CenterVertically", style = MaterialTheme.typography.bodySmall)
    }
}

// ============= 1.4 Box 层叠布局 =============
@Composable
fun BoxDemo() {
    Box(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF667eea), Color(0xFF764ba2))),
                    RoundedCornerShape(12.dp)
                )
        )
        Text("Box 居中叠加文字", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

// ============= 1.5 Spacer & Weight =============
@Composable
fun SpacerDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("左边", modifier = Modifier.background(Color(0xFFFFCDD2)))
            Spacer(modifier = Modifier.weight(1f))
            Text("右边", modifier = Modifier.background(Color(0xFFBBDEFB)))
        }
        Text("Spacer(weight=1f) 把剩余空间撑开", style = MaterialTheme.typography.bodySmall)

        Row(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.weight(1f).height(40.dp).background(Color(0xFFE91E63), RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.weight(2f).height(40.dp).background(Color(0xFF2196F3), RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.weight(1f).height(40.dp).background(Color(0xFF4CAF50), RoundedCornerShape(4.dp)))
        }
        Text("weight(1f) : weight(2f) : weight(1f) = 1:2:1 比例分配", style = MaterialTheme.typography.bodySmall)
    }
}

// ============= 1.6 Modifier 链式调用 =============
@Composable
fun ModifierChainDemo() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("顺序敏感", textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium)
    }
}

// ============= 2.1 Button 按钮样式 =============
@Composable
fun ButtonDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}) { Text("Filled") }
            ElevatedButton(onClick = {}) { Text("Elevated") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = {}) { Text("Tonal") }
            OutlinedButton(onClick = {}) { Text("Outlined") }
        }
        TextButton(onClick = {}) { Text("TextButton（纯文字按钮）") }
    }
}

// ============= 2.2 TextField 文本输入 =============
@Composable
fun TextFieldDemo() {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("请输入内容") },
            placeholder = { Text("占位文字...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (text.isNotEmpty()) {
            Text("你输入了: $text", color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ============= 2.3 Card 卡片 =============
@Composable
fun CardDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Card 卡片标题", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Material3 的 Card 组件自带圆角、阴影和颜色主题适配。" +
                    "可以配合 CardDefaults 自定义外观。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = {}) { Text("取消") }
                Button(onClick = {}) { Text("确认") }
            }
        }
    }
}

// ============= 2.4 Checkbox / Switch / Slider =============
@Composable
fun ToggleDemo() {
    var checked by remember { mutableStateOf(true) }
    var switched by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableFloatStateOf(0.5f) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { checked = it })
            Text("Checkbox: ${if (checked) "选中" else "未选中"}")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = switched, onCheckedChange = { switched = it })
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch: ${if (switched) "开" else "关"}")
        }

        Text("Slider: ${String.format("%.2f", sliderValue)}")
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ============= 2.5 ProgressIndicator 进度条 =============
@Composable
fun ProgressDemo() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CircularProgressIndicator()
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        LinearProgressIndicator(
            progress = { 0.65f },
            modifier = Modifier.fillMaxWidth()
        )
        Text("determinate LinearProgressIndicator (65%)", style = MaterialTheme.typography.bodySmall)
    }
}

// ============= 2.6 Icon 图标 =============
@Composable
fun IconDemo() {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color.Red)
            Text("Favorite", style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("Notifications", style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Text("Search", style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Settings, contentDescription = null)
            Text("Settings", style = MaterialTheme.typography.labelSmall)
        }
    }
}

// ============= 3.1 LazyRow 横向列表 =============
@Composable
fun LazyRowDemo() {
    val colors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFF45B7D1),
        Color(0xFF96CEB4), Color(0xFFFFEAA7), Color(0xFFDDA0DD),
        Color(0xFF98D8C8), Color(0xFFF7DC6F),
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = color.toString().takeLast(6),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============= 3.2 联系人列表 =============
data class Contact(val name: String, val phone: String, val initial: String)

@Composable
fun ContactListDemo() {
    val contacts = listOf(
        Contact("张三", "138-0000-0001", "张"),
        Contact("李四", "138-0000-0002", "李"),
        Contact("王五", "138-0000-0003", "王"),
        Contact("赵六", "138-0000-0004", "赵"),
        Contact("孙七", "138-0000-0005", "孙"),
        Contact("周八", "138-0000-0006", "周"),
        Contact("吴九", "138-0000-0007", "吴"),
        Contact("郑十", "138-0000-0008", "郑"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        contacts.forEachIndexed { index, contact ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 头像圆圈
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column {
                    Text(contact.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(contact.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (index < contacts.lastIndex) {
                HorizontalDivider(modifier = Modifier.padding(start = 60.dp))
            }
        }
    }
}


// ==================== Preview ====================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComposePracticePreview() {
    MyFirstKotlinAppTheme {
        ComposePracticeApp()
    }
}
