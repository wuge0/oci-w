package com.yohann.ocihelper.bean.response.oci.traffic;

import com.yohann.ocihelper.bean.dto.ValueLabelDTO;
import lombok.Data;

import java.util.List;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.response.oci.traffic
 * @className: FetchInstancesRsp
 * @author: Wuge
 * @date: 2025/3/7 22:32
 */
@Data
public class FetchInstancesRsp {

//    private List<ValueLabelDTO> instanceOptions;
    private String inboundTraffic;
    private String outboundTraffic;
    private Integer instanceCount;
}
