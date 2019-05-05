# Thrift Interfaces Description Parser

### 使用方法
* 编译源码
    ```
    mvn clean package
    ```
* 解析jar
    ```
    java -jar TIDP.jar /xxx/xxx.jar
    ```
    注：“/xxx/xxx.jar”为IDL编译后jar文件
* 解析classpath
    ```
    java -jar TIDP.jar /xxx/xxx/
    ```
    注：“/xxx/xxx/”为IDL编译后class文件包路径