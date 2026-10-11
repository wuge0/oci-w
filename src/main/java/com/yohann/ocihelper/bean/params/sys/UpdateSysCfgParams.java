package com.yohann.ocihelper.bean.params.sys;

import lombok.Data;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params.sys
 * @className: UpdateLoginCfgParams
 * @author: Wuge
 * @date: 2024/11/30 18:22
 */
@Data
public class UpdateSysCfgParams {

    private String dingToken;
    private String dingSecret;
    private String tgChatId;
    private String tgBotToken;
    private Boolean enableMfa;

    private Boolean enableDailyBroadcast;
    private String dailyBroadcastCron;
    private Boolean enableVersionInform;

    private String gjAiApi;
    private String bootBroadcastToken;
    private Boolean enableGoogleLogin;
    private String googleClientId;
    private String allowedEmails;
    /** 全局代理地址，例如 http://host:port 或 socks5://host:port */
    private String proxy;
}
