package me.geek.tom.mcprofileview.events;

import me.geek.tom.mcprofileview.gui.ProfilerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static me.geek.tom.mcprofileview.McProfileView.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onGuiOpen(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof MainMenuScreen) {
            event.addWidget(new Button(10, 10, 100, 20, "McProfileViewer", btn ->
                    Minecraft.getInstance().displayGuiScreen(new ProfilerScreen())));
        }
    }
}
