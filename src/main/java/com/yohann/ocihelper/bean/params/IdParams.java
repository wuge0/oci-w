package com.yohann.ocihelper.bean.params;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params
 * @className: IdParams
 * @author: Wuge
 * @date: 2024/11/13 23:52
 */
@Data
public class IdParams {

    @NotBlank(message = "id不能为空")
    private String id;
}
