package com.budderman18.IngotSkywars.Bukkit;

import com.budderman18.IngotMinigamesAPI.Addons.GameBorder;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ChatHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageType;
import com.budderman18.IngotSkywars.Core.Game;
import com.budderman18.IngotSkywars.Core.Lobby;
import com.budderman18.IngotSkywars.Core.SWArena;
import com.budderman18.IngotSkywars.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import static org.bukkit.GameMode.ADVENTURE;
import static org.bukkit.GameMode.SPECTATOR;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.PLAYER;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *
 * This class handles protection-based events. 
 * You can block building, breaking, and block/entity interactions
 * This contains events and has to be within every plugin, 
 * rather than in IngotMiniGamesAPI. 
 * 
 */
public class Events implements Listener {
    //plugin
    private static Plugin plugin = Main.getInstance();
    //files
    private static final String ROOT = "";
    private static FileConfiguration config = FileManager.getCustomData(plugin,"config",ROOT);
    //global vars
    private static boolean inGame = false;
    private static boolean isPlaying = false;
    private static boolean isFrozen = false;
    private static boolean isAlive = true;
    private static boolean isGraced = false;
    private static IngotPlayer iplayer = null;
    private static String killer = "";
    private static String item = "";
    /**
     * 
     * This method reloads the config file
     * 
     */
    public static void reload() {
        config = FileManager.getCustomData(plugin,"config",ROOT);
    }
    /**
     *
     * This method handles block breaking. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        //obtain IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //block event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles block placing. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //block event
            event.setCancelled(true);
        }
    } 
    /**
     *
     * This method handles item dropping 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is inGame
        if (inGame == true && isPlaying == false) { 
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles block interactions
     *
     * @param event the event ran
     */
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {       
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles entity interactions (right click). 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles armor stand interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onStandInteract(PlayerArmorStandManipulateEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles armor stand interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onFrameInteract(PlayerInteractAtEntityEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles lecturn interactions. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onLecturnInteract(PlayerTakeLecternBookEvent event) {
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isPlaying = iplayer.getIsPlaying();
        //check if protection is enabled and player is ingame
        if (config.getBoolean("enable-protection") == true && inGame == true && isPlaying == false) {  
            //cancel event
            event.setCancelled(true);
        }
    }
    /**
     *
     * This method handles attacking entities. 
     * Players are not protected while playing. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        //local vars
        boolean cancel = false;
        double xloc = 0;
        double zloc = 0;
        double diff = 0;
        IngotPlayer iplayer = null;
        GameBorder border = null;
        //check if entity is an armor stand
        if (event.getEntity().getType() == EntityType.ARMOR_STAND) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //cycle through hologram
                for (ArmorStand keys : key.getHologram()) {
                    //check if entity is apart of a hologram
                    if (keys == event.getEntity()) {
                        cancel = true;
                    }
                }
            }
            event.setCancelled(cancel);
        }
        //check if entity is a player and killer is a player
        if (event.getEntityType() == PLAYER && event.getDamager().getType() == PLAYER) {
            //get IngotPlayerdata
            iplayer = IngotPlayer.selectPlayer(event.getDamager().getName(), plugin); 
            inGame = iplayer.getInGame();
            isPlaying = iplayer.getIsPlaying();
            isGraced = iplayer.getIsGraced();
            //check if player is currently playing
            if (isPlaying == true) {
                //don't cancel event
                event.setCancelled(false);
            }
            //if above check fails, check if protection is enabled and player is inGame
            else if (config.getBoolean("enable-protection") == true && inGame == true) { 
                //cancel event
                event.setCancelled(true);
            }
            //check if attacking a teammate and cancel
            if (iplayer.getTeam() != null) {
                if (isPlaying == true && iplayer.getTeam().getFriendlyFire() == false && iplayer.getTeam().isOnSameTeam(iplayer, IngotPlayer.selectPlayer(event.getEntity().getName(), plugin)) == true) {
                    event.setCancelled(true);
                }
            }
            //check if damager has grace period
            if (isGraced == true && config.getBoolean("grace-period.enable") == true && config.getBoolean("grace-period.cancel-on-hit") == true) {
                iplayer.setIsGraced(false);
                //cycle through all effects
                for (PotionEffectType keys : PotionEffectType.values()) {
                    //remove effect
                    Bukkit.getPlayer(iplayer.getUsername()).removePotionEffect(keys);
                }
            }
            //check if damager has grace period and not using cancel on hit
            else if (isGraced == true && config.getBoolean("grace-period.enable") == true && config.getBoolean("grace-period.cancel-on-hit") == false) {
                event.setCancelled(true);
            }
            //get killer string
            killer = event.getDamager().getName();
            //check if player isnt null
            if (Bukkit.getPlayer(event.getDamager().getName()) != null) {
                //get item string
                item = Bukkit.getPlayer(event.getDamager().getName()).getInventory().getItemInMainHand().getType().toString().toLowerCase();
                //check if holding an item
                if (Bukkit.getPlayer(event.getDamager().getName()).getInventory().getItemInMainHand().getItemMeta() != null) {
                    //check if valid held item
                    if (!"".equals(Bukkit.getPlayer(event.getDamager().getName()).getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {
                        //obtain item
                        item = item.concat(":" + Bukkit.getPlayer(event.getDamager().getName()).getInventory().getItemInMainHand().getItemMeta().getDisplayName());
                        item = item.split(":", -1)[1];
                    }
                }
            } 
            else {
                item = "their bare hands";
            }
            //check if item was empty
            if (item.equalsIgnoreCase("air")) {
                //set defualt
                item = "their bare hands";
            }
        }
        //check if entity is a player
        else if (event.getEntityType() == PLAYER) {
            //reset death vars
            killer = event.getDamager().getName();
            item = " ";
            iplayer = IngotPlayer.selectPlayer(event.getEntity().getName(), plugin);
            //check if in a lobby
            if (iplayer.getInGame() == true && iplayer.getIsPlaying() == false) {
                //check if protection is enable
                if (config.getBoolean("enable-protection") == true) {
                    //dont damage
                    event.setCancelled(true);
                }
            }
            //check if in a game
            if (iplayer.getInGame() == true && iplayer.getIsPlaying() == true) {
                //get border
                border = GameBorder.selectBorder(iplayer.getGame(), plugin);
                //check if type matches border entity
                if (event.getDamager().getType() == border.getBorderEntity() && event.getDamager().getCustomName().equalsIgnoreCase(border.getName())) {
                    //setup location
                    xloc = event.getEntity().getLocation().getX() + border.getCenter().getX();
                    zloc = event.getEntity().getLocation().getZ() + border.getCenter().getZ();
                    //check if zloc is negative
                    if (zloc < 0) {
                        //get absolute value
                        zloc = zloc*-1;
                    }
                    //check if xloc is negative
                    if (xloc < 0) {
                        //get absolute value
                        xloc = xloc*-1;
                    } 
                    //get difference
                    diff = zloc - xloc;
                    //re-get location
                    xloc = event.getEntity().getLocation().getX() + border.getCenter().getX();
                    zloc = event.getEntity().getLocation().getZ() + border.getCenter().getZ();
                    //check for western wall
                    if ((xloc >= zloc && xloc > border.getCenter().getX()) && diff <= 0) {
                        //throw player
                        event.getEntity().setVelocity(new Vector(-border.getEntityKnockback(),border.getEntityKnockback()*0.5,0));
                    }    
                    //check for southern wall
                    else if ((zloc >= xloc && zloc > border.getCenter().getZ()) && diff > 0) {
                        //throw player
                        event.getEntity().setVelocity(new Vector(0,border.getEntityKnockback()*0.5,-border.getEntityKnockback()));
                    } 
                    //check for eastern wall
                    else if ((xloc < zloc && xloc < border.getCenter().getX() || (xloc > zloc && xloc < border.getCenter().getX())) && diff <= 0) {
                        //throw player
                        event.getEntity().setVelocity(new Vector(border.getEntityKnockback(),border.getEntityKnockback()*0.5,0));
                    }    
                    //check for northern wall
                    else if ((zloc < xloc && zloc < border.getCenter().getZ() || (zloc > xloc && zloc < border.getCenter().getZ())) && diff > 0) {
                        //throw player
                        event.getEntity().setVelocity(new Vector(0,border.getEntityKnockback()*0.5,border.getEntityKnockback()));
                    }
                    //get border name
                    item = event.getDamager().getCustomName();
                    //set damage manually so armor is ignored AND missed projectiles inflic damage
                    event.setDamage(0);
                    //check if hit didnt kill player
                    if (Bukkit.getPlayer(event.getEntity().getName()).getGameMode() == GameMode.SURVIVAL || Bukkit.getPlayer(event.getEntity().getName()).getGameMode() == GameMode.ADVENTURE) {
                        if ((Bukkit.getPlayer(event.getEntity().getName()).getHealth() - border.getEntityDamage()) >= 0) {
                            Bukkit.getPlayer(event.getEntity().getUniqueId()).setHealth(Bukkit.getPlayer(event.getEntity().getName()).getHealth() - border.getEntityDamage());
                        }
                        //run ifplayer should be killed
                        else {
                            Bukkit.getPlayer(event.getEntity().getUniqueId()).setHealth(0);
                        }
                    }
                    Bukkit.getPlayer(event.getEntity().getUniqueId()).playSound(event.getEntity(), Sound.ENTITY_PLAYER_HURT, 10, 1);
                }
            }
        }
        else if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() != EntityType.PLAYER) {
            try {
                //get IngotPlayerdata
                iplayer = IngotPlayer.selectPlayer(event.getDamager().getName(), plugin);
                inGame = iplayer.getInGame();
                isPlaying = iplayer.getIsPlaying();
                //check if protection is enabled and player is inGame
                if (config.getBoolean("enable-protection") == true && (inGame == true || isPlaying == true)) {
                    //cancel event
                    event.setCancelled(true);
                }
            }
            catch (NullPointerException ex) {}
        }
    }
    /**
     * 
     * This method handles projectile damage
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        //local vars
        GameBorder border = null;
        //cycle through players
        for (Player key : Bukkit.getOnlinePlayers()) {
            //get player
            iplayer = IngotPlayer.selectPlayer(key.getName(), plugin);
            //check if playter is ingame
            if (iplayer.getInGame() == true) {
                //get border
                border = GameBorder.selectBorder(iplayer.getGame(), plugin);
                //check if type matches border entity
                if (event.getEntity().getType() == border.getBorderEntity() && border.isInBorder(key.getLocation()) == false && (key.getGameMode() == GameMode.SURVIVAL || key.getGameMode() == GameMode.ADVENTURE)) {
                    //call hit event
                    onEntityHit(new EntityDamageByEntityEvent(event.getEntity(), key, DamageCause.PROJECTILE, border.getEntityDamage()));
                }
            }
        }
    }
    /**
     * 
     * This method handles entity deaths
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //local vars
        boolean cancel = false;
        //check if entity is an armor stand
        if (event.getEntity().getType() == EntityType.ARMOR_STAND && config.getBoolean("protect-holograms-from-kill-commands") == true) {
            //cycle through leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //cycle through hologram
                for (ArmorStand keys : key.getHologram()) {
                    //check if entity is apart of a hologram
                    if (keys == event.getEntity() && key.getSummoned() == true) {
                        cancel = true;
                    }
                }
                //check if cancleed
                if (cancel == true) {
                    //summon hologram
                    key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                    cancel = false;
                }
            }
        }
    }
    /**
     *
     * This method handles player movement(including rotation changes).
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //local vars
        Location frozenpos = event.getTo();
        //get IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        isFrozen = iplayer.getIsFrozen();
        isAlive = iplayer.getIsAlive();
        //get arena
        SWArena selectedArena = null;
        if (iplayer.getGame() != null || !"".equals(iplayer.getGame())) {
            selectedArena = SWArena.selectArena(iplayer.getGame(), plugin);
        }
        //check if player is froxen
        if (isFrozen == true) {
            //reset position to current position
            frozenpos.setX(event.getFrom().getX());
            frozenpos.setY(event.getFrom().getY());
            frozenpos.setZ(event.getFrom().getZ());
            //teleport player
            event.getPlayer().teleport(frozenpos);
        }
        //check if player is spectating and in arena
        if (iplayer.getGame() != null || !"".equals(iplayer.getGame())) {
            if (inGame == true && isAlive == false && event.getPlayer().getGameMode() == SPECTATOR && selectedArena.getArenaEquivelent().isInArena(event.getPlayer().getLocation()) == false) {
                //move player to center
                frozenpos = event.getPlayer().getLocation();
                frozenpos.setX(selectedArena.getArenaEquivelent().getCenter()[0]);
                frozenpos.setY(selectedArena.getArenaEquivelent().getCenter()[1]);
                frozenpos.setZ(selectedArena.getArenaEquivelent().getCenter()[2]);
                frozenpos.setYaw((float) selectedArena.getArenaEquivelent().getCenter()[3]);
                frozenpos.setPitch((float) selectedArena.getArenaEquivelent().getCenter()[4]);
                event.getPlayer().teleport(frozenpos);
            }
        }
    }
    /**
     *
     * This method handles player chatting. 
     *
     * @param event the event ran
     */
    @EventHandler    
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        //local vars
        List<Player> players = new ArrayList<>();
        IngotPlayer currentIPlayer = null;
        Player player = null;
        //IngotPlayerdata
        iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        inGame = iplayer.getInGame();
        //check if player is inGame and chat is enabled
        if (inGame == true && config.getBoolean("Chat.enable") == true) {
            //cancel event
            event.setCancelled(true);
            //cycle between all players
            for (Player key : Bukkit.getOnlinePlayers()) {
                //get IngotPlayer
                currentIPlayer = IngotPlayer.selectPlayer(key.getName(), plugin);
                inGame = currentIPlayer.getInGame();
                //checfk if player is inGame
                if (inGame == true && currentIPlayer.getGame().equals(iplayer.getGame())) {
                    //add player to list
                    players.add(key);
                }
            }
            //cycle between all assigned players
            for (short i = 0; i < players.size(); i++) {
                //send message to player
                player = players.get(i);
                player.sendMessage(ChatHandler.convertMessage(Bukkit.getPlayer(iplayer.getUsername()), event.getMessage(), config.getString("Chat.format")));
            }
            Bukkit.getServer().getConsoleSender().sendMessage(ChatHandler.convertMessage(Bukkit.getPlayer(iplayer.getUsername()), event.getMessage(), config.getString("Chat.format")));
        }
    }
    /**
     * 
     * This method handles player deaths
     * 
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //local vars
        String message = "";
        int length = 0;
        short kills = 0;
        byte gameKills = 0;
        short deaths = 0;
        short score = 0;
        try {
            iplayer = IngotPlayer.selectPlayer(event.getEntity().getName(), plugin);
            inGame = iplayer.getInGame();
            //check if player is ingame
            if (inGame == true) {
                //check for sword
                if (item.contains("sword")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.SWORD, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for axe
                else if (item.contains("axe")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.AXE, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for trident
                else if (item.contains("trident")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.TRIDENT, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for arrow
                else if (event.getDeathMessage().contains("shot")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.ARROW, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for firework
                else if (event.getDeathMessage().contains("bang")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.FIREWORK, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for melee trident
                else if (event.getDeathMessage().contains("impaled")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.TRIDENT, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for magic
                else if (event.getDeathMessage().contains("magic")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.POTION, plugin);
                    length = event.getDeathMessage().split(" ").length-3;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for slain w/o valid tool
                else if (event.getDeathMessage().contains("slain") && (item != "sword" || item != "axe")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.HAND, plugin);
                    length = event.getDeathMessage().split(" ").length-1;
                    //check for no name
                    if (!event.getDeathMessage().split(" ")[length].contains("]")) {
                        //get killer
                        killer = event.getDeathMessage().split(" ")[length];
                    }
                    else {
                        //get killer and item name
                        killer = event.getDeathMessage().replace("by ", ":");
                        killer = killer.replace(" using", ":");
                        killer = killer.split(":")[1];
                    }
                }
                //check for fall
                else if (event.getDeathMessage().contains("high place") || event.getDeathMessage().contains("fell out")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.FALL, plugin);
                }
                //check for explosion
                else if (event.getDeathMessage().contains("blew up")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.EXPLOSION, plugin);
                }
                //check for block
                else if (event.getDeathMessage().contains("squashed")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.CRUSHED, plugin);
                }
                //check for fire
                else if (event.getDeathMessage().contains("flames")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.FIRE, plugin);
                }
                //check for lava
                else if (event.getDeathMessage().contains("lava")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.LAVA, plugin);
                }
                //check for drowned
                else if (event.getDeathMessage().contains("drowned")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.DROWN, plugin);
                }
                //check for sufficate
                else if (event.getDeathMessage().contains("suffocated")) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.SUFFICATE, plugin);
                }
                //check for border
                else if (item.contains(iplayer.getGame())) {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.BORDER, plugin);
                }
                //run for everything else
                else {
                    //get message
                    message = DeathMessageHandler.getMessage(DeathMessageType.OTHER, plugin);
                }
                //check if message is somehow null
                if (message == null) {
                    //get message
                    message = "invalid message.";
                }
                //check for kill
                if (IngotPlayer.selectPlayer(killer, plugin) != null) {
                    //set kills and score
                    kills = IngotPlayer.selectPlayer(killer, plugin).getKills();
                    gameKills = IngotPlayer.selectPlayer(killer, plugin).getGameKills();
                    score = IngotPlayer.selectPlayer(killer, plugin).getScore();
                    kills++;
                    gameKills++;
                    score += config.getInt("Score.kill");
                    IngotPlayer.selectPlayer(killer, plugin).setKills(kills);
                    IngotPlayer.selectPlayer(killer, plugin).setGameKills(gameKills);
                    IngotPlayer.selectPlayer(killer, plugin).setScore(score);
                }
                //add death
                deaths = iplayer.getDeaths();
                score = iplayer.getScore();
                deaths++;
                score += config.getInt("Score.death");
                iplayer.setDeaths(deaths);
                iplayer.setScore(score);
                //item
                if (item.contains("]")) {
                    item = item.split(":")[1];
                }
                //set death message
                event.setDeathMessage(DeathMessageHandler.formatMessage(FileManager.getCustomData(plugin, "language", ROOT).getString("Prefix-Message") + message, event.getEntity().getName(), killer, item));
            }
        }
        catch (NullPointerException ex) {}
    }
    /**
     *
     * This method handles player respawns. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        //local vars
        Location spec = null;
        SWArena currentArena = null;
        IngotPlayer selectedPlayer = iplayer.selectPlayer(event.getPlayer().getName(), plugin);
        //check if player is playing ad is alive
        if (selectedPlayer.getIsPlaying() == true && selectedPlayer.getIsAlive() == true) {
            //get arena info
            currentArena = SWArena.selectArena(iplayer.getGame(), plugin);
            try {
                spec = new Location(Bukkit.getWorld(currentArena.getArenaEquivelent().getWorld()), currentArena.getArenaEquivelent().getSpectatorPos()[0],  currentArena.getArenaEquivelent().getSpectatorPos()[1], currentArena.getArenaEquivelent().getSpectatorPos()[2], (float) currentArena.getArenaEquivelent().getSpectatorPos()[3], (float) currentArena.getArenaEquivelent().getSpectatorPos()[4]);
            }
            catch (NullPointerException ex) {}
            //set as dead and inst spectator mode, as well as teleport
            selectedPlayer.setIsAlive(false);
            event.getPlayer().setGameMode(SPECTATOR);
            event.setRespawnLocation(spec);
        }
    }
    /**
     *
     * This method handles player joining. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //files
        File playerdataf = new File(plugin.getDataFolder(), "playerdata.yml");
        FileConfiguration playerdata = FileManager.getCustomData(plugin, "playerdata", ROOT);
        //get player
        Player player = event.getPlayer();
        //create IngotPlayer object
        IngotPlayer newPlayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
        //refresh scoreboard 
        //check if a new player
        if (playerdata.getString(player.getName() + ".uuid") == null) {
            //set data
            playerdata.createSection(player.getName());
            //save to file
            playerdata.set(event.getPlayer().getName() + ".uuid", event.getPlayer().getUniqueId().toString());
            try {
                playerdata.save(playerdataf);
            } 
            catch (IOException ex) {
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(Events.class.getName()).log(Level.SEVERE, "COULD NOT SAVE PLAYERDATA.YML!");
                }
            }
            //create object
            iplayer = IngotPlayer.createPlayer(event.getPlayer().getName(), false, false, false, false, false, false, (byte) 0, (short) (0), (short) (0), (short) (0), (short) (0), (short) (0), "", plugin);
            //cycle throuhg leaderboards
            for (Leaderboard key : Leaderboard.getInstances(plugin)) {
                //check if board is of this plugin
                if (key.getPlugin() == plugin) {
                    //add the new player
                    key.addPlayer(iplayer);
                    key.organizeLeaderboard(true);
                }
            }
        }
        else {
            newPlayer.setUsername(player.getName());
        }
    }
    /**
     *
     * This method handles player leaving. 
     *
     * @param event the event ran
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        //local vars
        Lobby lobby = null;
        Game game = null;
        //refresh scoreboard 
        try {
            //IngotPlayerdata
            iplayer = IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin);
            //check if ingame and not playing
            if (iplayer.getInGame() == true && iplayer.getIsPlaying() == false) {
                //leave lobby
                lobby = Lobby.selectLobby(SWArena.selectArena(iplayer.getGame(), plugin));
                lobby.leaveLobby(IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin), true, config.getBoolean("enable-inventories"));
            }
            //check if playing
            else if (iplayer.getIsPlaying() == true) {
                //leave game
                game = Game.selectGame(SWArena.selectArena(iplayer.getGame(), plugin));
                game.leaveGame(IngotPlayer.selectPlayer(event.getPlayer().getName(), plugin), true, true, config.getBoolean("enable-inventories"));
            }
            //check if spectating
            else if (iplayer.getIsPlaying() == true && event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                event.getPlayer().setGameMode(ADVENTURE);
            }
            iplayer.saveToFile();
        }
        catch (IndexOutOfBoundsException ex) {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                //log error
                Logger.getLogger(Events.class.getName()).log(Level.SEVERE, "COULD NOT PROPERLY KICK " + event.getPlayer().getName() + "FROM THE GAME/LOBBY!!!");
            }
        }
    }
}
