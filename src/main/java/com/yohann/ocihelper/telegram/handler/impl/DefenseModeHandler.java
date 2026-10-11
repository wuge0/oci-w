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

/**
 * Defense Mode Management Handler
 * Handles defense mode toggle (block all IPs when enabled)
 * 
 * @author Wuge
 */
@Slf4j
@Component
public class DefenseModeHandler extends AbstractCallbackHandler {
    
    @Override
    public BotApiMethod<? extends Serializable> handle(CallbackQuery callbackQuery, TelegramClient telegramClient) {
        IpSecurityService ipSecurityService = SpringUtil.getBean(IpSecurityService.class);
        
        try {
            boolean isEnabled = ipSecurityService.isDefenseModeEnabled();
            
            StringBuilder text = new StringBuilder();
            text.append("🛡️ *防御模式管理*\n\n");
            text.append("📌 *当前状态：*\n");
            
            if (isEnabled) {
                text.append("🔴 *已启用* - 所有IP访问已被阻止\n\n");
                text.append("⚠️ *警告：*\n");
                text.append("• 防御模式已开启\n");
                text.append("• 所有外部IP都无法访问系统\n");
                text.append("• 包括Web界面和API接口\n");
                text.append("• 仅Telegram Bot可以管理\n\n");
                text.append("💡 *适用场景：*\n");
                text.append("• 遭受攻击时紧急防护\n");
                text.append("• 系统维护期间\n");
                text.append("• 需要完全隔离系统访问\n\n");
                text.append("🔧 *操作建议：*\n");
                text.append("• 确认系统安全后可关闭防御模式\n");
                text.append("• 或使用IP黑名单精确控制\n");
            } else {
                text.append("🟢 *已禁用* - 系统正常访问\n\n");
                text.append("✅ *当前状态：*\n");
                text.append("• 系统可以正常访问\n");
                text.append("• IP黑名单仍然生效\n");
                text.append("• 未被禁止的IP可以访问\n\n");
                text.append("💡 *功能说明：*\n");
                text.append("防御模式是终极防护手段，启用后：\n");
                text.append("• 立即阻止所有IP访问\n");
                text.append("• 优先级高于IP黑名单\n");
                text.append("• 可通过Telegram随时开关\n\n");
                text.append("⚠️ *使用建议：*\n");
                text.append("• 仅在紧急情况下使用\n");
                text.append("• 日常使用IP黑名单即可\n");
                text.append("• 启用前请确保Telegram可控\n");
            }
            
            // Add timestamp to avoid "message not modified" error
            text.append("\n🕑 更新时间: ");
            text.append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            
            List<InlineKeyboardRow> keyboard = new ArrayList<>();
            
            if (isEnabled) {
                keyboard.add(new InlineKeyboardRow(
                    KeyboardBuilder.button("🟢 关闭防御模式", "defense_mode_disable_confirm"),
                    KeyboardBuilder.button("🔄 刷新状态", "defense_mode")
                ));
            } else {
                keyboard.add(new InlineKeyboardRow(
                    KeyboardBuilder.button("🔴 启用防御模式", "defense_mode_enable_confirm"),
                    KeyboardBuilder.button("🔄 刷新状态", "defense_mode")
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
            log.error("Failed to get defense mode info", e);
            return buildErrorMessage(callbackQuery, e.getMessage());
        }
    }
    
    @Override
    public String getCallbackPattern() {
        return "defense_mode";
    }
    
    @Override
    public boolean canHandle(String callbackData) {
        // Exact match to avoid conflicts with defense_mode_enable, defense_mode_disable, etc.
        return "defense_mode".equals(callbackData);
    }
    
    /**
     * Build error message
     */
    private BotApiMethod<? extends Serializable> buildErrorMessage(CallbackQuery callbackQuery, String errorMsg) {
        String text = String.format(
            "❌ *获取防御模式信息失败*\n\n" +
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
