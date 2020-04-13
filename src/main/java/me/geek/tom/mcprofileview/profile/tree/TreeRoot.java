package me.geek.tom.mcprofileview.profile.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeRoot implements TreePart {
    private Map<String, TreePart> branches = new HashMap<>();

    @Override
    public void addBranch(TreePart branch) {
        branches.put(branch.getName(), branch);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TreePart> T getBranch(String name) {
        return (T) branches.get(name);
    }

    @Override
    public List<TreePart> getChildren() {
        return new ArrayList<>(branches.values());
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public TreePart getParent() {
        return null;
    }

    @Override
    public float getTotalPercent() {
        return 100.0f;
    }

    @Override
    public float getParentPercent() {
        return 100.0f;
    }
}
