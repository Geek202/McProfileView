package me.geek.tom.mcprofileview.gui;

import me.geek.tom.mcprofileview.profile.ProfileFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

public class ProfilerScreen extends Screen {

    private File currentFile;
    private Screen parent;
    private ProfileFile profile = null;

    public ProfilerScreen() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        addButton(new Button(10, 10, 85, 20, "Open profile.", this::openFile));
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        super.render(p_render_1_, p_render_2_, p_render_3_);
        FontRenderer renderer = Minecraft.getInstance().fontRenderer;
        if (currentFile != null) {
            //int width = minecraft.getMainWindow().getScaledWidth();
            //int height = minecraft.getMainWindow().getScaledHeight();
            String text = currentFile.getAbsolutePath();
            int x = 100;
            int y = 16;
            renderer.drawStringWithShadow(text, x, y, 0xFFFFFFFF);
        }
    }

    private void openFile(Button button) {
        System.setProperty("java.awt.headless", "false"); //Make sure headless is false, since this runs in a minecraft client this cannot be true

        FileDialog dialog = new java.awt.FileDialog((java.awt.Frame) null);
        dialog.setDirectory(System.getProperty("user.dir"));
        dialog.setVisible(true);

        //Make sure a file was chosen
        if(dialog.getFile() == null) return;

        File result = Paths.get(dialog.getDirectory(), dialog.getFile()).toFile();
        if (!result.isDirectory() && result.exists()) {
            currentFile = result;
            try {
                profile = ProfileFile.load(currentFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
