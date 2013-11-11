/*
 * Copyright (C) 2013 Fabrizio Lungo <fab@lungo.co.uk> - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Created by Fabrizio Lungo <fab@lungo.co.uk>, November 2013
 */
package com.worldcretornica.plotme_defaultgenerator;

import me.flungo.bukkit.plotme.abstractgenerator.AbstractWorldConfigPath;
import me.flungo.bukkit.plotme.abstractgenerator.WorldConfigPath;

/**
 *
 * @author Fabrizio Lungo <fab@lungo.co.uk>
 */
public enum DefaultWorldConfigPath implements WorldConfigPath {

    PLOT_SIZE(AbstractWorldConfigPath.PLOT_SIZE),
    X_TRANSLATION(AbstractWorldConfigPath.X_TRANSLATION),
    Z_TRANSLATION(AbstractWorldConfigPath.Z_TRANSLATION),
    GROUND_LEVEL(AbstractWorldConfigPath.GROUND_LEVEL),
    BASE_BLOCK(AbstractWorldConfigPath.BASE_BLOCK),
    FILL_BLOCK(AbstractWorldConfigPath.FILL_BLOCK),
    PATH_WIDTH("PathWidth", 7),
    PLOT_FLOOR_BLOCK("PlotFloorBlock", "2"),
    ROAD_MAIN_BLOCK("RoadMainBlock", "5"),
    ROAD_ALT_BLOCK("RoadAltBlock", "5:2"),
    WALL_BLOCK("WallBlock", "44"),
    PROTECTED_WALL_BLOCK("ProtectedWallBlock", "44:4"),
    AUCTION_WALL_BLOCK("AuctionWallBlock", "44:1"),
    FOR_SALE_WALL_BLOCK("ForSaleWallBlock", "44:1");

    public final String path;
    public final Object def;

    private DefaultWorldConfigPath(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    private DefaultWorldConfigPath(AbstractWorldConfigPath awcp) {
        this.path = awcp.path;
        this.def = awcp.def;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public Object def() {
        return def;
    }
}
