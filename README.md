# Android组件化总结

### 1.什么是组件化

一个大型APP版本一定会不断的迭代，APP里的功能也会随之增加，项目的业务也会变的越来越复杂，这样导致项目代码也变的越来越多，
开发效率也会随之下降。并且单一工程下代码耦合严重，每修改一处代码后都要重新编译，非常耗时，单独修改的一个模块无法单独测试。

组件化架构的目的是让各个业务变得相对独立，各个组件在组件模式下可以独立开发调试，集成模式下又可以集成到“app壳工程”中，
从而得到一个具有完整功能的APP。 组件化每一个组件都可以是一个APP可以单独修改调试，而不影响总项目。

![项目结构图](https://img-blog.csdnimg.cn/img_convert/b5786f559aa7451ec7d941003951de6c.png)


### 2.为什么要用组件化

1. **编译速度**：    可以测试单一模块，极大提高了开发速度
2. **超级解耦**：    极度降低了模块间的耦合，便于后期的维护和更新
3. **功能重用**：    某一块的功能在另外的组件化项目中使用只需要单独依赖这一模块即可
4. **便于团队开发**： 组件化架构是团队开发必然会选择的一种开发方式,它能有效的使团队更好的协作

###  3.怎样实现组件化

* 代码解耦。将一个庞大的工程拆分解耦，这是非常耗时耗力的工作，但这也是最基础最重要的一步
* 数据传递。每个组件都有可能提供给其他组件使用，主项目与组件、组件与组件之间的数据传递
* UI跳转。
* 集成调试。在开发阶段如何做到按需的编译组件？一次调试中可能只有一两个组件参与集成，这样编译的时间就会大大降低，提高开发效率。
* 代码隔离。如何杜绝耦合的产生。

#### 1.新建项目模块

分别在项目中创建项目组件**login, share,commonlibs,app**等四个模块，其中**commmonlibs**是公用类库。**login，share,app**每个模块目前都可以单独运行

  ![项目结构图](https://seikim.com/i/2022/07/19/n861rf.png)

#### 2.统一Gradle版本号

**每一个模块都是一个application(除了commonlibs,它是个公用的类库)，所以每个模块都会有一个build.gradle，各个模块里面的配置不同，我们需要重新统一Gradle**
1. 在主模块创建config.gradle

   ![config](https://obohe.com/i/2022/07/19/ibrhcl.png)
   
2. 在config.gradle里去添加一些版本号
``` java
// 统一Gradle版本号
ext{
    android = [
            compileSdkVersion :32,
            buildToolsVersion: "30.0.2",
            applicationId :"com.example.routerdemo",
            minSdkVersion: 21,
            targetSdkVersion :32,
            versionCode :1,
            versionName :"1.0",
    ]
    dependencies = [
            "appcompat"             : 'androidx.appcompat:appcompat:1.3.0',
            "material"               : 'com.google.android.material:material:1.4.0',
            "constraintLayout"       : 'androidx.constraintlayout:constraintlayout:2.0.4',//约束性布局
            //test
            "junit"                  : "junit:junit:4.13.2",
            "testExtJunit"           : 'androidx.test.ext:junit:1.1.3',//测试依赖，新建项目时会默认添加，一般不建议添加
            "espressoCore"           : 'androidx.test.espresso:espresso-core:3.4.0',//测试依赖，新建项目时会默认添加，一般不建议添加
    ]
}
``` 
3. 在主模块的build.gradle里添加
``` java
//引用config.gradle
apply from: "config.gradle"
``` 
4. 在各模块中去引用这些版本号,如**commonlibs**中的build.gradle
``` java
android {
    
    compileSdk rootProject.ext.android["compileSdkVersion"]
    defaultConfig {
        minSdk rootProject.ext.android["minSdkVersion"]
        targetSdk rootProject.ext.android["targetSdkVersion"]
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles "consumer-rules.pro"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation rootProject.ext.dependencies["appcompat"]
    implementation rootProject.ext.dependencies["material"]
    
    testImplementation rootProject.ext.dependencies["junit"]
    androidTestImplementation rootProject.ext.dependencies["testExtJunit"]
    androidTestImplementation rootProject.ext.dependencies["espressoCore"]

}
``` 
   
#### 3.组件模式和集成模式转换
1. 在主模块gradle.properties里添加布尔类型选项。
``` java
//ture 表示当模块login,share 为可独立运行的App,false表示login,share等模块为组件，将集成到app中
is_Module=false
``` 
2. 在各个模块的build.gradle里添加更改语句
``` java
//组件模式和集成模式的转换
if(is_Module.toBoolean()){
    apply plugin: 'com.android.application'
}else{
    apply plugin: 'com.android.library'
}
``` 
3. 每个模块的applicationId也需要处理
``` java
  //当我们将is_module改为false时，再次运行编译器我们的模块都不能单独运行了
  //在login模块的build.gradle文件中修改
  if(is_Module.toBoolean()){
            applicationId "com.example.login"
        }
        
  //在share模块的build.gradle文件中修改 
  if(is_Module.toBoolean()){
            applicationId "com.example.share"
        }
``` 
4. 在**app**模块中添加判断依赖就可以在集成模式下将各模块添加到**app**主模块中
``` java
 //将各个组件集成到主app中
    if(is_Module.toBoolean()){
          implementation project(':login')
          implementation project(':share')
    }
```

#### 4.AndroidManifest的切换
1. 在组件模块里的main文件里新建manifest文件夹

   ![config](https://seikim.com/i/2022/07/19/naai9o.png)

2. 重写一个AndroidManifest.xml文件，集成模式下，业务组件的表单是绝对不能拥有自己的 Application 和 launch 的 
Activity的，也不能声明APP名称、图标等属性，总之app壳工程有的属性，业务组件都不能有，在这个表单中只声明了应用的主题，
而且这个主题还是跟app壳工程中的主题是一致的
``` java
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.login">

    <application
        android:theme="@style/Theme.RouterDemo">
        <activity android:name=".LoginActivity">
        </activity>
    </application>

</manifest>
```
3. 我们还要使其在不同的模式下加载不同的AndroidManifest只需在各模块的build.gradle里添加更改语句
``` java
 // 定义集成模式下和组件模式下manifest文件的相关配置
    sourceSets {
        main {
            if (is_Module.toBoolean()) {
                manifest.srcFile 'src/main/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/manifest/AndroidManifest.xml'
            }
        }
    }
```
#### 5.Application切换
每个模块在运行时都会有自己的application，而在组件化开发过程中，我们的主模块只能有一个application，但在单独运行时又需要自己的application这里就需要配置一下。
1. 在业务模块添加新文件夹命名module

   ![config](https://obohe.com/i/2022/07/19/nbsgf0.png)

2. 在里面建一个application文件

   ![config](https://obohe.com/i/2022/07/19/nc8qry.png)

3. 并且我们在build.gradle文件里配置module文件夹使其在单独运行时能够运行单独的application
   在配置manifest的语句中添加java.srcDir 'src/main/module'
``` java
 // 定义集成模式下和组件模式下manifest文件的相关配置
    sourceSets {
        main {
            if (is_Module.toBoolean()) {
                manifest.srcFile 'src/main/AndroidManifest.xml'
                java.srcDir('src/main/moudle')
            } else {
                manifest.srcFile 'src/main/manifest/AndroidManifest.xml'
            }
        }
    }
```
4. 我们可以在**commonlibs**基础层内新建application，用于加载一些数据的初始化
``` java
 public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","BaseApplication onCreate()");
    }
    
}
```
5. 在业务模块内module里重写该模块的application
``` java
 public class LoginApplication extends BaseApplication {
    @Override
    public void onCreate() {
        Log.e("TAG","LoginApplication onCreate()");
        super.onCreate();
    }
}

```

# 组件间的业务数据交互
### 1. 组件之间的数据传递
  由于主项目与组件，组件与组件之间都是不可以直接使用类的相互引用来进行数据传递的，那么在开发过程中如果有组件间的数据传递时应该如何解决呢，这里我们可以采用 [接口 + 实现] 的方式来解决。 在commonlibs基础库里定义组件可以对外提供访问自身数据的抽象方法的 Service。并且提供了一个 ServiceFactory，每个组件中都要提供一个类实现自己对应的 Service 中的抽象方法。在组件加载后，需要创建一个实现类的对象，然后将实现了 Service的类的对象添加到ServiceFactory 中。这样在不同组件交互时就可以通过 ServiceFactory 获取想要调用的组件的接口实现，然后调用其中的特定方法就可以实现组件间的数据传递与方法调用。 ServiceFactory 中也会提供所有的 Service 的空实现，在组件单独调试或部分集成调试时避免出现由于实现类对象为空引起的空指针异常。 


**具体实现**

1.其中 service文件夹中定义接口，LoginService 接口中定义了 Login 组件向外提供的数据传递的接口方法，EmptyService 中是 service 中定义的接口的空实现，ServiceFactory 接收组件中实现的接口对象的注册以及向外提供特定组件的接口实现。
``` java
  public interface LoginService {
    /**
     *是否登录
     * @return
     */
    boolean isLogin();

    /**
     * 获取登录用户的密码
     * @return
     */
    String getPassword();
}

public class EmptyService  implements  LoginService{
    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String getPassword() {
        return null;
    }
}

public class ServiceFactory {
    private LoginService loginService;

    /**
     * 禁止外部创建 ServiceFactory 对象
     */
    private ServiceFactory() {
    }

    /**
     * 通过静态内部类方式实现 ServiceFactory 的单例
     */
    public static ServiceFactory getInstance() {
        return Inner.serviceFactory;
    }

    private static class Inner {
        private static ServiceFactory serviceFactory = new ServiceFactory();
    }

    /**
     * 接收 Login 组件实现的 Service 实例
     */
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 返回 Login 组件的 Service 实例
     */
    public LoginService getLoginService() {
        if (loginService == null) {
            return new EmptyService();
        } else {
            return loginService;
        }
    }
}
```
2. 在login模块中实现接口
``` java
public class AccountService implements LoginService {
    private boolean isLogin;
    private String password;

    public AccountService(boolean isLogin,String password){
        this.isLogin = isLogin;
        this.password = password;
    }
    @Override
    public boolean isLogin() {
        return isLogin;
    }

    @Override
    public String getPassword() {
        return password;
    }
}

```
3. 新建一个Utils类用来存储登录数据
``` java
/**
 * 存储登录数据
 */
public class LoginUtils {
    static boolean isLogin = false;
    static String password = "123456";

}
```
4. 实现一下登录操作
``` java
      findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtils.isLogin = true;
                ServiceFactory.getInstance().setLoginService(new AccountService(true,LoginUtils.password));
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            }
        });
```
5. 分享模块获取登录信息
``` java
      findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ServiceFactory.getInstance().getLoginService().isLogin()){
                    Toast.makeText(ShareActivity.this,"分享成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ShareActivity.this,"分享失败，请先登录！",Toast.LENGTH_SHORT).show();
            }
            }
        });
```

#### 2. 组件之间的跳转

可以采用里巴巴的开源库ARouter来实现跳转功能,ARouter一个用于帮助 Android App 进行组件化改造的框架 —— 支持模块间的路由、通信、解耦







# ARouter

在代码里加入的@Route注解，会在编译时期通过apt生成一些存储path和activityClass映射关系的类文件，然后app进程启动的时候会拿到这些类文件，把保存这些映射关系的数据读到内存里(保存在map里)，然后在进行路由跳转的时候，通过build()方法传入要到达页面的路由地址，ARouter会通过它自己存储的路由表找到路由地址对应的Activity.class(activity.class = map.get(path))，然后new Intent()，当调用ARouter的withString()方法它的内部会调用intent.putExtra(String name, String value)，调用navigation()方法，它的内部会调用startActivity(intent)进行跳转，这样便可以实现两个相互没有依赖的module顺利的启动对方的Activity了。

**ARouter简介**

ARouter 是阿里开源的一款帮助 Android App 进行组件化改造的路由框架，是 Android 平台中对页面、服务提供路由功能的中间件，可以实现在不同模块的 Activity 之间跳转。

ARouter 的特点是灵活性强以及帮助项目解耦。

1. **灵活性强**

* 在一些复杂的业务场景下，很多功能都是运营人员动态配置的。比如电商系统需要下发一个活动页面，App 事先不知道该活动具体的目标页面，但如果提前做好了页面映射，就可以自由配置了。
2. **项目解耦**

* 随着业务量增长，App 项目代码会越来越多，开发人员之间的协作也会变得越来越复杂，而解决这个问题的方案一般就是插件化和组件化。
* 插件化和组件化的前提是解耦，解耦后还要保持页面之间的依赖关系，这时就需要一套路由机制了。


### 典型应用
1. 从外部URL映射到内部页面，以及参数传递与解析
2. 跨模块页面跳转，模块间解耦
3. 拦截跳转过程，处理登陆、埋点等逻辑
4. 跨模块API调用，通过控制反转来做组件解耦

### 基本功能
1. 添加依赖和配置
``` java

 android {
    defaultConfig {
        //app,loin,share ,commonlibs等module中的build.gradle文件中都要添加
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [AROUTER_MODULE_NAME: project.getName()]
            }
        }
    }
}

dependencies {
    // 替换成最新版本, 需要注意的是api
    // 要与compiler匹配使用，均使用最新版可以保证兼容
    compile 'com.alibaba:arouter-api:1.5.2'
    //app,loin,share ,commonlibs等module中的build.gradle文件中都要添加
    annotationProcessor 'com.alibaba:arouter-compiler:1.5.2'
    ...
}
```
2. 添加注解
``` java
// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx
@Route(path = "/login/LoginActivity")
public class LoginActivity extends AppCompatActivity {
    ...
}
```
3. 初始化SDK,可在MainApplication中的初始化
``` java
    private void initARouter() {
        if(BuildConfig.DEBUG){
           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();// 打印日志
            ARouter.openDebug();// 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
    }
```
4. 发起路由操作跳转
``` java
  // 1. 应用内简单的跳转(通过URL跳转在'进阶用法'中)
ARouter.getInstance().build("/login/LoginActivity").navigation();

// 2. 跳转并携带参数
ARouter.getInstance().build("/login/LoginActivity"")
            .withLong("key1", 666L)
            .withString("key3", "888")
            .withObject("key4", new Test("Jack", "Rose"))
            .navigation();
```

### ARouter中拦截器IInterceptor的应用

ARouter提供了IInterceptor接口和@Interceptor注解，供开发者实现自定义拦截器。IInterceptor拦截的方法在process()中，在方法中调用callback.onInterrupt()则拦截跳转，调用callback.onContinue(）则继续跳转。

**@Interceptor**

  ARouter的@Interceptor注解中可以设置Inteceptor的优先级。判断name和age是否为空的Interceptor如下：

``` java
// 比较经典的应用就是在跳转过程中处理登陆事件，这样就不需要在目标页重复做登陆检查
// 拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行 priority值越大优先级越高
@Interceptor(priority = 1,name = "登录状态拦截器")
public class LoginInterceptor implements IInterceptor {
    @Override
    public void process(Postcard postcard, InterceptorCallback callback) {
    callback.onContinue(postcard); // 处理完成，交还控制权
     // callback.onInterrupt(new RuntimeException("我觉得有点异常"));      // 觉得有问题，中断路由流程
    // 以上两种至少需要调用其中一种，否则不会继续路由
    }
    @Override
    public void init(Context context) {
    }
}
```

**NavigationCallback**

  在实现了拦截器后，在ARouter执行跳转时传入NavigationCallback参数，就可以收到拦截器是否拦截的回调：

``` java
 
    //拦截器回调
    private NavigationCallback navigationCallback = new NavigationCallback() {
        @Override
        public void onFound(Postcard postcard) { }
        @Override
        public void onLost(Postcard postcard) { }
        @Override
        public void onArrival(Postcard postcard) { }
        @Override
        public void onInterrupt(Postcard postcard) {
            //如果被拦截器拦截之后会收到onInterrupt()回调
            System.out.println("navigationCallback onInterrupt():"+postcard.getPath());
        }
    };
```


### ARouter源码分析

 ARouter是通过三步（添加注解、初始化SDK、发起路由）来实现的

#### 1. 注解处理APT


#### 2. 初始化SDK

``` java
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(getApplication());
```
上面是ARouter初始化，我们可以看看init()里面做了什么事情
``` java
     public static void init(Application application) {
        if (!hasInit) {
            logger = _ARouter.logger;
            _ARouter.logger.info(Consts.TAG, "ARouter init start.");
            hasInit = _ARouter.init(application);

            if (hasInit) {
                _ARouter.afterInit();
            }

            _ARouter.logger.info(Consts.TAG, "ARouter init over.");
        }
    }
```
可以看到最终调用的都是在_ARouter这个类里面
``` java
  protected static synchronized boolean init(Application application) {
        mContext = application;
        LogisticsCenter.init(mContext, executor);
        logger.info(Consts.TAG, "ARouter init success!");
        hasInit = true;
        mHandler = new Handler(Looper.getMainLooper());

        return true;
    }
```
核心就是LogisticsCenter.init()这个方法


``` java

```

#### 3. 发起路由











