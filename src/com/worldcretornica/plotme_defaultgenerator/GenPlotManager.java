package com.worldcretornica.plotme_defaultgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.worldcretornica.plotme_core.api.v0_14.IPlotMe_GeneratorManager;

public class GenPlotManager implements IPlotMe_GeneratorManager
{
	public Map<String, GenMapInfo> genplotmaps;
	
	public GenPlotManager()
	{
		genplotmaps = new HashMap<String, GenMapInfo>();
	}
	
	public String getPlotId(Location loc)
	{
		GenMapInfo pmi = getMap(loc);
		
		if(pmi != null)
		{
			int valx = loc.getBlockX();
			int valz = loc.getBlockZ();
			
			int size = pmi.PlotSize + pmi.PathWidth;
			int pathsize = pmi.PathWidth;
			boolean road = false;
			
			double n3;
			int mod2 = 0;
			int mod1 = 1;
			
			int x = (int) Math.ceil((double)valx / size);
			int z = (int) Math.ceil((double)valz / size);
			
			//int x2 = (int) Math.ceil((double)valx / size);
			//int z2 = (int) Math.ceil((double)valz / size);
			
			if(pathsize % 2 == 1)
			{
				n3 = Math.ceil(((double)pathsize)/2); //3 7
				mod2 = -1;
			}
			else
			{
				n3 = Math.floor(((double)pathsize)/2); //3 7
			}
						
			for(double i = n3; i >= 0; i--)
			{
				if((valx - i + mod1) % size == 0 ||
				   (valx + i + mod2) % size == 0)
				{
					road = true;
					
					x = (int) Math.ceil((double)(valx - n3) / size);
					//x2 = (int) Math.ceil((double)(valx + n3) / size);
				}
				if((valz - i + mod1) % size == 0 ||
				   (valz + i + mod2) % size == 0)
				{
					road = true;
					
					z = (int) Math.ceil((double)(valz - n3) / size);
					//z2 = (int) Math.ceil((double)(valz + n3) / size);
				}
			}
			
			if(road)
			{
				/*if(pmi.AutoLinkPlots)
				{
					String id1 = x + ";" + z;
					String id2 = x2 + ";" + z2;
					String id3 = x + ";" + z2;
					String id4 = x2 + ";" + z;
					
					HashMap<String, Plot> plots = pmi.plots;
					
					Plot p1 = plots.get(id1);
					Plot p2 = plots.get(id2);
					Plot p3 = plots.get(id3);
					Plot p4 = plots.get(id4);
					
					if(p1 == null || p2 == null || p3 == null || p4 == null || 
							!p1.owner.equalsIgnoreCase(p2.owner) ||
							!p2.owner.equalsIgnoreCase(p3.owner) ||
							!p3.owner.equalsIgnoreCase(p4.owner))
					{						
						return "";
					}
					else
					{
						return id1;
					}
				}
				else*/
					return "";
			}
			else
				return "" + x + ";" + z;
		}
		else
		{
			return "";
		}
	}
	
	public String getPlotId(Player player) 
	{
		return getPlotId(player.getLocation());
	}

	public List<Player> getPlayersInPlot(World w, String id) 
	{
		List<Player> playersInPlot = new ArrayList<Player>();
		
		for (Player p : w.getPlayers()) 
		{
		    if (getPlotId(p).equals(id)) 
		    {
				playersInPlot.add(p);
		    }
		}
		return playersInPlot;
	}
	
	public void fillroad(String id1, String id2, World w)
	{
		Location bottomPlot1 = getPlotBottomLoc(w, id1);
		Location topPlot1 = getPlotTopLoc(w, id1);
		Location bottomPlot2 = getPlotBottomLoc(w, id2);
		Location topPlot2 = getPlotTopLoc(w, id2);
		
		int minX;
		int maxX;
		int minZ;
		int maxZ;
		boolean isWallX;
		
		GenMapInfo pmi = getMap(w);
		int h = pmi.RoadHeight;
		int wallId = pmi.WallBlockId;
		byte wallValue = pmi.WallBlockValue;
		int fillId = pmi.PlotFloorBlockId;
		byte fillValue = pmi.PlotFloorBlockValue;
				
		if(bottomPlot1.getBlockX() == bottomPlot2.getBlockX())
		{
			minX = bottomPlot1.getBlockX();
			maxX = topPlot1.getBlockX();
			
			minZ = Math.min(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ()) + pmi.PlotSize;
			maxZ = Math.max(topPlot1.getBlockZ(), topPlot2.getBlockZ()) - pmi.PlotSize;
		}
		else
		{
			minZ = bottomPlot1.getBlockZ();
			maxZ = topPlot1.getBlockZ();
			
			minX = Math.min(bottomPlot1.getBlockX(), bottomPlot2.getBlockX()) + pmi.PlotSize;
			maxX = Math.max(topPlot1.getBlockX(), topPlot2.getBlockX()) - pmi.PlotSize;
		}
		
		isWallX = (maxX - minX) > (maxZ - minZ);
		
		if(isWallX)
		{
			minX--;
			maxX++;
		}
		else
		{
			minZ--;
			maxZ++;
		}
		
		for(int x = minX; x <= maxX; x++)
		{
			for(int z = minZ; z <= maxZ; z++)
			{
				for(int y = h; y < w.getMaxHeight(); y++)
				{
					if(y >= (h + 2))
					{
						w.getBlockAt(x, y, z).setType(Material.AIR);
					}
					else if(y == (h + 1))
					{
						if(isWallX && (x == minX || x == maxX))
						{
							w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
						}
						else if(!isWallX && (z == minZ || z == maxZ))
						{
							w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
						}
						else
						{
							w.getBlockAt(x, y, z).setType(Material.AIR);
						}
					}
					else
					{
						w.getBlockAt(x, y, z).setTypeIdAndData(fillId, fillValue, true);
					}
				}
			}
		}
	}
	
	public void fillmiddleroad(String id1, String id2, World w)
	{
		Location bottomPlot1 = getPlotBottomLoc(w, id1);
		Location topPlot1 = getPlotTopLoc(w, id1);
		Location bottomPlot2 = getPlotBottomLoc(w, id2);
		Location topPlot2 = getPlotTopLoc(w, id2);
		
		int minX;
		int maxX;
		int minZ;
		int maxZ;
		
		GenMapInfo pmi = getMap(w);
		int h = pmi.RoadHeight;
		int fillId = pmi.PlotFloorBlockId;
				
		
		minX = Math.min(topPlot1.getBlockX(), topPlot2.getBlockX());
		maxX = Math.max(bottomPlot1.getBlockX(), bottomPlot2.getBlockX());
		
		minZ = Math.min(topPlot1.getBlockZ(), topPlot2.getBlockZ());
		maxZ = Math.max(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ());
				
		for(int x = minX; x <= maxX; x++)
		{
			for(int z = minZ; z <= maxZ; z++)
			{
				for(int y = h; y < w.getMaxHeight(); y++)
				{
					if(y >= (h + 1))
					{
						w.getBlockAt(x, y, z).setType(Material.AIR);
					}
					else
					{
						w.getBlockAt(x, y, z).setTypeId(fillId);
					}
				}
			}
		}
	}
	
	
	
	public void setOwnerDisplay(World world, String id, String line1, String line2, String line3, String line4)
	{	
		Location pillar = new Location(world, bottomX(id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(id, world) - 1);
						
		Block bsign = pillar.clone().add(0, 0, -1).getBlock();
		bsign.setType(Material.AIR);
		bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 2, false);
		
		Sign sign = (Sign) bsign.getState();
		
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
			
		sign.update(true);
	}
	
	public void setSellerDisplay(World world, String id, String line1, String line2, String line3, String line4)
	{
		removeSellerDisplay(world, id);
		
		Location pillar = new Location(world, bottomX(id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(id, world) - 1);
							
		Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
		bsign.setType(Material.AIR);
		bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
			
		Sign sign = (Sign) bsign.getState();
			
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
				
		sign.update(true);
	}
	
	public void setAuctionDisplay(World world, String id, String line1, String line2, String line3, String line4)
	{
		removeSellerDisplay(world, id);
		
		Location pillar = new Location(world, bottomX(id, world) - 1, getMap(world).RoadHeight + 1, bottomZ(id, world) - 1);
							
		Block bsign = pillar.clone().add(-1, 0, 1).getBlock();
		bsign.setType(Material.AIR);
		bsign.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte) 4, false);
			
		Sign sign = (Sign) bsign.getState();
			
		sign.setLine(0, line1);
		sign.setLine(1, line2);
		sign.setLine(2, line3);
		sign.setLine(3, line4);
				
		sign.update(true);
	}
	
	public void removeOwnerDisplay(World world, String id)
	{
		Location bottom = getPlotBottomLoc(world, id);
		
		Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);
		
		Block bsign = pillar.add(0, 0, -1).getBlock();
		bsign.setType(Material.AIR);
	}
	
	public void removeSellerDisplay(World world, String id)
	{
		Location bottom = getPlotBottomLoc(world, id);
		
		Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);
		
		Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
		bsign.setType(Material.AIR);
						
		//bsign = pillar.clone().add(-1, 0, 1).getBlock();
		//bsign.setType(Material.AIR);
	}
	
	public void removeAuctionDisplay(World world, String id)
	{
		Location bottom = getPlotBottomLoc(world, id);
		
		Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);
		
		//Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
		//bsign.setType(Material.AIR);
						
		Block bsign = pillar.clone().add(-1, 0, 1).getBlock();
		bsign.setType(Material.AIR);
	}
	
	public int getIdX(String id)
	{
		return Integer.parseInt(id.substring(0, id.indexOf(";")));
	}
	
	public int getIdZ(String id)
	{
		return Integer.parseInt(id.substring(id.indexOf(";") + 1));
	}
	
	public Location getPlotBottomLoc(World world, String id)
	{
		int px = getIdX(id);
		int pz = getIdZ(id);
		
		GenMapInfo pmi = getMap(world);
		
		int x = px * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int)Math.floor(pmi.PathWidth/2));
		int z = pz * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int)Math.floor(pmi.PathWidth/2));
		
		return new Location(world, x, 1, z);
	}
	
	public Location getPlotTopLoc(World world, String id)
	{
		int px = getIdX(id);
		int pz = getIdZ(id);
		
		GenMapInfo pmi = getMap(world);
		
		int x = px * (pmi.PlotSize + pmi.PathWidth) - ((int)Math.floor(pmi.PathWidth/2)) - 1;
		int z = pz * (pmi.PlotSize + pmi.PathWidth) - ((int)Math.floor(pmi.PathWidth/2)) - 1;
		
		return new Location(world, x, 255, z);
	}
	
	public void setBiome(World w, String id, Biome b)
	{
		int bottomX = bottomX(id, w) - 1;
		int topX = topX(id, w) + 1;
		int bottomZ = bottomZ(id, w) - 1;
		int topZ = topZ(id, w) + 1;
		
		for(int x = bottomX; x <= topX; x++)
		{
			for(int z = bottomZ; z <= topZ; z++)
			{
				w.getBlockAt(x, 0, z).setBiome(b);
			}
		}
				
		refreshPlotChunks(w, id);
	}
	
	public void refreshPlotChunks(World w, String id)
	{
		int bottomX = bottomX(id, w);
		int topX = topX(id, w);
		int bottomZ = bottomZ(id, w);
		int topZ = topZ(id, w);
		
		int minChunkX = (int) Math.floor((double) bottomX / 16);
		int maxChunkX = (int) Math.floor((double) topX / 16);
		int minChunkZ = (int) Math.floor((double) bottomZ / 16);
		int maxChunkZ = (int) Math.floor((double) topZ / 16);
		
		for(int x = minChunkX; x <= maxChunkX; x++)
		{
			for(int z = minChunkZ; z <= maxChunkZ; z++)
			{
				w.refreshChunk(x, z);
			}
		}
	}
	
	public Location getTop(World w, String id)
	{
		return new Location(w, topX(id, w), w.getMaxHeight(), topZ(id, w));
	}
	
	public Location getBottom(World w, String id)
	{
		return new Location(w, bottomX(id, w), 0, bottomZ(id, w));
	}
	
	public void clear(World w, String id)
	{
		clear(getBottom(w, id), getTop(w, id));
	}
	
	public void clear(Location bottom, Location top)
	{
		GenMapInfo gmi = getMap(bottom);
		
		int bottomX = bottom.getBlockX();
		int topX = top.getBlockX();
		int bottomZ = bottom.getBlockZ();
		int topZ = top.getBlockZ();
		
		int minChunkX = (int) Math.floor((double) bottomX / 16);
		int maxChunkX = (int) Math.floor((double) topX / 16);
		int minChunkZ = (int) Math.floor((double) bottomZ / 16);
		int maxChunkZ = (int) Math.floor((double) topZ / 16);
		
		World w = bottom.getWorld();
		
		for(int cx = minChunkX; cx <= maxChunkX; cx++)
		{			
			for(int cz = minChunkZ; cz <= maxChunkZ; cz++)
			{			
				Chunk chunk = w.getChunkAt(cx, cz);
				
				for(Entity e : chunk.getEntities())
				{
					Location eloc = e.getLocation();
					
					if(!(e instanceof Player) && eloc.getBlockX() >= bottom.getBlockX() && eloc.getBlockX() <= top.getBlockX() &&
							eloc.getBlockZ() >= bottom.getBlockZ() && eloc.getBlockZ() <= top.getBlockZ())
					{
						e.remove();
					}
				}
			}
		}

		for(int x = bottomX; x <= topX; x++)
		{
			for(int z = bottomZ; z <= topZ; z++)
			{
				Block block = new Location(w, x, 0, z).getBlock();
				
				block.setBiome(Biome.PLAINS);
				
				for(int y = w.getMaxHeight(); y >= 0; y--)
				{
					block = new Location(w, x, y, z).getBlock();
					
					BlockState state = block.getState();
					
					if(state instanceof InventoryHolder)
					{
						InventoryHolder holder = (InventoryHolder) state;
						holder.getInventory().clear();
					}
					
					
					if(state instanceof Jukebox)
					{
						Jukebox jukebox = (Jukebox) state;
						//Remove once they fix the NullPointerException
						try
						{
							jukebox.setPlaying(Material.AIR);
						}catch(Exception e){}
					}
					
										
					if(y == 0)
						block.setTypeId(gmi.BottomBlockId);
					else if(y < gmi.RoadHeight)
						block.setTypeId(gmi.PlotFillingBlockId);
					else if(y == gmi.RoadHeight)
						block.setTypeId(gmi.PlotFloorBlockId);
					else
					{
						if(y == (gmi.RoadHeight + 1) && 
								(x == bottomX - 1 || 
								 x == topX + 1 ||
								 z == bottomZ - 1 || 
								 z == topZ + 1))
						{
							//block.setTypeId(pmi.WallBlockId);
						}
						else
						{
							block.setTypeIdAndData(0, (byte) 0, false); //.setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	public void adjustPlotFor(World w, String id, boolean Claimed, boolean Protected, boolean Auctionned, boolean ForSale)
	{
		//Plot plot = getPlotById(l);
		//World w = l.getWorld();
		GenMapInfo pmi = getMap(w);
		
		List<String> wallids = new ArrayList<String>();
		
		String auctionwallid = pmi.AuctionWallBlockId;
		String forsalewallid = pmi.ForSaleWallBlockId;
		
		if(Protected) wallids.add(pmi.ProtectedWallBlockId);
		if(Auctionned && !wallids.contains(auctionwallid)) wallids.add(auctionwallid);
		if(ForSale && !wallids.contains(forsalewallid)) wallids.add(forsalewallid);
		
		if(wallids.size() == 0) wallids.add("" + pmi.WallBlockId + ":" + pmi.WallBlockValue);
		
		int ctr = 0;
			
		Location bottom = getPlotBottomLoc(w, id);
		Location top = getPlotTopLoc(w, id);
		
		int x;
		int z;
		
		String currentblockid;
		Block block;
		
		for(x = bottom.getBlockX() - 1; x < top.getBlockX() + 1; x++)
		{
			z = bottom.getBlockZ() - 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(z = bottom.getBlockZ() - 1; z < top.getBlockZ() + 1; z++)
		{
			x = top.getBlockX() + 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(x = top.getBlockX() + 1; x > bottom.getBlockX() - 1; x--)
		{
			z = top.getBlockZ() + 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
		
		for(z = top.getBlockZ() + 1; z > bottom.getBlockZ() - 1; z--)
		{
			x = bottom.getBlockX() - 1;
			currentblockid = wallids.get(ctr);
			ctr = (ctr == wallids.size()-1)? 0 : ctr + 1;
			block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
			setWall(block, currentblockid);
		}
	}
	
	private void setWall(Block block, String currentblockid)
	{
		
		int blockId;
		byte blockData = 0;
		GenMapInfo pmi = getMap(block);
		
		if(currentblockid.contains(":"))
		{
			try
			{
				blockId = Integer.parseInt(currentblockid.substring(0, currentblockid.indexOf(":")));
				blockData = Byte.parseByte(currentblockid.substring(currentblockid.indexOf(":") + 1));
			}
			catch(NumberFormatException e)
			{
				blockId = pmi.WallBlockId;
				blockData = pmi.WallBlockValue;
			}
		}
		else
		{
			try
			{
				blockId = Integer.parseInt(currentblockid);
			}
			catch(NumberFormatException e)
			{
				blockId = pmi.WallBlockId;
			}
		}
		
		block.setTypeIdAndData(blockId, blockData, true);
	}
		
	public boolean isBlockInPlot(String id, Location blocklocation)
	{
		World w = blocklocation.getWorld();
		int lowestX = Math.min(bottomX(id, w), topX(id, w));
		int highestX = Math.max(bottomX(id, w), topX(id, w));
		int lowestZ = Math.min(bottomZ(id, w), topZ(id, w));
		int highestZ = Math.max(bottomZ(id, w), topZ(id, w));
		
		return blocklocation.getBlockX() >= lowestX && blocklocation.getBlockX() <= highestX
				&& blocklocation.getBlockZ() >= lowestZ && blocklocation.getBlockZ() <= highestZ;
	}
	
	public boolean movePlot(World wFrom, World wTo, String idFrom, String idTo)
	{
		Location plot1Bottom = getPlotBottomLoc(wFrom, idFrom);
		Location plot2Bottom = getPlotBottomLoc(wTo, idTo);
		Location plot1Top = getPlotTopLoc(wFrom, idFrom);
		
		int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
		int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();
		
		for(int x = plot1Bottom.getBlockX(); x <= plot1Top.getBlockX(); x++)
		{
			for(int z = plot1Bottom.getBlockZ(); z <= plot1Top.getBlockZ(); z++)
			{
				Block plot1Block = new Location(wFrom, x, 0, z).getBlock();
				Block plot2Block = new Location(wTo, x - distanceX, 0, z - distanceZ).getBlock();
				
				String plot1Biome = plot1Block.getBiome().name();
				String plot2Biome = plot2Block.getBiome().name();
				
				plot1Block.setBiome(Biome.valueOf(plot2Biome));
				plot2Block.setBiome(Biome.valueOf(plot1Biome));
				
				for(int y = 0; y < wFrom.getMaxHeight() ; y++)
				{
					plot1Block = new Location(wFrom, x, y, z).getBlock();
					int plot1Type = plot1Block.getTypeId();
					byte plot1Data = plot1Block.getData();
					
					plot2Block = new Location(wTo, x - distanceX, y, z - distanceZ).getBlock();
					int plot2Type = plot2Block.getTypeId();
					byte plot2Data = plot2Block.getData();
					
					plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
					plot1Block.setData(plot2Data);
					
					plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
					plot2Block.setData(plot1Data);
				}
			}
		}
		
		return true;
	}
	
	public int bottomX(String id, World w)
	{
		return getPlotBottomLoc(w, id).getBlockX();
	}
	
	public int bottomZ(String id, World w)
	{
		return getPlotBottomLoc(w, id).getBlockZ();
	}

	public int topX(String id, World w)
	{
		return getPlotTopLoc(w, id).getBlockX();
	}
	
	public int topZ(String id, World w)
	{
		return getPlotTopLoc(w, id).getBlockZ();
	}	
	
	public boolean isValidId(String id)
	{
		String[] coords = id.split(";");
		
		if(coords.length != 2)
			return false;
		else
		{
			try
			{
				Integer.parseInt(coords[0]);
				Integer.parseInt(coords[1]);
				return true;
			}catch(Exception e)
			{
				return false;
			}
		}
	}
	
	public void regen(World w, String id, CommandSender sender)
	{
		int bottomX = bottomX(id, w);
		int topX = topX(id, w);
		int bottomZ = bottomZ(id, w);
		int topZ = topZ(id, w);
		
		int minChunkX = (int) Math.floor((double) bottomX / 16);
		int maxChunkX = (int) Math.floor((double) topX / 16);
		int minChunkZ = (int) Math.floor((double) bottomZ / 16);
		int maxChunkZ = (int) Math.floor((double) topZ / 16);
		
		HashMap<Location, Biome> biomes = new HashMap<Location, Biome>();
		
		for(int cx = minChunkX; cx <= maxChunkX; cx++)
		{
			int xx = cx << 4;
			
			for(int cz = minChunkZ; cz <= maxChunkZ; cz++)
			{	
				int zz = cz << 4;
				
				BlockState[][][] blocks = new BlockState[16][16][w.getMaxHeight()];
				
				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{
						biomes.put(new Location(w, x + xx, 0, z + zz), w.getBiome(x + xx, z + zz));
						
						for(int y = 0; y < w.getMaxHeight(); y++)
						{
							Block block = w.getBlockAt(x + xx, y, z + zz);
							blocks[x][z][y] = block.getState();
							
							/*if(PlotMe.usinglwc)
							{
								LWC lwc = com.griefcraft.lwc.LWC.getInstance();
								Material material = block.getType();
								
								boolean ignoreBlockDestruction = Boolean.parseBoolean(lwc.resolveProtectionConfiguration(material, "ignoreBlockDestruction"));
								
								if (!ignoreBlockDestruction)
								{
									Protection protection = lwc.findProtection(block);

									if(protection != null)
									{
										protection.remove();
									}
								}
							}*/
						}
					}
				}
				
				try
				{
		            w.regenerateChunk(cx, cz);
		        } catch (Throwable t) {
		            t.printStackTrace();
		        }
				
				for(int x = 0; x < 16; x++)
				{
					for(int z = 0; z < 16; z++)
					{						
						for(int y = 0; y < w.getMaxHeight(); y++)
						{
							if((x + xx) < bottomX || (x + xx) > topX || (z + zz) < bottomZ || (z + zz) > topZ)
							{
								Block newblock = w.getBlockAt(x + xx, y, z + zz);
								BlockState oldblock = blocks[x][z][y];
								
								newblock.setTypeIdAndData(oldblock.getTypeId(), oldblock.getRawData(), false);
								oldblock.update();
							}
						}
					}
				}
			}
		}
		
		for(Location loc : biomes.keySet())
		{
			int x = loc.getBlockX();
			int z = loc.getBlockX();
			
			w.setBiome(x, z, biomes.get(loc));
		}
	}
	
	public Location getPlotHome(World w, String id)
	{
		GenMapInfo pmi = getMap(w);
		
		if(pmi != null)
		{
			return new Location(w, bottomX(id, w) + (topX(id, w) - bottomX(id, w))/2, pmi.RoadHeight + 2, bottomZ(id, w) - 2);
		}
		else
		{
			return w.getSpawnLocation();
		}
	}

	
	private GenMapInfo getMap(Location loc) 
	{
		return getMap(loc.getWorld());
	}

	private GenMapInfo getMap(Block b) 
	{
		return getMap(b.getLocation());
	}
	
	private GenMapInfo getMap(World w) 
	{
		return getMap(w.getName());
	}

	public GenMapInfo getMap(String worldname) 
	{
		return genplotmaps.get(worldname);
	}
}
