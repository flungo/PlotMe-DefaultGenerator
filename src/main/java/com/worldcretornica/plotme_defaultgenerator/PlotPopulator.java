package com.worldcretornica.plotme_defaultgenerator;

import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.BASE_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.FILL_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.GROUND_LEVEL;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PATH_WIDTH;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PLOT_FLOOR_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.PLOT_SIZE;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.ROAD_ALT_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.ROAD_MAIN_BLOCK;
import static com.worldcretornica.plotme_defaultgenerator.DefaultWorldConfigPath.WALL_BLOCK;
import java.util.Random;
import me.flungo.bukkit.plotme.abstractgenerator.WorldGenConfig;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

public class PlotPopulator extends BlockPopulator {

    private final PlotMe_DefaultGenerator plugin;
    private final String worldname;

    public PlotPopulator(PlotMe_DefaultGenerator plugin, String worldname) {
        this.plugin = plugin;
        this.worldname = worldname;
    }

    @Override
    public void populate(World w, Random rand, Chunk chunk) {
        WorldGenConfig wgc = plugin.getGeneratorManager().getWGC(worldname);

        final int plotsize = wgc.getInt(PLOT_SIZE);
        final int pathsize = wgc.getInt(PATH_WIDTH);
        final int roadheight = wgc.getInt(GROUND_LEVEL);
        final byte bottom = wgc.getBlockRepresentation(BASE_BLOCK).getData();
        final byte wall = wgc.getBlockRepresentation(WALL_BLOCK).getData();
        final byte floor1 = wgc.getBlockRepresentation(ROAD_MAIN_BLOCK).getData();
        final byte floor2 = wgc.getBlockRepresentation(ROAD_ALT_BLOCK).getData();
        final byte plotfloor = wgc.getBlockRepresentation(PLOT_FLOOR_BLOCK).getData();
        final byte filling = wgc.getBlockRepresentation(FILL_BLOCK).getData();

        int cx = chunk.getX();
        int cz = chunk.getZ();

        int xx = cx << 4;
        int zz = cz << 4;

        double size = plotsize + pathsize;
        int valx;
        int valz;

        double n1;
        double n2;
        double n3;
        int mod2 = 0;
        int mod1 = 1;

        if (pathsize % 2 == 1) {
            n1 = Math.ceil(((double) pathsize) / 2) - 2;
            n2 = Math.ceil(((double) pathsize) / 2) - 1;
            n3 = Math.ceil(((double) pathsize) / 2);
        } else {
            n1 = Math.floor(((double) pathsize) / 2) - 2;
            n2 = Math.floor(((double) pathsize) / 2) - 1;
            n3 = Math.floor(((double) pathsize) / 2);
        }

        if (pathsize % 2 == 1) {
            mod2 = -1;
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = roadheight + 2;

                w.setBiome(x + xx, z + zz, Biome.PLAINS);

                valx = (cx * 16 + x);
                valz = (cz * 16 + z);

                for (int y = 0; y < height; y++) {
                    if (y == 0) {
                        //result[(x * 16 + z) * 128 + y] = bottom;
                        setBlock(w, x + xx, y, z + zz, bottom);

                    } else if (y == roadheight) {
                        if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) //middle+3
                        {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                setBlock(w, x + xx, y, z + zz, floor1);
                            } else {
                                //result[(x * 16 + z) * 128 + y] = filling; //filling
                                setBlock(w, x + xx, y, z + zz, filling);
                            }
                        } else if ((valx - n2 + mod1) % size == 0 || (valx + n2 + mod2) % size == 0) //middle+2
                        {
                            if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0
                                    || (valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                setBlock(w, x + xx, y, z + zz, floor1);
                            } else {
                                //result[(x * 16 + z) * 128 + y] = floor2; //floor2
                                setBlock(w, x + xx, y, z + zz, floor2);
                            }
                        } else if ((valx - n1 + mod1) % size == 0 || (valx + n1 + mod2) % size == 0) //middle+2
                        {
                            if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0
                                    || (valz - n1 + mod1) % size == 0 || (valz + n1 + mod2) % size == 0) {
                                //result[(x * 16 + z) * 128 + y] = floor2; //floor2
                                setBlock(w, x + xx, y, z + zz, floor2);
                            } else {
                                //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                setBlock(w, x + xx, y, z + zz, floor1);
                            }
                        } else {
                            boolean found = false;
                            for (double i = n1; i >= 0; i--) {
                                if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                setBlock(w, x + xx, y, z + zz, floor1);
                            } else {
                                if ((valz - n2 + mod1) % size == 0 || (valz + n2 + mod2) % size == 0) {
                                    //result[(x * 16 + z) * 128 + y] = floor2; //floor2
                                    setBlock(w, x + xx, y, z + zz, floor2);
                                } else {
                                    boolean found2 = false;
                                    for (double i = n1; i >= 0; i--) {
                                        if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                            found2 = true;
                                            break;
                                        }
                                    }

                                    if (found2) {
                                        //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                        setBlock(w, x + xx, y, z + zz, floor1);
                                    } else {
                                        boolean found3 = false;
                                        for (double i = n3; i >= 0; i--) {
                                            if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                                found3 = true;
                                                break;
                                            }
                                        }

                                        if (found3) {
                                            //result[(x * 16 + z) * 128 + y] = floor1; //floor1
                                            setBlock(w, x + xx, y, z + zz, floor1);
                                        } else {
                                            //result[(x * 16 + z) * 128 + y] = plotfloor; //plotfloor
                                            setBlock(w, x + xx, y, z + zz, plotfloor);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (y == (roadheight + 1)) {

                        if ((valx - n3 + mod1) % size == 0 || (valx + n3 + mod2) % size == 0) //middle+3
                        {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if ((valz - i + mod1) % size == 0 || (valz + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                //result[(x * 16 + z) * 128 + y] = air;
                                //setBlock(result, x, y, z, air);
                            } else {
                                //result[(x * 16 + z) * 128 + y] = wall;
                                setBlock(w, x + xx, y, z + zz, wall);
                            }
                        } else {
                            boolean found = false;
                            for (double i = n2; i >= 0; i--) {
                                if ((valx - i + mod1) % size == 0 || (valx + i + mod2) % size == 0) {
                                    found = true;
                                    break;
                                }
                            }

                            if (found) {
                                //result[(x * 16 + z) * 128 + y] = air;
                                //setBlock(result, x, y, z, air);
                            } else {
                                if ((valz - n3 + mod1) % size == 0 || (valz + n3 + mod2) % size == 0) {
                                    //result[(x * 16 + z) * 128 + y] = wall;
                                    setBlock(w, x + xx, y, z + zz, wall);
                                } else {
                                    //result[(x * 16 + z) * 128 + y] = air;
                                    //setBlock(result, x, y, z, air);
                                }
                            }
                        }
                    } else {
                        //result[(x * 16 + z) * 128 + y] = filling;
                        setBlock(w, x + xx, y, z + zz, filling);
                    }
                }
            }
        }
    }

    private void setBlock(World w, int x, int y, int z, byte val) {
        if (val != 0) {
            w.getBlockAt(x, y, z).setData(val);
        }
    }

}
