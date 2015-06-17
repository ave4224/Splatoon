/*
 * Copyright 2015 Avery.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.happitopia.mcpaintball;

import static net.happitopia.mcpaintball.Paintball.TEAM_TAG;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

/**
 *
 * @author Avery Cowan
 */
public class PaintDripper implements Listener{
    
    public static final String ID = "dripper";
    
    private Plugin plugin;
    
    PaintDripper(Plugin p) {
        super();
        plugin = p;
    }
    
    public static ItemStack getWeapon() {
        return new ItemStack(Material.CLAY_BALL);
    }
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        ItemStack drop = event.getPlayer().getItemInHand();
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && drop.getType().equals(getWeapon().getType())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
            Arrow bullet = event.getPlayer().launchProjectile(Arrow.class);
            bullet.setVelocity(bullet.getVelocity().multiply(plugin.getConfig().getDouble("PaintDripper.speed")));
            bullet.setMetadata("weaponID", new FixedMetadataValue(plugin, ID));
            bullet.setBounce(false);
            Location l = event.getPlayer().getLocation();
            bullet.setMetadata("launchLocationX", new FixedMetadataValue(plugin, l.getX()));
            bullet.setMetadata("launchLocationY", new FixedMetadataValue(plugin, l.getY()));
            bullet.setMetadata("launchLocationZ", new FixedMetadataValue(plugin, l.getZ()));
        }
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && drop.getType().equals(Material.INK_SACK)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
            event.getPlayer().setMetadata(TEAM_TAG, new FixedMetadataValue(plugin, DyeColor.getByDyeData((byte)drop.getDurability()).getData()));
            event.getPlayer().sendMessage("You are now on team " + DyeColor.getByDyeData((byte)drop.getDurability()).name());
        }
    }
    
    @EventHandler@SuppressWarnings("empty-statement")
 void ProjectileHit(ProjectileHitEvent event) {
        if((!(event.getEntity().getShooter() instanceof Player)) || (!Paintball.getMeta(event.getEntity().getMetadata("weaponID")).asString().equals(ID)))
            return;
        Projectile p = (Projectile)(event.getEntity());
        MetadataValue meta = Paintball.getMeta(((Entity)p.getShooter()).getMetadata(Paintball.TEAM_TAG));
        Vector start = null;
        {
            double x = Paintball.getMeta(p.getMetadata("launchLocationX")).asDouble();
            double y = Paintball.getMeta(p.getMetadata("launchLocationY")).asDouble();
            double z = Paintball.getMeta(p.getMetadata("launchLocationZ")).asDouble();
            start = new Vector(x, y, z);
        }
        BlockIterator iterator = new BlockIterator(p.getWorld(), p.getLocation().toVector(), p.getVelocity().normalize(), 0, 4);
        Block hitBlock = null;
        while(iterator.hasNext() && (hitBlock=iterator.next()).isEmpty());
        if(hitBlock.isEmpty())
            return;
        
        iterator = new BlockIterator(p.getWorld(), start, p.getLocation().toVector().toBlockVector().subtract(start.toBlockVector()).normalize(), 0, (int)(2*hitBlock.getLocation().toVector().distance(start)));
        Block activeBlock = null;
        DyeColor color = DyeColor.getByData(Paintball.getMeta(((Entity)p.getShooter()).getMetadata(Paintball.TEAM_TAG)).asByte());
        while(iterator.hasNext()){
            activeBlock=iterator.next();
            if(activeBlock.getX() == hitBlock.getX() && activeBlock.getZ() == hitBlock.getZ())
                break;
            int x = activeBlock.getX();
            int z = activeBlock.getZ();
            for(int y = 255; y > 0; y--){
                Block b = p.getWorld().getBlockAt(x, y, z);
                if((!b.isEmpty()) && b.getType().isSolid() && y >= 0){
                    Paintball.paintBlock(b, color);
                    break;
                }
            }
        }
        Paintball.paintBlock(hitBlock, color);
    }
    
}