package com.yohann.ocihelper.telegram.handler.impl;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.List;

import static java.lang.Math.toIntExact;

/**
 * 版本信息回调处理器
 * 
 * @author Wuge
 */
@Slf4j
@Component
public class VersionInfoHandler extends VersionInfoBaseHandler {
    
    @Override
    public BotApiMethod<? extends Serializable> handle(CallbackQuery callbackQuery, TelegramClient telegramClient) {
        try {
            return getVersionInfo(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId(),
                    telegramClient
            );
        } catch (Exception e) {
            log.error("Handle version info error", e);
            return buildEditMessage(callbackQuery, "获取版本信息失败");
        }
    }
    
    @Override
    public String getCallbackPattern() {
        return "version_info";
    }
}

/**
 * 更新系统版本回调处理器
 * 
 * @author Wuge
 */
@Slf4j
@Component
class UpdateSysVersionHandler extends VersionInfoBaseHandler {
    
    @Override
    public BotApiMethod<? extends Serializable> handle(CallbackQuery callbackQuery, TelegramClient telegramClient) {
        long chatId = callbackQuery.getMessage().getChatId();
        
        // 更新入口已收敛为显式提示。
        log.warn("Blocked auto-update request from chat {}", chatId);
        return SendMessage.builder()
                .chatId(chatId)
                .text("⚠️ 一键更新已停用\n\n请重新构建本仓库源码后替换 JAR 完成升级。")
                .build();
    }
    
    @Override
    public String getCallbackPattern() {
        return "update_sys_version";
    }
}
