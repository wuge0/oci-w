package com.yohann.ocihelper.bean.params.sys;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params.sys
 * @className: GoogleLoginParams
 * @author: Wuge
 * @date: 2026/01/02
 */
@Data
public class GoogleLoginParams {

    @NotBlank(message = "Google credential不能为空")
    private String credential;
}
