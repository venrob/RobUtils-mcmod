package com.venrob.robutils.handlers;

import com.venrob.robutils.commands.IndividualPvpToggle;
import com.venrob.robutils.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@EventBusSubscriber
public class PVPEventHandler {
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void cancelPvp(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer) {
            EntityPlayer attacker = event.getEntityPlayer();
            EntityPlayer defender = (EntityPlayer)event.getEntity();
            if (IndividualPvpToggle.noAttack.contains(attacker)) {
                if (event.isCancelable()) event.setCanceled(true);
                attacker.sendMessage(new TextComponentString("You currently are not allowed to PVP"));
            }
            if (IndividualPvpToggle.noDefend.contains(defender)) {
                if (event.isCancelable()) event.setCanceled(true);
                attacker.sendMessage(new TextComponentString("This target is currently immune to PVP"));
            }
        }
    }

    @SubscribeEvent()
    public static void cancelPvpSetOnConnect(PlayerEvent.PlayerLoggedInEvent event){
        EntityPlayer pl = event.player;
        if(pl.getTags().contains("pvpt")) {
            IndividualPvpToggle.noAttack.add(pl);
            IndividualPvpToggle.noDefend.add(pl);
        }
    }
}
