/*******************************************************************************
 * ItemArrowBase.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.io.WorldPropertiesManager;
import arrowsplus.core.util.Color;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Base class for all arrow items.
 */
public class ItemArrowBase extends Item
{
	/** How much damage the arrow does. */
	private float damageModifier;
	
	/** The type of wood the arrow is made of. */
	public final int woodType;
	
	/**
	 * Constructor
	 * 
	 * @param itemId			The arrow's item ID.
	 * @param damageModifier	The arrow's damage modifier.
	 * @param woodType			The type of wood the arrow is made of. (Meta value of the log it came from.)
	 */
	public ItemArrowBase(int itemId, float damageModifier, int woodType) 
	{
		super(itemId);
		this.woodType = woodType;
		this.damageModifier = damageModifier;
		this.setCreativeTab(ArrowsPlus.instance.tabArrowsPlus);

		this.func_111206_d("arrowsplus:arrow_" + ArrowsPlus.woodNames[woodType]);
		this.setUnlocalizedName(ArrowsPlus.woodNamesCapitalized[woodType] + " Arrow");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer enittyPlayer) 
	{
		WorldPropertiesManager manager = ArrowsPlus.instance.playerWorldManagerMap.get(enittyPlayer.username);
		
		if (world.isRemote)
		{
			if (manager.worldProperties.selectedArrowMeta == this.woodType)
			{
				enittyPlayer.addChatMessage(Color.DARKRED + "Arrow unequipped. Most powerful arrow will be used.");
				manager.worldProperties.selectedArrowMeta = -1;
				manager.saveWorldProperties();
			}
			
			else
			{
				enittyPlayer.addChatMessage(Color.DARKGREEN + "Equipped " + itemStack.getDisplayName());
				manager.worldProperties.selectedArrowMeta = this.woodType;
				manager.saveWorldProperties();
			}
		}
		
		return super.onItemRightClick(itemStack, world, enittyPlayer);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List informationList, boolean unknown)
	{
		switch (woodType)
		{
		case 0: informationList.add("25% chance of breaking"); informationList.add("Standard"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 1: informationList.add("50% chance of breaking"); informationList.add("Lightweight"); informationList.add("Accurate"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 2: informationList.add("30% chance of breaking"); informationList.add("Standard"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 3: informationList.add("30% chance of breaking"); informationList.add("Wither"); informationList.add("Heavy"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 4: informationList.add("25% chance of breaking"); informationList.add("Lightweight"); informationList.add("Inaccurate"); informationList.add("Slowness"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 5: informationList.add("15% chance of breaking"); informationList.add("Unreliable"); informationList.add("Wind Prone"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 6: informationList.add("75% chance of breaking"); informationList.add("Chance of blindness"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 7: informationList.add("15% chance of breaking"); informationList.add("Can't pierce armor"); informationList.add("Poison"); informationList.add("Very accurate"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 8: informationList.add("15% chance of breaking"); informationList.add("Knockback"); informationList.add("Block Penetration"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 9: informationList.add("100% chance of breaking"); informationList.add("Heavy"); informationList.add("Destroy 1 block"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 10: informationList.add("10% chance of breaking"); informationList.add("Heavy"); informationList.add("Knockback"); informationList.add("Accurate"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 11: informationList.add("5% chance of breaking"); informationList.add("Random flames, explosion,"); informationList.add("or insta-hit"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		}
		
		if (ArrowsPlus.instance.inDebugMode)
		{
			informationList.add("Metadata = " + woodType);
		}
	}
}