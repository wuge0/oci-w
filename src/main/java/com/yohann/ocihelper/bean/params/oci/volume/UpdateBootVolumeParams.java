package com.yohann.ocihelper.bean.params.oci.volume;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params.oci
 * @className: UpdateBootVolumeParams
 * @author: Wuge
 * @date: 2025/1/4 19:27
 */
@Data
public class UpdateBootVolumeParams {

    @NotBlank(message = "配置id不能为空")
    private String ociCfgId;
    @NotBlank(message = "引导卷id不能为空")
    private String bootVolumeId;
    @NotBlank(message = "引导卷大小不能为空")
    private String bootVolumeSize;
    @NotBlank(message = "引导卷VPU不能为空")
    private String bootVolumeVpu;
}
