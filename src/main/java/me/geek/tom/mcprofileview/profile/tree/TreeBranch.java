package me.geek.tom.mcprofileview.profile.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeBranch implements TreePart {

    private String name, value;
    private float percentOfParent, percentOfTotal;
    private TreePart parent;

    private Map<String, TreePart> branches = new HashMap<>();

    public TreeBranch(String name, String value, float percentOfParent, float percentOfTotal) {
        this.name = name;
        this.value = value;
        this.percentOfParent = percentOfParent;
        this.percentOfTotal = percentOfTotal;
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
    public List<TreePart> getChildren() {
        return new ArrayList<>(branches.values());
    }

    @Override
    public float getTotalPercent() {
        return this.percentOfTotal;
    }

    @Override
    public float getParentPercent() {
        return this.percentOfParent;
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
