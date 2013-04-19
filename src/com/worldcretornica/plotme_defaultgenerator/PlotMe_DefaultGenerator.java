package com.worldcretornica.plotme_defaultgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme_defaultgenerator.PlotGen;

public class PlotMe_DefaultGenerator extends JavaPlugin
{
	public static String NAME;
	public static String PREFIX;
	public static String VERSION;
	public static String WEBSITE;
	
	public static Logger logger = Logger.getLogger("Minecraft");
		
	public static String configpath;
    public static Boolean advancedlogging;
    
    public static Boolean usinglwc = false;
    
    private static GenPlotManager genPlotManager;
    	
	public void onEnable()
	{
		initialize();
	}
	
	public static GenPlotManager getGenPlotManager()
	{
		return genPlotManager;
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id)
	{		
		if(genPlotManager.genplotmaps.containsKey(worldname))
		{
			return new PlotGen(genPlotManager.getMap(worldname));
		}
		else
		{
			logger.warning(PREFIX + "Configuration not found for PlotMe world '" + worldname + "' Using defaults");
			return new PlotGen();
		}
	}
	
	private void importOldConfigs(File newfile)
	{
		File oldfile;
		
		oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + "/PlotMe/config.yml");
		
		if(!oldfile.exists())
		{
			oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + "/PlotMe/config.backup.yml");
		}
		
		if(oldfile.exists())
		{
			logger.info(PREFIX + "Importing old configurations");
			FileConfiguration oldconfig = new YamlConfiguration();
			FileConfiguration newconfig = new YamlConfiguration();
			
			try 
			{
				oldconfig.load(oldfile);
			} 
			catch (FileNotFoundException e) 
			{
				return;
			} 
			catch (IOException e) 
			{
				logger.severe(PREFIX + "can't read old configuration file");
				e.printStackTrace();
				return;
			} 
			catch (InvalidConfigurationException e) 
			{
				logger.severe(PREFIX + "invalid old configuration format");
				e.printStackTrace();
				return;
			}

			ConfigurationSection oldworlds;
			ConfigurationSection newworlds;
			
			if(!oldconfig.contains("worlds"))
			{
				return;
			}
			else
			{
				oldworlds = oldconfig.getConfigurationSection("worlds");
			}
			
			if(!newconfig.contains("worlds"))
			{
				newworlds = newconfig.createSection("worlds");
			}
			else
			{
				newworlds = newconfig.getConfigurationSection("worlds");
			}
					
			for(String worldname : oldworlds.getKeys(false))
			{
				ConfigurationSection oldcurrworld = oldworlds.getConfigurationSection(worldname);
				ConfigurationSection newcurrworld;
				
				if(newworlds.contains("worldname"))
				{
					newcurrworld = newworlds.getConfigurationSection(worldname);
				}
				else
				{
					newcurrworld = newworlds.createSection(worldname);
				}
				
				newcurrworld.set("PathWidth", oldcurrworld.getInt("PathWidth", 7));
				newcurrworld.set("PlotSize", oldcurrworld.getInt("PlotSize", 32));
				
				newcurrworld.set("XTranslation", oldcurrworld.getInt("XTranslation", 0));
				newcurrworld.set("ZTranslation",  oldcurrworld.getInt("ZTranslation", 0));
				
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
			
			try 
			{
				newconfig.save(newfile);
				
				if(!oldfile.getName().contains("config.backup.yml"))
				{
					oldfile.renameTo(new File(getDataFolder().getParentFile().getAbsolutePath() + "/PlotMe/config.backup.yml"));
				}
			} 
			catch (IOException e) 
			{
				logger.severe(PREFIX + "error writting configurations");
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void initialize()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		NAME = pdfFile.getName();
		PREFIX = ChatColor.BLUE + "[" + NAME + "] " + ChatColor.RESET;
		VERSION = pdfFile.getVersion();
		WEBSITE = pdfFile.getWebsite();
		configpath = getDataFolder().getParentFile().getAbsolutePath() + "/PlotMe/" + NAME;
		
		File configfolder = new File(configpath);
		
		if(!configfolder.exists()) 
		{
			configfolder.mkdirs();
        }
		
		genPlotManager = new GenPlotManager();
				
		File configfile = new File(configpath, "config.yml");
		
		if(!configfile.exists())
		{
			importOldConfigs(configfile);
		}
		
		FileConfiguration config = new YamlConfiguration();
		
		try 
		{
			config.load(configfile);
		} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) 
		{
			logger.severe(PREFIX + "can't read configuration file");
			e.printStackTrace();
		} 
		catch (InvalidConfigurationException e) 
		{
			logger.severe(PREFIX + "invalid configuration format");
			e.printStackTrace();
		}
        
		advancedlogging = config.getBoolean("AdvancedLogging", false);

		ConfigurationSection worlds;
		
		if(!config.contains("worlds"))
		{
			worlds = config.createSection("worlds");
			
			ConfigurationSection plotworld = worlds.createSection("plotworld");
			
			plotworld.set("PathWidth", 7);
			plotworld.set("PlotSize", 32);
			
			plotworld.set("XTranslation", 0);
			plotworld.set("ZTranslation", 0);
			
			plotworld.set("BottomBlockId", "7");
			plotworld.set("WallBlockId", "44");
			plotworld.set("PlotFloorBlockId", "2");
			plotworld.set("PlotFillingBlockId", "3");
			plotworld.set("RoadMainBlockId", "5");
			plotworld.set("RoadStripeBlockId", "5:2");
			
			plotworld.set("RoadHeight", 64);
			plotworld.set("ProtectedWallBlockId", "44:4");
			plotworld.set("ForSaleWallBlockId", "44:1");
			plotworld.set("AuctionWallBlockId", "44:1");
						
			worlds.set("plotworld", plotworld);
			config.set("worlds", worlds);
		}
		else
		{
			worlds = config.getConfigurationSection("worlds");
		}
				
		for(String worldname : worlds.getKeys(false))
		{
			GenMapInfo tempPlotInfo = new GenMapInfo();
			ConfigurationSection currworld = worlds.getConfigurationSection(worldname);
			
			tempPlotInfo.PathWidth = currworld.getInt("PathWidth", 7);
			tempPlotInfo.PlotSize = currworld.getInt("PlotSize", 32);
			
			tempPlotInfo.XTranslation = currworld.getInt("XTranslation", 0);
			tempPlotInfo.ZTranslation = currworld.getInt("ZTranslation", 0);
			
			tempPlotInfo.BottomBlockId = getBlockId(currworld, "BottomBlockId", "7:0");
			tempPlotInfo.BottomBlockValue = getBlockValue(currworld, "BottomBlockId", "7:0");
			tempPlotInfo.WallBlockId = getBlockId(currworld, "WallBlockId", "44:0");
			tempPlotInfo.WallBlockValue = getBlockValue(currworld, "WallBlockId", "44:0");
			tempPlotInfo.PlotFloorBlockId = getBlockId(currworld, "PlotFloorBlockId", "2:0");
			tempPlotInfo.PlotFloorBlockValue = getBlockValue(currworld, "PlotFloorBlockId", "2:0");
			tempPlotInfo.PlotFillingBlockId = getBlockId(currworld, "PlotFillingBlockId", "3:0");
			tempPlotInfo.PlotFillingBlockValue = getBlockValue(currworld, "PlotFillingBlockId", "3:0");
			tempPlotInfo.RoadMainBlockId = getBlockId(currworld, "RoadMainBlockId", "5:0");
			tempPlotInfo.RoadMainBlockValue = getBlockValue(currworld, "RoadMainBlockId", "5:0");
			tempPlotInfo.RoadStripeBlockId = getBlockId(currworld, "RoadStripeBlockId", "5:2");
			tempPlotInfo.RoadStripeBlockValue = getBlockValue(currworld, "RoadStripeBlockId", "5:2");
			
			tempPlotInfo.RoadHeight = currworld.getInt("RoadHeight", currworld.getInt("WorldHeight", 64));
			if(tempPlotInfo.RoadHeight > 250)
			{
				logger.severe(PREFIX + "RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
				tempPlotInfo.RoadHeight = 64;
			}

			tempPlotInfo.ProtectedWallBlockId = currworld.getString("ProtectedWallBlockId", "44:4");
			tempPlotInfo.ForSaleWallBlockId = currworld.getString("ForSaleWallBlockId", "44:1");
			tempPlotInfo.AuctionWallBlockId = currworld.getString("AuctionWallBlockId", "44:1");
						
			currworld.set("PathWidth", tempPlotInfo.PathWidth);
			currworld.set("PlotSize", tempPlotInfo.PlotSize);
			
			currworld.set("XTranslation", tempPlotInfo.XTranslation);
			currworld.set("ZTranslation", tempPlotInfo.ZTranslation);
			
			currworld.set("BottomBlockId", getBlockValueId(tempPlotInfo.BottomBlockId, tempPlotInfo.BottomBlockValue));
			currworld.set("WallBlockId", getBlockValueId(tempPlotInfo.WallBlockId, tempPlotInfo.WallBlockValue));
			currworld.set("PlotFloorBlockId", getBlockValueId(tempPlotInfo.PlotFloorBlockId, tempPlotInfo.PlotFloorBlockValue));
			currworld.set("PlotFillingBlockId", getBlockValueId(tempPlotInfo.PlotFillingBlockId, tempPlotInfo.PlotFillingBlockValue));
			currworld.set("RoadMainBlockId", getBlockValueId(tempPlotInfo.RoadMainBlockId, tempPlotInfo.RoadMainBlockValue));
			currworld.set("RoadStripeBlockId", getBlockValueId(tempPlotInfo.RoadStripeBlockId, tempPlotInfo.RoadStripeBlockValue));
			
			currworld.set("RoadHeight", tempPlotInfo.RoadHeight);
			currworld.set("WorldHeight", null);

			currworld.set("ProtectedWallBlockId", tempPlotInfo.ProtectedWallBlockId);
			currworld.set("ForSaleWallBlockId", tempPlotInfo.ForSaleWallBlockId);
			currworld.set("AuctionWallBlockId", tempPlotInfo.AuctionWallBlockId);
			
			worlds.set(worldname, currworld);
			
			genPlotManager.genplotmaps.put(worldname.toLowerCase(), tempPlotInfo);
			logger.info("map : " + worldname.toLowerCase());
		}
		
		try 
		{
			config.save(configfile);
		} 
		catch (IOException e) 
		{
			logger.severe(PREFIX + "error writting configurations");
			e.printStackTrace();
		}
    }
	
	private short getBlockId(ConfigurationSection cs, String section, String def)
	{
		String idvalue = cs.getString(section, def.toString());
		if(idvalue.indexOf(":") > 0)
		{
			return Short.parseShort(idvalue.split(":")[0]);
		}
		else
		{
			return Short.parseShort(idvalue);
		}
	}
	
	private byte getBlockValue(ConfigurationSection cs, String section, String def)
	{
		String idvalue = cs.getString(section, def.toString());
		if(idvalue.indexOf(":") > 0)
		{
			return Byte.parseByte(idvalue.split(":")[1]);
		}
		else
		{
			return 0;
		}
	}
	
	private String getBlockValueId(Short id, Byte value)
	{
		return (value == 0) ? id.toString() : id.toString() + ":" + value.toString();
	}
}
