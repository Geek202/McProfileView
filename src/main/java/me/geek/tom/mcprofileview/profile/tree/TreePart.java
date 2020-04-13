package me.geek.tom.mcprofileview.profile.tree;

import java.util.List;

public interface TreePart {
    String getValue();
    String getName();
    TreePart getParent();

    void addBranch(TreePart branch);
    <T extends TreePart> T getBranch(String name);

    List<TreePart> getChildren();
    float getTotalPercent();
    float getParentPercent();
}
