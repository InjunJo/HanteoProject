package com.hanteo.hanteoproject.controller;

import com.hanteo.hanteoproject.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/{root}")
    public ResponseEntity<String> addRoot(@PathVariable(name = "root") String root) {

        categoryService.addRoot(root);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.getNodesByName(root));
    }

    @PostMapping("/{root}/{branch}")
    public ResponseEntity<String> addBranch(@PathVariable(name = "root") String root,
        @PathVariable(name = "branch") String branch) {

        categoryService.addBranch(root,branch);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.getNodesByName(root));
    }

    @PostMapping("/{root}/{branch}/{leaf}")
    public ResponseEntity<String> addLeaf(@PathVariable(name = "root") String root,
        @PathVariable(name = "branch") String branch,@PathVariable(name = "leaf") String leaf) {

        categoryService.addLeaf(branch,leaf);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.getNodesByName(root));
    }

    @GetMapping("/all")
    public ResponseEntity<String> getNodes() {

        String resp = categoryService.getAllCategory();

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/name/{categoryNm}")
    public ResponseEntity<String> getNodes(@PathVariable(name = "categoryNm") String name) {

        String resp = categoryService.getNodesByName(name);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/id/{categoryId}")
    public ResponseEntity<String> getNodesById(@PathVariable(name = "categoryId") String id) {

        String resp = categoryService.getNodesById(id);

        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/name/{categoryNm}")
    public ResponseEntity<String> removeNodesByName(@PathVariable(name = "categoryNm") String name) {

        categoryService.removeCatByNm(name);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제 완료");
    }

    @DeleteMapping("/id/{categoryId}")
    public ResponseEntity<String> removeNodesById(@PathVariable(name = "categoryId") String id) {

        categoryService.removeCatById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("삭제 완료");
    }



}
