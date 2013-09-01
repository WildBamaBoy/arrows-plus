/*******************************************************************************
 * BlockSaplingBase.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import arrowsplus.core.ArrowsPlus;
import arrowsplus.gen.WorldGenArrowTrees;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Base class for all saplings. Set up so that each sapling doesn't need its own class.
 */
public class BlockSaplingBase extends BlockFlower
{
	/** Possible icons for saplings. */
	public static Icon[] saplingIcons;

	/** The meta value of the wood this sapling represents. */
	public int saplingType;

	/**
	 * Constructor
	 * 
	 * @param 	itemId	The sapling's item ID.
	 * @param 	damage	The sapling's damage value.
	 */
	public BlockSaplingBase(int itemId, int damage) 
	{
		super(itemId);
		this.saplingType = damage;
		this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);
		this.setCreativeTab(ArrowsPlus.instance.tabArrowsPlus);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote)
		{
			super.updateTick(world, x, y, z, random);

			if (world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(7) == 0)
			{
				this.tryGrow(world, x, y, z, random);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int metadata)
	{
		return this.saplingIcons[saplingType];
	}

	@Override
	public int damageDropped(int damage)
	{
		return saplingType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		this.saplingIcons = new Icon[ArrowsPlus.instance.woodNames.length];

		for (int i = 0; i < this.saplingIcons.length; ++i)
		{
			this.saplingIcons[i] = iconRegister.registerIcon("arrowsplus:tree_" + ArrowsPlus.instance.woodNames[i] + "_sapling");
		}
	}

	/**
	 * Tries to make the tree grow.
	 * 
	 * @param 	world	World the sapling is in.
	 * @param 	x		X coordinate of the sapling.
	 * @param 	y		Y coordinate of the sapling.
	 * @param 	z		Z coordinate of the sapling.
	 * @param 	random	Instance of Random.
	 */
	public void tryGrow(World world, int x, int y, int z, Random random)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
		}

		else
		{
			this.growTree(world, x, y, z, random);
		}
	}

	/**
	 * Make a sapling grow into a tree.
	 * 
	 * @param 	world	World the sapling is in.
	 * @param 	x		X coordinate of the sapling.
	 * @param 	y		Y coordinate of the sapling.
	 * @param 	z		Z coordinate of the sapling.
	 * @param 	random	Instance of Random.
	 */
	public void growTree(World world, int x, int y, int z, Random random)
	{
		//Ask Forge if growing a tree is denied.
		if (!TerrainGen.saplingGrowTree(world, random, x, y, z))
		{
			return;
		}

		int blockId = world.getBlockId(x, y, z);
		BlockSaplingBase sapling = (BlockSaplingBase)Block.blocksList[blockId];

		WorldGenArrowTrees worldGenerator = new WorldGenArrowTrees(false, 5, saplingType);
		world.setBlock(x, y, z, 0, 0, 1);
		worldGenerator.generate(world, random, x, y, z);
	}
}
