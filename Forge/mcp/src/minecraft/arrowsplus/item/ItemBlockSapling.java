/*******************************************************************************
 * ItemBlockSapling.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import arrowsplus.block.BlockSaplingBase;
import arrowsplus.core.ArrowsPlus;

/**
 * The item block for saplings. Used to display the block in the inventory. (?)
 */
public class ItemBlockSapling extends ItemBlock
{
	/**
	 * Constructor
	 * 
	 * @param 	id	The sapling's ID.
	 */
	public ItemBlockSapling(int id) 
	{
		super(id);
		setUnlocalizedName("ItemBlockSapling");
	}
	
	@Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
    	BlockSaplingBase sapling = (BlockSaplingBase) Block.blocksList[itemStack.itemID];
    	return ArrowsPlus.woodNamesCapitalized[sapling.saplingType] + " Sapling";
    }
}