package com.yohann.ocihelper.bean.params.oci.task;

import lombok.Data;

/**
 * @projectName: oci-w
 * @package: com.yohann.ocihelper.bean.params
 * @className: CreateTaskPageParams
 * @author: Wuge
 * @date: 2024/11/15 21:37
 */
@Data
public class CreateTaskPageParams {

    private String keyword;
    private long currentPage;
    private long pageSize;
    private String architecture;
}
