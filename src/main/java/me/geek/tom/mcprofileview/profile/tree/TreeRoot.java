package me.geek.tom.mcprofileview.profile.tree;

import java.util.HashMap;
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
}
