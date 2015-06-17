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

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
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

/**
 *
 * @author Avery Cowan
 */
public class PaintRifle implements Listener {

    public static final String ID = "sniper";

    private Plugin plugin;

    PaintRifle(Plugin p) {
        super();
        plugin = p;
    }

    public static ItemStack getWeapon() {
        return new ItemStack(Material.BLAZE_ROD);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        ItemStack drop = event.getPlayer().getItemInHand();
        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && drop.getType().equals(getWeapon().getType())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
            SmallFireball bullet = event.getPlayer().launchProjectile(SmallFireball.class);
            bullet.setDirection(event.getPlayer().getLocation().getDirection().clone());
            bullet.setVelocity(bullet.getVelocity().multiply(plugin.getConfig().getDouble("Paint Rifle.speed")));
            bullet.setIsIncendiary(false);
            bullet.setMetadata("weaponID", new FixedMetadataValue(plugin, ID));
            //bullet.setBounce(true);
            bullet.setYield(0);
        }
    }

    @EventHandler
    @SuppressWarnings("empty-statement")
    public void projectileHit(ProjectileHitEvent event) {
        if (!Paintball.getMeta(event.getEntity().getMetadata("weaponID")).asString().equals(ID)) {
            return;
        }
        Projectile p = (Projectile) (event.getEntity());
        MetadataValue meta = Paintball.getMeta(((Entity) p.getShooter()).getMetadata(Paintball.TEAM_TAG));

        if (!(p.getShooter() instanceof Player)) //Making sure the shooter is a player
        {
            return;
        }

        BlockIterator iterator = new BlockIterator(p.getWorld(), p.getLocation().toVector(), p.getVelocity().normalize(), 0, 256);
        Block hitBlock = null;
        while (iterator.hasNext() && ((hitBlock = iterator.next()).isEmpty() || hitBlock.isLiquid()));
        if (hitBlock.isEmpty() || hitBlock.isLiquid()) {
            return;
        }

        Paintball.paintBlock(hitBlock, DyeColor.getByData(meta.asByte()));
    }

}
