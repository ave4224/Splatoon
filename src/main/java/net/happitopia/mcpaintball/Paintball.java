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


import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Avery Cowan
 */
public class Paintball extends JavaPlugin {

    public static final boolean PAINT_RIFLE = true;
    public static final boolean PAINT_DRIPPER = true;

    private static Paintball instance = null;
    
    public static final Material PAINT = Material.STAINED_CLAY;
    public static final DyeColor[] COLOR = {DyeColor.RED, DyeColor.BLUE};
    
    public static final String TEAM_TAG = "team";

    public static Paintball getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        Configuration config = getConfig();
        config.addDefault("Debug-Mode", Boolean.TRUE);
        config.addDefault("Paint Rifle.active", Boolean.TRUE);
        config.addDefault("Paint Rifle.display-name", "§r§bPaint Rifle");
        config.addDefault("Paint Rifle.speed", "3");
        
        config.addDefault("Debug-Mode", Boolean.FALSE);
        config.addDefault("Paint Dripper.active", Boolean.TRUE);
        config.addDefault("Paint Dripper.display-name", "§r§bPaint Dripper");
        config.addDefault("Paint Dripper.speed", "1");
        saveConfig();
        if (config.getBoolean("Paint Rifle.active")) {
            getServer().getPluginManager().registerEvents(new PaintRifle(this), this);
        }
        if (config.getBoolean("Paint Dripper.active")) {
            getServer().getPluginManager().registerEvents(new PaintDripper(this), this);
        }
    }
    
    public static void paintBlock(Block b, int team){
        paintBlock(b, COLOR[team]);
    }
    
    public static void paintBlock(Block b, DyeColor team) {
        if(b.getType().equals(Material.BEDROCK))
            return;
        b.setType(PAINT);
        b.setData(team.getData());
    }
    
    public static MetadataValue getMeta(List<MetadataValue> data){
        for(MetadataValue mdv: data){
            if(mdv.getOwningPlugin()==instance){
                return mdv;
            }
        }
        return new FixedMetadataValue(instance, 0);
    }

    
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setteam") && getConfig().getBoolean("Debug-Mode")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if(args.length==1){
                        ((Player)sender).setMetadata(TEAM_TAG, new FixedMetadataValue(this, args[0]));
                    }
                }
            }

        }
        return false;
    }
}