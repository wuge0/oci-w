package com.yohann.ocihelper.bean.params.ipdata;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params.ipdata
 * @className: AddIpDataParams
 * @author: Wuge
 * @date: 2025/8/5 21:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddIpDataParams {

    @NotBlank(message = "ip地址不能为空")
    private String ip;
}
