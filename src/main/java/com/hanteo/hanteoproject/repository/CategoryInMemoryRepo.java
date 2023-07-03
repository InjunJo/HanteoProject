package com.hanteo.hanteoproject.repository;

import com.hanteo.hanteoproject.exception.DuplicateException;
import com.hanteo.hanteoproject.entity.BranchNode;
import com.hanteo.hanteoproject.entity.CategoryNode;
import com.hanteo.hanteoproject.entity.LeafNode;
import com.hanteo.hanteoproject.exception.NotFoundException;
import com.hanteo.hanteoproject.exception.NotFoundParentException;
import com.hanteo.hanteoproject.dto.RespCategory;
import com.hanteo.hanteoproject.entity.RootNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryInMemoryRepo {

    private final List<RootNode> rootTable;
    private final List<BranchNode> branchTable;
    private final List<LeafNode> leafTable;
    private final Map<String, CategoryNode> metaData;
    private final List<List<? extends CategoryNode>> list;

    private static final char CATEGORY_TYPE_OFFSET = 'a';
    private static final int LEAF_TYPE = 2;


    public CategoryInMemoryRepo() {
        this.rootTable = new ArrayList<>();
        this.branchTable = new ArrayList<>();
        this.leafTable = new ArrayList<>();
        this.metaData = new HashMap<>();
        this.list = new ArrayList<>();

        this.list.add(rootTable);
        this.list.add(branchTable);
        this.list.add(leafTable);
    }

    public CategoryInMemoryRepo(List<RootNode> rootTable, List<BranchNode> branchTable,
        List<LeafNode> leafTable, Map<String, CategoryNode> metaData,
        List<List<? extends CategoryNode>> list) {

        this.rootTable = rootTable;
        this.branchTable = branchTable;
        this.leafTable = leafTable;
        this.metaData = metaData;
        this.list = list;

        this.list.add(rootTable);
        this.list.add(branchTable);
        this.list.add(leafTable);
    }

    public RespCategory getAllCategory() {

        RespCategory respCategory = new RespCategory("0", -1, "카테고리", new ArrayList<>());

        for (RootNode node : rootTable) {
            if (node != null) {
                dfsCategoryNodes(node, respCategory, respCategory.getCategoryNodes());
            }
        }

        return respCategory;
    }


    public RespCategory getNodesByName(String categoryName) throws NotFoundException {

        // 등록된 카테고리 정보가 metaData Map에 없으면 예외를 던진다.
        if (!metaData.containsKey(categoryName)) {
            throw new NotFoundException("존재하지 않는 카테고리");
        }

        RespCategory respCategory = new RespCategory("0", -1, "카테고리", new ArrayList<>());

        // 등록된 카테고리가 어떤 레벨인지 그리고 몇 번째 순서인지 파악한다. a2 => a(root 순서)의 2번 째 idx
        String page = metaData.get(categoryName).getId();

        int id = Integer.parseInt(page.substring(1));
        int type = page.charAt(0) - CATEGORY_TYPE_OFFSET;

        // leaf 카테고리(자식을 포함하지 않는 최종 노드일 때)
        if (type == LEAF_TYPE) {
            return getLeafNode(type, categoryName, respCategory);
        }

        CategoryNode categoryNode = getCategoryNode(type, id);

        // 하위 카테고리를 포함하기 위해 깊이 우선 탐색을 실시한다.
        return dfsCategoryNodes(categoryNode, respCategory,
            respCategory.getCategoryNodes());
    }

    // leaf 카테고리(자식을 포함하지 않는 최종 노드일 때)
    private RespCategory getLeafNode(int type, String categoryName, RespCategory respCategory) {

        for (LeafNode node : leafTable) {
            if (node.getName().equals(categoryName)) {
                respCategory.getCategoryNodes()
                    .add(new RespCategory(node, Collections.emptyList()));
            }
        }

        return respCategory;
    }

    public RespCategory getNodesById(String id) {

        RespCategory respCategory = new RespCategory("0", -1, "카테고리", new ArrayList<>());

        int type = id.charAt(0) - CATEGORY_TYPE_OFFSET;
        int idx = Integer.parseInt(id.substring(1));

        // leaf 카테고리(자식을 포함하지 않는 최종 노드일 때)
        if (type == LEAF_TYPE) {

            return getLeafNode(type, id, respCategory);
        }

        // 하위 카테고리를 포함하기 위해 깊이 우선 탐색을 실시한다.
        return dfsCategoryNodes(getCategoryNode(type, idx), respCategory,
            respCategory.getCategoryNodes());
    }

    private RespCategory dfsCategoryNodes(CategoryNode node, RespCategory respCategory,
        List<RespCategory> list) {

        int id = extractId(node);

        // node의 타입에 맞게 하위 카테고리를 for문으로 순회하며 조건식에 부합 할 때, 재귀 함수를 호출 한다.
        if (node instanceof RootNode) {

            List<RespCategory> branchList = new ArrayList<>();
            list.add(new RespCategory(node, branchList));

            for (BranchNode branchNode : branchTable) {
                if (branchNode != null && branchNode.getParentIdx() == id) {
                    dfsCategoryNodes(branchNode, respCategory, branchList);
                }
            }
        } else if (node instanceof BranchNode) {
            List<RespCategory> leafList = new ArrayList<>();
            list.add(new RespCategory(node, leafList));

            for (LeafNode leafNode : leafTable) {
                if (leafNode != null && leafNode.getParentIdx() == id) {
                    dfsCategoryNodes(leafNode, respCategory, leafList);
                }
            }
        } else {
            list.add(new RespCategory(node, Collections.emptyList()));
        }

        return respCategory;
    }


    public int getParentInx(String parentName) {

        return Integer.parseInt(metaData.get(parentName).getId().substring(1));
    }

    // 같은 레벨에서 중복된 카테고리를 허용하지 않기 위해 중복 검사를 한다.
    public void checkDuplicate(CategoryNode categoryNode) throws DuplicateException {

        if (metaData.containsKey(categoryNode.getName())) {

            if (metaData.get(categoryNode.getName()).getParentIdx()
                == categoryNode.getParentIdx()) {
                throw new DuplicateException("중복된 카테고리");
            }
        }
    }

    public void addCategory(CategoryNode categoryNode) {
        // 같은 레벨의 중복 카테고리를 허용하지 않는다.
        checkDuplicate(categoryNode);

        // 각각의 타입에 맞는 list에 category node를 추가한다.
        if (categoryNode instanceof RootNode) {

            rootTable.add((RootNode) categoryNode);

        } else if (categoryNode instanceof BranchNode) {

            branchTable.add((BranchNode) categoryNode);
        } else {
            addLeaf((LeafNode) categoryNode);
        }

        metaData.put(categoryNode.getName(), categoryNode);
    }


    public void addLeaf(LeafNode leafNode) throws DuplicateException {
        checkDuplicate(leafNode);

        if (metaData.containsKey(leafNode.getName()) && leafNode.getName().equals("익명게시판")) {
            int idx = extractId(metaData.get(leafNode.getName()));

            LeafNode origin = leafTable.get(idx - 1);
            leafNode.setId(origin.getId());
            leafTable.add(leafNode);

        } else {
            leafTable.add(leafNode);
            metaData.put(leafNode.getName(), leafNode);
        }
    }

    private int extractId(CategoryNode categoryNode) {

        return Integer.parseInt(categoryNode.getId().substring(1));
    }

    public boolean isPresent(String categoryName) {

        return metaData.containsKey(categoryName);
    }

    public void remove(String categoryName) throws NotFoundException {

        CategoryNode result = metaData.get(categoryName);

        if (result == null) {
            throw new NotFoundException("[삭제 실패] 존재하지 않는 카테고리");
        }
        removeRelatedNodes(result);
    }

    private CategoryNode getCategoryNode(int type, int idx) throws NotFoundException {

        // 범위 밖의 카테고리 ID는 out of Index 에러를 방지하기 위해 Custom Exception으로 변환하여 에러를 던진다.
        if (list.get(type).size() <= idx) {
            throw new NotFoundParentException("존재하지 않는 카테고리 ID");
        }

        return list.get(type).get(idx);
    }

    public void removeById(String id) throws NotFoundException {

        int type = id.charAt(0) - CATEGORY_TYPE_OFFSET;
        int idx = Integer.parseInt(id.substring(1));

        CategoryNode categoryNode = getCategoryNode(type, idx);

        remove(categoryNode.getName());
    }

    // 삭제하고자 하는 카테고리의 하위 카테고리도 깊이 우선 탐색을 통해 함께 삭제 한다.
    // 관계데이터를 parenteIdx와 childId로만 하기 위해 여기선 idx의 변화를 없게 하기 위해 null 처리로 삭제를 대신 한다.
    private void removeRelatedNodes(CategoryNode categoryNode) {

        int id = extractId(categoryNode);

        //metaData에서 해당 카테고리 제거
        removeFromMetaData(categoryNode);

        if (categoryNode instanceof RootNode) {
            rootTable.set(id, null);

            for (BranchNode branchNode : branchTable) {
                if (branchNode != null && branchNode.getParentIdx() == id) {

                    branchTable.set(id, null);
                    removeRelatedNodes(branchNode);
                }
            }
        } else if (categoryNode instanceof BranchNode) {

            for (LeafNode leafNode : leafTable) {
                if (leafNode != null && leafNode.getParentIdx() == id) {

                    leafTable.set(id - 1, null);
                    removeRelatedNodes(leafNode);
                }
            }
        } else {
            categoryNode = null;
        }

    }

    // 카테고리를 삭제 할 때, metaData에서도 삭제를 한다.
    private void removeFromMetaData(CategoryNode categoryNode) {
        if (categoryNode.getName().equals("익명게시판")) {
            return;
        }

        metaData.remove(categoryNode.getName());
    }

}
