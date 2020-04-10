package me.geek.tom.mcprofileview;

import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static me.geek.tom.mcprofileview.McProfileView.MODID;

@Mod(MODID)
public class McProfileView {

    public static final String MODID = "mcprofileview";

    public McProfileView() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
    }

    private void init(FMLCommonSetupEvent event) {
        StartupMessageManager.addModMessage("mcprofileview::init");
    }

}
