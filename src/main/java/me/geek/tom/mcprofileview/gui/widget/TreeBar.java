package me.geek.tom.mcprofileview.gui.widget;

import me.geek.tom.mcprofileview.profile.tree.TreePart;
import me.geek.tom.mcprofileview.profile.tree.TreeRoot;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

import java.util.List;
import java.util.function.Consumer;

public class TreeBar extends Widget {

    private TreeRoot root;
    public TreePart currentParent;

    private Consumer<Boolean> canStepUp;

    private List<TreePart> currentDisplay;

    public static final int WIDTH = 400;
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

    public void setCanStepUp(Consumer<Boolean> canStepUp) {
        this.canStepUp = canStepUp;
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        int pos = 0;

        fill(x, y, x + WIDTH, y + HEIGHT, 0xFF000000);

        for (int i = 0; i < this.currentDisplay.size(); i++) {
            if (this.currentDisplay.get(i).getParentPercent() == 0.0f)
                continue;
            int col = i % COLOURS.length;

            int width = (int) (4 * this.currentDisplay.get(i).getParentPercent());
            fill(x + pos, y, x + pos + width, y + HEIGHT, COLOURS[col]);
            pos += width;
        }
    }

    @Override
    public void onClick(double x, double y) {
        int idx = getMouseOver(x, y);

        if (idx != -1)
            updateDisplay(idx);
    }

    public int getMouseOver(double x, double y) {
        if (!this.isMouseOver(x, y))
            return -1;

        int idx = -1;

        for (int i = 0, pos = this.x; i < this.currentDisplay.size(); i++) {
            int w = (int)(4 * this.currentDisplay.get(i).getParentPercent());
            if (x >= pos && x < pos + w) {
                idx = i;
                break;
            }
            pos += w;
        }

        return idx;
    }

    public void updateDisplay(int idx) {
        if (idx >= 0) {
            this.currentParent = this.currentDisplay.get(idx);
            this.canStepUp.accept(true);
        } else {
            if (this.currentParent == this.root) {
                System.out.println("Trying to step up from the root element! This should not happen, but has somehow!");
                return;
            }
            this.currentParent = this.currentParent.getParent();
            this.canStepUp.accept(this.currentParent != this.root);
        }

        this.currentDisplay = this.currentParent.getChildren();
    }
}
