/*******************************************************************************
 * ItemBlockLeaves.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import arrowsplus.core.ArrowsPlus;

/**
 * The item block for leaves. Used to display the block in the inventory. (?)
 */
public class ItemBlockLeaves extends ItemBlock
{
	/**
	 * Constructor
	 * 
	 * @param 	id	The block's ID.
	 */
	public ItemBlockLeaves(int id) 
	{
		super(id);
		setHasSubtypes(true);
		setUnlocalizedName("ItemBlockLeaves");
	}
	
	@Override
	public int getMetadata(int damageValue) 
	{
		return damageValue;
	}
	
	@Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return ArrowsPlus.woodNamesCapitalized[itemStack.getItemDamage()] + " Leaves";
    }
}