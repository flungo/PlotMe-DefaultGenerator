package com.worldcretornica.plotme_defaultgenerator;

import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_GeneratorManager;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.AUCTION_WALL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.BASE_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.FILL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.FOR_SALE_WALL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.GROUND_LEVEL;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PATH_WIDTH;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PLOT_FLOOR_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PLOT_SIZE;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PROTECTED_WALL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.ROAD_ALT_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.ROAD_MAIN_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.WALL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.X_TRANSLATION;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.Z_TRANSLATION;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import me.flungo.bukkit.plotme.abstractgenerator.AbstractGenerator;
import me.flungo.bukkit.plotme.abstractgenerator.WorldGenConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;

public class PlotMe_DefaultGenerator extends AbstractGenerator {

    public static final String CORE_OLD_CONFIG = "config-old.yml";
    public static final String DEFAULT_WORLD = "plotsworld";

    public String PREFIX;

    public String language;

    private Boolean advancedlogging;

    private HashMap<String, String> captions;

    private GenPlotManager genPlotManager;

    @Override
    public void takedown() {
        captions = null;
        genPlotManager = null;
        setAdvancedLogging(null);
        PREFIX = null;
    }

    public GenPlotManager getGenPlotManager() {
        return genPlotManager;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldname, String id) {
        String map = worldname.toLowerCase();
        return new PlotGen(this, genPlotManager.getWGC(map));
    }

    public void importOldConfigs() {
        // Get the old config file
        final File oldConfigFile = new File(getCoreFolder(), CORE_OLD_CONFIG);

        // If it doesn't exist there is nothing to import
        if (!oldConfigFile.exists()) {
            return;
        }

        // Load the config from the file and get the worlds config section
        final FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
        final ConfigurationSection oldWorldsCS = oldConfig.getConfigurationSection(WORLDS_CONFIG_SECTION);

        // If there are no worlds then there is nothing to import
        if (oldWorldsCS == null || oldWorldsCS.getKeys(false).isEmpty()) {
            return;
        }

        // Get the local worlds config section
        final ConfigurationSection worldsCS = getConfig().getConfigurationSection(WORLDS_CONFIG_SECTION);

        // Create a mapping from oldConfig to config
        final Map<String, String> mapping = new HashMap<String, String>();

        mapping.put("PlotSize", PLOT_SIZE.path);
        mapping.put("XTranslation", X_TRANSLATION.path);
        mapping.put("ZTranslation", Z_TRANSLATION.path);
        mapping.put("RoadHeight", GROUND_LEVEL.path);
        mapping.put("BottomBlockId", BASE_BLOCK.path);
        mapping.put("PlotFillingBlockId", FILL_BLOCK.path);
        mapping.put("PathWidth", PATH_WIDTH.path);
        mapping.put("PlotFloorBlockId", PLOT_FLOOR_BLOCK.path);
        mapping.put("RoadMainBlockId", ROAD_MAIN_BLOCK.path);
        mapping.put("RoadStripeBlockId", ROAD_ALT_BLOCK.path);
        mapping.put("WallBlockId", WALL_BLOCK.path);
        mapping.put("ProtectedWallBlockId", PROTECTED_WALL_BLOCK.path);
        mapping.put("AuctionWallBlockId", AUCTION_WALL_BLOCK.path);
        mapping.put("ForSaleWallBlockId", FOR_SALE_WALL_BLOCK.path);

        // Import each world
        for (String worldname : oldWorldsCS.getKeys(false)) {
            ConfigurationSection oldWorldCS = oldWorldsCS.getConfigurationSection(worldname);

            // Get the local config world section and create it if it doesn't exist
            ConfigurationSection worldCS = worldsCS.getConfigurationSection(worldname);
            if (worldCS == null) {
                worldCS = worldsCS.createSection(worldname);
            }

            // For each path import config and rename where required.
            for (String path : oldWorldCS.getKeys(true)) {
                if (mapping.containsKey(path)) {
                    String newPath = mapping.get(path);
                    if (worldCS.contains(newPath)) {
                        if (worldCS.get(newPath).equals(oldWorldCS.get(path))) {
                            // Great no work to do except deleting from the old config
                            oldWorldCS.set(path, null);
                        } else {
                            // Can't migrate the path
                            String fullPathBase = oldWorldCS.getCurrentPath();
                            getLogger().log(Level.WARNING,
                                    "Could not migrate '{0}.{1}' from {2} to '{0}.{3}' in {4}{5}: Path exists in desitnation. Please merge manually." + DEFAULT_CONFIG_NAME,
                                    new Object[]{fullPathBase, path, oldConfigFile, newPath, getConfigFolder(), File.separator});
                        }
                    } else {
                        // Migrate!
                        worldCS.set(path, oldWorldCS.get(path));
                        oldWorldCS.set(path, null);
                    }
                }
            }
        }

        // Save the configs
        saveConfig();

        try {
            oldConfig.save(oldConfigFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + oldConfigFile, ex);
        }
    }

    @Override
    public void initialize() {
        PREFIX = ChatColor.BLUE + "[" + getName() + "] " + ChatColor.RESET;
        genPlotManager = new GenPlotManager(this);

        // Setup PluginListener
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PluginListener(this), this);

        // Set defaults for WorldGenConfig
        for (DefaultWorldConfigPath wcp : DefaultWorldConfigPath.values()) {
            WorldGenConfig.putDefault(wcp);
        }

        // Override defaults from AbstarctWorldConfigPath
        WorldGenConfig.putDefault(PLOT_SIZE, 32);

        // If no world are defined in our config, define a sample world for the user to be able to copy.
        if (!getConfig().contains(WORLDS_CONFIG_SECTION)) {
            // Get the config for an imaginary gridplots so that the config is generated.
            getWorldGenConfig(DEFAULT_WORLD);
            saveConfig();
        }

        ConfigurationSection worlds = getConfig().getConfigurationSection(WORLDS_CONFIG_SECTION);

        for (String worldname : worlds.getKeys(false)) {
            // Get config for world
            WorldGenConfig wgc = getWorldGenConfig(worldname);

            // Validate config
            if (wgc.getInt(GROUND_LEVEL) > 250) {
                getLogger().severe(PREFIX + "RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 250.");
                wgc.set(GROUND_LEVEL, 250);
            }

            genPlotManager.putWGC(worldname.toLowerCase(), wgc);
        }

        saveConfig();
    }

    public short getBlockId(String block) {
        if (block.indexOf(":") > 0) {
            return Short.parseShort(block.split(":")[0]);
        } else {
            return Short.parseShort(block);
        }
    }

    public byte getBlockValue(String block) {
        if (block.indexOf(":") > 0) {
            return Byte.parseByte(block.split(":")[1]);
        } else {
            return 0;
        }
    }

    public String caption(String s) {
        if (captions.containsKey(s)) {
            return addColor(captions.get(s));
        } else {
            getLogger().warning("[" + getName() + "] Missing caption: " + s);
            return "ERROR:Missing caption '" + s + "'";
        }
    }

    public String addColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public Boolean getAdvancedLogging() {
        return advancedlogging;
    }

    private void setAdvancedLogging(Boolean advancedlogging) {
        this.advancedlogging = advancedlogging;
    }

    @Override
    public IPlotMe_GeneratorManager getGeneratorManager() {
        return genPlotManager;
    }
}
