FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests \
    && cp target/oci-w-*.jar /app/oci-w.jar

FROM eclipse-temurin:21-jre-jammy AS base-with-tools

ENV LANG=zh_CN.UTF-8 \
    LC_ALL=zh_CN.UTF-8 \
    TZ=Asia/Shanghai

RUN apt update && \
    apt install -y --no-install-recommends openssh-client lsof curl locales && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p /root/.ssh && \
    echo "Host *\n  HostKeyAlgorithms +ssh-rsa\n  PubkeyAcceptedKeyTypes +ssh-rsa" > /root/.ssh/config && \
    chmod 700 /root/.ssh && chmod 600 /root/.ssh/config && \
    locale-gen zh_CN.UTF-8 && \
    ln -fs /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

FROM base-with-tools

ENV OCI_W_VERSION=3.5.1

WORKDIR /app/oci-w

COPY --from=builder /app/oci-w.jar .

EXPOSE 8818

# 以 JSON 形式声明 CMD —— shell 形式会让构建器给出 JSONArgsRecommended 警告，
# 且信号语义不明确。日志仍同时写入 /var/log/oci-w.log 便于挂载持久化。
# 若希望 java 直接作为 PID 1、获得最干净的停机信号语义，可去掉 tee 改为：
#   CMD ["java","--add-opens","java.base/java.net=ALL-UNNAMED", \
#        "--add-opens","java.base/sun.net.www.protocol.https=ALL-UNNAMED","-jar","oci-w.jar"]
# （此时日志只走 stdout，用 docker logs 查看）
CMD ["sh","-c","exec java --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED -jar oci-w.jar | tee -a /var/log/oci-w.log"]
