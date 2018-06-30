package com.venrob.robutils.handlers;

import com.venrob.robutils.Main;
import com.venrob.robutils.util.FileLogger;
import com.venrob.robutils.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

@Mod.EventBusSubscriber
public class LoggerEventHandler {
    private static ArrayList<Item> itemsToLogR;
    private static File config;
    private static boolean doLog;
    private static boolean failLog;
    private static FileLogger fLogger;
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void preInit(){
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT)return;
        Main.logger.info("LoggerEventHandler PreInit...");
        config = new File(Main.baseMCPath + "/config/RobUtils/config.txt");
        if(!config.exists()){
            try {
                config.getParentFile().mkdirs();
                config.createNewFile();
                Main.logger.info("Creating \"/config/RobUtils/config.txt\"");
                FileWriter writer = new FileWriter(config);
                writer.append("//Set to true to log any broken/placed blocks as well as any items used from the list below to a log file\r\n" +
                        "doLog : false\r\n" +
                        "//In the lines below, specify the backend-name of the items you wish to log the right-click use of.\r\n" +
                        "//Example: to log someone eating an apple would be \"minecraft:apple\".\r\n" +
                        "//Do note that this will log when they start eating the apple, so if they start but then stop it will still log the event. This means they could start eating the same apple over and over, and it would log each time.\r\n" +
                        "//Enter one of these, without any quotation marks, per line. This will not work if the above value is not set to \"true\"\r\n");
                writer.close();
            } catch (IOException e) {
                Main.logger.warn("Could not create file \"/config/RobUtils/config.txt\"!");
            }
        }
    }
    public static void postInit(){
        if(FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT)return;
        readConfig(config);
    }
    public static void onServerStart(){
        if(doLog) {
            Main.logger.info("Initializing RUM Logger");
            try {
                fLogger = new FileLogger(Main.baseMCPath + "/logs/RobUtils/log-" + new Date().toString().replaceAll("[:\\s]","-") + ".txt");
                failLog = false;
            } catch (IOException e) {
                Main.logger.error(Utils.getStackTraceString(e));
            }
        } else {
            Main.logger.info("doLog is false in \"/config/RobUtils/config.txt\", or this is a Client-Only instance- RUM Logger will not be initialized");
        }
    }
    public static void onServerStop(){
        if(!doLog)return;
        Main.logger.info("Closing RUM Logger");
        fLogger.close();
    }
    private static void readConfig(File configFile){
        ArrayList<Item> out = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            int state = 0;
            while(reader.ready()&&state!=-1){
                String line = reader.readLine();
                if(line.startsWith("//"))continue;
                switch (state){
                    case 0:
                        if(line.startsWith("doLog")){
                            try {
                                doLog = Boolean.parseBoolean(line.split(":")[1].toLowerCase().replaceAll("\\s", ""));
                                if (doLog) {
                                    state = 1;
                                } else {
                                    state = -1;
                                }
                            }catch (Exception e){
                                doLog = false;
                                state = -1;
                            }
                        }
                        break;
                    case 1:
                        Item i = Item.getByNameOrId(line);
                        if(i!=null) out.add(i);
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            Main.logger.error("Error in \"/config/RobUtils/config.txt\"" + Utils.getStackTraceString(e));
        }
        itemsToLogR = out;
        StringBuilder sb = new StringBuilder();
        for(Item it : out){
            sb.append("\n");
            sb.append(it.getRegistryName());
        }
        Main.logger.info("Items read:" + sb.toString());
    }
    @SubscribeEvent
    public static void logBlockMod(BlockEvent event){
        if(!doLog||failLog) return;
        if(fLogger==null){
            Main.logger.error("File Logger not initialized!");
            failLog = true;
            return;
        }
        BlockEvent.BreakEvent breakEvent = null;
        BlockEvent.PlaceEvent placeEvent = null;
        if(event instanceof BlockEvent.BreakEvent)breakEvent = (BlockEvent.BreakEvent) event;
        else if(event instanceof BlockEvent.PlaceEvent)placeEvent = (BlockEvent.PlaceEvent) event;
        else return;
        //
        BlockPos pos = event.getPos();
        String s = Utils.getCurrentTimeStamp();
        if(breakEvent!=null)s += " [BlockBreak] " + breakEvent.getPlayer().getDisplayNameString() + " broke block ";
        else s += " [BlockPlace] " + placeEvent.getPlayer().getDisplayNameString() + " placed block ";
        s += event.getState().getBlock().getLocalizedName();
        s += " at coordinates " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        fLogger.write(s);
    }
    @SubscribeEvent
    public static void logItemUse(LivingEntityUseItemEvent.Start event){
        if(!doLog||failLog)return;
        if(fLogger==null){
            Main.logger.error("File Logger not initialized!");
            failLog = true;
            return;
        }
        if(itemsToLogR.contains(event.getItem().getItem())){
            if(!(event.getEntity() instanceof EntityPlayer))return;
            EntityPlayer player = (EntityPlayer) event.getEntity();
            BlockPos pos = player.getPosition();
            String s = Utils.getCurrentTimeStamp() + " [ItemUse] " + player.getDisplayNameString() + " used item ";
            if(event.getItem().getItem().getRegistryName()!=null)
                s += event.getItem().getItem().getRegistryName();
            else
                s += event.getItem().getItem().getUnlocalizedName();
            s += " at coordinates " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
            fLogger.write(s);
        }
    }
}
