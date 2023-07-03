package com.hanteo.hanteoproject.entity;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class LeafNode implements CategoryNode {

    private String id;

    private final String name;

    private final int parentIdx;

    private static int cnt = 1;

    public LeafNode(String name,int parentIdx) {
        this.id = "c"+cnt;
        this.name = name;
        this.parentIdx = parentIdx;
        ++cnt;
    }

    public void setId(String id){
        this.id = id;
    }

    public static void resetCnt(){
        cnt = 1;
    }

}
