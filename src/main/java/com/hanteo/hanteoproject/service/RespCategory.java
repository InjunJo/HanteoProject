package com.hanteo.hanteoproject.service;

import com.hanteo.hanteoproject.entity.CategoryNode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString @Getter
@Builder
public class RespCategory {

    private final String id;

    private final int parentIdx;
    private final String name;
    private final List<RespCategory> categoryNodes;

    public RespCategory(String id, int parentIdx, String name, List<RespCategory> categoryNodes) {
        this.id = id;
        this.parentIdx = parentIdx;
        this.name = name;
        this.categoryNodes = categoryNodes;
    }

    public RespCategory(CategoryNode node, List<RespCategory> categoryNodes) {
        this.id = node.getId();
        this.parentIdx = node.getParentIdx();
        this.name = node.getName();
        this.categoryNodes = categoryNodes;
    }
}
