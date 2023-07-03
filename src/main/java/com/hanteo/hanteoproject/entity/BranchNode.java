package com.hanteo.hanteoproject.entity;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class BranchNode implements CategoryNode {

    private final String id;
    private final String name;

    private final int parentIdx;
    private static int cnt;

    public BranchNode(String name, int parentIdx) {
        this.id = "b"+cnt;
        this.name = name;
        this.parentIdx = parentIdx;
        ++cnt;
    }

    public static void resetCnt(){
        cnt = 0;
    }

}
