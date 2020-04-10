package me.geek.tom.mcprofileview.profile.tree;

public interface TreePart {
    String getValue();
    String getName();
    TreePart getParent();

    void addBranch(TreePart branch);
    <T extends TreePart> T getBranch(String name);
}
