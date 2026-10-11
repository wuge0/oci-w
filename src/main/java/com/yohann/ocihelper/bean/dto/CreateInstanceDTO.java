package com.yohann.ocihelper.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * CreateInstanceDTO
 * </p >
 *
 * @author Wuge
 * @since 2024/11/19 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInstanceDTO {

    private List<InstanceDetailDTO> createInstanceList;
}
