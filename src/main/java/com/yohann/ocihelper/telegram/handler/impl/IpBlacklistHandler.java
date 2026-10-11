package com.yohann.ocihelper.telegram.handler.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.yohann.ocihelper.service.IpSecurityService;
import com.yohann.ocihelper.telegram.builder.KeyboardBuilder;
import com.yohann.ocihelper.telegram.handler.AbstractCallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IP Blacklist Management Handler
 * Handles IP blacklist operations (add, remove, query, clear)
 * 
 * @author Wuge
 */
@Slf4j
@Component
public class IpBlacklistHandler extends AbstractCallbackHandler {
    
    @Override
    public BotApiMethod<? extends Serializable> handle(CallbackQuery callbackQuery, TelegramClient telegramClient) {
        IpSecurityService ipSecurityService = SpringUtil.getBean(IpSecurityService.class);
        
        try {
            // Get statistics
            Map<String, Object> stats = ipSecurityService.getStatistics();
            List<String> blacklistEntries = ipSecurityService.getAllBlacklistEntries();
            
            StringBuilder text = new StringBuilder();
            text.append("🚫 *IP黑名单管理*\n\n");
            text.append("📊 *当前状态：*\n");
            text.append("• 总条目：").append(stats.get("totalEntries")).append(" 个\n");
            text.append("• 单个IP：").append(stats.get("ipCount")).append(" 个\n");
            text.append("• IP段：").append(stats.get("rangeCount")).append(" 个\n\n");
            
            text.append("🛡️ *登录保护：*\n");
            text.append("• 失败阈值：").append(stats.get("maxLoginFailures")).append(" 次\n");
            text.append("• 跟踪窗口：").append(stats.get("failureTrackingWindowMinutes")).append(" 分钟\n");
            text.append("• 达到阈值后自动拉黑IP\n\n");
            
            if (blacklistEntries.isEmpty()) {
                text.append("📋 *黑名单列表：*\n");
                text.append("_暂无黑名单条目_\n\n");
            } else {
                text.append("📋 *黑名单列表：*\n");
                int count = 0;
                for (String entry : blacklistEntries) {
                    if (count >= 20) {
                        text.append("_... 及其他 ").append(blacklistEntries.size() - 20).append(" 个条目_\n");
                        break;
                    }
                    text.append("• `").append(entry).append("`\n");
                    count++;
                }
                text.append("\n");
            }
            
            text.append("💡 *功能说明：*\n");
            text.append("• *添加IP*：添加单个IP到黑名单\n");
            text.append("• *添加IP段*：添加CIDR格式的IP段\n");
            text.append("• *删除IP*：从黑名单中移除指定IP\n");
            text.append("• *清空列表*：清空所有黑名单条目\n\n");
            
            text.append("⚠️ *注意事项：*\n");
            text.append("• IP格式：192.168.1.100\n");
            text.append("• IP段格式：192.168.1.0/24\n");
            text.append("• 黑名单中的IP无法访问系统\n\n");
            
            // Add timestamp to avoid "message not modified" error
            text.append("🕑 更新时间: ");
            text.append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            text.append("\n");
            
            List<InlineKeyboardRow> keyboard = new ArrayList<>();
            
            keyboard.add(new InlineKeyboardRow(
                KeyboardBuilder.button("➕ 添加IP", "ip_blacklist_add"),
                KeyboardBuilder.button("➕ 添加IP段", "ip_blacklist_add_range")
            ));
            
            keyboard.add(new InlineKeyboardRow(
                KeyboardBuilder.button("➖ 删除IP", "ip_blacklist_remove"),
                KeyboardBuilder.button("🔄 查询列表", "ip_blacklist")
            ));
            
            if (!blacklistEntries.isEmpty()) {
                keyboard.add(new InlineKeyboardRow(
                    KeyboardBuilder.button("🗑️ 清空列表", "ip_blacklist_clear_confirm")
                ));
            }
            
            keyboard.add(KeyboardBuilder.buildBackToMainMenuRow());
            keyboard.add(KeyboardBuilder.buildCancelRow());
            
            return buildEditMessage(
                callbackQuery,
                text.toString(),
                new InlineKeyboardMarkup(keyboard)
            );
            
        } catch (Exception e) {
            log.error("Failed to get IP blacklist info", e);
            return buildErrorMessage(callbackQuery, e.getMessage());
        }
    }
    
    @Override
    public String getCallbackPattern() {
        return "ip_blacklist";
    }
    
    @Override
    public boolean canHandle(String callbackData) {
        // Exact match to avoid conflicts with ip_blacklist_add, ip_blacklist_remove, etc.
        return "ip_blacklist".equals(callbackData);
    }
    
    /**
     * Build error message
     */
    private BotApiMethod<? extends Serializable> buildErrorMessage(CallbackQuery callbackQuery, String errorMsg) {
        String text = String.format(
            "❌ *获取黑名单信息失败*\n\n" +
            "错误信息：%s\n\n" +
            "请稍后重试或联系管理员。",
            errorMsg
        );
        
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(KeyboardBuilder.buildBackToMainMenuRow());
        keyboard.add(KeyboardBuilder.buildCancelRow());
        
        return buildEditMessage(
            callbackQuery,
            text,
            new InlineKeyboardMarkup(keyboard)
        );
    }
}
