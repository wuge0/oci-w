package com.yohann.ocihelper.bean.params.cf;

import lombok.Data;

/**
 * @ClassName UpdateCfCfgParams
 * @Description:
 * @Author: Wuge
 * @CreateTime: 2025-03-20 17:37
 **/
@Data
public class UpdateCfCfgParams {

    private String id;
    private String domain;
    private String zoneId;
    private String apiToken;
}
