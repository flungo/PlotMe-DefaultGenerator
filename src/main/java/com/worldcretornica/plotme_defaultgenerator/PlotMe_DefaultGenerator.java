package com.worldcretornica.plotme_defaultgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.worldcretornica.plotme_defaultgenerator.PlotGen;

public class PlotMe_DefaultGenerator extends JavaPlugin
{
	public String PREFIX;
	public String VERSION;
	
	public String language;
		
	private String configpath;
    private Boolean advancedlogging;
    
    public Boolean usinglwc = false;
    
    private HashMap<String, String> captions;
    
    private GenPlotManager genPlotManager;
    
    public void onDisable()
    {
    	captions = null;
    	genPlotManager = null;
    	usinglwc = null;
    	setAdvancedLogging(null);
    	setConfigPath(null);
    	PREFIX = null;
    	VERSION = null;
    }
    
	public void onEnable()
	{
		initialize();
		
		loadCaptions();
	}
	
	private void loadCaptions() 
	{
		File filelang = new File(getConfigPath(), "caption-english.yml");
		TreeMap<String, String> properties = new TreeMap<String, String>();
		
		//properties.put("MsgStartDeleteSession","Starting delete session");
		
		//properties.put("ConsoleHelpMain", " ---==PlotMe Console Help Page==---");
		//properties.put("ConsoleHelpReload", " - Reloads the plugin and its configuration files");
		
		//properties.put("HelpTitle", "PlotMe Help Page");
		/*properties.put("HelpPath", " width of the path");
		properties.put("HelpPlot", " size of the plots");
		properties.put("HelpHeight", " height of the road");
		properties.put("HelpBottom", " block id at the bottom");
		properties.put("HelpWall", " id of the plot border");
		properties.put("HelpPlotFloor", " id of the plot surface");
		properties.put("HelpFilling", " id of the blocks below surface");
		properties.put("HelpRoadFloor1", " id of the road middle");
		properties.put("HelpRoadFloor2", " id of the road border");*/
		
		//properties.put("WordWorld", "World");
		properties.put("WordArguments", "arguments");
		
		//properties.put("InfoId", "ID");
		
		//properties.put("CommandBuy", "buy");
		
		//properties.put("ErrCannotBuild","You cannot build here.");
		
		CreateConfig(filelang, properties, "PlotMe Caption configuration αω");
		
		if (language != null && !language.equals("english"))
		{
			filelang = new File(getConfigPath(), "caption-" + language + ".yml");
			CreateConfig(filelang, properties, "PlotMe DefaultGenerator Caption configuration");
		}
		
		InputStream input = null;
		
		try
		{				
			input = new FileInputStream(filelang);
		    Yaml yaml = new Yaml();
		    Object obj = yaml.load(input);
		
		    if(obj instanceof LinkedHashMap<?, ?>)
		    {
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, String> data = (LinkedHashMap<String, String>) obj;
							    
			    captions = new HashMap<String, String>();
				for(String key : data.keySet())
				{
					captions.put(key, data.get(key));
				}
		    }
		} catch (FileNotFoundException e) {
			getLogger().severe("[" + getName() + "] File not found: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			getLogger().severe("[" + getName() + "] Error with configuration: " + e.getMessage());
			e.printStackTrace();
		} finally {                      
			if (input != null) try {
				input.close();
			} catch (IOException e) {}
		}
	}
	
	private void CreateConfig(File file, TreeMap<String, String> properties, String Title)
	{
		if(!file.exists())
		{
			BufferedWriter writer = null;
			
			try{
				File dir = new File(getConfigPath(), "");
				dir.mkdirs();			
				
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
				writer.write("# " + Title);
				
				for(Entry<String, String> e : properties.entrySet())
				{
					writer.write("\n" + e.getKey() + ": '" + e.getValue().replace("'", "''") + "'");
				}
				
				writer.close();
			}catch (IOException e){
				getLogger().severe("[" + getName() + "] Unable to create config file : " + Title + "!");
				getLogger().severe(e.getMessage());
			} finally {                      
				if (writer != null) try {
					writer.close();
				} catch (IOException e2) {}
			}
		}
		else
		{
			OutputStreamWriter writer = null;
			InputStream input = null;
			
			try
			{				
				input = new FileInputStream(file);
			    Yaml yaml = new Yaml();
			    Object obj = yaml.load(input);
			    
			    if(obj instanceof LinkedHashMap<?, ?>)
			    {
					@SuppressWarnings("unchecked")
					LinkedHashMap<String, String> data = (LinkedHashMap<String, String>) obj;
					
				    writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
					
					for(Entry<String, String> e : properties.entrySet())
					{						
						if (!data.containsKey(e.getKey()))
							writer.write("\n" + e.getKey() + ": '" + e.getValue().replace("'", "''") + "'");
					}
					
					writer.close();
					input.close();
			    }
			} catch (FileNotFoundException e) {
				getLogger().severe("[" + getName() + "] File not found: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				getLogger().severe("[" + getName() + "] Error with configuration: " + e.getMessage());
				e.printStackTrace();
			} finally {                      
				if (writer != null) try {
					writer.close();
				} catch (IOException e2) {}
				if (input != null) try {
					input.close();
				} catch (IOException e) {}
			}
		}
	}

	public GenPlotManager getGenPlotManager()
	{
		return genPlotManager;
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String worldname, String id)
	{		
		String map = worldname.toLowerCase();
		if(genPlotManager.genplotmaps.containsKey(map))
		{
			return new PlotGen(this, genPlotManager.getMap(map));
		}
		else
		{
			return new PlotGen(this);
		}
	}
	
	private void importOldConfigs(File newfile)
	{
		File oldfile;
		
		oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.yml");
		
		if(!oldfile.exists())
		{
			oldfile = new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.backup.yml");
		}
		
		if(oldfile.exists())
		{
			getLogger().info(PREFIX + "Importing old configurations");
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
				getLogger().severe(PREFIX + "can't read old configuration file");
				e.printStackTrace();
				return;
			} 
			catch (InvalidConfigurationException e) 
			{
				getLogger().severe(PREFIX + "invalid old configuration format");
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
					oldfile.renameTo(new File(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + "config.backup.yml"));
				}
			} 
			catch (IOException e) 
			{
				getLogger().severe(PREFIX + "error writting configurations");
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void initialize()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		PREFIX = ChatColor.BLUE + "[" + getName() + "] " + ChatColor.RESET;
		VERSION = pdfFile.getVersion();
		setConfigPath(getDataFolder().getParentFile().getAbsolutePath() + File.separator + "PlotMe" + File.separator + getName());
		
		File configfolder = new File(getConfigPath());
		
		if(!configfolder.exists()) 
		{
			configfolder.mkdirs();
        }
		
		genPlotManager = new GenPlotManager(this);
				
		File configfile = new File(getConfigPath(), "config.yml");
		
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
			getLogger().severe(PREFIX + "can't read configuration file");
			e.printStackTrace();
		} 
		catch (InvalidConfigurationException e) 
		{
			getLogger().severe(PREFIX + "invalid configuration format");
			e.printStackTrace();
		}
        
		setAdvancedLogging(config.getBoolean("AdvancedLogging", false));

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
				getLogger().severe(PREFIX + "RoadHeight above 250 is unsafe. This is the height at which your road is located. Setting it to 64.");
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
		}
		
		try 
		{
			config.save(configfile);
		} 
		catch (IOException e) 
		{
			getLogger().severe(PREFIX + "error writting configurations");
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
	
	public short getBlockId(String block)
	{
		if(block.indexOf(":") > 0)
		{
			return Short.parseShort(block.split(":")[0]);
		}
		else
		{
			return Short.parseShort(block);
		}
	}
	
	public byte getBlockValue(String block)
	{
		if(block.indexOf(":") > 0)
		{
			return Byte.parseByte(block.split(":")[1]);
		}
		else
		{
			return 0;
		}
	}
	
	public String caption(String s)
	{
		if(captions.containsKey(s))
		{
			return addColor(captions.get(s));
		}
		else
		{
			getLogger().warning("[" + getName() + "] Missing caption: " + s);
			return "ERROR:Missing caption '" + s + "'";
		}
	}
	
	public String addColor(String string) 
	{
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
}
