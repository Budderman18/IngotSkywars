package com.budderman18.IngotSkywars;

import com.budderman18.IngotMinigamesAPI.Addons.ChestHandler;
import com.budderman18.IngotMinigamesAPI.Addons.GameBorder;
import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Data.LeaderboardType;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.DeathMessageType;
import com.budderman18.IngotSkywars.Bukkit.Events;
import com.budderman18.IngotSkywars.Bukkit.SWAdminCommand;
import com.budderman18.IngotSkywars.Bukkit.SWCommand;
import com.budderman18.IngotSkywars.Core.Game;
import com.budderman18.IngotSkywars.Core.Lobby;
import com.budderman18.IngotSkywars.Core.SWArena;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * This class enables and disables the plugin. 
 * It also imports commands and handles events. 
 * 
 */
public class Main extends JavaPlugin implements Listener { 
    //retrive plugin instance
    private static Main plugin = null;
    //global vars
    private final String ROOT = "";
    private final ConsoleCommandSender sender = getServer().getConsoleSender();
    /**
     *
     * This method retrieves the current plugin data
     *
     * @return the plugin
     */
    public static Main getInstance() {
        return plugin;
    }
    /**
     * 
     * This method loads all kits
     * 
     */
    public static void loadKits() {
        //local vars
        FileConfiguration kit = FileManager.getCustomData(plugin, "kit", "");
        //cycle through kits
        for (String key : kit.getKeys(true)) {
            //check if not on version
            if (!key.equalsIgnoreCase("version")) {
                //check if player has permission
                if (Bukkit.getPluginManager().getPermission(kit.getString(key + ".permission") + "") != null) {
                    //remove perm
                    Bukkit.getPluginManager().removePermission(kit.getString(key + ".permission") + "");
                }
                //add new perm
                Bukkit.getPluginManager().addPermission(new Permission(kit.getString(key + ".permission") + ""));
            }
        }
    }
    /**
     * 
     * This method loads all leaderboards and players
     * 
     */
    public static void loadPlayers() {
        //local vars
        Plugin pluginn = getInstance();
        FileConfiguration config = FileManager.getCustomData(pluginn, "config", "");
        FileConfiguration playerdata = FileManager.getCustomData(pluginn, "playerdata", "");
        FileConfiguration hologram = FileManager.getCustomData(pluginn, "hologram", "");
        Leaderboard board = null;
        List<String> boardsToCheck = new ArrayList<>();
        List<ArmorStand> hologramm = null;
        byte maxSize = (byte) config.getInt("Leaderboard.max-size");
        boolean kills = false;
        boolean deaths = false;
        boolean wins = false;
        boolean losses = false;
        boolean kd = false;
        boolean wl = false;
        boolean score = false;
        //delete all currently loaded players
        for (IngotPlayer key : IngotPlayer.getInstances(plugin)) {
            key.deletePlayer();
        }
        //cycle through file
        for (String key : playerdata.getKeys(false)) {
            //check if not on version
            if (!(key.equalsIgnoreCase("version"))) {
                //add player
                IngotPlayer.createPlayer(key, false, false, false, false, false, false, (byte) 0, (short) (playerdata.getInt(key + ".kills")), (short) (playerdata.getInt(key + ".deaths")), (short) (playerdata.getInt(key + ".wins")), (short) (playerdata.getInt(key + ".losses")), (short) (playerdata.getInt(key + ".score")),  "", plugin);
            }
        }
        //cycle through leaderboards
        for (String key : hologram.getKeys(false)) {
            //check if not on version
            if (!(key.equalsIgnoreCase("version"))) {
                //create leaderboard
                boardsToCheck.add(key);
            }
        }
        //cycle through checked boarfs
        for (String key : boardsToCheck) {
            //get board
            board = Leaderboard.selectBoard(key, plugin);
            //check if board isnt null
            if (board != null) {
                //delete board and get hologram
                hologramm = board.getHologram();
                board.deleteBoard();
            }
            else {
                //create new board
                Leaderboard.createBoard(key, LeaderboardType.getFromString(hologram.getString(key + ".type")), IngotPlayer.getInstances(plugin), hologramm, new Location(Bukkit.getWorld(hologram.getString(key + ".location.world")), hologram.getDouble(key + ".location.x"), hologram.getDouble(key + ".location.y"), hologram.getDouble(key + ".location.z")), (byte) hologram.getInt(key + ".max-size"), Boolean.parseBoolean(hologram.getString(key + ".invert-list")), Boolean.parseBoolean(hologram.getString(key + ".summoned")), plugin);
            }
        }
        //cycle through current leaderboards
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            //check for kill
            if (key.getName().equalsIgnoreCase("kills")) {
                kills = true;
            }
            //check for death
            else if (key.getName().equalsIgnoreCase("deaths")) {
                deaths = true;
            }
            //check for win
            else if (key.getName().equalsIgnoreCase("wins")) {
                wins = true;
            }
            //check for loss
            else if (key.getName().equalsIgnoreCase("losses")) {
                losses = true;
            }
            //check for kill/death
            else if (key.getName().equalsIgnoreCase("kdratio")) {
                kd = true;
            }
            //check for win/loss
            else if (key.getName().equalsIgnoreCase("wlratio")) {
                wl = true;
            }
            //check for score
            else if (key.getName().equalsIgnoreCase("score")) {
                score = true;
            }
        }
        //cycle through leaderboards
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            try {
                //check if there is at least 1 player
                if (key.getPlayers().get(0).getUsername() != null) {
                    //organize board
                    key.organizeLeaderboard(true);
                    //check if summoned
                    if (key.getSummoned() == true) {
                        //summon hologram
                        key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
                    }
                }
            }
            catch (IndexOutOfBoundsException ex) {}
        }
        //check for kills
        if (kills == false) {
            //set board to kills
            board = Leaderboard.createBoard("kills", LeaderboardType.KILLS, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for deaths
        if (deaths == false) {
            //set board to deaths
            board = Leaderboard.createBoard("deaths", LeaderboardType.DEATHS, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for wins
        if (wins == false) {
            //set board to wins
            board = Leaderboard.createBoard("wins", LeaderboardType.WINS, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for losses
        if (losses == false) {
            //set board to losses
            board = Leaderboard.createBoard("losses", LeaderboardType.LOSSES, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for kd
        if (kd == false) {
            //set board to kd
            board = Leaderboard.createBoard("kdratio", LeaderboardType.KDRATIO, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for wl
        if (wl == false) {
            //set board to wl
            board = Leaderboard.createBoard("wlratio", LeaderboardType.WLRATIO, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
        //check for score
        if (score == false) {
            //set to board
            board = Leaderboard.createBoard("score", LeaderboardType.SCORE, IngotPlayer.getInstances(plugin), null, null, maxSize, false, false, plugin);
            board.organizeLeaderboard(true);
        }
    }
    /**
     * 
     * This method loads in all the death messages
     * 
     */
    public static void loadDeathMessages() {
        //local vars
        FileConfiguration language = FileManager.getCustomData(plugin, "language", "");
        //add messages
        DeathMessageHandler.replaceMessage(language.getString("Death-Sword-Message"), DeathMessageType.SWORD, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Axe-Message"), DeathMessageType.AXE, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Arrow-Message"), DeathMessageType.ARROW, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Firework-Message"), DeathMessageType.FIREWORK, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Trident-Message"), DeathMessageType.TRIDENT, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Potion-Message"), DeathMessageType.POTION, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Hand-Message"), DeathMessageType.HAND, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Fall-Message"), DeathMessageType.FALL, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Explosion-Message"), DeathMessageType.EXPLOSION, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Crushed-Message"), DeathMessageType.CRUSHED, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Fire-Message"), DeathMessageType.FIRE, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Lava-Message"), DeathMessageType.LAVA, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Drown-Message"), DeathMessageType.DROWN, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Sufficate-Message"), DeathMessageType.SUFFICATE, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Border-Message"), DeathMessageType.BORDER, plugin);
        DeathMessageHandler.replaceMessage(language.getString("Death-Other-Message"), DeathMessageType.OTHER, plugin);
    }
    /**
     *
     * This method loads in all arenas from the arenas folder 
     * Useful for startup and reloading
     *
     */
    public static void loadArenas() {
        //files
        FileConfiguration arenaData = null;
        File loadArenas = new File(plugin.getDataFolder() + "/Arenas/");
        File temparenaf = null;
        //arena
        SWArena temparena = null;
        List<Chest> chests = null;
        List<Block> drops = new ArrayList<>();
        Lobby lobbyy = null;
        Game gamee = null;
        int[] pos1 = new int[3];
        int[] pos2 = new int[3];
        String world = null;
        String name = null;
        byte minPlayers = 0;
        byte skipPlayers = 0;
        byte maxPlayers = 0;
        byte teamSize = 0;
        byte loopSize = 0;
        int lobbyWaitTime = 0;
        int lobbySkipTime = 0;
        int gameWaitTime = 0;
        int gameLengthTime = 0;
        ArenaStatus status = null;
        int dropStart = 0;
        int dropInterval = 0;
        int dropEnd = 0;
        short borderSize = 0;
        double times = 0;
        boolean useVanilla = false;
        //Spawn
        Spawn tempspawn = null;
        List<String> currentSpawn = null;
        String namee = null;
        double x = 0;
        double y = 0;
        double z = 0;
        double yaw = 0;
        double pitch = 0;
        //positions
        double[] lobby = new double[6];
        String lobbyWorld = null;
        double[] exit = new double[6];
        String exitWorld = null;
        double[] spec = new double[6];
        double[] center = new double[6];
        //language
        FileConfiguration language = FileManager.getCustomData(plugin, "language", "");
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message") + ""); 
        String arenaLoadedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Arena-Loaded-Message") + ""); 
        List<Team> teams = new ArrayList<>();
        //cycle through arena directory
        for (String key : loadArenas.list()) {
            //get file
            temparenaf = new File(plugin.getDataFolder() + "/Arenas/" + key + '/', "settings.yml");
            arenaData = FileManager.getCustomData(plugin, "settings", "/Arenas/" + key + '/');
            //check if arena has a settings file
            if (temparenaf.exists()) {
                //reset arrays
                pos1 = new int[3];
                pos2 = new int[3];
                lobby = new double[6];
                exit = new double[6];
                center = new double[6];
                spec = new double[6];
                //get positions
                pos1[0] = arenaData.getInt("pos1.x");
                pos1[1] = arenaData.getInt("pos1.y");
                pos1[2] = arenaData.getInt("pos1.z");
                pos2[0] = arenaData.getInt("pos2.x");
                pos2[1] = arenaData.getInt("pos2.y");
                pos2[2] = arenaData.getInt("pos2.z");
                lobbyWorld = arenaData.getString("Lobby.world");
                lobby[0] = arenaData.getDouble("Lobby.x");
                lobby[1] = arenaData.getDouble("Lobby.y");
                lobby[2] = arenaData.getDouble("Lobby.z");
                lobby[3] = arenaData.getDouble("Lobby.yaw");
                lobby[4] = arenaData.getDouble("Lobby.pitch");
                exitWorld = arenaData.getString("Exit.world");
                exit[0] = arenaData.getDouble("Exit.x");
                exit[1] = arenaData.getDouble("Exit.y");
                exit[2] = arenaData.getDouble("Exit.z");
                exit[3] = arenaData.getDouble("Exit.yaw");
                exit[4] = arenaData.getDouble("Exit.pitch");
                spec[0] = arenaData.getDouble("Spec.x");
                spec[1] = arenaData.getDouble("Spec.y");
                spec[2] = arenaData.getDouble("Spec.z");
                spec[3] = arenaData.getDouble("Spec.yaw");
                spec[4] = arenaData.getDouble("Spec.pitch");
                center[0] = arenaData.getDouble("Center.x");
                center[1] = arenaData.getDouble("Center.y");
                center[2] = arenaData.getDouble("Center.z");
                center[3] = arenaData.getDouble("Center.yaw");
                center[4] = arenaData.getDouble("Center.pitch");
                //get world
                world = arenaData.getString("world");
                //get name
                name = arenaData.getString("name");
                //get player vars
                minPlayers = (byte) arenaData.getInt("minPlayers");
                skipPlayers = (byte) arenaData.getInt("skipPlayers");
                maxPlayers = (byte) arenaData.getInt("maxPlayers");
                teamSize = (byte) arenaData.getInt("teamSize");
                //get timer vars
                lobbyWaitTime = arenaData.getInt("lobby-wait-time");
                lobbySkipTime = arenaData.getInt("lobby-skip-time");
                gameWaitTime = arenaData.getInt("game-wait-time");
                gameLengthTime = arenaData.getInt("game-length-time");
                //get drop vars
                dropStart = arenaData.getInt("drop-start-time");
                dropInterval = arenaData.getInt("drop-interval-time");
                dropEnd = arenaData.getInt("drop-end-time");
                borderSize = (short) arenaData.getInt("border-size");
                status = ArenaStatus.getFromString(arenaData.getString("status"));
                //check for perm
                if (Bukkit.getPluginManager().getPermission("ingotsw.arenas." + name) != null) {
                    Bukkit.getPluginManager().removePermission("ingotsw.arenas." + name);
                }
                //create arena
                temparena = SWArena.createArena(pos1, pos2, world, name, minPlayers, skipPlayers, maxPlayers, teamSize, lobbyWaitTime, lobbySkipTime, gameWaitTime, gameLengthTime, lobby, lobbyWorld, exit, exitWorld, spec, center, status, dropStart, dropInterval, dropEnd, borderSize, null, null, null, "/Arenas/" + key + '/', true, "ingotsw.arenas." + name, plugin);
                //set lobby again
                temparena.setLobby(lobby);
                temparena.setLobbyWorld(lobbyWorld);
                //setup chests
                chests = ChestHandler.chestsToRandomize(temparena.getArenaEquivelent());
                temparena.setChests(chests);
                for (Chest keys : chests) {
                    if (keys.getBlockInventory().getItem(0) != null) {
                        if (keys.getBlockInventory().getItem(0).getType().toString().equalsIgnoreCase(FileManager.getCustomData(plugin, "chest", "").getString("SUPPLY_DROP.material"))) {
                            drops.add(keys.getBlock());
                        }
                    }
                }
                temparena.setDrops(drops);
                //cycle spawns
                for (short i = 1; i < 65534; i++) {
                    //load from file
                    currentSpawn = new ArrayList<>();
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".name"));
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".x"));
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".y"));
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".z"));
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".yaw"));
                    currentSpawn.add(arenaData.getString("Spawnpoints.Spawn" + Short.toString(i) + ".pitch"));
                    if (currentSpawn.get(0) != null) {
                        //set name
                        namee = currentSpawn.get(0);
                        //set position
                        x = Double.parseDouble(currentSpawn.get(1));
                        y = Double.parseDouble(currentSpawn.get(2));
                        z = Double.parseDouble(currentSpawn.get(3));
                        yaw = Double.parseDouble(currentSpawn.get(4));
                        pitch = Double.parseDouble(currentSpawn.get(5));
                        //create spawn
                        tempspawn = Spawn.createSpawn(namee, x, y, z, yaw, pitch, plugin);
                        temparena.getArenaEquivelent().addSpawn(tempspawn);
                    }
                    else {
                        break;
                    }
                }
                //load teams
                if (teamSize != 0) {
                    loopSize = (byte) (maxPlayers/teamSize);
                }
                else {
                    loopSize = maxPlayers;
                }
                teams.clear();
                for (byte i=0; i < loopSize; i++) {
                    teams.add(Team.createTeam(temparena.getArenaEquivelent().getName() + "_Team" + (i+1), 0, 0, 0, 0, 0, 0, null, teamSize, false,false, false, plugin));
                }
                temparena.getArenaEquivelent().setTeams(teams);
                //load border
                useVanilla = false;
                if (FileManager.getCustomData(plugin, "config", "").getBoolean("use-vanilla-worldborder") == true) {
                    useVanilla = true;
                }
                GameBorder.createBorder(temparena.getArenaEquivelent().getName(), temparena.getArenaEquivelent().getPos2()[0]-temparena.getArenaEquivelent().getCenter()[0], new Location(Bukkit.getWorld(temparena.getArenaEquivelent().getWorld()), temparena.getArenaEquivelent().getCenter()[0], temparena.getArenaEquivelent().getCenter()[1], temparena.getArenaEquivelent().getCenter()[2]), EntityType.SNOWBALL, 10, 2, (short) (10), 1, temparena.getArenaEquivelent(), useVanilla, plugin);
                //cycle through arena file
                for (String keys : arenaData.getKeys(true)) {
                    //check for valid borderevent data
                    if (keys.contains("BorderEvents.") && !keys.contains(".time") && !keys.contains(".size")) {
                        //add border event
                        times = Double.parseDouble(keys.replace("BorderEvents.", ""));
                        times += Double.parseDouble("0.".concat(arenaData.getString(keys + ".time")));
                        temparena.addBorderEvent(times, (short) arenaData.getInt(keys + ".size"));
                    }
                }
                //load lobby + game
                lobbyy = new Lobby(temparena);
                gamee = new Game(temparena);
                //notify arena is loaded
                Bukkit.getServer().getConsoleSender().sendMessage(prefixMessage + arenaLoadedMessage + temparena.getArenaEquivelent().getName());
            }
        }
    }
    /**
     *
     * This method creates files if needed. 
     * Only needed if file is missing (first usage). 
     *
     */
    public static void createFiles() {
        //config file
        File configf = new File(plugin.getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
         }
        //config object
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } 
        catch (IOException | InvalidConfigurationException e) {}
        //langauge file
        File languagef = new File(plugin.getDataFolder(), "language.yml");
        if (!languagef.exists()) {
            languagef.getParentFile().mkdirs();
            plugin.saveResource("language.yml", false);
         }
        //langauge object
        FileConfiguration language = new YamlConfiguration();
        try {
            language.load(languagef);
        } 
        catch (IOException | InvalidConfigurationException e) {}
        //playerdata
        File playerdataf = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!playerdataf.exists()) {
            playerdataf.getParentFile().mkdirs();
            plugin.saveResource("playerdata.yml", false);
         }
        //pd object
        FileConfiguration pd = new YamlConfiguration();
        try {
            pd.load(playerdataf);
        } 
        catch (IOException | InvalidConfigurationException e) {}
        //hologram
        File hologramf = new File(plugin.getDataFolder(), "hologram.yml");
        if (!hologramf.exists()) {
            hologramf.getParentFile().mkdirs();
            plugin.saveResource("hologram.yml", false);
         }
        //hologram object
        FileConfiguration hologram = new YamlConfiguration();
        try {
            hologram.load(playerdataf);
        } 
        catch (IOException | InvalidConfigurationException e) {}
        //chest
        File chestf = new File(plugin.getDataFolder(), "chest.yml");
        if (!chestf.exists()) {
            chestf.getParentFile().mkdirs();
            plugin.saveResource("chest.yml", false);
        }
        //chest object
        FileConfiguration chest = new YamlConfiguration();
        try {
            chest.load(chestf);
        }
        catch (IOException | InvalidConfigurationException e) {}
        //arena folder
        File arenaFolder = new File(plugin.getDataFolder(), "/Arenas/");
        if (!arenaFolder.exists()) {
            arenaFolder.mkdirs();
        }
        //chest
        File kitf = new File(plugin.getDataFolder(), "kit.yml");
        if (!kitf.exists()) {
            kitf.getParentFile().mkdirs();
            plugin.saveResource("kit.yml", false);
        }
        //chest object
        FileConfiguration kit = new YamlConfiguration();
        try {
            kit.load(kitf);
        }
        catch (IOException | InvalidConfigurationException e) {}
    } 
    /**
     *
     * Enables the plugin. 
     * Checks if MC version isn't the latest. 
     * If its not, warn the player about lacking support 
     * Checks if server is running offline mode 
     * If it is, disable the plugin 
     * Also loads commands and events
     * Also loads in arenas and spawns
     *
     */
    @Override
    public void onEnable() {
        //create plugin instance
        plugin = this;
        //creates files if needed
        createFiles();
        //language variables
        FileConfiguration language = FileManager.getCustomData(plugin,"language",ROOT);
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message")); 
        String unsupportedVersionAMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionA-Message")); 
        String unsupportedVersionBMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionB-Message")); 
        String unsupportedVersionCMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsupported-VersionC-Message")); 
        String unsecureServerAMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerA-Message")); 
        String unsecureServerBMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerB-Message")); 
        String unsecureServerCMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Unsecure-ServerC-Message")); 
        String pluginEnabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Plugin-Enabled-Message")); 
        //check for correct version
        if (!(Bukkit.getVersion().contains("1.19.3"))) {
            sender.sendMessage(prefixMessage + unsupportedVersionAMessage);
            sender.sendMessage(prefixMessage + unsupportedVersionBMessage);
            sender.sendMessage(prefixMessage + unsupportedVersionCMessage); 
        }
        //check for online mode and not bungee mode
        if (getServer().getOnlineMode() == false && FileManager.getCustomData(null, "spigot", ROOT).getBoolean("settings.bungeecord") == false) {
            sender.sendMessage(prefixMessage + unsecureServerAMessage);
            sender.sendMessage(prefixMessage + unsecureServerBMessage);
            sender.sendMessage(prefixMessage + unsecureServerCMessage);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //commands
        this.getCommand("sw").setExecutor(new SWCommand());
        this.getCommand("swadmin").setExecutor(new SWAdminCommand());
        //events
        getServer().getPluginManager().registerEvents(new Events(),this);
        //load arenas
        loadArenas();
        //load death messages
        loadDeathMessages();
        //load players+holograms
        loadPlayers();
        //load kits
        loadKits();
        //enable plugin
        getServer().getPluginManager().enablePlugin(this);
        sender.sendMessage(prefixMessage + pluginEnabledMessage);
    }
    /**
     *
     * This method disables the plugin. 
     * It also saves all files and 
     * clears all loaded player data. 
     *
     */
    @Override
    public void onDisable() {
        plugin = this;
        //import files
        File configf = new File(plugin.getDataFolder(),"config.yml");
        File languagef = new File(plugin.getDataFolder(),"language.yml");
        File playerdataf = new File(plugin.getDataFolder(),"playerdata.yml");
        File hologramf = new File(plugin.getDataFolder(),"hologram.yml");
        FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
        FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
        FileConfiguration pd = FileManager.getCustomData(plugin, "playerdata", ROOT);
        FileConfiguration hologram = FileManager.getCustomData(plugin, "hologram", ROOT);
        FileConfiguration chest = FileManager.getCustomData(plugin, "chest", ROOT);
        //language
        String prefixMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Prefix-Message")); 
        String pluginDisabledMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Plugin-Disabled-Message")); 
        //local vars
        IngotPlayer currentIPlayer = null;
        //reset arena chests
        for (SWArena key : SWArena.getSWInstances()) {
            if (key.getChests() != null) {
                ChestHandler.resetChests(key.getChests(), chest);
            }
        }
        //reset iplayer data
        for (Player key : Bukkit.getOnlinePlayers()) {
            try {
                //get iplayerdata
                currentIPlayer = IngotPlayer.selectPlayer(key.getName(), plugin);
                //reset vars
                currentIPlayer.deletePlayer();
            }
            catch (IndexOutOfBoundsException ex) {}
        }
        //save holograms
        for (Leaderboard key : Leaderboard.getInstances(plugin)) {
            key.saveToFile(false);
            key.killHologram(false);
        }
        //saves files
        try {
            config.save(configf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE CONFIG.YML!");
            }
        }
        try {
            language.save(languagef);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE LANGUAGE.YML!");
            }
        }
        try {
            pd.save(playerdataf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE PLAYERDATA.YML");
            }
        }
        try {
            hologram.save(hologramf);
        } 
        catch (IOException ex) {
            if (config.getBoolean("enable-debug-mode") == true) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "COULD NOT SAVE HOLOGRAM.YML");
            }
        }
        //disables plugin
        getServer().getPluginManager().disablePlugin(this);
        sender.sendMessage(prefixMessage + pluginDisabledMessage);
    }
}