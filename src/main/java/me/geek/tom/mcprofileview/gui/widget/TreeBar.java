package me.geek.tom.mcprofileview.gui.widget;

import me.geek.tom.mcprofileview.profile.tree.TreePart;
import me.geek.tom.mcprofileview.profile.tree.TreeRoot;
import net.minecraft.client.gui.widget.Widget;

import java.util.List;

public class TreeBar extends Widget {

    private TreeRoot root;
    private TreePart currentParent;

    private List<TreePart> currentDisplay;

    public static final int WIDTH = 800;
    public static final int HEIGHT = 20;

    private static final int[] COLOURS = new int[] {
            0xFFFF0000,
            0xFFFFFF00,
            0xFF00FF00,
            0xFF00FFFF,
            0xFF0000FF,
            0xFFFF00FF
    };

    public TreeBar(int xIn, int yIn, TreeRoot root) {
        super(xIn, yIn, WIDTH, HEIGHT, "");

        this.root = root;
        this.currentParent = root;

        this.currentDisplay = this.currentParent.getChildren();
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        int pos = 0;

        for (int i = 0; i < this.currentDisplay.size(); i++) {
            if (this.currentDisplay.get(i).getParentPercent() == 0.0f)
                continue;
            int col = i % COLOURS.length;

            int width = (int) (8 * this.currentDisplay.get(i).getParentPercent());
            fill(x + pos, y, x + width, y + HEIGHT, COLOURS[col]);
            pos += width;
        }
    }

    @Override
    public void onClick(double x, double y) {
        int idx = 0;
        System.out.println(x + " / " + y);

        for (int i = 0, pos = 0; i < this.currentDisplay.size(); i++) {
            int w = (int)(8 * this.currentDisplay.get(i).getParentPercent());
            if (x >= x + pos && x < pos + w) {
                idx = i;
                break;
            }
            pos += w;
        }

        System.out.println("Got section :" + idx + " clicked!");
    }
}
