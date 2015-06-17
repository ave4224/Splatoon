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
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Avery Cowan
 */
public class SplashPotionManager implements Listener{
    
    private Plugin plugin;

    SplashPotionManager(Plugin p) {
        super();
        plugin = p;
    }
    
    @EventHandler
    public void projectileHit(ProjectileHitEvent event){
        if(event.getEntityType()==EntityType.SPLASH_POTION && event.getEntity().getShooter() instanceof Player){
            ThrownPotion s = (ThrownPotion) event.getEntity();
            Block b = s.getWorld().getBlockAt(s.getLocation());
            DyeColor team = DyeColor.getByData(Paintball.getMeta(((Player)s.getShooter()).getMetadata(Paintball.TEAM_TAG)).asByte());
            for(int i = 0; i < 27; i++)
                Paintball.paintBlock(b.getRelative(i/9-1, (i/3)%3-1, i%3-1), team);
        }
    }
}
