# oci-w 🐢

[![OpenJDK](https://img.shields.io/badge/OpenJDK-21+-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21)
[![Springboot](https://img.shields.io/badge/Springboot-3+-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-2+-green?logo=vuedotjs)](https://vuejs.org)
[![SQLite](https://img.shields.io/badge/SQLite-3+-blue?logo=sqlite)](https://sqlite.org)
[![Maven](https://img.shields.io/badge/Maven-3+-red?logo=apachemaven)](https://maven.apache.org)
[![Docker](https://img.shields.io/badge/Docker-28+-blue?logo=Docker)](https://www.docker.com)

> 行到水穷处，坐看云起时。
>
> —— 王维《终南别业》

oci-w（Wuge）是一个基于 Spring Boot 3 + JDK 21 开发的轻量级 OCI 管理面板，后端采用 SQLite 存储，可容器化运行，也可直接用 JAR 运行。项目面向 Oracle Cloud（OCI）的日常运维与自动化场景，提供实例管理、网络与 IP 操作、任务持久化、日志实时查看、多租户管理等功能。

---

## 📌 设计取向

> 云无心以出岫，鸟倦飞而知还。
>
> —— 陶渊明《归去来兮辞》

这个发行版把「**自持**」放在第一位：不依赖任何外部服务——从源码构建到运行镜像的整条链路都能在本地闭环完成，不需要访问集中式的镜像仓库或数据端点。

| 取向 | 说明 |
|---|---|
| **完全自持** | 不含任何联网请求外部服务的逻辑；离线环境下也能完成构建与部署 |
| **可复现构建** | 附带 `Dockerfile`、`.dockerignore` 与本地 `docker-compose.yml`，镜像由源码构建而非外部拉取 |
| **升级可控** | 面板与 TG 中的更新入口收敛为显式提示，升级一律通过重新构建源码完成 |
| **不收集数据** | 无任何形式的数据回传，相关配置项默认留空 |
| **依赖新鲜** | OCI Java SDK、Spring Boot、Spring AI、MyBatis-Plus 等 17 项主要依赖保持在当前版本 |
| **发布物干净** | 不含第三方推广位与个人署名，仅保留第三方开源库自身的许可声明 |

---

## 🔔 注意事项及免责声明

- ⚠️ 建议在局域网下使用，不要暴露到公网。
- ⚠️ 因开机、换 IP 频率过高而导致的封号，本版本不承担任何责任。
- 🔒 强烈建议不要裸 HTTP 访问，应使用 Nginx 反向代理配置 HTTPS 访问。
- 🔐 建议使用密钥登录服务器，防止服务器被 SSH 爆破导致 API 数据及密钥泄露。
- 📃 记得定时清理日志文件。

---

## ⚙ 核心功能

1. 支持同时**批量添加**多个租户配置信息，所有分页列表都支持**模糊搜索**、**状态筛选**。
2. 支持更改实例配置、引导卷配置、一键附加 IPv6、一键放行所有端口、实例列表、引导卷列表、**一键开启免费 AMD 实例下行 500Mbps**、**一键自动救援 / 缩小硬盘（默认 47GB）**、**安全列表**、**实时流量统计**（分钟级别）、配额查询、租户管理。⭐⭐
   > 注：「一键自动更新」在此版本中已收敛为提示，升级请通过重新构建源码完成。
3. 根据多个 **CIDR 网段**更换实例公共 IP，遇到请求频繁等异常会直接忽略，不影响下一次执行，直至更换到指定 IP 段的 IP。支持**更换 IP 后自动更新 Cloudflare DNS** 记录。⭐⭐
4. 多租户**同时批量开机**，后台一直运行，直至开机成功。⭐
5. 支持**断点续抢**，配置以及抢机任务都保存在本地数据库，服务重启会继续执行抢机任务，无需重复配置。⭐⭐
6. 支持多区号（配置项以 region 区分）。
7. 支持前端页面**实时查看后端日志**。
8. 支持**加密备份恢复**，实现无缝数据迁移。⭐⭐
9. 支持 **MFA** 登录验证功能（面板自身的登录保护，与 Oracle 无关）。
10. 支持分页查询、添加、更新、删除 **Cloudflare** 多个域名的 DNS 记录。⭐
11. 支持 **Telegram 机器人**简单操作，服务部署后向机器人发送 `/start` 选择操作。
12. 支持类似甲骨文云 **Cloud Shell** 的控制台功能，方便使用 netboot 救砖。⭐⭐
13. 支持 IP 信息查询（部署成功后访问 `http://ip:8818/ip-info`）以及首页全球服务器地图。
14. 支持 AI 聊天助手，基于 [硅基流动](https://cloud.siliconflow.cn) 的免费 API，需自行申请密钥。
15. 支持 Google 账号一键登录，配置方法见 [如何配置 Google 账号一键登录](./doc/GOOGLE_LOGIN_GUIDE.md) ⭐

---

## 🏗 本地构建

前置条件：**JDK 21+**、**Maven 3.6+**。

```bash
# 在仓库根目录执行
mvn -B -DskipTests clean package

# 产物
ls -lh target/oci-w-3.5.1.jar
```

> 首次构建需联网从 Maven 中央仓库下载依赖；依赖下载完成后可离线构建。
> 若你的 Maven 配置了境内镜像且缺少个别构件，构建会自动回落到中央仓库，无需额外处理。

---

## 💻 部署方式一：Docker（推荐）

**不依赖任何外部镜像仓库**，直接用本仓库源码在本地构建镜像。

### 1. 准备运行文件

```bash
cd <仓库目录>

# 私钥目录（空目录即可，之后把 OCI 的 pem 放进去）
mkdir -p keys logs

# 配置文件（首次从模板复制）
cp src/main/resources/application.yml ./application.yml

# 初始数据库
ls oci-w.db || echo "仓库内已附带，无需额外下载"
```

### 2. 修改配置（**必做**）

编辑 `./application.yml`，**务必改掉默认账号密码**：

```yaml
web:
  account: admin                    # 改成你自己的
  password: CHANGE_ME_BEFORE_START  # 务必修改
```

改完收紧权限：

```bash
chmod 600 application.yml
```

### 3. 构建并启动

```bash
docker compose up -d --build     # 首次构建会编译源码，耗时数分钟
docker compose ps                # 查看状态
docker compose logs -f oci-w     # 跟踪日志
```

启动后浏览器访问 `http://<服务器IP>:8818`。

### 4. 更新

```bash
git pull                          # 或手动替换源码
docker compose up -d --build      # 重新构建并滚动重启
```

> 该操作不会删除已挂载的 `application.yml`、`oci-w.db` 与 `keys/`。

### 关于 VNC / Cloud Shell 救砖

`docker-compose.yml` 中的 `websockify` 服务为 VNC 救砖通道提供支持。该组件以**预构建镜像**形式单独分发（未随本仓库提供源码），使用前先导入：

```bash
docker load -i oci-w-websockify.tar.gz
```

导入后执行 `docker compose up -d` 即可。**不需要 VNC 救砖功能时，可将 `docker-compose.yml` 中该服务整段注释掉**，不影响面板主体功能。

---

## 🖥 部署方式二：纯 JAR + systemd

不装 Docker 时使用。前置条件：**JDK 21+**。

### 1. 构建并安装

```bash
sudo bash deploy/install-local.sh jar
```

脚本会自动完成：构建 JAR → 安装到 `/opt/oci-w` → 生成配置与数据库 → 注册 systemd 服务并启动。

可通过环境变量自定义安装目录：

```bash
sudo INSTALL_DIR=/app/oci-w bash deploy/install-local.sh jar
```

### 2. 手动方式（等价操作）

```bash
mvn -B -DskipTests clean package
sudo mkdir -p /opt/oci-w/{keys,logs}
sudo cp target/oci-w-3.5.1.jar /opt/oci-w/oci-w.jar
sudo cp src/main/resources/application.yml /opt/oci-w/
sudo cp oci-w.db /opt/oci-w/
sudo chmod 600 /opt/oci-w/application.yml

sudo cp deploy/oci-w.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now oci-w
sudo systemctl status oci-w
```

### 3. 常用命令

```bash
systemctl status oci-w      # 状态
systemctl restart oci-w     # 重启
journalctl -u oci-w -f      # 日志（或看 /var/log/oci-w.log）
```

---

## 🌏 Nginx 反向代理（配置 HTTPS）

> 需要配置 HTTPS 又要保留 VNC 连接救砖时，按本节操作。

完整的 Nginx 配置模板见 **[`deploy/nginx.conf.example`](./deploy/nginx.conf.example)**，其中已包含面板主站与 VNC 通道两段配置。

要点：

1. 配置好 Nginx 后，把 `docker-compose.yml` 中 `oci-w` 的端口绑定改为 `"127.0.0.1:8818:8818"`，使面板只能由本机反代访问，避免用 IP+端口绕过 HTTPS。
2. 防火墙只放行 **80/443**，8818 与 6080 都不需要对公网开放。
3. 别忘记放行 Oracle 云侧安全列表中的 80/443（这是云侧第一层防火墙）。
4. WebSocket 的两个 `Upgrade` / `Connection` 头缺一不可，否则实时日志、WebSSH 与 VNC 都无法连接。

---

## 🔧 常用维护命令

```bash
# Docker 方式
docker compose logs -f oci-w                  # 实时日志
docker compose restart oci-w                  # 重启
docker compose down                           # 停止

# JAR 方式
systemctl status oci-w
tail -f /var/log/oci-w.log

# 查看初始账号密码（首次部署后请立即修改）
grep -A2 '^web:' application.yml
```

**备份**：面板内置加密备份恢复功能。另建议单独定期备份 `oci-w.db`（含租户配置与抢机任务）与 `keys/`（OCI 私钥），这两个文件是真正的核心资产。

---

## 🔍 页面展示

> 主页

![主页](./img/1-home.png)

> 新增配置

![新增配置](./img/3-add-1.png)

> 更换 IP 并自动更新 Cloudflare 域名 DNS 记录

![更换IP](./img/3-instance-details.png)

> 编辑实例

![编辑实例](./img/3-instance-cfg.png)

> TG 操作

![TG](./img/9-tg-bot.jpg)

<details>
    <summary> ☜ 点击查看更多 👨‍💻</summary>

![登录](./img/0-login.png)
![主页](./img/1-home.png)
![用户](./img/1-user.png)
![租户](./img/1-tenantInfo.png)
![新增配置1](./img/3-add-1.png)
![新增配置2](./img/3-add-2.png)
![创建实例](./img/3-create.png)
![实例详情](./img/3-instance-details.png)
![VNC](./img/3-instance-vnc.png)
![实例配置](./img/3-instance-cfg.png)
![安全列表](./img/3-security-rule.png)
![流量统计](./img/3-traffic.png)
![任务](./img/4-task.png)
![日志](./img/5-log.png)
![基础配置](./img/6-basic-cfg.png)
![备份](./img/7-backup.png)
![Cloudflare](./img/7-cf.png)
![通知](./img/8-inform.png)
![TG](./img/9-tg-bot.jpg)
![部署脚本](./img/10-shell.png)
![资源占用](./img/10-status.png)

</details>

---

## 📁 目录结构

```bash
.
├── src/                        # 后端源码（Java）
├── deploy/                     # 部署文件
│   ├── install-local.sh        #   一键安装脚本
│   ├── oci-w.service           #   systemd 单元
│   └── nginx.conf.example      #   Nginx 反向代理模板
├── docker-compose.yml          # Docker 编排（本地构建镜像）
├── Dockerfile                  # 多阶段构建：Maven 编译 → JRE 运行
├── .dockerignore               # 构建上下文排除项
├── oci-w.db                    # 初始数据库
├── keys/                       # OCI API 私钥目录（自行创建）
├── img/                        # 文档截图
└── pom.xml
```

---

<div align="center">

**不畏浮云遮望眼，自缘身在最高层。**

—— 王安石《登飞来峰》

</div>
