package com.venrob.robutils.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IndividualPvpToggle extends CommandBase {

    public static List<EntityPlayer> noAttack = new ArrayList<>();
    public static List<EntityPlayer> noDefend = new ArrayList<>();


    @Override
    public String getName() {
        return "pvpToggle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pvpToggle <Player> <true|false>";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos){
        if(args.length==1) {
            return Arrays.asList(server.getOnlinePlayerNames());
        } else if(args.length==2) {
            return Arrays.asList("true","false");
        } else {
            return Collections.emptyList();
        }
    }

    /*@Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
        if(sender.getCommandSenderEntity() instanceof EntityPlayer){
            if(((EntityPlayer)sender.getCommandSenderEntity()).canUseCommandBlock()){
                return true;
            }
            return false;
        } else {
            try {
                Utils.debug(sender.getDisplayName().getFormattedText());
                return true;
            } catch (Exception e){
                Utils.error("Exception thrown!");
                Utils.error(e.getMessage());
            }
        }
        return false;
    }*/

    //Commented out sections are for future implementation of "Toggling", rather than requiring true/false
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        boolean set = false;
        if(args.length<=1)throw new CommandException(getUsage(sender));
        //if(args.length>1){
            try {
                set = Boolean.parseBoolean(args[1]);
            } catch (Exception e){
                throw new CommandException(getUsage(sender));
            }
        //}
        List<EntityPlayerMP> pls = getPlayers(server, sender, args[0]);
        if(pls.size()==0)throw new CommandException("No such player found!");
        for(EntityPlayer pl : pls)
            togglePVP(sender,pl,/*(args.length==1),*/set);
    }
    private void togglePVP(ICommandSender sender, EntityPlayer pl, /*boolean tog,*/ boolean set) throws CommandException{
        if (pl != null) {
            /*if (tog) {
                if (noAttack.contains(pl)) {
                    noAttack.remove(pl);
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP Dampener disabled"));
                } else {
                    noAttack.add(pl);
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP Dampener enabled"));
                }
                if (noDefend.contains(pl)) {
                    noDefend.remove(pl);
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP Immunity disabled"));
                } else {
                    noDefend.add(pl);
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP Immunity enabled"));
                }
            } else */{
                if (set) {
                    if(noAttack.contains(pl)||noDefend.contains(pl))
                        sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP enabled"));
                    else
                        sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + " already has PVP"));
                    pl.removeTag("pvpt");
                    noAttack.remove(pl);
                    noDefend.remove(pl);
                } else {
                    if(noAttack.contains(pl)&&noDefend.contains(pl))
                        sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP is already disabled."));
                    else
                        sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP disabled"));
                    pl.addTag("pvpt");
                    if (!noAttack.contains(pl))
                        noAttack.add(pl);
                    if (!noDefend.contains(pl))
                        noDefend.add(pl);
                }
            }
        }
    }
}
