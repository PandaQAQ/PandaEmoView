### 该库具有以下特点：
- 支持 emoji 表情图片
- 支持 gif 动态表情输入显示
- 支持单张贴图表情（与微信收藏表情一致）
- 支持题图表情库的添加删除
### 效果图：
![效果图][1]
# 快速使用

## 引入库
``` groovy
compile 'com.pandaq:PandaEmoView:1.0.0'
```

## 表情资源及配置文件
- 默认的 emoji 和 gif 表情以及他们的配置文件是放在开发包 assets 目录下的，若表情比较多比较大也可自行修改源码在 APP 启动时从服务器下载。

![assets 目录图片][2]
- emoji 表情配置文件

![config 截图][3] 
- 非自定义 sticker 配置文件(自定义 sticker 是没有配置文件的)

![sticker config 截图][4]
## 具体使用规则

**与表情输入控件相关的 EditText 必须使用 PandaEditText**
PandaEditText 只是重写了 onKeyPreIme() 获取按返回键的通知，继承自 EditText 的控件可继承 PandaEditText 自定义

1.应用 Application 中进行全局参数配置
``` java
    private void configPandaEmoView() {
        new PandaEmoManager.Builder()
                .with(getApplicationContext()) // 传递 Context
                .configFileName("emoji.xml")// 配置文件名称
                .emoticonDir("face") // asset 下存放表情的目录路径（asset——> configFileName 之间的路径,结尾不带斜杠）
                .sourceDir("images") // 存放 emoji 表情资源文件夹路径（emoticonDir 图片资源之间的路径,结尾不带斜杠）
                .showAddTab(true)//tab栏是否显示添加按钮
                .showStickers(true)//tab栏是否显示贴图切换按键
                .showSetTab(true)//tab栏是否显示设置按钮
                .defaultBounds(30)//emoji 表情显示出来的宽高
                .cacheSize(1024)//加载资源到内存时 LruCache 缓存大小
                .defaultTabIcon(R.drawable.ic_default)//emoji表情Tab栏图标
                .emojiColumn(7)//单页显示表情的列数
                .emojiRow(3)//单页显示表情的行数
                .stickerRow(2)//单页显示贴图表情的行数
                .stickerColumn(4)//单页显示贴图表情的列数
                .maxCustomStickers(30)//允许添加的收藏表情数
                .imageLoader(new IImageLoader() {
                    @Override
                    public void displayImage(String path, ImageView imageView) { // 加载贴图表情的图片加载接口
                        Picasso.with(getApplicationContext())
                                .load(path)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                    }
                })
                .build(); //构建 PandaEmoManager 单利
    }
```

2.使用此控件的 Activity 在 manifest 文件中配置
 ```
 // 这句是一定要加上的。
 android:windowSoftInputMode="adjustResize"
 ```
3.使用此控件的界面 xml 文件规则
布局规则如下图，lockView 即是我们正常显示内容的 View 它与表情输入控件 PandaEmoView 属于同一层级，父布局必须为纵向线性布局，且设置 lockView 权重为 1 ，PandaEmoView 高度包裹内容即可

![布局规则说明][5]
4.使用控件的 Activity Java 代码设置
``` java
//界面控件初始化后 .attachEditText()绑定输入控件

//初始化 KeyBoardManager，PandaEmoView.attachEditText() 必须在后调用

```
# 主要使用类及公有方法概览
## PandaEmoEditText 
- 表情输入框继承自 `EditText` 只对 `onKeyIme()` 进行复写用于监听输入键盘或者软键盘的弹出与关闭

## PandaEmoView
- 表情输入控件 View 继承自 `RelativeLayout`

| 方法 | 返回值 | 参数  | 描述 | 
| :-- | :-- | :-- |   :--    |
|   attachEditText(PandaEmoEditText input)   |    void    |  表情输入控件（如使用自定义 EditText 可直接继承重写）   |  绑定输入控件   |
|   getAttachEditText()   |    void    |     |   获取当前表情控件绑定的输入框控件  |
|   reloadEmos   |    void    |  position 重载表情控件数据后默认选中 Tab 的 position   |  添加或者删除表情数据后重载刷新表情控件   |

## PandaEmoManager
- `PandaEmoManager` 为核心配置类，表情控件的各种参数都通过此类的构造器进行配置

| 属性 | 类型 | 描述 |默认值|
| :--  |:--    |:--  |:--  |
|   EMOT_DIR   |    String   | 默认表情图在 assets 中的路径 (assets 目录到配置 xml 文件之间的部分的路径，demo 中的 face)|face|
|   SOURCE_DIR   |   String    |  EMOT_DIR 目录下存放图片资源的文件夹路径 （demo 中的 images）|source_default|
|   STICKER_PATH   |    String   | Sticker贴图包的存放目录，该目录下的每一个文件目录都为一个贴图包|   /data/data/< package name>/files/sticker   |
|   CACHE_MAX_SIZE   |   int    |  加载表情 LruCache 缓存大小  | 1024 |
|   DEFAULT_EMO_BOUNDS_DP   |   int    |   表情图显示大小（非贴图表情）   |   30dp   |
|   defaultIcon   |   int    |    表情 Tab 资源文件名  |   R.drawable.ic_default   |
|   mContext  |   Context    |   上下文   |   null   |
|   mConfigFile   |    String   |   EMOT_DIR 目录下的 emoji 配置文件名称   |   emoji_default.xml   |
|   mIImageLoader   |   IImageLoader    |  Sticker 图片加载器接口，加载方式外部传入   |  null    |
|   MAX_CUSTOM_STICKER   |    int   |  最大添加的自定义贴图表情数    |  30    |
|   EMOJI_ROW   |   int     |   emoji 表情单页行数  |   3  |
|   EMOJI_COLUMN   |  int      |  emoji 表情单页列数   |  7   |
|   STICKER_ROW   |    int    |  sticker 表情单页行数   |  2   |
|   STICKER_COLUMN   | int       |  sticker 表情单页列数   |  4   |
|   showAddButton   |     boolean   |  tab 栏是否显示添加按钮   |   ture  |
|   showSetButton   |   boolean     |   tab 栏是否显示设置按钮  |ture|
|   showStickers   |   boolean     |  tab 栏是否显示贴图按钮（所以 sticker）   |  ture   |

| 方法 | 返回值 | 参数  | 描述 | 
| :-- | :-- | :-- |   :--    |
|   init（）   |   void     |  无参   |  初始化 拼接 Sticker 路径及创建自定义贴图 文件夹 (STICKER_PATH + "/selfSticker" ) |
|   makePattern()   |    Pattern    |   无参  |  创建 emoji 正则匹配器  |
`剩余方法都为属性值的 getter() setter() 不在赘述。`
## PandaEmoManager.Builder
- `PandaEmoManager` 的构造器类，属性及方法都与 PandaEmoManager 一一对应；

## KeyBoardManager
- `KeyBoardManager` 为输入法软键盘与表情输入控件协调管理类
 
| 属性 | 类型 | 描述 | 默认值 |
| :-- | :-- | :-- |   :--    |
|   SHARE_PREFERENCE_NAME   |    String    |   用于存储键盘高度的 SP 的名字  |  "EmotionKeyBoard"   |
|    SHARE_PREFERENCE_SOFT_INPUT_HEIGHT  |   String     |  用于存储键盘高度的 key   |  "EmotionKeyBoard" |
|    mActivity  |    Activity    |  控件依附的 Activity 界面   |  null   |
|    mEmotionView  |   PandaEmoView   |   当前管理的表情输入控件  |  null   |
|    interceptBackPress  |    boolean    |  是否拦截返回键   |  false   |
|    lockView  |    View    |  锁定高度的 View（即同一线性父布局中，表情控件之外的布局视图）   |   null  |
|    mOnEmotionButtonOnClickListener  |    OnEmotionButtonOnClickListener    |  表情显示控制按钮监听   |  null   |
|    mOnInputShowListener  |     OnInputShowListener   |   监听输入栏的弹出与关闭  |  null   |

| 方法 | 返回值 | 参数  | 描述 | 
| :------  | :--    | :-- |   :--    |
|   with()  |   KeyBoardManager     |   Activity : 当前输入控件依附的 Activity  |   初始化 KeyBoardManager 创建单例 |
|   bindToLockContent()   |    KeyBoardManager    |  lockView:切换需要锁定高度的 View    |  赋值给内部属性 lockView   |
|   bindToEmotionButton()    |    KeyBoardManager    |   View...: 多个 View 参数，切换控制按钮  |  为输入控件绑定控制按钮   |
|   setEmotionView()   |    KeyBoardManager    | PandaEmoView: 被管理的表情控件  | 绑定当前管理的输入控件（绑定的控件必须在此之前调用 PandaEmoView.attachEditText() 否则内部将会空指针）  |
|   interceptBackPress()         |    boolean    |   null  |  在 Activity 的 backPressd()  中检查是否需要拦截返回键关闭输入栏而不是退出界面  |
|  hideInputLayout()     | void|null | 供外部调用关闭输入栏（不能未初始化直接调用） |
|  showInputLayout()   | void|null | 供外部调用显示输入栏（不能未初始化直接调用） |
## EmoticonManager
- `EmoticonManager` 为 emoji 表情加载管理类，此类提供方法将资源文件根据配置文件加载进内存，方法大多数为私有方法，源码中可查看注释。

## StickerManager
- `StickerManager` 为 sticker 表情加载管理类，此类提供方法将资源文件根据配置文件加载进内存，与 `EmoticonManager` 类似 

## PandaEmoTranslator
- `PandaEmoTranslator` 为 emoji 表情  [文字] 转表情的转换工具类

| 方法 | 返回值 | 参数  | 描述 | 
| :-- | :-- | :-- |   :--    |
|   getInstance()   |    PandaEmoTranslator    |  null   |  获取文本表情转换器单例   |
|   setMaxGifPerView()   |    void    |   maxGifPerView: 每个 TextView 控件最多显示动态表情的个数   |   设置每个 TextView 控件最多显示动态表情的个数，超过此数全部显示为静态表情  |
|   getMaxGifPerView()  |    int    |  null   |  获取每个 TextView 控件最多显示动态表情的个数  |
|   makeGifSpannable()   |   SpannableString   |  classTag:PandaEmoView 依附的 ActivityTag（推荐使用 Activity.getLocalClassName()）；value : 待替换的文本 ；gif 运行回调（回调中需要刷新 TextView 重绘）|  整段图文混排，支持 gif 和静态 emoji   |
|   makeEmojiSpannable()   |   SpannableString     |  classTag:PandaEmoView 依附的 ActivityTag（推荐使用 Activity.getLocalClassName()）；value : 待替换的文本 ；gif 运行回调（回调中需要刷新 TextView 重绘）   |   整段图文混排，所以内容转换为静态 emoji  |
|   resumeGif()   |    void    |  activityTag : makeGifSpannable() 传入的 tag 名 |  开始 activityTag 对应的所有 gif 表情执行  |
|   pauseGif()   |   void     |  暂停所有的 Gif 表情运行   |     |
|   clearGif()   |    void    |   activityTag : makeGifSpannable() 传入的 tag 名  |   停止 activityTag 对应的所有 gif 表情执行，并将期从任务栈中移除  |
# 关于内存优化
因为表情，gif 表情，自定义贴图，表情包贴图这些都涉及到图片资源加载到内存中。因此开发过程中不可避免的也遇到了许多的内存优化相关的问题。
### 工具
就地取材，直接使用 Android Studio 的 Monitors 工具可以直观的查看到应用运行过程中内存的变化过程
### 优化点1 —— Gif 播放类的优化
- 问题：
参考网上的 gif 图文混排项目，虽然实现了 gif 与文字的图文混排效果，但存在致命的缺陷。该项目中每一个 gif 动态表情图都有一个对应的 Runable 对象去执行 gif 图片的逐帧播放，当一个表情重复输入也会有新的 Runable 对象去执行这样的操作，这样做的后果就是当输入的表情数量增加时，所消耗的内存是持续增长的。这显然不能满足生产使用的需求。
- 解决方案：
考虑到此处内存增加的原因是让表情动起来的 Runable 泛滥引起的，因此减少 Runable 的数量就是解决此处内存问题的关键。我的方案做的比较彻底，整个应用 gif 表情这一块儿都交给一个 Runable 去处理，这个 Runable 在 PandaTranslator 中进行图文转化时会被初始化
``` java
 // PandaTranslator 的 103 - 107 行 
 103                   if (mGifRunnable == null) {
 104                      mGifRunnable = new GifRunnable(gifDrawable, mHandler);
 105                   } else {
 106                      mGifRunnable.addGifDrawable(gifDrawable);
 107                   }
```
因为 PandaTranslator 是一个单例实现，所以在他初始化后 mGifRunnable 也将保持唯一性。无论是新建初始化还是 addGifDrawable() 都是把 Gif 表情对象放入 GifRunnable 中的一个 Map 中。Map 的 key value 分别是表情控件依附的 Activity 的 LocalName 和 一个 AnimatedGifDrawable 的 List。在 GifRunnable 的 run 方法中会根据当前的 Activity 的 LocalName 去取出对应的 AnimatedGifDrawable 列表，遍历执行并按第一张 gif 表情的帧间隔去刷新 Drawable 并触发 TextView 刷新回调
``` java
@Override
    public void run() {
        isRunning = true;
        if (currentActivity != null) {
            List<AnimatedGifDrawable> runningDrawables = mGifDrawableMap.get(currentActivity);
            if (runningDrawables != null) {
                for (AnimatedGifDrawable gifDrawable : runningDrawables) {
                    AnimatedGifDrawable.RunGifCallBack listener = gifDrawable.getUpdateListener();
                    List<AnimatedGifDrawable.RunGifCallBack> runningListener = listenersMap.get(currentActivity);
                    if (runningListener != null) {
					// 避免一个 TextView 多个表情时重复添加回调
                        if (!runningListener.contains(listener)) {
                            runningListener.add(listener);
                        }
                    } else {
                        // 为空时肯定不存在直接添加
                        runningListener = new ArrayList<>();
                        runningListener.add(listener);
                        listenersMap.put(currentActivity, runningListener);
                    }
                    gifDrawable.nextFrame();
                }
                for (AnimatedGifDrawable.RunGifCallBack callBack : listenersMap.get(currentActivity)) {
                    if (callBack != null) {
                        callBack.run();
                    }
                }
                frameDuration = runningDrawables.get(0).getFrameDuration();
            }
        }
        mHandler.postDelayed(this, frameDuration);
    }
```
这样就实现了全局使用一个 Runable 来执行 gif 动起来的任务，不同的界面也仅需要将该界面的 AnimatedGifDrawable 对象加入任务 Map 即可。
### 优化点2 —— 界面暂停或退出时 Gif 播放资源同步退出回收
上面说到的将 AnimatedGifDrawable 列表加入任务 Map，只进不出显然是不科学的也会持续增加内存的消耗。我们希望在 Activity 退出时能将将当前 Activity 的 AnimatedGifDrawable 列表销毁移除，在界面不可见但是可能会恢复时（pause 状态）暂停 Runable 的执行，减少资源消耗。于是 GifRunable 提供了如下三个方法给外部调用
``` java
/**
     * 使用了表情转换的界面退出时调用，停止动态图handler
     */
    public void clearHandler(String activityName) {
        currentActivity = null;
        //清除当前页的数据
        mGifDrawableMap.remove(activityName);
        // 当退出当前Activity后没表情显示时停止 Runable 清除所有动态表情数据
        listenersMap.remove(activityName);
        if (mGifDrawableMap.size() == 0) {
            clearAll();
        }
    }

    private void clearAll() {
        mHandler.removeCallbacks(this);
        mHandler.removeCallbacksAndMessages(null);
        mGifDrawableMap.clear();
        isRunning = false;
    }

    /**
     * 启动运行
     */
    public void startHandler(String activityName) {
        currentActivity = activityName;
        if (mGifDrawableMap != null && mGifDrawableMap.size() > 0 && !isRunning) {
            run();
        }
    }
```
它的调用入口都在 PandaTranslator 中，然后我们只需在使用到 PandaEmoView 或者直接在 BaseActivity 的 onResume(),onPause(),onDestory() 中分别调用以下三个方法：
``` java
PandaTranslator.getInstance().resumeGif(activityLocalName);

PandaTranslator.getInstance().pauseGif();

PandaTranslator.getInstance().clearGif(activityLocalName)
```
### 优化点3 —— 使用 LruCache 缓存 emoji 资源
根据 LRU 规则将表情 Gif 缓存，避免重复加载创建新对象。

本库地址 [PandaEmoView][6] 欢迎 star 和提 issue 


  [1]: http://oddbiem8l.bkt.clouddn.com/pandemoview.gif
  [2]: http://oddbiem8l.bkt.clouddn.com/panda_assets.png
  [3]: http://oddbiem8l.bkt.clouddn.com/emoji_config.png
  [4]: http://oddbiem8l.bkt.clouddn.com/panda_sticker.png
  [5]: http://oddbiem8l.bkt.clouddn.com/xml_layout.png
  [6]: https://github.com/PandaQAQ/PandaEmoView
# License
``` html
Copyright 2017 PandaQ.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
