package com.yohann.ocihelper.bean.params.oci.instance;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params
 * @className: UpdateInstanceStateParams
 * @author: Wuge
 * @date: 2024/11/28 21:28
 */
@Data
public class UpdateInstanceStateParams {

    @NotBlank(message = "配置id不能为空")
    private String ociCfgId;

    @NotBlank(message = "实例id不能为空")
    private String instanceId;

    @NotBlank(message = "实例操作不能为空")
    private String action;

}
