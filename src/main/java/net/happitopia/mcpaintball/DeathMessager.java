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

import averycowan.bukkit.util.Tools;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Avery Cowan
 */
public class DeathMessager implements Listener {
    
    private Plugin plugin;

    DeathMessager(Plugin p) {
        super();
        plugin = p;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity().getPlayer();
        event.setDeathMessage(Tools.dyeToChatColor(DyeColor.getByData(Paintball.getMeta(event.getEntity().getMetadata(Paintball.TEAM_TAG)).asByte())) + "" + ChatColor.BOLD + "" + p.getName() + " was Splatted.");
    }
}
