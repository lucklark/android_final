# 记忆助手

## 成员信息

李赛尉

李秀祥

陆俞因 17343081

## 简介

（介绍APP的背景，动机等）

## 开发环境

- **操作系统**：Windows
- **IDE**：Android Studio

## 成员分工

（介绍成员分工情况）

- **罗锐堃**：负责UI设计，主页面编写，Minmax算法编写。

## 重点&难点

（介绍APP实现中的重点、难点等内容）

### 1. AI对战

由于我们团队没有实现网络对战，因此需要机器人与玩家进行对战。为此，我们使用MinMax算法与玩家对战。

#### MinMax算法

（如有必要，可以展开）

## 功能信息

（确定APP的所需要实现的功能，此内容将作为检查APP是否功能完善的重要依据）

1. 实现与AI战斗的3x3井字棋游戏
2. 实现AI难度可调

## 实现方法

（下面内容属于第三次考核，依个人写作习惯和项目情况，自己组织语言和结构。需要包括**需求分析（用例图），设计（类图，流程图）等**，完整展示APP的实现方法。此内容将作为提问环节的重要依据。）

应用执行的主要流程可用下图表示：

![微信截图_20200215111205](C:/Users/DELL/Desktop/android/project/MAD2020_FHW/homework/19214821_罗锐堃/report/微信截图_20200215111205.png)

项目包含2个主要文件：

### MainActivity.java

主页面代码。同时也 implements OnTouchListener，作为棋盘的监听器。主要功能包括：

- **OnCreate() && init()**：绘制出初始UI，为棋盘设置监听器。（难度按钮的触发函数已在xml设定）。
- **setLevel()**：难度按钮的触发函数，设置level值。
- **OnTouch()**：棋盘的触发函数，当玩家按在棋盘某处时，传递坐标给棋盘的human_play函数落子，并根据human_play的返回判断是否结束：若已结束，则在文本框中显示结果；若未结束，则传递level给棋盘的ai_play函数，由AI落子。

### Board.java

棋盘代码，继承至View。其维护一个棋盘数组，绘制时根据棋盘数组的情况绘制棋盘。主要功能包括：

- **onDraw()**：绘制棋盘，包括根据当前棋盘数组状况绘制相应的O和X。Activity的初始化和自己继承的invalidate()都会调用该函数。
- **human_play(float x, float y)**：根据(x, y)，在棋盘数组上落子。调用checkWinner()判断棋局是否结束。
- **ai_play(int level)**：根据level和当前棋盘数组，调用算法确定落子位置，并在棋盘数组上落子。调用checkWinner()判断棋局是否结束。
- **minmax(int depth, boolean isMaximizing)**：递归算法。根据当前棋盘数组，确定最优落子方案。
- **checkWinner()**：根据棋盘数组，判断棋局是否已结束。



### MainActivity.java

主页面代码，对应主布局 `tab_layout.xml`。通过 `FrameLayout`+`Fragment` 实现 Tab 选项卡，控制笔记页、类别页、统计页间的切换，并实现各个子页间的间接通信，同时控制 `ActionBar` 的按键事件。主要功能包括：

- `onCreate(Bundle)`: 初始化数据库、主视图、Tab 按键事件，动态获取权限；
- `onClick(View)` && `setSelected(int)`: 实现 `View.OnClickListener` 接口的 `onClick(View)` 方法，实现了点击 Tab 按键，切换到相应的页面，具体来说，是将对应页面的 `fragment` 填充到主布局 `tab_layout.xml` 的 `<FrameLayout>` 中；
- `onOptionsItemSelected(MenuItem)`: 控制 `ActionBar` 的按键事件，
  1. 点击 `+` 按键，若当前处于笔记页，则发送 `Intent` 启动 `NewNotes` 页面新增笔记；若当前处于类别页，则调用 `newClassDialog()` 方法新增类别。
  2. 点击 `about` 按键，弹出应用说明。

- `onCreateContextMenu(...)` && `onCreateOptionsMenu(Menu)`: 创建笔记页和类别页用到的上下文菜单；
- `deleteNotesInform(String)`: 实现了笔记页的回调函数，接收笔记页删除笔记的信息，并更新类别页的显示；
- `setSelectedClass(String)`: 实现了类别页的回调函数，取得用户点击选中的类别，并重设笔记页的显示，即在笔记页显示选中类的所有笔记。



### ClassFragment.java

类别页面代码，对应类别页布局 `class_layout.xml` 以及类别项布局 `class_item.xml`。主要功能包括：

- `onCreateView(...)`: 初始化类别页的 UI，并设置 `<ListView>` 组件的 `Adapter`；
- `interface ClasstoActivityListener`: `ClassFragment` 向 `MainActivity` 通信的回调接口；
- `onContextItemSelected(MenuItem)`: 设置了类别页中上下文菜单删除键的点击事件，实现删除类别(同时删除该类别的所有笔记)。



### NotesFragment.java

笔记主页面代码，对应笔记页布局 `activity_notes.xml` 以及笔记项布局 `list_items.xml`。主要功能包括：

- `newInstance(String)`: `NotesFragment` 的实例化方法，参数为当前选中的类别名。通过 `Bundle` 实现 `NotesFragment` 与 `MainActivity` 间的通信，从 `MainActivity` 取得当前选中的类别名；
- `onCreateView(...)`: 初始化类别页的 UI，并设置 `<ListView>` 组件的 `Adapter`，同时设置笔记项的点击事件，点击笔记项启动详情页 `AnswerCard` 显示选中笔记的内容；
- `interface toActivityListener`: `NotesFragment` 向 `MainActivity` 通信的回调接口；
- `onContextItemSelected(MenuItem)`: 设置了笔记页中上下文菜单删除键的点击事件，实现删除笔记，并调用回调函数通知 `MainActivity` 同时更新类别页的显示。



### Persistence

持久层，定义了数据库的数据结构并封装了数据库的接口。

#### Data Structure

类别的数据结构，对应 `ClassItems.java`。

| attribute            | data type | description        |
| -------------------- | --------- | ------------------ |
| _id(**primary key**) | int       | 类别的唯一 id      |
| class_name           | String    | 类别名             |
| notes_num            | int       | 类别包含的笔记总数 |
| class_order          | int       | 类别的排序         |
| class_review_time    | String    | 复习该类别的总时长 |



笔记的数据结构，对应 `NoteItems.java`。

| attribute            | data type | description              |
| -------------------- | --------- | ------------------------ |
| _id(**primary key**) | int       | 笔记的唯一 id            |
| title                | String    | 笔记标题                 |
| last_reviewed        | String    | 最近一次复习该笔记的时间 |
| total_reviews        | int       | 复习该笔记的总次数       |
| content              | String    | 笔记内容                 |
| note_class           | String    | 笔记所属类别名           |
| total_review_time    | String    | 复习该笔记的总时长       |



#### SQLiteHelper.java

创建和更新 SQLite 数据库，创建类别表和笔记表。



#### ClassDataSource.java

实现操作类别表的接口。主要功能包括：

- `insertClass(String)`: 增加类别；
- `deleteOne(String)`: 删除类别；
- `incrementNotesNum(String)`: 增加笔记时，该类别的笔记总数(`notes_num`) 加一；
- `decrementNotesNum(String)`: 删除笔记时，该类别的笔记总数(`notes_num`) 减一；
- `updateClassReviewTime(long, String)`: 复习笔记时，更新该类别的复习总时长；
- `getAllClass()`: 取得所有类别的数据。



#### NotesDataSource.java

实现操作笔记表的接口。主要功能包括：

- `insertNotes(...)` : 增加笔记；
- `deleteOne(String, String)`: 删除一条笔记；
- `deleteByClass(String)`: 删除某类别的所有笔记；
- `incrementTotalReviews(String)`: 复习某笔记时，该笔记的复习次数(`total_reviews`)加一；
- `modifyLastSeen(String)`: 复习某笔记时，更新该笔记的最近复习时间(`last_reviewed`)；
- `modifyTotalReviewTime(String)`: 结束复习某笔记时，更新该笔记的总复习时长(`total_review_time`)；
- `getAllNotes()`: 取得所有笔记数据；
- `getNotesOfClass(String)`: 取得某类别的所有笔记数据；
- `getAllNotesForNotification()`: 取得所有需要复习的笔记。