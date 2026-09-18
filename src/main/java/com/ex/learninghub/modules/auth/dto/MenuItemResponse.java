package com.ex.learninghub.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private String id;
    private String title;
    private String path;
    private String icon;
    private List<MenuItemResponse> children;
}
