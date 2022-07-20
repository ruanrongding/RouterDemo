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

添加注解@Route(path = "/login/LoginActivity")后,ARouter是使用arouter-compiler来处理注解，自动生成代码，在此基础上实现路由跳转的功能。

ARouter APT自动生成三个class文件（位于login/build/generated/ap_generated_sources/debug/out/目录下）

![项目结构图](https://obohe.com/i/2022/07/20/m7qdv6.png)

这三个class分别实现了IRouteGroup、IRouteRoot、IProviderGroup，且类名都以ARouter$开头，都位于com.alibaba.android.arouter.routes包下：
``` java
public class ARouter$$Group$$login implements IRouteGroup {
  @Override
  public void loadInto(Map<String, RouteMeta> atlas) {
    atlas.put("/login/LoginActivity", RouteMeta.build(RouteType.ACTIVITY, LoginActivity.class, "/login/loginactivity", "login", null, -1, -2147483648));
  }
}

public class ARouter$$Providers$$login implements IProviderGroup {
  @Override
  public void loadInto(Map<String, RouteMeta> providers) {
  }
}

public class ARouter$$Root$$login implements IRouteRoot {
  @Override
  public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
    routes.put("login", ARouter$$Group$$login.class);
  }
}
```

自动生成的代码有什么用途？


#### 2. 初始化SDK

``` java
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(getApplication());
```
Arouter的初始化ARouter.init(getApplication())，过程如下：

![项目结构图](https://seikim.com/i/2022/07/20/mgr3rt.png)

我们具体看看init()里面做了什么事情
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
public class LogisticsCenter {
    private static Context mContext;
    static ThreadPoolExecutor executor;
    private static boolean registerByPlugin;

    public synchronized static void init(Context context, ThreadPoolExecutor tpe) throws HandlerException {
        mContext = context;
        executor = tpe;
        //load by plugin first
        loadRouterMap();
        if (registerByPlugin) {
            logger.info(TAG, "Load router map by arouter-auto-register plugin.");
        } else {
            // 1.关键代码routeMap
            Set<String> routerMap;
            // It will rebuild router map every times when debuggable.
            // 2.debug模式或者PackageUtils判断本地路由为空或有新版本
            if (ARouter.debuggable() || PackageUtils.isNewVersion(context)) {
                // 3.获取ROUTE_ROOT_PAKCAGE(com.alibaba.android.arouter.routes)包名下的所有类
                // arouter-compiler根据注解自动生成的类都放在com.alibaba.android.arouter.routes包下
                routerMap = ClassUtils.getFileNameByPackageName(mContext, ROUTE_ROOT_PAKCAGE);
                // 4.建立routeMap后保存到sp中，下次直接从sp中读取StringSet;逻辑见else分支;
                if (!routerMap.isEmpty()) {
                    context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).edit().putStringSet(AROUTER_SP_KEY_MAP, routerMap).apply();
                }
                // 5.更新本地路由的版本号
                PackageUtils.updateVersion(context);    // Save new version name when router map update finishes.
            } else {
                routerMap = new HashSet<>(context.getSharedPreferences(AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE).getStringSet(AROUTER_SP_KEY_MAP, new HashSet<String>()));
            }
            // 6.获取routeMap后,根据路由类型注册到对应的分组里
            for (String className : routerMap) {
                if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_ROOT)) {
                    // 7.加载root，类名以SUFFIX_ROOT(com.alibaba.android.arouter.routes.ARouter$$Root)开头
                    // 以<String,Class>添加到HashMap(Warehouse.groupsIndex)中
                    ((IRouteRoot) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.groupsIndex);
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_INTERCEPTORS)) {
                    // 8.加载interceptorMeta，类名以SUFFIX_INTERCEPTORS(com.alibaba.android.arouter.routes.ARouter$$Interceptors)开头
                    // 以<String,IInterceptorGroup>添加到UniqueKeyTreeMap(Warehouse.interceptorsIndex)中;以树形结构实现顺序拦截
                    ((IInterceptorGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.interceptorsIndex);
                } else if (className.startsWith(ROUTE_ROOT_PAKCAGE + DOT + SDK_NAME + SEPARATOR + SUFFIX_PROVIDERS)) {
                    // 9.加载providerIndex，类名以SUFFIX_PROVIDERS(com.alibaba.android.arouter.routes.ARouter$$Providers)开头
                    // 以<String,IProviderGroup>添加到HashMap(Warehouse.providersIndex)中
                    ((IProviderGroup) (Class.forName(className).getConstructor().newInstance())).loadInto(Warehouse.providersIndex);
                }
            }
        }
    }
}
```

我们可以看到，首先是去获取到所有的app里的由ARouter注解生成的类的类名，他们的统一特点就是在同一个包下，包名为：
com.alibaba.android.arouter.routes
然后就是循环遍历这些类，也就是刚才我们说的那三种类。在这里，有一个
Warehouse类，看下代码

``` java
class Warehouse {
    // Cache route and metas
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    static Map<String, RouteMeta> routes = new HashMap<>();

    // Cache provider
    static Map<Class, IProvider> providers = new HashMap<>();
    static Map<String, RouteMeta> providersIndex = new HashMap<>();

    // Cache interceptor
    static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
    static List<IInterceptor> interceptors = new ArrayList<>();
}
结合前面自动生成的代码来分析，Warehouse.groupsIndex中存放的key就是@Route(path = "/login/LoginActivity")注解中所指定的path，value就是class：

```

#### 3. 发起路由

发起跳转
``` java
ARouter.getInstance().build("/login/LoginActivity").navigation();
```
最终调用代码则是_ARouter类里面的_navigation方法：

我们先看ARouter的源码实现
``` java
public final class ARouter {
    // 单例模式
    public static ARouter getInstance() {
        if (!hasInit) {
            throw new InitException("ARouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (ARouter.class) {
                    if (instance == null) {
                        instance = new ARouter();
                    }
                }
            }
            return instance;
        }
    }

    public Postcard build(String path) {
        // _ARouter也是单例模式
        return _ARouter.getInstance().build(path);
    }
}
```

ARouter.getInstance()的单例模式的实际调用也是在_ARouter.getInstance()中， build(String path)、navigation()等代码实际实现都在_ARouter中


分析_ARouter.getInstance().build()方法，方法返回Postcard对象，该对象表示一次路由操作所需的全部信息：
``` java
final class _ARouter {
    protected Postcard build(String path) {
        // 1.首先获取PathReplaceService，判断是否重写跳转URL，默认为空
        // 进阶用法可以自定义类实现PathReplaceService来实现重写跳转URL，见github README
        PathReplaceService pService = ARouter.getInstance().navigation(PathReplaceService.class);
        if (null != pService) {
            path = pService.forString(path);
        }
        // 2.构造Postcard对象
        return build(path, extractGroup(path), true);
    }
    
    /**
    * 取出path中的组路径: /后的第一个即group
    */
    private String extractGroup(String path) {
        String defaultGroup = path.substring(1, path.indexOf("/", 1));
        return defaultGroup;
    }

    /**
    * 构造Postcard对象
    */
    protected Postcard build(String path, String group, Boolean afterReplace) {
        // 1.同build(String path)中的说明，判断是否重写跳转URL，默认没有重写的实现，afterReplace为true
        if (!afterReplace) {
            PathReplaceService pService = ARouter.getInstance().navigation(PathReplaceService.class);
            if (null != pService) {
                path = pService.forString(path);
            }
        }
        // 2.构造Postcard对象
        return new Postcard(path, group);
    }
}
```

Postcard表示明信片，代表一次路由操作的所需信息，如下所示，信息比较多，我们暂时先只关注其父类RouteMeta的group和path属性：
``` java
public final class Postcard extends RouteMeta {
    // Base
    private Uri uri;
    private Object tag;             // A tag prepare for some thing wrong. inner params, DO NOT USE!
    private Bundle mBundle;         // Data to transform
    private int flags = 0;         // Flags of route
    private int timeout = 300;      // Navigation timeout, TimeUnit.Second
    private IProvider provider;     // It will be set value, if this postcard was provider.
    private boolean greenChannel;
    private SerializationService serializationService;
    private Context context;        // May application or activity, check instance type before use it.
    private String action;

    // Animation
    private Bundle optionsCompat;    // The transition animation of activity
    private int enterAnim = -1;
    private int exitAnim = -1;
}

public class RouteMeta {
    private RouteType type;         // Type of route
    private Element rawType;        // Raw type of route
    private Class<?> destination;   // Destination
    private String path;            // Path of route  路径
    private String group;           // Group of route 组
    private int priority = -1;      // The smaller the number, the higher the priority
    private int extra;              // Extra data
    private Map<String, Integer> paramsType;  // Param type
    private String name;
    private Map<String, Autowired> injectConfig;  // Cache inject config.
}
```

ARouter.getInstance().build("/login/LoginActivity")返回Postcard对象，接下来继续分析Postcard.navigation()：

``` java
public final class Postcard extends RouteMeta {
    public Object navigation() {
        return navigation(null);
    }

    public Object navigation(Context context, NavigationCallback callback) {
        // 实际实现在ARouter中
        return ARouter.getInstance().navigation(context, this, -1, callback);
    }
}
```

接下来继续分析_ARouter：

``` java
final class _ARouter {
    /**
     * 执行路由流程，主要工作包括：预处理、完善路由信息、拦截、继续执行路由流程
     */
    protected Object navigation(final Context context, final Postcard postcard, final int requestCode, final NavigationCallback callback) {
        // 1.自定义预处理PretreatmentService；没有自定义预处理或者预处理完成后继续向下传递
        PretreatmentService pretreatmentService = ARouter.getInstance().navigation(PretreatmentService.class);
        if (null != pretreatmentService && !pretreatmentService.onPretreatment(context, postcard)) {
            return null;
        }
        // 设置Application Context
        postcard.setContext(null == context ? mContext : context);
        try {
            // 2.LogisticsCenter完善路由信息；详见3.3.6分析
            // 在我们的例子中postcard现在只有path和group信息,LogisticsCenter会完善要打开的Activity类、routeType等路由信息
            LogisticsCenter.completion(postcard);
        } catch (NoRouteFoundException ex) {
            // LogisticsCenter根据path和group信息完善路由信息时如果未找到，则回调onLost
            if (null != callback) {
                callback.onLost(postcard);
            } else {
                DegradeService degradeService = ARouter.getInstance().navigation(DegradeService.class);
                if (null != degradeService) {
                    degradeService.onLost(context, postcard);
                }
            }
            return null;
        }
        if (null != callback) {
            callback.onFound(postcard);
        }
        // 3.Postcard是否是绿色通道，是则继续执行_navigation；
        // 不是则执行interceptorService判断是否有拦截流程，本次暂不分析拦截流程；
        if (!postcard.isGreenChannel()) {   // It must be run in async thread, maybe interceptor cost too mush time made ANR.
            interceptorService.doInterceptions(postcard, new InterceptorCallback() {
                @Override
                public void onContinue(Postcard postcard) {
                    _navigation(postcard, requestCode, callback);
                }
                
                @Override
                public void onInterrupt(Throwable exception) {
                    if (null != callback) {
                        callback.onInterrupt(postcard);
                    }
                }
            });
        } else {
            // 4.继续执行_navigation流程
            return _navigation(postcard, requestCode, callback);
        }
        return null;
    }

    /**
     * 根据完善的Postcard,执行对应的路由逻辑
     */
    private Object _navigation(final Postcard postcard, final int requestCode, final NavigationCallback callback) {
        final Context currentContext = postcard.getContext();
        // 1.根据不同的routeType执行不同逻辑;我们的例子中routeType是ACTIVITY
        switch (postcard.getType()) {
            case ACTIVITY:
                // 2.从Postcard取出信息构造Intent;我们的例子中postcard.getDestination()是要打开的Activity类
                final Intent intent = new Intent(currentContext, postcard.getDestination());
                intent.putExtras(postcard.getExtras());
                // Set flags.
                int flags = postcard.getFlags();
                if (0 != flags) {
                    intent.setFlags(flags);
                }
                if (!(currentContext instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                String action = postcard.getAction();
                if (!TextUtils.isEmpty(action)) {
                    intent.setAction(action);
                }
                runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        // 3.启动Activity
                        startActivity(requestCode, currentContext, intent, postcard, callback);
                    }
                });
                break;
            case PROVIDER:
                return postcard.getProvider();
            case BOARDCAST:
            case CONTENT_PROVIDER:
            case FRAGMENT:
                Class<?> fragmentMeta = postcard.getDestination();
                try {
                    Object instance = fragmentMeta.getConstructor().newInstance();
                    if (instance instanceof Fragment) {
                        ((Fragment) instance).setArguments(postcard.getExtras());
                    } else if (instance instanceof android.support.v4.app.Fragment) {
                        ((android.support.v4.app.Fragment) instance).setArguments(postcard.getExtras());
                    }
                    return instance;
                } catch (Exception ex) {
                    logger.error(Consts.TAG, "Fetch fragment instance error, " + TextUtils.formatStackTrace(ex.getStackTrace()));
                }
            case METHOD:
            case SERVICE:
            default:
                return null;
        }
        return null;
    }
}
```

再看LogisticsCenter.completion()的主要工作：

``` java
public class LogisticsCenter {
    public synchronized static void completion(Postcard postcard) {
        // 1.从Warehouse.routes中查找Postcard的path所对应的RouteMeta
        RouteMeta routeMeta = Warehouse.routes.get(postcard.getPath());
        if (null == routeMeta) {
            // routeMet为空，则从groupsIndex查找；没查找到则不存在，查找到则动态添加
            if (!Warehouse.groupsIndex.containsKey(postcard.getGroup())) {
                throw new NoRouteFoundException(TAG + "There is no route match the path [" + postcard.getPath() + "], in group [" + postcard.getGroup() + "]");
            } else {
                // Load route and cache it into memory, then delete from metas.
                addRouteGroupDynamic(postcard.getGroup(), null);
                completion(postcard);   // Reload
            }
        } else {
            // 2.从Warehouse.routes中查找到Postcard所对应的RouteMeta后，完善Postcard信息
            postcard.setDestination(routeMeta.getDestination());
            postcard.setType(routeMeta.getType());
            postcard.setPriority(routeMeta.getPriority());
            postcard.setExtra(routeMeta.getExtra());
            Uri rawUri = postcard.getUri();
            if (null != rawUri) {   // Try to set params into bundle.
                Map<String, String> resultMap = TextUtils.splitQueryParameters(rawUri);
                Map<String, Integer> paramsType = routeMeta.getParamsType();
                if (MapUtils.isNotEmpty(paramsType)) {
                    // Set value by its type, just for params which annotation by @Param
                    for (Map.Entry<String, Integer> params : paramsType.entrySet()) {
                        setValue(postcard,
                                params.getValue(),
                                params.getKey(),
                                resultMap.get(params.getKey()));
                    }
                    // Save params name which need auto inject.
                    postcard.getExtras().putStringArray(ARouter.AUTO_INJECT, paramsType.keySet().toArray(new String[]{}));
                }
                // Save raw uri
                postcard.withString(ARouter.RAW_URI, rawUri.toString());
            }
            switch (routeMeta.getType()) {
                case PROVIDER:  // if the route is provider, should find its instance
                    // Its provider, so it must implement IProvider
                    Class<? extends IProvider> providerMeta = (Class<? extends IProvider>) routeMeta.getDestination();
                    IProvider instance = Warehouse.providers.get(providerMeta);
                    if (null == instance) { // There's no instance of this provider
                        IProvider provider;
                        try {
                            provider = providerMeta.getConstructor().newInstance();
                            provider.init(mContext);
                            Warehouse.providers.put(providerMeta, provider);
                            instance = provider;
                        } catch (Exception e) {
                            logger.error(TAG, "Init provider failed!", e);
                            throw new HandlerException("Init provider failed!");
                        }
                    }
                    postcard.setProvider(instance);
                    postcard.greenChannel();    // Provider should skip all of interceptors
                    break;
                case FRAGMENT:
                    postcard.greenChannel();    // Fragment needn't interceptors
                default:
                    break;
            }
        }
    }
}
```

LogisticsCenter是如何知道path="/login/LoginActivity"的Postcard在补全信息时，其对应的RouteType是Activity，对应的类是LoginActivity.class呢，在注解自动生成的代码，就可以看出来，APT处理过程中就会生成其对应信息，然后在LogisticsCenter.init()会将这些信息记录下来：

``` java
public class ARouter$$Group$$first implements IRouteGroup {
    @Override
    public void loadInto(Map<String, RouteMeta> atlas) {
        atlas.put("/login/LoginActivity", RouteMeta.build(RouteType.ACTIVITY, FirstActivity.class, 
                "/login/LoginActivity", "first", null, -1, -2147483648));
    }
}
```

最后总结下ARouter路由的调用过程

![config](https://obohe.com/i/2022/07/20/nin56i.png)













