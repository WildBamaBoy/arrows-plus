/*******************************************************************************
 * ItemBowBase.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import org.lwjgl.input.Keyboard;

import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.io.WorldPropertiesManager;
import arrowsplus.core.util.Color;
import arrowsplus.entity.EntityArrowBase;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Defines each bow in the mod.
 */
public class ItemBowBase extends Item
{
	@SideOnly(Side.CLIENT)
	private Icon[] alderIconArray;
	private Icon[] ashIconArray;
	private Icon[] aspenIconArray;
	private Icon[] beechIconArray;
	private Icon[] cottonwoodIconArray;
	private Icon[] gumIconArray;
	private Icon[] hardMapleIconArray;
	private Icon[] hickoryIconArray;
	private Icon[] mahoganyIconArray;
	private Icon[] softMapleIconArray;
	private Icon[] sycamoreIconArray;
	private Icon[] sypherusIconArray;

	/** The type of wood this bow is made of. (Meta value of the log it came from.) */
	public final int woodType;

	/** How much damage the bow itself can do. */
	public final float damageModifier;

	/** How much, if any, to slow down the drawing of the bow. */
	public final int slowdownModifier;

	/** How much, if any, the bow modifies the base stability of 3.5*/
	public final float stabilityModifier;

	/**Is the bow being drawn by right clicking? */
	public boolean isBeingDrawn;

	/**How long the bow has been drawn. */
	public int drawnTicks;

	/**
	 * Constructor
	 * 
	 * @param 	itemId		The bow's item ID.
	 * @param 	durability	The bow's durability.
	 * @param 	woodType	The bow's wood type/damage/metadata value.
	 */
	public ItemBowBase(int itemId, int durability, int woodType)
	{
		super(itemId);

		this.maxStackSize = 1;
		this.woodType = woodType;
		this.damageModifier = ArrowsPlus.getBowDamageModifierByWoodType(woodType);
		this.slowdownModifier = ArrowsPlus.getBowDrawSpeedModifierByWoodType(woodType);
		this.stabilityModifier = ArrowsPlus.getBowStabilityModifierByWoodType(woodType);
		this.isBeingDrawn = false;
		this.drawnTicks = 0;
		
		this.setMaxDamage(durability);
		this.setCreativeTab(ArrowsPlus.instance.tabArrowsPlus);
		this.func_111206_d("arrowsplus:bows/bow_" + ArrowsPlus.instance.woodNames[woodType]);
		this.setUnlocalizedName(ArrowsPlus.instance.woodNamesCapitalized[woodType] + " Bow");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() 
	{
		return true;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int unknown, boolean isBeingUsed) 
	{
		super.onUpdate(itemStack, world, entity, unknown, isBeingUsed);
	
		if (world.isRemote)
		{
			if (isBeingUsed)
			{
				drawnTicks++;
				Minecraft minecraft = Minecraft.getMinecraft();
				EntityRenderer renderer = minecraft.entityRenderer;
	
				float fovModifierHand = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, renderer, 33);
				float smoothCameraFilterX = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, renderer, 23);
				float smoothCameraFilterY = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, renderer, 24);
	
				if (isBeingDrawn)
				{
					minecraft.gameSettings.smoothCamera = true;
					float modifier = drawnTicks * 0.02F;
	
					//Zoom in for the mahogany bow if Ctrl is held.
					if (woodType == 10 && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)))
					{
						if (modifier <= 0.35F)
						{
							ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, fovModifierHand - modifier, 33);
						}
	
						else
						{
							ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, fovModifierHand - 0.35F, 33);
						}
					}
	
					//Do camera shake.
					if (world.rand.nextBoolean())
					{
						if (world.rand.nextBoolean())
						{
							smoothCameraFilterX += (3.5F + stabilityModifier);
						}
	
						else
						{
							smoothCameraFilterY -= (3.5F + stabilityModifier);
						}
	
						ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, smoothCameraFilterX, 23);
					}
	
					else
					{
						if (world.rand.nextBoolean())
						{
							smoothCameraFilterY -= (3.5F + stabilityModifier);
						}
	
						else
						{
							smoothCameraFilterY += (3.5F + stabilityModifier);
						}
	
						ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, smoothCameraFilterY, 24);
					}
				}
	
				else
				{
					minecraft.gameSettings.smoothCamera = false;
					drawnTicks = 0;
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
	{
		if (getSlotOfSelectedArrow(entityPlayer) != -1)
		{
			isBeingDrawn = true;
	
			ArrowNockEvent event = new ArrowNockEvent(entityPlayer, itemStack);
			MinecraftForge.EVENT_BUS.post(event);
	
			if (event.isCanceled())
			{
				return event.result;
			}
	
			if (entityPlayer.capabilities.isCreativeMode || hasValidArrow(entityPlayer))
			{
				entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
			}
		}
	
		return itemStack;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int inUseCount)
	{
		isBeingDrawn = false;
		int useAmount = ((this.getMaxItemUseDuration(itemStack) - inUseCount) - slowdownModifier);
		
		if (slowdownModifier < 0)
		{
			useAmount += slowdownModifier;
		}
		
		//Prevent from firing if negative. Sometimes the event doesn't cancel it, allowing for rapid fire.
		if (useAmount <= 0)
		{
			return;
		}

		ArrowLooseEvent event = new ArrowLooseEvent(entityPlayer, itemStack, useAmount);
		MinecraftForge.EVENT_BUS.post(event);

		if (event.isCanceled())
		{
			return;
		}

		useAmount = event.charge;

		boolean infiniteFlag = entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) > 0;

		if (infiniteFlag || hasValidArrow(entityPlayer))
		{
			float velocity = (float)useAmount / 20.0F;
			velocity = (velocity * velocity + velocity * 2.0F) / 3.0F;

			if ((double)velocity < 0.1D)
			{
				return;
			}

			if (velocity > 1.0F)
			{
				velocity = 1.0F;
			}

			ItemArrowBase itemArrow = (ItemArrowBase)entityPlayer.inventory.getStackInSlot(getSlotOfSelectedArrow(entityPlayer)).getItem();

			//Velocity calculations. Only if bow fully drawn.
			if (useAmount >= 18)
			{
				velocity += ArrowsPlus.instance.getBowVelocityModifierByWoodType(woodType);
				velocity += ArrowsPlus.instance.getArrowVelocityModifierByWoodType(itemArrow.woodType);
			}

			EntityArrowBase entityArrow = new EntityArrowBase(world, entityPlayer, velocity * 2.0F, itemArrow.woodType, woodType);

			//Check for Sypherus arrow set on fire.
			if (itemArrow.woodType == 11)
			{
				if (world.rand.nextBoolean())
				{
					world.playSoundAtEntity(entityPlayer, "mob.ghast.fireball", 0.8F, 1.0F);
					entityArrow.setFire(15);
				}
			}

			//Check enchantments
			if (velocity >= (1.0F + ArrowsPlus.instance.getBowVelocityModifierByWoodType(woodType) + ArrowsPlus.instance.getArrowVelocityModifierByWoodType(itemArrow.woodType)))
			{
				if (entityArrow.arrowType < 5)
				{
					entityArrow.setIsCritical(true);
				}
			}

			int powerEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);

			if (powerEnchantmentLevel > 0)
			{
				entityArrow.setDamage(entityArrow.getDamage() + (double)powerEnchantmentLevel * 0.5D + 0.5D);
			}

			int punchEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack);

			if (punchEnchantmentLevel > 0)
			{
				entityArrow.setKnockbackStrength(punchEnchantmentLevel);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) > 0)
			{
				entityArrow.setFire(100);
			}

			//Damage bow.
			itemStack.damageItem(1, entityPlayer);

			//Check if the arrow broke.
			if (!entityArrow.isDead)
			{
				world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
			}

			if (infiniteFlag)
			{
				entityArrow.canBePickedUp = 2;
			}

			else
			{
				entityPlayer.inventory.consumeInventoryItem(entityPlayer.inventory.getStackInSlot(getSlotOfSelectedArrow(entityPlayer)).itemID);
			}

			//Fire the arrow.
			if (!world.isRemote)
			{
				world.spawnEntityInWorld(entityArrow);
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemStack)
	{
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemStack)
	{
		return EnumAction.bow;
	}

	@Override
	public int getItemEnchantability()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		String woodTypeName = ArrowsPlus.woodNames[woodType];

		this.itemIcon = iconRegister.registerIcon("arrowsplus:bows/bow_" + woodTypeName);
		Icon[] iconArray = getIconArrayForWoodType(woodType);

		//1 for no arrow. 3 per wood type. 3 * 12 = 36 + 1 = 37. 
		//The bow with no arrow will always be index 0, so add 1 to index when getting the icon with an arrow in the inventory.
		iconArray = new Icon[37];
		iconArray[0] = itemIcon;

		int currentArrowType = 0;
		int currentPullIndex = 0;
		for (int i = 1; i < iconArray.length; ++i)
		{
			if (currentPullIndex == 3)
			{
				currentArrowType++;
				currentPullIndex = 0;
			}

			iconArray[i] = iconRegister.registerIcon("arrowsplus:bows/bow_" + woodTypeName + 
					"_arrow_" + ArrowsPlus.woodNames[currentArrowType] + "_pull_" + currentPullIndex);
			currentPullIndex++;
		}

		setIconArrayForWoodType(woodType, iconArray);
	}

	@Override
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		if (useRemaining != 0)
		{
			int i = getMaxItemUseDuration(stack) - useRemaining;

			if (i >= 18 + slowdownModifier)
			{
				return getItemIconForUseDuration(2, player);
			}

			else if (i > 13 + (slowdownModifier / 2))
			{
				return getItemIconForUseDuration(1, player);
			}

			return getItemIconForUseDuration(0, player);
		}

		else
		{
			return getItemIconForUseDuration(0, player);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List informationList, boolean unknown)
	{
		//The attributes below the bows and arrows aren't real attributes.
		switch (woodType)
		{
		case 0: informationList.add("Standard"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 1: informationList.add("Lightweight"); informationList.add("Unstable"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 2: informationList.add("Strong string"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 3: informationList.add("Strong string"); informationList.add("Slow draw"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 4: informationList.add("Slow draw"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 5: informationList.add("Standard"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 6: informationList.add("Accurate"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 7: informationList.add("Slow draw"); informationList.add("Flexible"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 8: informationList.add("Very stable"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 9: informationList.add("Very slow draw"); informationList.add("Very strong string"); informationList.add("Unstable"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 10: informationList.add("Stable"); informationList.add("Accurate"); informationList.add("Sights with Ctrl"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		case 11: informationList.add("Teleportation"); informationList.add(""); informationList.add(Color.BLUE + "+" + damageModifier + " Attack Damage"); break;
		}

		if (ArrowsPlus.instance.inDebugMode)
		{
			informationList.add("Metadata = " + woodType);
		}
	}

	/**
	 * Check if the player has a "arrow tree" arrow in their inventory.
	 * 
	 * @param 	player	An instance of the player to check.
	 * 
	 * @return	True if an ItemArrowBase is within the provided player's inventory.
	 */
	private boolean hasValidArrow(EntityPlayer player)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null)
			{
				if (stack.getItem() instanceof ItemArrowBase)
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the slot ID of the arrow within a player's inventory that has the player's selected arrow metadata value.
	 * 
	 * @param 	player	An instance of the player to check.
	 * 
	 * @return	The slot ID of the arrow with the player's selected arrow metadata value.
	 */
	private int getSlotOfSelectedArrow(EntityPlayer player)
	{
		int slot = -1;

		WorldPropertiesManager manager = ArrowsPlus.instance.playerWorldManagerMap.get(player.username);

		if (manager.worldProperties.selectedArrowMeta != -1)
		{
			for (int i = 0; i < player.inventory.getSizeInventory(); i++)
			{
				ItemStack stack = player.inventory.getStackInSlot(i);

				if (stack != null)
				{
					if (stack.getItem() instanceof ItemArrowBase)
					{
						ItemArrowBase arrow = (ItemArrowBase)stack.getItem();

						if (arrow.woodType == manager.worldProperties.selectedArrowMeta)
						{
							return i;
						}
					}
				}
			}

			player.addChatMessage(Color.RED + "Out of equipped arrows! Using most powerful arrow in inventory.");
			manager.worldProperties.selectedArrowMeta = -1;
			manager.saveWorldProperties();
		}

		if (slot == -1)
		{
			slot = getSlotOfHighestMetaArrow(player);
		}

		return slot;
	}

	/**
	 * Gets the slot ID of the arrow within a player's inventory that has the highest metadata value.
	 * 
	 * @param 	player	An instance of the player to check.
	 * 
	 * @return	The slot ID of the arrow with the highest metadata value and therefore best wood type.
	 */
	private int getSlotOfHighestMetaArrow(EntityPlayer player)
	{
		int highestMeta = -1;
		int highestMetaSlot = -1;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null)
			{
				if (stack.getItem() instanceof ItemArrowBase)
				{
					ItemArrowBase arrow = (ItemArrowBase)stack.getItem();

					if (arrow.woodType >= highestMeta)
					{
						highestMeta = arrow.woodType;
						highestMetaSlot = i;
					}
				}
			}
		}

		return highestMetaSlot;
	}

	/**
	 * Gets the appropriate icon for the item based on how it is currently being used.
	 * 
	 * @param 	index	The index of the pull animation.
	 * @param 	player	The player performing the action.
	 * 
	 * @return	Icon object containing appropriate icon for item.
	 */
	@SideOnly(Side.CLIENT)
	private Icon getItemIconForUseDuration(int index, EntityPlayer player)
	{
		//Check for the preferred arrow in the player's inventory.
		ItemArrowBase arrow = null;

		try
		{
			arrow = (ItemArrowBase)player.inventory.getStackInSlot(getSlotOfSelectedArrow(player)).getItem();
		}

		//NullPointer or ClassCast is thrown if they don't have an arrow since slot 0 is returned.
		catch (Throwable e)
		{
			return getIconArrayForWoodType(woodType)[index];
		}

		//We have an arrow.
		if (arrow != null)
		{
			//Determine the arrow's wood type and give the bow the correct icon so that the arrow is shown already in the bow.
			if (arrow.woodType != 0)
			{
				return getIconArrayForWoodType(woodType)[1 + index + (arrow.woodType * 3)];
			}

			else
			{
				return getIconArrayForWoodType(woodType)[1 + index];
			}
		}

		//We don't have an arrow. This will return the bow without any arrow in it. Index 0 of the icon array.
		else
		{
			return getIconArrayForWoodType(woodType)[index];
		}
	}

	/**
	 * Gets the appropriate icon array based on the type of wood this bow is made of.
	 * 
	 * @param 	woodType	The type of wood that the bow is made of. (Meta value of log it came from.)
	 * 
	 * @return	Icon array used to store icons for this bow type.
	 */
	private Icon[] getIconArrayForWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return aspenIconArray;
		case 1: return cottonwoodIconArray;
		case 2: return alderIconArray;
		case 3: return sycamoreIconArray;
		case 4: return gumIconArray;
		case 5: return softMapleIconArray;
		case 6: return ashIconArray;
		case 7: return beechIconArray;
		case 8: return hardMapleIconArray;
		case 9: return hickoryIconArray;
		case 10: return mahoganyIconArray;
		case 11: return sypherusIconArray;
		default: return null;
		}
	}

	/**
	 * Sets the values of icon array for the specified wood type to the values in the provided icon array.
	 * 
	 * @param 	woodType	The type of wood that this bow is made of.
	 * @param 	iconArray	The icon array to assign.
	 */
	private void setIconArrayForWoodType(int woodType, Icon[] iconArray)
	{
		switch (woodType)
		{
		case 0: aspenIconArray = iconArray; break;
		case 1: cottonwoodIconArray = iconArray; break;
		case 2: alderIconArray = iconArray; break;
		case 3: sycamoreIconArray = iconArray; break;
		case 4: gumIconArray = iconArray; break;
		case 5: softMapleIconArray = iconArray; break;
		case 6: ashIconArray = iconArray; break;
		case 7: beechIconArray = iconArray; break;
		case 8: hardMapleIconArray = iconArray; break;
		case 9: hickoryIconArray = iconArray; break;
		case 10: mahoganyIconArray = iconArray; break;
		case 11: sypherusIconArray = iconArray; break;
		}
	}
}