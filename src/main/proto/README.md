# Protobuf 使用

## 运行命令 

生成Java类运行如下命令：

```shell
protoc --java_out=src/main/java -I=src/main/proto src/main/proto/Student.proto
```

| 部分 | 含义 | 
| --- | --- |
| `protoc` | protobuf编译器 | 
| `--java_out=src/main/java` | 生成Java源文件的根目录 | 
| `-I=src/main/proto` | import 搜索路径（proto 的 include path） | 
| `最后的Student.proto` | 要编译的文件 | 

其中：

1. `--java_out=src/main/java`
生成目录按 `option java_package = "com.demo.netty.protobuf"` 再拼路径，最终落到
src/main/java/com/demo/netty/protobuf/StudentPOJO.java，正好是 Maven 源码目录。

2. `-I=src/main/proto`
告诉 protoc：找 `.proto`、解析 `import` 时从这里搜。只有一个文件时也建议写上，路径更稳定；以后 Student.proto 再 import 别的 proto，会从 `-I` 指定的目录找。

3. 最后写完整路径 src/main/proto/Student.proto
**指定本次编译哪个文件。** 