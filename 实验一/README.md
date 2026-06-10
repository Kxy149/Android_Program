Windows 系统开发软件安装指南.md
# Windows 系统开发软件安装指南
## 说明
本文档用于记录 Windows 系统下 VS Code、Jupyter Notebook、Android Studio 三款开发软件的完整安装流程，步骤清晰可操作，适配作业提交要求，确保每一步均能正常执行，避免安装报错，同时修正原文档中失效的下载链接，保障安装顺利进行。
## 一、前置准备
安装前请确保Windows系统已更新（打开设置→Windows更新→检查更新），关闭电脑中的杀毒软件（避免拦截安装程序），同时准备好稳定的网络环境，避免因网络问题导致安装包下载失败或安装中断。
## 二、VS Code 安装流程
### 1. 下载VS Code安装包
访问VS Code官方下载页面，下载Windows版本安装包，避免使用原文档中可能导致报错的密钥导入方式，采用更简单的图形化安装：
官方下载地址：[https://code.visualstudio.com/Download](https://code.visualstudio.com/Download)
选择“Windows”→ 点击“User Installer (64-bit)”（适合当前用户，无需管理员权限）或“System Installer (64-bit)”（适合所有用户，需管理员权限），下载安装包。
### 2. 安装VS Code
1. 双击下载完成的安装包（如VSCodeUserSetup-x64-xxx.exe），启动安装向导；
2. 勾选“我接受协议”，点击“下一步”；
3. 选择安装路径（建议默认路径，或自定义路径，避免中文路径），点击“下一步”；
4. 勾选需要的附加任务（推荐勾选“创建桌面快捷方式”“将Code注册为支持的文件类型的编辑器”“添加到PATH”），点击“下一步”；
5. 点击“安装”，等待安装完成（约1-3分钟）；
6. 安装完成后，勾选“运行Visual Studio Code”，点击“完成”。
### 3. 验证安装
启动VS Code后，若能正常进入软件界面，且无报错提示，即安装完成。可通过桌面快捷方式、开始菜单或终端输入code（需勾选“添加到PATH”）启动软件。
注意：原文档中通过命令导入VS Code官方密钥的方式，在Windows系统中无需操作，直接通过官方安装包安装即可，避免出现“当前不支持该文件类型，请尝试其他文件”的报错。
## 三、Jupyter Notebook 安装流程
Jupyter Notebook 依赖Python环境，需先安装Python，再通过pip安装Jupyter Notebook，步骤如下：
### 1. 安装Python（含pip工具）
1. 访问Python官方下载页面：[https://www.python.org/downloads/windows/](https://www.python.org/downloads/windows/)
2. 选择适合Windows系统的Python版本（推荐Python 3.8，兼容性好，适配Jupyter Notebook），点击“Download Python 3.8.10”（或对应版本）；
3. 双击下载完成的安装包，勾选“Add Python 3.8 to PATH”（关键步骤，避免后续配置环境变量），点击“Install Now”；
4. 等待安装完成，点击“Close”。
### 2. 升级pip并安装Jupyter Notebook
打开Windows终端（Win+R，输入cmd，回车），执行以下命令，升级pip并安装Jupyter Notebook：
pip install --upgrade pip
pip install jupyter
### 3. 配置环境变量（可选）
若执行 jupyter notebook 提示“不是内部或外部命令”，说明Python未添加到PATH，解决方案：
1. 右键“此电脑”→“属性”→“高级系统设置”→“环境变量”；
2. 在“系统变量”中找到“Path”，点击“编辑”；
3. 点击“新建”，添加Python安装路径（默认路径为C:\Python38和C:\Python38\Scripts）；
4. 点击“确定”保存，关闭终端后重新打开即可。
### 4. 验证安装
终端输入以下命令启动Jupyter Notebook：
jupyter notebook
启动成功后，会自动打开浏览器，进入Jupyter Notebook界面，即可创建和运行Python代码。
## 四、Android Studio 安装流程
### 1. 安装依赖组件
Windows系统安装Android Studio需先确保安装了Java环境（Android Studio自带JDK，可无需单独安装），同时开启电脑的“虚拟化技术”（BIOS中开启，避免模拟器无法运行）。
### 2. 下载Android Studio安装包
访问Android Studio官方下载页面，下载Windows版本安装包，替换原文档中失效的下载链接（原链接会提示“系统内部异常，请稍后重试”）：
官方下载地址：[https://developer.android.com/studio](https://developer.android.com/studio)
下拉页面找到“Android Studio downloads”，选择“Windows (64-bit)”对应的安装包：android-studio-2025.1.1.13-windows.exe（推荐最新稳定版），点击下载，需同意协议后开始下载。
### 3. 安装Android Studio
1. 双击下载完成的安装包（如android-studio-2025.1.1.13-windows.exe），启动安装向导，点击“Next”；
2. 勾选“Android Studio”和“Android Virtual Device”（模拟器，可选，建议勾选），点击“Next”；
3. 选择安装路径（建议默认路径，或自定义路径，避免中文路径），点击“Next”；
4. 点击“Install”，等待安装完成（约5-10分钟，取决于网络速度）；
5. 安装完成后，勾选“Start Android Studio”，点击“Finish”。
### 4. 首次启动配置
1. 首次启动Android Studio，会提示“Do you want to import previous Android Studio settings?”，选择“Do not import settings”，点击“OK”；
2. 进入“Android Studio Setup Wizard”，点击“Next”；
3. 选择“Standard”（标准配置，适合新手），点击“Next”；
4. 选择界面主题（推荐Light或Darcula），点击“Next”；
5. 确认SDK组件下载路径，点击“Next”；
6. 点击“Finish”，开始下载SDK组件（约10-20分钟，需耐心等待）；
7. 组件下载完成后，点击“Finish”，即可进入Android Studio主界面。
### 5. 验证安装
进入Android Studio主界面后，创建一个新的“Empty Views Activity”项目，若能正常创建项目、无报错，且模拟器能正常启动，即安装完成。
注意：若下载Android Studio安装包时出现异常，可更换浏览器重新下载，或选择官方提供的zip格式安装包（无需.exe安装，解压后即可使用）。
