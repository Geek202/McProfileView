package me.geek.tom.mcprofileview.profile.tree;

import java.util.HashMap;
import java.util.Map;

public class TreeBranch implements TreePart {

    private String name, value;
    private TreePart parent;

    private Map<String, TreePart> branches = new HashMap<>();

    public TreeBranch(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setParent(TreePart parent) {
        this.parent = parent;
    }
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
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TreePart getParent() {
        return parent;
    }
}
