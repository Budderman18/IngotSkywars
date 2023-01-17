package com.budderman18.IngotSkywars.Core;

import com.budderman18.IngotMinigamesAPI.Core.Data.Arena;
import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotSkywars.Main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * This class handles arena data specific to ingotSkywars
 * It is based off of IMAPI's arena
* 
 */
public class SWArena extends Arena {
    //arena vars
    private String name = "";
    private Plugin plugin = null;
    private int dropStartTime = 0;
    private int dropIntervalTime = 0;
    private int dropEndTime = 0;
    private short borderSize = 0;
    private List<Chest> chests = new ArrayList<>();
    private List<Block> drops = new ArrayList<>();
    private HashMap<Double, Short> borderEvents = new HashMap<>();
    private int index = 0;
    //global vars
    private static Plugin staticPlugin = Main.getInstance();
    private static FileConfiguration config = FileManager.getCustomData(staticPlugin, "config", "");
    private static List<SWArena> arenas = new ArrayList<>();
    private static int trueIndex = 0;
    /**
     * 
     * This constructor blocks new() usage
     * 
     */
    private SWArena() {}
    /**
     *
     * This method creates a new swarena.It will also save the settings file.Use createArenaSchematic() for the region file
     *
     * @param pos1 the negative-most corner of the arena
     * @param pos2 the positive-most corner of the arena
     * @param worldd the world name the arena is in
     * @param arenaName the arena's name
     * @param minPlayerss the minimum amount of players that can play
     * @param skipPlayerss the amount of players needed to shorten the wait timer
     * @param maxPlayerss the max amount of players that can play
     * @param teamSizee the max team size
     * @param lobbyWaitTimee the amount of time the lobby lasts
     * @param gameWaitTimee the amount of time needed for the game to start once entered
     * @param gameLengthh the length of the game
     * @param lobbySkipTimee the amount of time set to when skipPLayers is reached
     * @param lobbyWorldd the world name for the lobby
     * @param filePathh the filePath for the arena's files
     * @param exitWorldd the world name players get moved to when leaving
     * @param exitPoss the position players get moved to when leaving
     * @param centerPoss the center of the arena
     * @param lobbyPoss the position players get teleported to when joining
     * @param statuss the arena status
     * @param dropStart the time supply drops start
     * @param dropInt the time between drops
     * @param dropEnd the time supply drops end
     * @param borderSizee the default size for the worldborder 
     * @param chestss the chests to randomize
     * @param specPoss the position players are teleported to when spectating
     * @param dropss the supply drop locations
     * @param borderChange the events that change the worldborder
     * @param saveFilee weather or not to save a settings file
     * @param permName name of the permission to add
     * @param pluginn the plugin to attach this arena to
     * @return The arena object that was generated
     */
    public static SWArena createArena(int[] pos1, int[] pos2, String worldd, String arenaName, byte minPlayerss, byte skipPlayerss, byte maxPlayerss, byte teamSizee, int lobbyWaitTimee, int lobbySkipTimee, int gameWaitTimee, int gameLengthh, double[] lobbyPoss, String lobbyWorldd, double[] exitPoss, String exitWorldd, double[] specPoss, double[] centerPoss, ArenaStatus statuss, int dropStart, int dropInt, int dropEnd, short borderSizee, List<Chest> chestss, List<Block> dropss, HashMap<Double, Short> borderChange, String filePathh, boolean saveFilee, String permName, Plugin pluginn) {
        //newArena
        Arena extendedArena = null;
        SWArena arena = new SWArena();
        //files
        File arenaDataf = new File(pluginn.getDataFolder() + filePathh, "settings.yml");
        FileConfiguration arenaData = FileManager.getCustomData(pluginn, "settings", filePathh);
        //check if unextended arena needs creation
        if (Arena.selectArena(arenaName, pluginn) == null) {
            //create arena
            extendedArena = Arena.createArena(pos1, pos2, worldd, arenaName, minPlayerss, skipPlayerss, maxPlayerss, teamSizee, lobbyWaitTimee, lobbySkipTimee, gameWaitTimee, gameLengthh, lobbyPoss, lobbyWorldd, exitPoss, exitWorldd, specPoss, centerPoss, filePathh, saveFilee, statuss, permName, pluginn);    
        }
        else {
            //select arena
            extendedArena = Arena.selectArena(arenaName, pluginn);
        }
        //set selection vars
        arena.name = arenaName;
        arena.plugin = pluginn;
        //convert loaded Arena into this SWArena
        arena = SWArena.castToSWArena(extendedArena);
        //set vars
        arena.dropStartTime = dropStart;
        arena.dropIntervalTime = dropInt;
        arena.dropEndTime = dropEnd;
        arena.borderSize = borderSizee;
        arena.chests = chestss;
        arena.drops = dropss;
        arena.borderEvents = borderChange;
        arena.index = trueIndex;
        //check if saving file
        if (saveFilee == true) {
            //drop vars
            arenaData.set("drop-start-time", dropStart);
            arenaData.set("drop-interval-time", dropInt);
            arenaData.set("drop-end-time", dropEnd);
            arenaData.set("border-size", borderSizee);
            //save file
            try {
                arenaData.save(arenaDataf);
            } 
            catch (IOException ex) {
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SAVE SETTINGS.YML FOR ARENA " + arenaName + '!');
                }
            }
        }
        //add and return arena
        arenas.add(arena);
        trueIndex++;
        return arena;
    }
    /**
     *
     * This method deletes the selected swarena. 
     * If you want files deleted, use Arena's delete method first.
     *
     * @param leaveExtention false to not delete the Arena equivelent
     * @param deleteFiles true to delete the file folder
     */
    public void deleteArena(boolean leaveExtention, boolean deleteFiles) {
        //delete unextended arena
        if (leaveExtention == false && deleteFiles == false) {
            Arena.selectArena(this.name, this.plugin).deleteArena(false);
        }
        else if (leaveExtention == false && deleteFiles == true) {
            Arena.selectArena(this.name, this.plugin).deleteArena(true);
        }
        else if (deleteFiles == true) {
            new File(plugin.getDataFolder() + "/" + this.getFilePath()).delete();
        }
        //decrement all higher indexes to prevent bugs
        for (SWArena key : arenas) {
            if (key.index > this.index) {
                arenas.get(key.index).index--;
            }
        }
        //reset data
        arenas.remove(this.index);
        this.borderEvents = null;
        this.chests = null;
        this.drops = null;
        this.dropStartTime = 0;
        this.dropIntervalTime = 0;
        this.dropEndTime = 0;
        this.name = null;
        this.plugin = null;
        this.index = 0;
        trueIndex--;
    }
    /**
     *
     * This method selects and returns a given swarena.
     * Useful for swapping between loaded arenas. 
     *
     * @param namee the name to use when searching
     * @param pluginn the plugin to use when searching
     * @return the swarena that was located
     */
    public static SWArena selectArena(String namee, Plugin pluginn) {
        //cycle between all instances of arena
        for (SWArena key : arenas) {
            //check if arena incstanc e name isn't null
            if (key.getName() != null) {
                //check if arena is what is requested
                if (key.getName().equals(namee) && key.getPlugin() == pluginn) {
                    //set selection data
                    return key;
                }
            }
        }
        //send error and return null
        if (config.getBoolean("enable-debug-mode") == true && (namee != null || !"".equals(namee))) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT LOAD ARENA " + namee + '!');
        }
        return null;
    }
    /**
     * 
     * This method gets all loaded swarena instances
     * Instances with null names are ignored (they shouldn't exist)
     * 
     * @return The list of arenas
     */
    public static List<SWArena> getSWInstances() {
        //local vars
        List<SWArena> arenass = new ArrayList<>();
        //cycle through arenas
        for (SWArena key : arenas) {
            //check if name isnt null
            if (key.name != null) {
                //ass arena
                arenass.add(key);
            }
            else {
                //delete invalid arena
                key.deleteArena(false, false);
            }
        }
        return arenass;
    }
    /**
     * 
     * This method saves the settings files
     * 
     * @param onlyArena false to exclude SWArena specific vars
     */
    public void saveFiles(boolean onlyArena) {
        //local vars
        File arenaDataf = new File(this.plugin.getDataFolder() + this.getArenaEquivelent().getFilePath(), "settings.yml");
        FileConfiguration arenaData = null;
        String time = "";
        short changein = 0;
        short newSize = 0;
        //save arena vars
        Arena.selectArena(this.name, this.plugin).saveFiles();
        //check if using only Arena vars
        if (onlyArena == false) {
            //reload file
            arenaData = FileManager.getCustomData(this.plugin, "settings", this.getArenaEquivelent().getFilePath());
            //reset borderEvents
            arenaData.set("BorderEvents", "");
            try {
                arenaData.save(arenaDataf);
            } 
            catch (IOException ex) {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    //log error
                    Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SAVE SETTINGS.YML FOR ARENA " + this.name + '!');
                }
            }
            arenaData = FileManager.getCustomData(this.plugin, "settings", this.getArenaEquivelent().getFilePath());
            //drop vars
            arenaData.set("drop-start-time", this.dropStartTime);
            arenaData.set("drop-interval-time", this.dropIntervalTime);
            arenaData.set("drop-end-time", this.dropEndTime);
            //border events
            arenaData.set("border-size", this.borderSize);
            //cycle through border events
            if (this.borderEvents != null) {
                for (double key : this.borderEvents.keySet()) {
                    //get vars
                    time = Double.toString(key).split("\\.")[0];
                    changein = Short.parseShort(Double.toString(key).split("\\.")[1]);
                    newSize = this.borderEvents.get(key);
                    //save event
                    arenaData.set("BorderEvents." + time + ".time", changein);
                    arenaData.set("BorderEvents." + time + ".size", newSize);
                }
            }
            //save file
            try {
                arenaData.save(arenaDataf);
            } 
            catch (IOException ex) {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    //log error
                    Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SAVE SETTINGS.YML FOR ARENA " + this.name + '!');
                }
            }
        }
    }
    /**
     * 
     * This method casts an Arena into an SWArena
 You shouldnt need to use this, but if you're having issues with arena
 vars but not swarena vars, try this.
     * 
     * @param arena the swarena to cast
     * @return the arena
     */
    public static SWArena castToSWArena(Arena arena) {
        //local vars
        SWArena newArena = new SWArena();
        int indexx = 0;
        //convert data
        newArena.setPlugin(arena.getPlugin());
        newArena.setName(arena.getName());
        newArena.setPos1(arena.getPos1());
        newArena.setPos2(arena.getPos2());
        newArena.setWorld(arena.getWorld());
        newArena.setMinPlayers(arena.getMinPlayers());
        newArena.setSkipPlayers(arena.getSkipPlayers());
        newArena.setMaxPlayers(arena.getMaxPlayers());
        newArena.setSpawns(arena.getSpawns());
        newArena.setTeams(arena.getTeams());
        newArena.setTeamSize(arena.getTeamSize());
        newArena.setLobby(arena.getLobby());
        newArena.setLobbyWorld(arena.getLobbyWorld());
        newArena.setExit(arena.getExit());
        newArena.setExitWorld(arena.getExitWorld());
        newArena.setCenter(arena.getCenter());
        newArena.setSpectatorPos(arena.getSpectatorPos());
        newArena.setLobbyWaitTime(arena.getLobbyWaitTime());
        newArena.setLobbySkipTime(arena.getLobbySkipTime());
        newArena.setGameWaitTime(arena.getGameWaitTime());
        newArena.setGameLengthTime(arena.getGameLengthTime());
        newArena.setStatus(arena.getStatus());
        newArena.setPermission(arena.getPermission());
        newArena.setCurrentPlayers(arena.getCurrentPlayers());
        newArena.setFilePath(arena.getFilePath());
        //cycle through arenas
        for (Arena key : Arena.getInstances(staticPlugin)) {
            //check if arena matches this one
            if (key.getName().equalsIgnoreCase(arena.getName())) {
                //set index
                newArena.index = indexx;
                break;
            }
            indexx++;
        }
        return newArena;
    }
    /**
     * 
     * This method gets the Arena equivelent of the selected SWArena
 If you're having problems with arena methods, but not swarena methods,
 use this then put the method though this arena
     * 
     * @return the arena equivelent
     */
    public Arena getArenaEquivelent() {
        return Arena.selectArena(this.name, this.plugin);
    }
    /**
     *
     * This method changes the name of the current arena.
     * It will prevent setting to null, which breaks selections
     *
     * @param namee the name to set
     */
    @Override
    public void setName(String namee) {
        //check if name is null
        if (namee != null) {           
            //check for valid arena
            if (Arena.selectArena(this.name, this.plugin) != null) {
                //set unextended plugin
                Arena.selectArena(this.name, this.plugin).setName(namee);
            }
            else {
                //check for debug mode
                if (config.getBoolean("enable-debug-mode") == true) {
                    Logger.getLogger(SWArena.class.getName()).log(Level.SEVERE, "YOU'RE SETTING AN SWARENA FIELD BUT THERE'S NO ARENA EQUIVELENT!");
                }
            }
            //set instance list
            if (arenas.contains(this)) {
                arenas.get(this.index).name = namee;
            }
            //set selection
            this.name = namee;
        }
        else {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                //log error
                Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, "COULD NOT SET THE NAME FOR ARENA " + this.name + '!');
            }
        }
    }
    /**
     *
     * This method gets the current arena's name. 
     *
     * @return the arena name
     */
    @Override
    public String getName() {
        //return instance list
        if (arenas.contains(this)) {
            return arenas.get(this.index).name;
        }
        //return selection as a fallback
        return this.name;
    }
    /**
     *
     * This method adds a chest to the selected arena. 
     *
     * @param chestt the chest object to add
     */
    public void addChest(Chest chestt) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).chests.add(chestt);
        }
        //add selection
        this.chests.add(chestt);
    }
    /**
     *
     * This method deletes the given chest from the selected arena. 
     *
     * @param chestt the chest object to remove
     */
    public void removeChest(Chest chestt) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).chests.remove(chestt);
        }
        //add selection
        this.chests.remove(chestt);
    }
    /**
     *
     * This method sets the chests in the selected arena.
     *
     * @param chestss the chests to set
     */
    public void setChests(List<Chest> chestss) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).chests = chestss;
        }
        //set selection
        this.chests = chestss;
    }
    /**
     *
     * This method gets the chests in the selected arena.
     *
     * @return the chests
     */
    public List<Chest> getChests() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).chests;
        }
        //return selection as a fallback
        return this.chests;
    }
    /**
     *
     * This method adds a drop to the selected arena. 
     *
     * @param chestt the block object to add
     */
    public void addDrop(Block chestt) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).drops.add(chestt);
        }
        //add selection
        this.drops.add(chestt);
    }
    /**
     *
     * This method deletes the given drop from the selected arena. 
     *
     * @param chestt the block object to remove
     */
    public void removeDrop(Block chestt) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).drops.remove(chestt);
        }
        //add selection
        this.drops.remove(chestt);
    }
    /**
     *
     * This method sets the supply drops in the selected arena.
     *
     * @param dropss the drops to set
     */
    public void setDrops(List<Block> dropss) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).drops = dropss;
        }
        //set selection
        this.drops = dropss;
    }
    /**
     *
     * This method gets the supply drops in the selected arena.
     *
     * @return the supply drops
     */
    public List<Block> getDrops() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).drops;
        }
        //return selection as a fallback
        return this.drops;
    }
    /**
     *
     * This method adds a border event to the selected arena. 
     * The time values must be whole numbers. Since HashMaps only allow
     * 2 types, double is used to have 2 time-based numbers, the time to run and the time it takes to change. 
     * Time to run is before the decimal and time to change is after.
     *
     * @param times the time elements to add
     * @param amount the new size
     */
    public void addBorderEvent(double times, short amount) {
        //add instance list
        if (arenas.contains(this)) {
            if (arenas.get(this.index).borderEvents == null) {
                arenas.get(this.index).borderEvents = new HashMap<>();
            }
            arenas.get(this.index).borderEvents.put(times, amount);
        }
        //add selection
        if (this.borderEvents == null) {
            this.borderEvents = new HashMap<>();
        }
        this.borderEvents.put(times, amount);
    }
    /**
     *
     * This method deletes a border event using a time.
     *
     * @param times the event to remove
     */
    public void removeBorderEvent(double times) {
        //add instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).borderEvents.remove(times);
        }
        //add selection
        this.borderEvents.remove(times);
    }
    /**
     *
     * This method sets the chests in the selected arena.
     *
     * @param eventss the events to set
     */
    public void setBorderEvents(HashMap<Double, Short> eventss) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).borderEvents = eventss;
        }
        //set selection
        this.borderEvents = eventss;
    }
    /**
     *
     * This method gets the chests in the selected arena.
     *
     * @return the chests
     */
    public HashMap<Double, Short> getBorderEvents() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).borderEvents;
        }
        //return selection as a fallback
        return this.borderEvents;
    }
    /**
     *
     * This method sets the time supply drops start in the selected arena.
     *
     * @param dropStart the amount to set to
     */
    public void setDropStart(int dropStart) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).dropStartTime = dropStart;
        }
        //set selection
        this.dropStartTime = dropStart;
    }
    /**
     *
     * This method gets the time supply drops start loaded in the selected arena.
     *
     * @return the dropStart amount
     */
    public int getDropStart() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).dropStartTime;
        }
        //return selection as a fallback
        return this.dropStartTime;
    }
    /**
     *
     * This method sets the time supply drops start in the selected arena.
     *
     * @param dropInt the amount to set to
     */
    public void setDropInterval(int dropInt) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).dropIntervalTime = dropInt;
        }
        //set selection
        this.dropIntervalTime = dropInt;
    }
    /**
     *
     * This method gets the time supply drops spawn in the selected arena.
     *
     * @return the dropInterval amount
     */
    public int getDropInterval() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).dropIntervalTime;
        }
        //return selection as a fallback
        return this.dropIntervalTime;
    }
    /**
     *
     * This method sets the time supply drops end in the selected arena.
     *
     * @param dropEnd the amount to set to
     */
    public void setDropEnd(int dropEnd) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).dropEndTime = dropEnd;
        }
        //set selection
        this.dropEndTime = dropEnd;
    }
    /**
     *
     * This method gets the time supply drops end loaded in the selected arena.
     *
     * @return the dropEnd amount
     */
    public int getDropEnd() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).dropEndTime;
        }
        //return selection as a fallback
        return this.dropEndTime;
    }/**
     *
     * This method sets the default border size end in the selected arena.
     *
     * @param borderSizee the amount to set to
     */
    public void setBorderSize(short borderSizee) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).borderSize = borderSizee;
        }
        //set selection
        this.borderSize = borderSizee;
    }
    /**
     *
     * This method gets the time supply drops end loaded in the selected arena.
     *
     * @return the dropEnd amount
     */
    public short getBorderSize() {
        //return instance array
        if (arenas.contains(this)) {
            return arenas.get(this.index).borderSize;
        }
        //return selection as a fallback
        return this.borderSize;
    }
    /**
     *
     * This method sets the plugin for the selected arena. 
     *
     * @param pluginn the plugin to set
     */
    @Override
    public void setPlugin(Plugin pluginn) {
        //set instance list
        if (arenas.contains(this)) {
            arenas.get(this.index).plugin = pluginn;
        }
        //set selection
        this.plugin = pluginn;
        //check fir valid arena
        if (Arena.selectArena(this.name, pluginn) != null) {
            //set unextended plugin
            Arena.selectArena(this.name, this.plugin).setPlugin(pluginn);
        }
        else {
            //check for debug mode
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(SWArena.class.getName()).log(Level.SEVERE, "YOU'RE SETTING AN SWARENA FIELD BUT THERE'S NO ARENA EQUIVELENT!");
            }
        }
    }
    /**
     *
     * This method gets the plugin for the selected arena. 
     *
     * @return the plugin object
     */
    @Override
    public Plugin getPlugin() {
        //return instance list
        if (arenas.contains(this)) {
            return arenas.get(this.index).plugin;
        }
        //return selection as a fallback
        return this.plugin;
    }
}
