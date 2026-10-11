#!/usr/bin/env bash
# ============================================================================
#  oci-w 本地化安装 / 部署脚本
# ----------------------------------------------------------------------------
#  脚本说明：
#    全程不访问任何外部服务，只使用当前仓库内的源码与本地构建产物完成
#    部署，适合在离线或受控环境下使用。
#
#  用法：
#    sudo bash deploy/install-local.sh docker   # Docker 本地构建并启动（推荐）
#    sudo bash deploy/install-local.sh jar      # 纯 JAR + systemd
#    sudo bash deploy/install-local.sh build    # 仅构建 JAR（不部署）
#    sudo bash deploy/install-local.sh status   # 查看当前运行状态
#    sudo bash deploy/install-local.sh stop     # 停止服务
# ============================================================================

set -euo pipefail

REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
INSTALL_DIR="${INSTALL_DIR:-/opt/oci-w}"
JAR_VERSION="3.5.1"
JAR_FILE="oci-w-${JAR_VERSION}.jar"

log()  { printf '\033[1;32m[oci-w]\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[oci-w]\033[0m %s\n' "$*"; }
die()  { printf '\033[1;31m[oci-w]\033[0m %s\n' "$*" >&2; exit 1; }

need_root() { [ "$(id -u)" -eq 0 ] || die "请用 root 执行（或加 sudo）"; }

check_java() {
    command -v java >/dev/null 2>&1 || die "未找到 java，请先安装 JDK 21+"
    local v
    v=$(java -version 2>&1 | head -1 | grep -oP '"\K[0-9]+' || echo 0)
    [ "$v" -ge 21 ] || die "需要 JDK 21 或更高版本，当前为 $v"
    log "JDK 检测通过：$(java -version 2>&1 | head -1)"
}

check_maven() {
    command -v mvn >/dev/null 2>&1 || die "未找到 mvn，请先安装 Maven 3.6+"
    log "Maven 检测通过：$(mvn -v 2>/dev/null | head -1)"
}

build_jar() {
    check_java; check_maven
    log "开始构建（首次构建需联网下载 Maven 依赖，之后可离线）…"
    ( cd "$REPO_DIR" && mvn -B -DskipTests clean package )
    [ -f "$REPO_DIR/target/$JAR_FILE" ] || die "构建结束但未找到 target/$JAR_FILE"
    log "构建完成：$REPO_DIR/target/$JAR_FILE"
}

# 部署前确保三个必要文件就位，缺则从仓库内补齐
prepare_runtime_files() {
    local dir="$1"
    mkdir -p "$dir/keys" "$dir/logs"

    if [ ! -f "$dir/application.yml" ]; then
        cp "$REPO_DIR/src/main/resources/application.yml" "$dir/application.yml"
        warn "已生成 $dir/application.yml —— 请务必修改 web.account / web.password！"
    fi
    if [ ! -f "$dir/oci-w.db" ]; then
        cp "$REPO_DIR/oci-w.db" "$dir/oci-w.db"
        log "已放置初始数据库 oci-w.db"
    fi
    chmod 600 "$dir/application.yml" || true
}

deploy_jar() {
    need_root; check_java
    build_jar
    log "安装到 $INSTALL_DIR …"
    mkdir -p "$INSTALL_DIR"
    cp "$REPO_DIR/target/$JAR_FILE" "$INSTALL_DIR/oci-w.jar"
    prepare_runtime_files "$INSTALL_DIR"

    # 修正 systemd 单元里的路径（若使用自定义 INSTALL_DIR）
    sed "s|/opt/oci-w|$INSTALL_DIR|g" "$REPO_DIR/deploy/oci-w.service" \
        > /etc/systemd/system/oci-w.service
    systemctl daemon-reload
    systemctl enable oci-w >/dev/null 2>&1 || true
    systemctl restart oci-w
    sleep 3
    systemctl --no-pager --lines=5 status oci-w || true
    log "部署完成。访问 http://<服务器IP>:8818"
    warn "确认 application.yml 中的账号密码已修改，并已做好防火墙放行。"
}

deploy_docker() {
    need_root
    command -v docker >/dev/null 2>&1 || die "未找到 docker，请先安装 Docker"
    docker compose version >/dev/null 2>&1 || die "未找到 docker compose 插件"
    prepare_runtime_files "$REPO_DIR"
    log "本地构建镜像并启动（首次构建会编译源码，耗时较长）…"
    ( cd "$REPO_DIR" && docker compose up -d --build )
    log "部署完成。"
    ( cd "$REPO_DIR" && docker compose ps )
    warn "若日志显示 websockify 启动失败，说明未导入存档镜像；"
    warn "执行 docker load -i oci-w-websockify.tar.gz 即可，"
    warn "或直接在 docker-compose.yml 中注释掉该服务（不影响面板主体功能）。"
}

show_status() {
    if systemctl list-unit-files 2>/dev/null | grep -q '^oci-w.service'; then
        systemctl --no-pager --lines=10 status oci-w || true
    elif command -v docker >/dev/null 2>&1; then
        ( cd "$REPO_DIR" && docker compose ps ) || true
    else
        echo "未检测到已安装的 oci-w 服务"
    fi
}

do_stop() {
    need_root
    systemctl stop oci-w 2>/dev/null && log "systemd 服务已停止" || true
    if command -v docker >/dev/null 2>&1; then
        ( cd "$REPO_DIR" && docker compose down ) 2>/dev/null && log "容器已停止" || true
    fi
}

case "${1:-}" in
    docker) deploy_docker ;;
    jar)    deploy_jar ;;
    build)  build_jar ;;
    status) show_status ;;
    stop)   do_stop ;;
    *)
        cat <<'EOF'
oci-w 本地化安装脚本

用法: sudo bash deploy/install-local.sh <动作>

  docker   用本地源码构建 Docker 镜像并启动（推荐）
  jar      构建 JAR 并用 systemd 托管运行
  build    只构建 JAR，不部署
  status   查看运行状态
  stop     停止服务

环境变量:
  INSTALL_DIR   仅 jar 模式生效，默认 /opt/oci-w
EOF
        exit 1
        ;;
esac
