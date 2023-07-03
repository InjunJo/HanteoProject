package com.hanteo.hanteoproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanteo.hanteoproject.dto.RespCategory;
import com.hanteo.hanteoproject.entity.BranchNode;
import com.hanteo.hanteoproject.entity.LeafNode;
import com.hanteo.hanteoproject.entity.RootNode;
import com.hanteo.hanteoproject.exception.DuplicateException;
import com.hanteo.hanteoproject.exception.NotFoundException;
import com.hanteo.hanteoproject.exception.NotFoundParentException;
import com.hanteo.hanteoproject.repository.CategoryInMemoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service @Slf4j
public class CategoryService {

    private final CategoryInMemoryRepo categoryRepo;
    private final ObjectMapper objectMapper;

    public String getAllCategory() {
        RespCategory respCategory = categoryRepo.getAllCategory();

        return toJson(respCategory);
    }

    public String getNodesByName(String categoryName) throws NotFoundException {
        RespCategory respCategory = categoryRepo.getNodesByName(categoryName);

        return toJson(respCategory);
    }

    public String getNodesById(String id) throws NotFoundException {

        RespCategory respCategory = categoryRepo.getNodesById(id);
        return toJson(respCategory);
    }

    private String toJson(RespCategory respCategory) throws NotFoundException {
        if(respCategory == null){
            throw new NotFoundException("존재하지 않는 카테고리");
        }

        try {
            return objectMapper.writeValueAsString(respCategory);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCatByNm(String name) throws NotFoundException{
        categoryRepo.remove(name);
    }

    public void removeCatById(String id) throws NotFoundException{
        categoryRepo.removeById(id);
    }

    public void addRoot(String parentName) throws DuplicateException {

        categoryRepo.addCategory(new RootNode(parentName,0));
    }

    public void addBranch(String parentName,String childName) throws NotFoundParentException,DuplicateException{
        checkPresent(parentName);
        int parentIdx = categoryRepo.getParentInx(parentName);

        categoryRepo.addCategory(new BranchNode(childName,parentIdx));

    }

    public void addLeaf(String parentName,String childName) throws NotFoundParentException,DuplicateException{
        checkPresent(parentName);
        int parentIdx = categoryRepo.getParentInx(parentName);

        categoryRepo.addCategory(new LeafNode(childName,parentIdx));
    }

    void checkPresent(String parentName){

        if(!categoryRepo.isPresent(parentName)){
            throw new NotFoundParentException("존재하지 않는 부모 카테고리");
        }
    }


}
