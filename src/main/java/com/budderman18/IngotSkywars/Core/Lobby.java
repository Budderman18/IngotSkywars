package com.budderman18.IngotSkywars.Core;

import com.budderman18.IngotMinigamesAPI.Addons.GameBorder;
import com.budderman18.IngotMinigamesAPI.Core.Data.ArenaStatus;
import com.budderman18.IngotMinigamesAPI.Core.Data.FileManager;
import com.budderman18.IngotMinigamesAPI.Core.Data.IngotPlayer;
import com.budderman18.IngotMinigamesAPI.Core.Data.Spawn;
import com.budderman18.IngotMinigamesAPI.Core.Data.Team;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.BossbarHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.ScoreboardHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TablistHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TimerHandler;
import com.budderman18.IngotMinigamesAPI.Core.Handlers.TitleHandler;
import com.budderman18.IngotSkywars.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * This class handles lobby logic
 * Make one of these for every arena
 * 
 */
public class Lobby {
    //plugin
    private static final Plugin plugin = Main.getInstance();
    //files
    private static final String ROOT = "";
    private static FileConfiguration config = FileManager.getCustomData(plugin, "config", ROOT);
    //lobby vars
    private byte currentPlayers = 0;
    private byte tempPlayers = 0;
    private byte minPlayers = 0;
    private byte skipPlayers = 0;
    private byte maxPlayers = 0;
    private float time = 0;
    private float lastTime = 0;
    private int taskNumberMain = 0;
    private int taskNumberSkip = 0;
    private boolean startedMin = false;
    private boolean startedSkip = false;
    private SWArena arena = null;
    private BossBar bossbar = null;
    private int index = 0;
    private List<IngotPlayer> players = new ArrayList<>();
    private static int trueIndex = 0;
    private static List<Lobby> lobbies = new ArrayList<>();
    /**
     *
     * This constructor sets up a lobby for a given arena
     *
     * @param arenaa the arena to attach to
     */
    public Lobby(SWArena arenaa) {
        //check for valid arena
        if (arenaa != null) {
            //setup lobby vars
            this.arena = arenaa;
            this.minPlayers = this.arena.getArenaEquivelent().getMinPlayers();
            this.skipPlayers = this.arena.getArenaEquivelent().getSkipPlayers();
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
            this.index = trueIndex;
            trueIndex++;
        }
        //add lobby
        lobbies.add(this);
    }
    /**
     * 
     * This method reloads the config file
     * 
     */
    public static void reload() {
        config = FileManager.getCustomData(plugin, "config", ROOT);
    }
    /**
     * 
     * This method updated an existing lobby to a new arena
     * 
     * @param arenaa the arena to set to 
     */
    public void updateArena(SWArena arenaa) {
        //check for valid arena
        if (arenaa != null) {
            //setup lobby vars
            this.arena = arenaa;
            this.minPlayers = this.arena.getArenaEquivelent().getMinPlayers();
            this.skipPlayers = this.arena.getArenaEquivelent().getSkipPlayers();
            this.maxPlayers = this.arena.getArenaEquivelent().getMaxPlayers();
        }
    }
    /**
     * 
     * This method gets thebossbar for the lobby
     * 
     * @return the bossbar
     */
    public BossBar getBossBar() {
        //return selection
        if (lobbies.contains(this)) {
            return lobbies.get(this.index).bossbar;
        }
        //return instance list
        return this.bossbar;
    }
    /**
     * 
     * This method gets the list of players in the lobby
     * 
     * @return the player list
     */
    public List<IngotPlayer> getPlayers() {
        //return selection
        if (lobbies.contains(this)) {
            return lobbies.get(this.index).players;
        }
        //return instance list
        return this.players;
    }
    /**
     * 
     * This method adds a player to the lobby.
     * 
     * @param iplayer the player to join
     * @param useInventory true to use inventories
     */
    public void joinLobby(IngotPlayer iplayer, boolean useInventory) {
        //local vars
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        Location lobbyloc = null;
        ItemStack[] items = new ItemStack[41];
        List<PotionEffect> effects = new ArrayList<>();
        PotionEffect[] trueEffects = null;
        Runnable invclear = null;
        float[] xpp = new float[2];
        float[] hpp = new float[2];
        //lobbies locs are stupid and randomly change themselves, this will stop that
        String lobbyWorld = "";
        double[] lobby = new double[6];
        FileConfiguration arenaData = FileManager.getCustomData(plugin, "settings", this.arena.getArenaEquivelent().getFilePath());
        lobbyWorld = arenaData.getString("Lobby.world");
        lobby[0] = arenaData.getDouble("Lobby.x");
        lobby[1] = arenaData.getDouble("Lobby.y");
        lobby[2] = arenaData.getDouble("Lobby.z");
        lobby[3] = arenaData.getDouble("Lobby.yaw");
        lobby[4] = arenaData.getDouble("Lobby.pitch");
        if (lobby[0] != this.arena.getArenaEquivelent().getLobby()[0] || lobby[1] != this.arena.getArenaEquivelent().getLobby()[1] || lobby[2] != this.arena.getArenaEquivelent().getLobby()[2] || lobby[3] != this.arena.getArenaEquivelent().getLobby()[3] || lobby[4] != this.arena.getArenaEquivelent().getLobby()[4]) {
            this.arena.getArenaEquivelent().setLobbyWorld(lobbyWorld);
            this.arena.getArenaEquivelent().setLobby(lobby);
        }
        lobbyloc = new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getLobbyWorld()), this.arena.getArenaEquivelent().getLobby()[0], this.arena.getArenaEquivelent().getLobby()[1], this.arena.getArenaEquivelent().getLobby()[2], (float) this.arena.getArenaEquivelent().getLobby()[3], (float) this.arena.getArenaEquivelent().getLobby()[4]); 
        //add player
        this.players.add(iplayer);
        this.currentPlayers++;
        this.arena.setCurrentPlayers(this.currentPlayers);
        //update iplayer vars
        iplayer.setInGame(true);
        iplayer.setGame(this.arena.getArenaEquivelent().getName());
        player.teleport(lobbyloc);
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //update bossbar
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
            //TablistHandler.removePlayers(arena);
        }
        //check if titles are enabled
        if (config.getBoolean("Title.enable") == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.Join.title"), config.getString("Title.Join.subtitle"), config.getInt("Title.Join.fadein"), config.getInt("Title.Join.length"), config.getInt("Title.Join.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.Join.actionbar"));
        }
        //store and clear inventory
        for (byte i=0; i < items.length; i++) {
            //check for valid contents
            if (player.getInventory().getContents()[i] != null) {
                //get contents
                items[i] = player.getInventory().getContents()[i];
            }
            else {
                items[i] = new ItemStack(Material.AIR, 1);
            }
        }
        //set inventory
        iplayer.setInventory(items);
        //cycle through effects
        for (PotionEffectType key : PotionEffectType.values()) {
            //add effect
            effects.add(player.getPotionEffect(key));
        }
        trueEffects = new PotionEffect[effects.size()];
        //cycle through effecrs
        for (byte i=0; i < trueEffects.length; i++) {
            //clone ffect
            trueEffects[i] = effects.get(i);
        }
        iplayer.setEffects(trueEffects);
        xpp[0] = player.getExp();
        xpp[1] = player.getLevel();
        hpp[0] = (float) player.getHealth();
        hpp[1] = player.getFoodLevel();
        iplayer.setXP(xpp);
        iplayer.setHealth(hpp);
        if (useInventory == true) {
            iplayer.clearInventory(true, true, true);
        }
        //cycle trhough teams
        for (Team key : this.arena.getArenaEquivelent().getTeams()) {
            //check if team has room
            if (key.getMembers().size() < key.getMaxSize()) {
                //set team
                iplayer.setTeam(key, true);
                break;
            }
        }
        if (this.currentPlayers == 1) {
            //run lobby()
            lobby();
        }
    }
    /**
     * 
     * This method removes a player from the lobby.
     * 
     * @param iplayer the player to remove
     * @param leaveTeam true to leave the team
     * @param useInventory true to manage inventories
     */
    public void leaveLobby(IngotPlayer iplayer, boolean leaveTeam, boolean useInventory) {
        //local vars
        Location exitLoc = new Location(Bukkit.getWorld(this.arena.getArenaEquivelent().getExitWorld()), this.arena.getArenaEquivelent().getExit()[0], this.arena.getArenaEquivelent().getExit()[1], this.arena.getArenaEquivelent().getExit()[2], (float) this.arena.getArenaEquivelent().getExit()[3], (float) this.arena.getArenaEquivelent().getExit()[4]); 
        Player player = Bukkit.getPlayer(iplayer.getUsername());
        Runnable invstore = null;
        //remove player
        this.players.remove(iplayer);
        this.currentPlayers--;
        this.arena.setCurrentPlayers(this.currentPlayers);
        //set iplayer vars
        iplayer.setInGame(false);
        iplayer.setGame(null);
        player.teleport(exitLoc);
        //chek iftablist is enabled
        if (config.getBoolean("Tablist.enable") == true) {
            //reset tablist
            TablistHandler.reset(player);
        }
        //check if scoreboard is enabled
        if (config.getBoolean("Scoreboard.enable") == true) {
            ScoreboardHandler.clearScoreboard(player);
        }
        //check if titles are enabled
        if (config.getBoolean("Title.enable") == true) {
            //set title and actionbar
            TitleHandler.setTitle(player, config.getString("Title.Leave.title"), config.getString("Title.Leave.subtitle"), config.getInt("Title.Leave.fadein"), config.getInt("Title.Leave.length"), config.getInt("Title.Leave.fadeout"));
            TitleHandler.setActionBar(player, config.getString("Title.Leave.actionbar"));
        }
        //check if bossbar is enabled
        if (config.getBoolean("Bossbar.enable") == true) {
            //set bossbar
            BossbarHandler.clearBar(player, this.bossbar);
            BossbarHandler.displayBar(false, this.bossbar);
        }
        //leave team
        if (iplayer.getTeam() != null && leaveTeam == true) {
            iplayer.getTeam().removePlayer(iplayer);
        }
        //restore inventory
        if (useInventory == true) {
            iplayer.applyInventory(true, true, true); 
        }
    }
    /**
     *
     * This method selects a lobby attached to a certain arena
     *
     * @param arenaa the arena to check for
     * @return the lobby selected
     */
    public static Lobby selectLobby(SWArena arenaa) {
        //cycle through all lobbies
        for (Lobby key : lobbies) {
            //check if desired lobby
            if (key.arena == arenaa) {
                return key;
            }
        }
        //log error
        if (config.getBoolean("enable-debug-mode") == true) {
            Logger.getLogger(Lobby.class.getName()).log(Level.SEVERE, "COULD NOT OBTAIN THE LOBBY FOR ARENA " + arenaa.getName() + '!');
        }
        return null;
    }
    /**
     * 
     * This method handles all lobby logic
     * it is always re-ran
     * 
     */
    private void lobby() {
        //local vars
        //run on game start
        Runnable lobbyTimer = () -> {
            //runnable vars
            Player player = null;
            Game game = Game.selectGame(this.arena);
            //check if lobby has 2 players
            if (this.players.size() > 1) {
                //cycle trhough all players
                for (byte i = (byte) ((this.players.size()) - 1); i >= 0; i--) {
                    //get player
                    player = Bukkit.getPlayer(this.players.get(i).getUsername());
                    //check if bossbar is enabled
                    if (config.getBoolean("Bossbar.enable") == true) {
                        //clear bossbar
                        BossbarHandler.clearBar(player, this.bossbar);
                        BossbarHandler.displayBar(false, this.bossbar);
                    }
                    //cycle through all spawns
                    Spawn.moveToRandomSpawn(this.arena.getArenaEquivelent().getSpawns(), player, this.arena.getArenaEquivelent().getWorld());
                    //join game and leave lobby
                    game.joinGame(IngotPlayer.selectPlayer(player.getName(), plugin), false);
                    this.players.remove(this.players.get(i));
                }
                //set arena as active
                this.arena.getArenaEquivelent().setStatus(ArenaStatus.RUNNING);
                //empty lobby vars
                this.currentPlayers = 0;
                this.arena.setCurrentPlayers((byte) 0);    
                this.tempPlayers = 0;
            }
        };
        //updates lobby info
        Runnable action = () -> {
            //runnable vars
            int start = this.arena.getArenaEquivelent().getLobbyWaitTime();
            int skip = this.arena.getArenaEquivelent().getLobbySkipTime();
            int end = 0;
            int xloc = 0;
            int zloc = 0;
            short playerRadius = 0;
            String blankString = " ";
            String lineString = "";
            byte usedTeams = 0;
            Player player = null;
            GameBorder border = null;
            //cycle throguh all players
            for (IngotPlayer key : this.players) {
                try {
                    player = Bukkit.getPlayer(key.getUsername());
                    //check if players have been updated
                    if (this.tempPlayers != this.currentPlayers || (short) this.time != (short) this.lastTime) {
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
                            //reset scoreboard
                            ScoreboardHandler.clearScoreboard(player);
                            ScoreboardHandler.setTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("Scoreboard.title") + ""), config.getBoolean("Scoreboard.importMainScoreboard"));
                            //cycle though scoreboard limit
                            for (byte i = 0; i < config.getInt("Scoreboard.maxLines"); i++) {
                                //check if line is not null
                                if (config.getString("Scoreboard.line" + i) != null) {
                                    //set lines
                                    lineString = config.getString("Scoreboard.line" + i);
                                    lineString = lineString.replaceAll("%currentplayers%", Byte.toString(this.currentPlayers));
                                    lineString = lineString.replaceAll("%aliveplayers%", Byte.toString(this.currentPlayers));
                                    lineString = lineString.replaceAll("%maxplayers%", Byte.toString(this.maxPlayers));
                                    lineString = lineString.replaceAll("%borderSize%", Short.toString(this.arena.getBorderSize()));
                                    lineString = lineString.replaceAll("%playerRadius%", Short.toString(playerRadius));
                                    lineString = lineString.replaceAll("%time%", Byte.toString((byte) this.time));
                                    lineString = lineString.replaceAll("%timeinvert%", Short.toString((short) (this.arena.getArenaEquivelent().getLobbyWaitTime() - this.time)));
                                    ScoreboardHandler.setLine(player, i, ChatColor.translateAlternateColorCodes('&', lineString));
                                } 
                                //run if line is null
                                else {
                                    //add a spaace to null line
                                    blankString = blankString.concat(" ");
                                    //set line
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
                    if (this.tempPlayers != this.currentPlayers) {
                        usedTeams = 1;
                        //cycle through teams
                        for (Team keys : this.arena.getArenaEquivelent().getTeams()) {
                            //check if not valid team size
                            if (keys.getMaxSize() < 1) {
                                //set to 1
                                keys.setMaxSize((byte) 1);
                            }
                            //check if team isnt empty
                            if (!keys.getMembers().isEmpty()) {
                                //marked used
                                usedTeams++;
                            }
                        }
                        //check if endTImer should start
                        if ((this.currentPlayers == this.minPlayers || this.currentPlayers == this.skipPlayers) && this.currentPlayers > 1 && usedTeams > 1) {
                            //cycle through all players
                            for (IngotPlayer keys : this.players) {
                                //check if titles are enabled and title is minplayers
                                if (config.getBoolean("Title.enable") == true && keys.getInGame() == true && this.currentPlayers == this.minPlayers) {
                                    //set title and actionbar
                                    TitleHandler.setTitle(Bukkit.getPlayer(keys.getUsername()), config.getString("Title.Start.title") + this.arena.getArenaEquivelent().getLobbyWaitTime() + " seconds.", config.getString("Title.Start.subtitle"), config.getInt("Title.Start.fadein"), config.getInt("Title.Start.length"), config.getInt("Title.Start.fadeout"));
                                    TitleHandler.setActionBar(Bukkit.getPlayer(keys.getUsername()), config.getString("Title.Start.actionbar"));
                                } 
                                //check if titles are enabled and title is skipplayers
                                else if (config.getBoolean("Title.enable") == true && keys.getInGame() == true && this.currentPlayers == this.skipPlayers) {
                                    //set title and actionbar
                                    TitleHandler.setTitle(Bukkit.getPlayer(keys.getUsername()), config.getString("Title.Start.title") + this.arena.getArenaEquivelent().getLobbySkipTime() + " seconds.", config.getString("Title.Start.subtitle"), config.getInt("Title.Start.fadein"), config.getInt("Title.Start.length"), config.getInt("Title.Start.fadeout"));
                                    TitleHandler.setActionBar(Bukkit.getPlayer(keys.getUsername()), config.getString("Title.Start.actionbar"));
                                }
                            }
                            //check if lobbyTimer needs to run
                            if (this.startedMin == false && this.currentPlayers == this.minPlayers) {
                                //run lobbyTimer
                                this.taskNumberMain = TimerHandler.runTimer(plugin, start, end, lobbyTimer, false, false);
                                this.time = 0;
                                this.startedMin = true;
                            } 
                            //check if skipPlayers is achived
                            if (this.startedSkip == false && this.currentPlayers == this.skipPlayers) {
                                //reset timer
                                TimerHandler.cancelTimer(this.taskNumberMain);
                                this.taskNumberSkip = TimerHandler.runTimer(plugin, skip, end, lobbyTimer, false, false);
                                this.time = this.arena.getLobbyWaitTime() - this.arena.getLobbySkipTime();
                                this.startedSkip = true;
                            } 
                            //check if skipplayers needs to cancel
                            if (this.startedSkip == true && this.currentPlayers == this.minPlayers) {
                                //reset timer
                                TimerHandler.cancelTimer(this.taskNumberSkip);
                                this.taskNumberMain = TimerHandler.runTimer(plugin, start, end, lobbyTimer, false, false);
                                this.time = 0;
                                this.startedSkip = false;
                            } 
                        }
                        //check if minplayers needs to cancel
                        if (this.startedMin == true && this.currentPlayers < this.minPlayers) {
                            //reset timer
                            TimerHandler.cancelTimer(this.taskNumberMain);
                            this.startedMin = false;
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
            //check if tempplayers needs incrementing
            if (this.tempPlayers < this.currentPlayers) {
                this.tempPlayers++;
            }
            //check if tempplayers needs decrementing
            if (this.tempPlayers > this.currentPlayers) {
                this.tempPlayers--;
            }
            //check if time should be reset
            if (this.currentPlayers == 1) {
                this.time = 0;
            }
            //update players and rerun lobby
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            lobby();
        };
        //check if lobby needs to run
        if (this.currentPlayers != 0) {
            TimerHandler.runTimer(plugin, 0, 4, action, true, false);
            this.lastTime = this.time;
            if (this.startedMin == true) {
                this.time += 0.2;
            }
        }
        else {
            //reset lobby vars
            this.currentPlayers = 0;
            this.arena.getArenaEquivelent().setCurrentPlayers(this.currentPlayers);
            this.tempPlayers = 0;
            this.startedMin = false;
            this.startedSkip = false;
            this.players = new ArrayList<>();
            this.bossbar = null;
            this.time = 0;
            this.lastTime = 0;
        }
    }
}
