package com.yohann.ocihelper.bean.params.sys;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params.sys
 * @className: SendMsgParams
 * @author: Wuge
 * @date: 2024/11/30 18:32
 */
@Data
public class SendMsgParams {

    @NotBlank(message = "消息不能为空")
    private String message;
}
