package com.worldcretornica.plotme_defaultgenerator;

import org.bukkit.World;

public class BlockInfo {

    public World w;
    public int x;
    public int y;
    public int z;
    public int id;
    public byte data;

    public BlockInfo(World W, int X, int Y, int Z, int Id, byte Data) {
        w = W;
        x = X;
        y = Y;
        z = Z;
        id = Id;
        data = Data;
    }
}
