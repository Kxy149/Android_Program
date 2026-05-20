import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_csv(r"D:\download\fortune500.csv")

# ============================================================
# 1. 数据显示
# ============================================================
print("========== 数据概览 ==========")
print(f"数据集形状: {df.shape}")
print(f"\n前5行数据:")
print(df.head())
print(f"\n后3行数据:")
print(df.tail(3))

# ============================================================
# 2. 检查数据列属性
# ============================================================
print("\n========== 列属性检查 ==========")
print(f"\n列名列表:\n{df.columns.tolist()}")
print(f"\n各列数据类型:\n{df.dtypes}")
print(f"\n缺失值统计:\n{df.isnull().sum()}")
print(f"\n基本统计信息:")
print(df.describe(include="all"))

# ============================================================
# 3. 数据过滤
# ============================================================
print("\n========== 数据过滤 ==========")
# 过滤出 Revenue > 100000 的行（根据实际列名调整字段名）
numeric_cols = df.select_dtypes(include="number").columns
if len(numeric_cols) > 0:
    first_num_col = numeric_cols[0]
    median_val = df[first_num_col].median()
    filtered = df[df[first_num_col] > median_val]
    print(f"按 '{first_num_col}' > 中位数({median_val}) 过滤，得到 {len(filtered)} 行")
    print(filtered.head())

# ============================================================
# 4. 属性查询
# ============================================================
print("\n========== 属性查询 ==========")
# 使用 .loc 查询特定行/列
print("使用 loc 查询前3行、前3列:")
print(df.iloc[:3, :3])

# 使用 query 进行条件查询
if len(numeric_cols) >= 2:
    col_a, col_b = numeric_cols[0], numeric_cols[1]
    result = df.query(f"`{col_a}` > `{col_a}`.median()")
    print(f"\nquery('{col_a} > 中位数') 结果数量: {len(result)}")

# 按某列分组聚合
if len(df.select_dtypes(include="object").columns) > 0:
    cat_col = df.select_dtypes(include="object").columns[0]
    print(f"\n按 '{cat_col}' 分组计数:")
    print(df[cat_col].value_counts().head(10))

# ============================================================
# 5. 利润按年份分布直方图
# ============================================================
print("\n========== 利润年份分布 ==========")

profit_col = None
for col in df.columns:
    if "profit" in col.lower() or "利润" in col:
        profit_col = col
        break

year_col = None
for col in df.columns:
    if col.lower() in ("year", "年份"):
        year_col = col
        break

if profit_col and year_col:
    print(f"利润列: '{profit_col}', 年份列: '{year_col}'")
    years = sorted(df[year_col].dropna().unique())
    years = [int(y) for y in years]
    print(f"年份范围: {min(years)} - {max(years)}")

    # 主图: 按年份分面的直方图
    g = sns.displot(
        data=df,
        x=profit_col,
        col=year_col,
        col_wrap=4,
        bins=30,
        kde=True,
        height=3,
        aspect=1.2
    )
    g.set_titles("Year {col_name}")
    g.figure.suptitle(f"Profit Distribution by Year", y=1.02, fontsize=14)
    plt.show()

    # 补充图: 按年份的箱线图
    plt.figure(figsize=(12, 5))
    sns.boxplot(data=df, x=year_col, y=profit_col, palette="Set2")
    plt.title(f"Profit Boxplot by Year")
    plt.xlabel("Year")
    plt.ylabel(profit_col)
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.show()
else:
    print(f"未找到所需列 (profit: {profit_col is not None}, year: {year_col is not None})")

# ============================================================
# 6. 利润和收入同图展示
# ============================================================
print("\n========== 利润与收入关联分析 ==========")

revenue_col = None
for col in df.columns:
    if "revenue" in col.lower() or "收入" in col:
        revenue_col = col
        break

if profit_col and revenue_col:
    print(f"利润列: '{profit_col}', 收入列: '{revenue_col}'")

    fig, axes = plt.subplots(1, 3, figsize=(18, 5))

    # 图1: 散点图 — 收入 vs 利润
    ax1 = axes[0]
    scatter = ax1.scatter(df[revenue_col], df[profit_col],
                          c=df[year_col] if year_col else None,
                          cmap="viridis", alpha=0.6, edgecolors="none")
    ax1.set_xlabel(revenue_col)
    ax1.set_ylabel(profit_col)
    ax1.set_title("Revenue vs Profit (Scatter)")
    if year_col:
        cbar = plt.colorbar(scatter, ax=ax1)
        cbar.set_label(year_col)

    # 图2: 按年份汇总的利润和收入趋势（双Y轴）
    ax2 = axes[1]
    if year_col:
        yearly = df.groupby(year_col).agg({revenue_col: "sum", profit_col: "sum"}).reset_index()

        line1 = ax2.bar(yearly[year_col], yearly[revenue_col],
                        color="steelblue", alpha=0.7, label=f"Total {revenue_col}")
        ax2.set_xlabel("Year")
        ax2.set_ylabel(revenue_col, color="steelblue")
        ax2.tick_params(axis="y", labelcolor="steelblue")

        ax2b = ax2.twinx()
        line2 = ax2b.plot(yearly[year_col], yearly[profit_col],
                          color="coral", marker="o", linewidth=2, label=f"Total {profit_col}")
        ax2b.set_ylabel(profit_col, color="coral")
        ax2b.tick_params(axis="y", labelcolor="coral")
        ax2.set_title("Yearly Total: Revenue & Profit")

        lines1, labels1 = ax2.get_legend_handles_labels()
        lines2, labels2 = ax2b.get_legend_handles_labels()
        ax2.legend(lines1 + lines2, labels1 + labels2, loc="upper left")
    else:
        ax2.text(0.5, 0.5, "No year column found", ha="center", va="center")

    # 图3: 利润率分布直方图（利润/收入）
    ax3 = axes[2]
    margin = df[profit_col] / df[revenue_col]
    margin = margin[np.isfinite(margin)]
    ax3.hist(margin, bins=40, color="mediumseagreen", edgecolor="white", alpha=0.8)
    ax3.axvline(margin.median(), color="red", linestyle="--", linewidth=1.5, label=f"Median: {margin.median():.2%}")
    ax3.set_xlabel("Profit Margin")
    ax3.set_ylabel("Count")
    ax3.set_title("Profit Margin Distribution")
    ax3.legend()

    plt.tight_layout()
    plt.show()
else:
    missing = []
    if not profit_col:
        missing.append("profit")
    if not revenue_col:
        missing.append("revenue")
    print(f"未找到列: {missing}")

print("\n========== 完成 ==========")