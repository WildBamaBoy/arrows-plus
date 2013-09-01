/*******************************************************************************
 * BlockArrowTreeLog.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import arrowsplus.core.ArrowsPlus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Manages the logs of the "arrow trees" and how they behave.
 */
public class BlockArrowTreeLog extends Block
{
	@SideOnly(Side.CLIENT)
	private Icon[] treeIcons;

	@SideOnly(Side.CLIENT)
	private Icon[] treeIconsTop;

	/**
	 * Constructor.
	 * 
	 * @param 	id	The log's block ID.
	 */
	public BlockArrowTreeLog(int id) 
	{
		super(id, Material.wood);
		this.setCreativeTab(ArrowsPlus.instance.tabArrowsPlus);
		this.setTickRandomly(true);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		//Makes the Sypherus tree sparkle. The method handles whether it's actually supposed to or not.
		BlockArrowTreeLog.sparkle(world, x, y, z);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
	    int id = idPicked(world, x, y, z);
	
	    if (id == 0)
	    {
	        return null;
	    }
	
	    Item item = Item.itemsList[id];
	    
	    if (item == null)
	    {
	        return null;
	    }
	
	    return new ItemStack(id, 1, world.getBlockMetadata(x, y, z));
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
	public int quantityDropped(Random rand)
	{
		return 1;
	}

	@Override
    public int damageDropped(int damage)
    {
        return damage;
    }
    
	@Override
	public boolean canSustainLeaves(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public boolean isWood(World world, int x, int y, int z)
	{
		return true;
	}

	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		this.treeIcons = new Icon[ArrowsPlus.woodNames.length];
		this.treeIconsTop = new Icon[ArrowsPlus.woodNames.length];

		for (int i = 0; i < this.treeIcons.length; ++i)
		{
			this.treeIcons[i] = iconRegister.registerIcon("arrowsplus:tree_" + ArrowsPlus.woodNames[i]);
			this.treeIconsTop[i] = iconRegister.registerIcon("arrowsplus:tree_" + ArrowsPlus.woodNames[i] + "_top");
		}
	}

	@Override
	public Icon getIcon(int side, int metadata)
	{
		if (side == 0 || side == 1)
		{
			return treeIconsTop[metadata];
		}

		else
		{
			return treeIcons[metadata];
		}
	}

	@Override
	protected ItemStack createStackedBlock(int meta)
	{
		return new ItemStack(this.blockID, 1, meta);
	}

	/**
	 * Sparkles for Sypherus tree.
	 * 
	 * @param	world	The world the block is in.
	 * @param	x		The block's x coordinate.
	 * @param	y		The block's y coordinate.
	 * @param	z		The block's z coordinate.
	 */
	private static void sparkle(World world, int x, int y, int z)
	{
		if (world.getBlockMetadata(x, y, z) == 11)
		{
			Random random = world.rand;
			
			for (int pass = 0; pass < 6; ++pass)
			{
				double randX = x + random.nextFloat();
				double randY = y + random.nextFloat();
				double randZ = z + random.nextFloat();

				if (pass == 0 && !world.isBlockOpaqueCube(x, y + 1, z))
				{
					randY = y + 1 + 0.0625D;
				}

				if (pass == 1 && !world.isBlockOpaqueCube(x, y - 1, z))
				{
					randY = y + 0 - 0.0625D;
				}

				if (pass == 2 && !world.isBlockOpaqueCube(x, y, z + 1))
				{
					randZ = z + 1 + 0.0625D;
				}

				if (pass == 3 && !world.isBlockOpaqueCube(x, y, z - 1))
				{
					randZ = z + 0 - 0.0625D;
				}

				if (pass == 4 && !world.isBlockOpaqueCube(x + 1, y, z))
				{
					randX = x + 1 + 0.0625D;
				}

				if (pass == 5 && !world.isBlockOpaqueCube(x - 1, y, z))
				{
					randX = x + 0 - 0.0625D;
				}

				if (randX < x || randX > x + 1 || randY < 0.0D || randY > y + 1 || randZ < z || randZ > z + 1)
				{
					world.spawnParticle("reddust", randX, randY, randZ, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
}
