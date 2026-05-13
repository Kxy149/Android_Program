# 实验2_2：Jetpack Compose 组件布局练习

## 一、实验目的

1. 掌握 Jetpack Compose 声明式 UI 编程模型
2. 熟悉常用布局组件：`Column`、`Row`、`Box`、`LazyColumn`、`LazyRow`
3. 掌握 Material3 组件库中常用交互控件的使用
4. 理解 Compose 中的状态管理（`remember` + `mutableStateOf`）
5. 掌握 `Modifier` 修饰符的链式调用与顺序规则

## 二、实验环境

| 项目 | 版本 |
|------|------|
| Android Studio | Ladybug / Hedgehog+ |
| Kotlin | 2.0.21 |
| AGP (Android Gradle Plugin) | 8.13.2 |
| Compose BOM | 2024.09.00 |
| compileSdk / targetSdk | 36 |
| minSdk | 35 |
| Material3 | BOM 托管版本 |

## 三、应用结构

应用采用**底部导航栏 + 三标签页**的设计，通过 `Scaffold` + `NavigationBar` 实现页面切换。

```
┌──────────────────────────┐
│  TopAppBar: "Compose 组件练习" │
├──────────────────────────┤
│                          │
│     Tab 内容区域          │
│                          │
├──────────────────────────┤
│   布局  │   组件  │   列表  │
│  (Home)  │  (Star)  │ (Person) │
└──────────────────────────┘
```

### Tab1 — 布局练习

展示 Compose 核心布局容器和 `Modifier` 的使用：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| Text 文本样式 | `Text` | `MaterialTheme.typography` 字体系列（displayLarge → labelSmall）、`maxLines`、`overflow` |
| Column 垂直布局 | `Column` | `verticalArrangement = Arrangement.spacedBy()`、`horizontalAlignment` |
| Row 水平布局 | `Row` | `Arrangement.SpaceEvenly` / `SpaceBetween`、`Alignment.CenterVertically` |
| Box 层叠布局 | `Box` | `matchParentSize()`、`contentAlignment`、渐变背景 `Brush.horizontalGradient` |
| Spacer & Weight | `Spacer` / `weight` | `weight(1f)` 分配剩余空间、比例布局 `1:2:1` |
| Modifier 链 | `Modifier` | `size → clip → background → border → padding` 顺序说明 |

### Tab2 — 组件练习

展示 Material3 常用交互组件及状态绑定：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| Button 按钮 | `Button` / `ElevatedButton` / `FilledTonalButton` / `OutlinedButton` / `TextButton` | 5 种 Material3 按钮风格 |
| TextField 输入 | `OutlinedTextField` | `value` + `onValueChange` 双向绑定、`label`、`placeholder` |
| Card 卡片 | `Card` | `CardDefaults.cardElevation()`、`cardColors()`、圆角 `RoundedCornerShape` |
| 选择控件 | `Checkbox` / `Switch` / `Slider` | 三种选择器各自的状态管理方式（`mutableStateOf` / `mutableFloatStateOf`） |
| ProgressIndicator | `CircularProgressIndicator` / `LinearProgressIndicator` | indeterminate 与 determinate（`progress` lambda）两种模式 |
| Icon 图标 | `Icon` + `Icons.Filled.*` | Material Icons 的使用与 `tint` 着色 |

### Tab3 — 列表练习

展示滚动列表组件的实际应用：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| LazyRow | `LazyRow` + `items()` | 横向懒加载滚动列表、`contentPadding`、`Arrangement.spacedBy` |
| LazyColumn | `LazyColumn` | 整个页面本身就是 LazyColumn 实现，仅渲染可见项 |
| 联系人列表 | `Column` + 遍历 | 数据类 `data class Contact`、`forEachIndexed`、`HorizontalDivider`、头像圆圈 |

## 四、核心知识点总结

### 4.1 声明式 UI 与状态管理

```kotlin
var text by remember { mutableStateOf("") }          // 字符串状态
var checked by remember { mutableStateOf(true) }      // 布尔状态
var sliderValue by remember { mutableFloatStateOf(0.5f) }  // 浮点状态
var selectedTab by remember { mutableIntStateOf(0) }  // 整数状态
```

`remember` 确保重组时状态不丢失，`mutableStateOf` 在值变化时触发重组。

### 4.2 Modifier 顺序规则

Modifier 的链式调用**顺序敏感**：

```kotlin
Modifier
    .size(100.dp)           // [1] 确定尺寸
    .clip(RoundedCornerShape(16.dp))  // [2] 裁剪形状
    .background(color)      // [3] 在裁剪区域内填充背景
    .border(3.dp, color, shape)       // [4] 绘制边框
    .padding(16.dp)         // [5] 内边距（影响内容，不改变自身大小）
```

若调换 `background` 与 `clip` 顺序，背景色将不会受圆角裁剪。

### 4.3 布局排列参数

| 参数 | 常用值 | 作用 |
|------|--------|------|
| `Arrangement` | `Start` / `Center` / `End` / `SpaceEvenly` / `SpaceBetween` / `spacedBy(dp)` | 主轴排列方式 |
| `Alignment` | `Start` / `CenterHorizontally` / `End` / `CenterVertically` | 交叉轴对齐方式 |
| `weight` | `1f` / `2f`... | 在 Row/Column 中按权重分配剩余空间 |

### 4.4 懒加载列表

- `LazyColumn` / `LazyRow` 只组合（渲染）屏幕上可见的 item
- 适合处理大量数据或内容较长的滚动页面
- 通过 `items()` 扩展函数遍历数据源
- `contentPadding` 控制列表首尾的内边距

## 五、项目结构

```
MyFirstKotlinApp/
├── app/
│   ├── build.gradle.kts          # 应用级构建配置（Compose + Material3 依赖）
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/myfirstkotlinapp/
│   │   │   ├── MainActivity.kt   # 全部 Compose UI 代码（单文件练习）
│   │   │   └── ui/theme/
│   │   │       ├── Color.kt      # Material3 调色板定义
│   │   │       ├── Theme.kt      # 主题配置（支持 Dynamic Color）
│   │   │       └── Type.kt       # 字体排版定义
│   │   └── res/                  # 资源文件（图标、字符串、主题等）
│   └── src/test/                 # 单元测试
├── build.gradle.kts              # 项目级构建配置
├── gradle/libs.versions.toml     # 版本目录（统一依赖管理）
├── settings.gradle.kts
└── gradle.properties
```

## 六、运行方式

1. 用 Android Studio 打开项目根目录 `MyFirstKotlinApp/`
2. 等待 Gradle 同步完成
3. 选择模拟器或真机，点击 **Run 'app'**
4. 应用启动后通过底部导航栏切换三个标签页，查看各组件的实际效果

## 七、学习收获

通过本次实验，实践了以下 Jetpack Compose 核心能力：

- 使用 `Scaffold` + `TopAppBar` + `NavigationBar` 构建标准 Material3 应用骨架
- 组合 `Row` / `Column` / `Box` 实现复杂的界面布局
- 运用 `LazyColumn` / `LazyRow` 构建高性能滚动列表
- 通过 `remember` + `mutableStateOf` 管理 UI 状态并响应交互
- 理解 `Modifier` 链式调用的顺序对最终呈现的影响
- 熟悉 Material3 主题系统（`MaterialTheme.colorScheme` / `typography`）及其在组件中的自动适配
