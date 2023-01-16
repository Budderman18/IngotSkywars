package com.budderman18.IngotSkywars.Core;

import com.budderman18.IngotMinigamesAPI.Addons.ChestAnimation;
import com.budderman18.IngotMinigamesAPI.Addons.ChestHandler;
import com.budderman18.IngotMinigamesAPI.Addons.GameBorder;
import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Leaderboard;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.BossbarHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ChatHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ScoreboardHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TablistHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TimerHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TitleHandler;
import com.budderman18.IngotSkywars.Main;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * This class handles game logic
 * Make one of these for every arena
 *
 */
public class Game {
    //plugin
    private static final Plugin plugin = Main.getInstance();
    //files
    private static final String ROOT = "";
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
    private static FileConfiguration language = FileManager.getCustomData(plugin, "language", ROOT);
    private static FileConfiguration chestconfig = FileManager.getCustomData(plugin, "chest", ROOT);
    //lobby vars
    private byte currentPlayers = 0;
    private byte tempPlayers = 0;
    private byte alivePlayers = 0;
    private byte maxPlayers = 0;
    private int taskNumber = 0;
    private float time = 0;
    private float lastTime = 0;
    private byte dropAmount = 0;
    private boolean stopDrops = false;
    private double barSize = 1;
    private boolean gameWaiting = false;
    private boolean gameStarted = false;
    private SWArena arena = null;
    private BossBar bossbar = null;
    private int index = 0;
    private List<IngotPlayer> players = new ArrayList<>();
    private HashMap<Double, Boolean> checkedEvents = new HashMap<>();
    private static int trueIndex = 0;
    private static List<Game> games = new ArrayList<>();
    //language
    private static String gameWinnerMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Winner-Message")  + "");
    private static String gameDrawMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Draw-Message")  + "");
    private static String gameDropSpawnedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Drop-Spawned-Message")  + "");
    private static String gameBorderMessage1 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-1")  + "");
    private static String gameBorderMessage2 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-2")  + "");
    private static String gameBorderMessage3 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-3")  + "");
    /**
     *
     * This constructor sets up a game instance for an arena
     *
     * @param arenaa the arena to attach to
     */
    public Game(SWArena arenaa) {
        //check if arena isnt null
        if (arenaa != null) {
            //set data
            this.arena = arenaa;
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
            this.index = trueIndex;
            trueIndex++;
            //cycle though events
            if (this.arena.getBorderEvents() != null) {
                for (double key : this.arena.getBorderEvents().keySet()) {
                    //add event
                    this.checkedEvents.put(key, false);
                }
            }
        }
        //add game
        games.add(this);
    }
    //this method spawns a supply drop
    private void spawnDrop(Random random, List<Chest> drops, List<Chest> trueDrops) {
        //local vars
        int x = 0;
        int y = 0;
        int z = 0;
        Chest drop = null;
        byte infLoop = 0;
        //cycle through drops
        for (Block key : this.arena.getDrops()) {
            //set to air
            key.setType(Material.CHEST);
            trueDrops.add((Chest) key.getState());
        }
        //run if drop is in the border
        drops.clear();
        while (drops.isEmpty() && infLoop < 126) {
            //get drop
            drop = (Chest) this.arena.getDrops().get(random.nextInt(0, this.arena.getDrops().size())).getState();
            //check if drop is inside the border
            if (GameBorder.selectBorder(this.arena.getName(), plugin).isInBorder(drop.getLocation()) == true) {
                //add random supply drop
                drops.add(drop);
            }
            infLoop++;
        }
        //check if drop sis empty (no drops in the border)
        if (!drops.isEmpty()) {
            //cycle through drops
            for (Chest key : drops) {
                //set item to supply drop item and reset
                key.getBlockInventory().setItem(0, new ItemStack(Material.getMaterial(this.chestconfig.getString("SUPPLY_DROP.material").toUpperCase())));
                ChestHandler.resetChests(trueDrops, this.chestconfig);
                //cycle trhough drops
                for (Block keys : this.arena.getDrops()) {
                    //set to air
                    keys.setType(Material.AIR);
                }
                //check if animation is random
                if (this.chestconfig.getString("SUPPLY_DROP.animation").equalsIgnoreCase("random")) {
                    //animate randomly
                    ChestHandler.animateChest(key.getLocation(), ChestAnimation.values()[random.nextInt(0, 4)], chestconfig);
                } 
                else {
                    //anaimate chest
                    ChestHandler.animateChest(key.getBlock().getLocation(), ChestAnimation.getFromString(this.chestconfig.getString("SUPPLY_DROP.animation")), chestconfig);
                }
                //set pos vars
                x = key.getLocation().getBlockX();
                y = key.getLocation().getBlockY();
                z = key.getLocation().getBlockZ();
            }
            //tell players
            ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, gameDropSpawnedMessage + x + ", " + y + ", " + z, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
            this.dropAmount++;
        }
    }
    /**
     * 
     * This method reloads the config file
     * 
     */
    public static void reload() {
        config = FileManager.getCustomData(plugin, "config", ROOT);
        language = FileManager.getCustomData(plugin, "language", ROOT);
        chestconfig = FileManager.getCustomData(plugin, "chest", ROOT);
        //messages
        gameWinnerMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Winner-Message")  + "");
        gameDrawMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Draw-Message")  + "");
        gameDropSpawnedMessage = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Drop-Spawned-Message")  + "");
        gameBorderMessage1 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-1")  + "");
        gameBorderMessage2 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-2")  + "");
        gameBorderMessage3 = ChatColor.translateAlternateColorCodes('&', language.getString("Game-Border-Message-3")  + "");
    }
    /**
     * 
     * This method updated an existing lobby to a new arena
     * 
     * @param arenaa the arena to set to 
     */
    public void updateArena(SWArena arenaa) {
        //check if arena isnt null
        if (arenaa != null) {
            //set data
            this.arena = arenaa;
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
            //cycle though events
            if (this.arena.getBorderEvents() != null) {
                for (double key : this.arena.getBorderEvents().keySet()) {
                    //add event
                    this.checkedEvents.put(key, false);
                }
            }
        }
    }
    /**
     * 
     * This method gets the bossbar for the game
     * 
     * @return the bossbar
     */
    public BossBar getBossBar() {
        //return selection
        if (games.contains(this)) {
            return games.get(this.index).bossbar;
        }
        //return instance list
        return this.bossbar;
    }
    /**
     * 
     * This method gets the list of players in the game
     * 
     * @return the player list
     */
    public List<IngotPlayer> getPlayers() {
        //return selection
        if (games.contains(this)) {
            return games.get(this.index).players;
        }
        //return instance list
        return this.players;
    }
    /**
     * 
     * This method adds a player to the game.
     * 
     * @param iplayer the player to add
     * @param useTeam true to add to team
     */
    public void joinGame(IngotPlayer iplayer, boolean useTeam) {
        //local vars
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        Runnable invclear = null;
        //add player
        this.players.add(iplayer);
        this.currentPlayers++;
        this.arena.setCurrentPlayers(this.currentPlayers);
        this.alivePlayers++;
        //set iplayer vars
        iplayer.setInGame(true);
        iplayer.setIsPlaying(true);
        iplayer.setIsAlive(true);
        iplayer.setGame(this.arena.getArenaEquivelent().getName());
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //set bossbar
            this.bossbar = BossbarHandler.setBarTitle(player, config.getString("Bossbar.title"), this.bossbar);
            BossbarHandler.setBarColor(player, config.getString("Bossbar.color"), this.bossbar);
            BossbarHandler.setBarSize(player, 1, this.bossbar);
            BossbarHandler.displayBar(true, this.bossbar);
        }
        //check if tablist is enabled
        if (config.getBoolean("Tablist.enable") == true) {
            //set header and footer
            TablistHandler.setHeader(player, config.getString("Tablist.header"));
            TablistHandler.setFooter(player, config.getString("Tablist.footer"));
        }
        //check if titles are enabled
        if (config.getBoolean("Title.enable") == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.InGameStart.title"), config.getString("Title.InGameStart.subtitle"), config.getInt("Title.InGameStart.fadein"), config.getInt("Title.InGameStart.length"), config.getInt("Title.InGameStart.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.InGameStart.actionbar"));
        }
        //join team
        if (useTeam == true) {
            //cycle through teams
            for (Team key : this.arena.getArenaEquivelent().getTeams()) {
                //check if team has room
                if (key.getMembers().size() != key.getMaxSize()) {
                    //add to team
                    key.addPlayer(iplayer);
                }
            }
        }
        //clear inventory and run game()
        invclear = () -> {
            iplayer.clearInventory(true, true, true);
        };
        TimerHandler.runTimer(plugin, 0, 1, invclear, false, false);
        if (this.currentPlayers == 1) {
            game();
        }
    }
    /**
     * 
     * This method removes a player from the game.
     * 
     * @param iplayer the player to remove
     * @param displayTitle true to display titles
     * @param addLoss weather or not to add a loss to the player
     * @param useInventory true to manage inventories
     */
    public void leaveGame(IngotPlayer iplayer, boolean displayTitle, boolean addLoss, boolean useInventory) {
        //local vars
        Location exitLoc = new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getExitWorld()), this.arena.getArenaEquivelent().getExit()[0], this.arena.getArenaEquivelent().getExit()[1], this.arena.getArenaEquivelent().getExit()[2], (float) this.arena.getArenaEquivelent().getExit()[3], (float) this.arena.getArenaEquivelent().getExit()[4]); 
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        Runnable invstore = null;
        short losses = iplayer.getLosses();
        short score = iplayer.getScore();
        //remove player
        iplayer.setGameKills((byte) 0);
        this.players.remove(iplayer);
        this.currentPlayers--;
        this.arena.setCurrentPlayers(this.currentPlayers);
        this.alivePlayers--;
        //check if player isnt alive
        if (iplayer.getIsAlive() == false) {
            //back to adventure
            player.setGameMode(GameMode.ADVENTURE);
        }
        //set iplayer vars
        iplayer.setInGame(false);
        iplayer.setIsPlaying(false);
        iplayer.setIsFrozen(false);
        iplayer.setIsAlive(true);
        //check if adding a loss
        if (addLoss == true) {
            //add loss and set new score
            losses++;
            score += config.getInt("Score.loss");
            iplayer.setLosses(losses);
            iplayer.setScore(score);
        }
        //set game
        iplayer.setGame(null);
        //teleport to exit
        player.teleport(exitLoc);
        //check if tablist is enabled
        if (config.getBoolean("Tablist.enable") == true) {
            //reset tablist
            TablistHandler.reset(player);
        }
        //check if scoreboard is enabled
        if (config.getBoolean("Scoreboard.enable") == true) {
            //clear scoreboard
            ScoreboardHandler.clearScoreboard(player);
        }
        //check if title is enabled
        if (config.getBoolean("Title.enable") == true && displayTitle == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.Leave.title"), config.getString("Title.Leave.subtitle"), config.getInt("Title.Leave.fadein"), config.getInt("Title.Leave.length"), config.getInt("Title.Leave.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.Leave.actionbar"));
        }
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //clear bossbar
            BossbarHandler.clearBar(player, this.bossbar);
            BossbarHandler.displayBar(false, this.bossbar);
        }
        //leave team
        if (iplayer.getTeam() != null) {
            iplayer.getTeam().removePlayer(iplayer);
        }
        if (useInventory == true) {
            iplayer.applyInventory(true, true, true);
        }
    }
    /**
     *
     * This method selects a game based of of its arena
     *
     * @param arenaa the arena to obtain its game from
     * @return the game object
     */
    public static Game selectGame(SWArena arenaa) {
        //cycle through games
        for (Game key : games) {
            //check if desired arena then return
            if (key.arena == arenaa) {
                return key;
            }
        }
        //log error
        if (config.getBoolean("enable-debug-mode") == true) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT OBTAIN THE GAME FOR ARENA " + arenaa.getName() + '!');
        }
        return null;
    }
    /**
     *
     * This method runs all game logic
     * It is automatically re-ran
     * 
     */
    private void game() {
        //local vars
        Runnable graceTimer = () -> {
            //runnable vars
            Player player = null;
            //cycle through all players
            for (IngotPlayer key : this.players) {
                //get player
                player = Bukkit.getPlayer(key.getUsername());
                //check if player has grace period
                if (key.getIsGraced() == true) {
                    //cancel grace
                    key.setIsGraced(false);
                    //cycle through all effects
                    for (PotionEffectType keys : PotionEffectType.values()) {
                        //remove effect
                        player.removePotionEffect(keys);
                    }
                }
            }
        };
        //run this at the end
        Runnable endTimer = () -> {
            //local vars
            Player player = null;
            List<IngotPlayer> tempPlayerss = this.players;
            byte lastKills = 0;
            byte kills = 0;
            short wins = 0;
            short score = 0;
            List<Chest> drops = new ArrayList<>();
            List<Block> blockDrop = new ArrayList<>();
            Chest drop = null;
            String message = "";
            IngotPlayer keyy = null;
            IngotPlayer winner = null;
            boolean tieChecked = false;
            //cycle through all players
            for (IngotPlayer key : this.players) {
                try {
                    //get player
                    player = Bukkit.getPlayer(key.getUsername());
                    //check if player isnt alive
                    if (key.getIsAlive() == false) {
                        player.setGameMode(GameMode.ADVENTURE);
                    }
                    //check if title is enabled
                    if (config.getBoolean("Title.enable") == true) {
                        //set title and actionbar
                        TitleHandler.setTitle(player, config.getString("Title.End.title"), config.getString("Title.End.subtitle"), config.getInt("Title.End.fadein"), config.getInt("Title.End.length"), config.getInt("Title.End.fadeout"));
                        TitleHandler.setActionBar(player, config.getString("Title.End.actionbar"));
                    }
                } 
                catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    //check for debug mode
                    if (config.getBoolean("enable-debug-mode") == true) {
                        //log error
                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT RETRIVE PLAYER " + key.getUsername() + '!');
                    }
                }
            }
            //check if tiebreaker or winner is needed
            if (tieChecked == false && this.alivePlayers > 1) {
                //cycle though players
                for (byte i=0; i < this.players.size(); i++) {
                    //get player
                    keyy = this.players.get(i);
                    //check if player is alive
                    if (keyy.getIsAlive() == true) {
                        //get kills
                        kills = keyy.getGameKills();
                    }
                    //check if this player has more kills than the last
                    if (kills > lastKills) {
                        //set winner
                        winner = keyy;
                    }
                    lastKills = kills;
                }
                //check if winner isnt null
                if (winner != null) {
                    //set message
                    message = "&6" + winner.getUsername() + gameWinnerMessage;
                    //update wins and score
                    wins = winner.getWins();
                    score = winner.getScore();
                    wins++;
                    score += config.getInt("Score.win");
                    winner.setWins(wins);
                    winner.setScore(score);
                    //sent message and stop timers
                    ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                    TimerHandler.cancelTimer(this.taskNumber);
                }
                //run if a tie
                else {
                    //set message
                    message = gameDrawMessage;
                    //send message and cancel timer
                    ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                    TimerHandler.cancelTimer(this.taskNumber);
                }
                tieChecked = true;
            }
            //check if not tied
            else if (tieChecked == false && this.alivePlayers <= 1) {
                //cycle though players
                for (IngotPlayer key : this.players) {
                    //check if player is alive
                    if (key.getIsAlive() == true) {
                        winner = key;
                        //set message
                        message = "&6" + winner.getUsername() + gameWinnerMessage;
                        //update wins and score
                        wins = winner.getWins();
                        score = winner.getScore();
                        wins++;
                        score += config.getInt("Score.win");
                        winner.setWins(wins);
                        winner.setScore(score);
                        //sent message and stop timers
                        ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                        TimerHandler.cancelTimer(this.taskNumber);
                        tieChecked = true;
                    }
                }
            }
            //reset chests
            ChestHandler.resetChests(this.arena.getChests(), this.chestconfig);
            //cycle through drops
            for (Block key : this.arena.getDrops()) {
                key.getLocation().getBlock().setType(Material.CHEST);
                //reset chest
                drop = (Chest) key.getState();
                drop.setLock(this.chestconfig.getString("SUPPLY_DROP.material").toUpperCase());
                drop.getBlockInventory().setItem(0, new ItemStack(Material.getMaterial(chestconfig.getString("SUPPLY_DROP.material").toUpperCase())));
                drops.add(drop);
                blockDrop.add(key);
            }
            this.arena.setDrops(blockDrop);
            ChestHandler.resetChests(drops, this.chestconfig);
            drops.clear();
            //cycle through all players
            for (byte i = (byte) (this.players.size()-1); i >= 0; i--) {
                //force leave
                //check if player is dead
                if (this.players.get(i).getIsAlive() == false) {
                    //force leave
                    this.leaveGame(this.players.get(i), false, true, config.getBoolean("enable-inventories"));
                }
                //run to force winner to leave
                else {
                    this.leaveGame(this.players.get(i), true, false, config.getBoolean("enable-inventories"));
                }
            }
            //cancel all animations
            try {
                for (Chest key : ChestHandler.getCurrentlyAnimatingChests()) {
                    if (this.arena.getArenaEquivelent().isInArena(key.getLocation()) == true) {
                        ChestHandler.cancelAnimation(key);
                    }
                }
            }
            catch (ConcurrentModificationException ex) {}
            //set arena as inactive
            this.arena.getArenaEquivelent().setStatus(ArenaStatus.WAITING);
            this.barSize = 1.0;
            this.currentPlayers = 0;
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.players.clear();
            this.tempPlayers = 0;
            this.alivePlayers = 0;
            //update leaderboards
            for (Leaderboard key : Leaderboard.getInstances()) {
                //update leaderboard
                key.setPlayers(IngotPlayer.getInstances());
                key.organizeLeaderboard(true);
                key.killHologram(false);
                key.summonHologram(config.getString("Leaderboard.header"), config.getString("Leaderboard.format"), config.getString("Leaderboard.footer"), true);
            }
        };
        //run for active game logic
        Runnable startTimer = () -> {
            //local vars
            int start = this.arena.getArenaEquivelent().getGameLengthTime();
            int end = 0;
            Player player = null;
            //check fi border wasnt reset
            if (GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).getRadius() != this.arena.getBorderSize()) {
                GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).setRadius(this.arena.getBorderSize());
            }
            //cycle through all players
            for (IngotPlayer key : this.players) {
                try {
                    //check if game hasnt started
                    if (this.gameStarted == false) {
                        //cycle through all players
                        for (IngotPlayer keys : this.players) {
                            //unfreeze player
                            player = Bukkit.getPlayer(keys.getUsername());
                            keys.setIsFrozen(false);
                            //check if titles are enabled
                            if (config.getBoolean("Title.enable") == true) {
                                //set title and actionbar
                                TitleHandler.setTitle(player, config.getString("Title.InGameRelease.title"), config.getString("Title.InGameRelease.subtitle"), config.getInt("Title.InGameRelease.fadein"), config.getInt("Title.InGameRelease.length"), config.getInt("Title.InGameRelease.fadeout"));
                                TitleHandler.setActionBar(player, config.getString("Title.InGameRelease.actionbar"));
                            }
                            //check for grace perios
                            if (config.getBoolean("grace-period.enable") == true) {
                                //set gracerd
                                keys.setIsGraced(true);
                                //cycle through effects
                                for (String keyss : config.getStringList("grace-period.effects")) {
                                    //add efect
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(keyss.split(" ")[0].toUpperCase()), config.getInt("grace-period.length") * 20, Integer.parseInt(keyss.split(" ")[1])));
                                }
                                //run grace expire timer
                                TimerHandler.runTimer(plugin, config.getInt("grace-period.length"), end, graceTimer, false, false);
                            }
                        }
                        //clear spawns
                        for (Spawn keys : Spawn.getInstances()) {
                            //set occupied
                            keys.setIsOccupied(false);
                        }
                        //set as started
                        this.gameStarted = true;
                        this.taskNumber = TimerHandler.runTimer(plugin, start, end, endTimer, false, false);
                        //fill chests
                        ChestHandler.fillChests(this.arena.getChests(), chestconfig);
                        //re-add drops
                        for (Block keys : this.arena.getDrops()) {
                            keys.setType(Material.AIR);
                        }
                    }
                } 
                catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    //check for debug mode
                    if (config.getBoolean("enable-debug-mode") == true) {
                        //log error
                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT RETRIVE PLAYER " + key.getUsername() + '!');
                    }
                }
            }
        };
        //run for waiting logic
        Runnable action = () -> {
            //runnable vars
            int startTime = this.arena.getArenaEquivelent().getGameWaitTime();
            int start = this.arena.getArenaEquivelent().getGameLengthTime();
            int end = 0;
            short wins = 0;
            short score = 0;
            short timeToChange = 0;
            short newSize = 0;
            GameBorder border = null;
            int xloc = 0;
            int zloc = 0;
            short playerRadius = 0;
            String message = "";
            Player player = null;
            String blankString = " ";
            String lineString = null;
            Chest drop = null;
            List<Chest> drops = new ArrayList<>();
            List<Chest> trueDrops = new ArrayList<>();
            Random random = new Random();
            //cycle through players
            for (IngotPlayer key : this.players) {
                try {
                    //get player
                    player = Bukkit.getPlayer(key.getUsername());
                    //check if not equal to the players alive or the time has changed
                    if (this.tempPlayers != this.alivePlayers || (short) this.time != (short) this.lastTime) {
                        //check if scoreboards are enabled
                        if (config.getBoolean("Scoreboard.enable") == true) {
                            //get border
                            border = GameBorder.selectBorder(this.arena.getName(), plugin);
                            //get xpos
                            xloc = player.getLocation().getBlockX() - border.getCenter().getBlockX();
                            //check for absolute value
                            if (xloc < 0) {
                                xloc*=-1;
                            }
                            //get zpos
                            zloc = player.getLocation().getBlockZ() - border.getCenter().getBlockZ();
                            //check for absolute value
                            if (zloc < 0) {
                                zloc*=-1;
                            }
                            //check if x is greater than Z
                            if (xloc >= zloc) {
                                //ser radius
                                playerRadius = (short) (player.getLocation().getBlockX() - border.getCenter().getBlockX());
                            }
                            else if (xloc < zloc) {
                                //ser radius
                                playerRadius = (short) (player.getLocation().getBlockZ() - border.getCenter().getBlockZ());
                            }
                            //check for absolute value
                            if (playerRadius < 0) {
                                playerRadius*=-1;
                            }
                            //clear scoreboard
                            ScoreboardHandler.clearScoreboard(player);
                            ScoreboardHandler.setTitle(player, ChatColor.translateAlternateColorCodes('&', "&7[&6IngotSurvivalGames&7]"), config.getBoolean("Scoreboard.importMainScoreboard"));
                            //cycle though scoreboard limit
                            for (byte i = 0; i < config.getInt("Scoreboard.maxLines"); i++) {
                                //check if line is not null
                                if (config.getString("Scoreboard.line" + i) != null) {
                                    //set lines
                                    lineString = config.getString("Scoreboard.line" + i);
                                    lineString = lineString.replaceAll("%currentplayers%", Byte.toString(this.currentPlayers));
                                    lineString = lineString.replaceAll("%aliveplayers%", Byte.toString(this.currentPlayers));
                                    lineString = lineString.replaceAll("%maxplayers%", Byte.toString(this.maxPlayers));
                                    lineString = lineString.replaceAll("%borderSize%", Short.toString((short) GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), this.plugin).getRadius()));
                                    lineString = lineString.replaceAll("%playerRadius%", Short.toString(playerRadius));
                                    lineString = lineString.replaceAll("%time%", Short.toString((short) this.time));
                                    lineString = lineString.replaceAll("%timeinvert%", Short.toString((short) (this.arena.getArenaEquivelent().getGameLengthTime() - this.time)));
                                    ScoreboardHandler.setLine(player, i, ChatColor.translateAlternateColorCodes('&', lineString));
                                } 
                                //run if line is null
                                else {
                                    //add a spaace to null line
                                    blankString = blankString.concat(" ");
                                    //set line.hj89ngy
                                    ScoreboardHandler.setLine(player, i, blankString);
                                }
                            }
                            if (config.getBoolean("Scoreboard.importMainScoreboard") == true) {
                                ScoreboardHandler.updateScoreboard();
                            }
                            lineString = "";
                            blankString = " ";
                        }
                    }
                    if (this.tempPlayers != this.alivePlayers) {
                        //check if game is no longer eaiting
                        if (this.gameWaiting == false) {
                            //cycle through all players
                            for (IngotPlayer keys : this.players) {
                                //freeze player
                                player = Bukkit.getPlayer(keys.getUsername());
                                keys.setIsFrozen(true);
                                //check if titles are enabled
                                if (config.getBoolean("Title.enable")) {
                                    //set title and actionbar
                                    TitleHandler.setTitle(player, config.getString("Title.InGameStart.title") + startTime + " seconds", config.getString("Title.InGameStart.subtitle"), config.getInt("Title.InGameStart.fadein"), config.getInt("Title.InGameStart.length"), config.getInt("Title.InGameStart.fadeout"));
                                    TitleHandler.setActionBar(player, config.getString("Title.InGameStart.actionbar"));
                                }
                            }
                            //run timer and initiate wait
                            TimerHandler.runTimer(plugin, startTime, end, startTimer, false, false);
                            this.gameWaiting = true;
                        }
                    }
                } 
                catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    //check for debug mode
                    if (config.getBoolean("enable-debug-mode") == true) {
                        //log error
                        Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT RETRIVE PLAYER " + key.getUsername() + '!');
                    }
                }
                //check if player needs to be attacked
                if (GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).isInBorder(player.getLocation()) == false && key.getIsAttacked() == false && this.arena.getBorderSize() > 0) {
                    //get border and attack player
                    GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).attackPlayer(player, false);
                    key.setIsAttacked(true);
                }
                //check if player no longer needs to be attacked
                if (GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).isInBorder(player.getLocation()) == true && key.getIsAttacked() == true) {
                    //stop attacking
                    key.setIsAttacked(false);
                }
            }
            //check if supply drop needed
            if ((int) this.time >= this.arena.getDropStart() && (int) this.time <= this.arena.getDropEnd() && this.arena.getDropStart() != this.arena.getDropEnd()) {
                //check fi there is a supply drop
                if (!this.arena.getDrops().isEmpty()) {
                    //check if on starting drop
                    trueDrops.clear();
                    if ((int) this.time == this.arena.getDropStart() && this.dropAmount == 0) {
                        spawnDrop(random, drops, trueDrops);
                    }
                    //check for oither drops
                    else if ((int) this.dropAmount == ((int) (this.time - this.arena.getDropStart()) / this.arena.getDropInterval()) && this.time <= this.arena.getDropEnd() && this.arena.getDropStart() != this.arena.getDropEnd()) {
                        spawnDrop(random, drops, trueDrops);
                    }
                    //check if on ending drop
                    else if ((int) this.time == this.arena.getDropEnd() && this.stopDrops == false && this.arena.getDropStart() != this.arena.getDropEnd()) {
                       spawnDrop(random, drops, trueDrops);   
                    }
                    drops.clear();
                }
            }
            //check if borderEvent is needed
            for (double key : this.arena.getBorderEvents().keySet()) {
                //check if event should be ran and isnt checked yet
                if ((int) key == (int) this.time && this.checkedEvents.get(key) == false) {
                    //get data
                    timeToChange = Short.parseShort(Double.toString(key).split("\\.")[1]);
                    newSize = this.arena.getBorderEvents().get(key);
                    message = gameBorderMessage1 + newSize + gameBorderMessage2 + timeToChange + gameBorderMessage3;
                    //send message and run event
                    ChatHandler.sendMessageToAll(ChatHandler.convertMessage(null, message, config.getString("Chat.format")), true, true, this.arena.getArenaEquivelent(), plugin);
                    GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), this.plugin).modifyBorderSize(newSize, (short) (timeToChange*20));
                    this.checkedEvents.put(key, true);
                }
            }
            //check if only 1 survivor or out of time
            if (this.alivePlayers == 1 || (short) this.time >= this.arena.getArenaEquivelent().getGameLengthTime()) {
                endTimer.run(); 
            }
            //check if bossbar is enabled
            if (this.gameStarted == true && config.getBoolean("Bossbar.enable") == true) {
                //get size
                this.barSize -= (((double) 1 / 5) / start);
                //check for invalid bossbar sixe
                if (this.barSize < 0) {
                    this.barSize = 0;
                }
                //cycle through all players
                for (IngotPlayer key : this.players) {
                    //set bossbar size
                    player = Bukkit.getPlayer(key.getUsername());
                    BossbarHandler.setBarSize(player, this.barSize, this.bossbar);
                }
            }
            //check if playercount needs increasing
            if (this.tempPlayers < this.currentPlayers || this.tempPlayers < this.alivePlayers) {
                this.tempPlayers++;
            }
            //check if playercount needs decreasing
            if (this.tempPlayers > this.currentPlayers || this.tempPlayers > this.alivePlayers) {
                this.tempPlayers--;
            }
            //cycle through all players
            for (IngotPlayer key : players) {
                //check if player has died
                if (key.getIsAlive() == false && key.getIsPlaying() == true) {
                    //update alive count
                    this.alivePlayers--;
                    key.setIsPlaying(false);
                }
            }
            //set players and re-run game
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.lastTime = time;
            this.time+=0.2;
            game();
        };
        //check if game needs to re-run
        if (this.currentPlayers != 0) {
            TimerHandler.runTimer(plugin, 0, 4, action, true, false);
        }
        else {
            //reset game instance
            this.currentPlayers = 0;
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            GameBorder.selectBorder(this.arena.getArenaEquivelent().getName(), plugin).setRadius(this.arena.getBorderSize());
            this.tempPlayers = 0;
            this.alivePlayers = 0;
            this.time = 0;
            this.dropAmount = 0;
            this.stopDrops = false;
            this.barSize = 1;
            this.gameStarted = false;
            this.gameWaiting =  false;
            this.players = new ArrayList<>();
            this.bossbar = null;
            for (double key : this.arena.getBorderEvents().keySet()) {
                this.checkedEvents.put(key, false);
            }
            //check if arena needs regenerating
            if (config.getBoolean("regenerate-arena-after-finishing") == true) {
                this.arena.getArenaEquivelent().loadArenaSchematic(true, true, true, true);
                ChestHandler.resetChests(this.arena.getChests(), chestconfig);
            }
            else {
                try {
                    this.arena.getArenaEquivelent().loadArenaSchematic(false, false, false, true);
                }
                catch (NullPointerException ex) {}
            }
        }    
    }
}
