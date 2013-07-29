/*******************************************************************************
 * WorldGenArrowTrees.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ForgeDirection;

/**
 * Generates the special trees added by ArrowsPlus.
 */
public class WorldGenArrowTrees extends WorldGenerator
{
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;

    /** The metadata value of the wood to use in tree generation. */
    private final int metaWood;

    /** The metadata value of the leaves to use in tree generation. */
    private final int metaLeaves;

    /**
     * Constructor
     * 
     * @param 	shouldNotify	Should nearby blocks be notified of the change?
     * @param 	minimumHeight	The tree's minimum height.
     * @param 	woodMetadata	The metadata value of the tree.
     */
    public WorldGenArrowTrees(boolean shouldNotify, int minimumHeight, int woodMetadata)
    {
        super(shouldNotify);
        this.minTreeHeight = minimumHeight;
        this.metaWood = woodMetadata;
        this.metaLeaves = woodMetadata;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z)
    {
        int l = random.nextInt(3) + this.minTreeHeight;
        
        boolean flag = true;

        if (y >= 1 && y + l + 1 <= 256)
        {
            int i1;
            byte b0;
            int j1;
            int k1;

            for (i1 = y; i1 <= y + 1 + l; ++i1)
            {
                b0 = 1;

                if (i1 == y)
                {
                    b0 = 0;
                }

                if (i1 >= y + 1 + l - 2)
                {
                    b0 = 2;
                }

                for (int l1 = x - b0; l1 <= x + b0 && flag; ++l1)
                {
                    for (j1 = z - b0; j1 <= z + b0 && flag; ++j1)
                    {
                        if (i1 >= 0 && i1 < 256)
                        {
                            k1 = world.getBlockId(l1, i1, j1);

                            Block block = Block.blocksList[k1];
                            boolean isAir = world.isAirBlock(l1, i1, j1);

                            if (!isAir &&
                               !block.isLeaves(world, l1, i1, j1) &&
                                k1 != Block.grass.blockID &&
                                k1 != Block.dirt.blockID &&
                               !block.isWood(world, l1, i1, j1))
                            {
                                flag = false;
                            }
                        }
                        
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            
            else
            {
                i1 = world.getBlockId(x, y - 1, z);
                Block soil = Block.blocksList[i1];
                boolean isSoil = (soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, (BlockSapling)Block.sapling));

                if (isSoil && y < 256 - l - 1)
                {
                    soil.onPlantGrow(world, x, y - 1, z, x, y, z);
                    b0 = 3;
                    byte b1 = 0;
                    int i2;
                    int j2;
                    int k2;

                    for (j1 = y - b0 + l; j1 <= y + l; ++j1)
                    {
                        k1 = j1 - (y + l);
                        i2 = b1 + 1 - k1 / 2;

                        for (j2 = x - i2; j2 <= x + i2; ++j2)
                        {
                            k2 = j2 - x;

                            for (int l2 = z - i2; l2 <= z + i2; ++l2)
                            {
                                int i3 = l2 - z;

                                if (Math.abs(k2) != i2 || Math.abs(i3) != i2 || random.nextInt(2) != 0 && k1 != 0)
                                {
                                    int j3 = world.getBlockId(j2, j1, l2);
                                    Block block = Block.blocksList[j3];

                                    if (block == null || block.canBeReplacedByLeaves(world, j2, j1, l2))
                                    {
                                        this.setBlockAndMetadata(world, j2, j1, l2, ArrowsPlus.instance.blockArrowTreeLeaves.blockID, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (j1 = 0; j1 < l; ++j1)
                    {
                        k1 = world.getBlockId(x, y + j1, z);

                        Block block = Block.blocksList[k1];

                        if (k1 == 0 || block == null || block.isLeaves(world, x, y + j1, z))
                        {
                            this.setBlockAndMetadata(world, x, y + j1, z, ArrowsPlus.instance.blockArrowTreeLog.blockID, this.metaWood);
                        }
                    }

                    return true;
                }
                
                else
                {
                    return false;
                }
            }
        }
        
        else
        {
            return false;
        }
    }
}
