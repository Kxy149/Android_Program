# 实验2_2：Jetpack Compose 组件布局练习 & AI 图像识别界面

## 一、实验目的

### 目标一：Compose 组件布局练习

1. 掌握 Jetpack Compose 声明式 UI 编程模型
2. 熟悉常用布局组件：`Column`、`Row`、`Box`、`LazyColumn`、`LazyRow`
3. 掌握 Material3 组件库中常用交互控件的使用
4. 理解 Compose 中的状态管理（`remember` + `mutableStateOf`）
5. 掌握 `Modifier` 修饰符的链式调用与顺序规则

### 目标二：面向 AI 应用的 Compose 界面设计

6. 掌握使用 Compose 搭建 AI 图像识别应用的 UI 框架
7. 学会用 `Card` + `Column` 展示结构化数据（模型名称、结果、置信度、推理时间）
8. 学会用 `Row` / `Column` 组合排列多个操作按钮
9. 理解 UI 状态驱动的界面切换模式（有结果 / 无结果）

## 二、实验环境

| 项目 | 版本 |
|------|------|
| Android Studio | Ladybug / Hedgehog+ |
| Kotlin | 2.0.21 |
| AGP | 8.13.2 |
| Compose BOM | 2024.09.00 |
| compileSdk / targetSdk | 36 |
| minSdk | 35 |
| Material3 | BOM 托管版本 |
| material-icons-extended | BOM 托管版本 |

## 三、应用总览

应用采用**底部导航栏双页切换**架构，主页为 Compose 组件练习，副页为 AI 图像识别界面。

```
┌──────────────────────────────────┐
│  TopAppBar（标题随 Tab 切换）      │
├──────────────────────────────────┤
│                                  │
│         当前 Tab 内容区            │
│                                  │
├──────────────────────────────────┤
│   Compose练习  │   AI识别    │
└──────────────────────────────────┘
```

---

## 四、Tab1 — Compose 组件布局练习

此 Tab 内通过**三个子标签页（布局 / 组件 / 列表）**组织练习内容。

### 4.1 子Tab：布局

展示 Compose 核心布局容器和 `Modifier` 的使用：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| Text 文本样式 | `Text` | `typography` 字体系列（displayLarge → labelSmall）、`maxLines`、`TextOverflow.Ellipsis` |
| Column 垂直布局 | `Column` | `verticalArrangement = Arrangement.spacedBy()`、`horizontalAlignment` 对齐 |
| Row 水平布局 | `Row` | `Arrangement.SpaceEvenly` / `SpaceBetween`、`Alignment.CenterVertically` |
| Box 层叠布局 | `Box` | `matchParentSize()` 匹配父容器、`contentAlignment`、`Brush.horizontalGradient` 渐变 |
| Spacer & Weight | `Spacer` / `weight` | `weight(1f)` 撑开剩余空间、`1:2:1` 比例分配 |
| Modifier 链 | `Modifier` | `size → clip → background → border → padding` 顺序说明 |

### 4.2 子Tab：组件

展示 Material3 常用交互组件及状态绑定：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| Button 按钮 | `Button` / `ElevatedButton` / `FilledTonalButton` / `OutlinedButton` / `TextButton` | 5 种 Material3 按钮风格 |
| TextField 输入 | `OutlinedTextField` | `value` + `onValueChange` 双向绑定、`label`、`placeholder` |
| Card 卡片 | `Card` | `CardDefaults.cardElevation()`、`cardColors()`、圆角 |
| 选择控件 | `Checkbox` / `Switch` / `Slider` | `mutableStateOf` / `mutableFloatStateOf` 三种状态类型 |
| ProgressIndicator | `CircularProgressIndicator` / `LinearProgressIndicator` | indeterminate 与 determinate（`progress` lambda） |
| Icon 图标 | `Icon` + `Icons.Filled.*` | Material Icons 与 `tint` 着色 |

### 4.3 子Tab：列表

展示滚动列表组件的实际应用：

| 章节 | 组件 | 关键知识点 |
|------|------|-----------|
| LazyRow | `LazyRow` + `items()` | 横向懒加载列表、`contentPadding`、`Arrangement.spacedBy` |
| LazyColumn 说明 | 文档说明 | 整个页面本身就是 LazyColumn，仅渲染可见 item |
| 联系人列表 | `Column` + `forEachIndexed` | `data class` 数据模型、`HorizontalDivider`、头像圆圈 |

---

## 五、Tab2 — AI 图像识别界面

面向 AI 图像识别场景，使用 `Column` 组织四区域布局。

### 5.1 界面结构

```
┌──────────────────────────────────┐
│  TopAppBar: "AI 图像识别"    [⋮] │  ← 顶部栏
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐  │
│  │        CameraX 相机预览    │  │  ← 预览区（Box 占位）
│  │       后续替换为实时画面     │  │
│  └────────────────────────────┘  │
│                                  │
│  ┌ 结果区 ────────────────────┐  │
│  │ 模型名称    MobileNetV3     │  │  ← 结果区（Card + Column）
│  │ ────────────────────────── │  │
│  │ 识别结果    金毛犬           │  │     4 项结构化信息
│  │ ────────────────────────── │  │     置信度颜色编码
│  │ 置信度 96.2%    推理 127ms │  │
│  └────────────────────────────┘  │
│                                  │
│  [ 拍照识别]    [ 相册导入]    │  ← 按钮区（Row+Row）
│  [ 切换模型]    [ 清空结果]    │
│       当前模型：MobileNetV3      │
└──────────────────────────────────┘
```

### 5.2 各区域实现说明

| 区域 | 组件 | 实现要点 |
|------|------|---------|
| **顶部栏** | `TopAppBar` + `DropdownMenu` | 标题 + 右侧溢出菜单（设置 / 关于） |
| **预览区** | `Box`(280dp 高) | `surfaceVariant` 背景 + 相机图标居中占位，后续替换为 CameraX |
| **结果区** | `Card` + `Column` | 模型名称 / 识别结果 / 置信度 / 推理时间 四项信息；置信度 ≥90% 绿色、≥70% 橙色、<70% 红色；无结果时显示 "—"，Card 背景变灰 |
| **按钮区** | `Row`(weight=1f) × 2 行 | 拍照识别(Button) + 相册导入(OutlinedButton)；切换模型(tertiary 配色，显示当前模型名) + 清空结果(无结果时禁用) |

### 5.3 交互模拟

| 操作 | 行为 |
|------|------|
| 拍照识别 | 模拟识别为"金毛犬 (Golden Retriever)"，置信度 96.2%，推理 127ms |
| 相册导入 | 模拟识别为"波斯猫 (Persian Cat)"，置信度 88.3%，推理 95ms |
| 切换模型 | 在 MobileNetV3 → ResNet50 → EfficientNetV2 → ViT-B/16 中循环切换，自动清空结果 |
| 清空结果 | 重置所有数据，Card 恢复灰色占位状态 |

---

## 六、核心知识点总结

### 6.1 声明式 UI 与状态管理

```kotlin
var text by remember { mutableStateOf("") }            // String 状态
var checked by remember { mutableStateOf(true) }        // Boolean 状态
var sliderValue by remember { mutableFloatStateOf(0.5f) } // Float 状态
var selectedTab by remember { mutableIntStateOf(0) }    // Int 状态
var hasResult by remember { mutableStateOf(false) }     // 控制结果区显示/隐藏
```

`remember` 确保重组时状态不丢失；`mutableStateOf` 在值变化时自动触发 UI 重组。

### 6.2 Modifier 顺序规则

```kotlin
Modifier
    .size(100.dp)                              // [1] 确定尺寸
    .clip(RoundedCornerShape(16.dp))            // [2] 裁剪形状
    .background(MaterialTheme.colorScheme.primaryContainer) // [3] 填充背景
    .border(3.dp, color, RoundedCornerShape(16.dp))        // [4] 绘制边框
    .padding(16.dp)                             // [5] 内边距
```

若调换 `background` 与 `clip` 顺序，背景色将不受圆角裁剪。

### 6.3 布局排列参数

| 参数 | 常用值 | 作用 |
|------|--------|------|
| `Arrangement` | `Start` / `Center` / `End` / `SpaceEvenly` / `SpaceBetween` / `spacedBy(dp)` | 主轴排列方式 |
| `Alignment` | `Start` / `CenterHorizontally` / `End` / `CenterVertically` | 交叉轴对齐方式 |
| `weight` | `1f` / `2f` | 在 Row/Column 中按权重分配剩余空间 |

### 6.4 懒加载列表

- `LazyColumn` / `LazyRow` 只组合屏幕上可见的 item，适合大量数据
- 通过 `items()` 扩展函数遍历数据源
- `contentPadding` 控制列表首尾内边距

### 6.5 状态驱动的 UI 模式

AI 识别界面中使用 `hasResult` 布尔状态控制：

- 结果 Card 的背景色切换（`secondaryContainer` / `surfaceVariant`）
- 各项数据显示或显示 "—"
- "清空结果" 按钮的启用/禁用状态

---

## 七、项目结构

```
MyFirstKotlinApp/
├── app/
│   ├── build.gradle.kts              # Compose + Material3 + Icons Extended 依赖
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/example/myfirstkotlinapp/
│   │   │   ├── MainActivity.kt       # 全部 UI 代码（两个 Tab 在同一文件）
│   │   │   └── ui/theme/
│   │   │       ├── Color.kt          # Material3 调色板
│   │   │       ├── Theme.kt          # 主题（支持 Dynamic Color）
│   │   │       └── Type.kt           # 字体排版
│   │   └── res/                      # 资源（图标、字符串、主题）
│   └── src/test/                     # 单元测试
├── build.gradle.kts
├── gradle/libs.versions.toml         # 版本目录
├── settings.gradle.kts
└── gradle.properties
```

## 八、运行方式

1. 用 Android Studio 打开项目根目录 `MyFirstKotlinApp/`
2. 等待 Gradle 同步完成
3. 选择模拟器或真机，点击 **Run 'app'**
4. 通过底部导航栏在 **Compose练习** 与 **AI识别** 之间切换

## 九、学习收获

通过本次实验，实践了以下 Jetpack Compose 核心能力：

- 使用 `Scaffold` + `TopAppBar` + `NavigationBar` 构建 Material3 应用骨架
- 组合 `Row` / `Column` / `Box` / `Card` 实现不同复杂度的界面布局
- 运用 `LazyColumn` / `LazyRow` 构建高性能滚动列表
- 通过 `remember` + `mutableStateOf` 系列 API 管理 UI 状态
- 理解 `Modifier` 链式调用的顺序对最终呈现的影响
- 掌握 Material3 主题系统（`colorScheme` / `typography`）的自动适配
- 实战面向 AI 应用的界面设计：预览区占位、结构化结果展示、多按钮操作区编排
