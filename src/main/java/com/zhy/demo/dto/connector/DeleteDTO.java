package com.zhy.demo.dto.connector;

import lombok.Data;

/**
 * @author zhy
 */
@Data
public class DeleteDTO {
    private Integer start;
    private Integer end;
    private String name;
    private String url;
}
