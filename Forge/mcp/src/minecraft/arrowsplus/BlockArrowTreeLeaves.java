/*******************************************************************************
 * BlockArrowTreeLeaves.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Controls how the leaves on "arrow trees" behave. This class manages all 12 leaves blocks.
 */
public class BlockArrowTreeLeaves extends BlockLeavesBase
{
	@SideOnly(Side.CLIENT)
	private Icon[] leavesIcons;

	/**
	 * Constructor.
	 * 
	 * @param 	id	The block ID of the leaves.
	 */
	protected BlockArrowTreeLeaves(int id) 
	{
		super(id, Material.leaves, false);
		this.setCreativeTab(ArrowsPlus.instance.tabArrowsPlus);
		this.setLightOpacity(1);
		this.setResistance(2.0F);
		this.setHardness(0.2F);
		this.setTickRandomly(true);
		this.func_111022_d("leaves");
		this.setUnlocalizedName("arrowleaves");
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side)
	{
		return true;
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.leavesIcons = new Icon[ArrowsPlus.instance.woodNames.length];

		for (int i = 0; i < this.leavesIcons.length; i++)
		{
			this.leavesIcons[i] = iconRegister.registerIcon("arrowsPlus:tree_" + ArrowsPlus.instance.woodNames[i] + "_leaves");
		}
	}

	@Override
	public Icon getIcon(int side, int metadata)
	{
		return leavesIcons[metadata];
	}

	@Override
	public void getSubBlocks(int itemId, CreativeTabs creativeTabs, List listSubBlocks)
	{
		listSubBlocks.add(new ItemStack(itemId, 1, 0));
		listSubBlocks.add(new ItemStack(itemId, 1, 1));
		listSubBlocks.add(new ItemStack(itemId, 1, 2));
		listSubBlocks.add(new ItemStack(itemId, 1, 3));
		listSubBlocks.add(new ItemStack(itemId, 1, 4));
		listSubBlocks.add(new ItemStack(itemId, 1, 5));
		listSubBlocks.add(new ItemStack(itemId, 1, 6));
		listSubBlocks.add(new ItemStack(itemId, 1, 7));
		listSubBlocks.add(new ItemStack(itemId, 1, 8));
		listSubBlocks.add(new ItemStack(itemId, 1, 9));
		listSubBlocks.add(new ItemStack(itemId, 1, 10));
		listSubBlocks.add(new ItemStack(itemId, 1, 11));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		decayLeaves(world, x, y, z, random);
	}

	@Override
	public int quantityDropped(Random random)
	{
		return random.nextInt(20) != 0 ? 0 : 1;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int x, int y, int z, int meta)
	{
		if (!world.isRemote && entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() instanceof ItemShears)
		{
			entityplayer.addStat(StatList.mineBlockStatArray[blockID], 1);
			dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.leaves.blockID, 1, meta));
		}

		else
		{
			super.harvestBlock(world, entityplayer, x, y, z, meta);
		}
	}

	@Override
	public int damageDropped(int i)
	{
		return i;
	}

	@Override
	public boolean isLeaves(World world, int x, int y, int z)
	{
		return true;
	}

	/**
	 * Simplified version of the hard to read code that checked if leaves should decay.
	 * 
	 * @param 	world	Instance of the world object.
	 * @param 	x		X position of the ticking block.
	 * @param 	y		Y position of the ticking block.
	 * @param 	z		Z position of the ticking block.
	 * @param 	random	Instance of random.
	 */
	private void decayLeaves(World world, int x, int y, int z, Random random)
	{
		int tempX, tempY, tempZ;
		int blockId, blockMeta;
		int leavesMeta = world.getBlockMetadata(x, y, z);

		if (!world.isRemote)
		{      
			for (tempX = -2; tempX <=  2; ++tempX)
			{
				for (tempY = -2; tempY <= 0; ++tempY)
				{
					for (tempZ = -2; tempZ <= 2; ++tempZ)
					{
						int distance = Math.abs(tempX) + Math.abs(tempY) + Math.abs(tempZ);

						if (distance <= 3)
						{
							blockId = world.getBlockId(x + tempX, y + tempY, z + tempZ);
							blockMeta = world.getBlockMetadata(x + tempX, y + tempY, z + tempZ);
							Block block = Block.blocksList[blockId];

							if (block != null)
							{
								if (block.canSustainLeaves(world, x + tempX, y + tempY, z + tempZ) && blockMeta == leavesMeta)
								{
									blockId = world.getBlockId(x, y - 1, z);

									//Don't want it to drop anything randomly, but will keep this in mind.
									if (blockId == 0)
									{
										if (world.rand.nextInt(20) == 0)
										{
											//this.dropBlockAsItemWithChance(world, x, y - 1, z, 0, 0.0F, 0);
										}
									}

									return;
								}
							}
						}
					}
				}
			}

			this.removeLeaves(world, x, y, z);
		}
	}

	private void removeLeaves(World world, int x, int y, int z)
	{
		EntityItem entityItem = null;

		if (world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean() && world.rand.nextBoolean())
		{
			entityItem = new EntityItem(world, x, y, z, new ItemStack(getSaplingByWoodType(world.getBlockMetadata(x, y, z)), 1, world.getBlockMetadata(x, y, z)));
		}

		else
		{
			entityItem = new EntityItem(world, x, y, z, new ItemStack(world.getBlockId(x, y, z), 1, world.getBlockMetadata(x, y, z)));
		}

		world.spawnEntityInWorld(entityItem);
		world.setBlock(x, y, z, 0);
	}

	private Block getSaplingByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return ArrowsPlus.instance.blockSaplingAspen;
		case 1: return ArrowsPlus.instance.blockSaplingCottonwood;
		case 2: return ArrowsPlus.instance.blockSaplingAlder;
		case 3: return ArrowsPlus.instance.blockSaplingSycamore;
		case 4: return ArrowsPlus.instance.blockSaplingGum;
		case 5: return ArrowsPlus.instance.blockSaplingSoftMaple;
		case 6: return ArrowsPlus.instance.blockSaplingAsh;
		case 7: return ArrowsPlus.instance.blockSaplingBeech;
		case 8: return ArrowsPlus.instance.blockSaplingHardMaple;
		case 9: return ArrowsPlus.instance.blockSaplingHickory;
		case 10: return ArrowsPlus.instance.blockSaplingMahogany;
		case 11: return ArrowsPlus.instance.blockSaplingSypherus;
		default: return null;
		}
	}
}