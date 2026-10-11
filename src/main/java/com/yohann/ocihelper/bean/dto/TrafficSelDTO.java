package com.yohann.ocihelper.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.dto
 * @className: TrafficSelDTO
 * @author: Wuge
 * @date: 2025/3/7 23:59
 */
@Data
public class TrafficSelDTO {

    private String key;
    private List<ValueLabelDTO> list;
}
