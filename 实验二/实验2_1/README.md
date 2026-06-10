README.md
# Kotlin 算法与语法练习笔记
本仓库用于存放 Kotlin 算法练习、语法实操代码，涵盖集合操作、函数式编程、属性委托等核心知识点，适合巩固 Kotlin 基础语法与实操能力。
## 📋 目录
- 一、集合操作（Shop/订单/产品场景）
- 二、函数式转换
- 三、属性与委托
- 四、带接收者的函数字面值
- 五、核心知识点总结
- 六、上传与操作说明
## 一、集合操作（Shop/订单/产品场景）
基于 Shop、Customer、Order、Product 场景，实现常见集合操作，包含普通集合与惰性序列两种实现。
### 1. 获取客户订购的所有产品（去重）
```kotlin
fun Customer.getOrderedProducts(): Set<Product> =
    orders.flatMap { it.products }.toSet()
```
### 2. 获取所有客户都订购过的产品（交集）
```kotlin
fun Shop.getProductsOrderedByAll(): Set<Product> =
    customers.map { it.getOrderedProducts() }
        .reduce { acc, set -> acc intersect set }
```
### 3. 找到客户已交付订单中最贵的产品
```kotlin
fun findMostExpensiveProductBy(customer: Customer): Product? {
    return customer.orders
        .filter { it.isDelivered }
        .flatMap { it.products }
        .maxByOrNull { it.price }
}
```
### 4. 统计产品被订购的总次数
```kotlin
fun Shop.getNumberOfTimesProductWasOrdered(product: Product): Int {
    return customers
        .flatMap { it.getOrderedProducts() }
        .count { it == product }
}
```
### 5. 惰性序列 Sequence 优化版本
```kotlin
fun Customer.getOrderedProducts(): Sequence<Product> =
 orders.asSequence().flatMap { it.products.asSequence() }
```
## 二、函数式转换
将命令式代码改写为 Kotlin 函数式风格，简洁高效，避免手动循环与可变变量。
```kotlin
fun doSomethingWithCollection(collection: Collection<String>): Collection<String>? {
    return collection.groupBy { it.length }
        .values
        .maxByOrNull { it.size }
}
```
## 三、属性与委托
掌握 Kotlin 属性的自定义 setter、惰性初始化、自定义委托等核心特性。
### 1. 自定义 setter（赋值时计数）
```kotlin
class PropertyExample() {
    var counter = 0
    var propertyWithCounter: Int? = null
        set(newValue) {
            field = newValue
 counter++
        }
}
```
### 2. 手动实现惰性属性
```kotlin
class LazyProperty(val initializer: () -> Int) {
    private var value: Int? = null
    val lazy: Int
        get() {
            if (value == null) {
                value = initializer()
            }
            return value!!
        }
}
```
### 3. 使用 by lazy 委托（标准库实现）
```kotlin
class LazyProperty(val initializer: () -> Int) {
    val lazyValue: Int by lazy(initializer)
}
```
### 4. 自定义属性委托（日期存储）
```kotlin
class D {
 var date: MyDate by EffectiveDate()
}

class EffectiveDate<R> : ReadWriteProperty<R, MyDate> {
    var timeInMillis: Long? = null
    override fun getValue(thisRef: R, property: KProperty<*>): MyDate {
        return timeInMillis?.toDate() ?: error("未初始化")
    }
    override fun setValue(thisRef: R, property: KProperty<*>, value: MyDate) {
        timeInMillis = value.toMillis()
    }
}
```
## 四、带接收者的函数字面值
类似扩展函数，支持「变量.函数()」调用，常用于 DSL 构建与简洁语法实现。
```kotlin
fun task(): List<Boolean> {
    val isEven: Int.() -> Boolean = { this % 2 == 0 }
    val isOdd: Int.() -> Boolean = { this % 2 != 0 }
    return listOf(42.isOdd(), 239.isOdd(), 294823098.isEven())
}
```
## 五、核心知识点总结
| 知识点 | 核心说明 |
| ---- | ---- |
| flatMap | 展开嵌套集合，处理多层数据（如订单-产品） |
| toSet() | 自动去重，获取不重复元素集合 |
| intersect | 集合交集操作，筛选共同元素 |
| Sequence | 惰性求值，大数据量下减少临时对象，提升性能 |
| 自定义 setter | 通过 field 关键字操作幕后字段，赋值时附加逻辑（如计数） |
| by lazy | 标准库提供，线程安全的惰性初始化，仅首次访问执行 |
| 属性委托 | 封装 get/set 逻辑，解耦属性与存储/转换逻辑 |
| 带接收者函数 | 接收者作为 this，支持扩展式调用，简化语法 |
