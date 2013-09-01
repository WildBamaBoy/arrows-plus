/*******************************************************************************
 * CraftingHandler.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core.forge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import arrowsplus.core.ArrowsPlus;
import cpw.mods.fml.common.ICraftingHandler;

/**
 * Handles onCrafting and onSmelting events in Arrows Plus.
 */
public class CraftingHandler implements ICraftingHandler
{
	@Override
	public void onCrafting(EntityPlayer player, ItemStack itemStack, IInventory craftMatrix) 
	{
		//Simulate shift-clicking when appropriate for iron shards.
		if (itemStack.getItem().itemID == ArrowsPlus.instance.itemIronShard.itemID)
		{
			int shardsToMake = 0;
			int ironIngotSlot = 0;

			//Run through the inventory.
			for (int i = 0; i < craftMatrix.getSizeInventory() - 1; i++)
			{
				ItemStack stack = craftMatrix.getStackInSlot(i);

				if (stack != null)
				{
					//Found the hammer. We want that put back in the inventory.
					if (stack.getItem().itemID == ArrowsPlus.instance.itemHammer.itemID)
					{
						player.inventory.addItemStackToInventory(stack);
					}

					//Calculate what the return would be just in case shift is held down.
					else if (stack.getItem().itemID == Item.ingotIron.itemID)
					{
						shardsToMake = stack.stackSize * 4;
						ironIngotSlot = i;
					}
				}
			}
		}
	}

	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) 
	{
		return;
	}
}
