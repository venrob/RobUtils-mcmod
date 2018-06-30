package com.venrob.robutils;

import com.venrob.robutils.commands.IndividualPvpToggle;
import com.venrob.robutils.handlers.LoggerEventHandler;
import com.venrob.robutils.proxy.CommonProxy;
import com.venrob.robutils.util.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.File;


@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
    public static Logger logger;
    public static String baseMCPath;

    @Instance
    public static Main instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        logger = event.getModLog();
        baseMCPath = new File(".").getAbsolutePath();
        logger.info("Found Minecraft base path at \"" + baseMCPath + "\"");
        LoggerEventHandler.preInit();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event){

    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event){
        LoggerEventHandler.postInit();
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event){
        event.registerServerCommand(new IndividualPvpToggle());
        LoggerEventHandler.onServerStart();
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event){
        LoggerEventHandler.onServerStop();
    }

    //Catch block universal:
    //Main.logger.error(Utils.getStackTraceString(e));
}
