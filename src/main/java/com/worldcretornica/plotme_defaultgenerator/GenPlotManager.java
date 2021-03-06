package com.worldcretornica.plotme_defaultgenerator;

import com.worldcretornica.plotme_core.api.v0_14b.IPlotMe_GeneratorManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Art;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GenPlotManager implements IPlotMe_GeneratorManager {

    private PlotMe_DefaultGenerator plugin = null;

    private static final Set<Integer> blockPlacedLast = new HashSet<Integer>();

    static {
        blockPlacedLast.add(Material.SAPLING.getId());
        blockPlacedLast.add(Material.BED.getId());
        blockPlacedLast.add(Material.POWERED_RAIL.getId());
        blockPlacedLast.add(Material.DETECTOR_RAIL.getId());
        blockPlacedLast.add(Material.LONG_GRASS.getId());
        blockPlacedLast.add(Material.DEAD_BUSH.getId());
        blockPlacedLast.add(Material.PISTON_EXTENSION.getId());
        blockPlacedLast.add(Material.YELLOW_FLOWER.getId());
        blockPlacedLast.add(Material.RED_ROSE.getId());
        blockPlacedLast.add(Material.BROWN_MUSHROOM.getId());
        blockPlacedLast.add(Material.RED_MUSHROOM.getId());
        blockPlacedLast.add(Material.TORCH.getId());
        blockPlacedLast.add(Material.FIRE.getId());
        blockPlacedLast.add(Material.REDSTONE_WIRE.getId());
        blockPlacedLast.add(Material.CROPS.getId());
        blockPlacedLast.add(Material.LADDER.getId());
        blockPlacedLast.add(Material.RAILS.getId());
        blockPlacedLast.add(Material.LEVER.getId());
        blockPlacedLast.add(Material.STONE_PLATE.getId());
        blockPlacedLast.add(Material.WOOD_PLATE.getId());
        blockPlacedLast.add(Material.REDSTONE_TORCH_OFF.getId());
        blockPlacedLast.add(Material.REDSTONE_TORCH_ON.getId());
        blockPlacedLast.add(Material.STONE_BUTTON.getId());
        blockPlacedLast.add(Material.SNOW.getId());
        blockPlacedLast.add(Material.PORTAL.getId());
        blockPlacedLast.add(Material.DIODE_BLOCK_OFF.getId());
        blockPlacedLast.add(Material.DIODE_BLOCK_ON.getId());
        blockPlacedLast.add(Material.TRAP_DOOR.getId());
        blockPlacedLast.add(Material.VINE.getId());
        blockPlacedLast.add(Material.WATER_LILY.getId());
        blockPlacedLast.add(Material.NETHER_WARTS.getId());
        blockPlacedLast.add(Material.PISTON_BASE.getId());
        blockPlacedLast.add(Material.PISTON_STICKY_BASE.getId());
        blockPlacedLast.add(Material.PISTON_EXTENSION.getId());
        blockPlacedLast.add(Material.PISTON_MOVING_PIECE.getId());
        blockPlacedLast.add(Material.COCOA.getId());
        blockPlacedLast.add(Material.TRIPWIRE_HOOK.getId());
        blockPlacedLast.add(Material.TRIPWIRE.getId());
        blockPlacedLast.add(Material.FLOWER_POT.getId());
        blockPlacedLast.add(Material.CARROT.getId());
        blockPlacedLast.add(Material.POTATO.getId());
        blockPlacedLast.add(Material.WOOD_BUTTON.getId());
        blockPlacedLast.add(Material.SKULL.getId());
        blockPlacedLast.add(Material.GOLD_PLATE.getId());
        blockPlacedLast.add(Material.IRON_PLATE.getId());
        blockPlacedLast.add(Material.REDSTONE_COMPARATOR_OFF.getId());
        blockPlacedLast.add(Material.REDSTONE_COMPARATOR_ON.getId());
        blockPlacedLast.add(Material.ACTIVATOR_RAIL.getId());
    }

    public Map<String, GenMapInfo> genplotmaps;

    public GenPlotManager(PlotMe_DefaultGenerator instance) {
        plugin = instance;
        genplotmaps = new HashMap<String, GenMapInfo>();
    }

    @Override
    public int getRoadHeight(String worldname) {
        return getMap(worldname).RoadHeight;
    }

    @Override
    public String getPlotId(Location loc) {
        GenMapInfo pmi = getMap(loc);

        if (pmi != null) {
            int valx = loc.getBlockX();
            int valz = loc.getBlockZ();

            int size = pmi.PlotSize + pmi.PathWidth;
            int pathsize = pmi.PathWidth;
            boolean road = false;

            double n3;
            int mod2 = 0;
            int mod1 = 1;

            int x = (int) Math.ceil((double) valx / size);
            int z = (int) Math.ceil((double) valz / size);

            //int x2 = (int) Math.ceil((double)valx / size);
            //int z2 = (int) Math.ceil((double)valz / size);
            if (pathsize % 2 == 1) {
                n3 = Math.ceil(((double) pathsize) / 2); //3 7
                mod2 = -1;
            } else {
                n3 = Math.floor(((double) pathsize) / 2); //3 7
            }

            for (double i = n3; i >= 0; i--) {
                if ((valx - i + mod1) % size == 0
                        || (valx + i + mod2) % size == 0) {
                    road = true;

                    x = (int) Math.ceil((double) (valx - n3) / size);
                    //x2 = (int) Math.ceil((double)(valx + n3) / size);
                }
                if ((valz - i + mod1) % size == 0
                        || (valz + i + mod2) % size == 0) {
                    road = true;

                    z = (int) Math.ceil((double) (valz - n3) / size);
                    //z2 = (int) Math.ceil((double)(valz + n3) / size);
                }
            }

            if (road) {
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
            } else {
                return "" + x + ";" + z;
            }
        } else {
            return "";
        }
    }

    @Override
    public String getPlotId(Player player) {
        return getPlotId(player.getLocation());
    }

    @Override
    public List<Player> getPlayersInPlot(World w, String id) {
        List<Player> playersInPlot = new ArrayList<Player>();

        for (Player p : w.getPlayers()) {
            if (getPlotId(p).equals(id)) {
                playersInPlot.add(p);
            }
        }
        return playersInPlot;
    }

    @Override
    public void fillroad(String id1, String id2, World w) {
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

        if (bottomPlot1.getBlockX() == bottomPlot2.getBlockX()) {
            minX = bottomPlot1.getBlockX();
            maxX = topPlot1.getBlockX();

            minZ = Math.min(bottomPlot1.getBlockZ(), bottomPlot2.getBlockZ()) + pmi.PlotSize;
            maxZ = Math.max(topPlot1.getBlockZ(), topPlot2.getBlockZ()) - pmi.PlotSize;
        } else {
            minZ = bottomPlot1.getBlockZ();
            maxZ = topPlot1.getBlockZ();

            minX = Math.min(bottomPlot1.getBlockX(), bottomPlot2.getBlockX()) + pmi.PlotSize;
            maxX = Math.max(topPlot1.getBlockX(), topPlot2.getBlockX()) - pmi.PlotSize;
        }

        isWallX = (maxX - minX) > (maxZ - minZ);

        if (isWallX) {
            minX--;
            maxX++;
        } else {
            minZ--;
            maxZ++;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = h; y < w.getMaxHeight(); y++) {
                    if (y >= (h + 2)) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    } else if (y == (h + 1)) {
                        if (isWallX && (x == minX || x == maxX)) {
                            w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                        } else if (!isWallX && (z == minZ || z == maxZ)) {
                            w.getBlockAt(x, y, z).setTypeIdAndData(wallId, wallValue, true);
                        } else {
                            w.getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    } else {
                        w.getBlockAt(x, y, z).setTypeIdAndData(fillId, fillValue, true);
                    }
                }
            }
        }
    }

    @Override
    public void fillmiddleroad(String id1, String id2, World w) {
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

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = h; y < w.getMaxHeight(); y++) {
                    if (y >= (h + 1)) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    } else {
                        w.getBlockAt(x, y, z).setTypeId(fillId);
                    }
                }
            }
        }
    }

    @Override
    public void setOwnerDisplay(World world, String id, String line1, String line2, String line3, String line4) {
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

    @Override
    public void setSellerDisplay(World world, String id, String line1, String line2, String line3, String line4) {
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

    @Override
    public void setAuctionDisplay(World world, String id, String line1, String line2, String line3, String line4) {
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

    @Override
    public void removeOwnerDisplay(World world, String id) {
        Location bottom = getPlotBottomLoc(world, id);

        Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);

        Block bsign = pillar.add(0, 0, -1).getBlock();
        bsign.setType(Material.AIR);
    }

    @Override
    public void removeSellerDisplay(World world, String id) {
        Location bottom = getPlotBottomLoc(world, id);

        Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);

        Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
        bsign.setType(Material.AIR);

        //bsign = pillar.clone().add(-1, 0, 1).getBlock();
        //bsign.setType(Material.AIR);
    }

    @Override
    public void removeAuctionDisplay(World world, String id) {
        Location bottom = getPlotBottomLoc(world, id);

        Location pillar = new Location(world, bottom.getX() - 1, getMap(world).RoadHeight + 1, bottom.getZ() - 1);

        //Block bsign = pillar.clone().add(-1, 0, 0).getBlock();
        //bsign.setType(Material.AIR);
        Block bsign = pillar.clone().add(-1, 0, 1).getBlock();
        bsign.setType(Material.AIR);
    }

    @Override
    public int getIdX(String id) {
        return Integer.parseInt(id.substring(0, id.indexOf(";")));
    }

    @Override
    public int getIdZ(String id) {
        return Integer.parseInt(id.substring(id.indexOf(";") + 1));
    }

    @Override
    public Location getPlotBottomLoc(World world, String id) {
        int px = getIdX(id);
        int pz = getIdZ(id);

        GenMapInfo pmi = getMap(world);

        int x = px * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int) Math.floor(pmi.PathWidth / 2));
        int z = pz * (pmi.PlotSize + pmi.PathWidth) - (pmi.PlotSize) - ((int) Math.floor(pmi.PathWidth / 2));

        return new Location(world, x, 1, z);
    }

    @Override
    public Location getPlotTopLoc(World world, String id) {
        int px = getIdX(id);
        int pz = getIdZ(id);

        GenMapInfo pmi = getMap(world);

        int x = px * (pmi.PlotSize + pmi.PathWidth) - ((int) Math.floor(pmi.PathWidth / 2)) - 1;
        int z = pz * (pmi.PlotSize + pmi.PathWidth) - ((int) Math.floor(pmi.PathWidth / 2)) - 1;

        return new Location(world, x, 255, z);
    }

    @Override
    public void setBiome(World w, String id, Biome b) {
        int bottomX = bottomX(id, w) - 1;
        int topX = topX(id, w) + 1;
        int bottomZ = bottomZ(id, w) - 1;
        int topZ = topZ(id, w) + 1;

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                w.getBlockAt(x, 0, z).setBiome(b);
            }
        }

        refreshPlotChunks(w, id);
    }

    @Override
    public void refreshPlotChunks(World w, String id) {
        int bottomX = bottomX(id, w);
        int topX = topX(id, w);
        int bottomZ = bottomZ(id, w);
        int topZ = topZ(id, w);

        int minChunkX = (int) Math.floor((double) bottomX / 16);
        int maxChunkX = (int) Math.floor((double) topX / 16);
        int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ = (int) Math.floor((double) topZ / 16);

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                w.refreshChunk(x, z);
            }
        }
    }

    @Override
    public Location getTop(World w, String id) {
        //return new Location(w, topX(id, w), w.getMaxHeight(), topZ(id, w));
        return getPlotTopLoc(w, id);
    }

    @Override
    public Location getBottom(World w, String id) {
        //return new Location(w, bottomX(id, w), 0, bottomZ(id, w));
        return getPlotBottomLoc(w, id);
    }

    @Override
    public void clear(World w, String id) {
        clear(getBottom(w, id), getTop(w, id));
    }

    @Override
    public Long[] clear(World w, String id, long maxBlocks, boolean clearEntities, Long[] start) {
        return clear(getBottom(w, id), getTop(w, id), maxBlocks, clearEntities, start);
    }

    @Override
    public void clear(Location bottom, Location top) {
        GenMapInfo gmi = getMap(bottom);

        int bottomX = bottom.getBlockX();
        int topX = top.getBlockX();
        int bottomZ = bottom.getBlockZ();
        int topZ = top.getBlockZ();

        World w = bottom.getWorld();

        clearEntities(bottom, top);

        int maxY = w.getMaxHeight();

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block block = w.getBlockAt(x, 0, z);

                block.setBiome(Biome.PLAINS);

                for (int y = maxY; y >= 0; y--) {
                    block = w.getBlockAt(x, y, z);

                    if (block.getType() == Material.BEACON
                            || block.getType() == Material.CHEST
                            || block.getType() == Material.BREWING_STAND
                            || block.getType() == Material.DISPENSER
                            || block.getType() == Material.FURNACE
                            || block.getType() == Material.DROPPER
                            || block.getType() == Material.HOPPER) {
                        InventoryHolder holder = (InventoryHolder) block.getState();
                        holder.getInventory().clear();
                    }

                    if (block.getType() == Material.JUKEBOX) {
                        Jukebox jukebox = (Jukebox) block.getState();
                        //Remove once they fix the NullPointerException
                        try {
                            jukebox.setPlaying(Material.AIR);
                        } catch (Exception e) {
                        }
                    }

                    if (y == 0) {
                        block.setTypeId(gmi.BottomBlockId);
                    } else if (y < gmi.RoadHeight) {
                        block.setTypeId(gmi.PlotFillingBlockId);
                    } else if (y == gmi.RoadHeight) {
                        block.setTypeId(gmi.PlotFloorBlockId);
                    } else {
                        if (y == (gmi.RoadHeight + 1)
                                && (x == bottomX - 1
                                || x == topX + 1
                                || z == bottomZ - 1
                                || z == topZ + 1)) {
                            //block.setTypeId(pmi.WallBlockId);
                        } else {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    public void clearEntities(Location bottom, Location top) {
        int bottomX = bottom.getBlockX();
        int topX = top.getBlockX();
        int bottomZ = bottom.getBlockZ();
        int topZ = top.getBlockZ();

        World w = bottom.getWorld();

        int minChunkX = (int) Math.floor((double) bottomX / 16);
        int maxChunkX = (int) Math.floor((double) topX / 16);
        int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ = (int) Math.floor((double) topZ / 16);

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                Chunk chunk = w.getChunkAt(cx, cz);

                for (Entity e : chunk.getEntities()) {
                    Location eloc = e.getLocation();

                    if (!(e instanceof Player) && eloc.getBlockX() >= bottom.getBlockX() && eloc.getBlockX() <= top.getBlockX()
                            && eloc.getBlockZ() >= bottom.getBlockZ() && eloc.getBlockZ() <= top.getBlockZ()) {
                        e.remove();
                    }
                }
            }
        }
    }

    @Override
    public Long[] clear(Location bottom, Location top, long maxBlocks, boolean clearEntities, Long[] start) {
        if (clearEntities) {
            clearEntities(bottom, top);
        }

        GenMapInfo gmi = getMap(bottom);

        int bottomX = 0;
        int topX = top.getBlockX();
        int bottomZ = 0;
        int topZ = top.getBlockZ();
        int maxY = 0;

        long nbBlockCleared = 0;
        long nbBlockClearedBefore = 0;

        World w = bottom.getWorld();

        if (start == null) {
            bottomX = bottom.getBlockX();
            maxY = w.getMaxHeight();
            bottomZ = bottom.getBlockZ();
        } else {
            bottomX = start[0].intValue();
            maxY = start[1].intValue() - 1;
            bottomZ = start[2].intValue();
            nbBlockClearedBefore = start[3];
        }

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block block = w.getBlockAt(x, 0, z);

                block.setBiome(Biome.PLAINS);

                for (int y = maxY; y >= 0; y--) {
                    block = w.getBlockAt(x, y, z);

                    if (block.getType() == Material.BEACON
                            || block.getType() == Material.CHEST
                            || block.getType() == Material.BREWING_STAND
                            || block.getType() == Material.DISPENSER
                            || block.getType() == Material.FURNACE
                            || block.getType() == Material.DROPPER
                            || block.getType() == Material.HOPPER) {
                        InventoryHolder holder = (InventoryHolder) block.getState();
                        holder.getInventory().clear();
                    }

                    if (block.getType() == Material.JUKEBOX) {
                        Jukebox jukebox = (Jukebox) block.getState();
                        //Remove once they fix the NullPointerException
                        try {
                            jukebox.setPlaying(Material.AIR);
                        } catch (Exception e) {
                        }
                    }

                    if (y == 0) {
                        block.setTypeId(gmi.BottomBlockId);
                    } else if (y < gmi.RoadHeight) {
                        block.setTypeId(gmi.PlotFillingBlockId);
                    } else if (y == gmi.RoadHeight) {
                        block.setTypeId(gmi.PlotFloorBlockId);
                    } else {
                        if (y == (gmi.RoadHeight + 1)
                                && (x == bottomX - 1
                                || x == topX + 1
                                || z == bottomZ - 1
                                || z == topZ + 1)) {
                            //block.setTypeId(pmi.WallBlockId);
                        } else {
                            //block.setTypeIdAndData(0, (byte) 0, false); //.setType(Material.AIR);
                            block.setType(Material.AIR);
                        }
                    }

                    nbBlockCleared++;

                    if (nbBlockCleared >= maxBlocks) {
                        return new Long[]{(long) x, (long) y, (long) z, nbBlockClearedBefore + nbBlockCleared};
                    }
                }
                maxY = w.getMaxHeight();
            }
            bottomZ = bottom.getBlockZ();
        }

        return null;
    }

    @Override
    public void adjustPlotFor(World w, String id, boolean Claimed, boolean Protected, boolean Auctionned, boolean ForSale) {
        //Plot plot = getPlotById(l);
        //World w = l.getWorld();
        GenMapInfo pmi = getMap(w);

        List<String> wallids = new ArrayList<String>();

        String auctionwallid = pmi.AuctionWallBlockId;
        String forsalewallid = pmi.ForSaleWallBlockId;

        if (Protected) {
            wallids.add(pmi.ProtectedWallBlockId);
        }
        if (Auctionned && !wallids.contains(auctionwallid)) {
            wallids.add(auctionwallid);
        }
        if (ForSale && !wallids.contains(forsalewallid)) {
            wallids.add(forsalewallid);
        }

        if (wallids.isEmpty()) {
            wallids.add("" + pmi.WallBlockId + ":" + pmi.WallBlockValue);
        }

        int ctr = 0;

        Location bottom = getPlotBottomLoc(w, id);
        Location top = getPlotTopLoc(w, id);

        int x;
        int z;

        String currentblockid;
        Block block;

        for (x = bottom.getBlockX() - 1; x < top.getBlockX() + 1; x++) {
            z = bottom.getBlockZ() - 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == wallids.size() - 1) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (z = bottom.getBlockZ() - 1; z < top.getBlockZ() + 1; z++) {
            x = top.getBlockX() + 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == wallids.size() - 1) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (x = top.getBlockX() + 1; x > bottom.getBlockX() - 1; x--) {
            z = top.getBlockZ() + 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == wallids.size() - 1) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }

        for (z = top.getBlockZ() + 1; z > bottom.getBlockZ() - 1; z--) {
            x = bottom.getBlockX() - 1;
            currentblockid = wallids.get(ctr);
            ctr = (ctr == wallids.size() - 1) ? 0 : ctr + 1;
            block = w.getBlockAt(x, pmi.RoadHeight + 1, z);
            setWall(block, currentblockid);
        }
    }

    private void setWall(Block block, String currentblockid) {

        int blockId;
        byte blockData = 0;
        GenMapInfo pmi = getMap(block);

        if (currentblockid.contains(":")) {
            try {
                blockId = Integer.parseInt(currentblockid.substring(0, currentblockid.indexOf(":")));
                blockData = Byte.parseByte(currentblockid.substring(currentblockid.indexOf(":") + 1));
            } catch (NumberFormatException e) {
                blockId = pmi.WallBlockId;
                blockData = pmi.WallBlockValue;
            }
        } else {
            try {
                blockId = Integer.parseInt(currentblockid);
            } catch (NumberFormatException e) {
                blockId = pmi.WallBlockId;
            }
        }

        block.setTypeIdAndData(blockId, blockData, true);
    }

    @Override
    public boolean isBlockInPlot(String id, Location blocklocation) {
        World w = blocklocation.getWorld();
        int lowestX = Math.min(bottomX(id, w), topX(id, w));
        int highestX = Math.max(bottomX(id, w), topX(id, w));
        int lowestZ = Math.min(bottomZ(id, w), topZ(id, w));
        int highestZ = Math.max(bottomZ(id, w), topZ(id, w));

        return blocklocation.getBlockX() >= lowestX && blocklocation.getBlockX() <= highestX
                && blocklocation.getBlockZ() >= lowestZ && blocklocation.getBlockZ() <= highestZ;
    }

    @Override
    public boolean movePlot(World wFrom, World wTo, String idFrom, String idTo) {
        Location plot1Bottom = getPlotBottomLoc(wFrom, idFrom);
        Location plot2Bottom = getPlotBottomLoc(wTo, idTo);
        Location plot1Top = getPlotTopLoc(wFrom, idFrom);
        Location plot2Top = getPlotTopLoc(wTo, idTo);

        int distanceX = plot1Bottom.getBlockX() - plot2Bottom.getBlockX();
        int distanceZ = plot1Bottom.getBlockZ() - plot2Bottom.getBlockZ();

        Set<BlockInfo> lastblocks = new HashSet<BlockInfo>();

        int bottomX = plot1Bottom.getBlockX();
        int topX = plot1Top.getBlockX();
        int bottomZ = plot1Bottom.getBlockZ();
        int topZ = plot1Top.getBlockZ();

        for (int x = bottomX; x <= topX; x++) {
            for (int z = bottomZ; z <= topZ; z++) {
                Block plot1Block = wFrom.getBlockAt(x, 0, z);
                Block plot2Block = wTo.getBlockAt(x - distanceX, 0, z - distanceZ);

                String plot1Biome = plot1Block.getBiome().name();
                String plot2Biome = plot2Block.getBiome().name();

                plot1Block.setBiome(Biome.valueOf(plot2Biome));
                plot2Block.setBiome(Biome.valueOf(plot1Biome));

                for (int y = 0; y < wFrom.getMaxHeight(); y++) {
                    plot1Block = wFrom.getBlockAt(x, y, z);
                    int plot1Type = plot1Block.getTypeId();
                    byte plot1Data = plot1Block.getData();

                    plot2Block = wTo.getBlockAt(x - distanceX, y, z - distanceZ);
                    int plot2Type = plot2Block.getTypeId();
                    byte plot2Data = plot2Block.getData();

                    if (!blockPlacedLast.contains(plot2Type)) {
                        plot1Block.setTypeIdAndData(plot2Type, plot2Data, false);
                    } else {
                        plot1Block.setTypeId(0, false);
                        lastblocks.add(new BlockInfo(wFrom, x, y, z, plot2Type, plot2Data));
                    }

                    if (!blockPlacedLast.contains(plot1Type)) {
                        plot2Block.setTypeIdAndData(plot1Type, plot1Data, false);
                    } else {
                        plot2Block.setTypeId(0, false);
                        lastblocks.add(new BlockInfo(wTo, x - distanceX, y, z - distanceZ, plot1Type, plot1Data));
                    }
                }
            }
        }

        for (BlockInfo bi : lastblocks) {
            Block block = bi.w.getBlockAt(bi.x, bi.y, bi.z);
            block.setTypeIdAndData(bi.id, bi.data, false);
        }

        lastblocks.clear();
        lastblocks = null;

        //Move entities
        int minChunkX1 = (int) Math.floor((double) bottomX / 16);
        int maxChunkX1 = (int) Math.floor((double) topX / 16);
        int minChunkZ1 = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ1 = (int) Math.floor((double) topZ / 16);

        int minChunkX2 = (int) Math.floor((double) (bottomX - distanceX) / 16);
        int maxChunkX2 = (int) Math.floor((double) (topX - distanceX) / 16);
        int minChunkZ2 = (int) Math.floor((double) (bottomZ - distanceZ) / 16);
        int maxChunkZ2 = (int) Math.floor((double) (topZ - distanceZ) / 16);

        Set<Entity> entities1 = new HashSet<Entity>();
        Set<Entity> entities2 = new HashSet<Entity>();

        for (int cx = minChunkX1; cx <= maxChunkX1; cx++) {
            for (int cz = minChunkZ1; cz <= maxChunkZ1; cz++) {
                Chunk chunk = wFrom.getChunkAt(cx, cz);

                for (Entity e : chunk.getEntities()) {
                    Location eloc = e.getLocation();

                    if (!(e instanceof Player) /*&& !(e instanceof Hanging)*/ && eloc.getBlockX() >= plot1Bottom.getBlockX() && eloc.getBlockX() <= plot1Top.getBlockX()
                            && eloc.getBlockZ() >= plot1Bottom.getBlockZ() && eloc.getBlockZ() <= plot1Top.getBlockZ()) {
                        entities1.add(e);
                    }
                }
            }
        }

        for (int cx = minChunkX2; cx <= maxChunkX2; cx++) {
            for (int cz = minChunkZ2; cz <= maxChunkZ2; cz++) {
                Chunk chunk = wFrom.getChunkAt(cx, cz);

                for (Entity e : chunk.getEntities()) {
                    Location eloc = e.getLocation();

                    if (!(e instanceof Player) /*&& !(e instanceof Hanging)*/ && eloc.getBlockX() >= plot2Bottom.getBlockX() && eloc.getBlockX() <= plot2Top.getBlockX()
                            && eloc.getBlockZ() >= plot2Bottom.getBlockZ() && eloc.getBlockZ() <= plot2Top.getBlockZ()) {
                        entities2.add(e);
                    }
                }
            }
        }

        for (Entity e : entities1) {
            Location l = e.getLocation();
            Location newl = new Location(wTo, l.getX() - distanceX, l.getY(), l.getZ() - distanceZ);

            if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame i = ((ItemFrame) e);
                BlockFace bf = i.getFacing();
                ItemStack is = i.getItem();
                Rotation rot = i.getRotation();

                i.teleport(newl);
                i.setItem(is);
                i.setRotation(rot);
                i.setFacingDirection(bf, true);
            } else if (e.getType() == EntityType.PAINTING) {
                Painting p = ((Painting) e);
                BlockFace bf = p.getFacing();
                int[] mod = getPaintingMod(p.getArt(), bf);
                if (mod != null) {
                    newl = newl.add(mod[0], mod[1], mod[2]);
                }
                p.teleport(newl);
                p.setFacingDirection(bf, true);
            } else {
                e.teleport(newl);
            }
        }

        for (Entity e : entities2) {
            Location l = e.getLocation();
            Location newl = new Location(wFrom, l.getX() + distanceX, l.getY(), l.getZ() + distanceZ);

            if (e.getType() == EntityType.ITEM_FRAME) {
                ItemFrame i = ((ItemFrame) e);
                BlockFace bf = i.getFacing();
                ItemStack is = i.getItem();
                Rotation rot = i.getRotation();

                i.teleport(newl);
                i.setItem(is);
                i.setRotation(rot);
                i.setFacingDirection(bf, true);

            } else if (e.getType() == EntityType.PAINTING) {
                Painting p = ((Painting) e);
                BlockFace bf = p.getFacing();
                int[] mod = getPaintingMod(p.getArt(), bf);
                if (mod != null) {
                    newl = newl.add(mod[0], mod[1], mod[2]);
                }
                p.teleport(newl);
                p.setFacingDirection(bf, true);
            } else {
                e.teleport(newl);
            }
        }

        return true;
    }

    @Override
    public int bottomX(String id, World w) {
        return getPlotBottomLoc(w, id).getBlockX();
    }

    @Override
    public int bottomZ(String id, World w) {
        return getPlotBottomLoc(w, id).getBlockZ();
    }

    @Override
    public int topX(String id, World w) {
        return getPlotTopLoc(w, id).getBlockX();
    }

    @Override
    public int topZ(String id, World w) {
        return getPlotTopLoc(w, id).getBlockZ();
    }

    @Override
    public boolean isValidId(String id) {
        String[] coords = id.split(";");

        if (coords.length != 2) {
            return false;
        } else {
            try {
                Integer.parseInt(coords[0]);
                Integer.parseInt(coords[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    @Override
    public void regen(World w, String id, CommandSender sender) {
        int bottomX = bottomX(id, w);
        int topX = topX(id, w);
        int bottomZ = bottomZ(id, w);
        int topZ = topZ(id, w);

        int minChunkX = (int) Math.floor((double) bottomX / 16);
        int maxChunkX = (int) Math.floor((double) topX / 16);
        int minChunkZ = (int) Math.floor((double) bottomZ / 16);
        int maxChunkZ = (int) Math.floor((double) topZ / 16);

        HashMap<Location, Biome> biomes = new HashMap<Location, Biome>();

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            int xx = cx << 4;

            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                int zz = cz << 4;

                BlockState[][][] blocks = new BlockState[16][16][w.getMaxHeight()];

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        biomes.put(new Location(w, x + xx, 0, z + zz), w.getBiome(x + xx, z + zz));

                        for (int y = 0; y < w.getMaxHeight(); y++) {
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

                try {
                    w.regenerateChunk(cx, cz);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < w.getMaxHeight(); y++) {
                            if ((x + xx) < bottomX || (x + xx) > topX || (z + zz) < bottomZ || (z + zz) > topZ) {
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

        for (Location loc : biomes.keySet()) {
            int x = loc.getBlockX();
            int z = loc.getBlockX();

            w.setBiome(x, z, biomes.get(loc));
        }
    }

    @Override
    public Location getPlotHome(World w, String id) {
        GenMapInfo pmi = getMap(w);

        if (pmi != null) {
            return new Location(w, bottomX(id, w) + (topX(id, w) - bottomX(id, w)) / 2, pmi.RoadHeight + 2, bottomZ(id, w) - 2);
        } else {
            return w.getSpawnLocation();
        }
    }

    private GenMapInfo getMap(Location loc) {
        return getMap(loc.getWorld());
    }

    private GenMapInfo getMap(Block b) {
        return getMap(b.getWorld());
    }

    private GenMapInfo getMap(World w) {
        return getMap(w.getName());
    }

    public GenMapInfo getMap(String worldname) {
        return genplotmaps.get(worldname.toLowerCase());
    }

    @Override
    public int getPlotSize(String worldname) {
        return getMap(worldname).PlotSize;
    }

    @Override
    public boolean createConfig(String worldname, Map<String, String> args, CommandSender cs) {
        FileConfiguration config = new YamlConfiguration();
        File configfile = new File(plugin.getConfigPath(), "config.yml");
        try {
            config.load(configfile);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            plugin.getLogger().severe(plugin.PREFIX + "can't read configuration file");
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe(plugin.PREFIX + "invalid configuration format");
            e.printStackTrace();
        }

        ConfigurationSection worlds;

        if (!config.contains("worlds")) {
            worlds = config.createSection("worlds");
        } else {
            worlds = config.getConfigurationSection("worlds");
        }

        GenMapInfo tempPlotInfo = new GenMapInfo();
        ConfigurationSection currworld = worlds.getConfigurationSection(worldname);

        if (currworld == null) {
            currworld = worlds.createSection(worldname);
        }

        tempPlotInfo.PathWidth = Integer.parseInt(args.get("PathWidth"));
        tempPlotInfo.PlotSize = Integer.parseInt(args.get("PlotSize"));

        tempPlotInfo.XTranslation = Integer.parseInt(args.get("XTranslation"));
        tempPlotInfo.ZTranslation = Integer.parseInt(args.get("ZTranslation"));

        tempPlotInfo.BottomBlockId = plugin.getBlockId(args.get("BottomBlockId"));
        tempPlotInfo.BottomBlockValue = plugin.getBlockValue(args.get("BottomBlockId"));
        tempPlotInfo.WallBlockId = plugin.getBlockId(args.get("WallBlockId"));
        tempPlotInfo.WallBlockValue = plugin.getBlockValue(args.get("WallBlockId"));
        tempPlotInfo.PlotFloorBlockId = plugin.getBlockId(args.get("PlotFloorBlockId"));
        tempPlotInfo.PlotFloorBlockValue = plugin.getBlockValue(args.get("PlotFloorBlockId"));
        tempPlotInfo.PlotFillingBlockId = plugin.getBlockId(args.get("PlotFillingBlockId"));
        tempPlotInfo.PlotFillingBlockValue = plugin.getBlockValue(args.get("PlotFillingBlockId"));
        tempPlotInfo.RoadMainBlockId = plugin.getBlockId(args.get("RoadMainBlockId"));
        tempPlotInfo.RoadMainBlockValue = plugin.getBlockValue(args.get("RoadMainBlockId"));
        tempPlotInfo.RoadStripeBlockId = plugin.getBlockId(args.get("RoadStripeBlockId"));
        tempPlotInfo.RoadStripeBlockValue = plugin.getBlockValue(args.get("RoadStripeBlockId"));

        tempPlotInfo.RoadHeight = Integer.parseInt(args.get("RoadHeight"));

        tempPlotInfo.ProtectedWallBlockId = args.get("ProtectedWallBlockId");
        tempPlotInfo.ForSaleWallBlockId = args.get("ForSaleWallBlockId");
        tempPlotInfo.AuctionWallBlockId = args.get("AuctionWallBlockId");

        currworld.set("PathWidth", Integer.parseInt(args.get("PathWidth")));
        currworld.set("PlotSize", Integer.parseInt(args.get("PlotSize")));
        currworld.set("XTranslation", Integer.parseInt(args.get("XTranslation")));
        currworld.set("ZTranslation", Integer.parseInt(args.get("ZTranslation")));
        currworld.set("BottomBlockId", args.get("BottomBlockId"));
        currworld.set("WallBlockId", args.get("WallBlockId"));
        currworld.set("PlotFloorBlockId", args.get("PlotFloorBlockId"));
        currworld.set("PlotFillingBlockId", args.get("PlotFillingBlockId"));
        currworld.set("RoadMainBlockId", args.get("RoadMainBlockId"));
        currworld.set("RoadStripeBlockId", args.get("RoadStripeBlockId"));

        currworld.set("RoadHeight", Integer.parseInt(args.get("RoadHeight")));
        currworld.set("WorldHeight", null);

        currworld.set("ProtectedWallBlockId", tempPlotInfo.ProtectedWallBlockId);
        currworld.set("ForSaleWallBlockId", tempPlotInfo.ForSaleWallBlockId);
        currworld.set("AuctionWallBlockId", tempPlotInfo.AuctionWallBlockId);

        worlds.set(worldname, currworld);

        genplotmaps.put(worldname.toLowerCase(), tempPlotInfo);

        config.set("worlds", worlds);

        try {
            config.save(configfile);
        } catch (IOException e) {
            plugin.getLogger().severe(plugin.PREFIX + "error writting configurations");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Map<String, String> getDefaultGenerationConfig() {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("PathWidth", "7");
        parameters.put("PlotSize", "32");
        parameters.put("RoadHeight", "64");
        parameters.put("BottomBlockId", "7");
        parameters.put("WallBlockId", "44");
        parameters.put("PlotFloorBlockId", "2");
        parameters.put("PlotFillingBlockId", "3");
        parameters.put("RoadMainBlockId", "5");
        parameters.put("RoadStripeBlockId", "5:2");
        parameters.put("XTranslation", "0");
        parameters.put("ZTranslation", "0");
        parameters.put("ProtectedWallBlockId", "44:4");
        parameters.put("ForSaleWallBlockId", "44:1");
        parameters.put("AuctionWallBlockId", "44:1");

        return parameters;
    }

    private int[] getPaintingMod(Art a, BlockFace bf) {
        int H = a.getBlockHeight();
        int W = a.getBlockWidth();

        //Same for all faces
        if (H == 2 && W == 1) {
            return new int[]{0, -1, 0};
        }

        switch (bf) {
            case WEST:
                if (H == 3 && W == 4 || H == 1 && W == 2) {
                    return new int[]{0, 0, -1};
                } else if (H == 2 && W == 2 || H == 4 && W == 4 || H == 2 && W == 4) {
                    return new int[]{0, -1, -1};
                }

                break;
            case SOUTH:
                if (H == 3 && W == 4 || H == 1 && W == 2) {
                    return new int[]{-1, 0, 0};
                } else if (H == 2 && W == 2 || H == 4 && W == 4 || H == 2 && W == 4) {
                    return new int[]{-1, -1, 0};
                }

                break;
            case EAST:
                if (H == 2 && W == 2 || H == 4 && W == 4 || H == 2 && W == 4) {
                    return new int[]{0, -1, 0};
                }

                break;
            case NORTH:
                if (H == 2 && W == 2 || H == 4 && W == 4 || H == 2 && W == 4) {
                    return new int[]{0, -1, 0};
                }

                break;
            default:
                return new int[]{0, 0, 0};
        }

        return new int[]{0, 0, 0};
    }
}
