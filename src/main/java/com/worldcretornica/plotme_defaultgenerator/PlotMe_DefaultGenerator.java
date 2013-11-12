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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import me.flungo.bukkit.plotme.abstractgenerator.AbstractGenerator;
import me.flungo.bukkit.plotme.abstractgenerator.WorldGenConfig;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;

public class PlotMe_DefaultGenerator extends AbstractGenerator {

    public static final String DEFAULT_WORLD = "plotsworld";

    public String PREFIX;

    public String language;

    private String configpath;
    private Boolean advancedlogging;

    private HashMap<String, String> captions;

    private GenPlotManager genPlotManager;

    @Override
    public void takedown() {
        captions = null;
        genPlotManager = null;
        setAdvancedLogging(null);
        setConfigPath(null);
        PREFIX = null;
    }

    public GenPlotManager getGenPlotManager() {
        return genPlotManager;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldname, String id) {
        String map = worldname.toLowerCase();
        if (genPlotManager.genplotmaps.containsKey(map)) {
            return new PlotGen(this, genPlotManager.getMap(map));
        } else {
            return new PlotGen(this);
        }
    }

    private void importOldConfigs(File newfile) {
        File oldfile;

        oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.yml");

        if (!oldfile.exists()) {
            oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.backup.yml");
        }

        if (oldfile.exists()) {
            getLogger().info(PREFIX + "Importing old configurations");
            FileConfiguration oldconfig = new YamlConfiguration();
            FileConfiguration newconfig = new YamlConfiguration();

            try {
                oldconfig.load(oldfile);
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                getLogger().severe(PREFIX + "can't read old configuration file");
                e.printStackTrace();
                return;
            } catch (InvalidConfigurationException e) {
                getLogger().severe(PREFIX + "invalid old configuration format");
                e.printStackTrace();
                return;
            }

            ConfigurationSection oldworlds;
            ConfigurationSection newworlds;

            if (!oldconfig.contains("worlds")) {
                return;
            } else {
                oldworlds = oldconfig.getConfigurationSection("worlds");
            }

            if (!newconfig.contains("worlds")) {
                newworlds = newconfig.createSection("worlds");
            } else {
                newworlds = newconfig.getConfigurationSection("worlds");
            }

            for (String worldname : oldworlds.getKeys(false)) {
                ConfigurationSection oldcurrworld = oldworlds.getConfigurationSection(worldname);
                ConfigurationSection newcurrworld;

                if (newworlds.contains("worldname")) {
                    newcurrworld = newworlds.getConfigurationSection(worldname);
                } else {
                    newcurrworld = newworlds.createSection(worldname);
                }

                newcurrworld.set("PathWidth", oldcurrworld.getInt("PathWidth", 7));
                newcurrworld.set("PlotSize", oldcurrworld.getInt("PlotSize", 32));

                newcurrworld.set("XTranslation", oldcurrworld.getInt("XTranslation", 0));
                newcurrworld.set("ZTranslation", oldcurrworld.getInt("ZTranslation", 0));

                newcurrworld.set("BottomBlockId", oldcurrworld.getString("BottomBlockId", "7:0"));
                newcurrworld.set("WallBlockId", oldcurrworld.getString("WallBlockId", "44:0"));
                newcurrworld.set("PlotFloorBlockId", oldcurrworld.getString("PlotFloorBlockId", "2:0"));
                newcurrworld.set("PlotFillingBlockId", oldcurrworld.getString("PlotFillingBlockId", "3:0"));
                newcurrworld.set("RoadMainBlockId", oldcurrworld.getString("RoadMainBlockId", "5:0"));
                newcurrworld.set("RoadStripeBlockId", oldcurrworld.getString("RoadStripeBlockId", "5:2"));

                newcurrworld.set("RoadHeight", oldcurrworld.getInt("RoadHeight", 64));

                newcurrworld.set("ProtectedWallBlockId", oldcurrworld.getString("ProtectedWallBlockId", "44:4"));
                newcurrworld.set("ForSaleWallBlockId", oldcurrworld.getString("ForSaleWallBlockId", "44:1"));
                newcurrworld.set("AuctionWallBlockId", oldcurrworld.getString("AuctionWallBlockId", "44:1"));

                newworlds.set(worldname, newcurrworld);

            }

            newconfig.set("worlds", newworlds);

            try {
                newconfig.save(newfile);

                if (!oldfile.getName().contains("config.backup.yml")) {
                    oldfile.renameTo(new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.backup.yml"));
                }
            } catch (IOException e) {
                getLogger().severe(PREFIX + "error writting configurations");
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void initialize() {
        PREFIX = ChatColor.BLUE + "[" + getName() + "] " + ChatColor.RESET;
        genPlotManager = new GenPlotManager(this);

        File configfile = new File(getConfigPath(), "config.yml");

        if (!configfile.exists()) {
            importOldConfigs(configfile);
        }

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

            GenMapInfo tempPlotInfo = new GenMapInfo();

            tempPlotInfo.PathWidth = wgc.getInt(PATH_WIDTH);
            tempPlotInfo.PlotSize = wgc.getInt(PLOT_SIZE);

            tempPlotInfo.XTranslation = wgc.getInt(X_TRANSLATION);
            tempPlotInfo.ZTranslation = wgc.getInt(Z_TRANSLATION);

            tempPlotInfo.BottomBlockId = wgc.getBlockRepresentation(BASE_BLOCK).getId();
            tempPlotInfo.BottomBlockValue = wgc.getBlockRepresentation(BASE_BLOCK).getData();
            tempPlotInfo.WallBlockId = wgc.getBlockRepresentation(WALL_BLOCK).getId();
            tempPlotInfo.WallBlockValue = wgc.getBlockRepresentation(WALL_BLOCK).getData();
            tempPlotInfo.PlotFloorBlockId = wgc.getBlockRepresentation(PLOT_FLOOR_BLOCK).getId();
            tempPlotInfo.PlotFloorBlockValue = wgc.getBlockRepresentation(PLOT_FLOOR_BLOCK).getData();
            tempPlotInfo.PlotFillingBlockId = wgc.getBlockRepresentation(FILL_BLOCK).getId();
            tempPlotInfo.PlotFillingBlockValue = wgc.getBlockRepresentation(FILL_BLOCK).getData();
            tempPlotInfo.RoadMainBlockId = wgc.getBlockRepresentation(ROAD_MAIN_BLOCK).getId();
            tempPlotInfo.RoadMainBlockValue = wgc.getBlockRepresentation(ROAD_MAIN_BLOCK).getData();
            tempPlotInfo.RoadStripeBlockId = wgc.getBlockRepresentation(ROAD_ALT_BLOCK).getId();
            tempPlotInfo.RoadStripeBlockValue = wgc.getBlockRepresentation(ROAD_ALT_BLOCK).getData();

            tempPlotInfo.RoadHeight = wgc.getInt(GROUND_LEVEL);

            tempPlotInfo.ProtectedWallBlockId = wgc.getString(PROTECTED_WALL_BLOCK);
            tempPlotInfo.ForSaleWallBlockId = wgc.getString(FOR_SALE_WALL_BLOCK);
            tempPlotInfo.AuctionWallBlockId = wgc.getString(AUCTION_WALL_BLOCK);

            genPlotManager.genplotmaps.put(worldname.toLowerCase(), tempPlotInfo);
        }

        saveConfig();
    }

    private short getBlockId(ConfigurationSection cs, String section, String def) {
        String idvalue = cs.getString(section, def.toString());
        if (idvalue.indexOf(":") > 0) {
            return Short.parseShort(idvalue.split(":")[0]);
        } else {
            return Short.parseShort(idvalue);
        }
    }

    private byte getBlockValue(ConfigurationSection cs, String section, String def) {
        String idvalue = cs.getString(section, def.toString());
        if (idvalue.indexOf(":") > 0) {
            return Byte.parseByte(idvalue.split(":")[1]);
        } else {
            return 0;
        }
    }

    private String getBlockValueId(Short id, Byte value) {
        return (value == 0) ? id.toString() : id.toString() + ":" + value.toString();
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

    public String getConfigPath() {
        return configpath;
    }

    private void setConfigPath(String configpath) {
        this.configpath = configpath;
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
