# [Springboot整合ehcache缓存](https://www.cnblogs.com/xzmiyx/p/9897623.html)

EhCache是一个比较成熟的Java缓存框架，最早从hibernate发展而来， 是进程中的缓存系统，它提供了用内存，磁盘文件存储，以及分布式存储方式等多种灵活的cache管理方案，快速简单。

Springboot对ehcache的使用非常支持，所以在Springboot中只需做些配置就可使用，且使用方式也简易。

在你的项目上配置以下几步即可使用

首先，老规矩,pom.xml加依赖;

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
      <groupId>net.sf.ehcache</groupId>
      <artifactId>ehcache</artifactId>
      <version>2.10.4</version>
</dependency>
```

第二步，创建ehcache.xml配置文件

位置：classpath目录下，即src/main/resources/ehcache.xml

文件内容开发的时候可参考第一步导入的jar包，具体在哪呢，看下面：

![img](https://img2018.cnblogs.com/blog/1526090/201811/1526090-20181102164413745-434123704.png)![img](https://img2018.cnblogs.com/blog/1526090/201811/1526090-20181102164445394-1204884129.png)

再看代码：

```xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../config/ehcache.xsd">
 
    <diskStore path="java.io.tmpdir"/>
 
  <!--defaultCache:echcache的默认缓存策略  -->
    <defaultCache
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            maxElementsOnDisk="10000000"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </defaultCache>
    <cache name="users"
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            maxElementsOnDisk="10000000"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </cache>
</ehcache>
```

　说明：

```
maxElementsInMemory      内存缓存中最多可以存放的元素数量,若放入Cache中的元素超过这个数值,则有以下两种情况  
                         1)若overflowToDisk=true,则会将Cache中多出的元素放入磁盘文件中  
                         2)若overflowToDisk=false,则根据memoryStoreEvictionPolicy策略替换Cache中原有的元素
```

```
overflowToDisk           内存不足时,是否启用磁盘缓存
已废弃，persistence来代替，如：<persistence strategy=”localTempSwap”/>
```

```
diskExpiryThreadIntervalSeconds   磁盘缓存的清理线程运行间隔,默认是120秒
```

```
memoryStoreEvictionPolicy   内存存储与释放策略,即达到maxElementsInMemory限制时,Ehcache会根据指定策略清理内存  共有三种策略,分别为LRU(最近最少使用)、LFU(最常用的)、FIFO(先进先出)

另外，defaultCache是默认缓存方式，cache是自定义的缓存方式，自行设置name


第三步，在Springboot配置文件中把ehcache.xml配置进去；即在application.properties中加入以下配置代码(其实不加也行)spring.cache.ehcache.config=ehcache.xml
```

　

第三步结束，ehcache在Springboot中就配置完成了，下面就是怎么在Springboot中使用



第四步，在启动类前加上@EnableCaching注解；这样的话，启动类启动时会去启动缓存启动器。

```java
@SpringBootApplication
@MapperScan("com.dsx.mapper")
@EnableCaching
public class app {
 
    public static void main(String[] args) {
        SpringApplication.run(app.class, args);
 
    }
 
}
```

  第五步，实体类实现可序列化接口Serializable；由于需要实体类支持缓存中的磁盘存储，所以需要实体类实现可序列化接口

　　

```java
public class user implements Serializable{
    private Integer id;
    private String name;
    private Integer age;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
     
}
```

第六步，使用@Cacheable把数据存进缓存，下面就是专门把方法返回值存入缓存

```java
@Service
@Transactional     //事务，表示该类下所有的都受事务控制
public class userServiceImpl implements userService {
    @Autowired
    private userMapper usermapper;
    @Override
    @Cacheable(value="users")
    public user selectUserById(int id) {
        user user=this.usermapper.selectUserById(id);
        System.out.println("1111111111111111111111111");
        return user;
    }
 
}
```

　说明：　@Cacheable可以标记在一个方法上，也可以标记在一个类上，当标记在一个方法上时表示该方法是支持缓存的，当标记在一个类上时则表示该类所有的方法都是支持缓存的。对于一个支持缓存的方法，Spring会在其被调用后将其返回值缓存起来，以保证下次利用同样的参数来执行该方法时可以直接从缓存中获取结果，而不需要再次执行该方法。Spring在缓存方法的返回值时是以键值对进行缓存的，值就是方法的返回结果。

​                 @Cacheable可以指定三个属性，value、key和condition。

​                                  value属性指定cache的名称（即选择ehcache.xml中哪种缓存方式存储）

​                                  key属性是用来指定Spring缓存方法的返回结果时对应的key的。该属性支持SpringEL表达式。当我们没有指定该属性时，Spring将使用默认策略生成key。我们也直接使用“#参数名”或者“#p参数index”。下面是几个使用参数作为key的示例

 

```java
@Cacheable(value="users", key="#id")
 
public User find(Integer id) {
 
   retur null;
 
}
 
 
 
@Cacheable(value="users", key="#p0")
 
public User find(Integer id) {
 
   retur null;
 
}
 
 
 
@Cacheable(value="users", key="#user.id")
 
public User find(User user) {
 
   retur null;
 
}
 
 
 
@Cacheable(value="users", key="#p0.id")
 
public User find(User user) {
 
   retur null;
 
}
```

   最后，使用@CacheEvict清除缓存；

```java
@CacheEvict(value="users",allEntries=true)
public void saveUsers(Users users) {
this.usersRepository.save(users);
}
```

　　说明：@CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作。@CacheEvict可以指定的属性有value、key、condition、allEntries和beforeInvocation。

​                    其中value、key和condition的语义与@Cacheable对应的属性类似；allEntries是boolean类型，表示是否需要清除缓存中的所有元素。默认为false，表示不需要。

​                   当指定了allEntries为true时，Spring Cache将忽略指定的key。有的时候我们需要Cache一下清除所有的元素，这比一个一个清除元素更有效率。