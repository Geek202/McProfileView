package me.geek.tom.mcprofileview.gui;

import com.google.common.collect.Lists;
import me.geek.tom.mcprofileview.gui.widget.TreeBar;
import me.geek.tom.mcprofileview.profile.ProfileFile;
import me.geek.tom.mcprofileview.profile.tree.TreePart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfilerScreen extends Screen {

    private File currentFile;
    //private Screen parent;
    private ProfileFile profile = null;

    private TreeBar bar = null;
    private Button stepUp = null;

    public ProfilerScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        addButton(new Button(10, 10, 85, 20, "Open profile.", this::openFile));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        if (currentFile != null) {
            //int width = minecraft.getMainWindow().getScaledWidth();
            //int height = minecraft.getMainWindow().getScaledHeight();
            String text = currentFile.getAbsolutePath();
            int x = 100;
            int y = 16;
            renderer.drawStringWithShadow(text, x, y, 0xFFFFFFFF);
            if (profile != null && bar != null) {
                renderer.drawStringWithShadow("Ran for " + profile.getTicks() + " ticks in " + profile.getTime()
                        + " ms. (Aprox " + profile.getAverageTps() + " tps)",
                        10, 35, 0xFFFFFFFF);

                renderer.drawStringWithShadow(this.bar.currentParent.getName(), 10, 45, 0xFFFFFFFF);

                if (bar.getMouseOver(mouseX, mouseY) != -1)
                    this.drawTooltips(mouseX, mouseY);
            }
        }
    }

    private void drawTooltips(int mouseX, int mouseY) {
        TreePart over = bar.currentParent.getChildren().get(bar.getMouseOver(mouseX, mouseY));

        String[] pts = over.getName().split(" - ");
        String[] percents = pts[1].split("/");
        List<String> lines = new ArrayList<>();
        lines.add(pts[0]);
        lines.add(percents[0] + " of parent");
        lines.add(percents[1] + "of total");
        this.renderTooltip(lines, mouseX, mouseY);
    }

    private void openFile(Button button) {
        System.setProperty("java.awt.headless", "false"); // Make sure headless is false, since this runs in a minecraft client this cannot be true

        FileDialog dialog = new FileDialog((Frame) null);
        dialog.setDirectory(System.getProperty("user.dir"));
        dialog.setVisible(true);

        //Make sure a file was chosen
        if(dialog.getFile() == null) return;

        File result = Paths.get(dialog.getDirectory(), dialog.getFile()).toFile();
        if (!result.isDirectory() && result.exists()) {
            currentFile = result;
            try {
                profile = ProfileFile.loadNew(currentFile);

                if (bar != null) {
                    this.buttons.remove(bar);
                    this.children.remove(bar);
                } if (stepUp != null) {
                    this.buttons.remove(stepUp);
                    this.children.remove(stepUp);
                }

                bar = addButton(new TreeBar(10, 65, profile.getRoot()));
                stepUp = addButton(new Button(10, 90, 85, 20, "Step up", btn -> bar.updateDisplay(-1)));
                stepUp.active = false;
                bar.setCanStepUp(b -> stepUp.active = b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
