package com.hanteo.hanteoproject.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanteo.hanteoproject.entity.BranchNode;
import com.hanteo.hanteoproject.entity.LeafNode;
import com.hanteo.hanteoproject.entity.RootNode;
import com.hanteo.hanteoproject.exception.DuplicateException;
import com.hanteo.hanteoproject.exception.NotFoundParentException;
import com.hanteo.hanteoproject.repository.CategoryInMemoryRepo;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class CategoryServiceTest {

    private CategoryService categoryService;

    private CategoryInMemoryRepo categoryRepo;

    @BeforeEach
    public void setUp() {

        categoryRepo = CategoryInMemoryRepo.builder()
            .metaData(new HashMap<>())
            .rootTable(new ArrayList<>())
            .branchTable(new ArrayList<>())
            .leafTable(new ArrayList<>())
            .list(new ArrayList<>()).build();

        categoryService = new CategoryService(categoryRepo, new ObjectMapper());

        RootNode.resetCnt();
        BranchNode.resetCnt();
        LeafNode.resetCnt();
    }


    @Test
    void getNodes() {
        /* given */

        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */
        String result = categoryService.getNodesByName("남자");

        /* then */

        assertFalse(result.isEmpty());
    }

    @Test
    void getNodes_throw_NotFound() {
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */
        Executable e = () -> categoryService.getNodesByName("여자");

        /* then */

        assertThrows(NotFoundParentException.class, e);
    }

    @Test
    void remove() {
        /* given */

        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("엑소", "백현");


        /* when */
        System.out.println(categoryRepo.getBranchTable());
        categoryService.removeCatByNm("남자");

        var result = categoryRepo.getRootTable().stream()
            .anyMatch(r -> r != null && r.getName().equals("남자"));

        /* then */

        assertFalse(result);

    }

    @Test
    void addRoot() {
        /* given */
        categoryService.addRoot("남자");

        /* when */

        boolean success = categoryRepo.getRootTable().stream()
            .anyMatch(r -> r.getName().equals("남자"));

        /* then */
        assertTrue(success);
    }

    @Test
    void addRoot_duplicate_throw_Exception() {
        /* given */
        categoryService.addRoot("남자");


        /* when */

        Executable e = () -> categoryService.addRoot("남자");

        /* then */
        assertThrows(DuplicateException.class, e);
    }

    @Test
    void addBranch() {
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");

        /* when */

        boolean success = categoryRepo.getBranchTable().stream()
            .anyMatch(r -> r.getName().equals("엑소"));

        /* then */
        assertTrue(success);
    }

    @Test
    void addLeaf() {
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */

        boolean success = categoryRepo.getLeafTable().stream()
            .anyMatch(r -> r.getName().equals("백현"));

        /* then */
        assertTrue(success);
    }

    @Test
    public void checkPresent(){
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */

        Executable e = () -> categoryService.checkPresent("남자");

        /* then */

        assertDoesNotThrow(e);
    }

    @Test
    public void checkPresent_throw(){
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */

        Executable e = () -> categoryService.checkPresent("여자");

        /* then */

        assertThrows(NotFoundParentException.class,e);
    }

    @Test
    public void getNodesById(){
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */

        Executable e = () -> categoryService.getNodesById("b0");

        /* then */
        assertDoesNotThrow(e);
    }

    @Test
    public void getNodesById_throw(){
        /* given */
        categoryService.addRoot("남자");
        categoryService.addBranch("남자", "엑소");
        categoryService.addLeaf("남자", "백현");

        /* when */

        Executable e = () -> categoryService.getNodesById("b3");

        /* then */
        assertThrows(NotFoundParentException.class,e);
    }

    @Test
    public void total_test(){
        /* given */
        categoryService.addRoot("남자");

        categoryService.addBranch("남자","엑소");
        categoryService.addLeaf("엑소","공지사항");
        categoryService.addLeaf("엑소","첸");
        categoryService.addLeaf("엑소","백현");
        categoryService.addLeaf("엑소","시우민");

        categoryService.addBranch("남자","방탄");
        categoryService.addLeaf("방탄","공지시항");
        categoryService.addLeaf("방탄","익명게시판");
        categoryService.addLeaf("방탄","뷔");

        categoryService.addRoot("여자");
        categoryService.addBranch("여자","블랙핑크");
        categoryService.addLeaf("블랙핑크","공지사항");
        categoryService.addLeaf("블랙핑크","익명게시판");

        /* when */

        var result = categoryService.getNodesByName("여자");

        /* then */
        System.out.println(result);
    }

}