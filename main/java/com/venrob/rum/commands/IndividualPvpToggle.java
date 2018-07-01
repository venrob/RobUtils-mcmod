package com.venrob.rum.commands;

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

    public static List<EntityPlayer> noPVP = new ArrayList<>();

    @Override
    public String getName() {
        return "pvpToggle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pvpToggle <Player> <true|false>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return Arrays.asList(server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return Arrays.asList("true", "false");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean set = false;
        if (args.length <= 1) throw new CommandException(getUsage(sender));
        set = Boolean.parseBoolean(args[1]);
        List<EntityPlayerMP> pls = getPlayers(server, sender, args[0]);
        if (pls.size() == 0) throw new CommandException("No such player found!");
        for (EntityPlayer pl : pls)
            togglePVP(sender, pl, set);
    }

    private void togglePVP(ICommandSender sender, EntityPlayer pl, boolean set) throws CommandException {
        if (pl != null) {
            if (set) {
                if (noPVP.contains(pl)) {
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP enabled"));
                    noPVP.remove(pl);
                } else
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + " already has PVP"));
                pl.removeTag("pvpt");
            } else {
                if (noPVP.contains(pl))
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP is already disabled."));
                else {
                    sender.sendMessage(new TextComponentString(pl.getDisplayNameString() + "'s PVP disabled"));
                    noPVP.add(pl);
                }
                pl.addTag("pvpt");
            }
        }
    }
}
