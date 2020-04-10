package me.geek.tom.mcprofileview.gui;

import javafx.stage.FileChooser;
import me.geek.tom.mcprofileview.profile.ProfileFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

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
        System.setProperty("java.awt.headless", "false");
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File result = chooser.showOpenDialog(null);
        if (result != null) {
            currentFile = result;
            if (!currentFile.isDirectory()) {
                try {
                    profile = ProfileFile.load(currentFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
