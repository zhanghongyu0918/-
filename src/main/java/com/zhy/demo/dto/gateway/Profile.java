package com.zhy.demo.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhy
 */
@AllArgsConstructor
@Data
public class Profile {
	private String baseUrl;
	private String basicAuth;
}
