/*******************************************************************************
 * ArrowsPlus.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import arrowsplus.block.BlockArrowTreeLeaves;
import arrowsplus.block.BlockArrowTreeLog;
import arrowsplus.block.BlockSaplingBase;
import arrowsplus.command.CommandCheckUpdates;
import arrowsplus.command.CommandDebugMode;
import arrowsplus.command.CommandShowExperience;
import arrowsplus.core.forge.CommonProxy;
import arrowsplus.core.forge.ConnectionHandler;
import arrowsplus.core.forge.CraftingHandler;
import arrowsplus.core.forge.EventHooks;
import arrowsplus.core.forge.PacketHandler;
import arrowsplus.core.io.ModPropertiesManager;
import arrowsplus.core.io.WorldPropertiesManager;
import arrowsplus.core.util.object.PlayerInteractEntry;
import arrowsplus.core.util.object.UpdateHandler;
import arrowsplus.entity.EntityArrowBase;
import arrowsplus.gen.WorldGeneratorTrees;
import arrowsplus.item.ItemArrowBase;
import arrowsplus.item.ItemBlockLeaves;
import arrowsplus.item.ItemBlockLog;
import arrowsplus.item.ItemBlockSapling;
import arrowsplus.item.ItemBowBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Core of the Arrows Plus mod. Built on core of Minecraft Comes Alive and other systems from it.
 */
@Mod(modid="arrowsplus", name="Arrows Plus", version=UpdateHandler.VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=false,
channels={"AP_LOGIN", "AP_WORLDPROP", "AP_ADDITEM"}, packetHandler = PacketHandler.class)
public class ArrowsPlus
{
	//Creative Tab
	public CreativeTabs tabArrowsPlus;

	//Items and Blocks
	public Block blockArrowTreeLog;
	public Block blockArrowTreeLeaves;
	public Block blockSaplingAspen;
	public Block blockSaplingCottonwood;
	public Block blockSaplingAlder;
	public Block blockSaplingSycamore;
	public Block blockSaplingGum;
	public Block blockSaplingSoftMaple;
	public Block blockSaplingAsh;
	public Block blockSaplingBeech;
	public Block blockSaplingHardMaple;
	public Block blockSaplingHickory;
	public Block blockSaplingMahogany;
	public Block blockSaplingSypherus;

	public Item itemArrowAspen;
	public Item itemArrowCottonwood;
	public Item itemArrowAlder;
	public Item itemArrowSycamore;
	public Item itemArrowGum;
	public Item itemArrowSoftMaple;
	public Item itemArrowAsh;
	public Item itemArrowBeech;
	public Item itemArrowHardMaple;
	public Item itemArrowHickory;
	public Item itemArrowMahogany;
	public Item itemArrowSypherus;
	public Item itemStickAspen;
	public Item itemStickCottonwood;
	public Item itemStickAlder;
	public Item itemStickSycamore;
	public Item itemStickGum;
	public Item itemStickSoftMaple;
	public Item itemStickAsh;
	public Item itemStickBeech;
	public Item itemStickHardMaple;
	public Item itemStickHickory;
	public Item itemStickMahogany;
	public Item itemStickSypherus;
	public Item itemBowAspen;
	public Item itemBowCottonwood;
	public Item itemBowAlder;
	public Item itemBowSycamore;
	public Item itemBowGum;
	public Item itemBowSoftMaple;
	public Item itemBowAsh;
	public Item itemBowBeech;
	public Item itemBowHardMaple;
	public Item itemBowHickory;
	public Item itemBowMahogany;
	public Item itemBowSypherus;
	public Item itemIronShard;
	public Item itemHammer;

	//Achievements
	public Achievement achievementHarvestAspen;
	public Achievement achievementHarvestCottonwood;
	public Achievement achievementHarvestAlder;
	public Achievement achievementHarvestSycamore;
	public Achievement achievementHarvestGum;
	public Achievement achievementHarvestSoftMaple;
	public Achievement achievementHarvestAsh;
	public Achievement achievementHarvestBeech;
	public Achievement achievementHarvestHardMaple;
	public Achievement achievementHarvestHickory;
	public Achievement achievementHarvestMahogany;
	public Achievement achievementHarvestSypherus;

	//Fields for core functions.
	public String runningDirectory = "";
	public boolean inDebugMode = false;
	public boolean hasLoadedProperties = false;
	public boolean hasEmptiedPropertiesFolder = false;
	public boolean hasCompletedMainMenuTick   = false;
	public boolean hasCheckedForUpdates = false;
	private Logger logger = FMLLog.getLogger();
	public Random random = new Random();
	public ModPropertiesManager modPropertiesManager = null;

	//Side related fields.
	public boolean isDedicatedServer 			= false;
	public boolean isIntegratedServer			= false;
	public boolean isIntegratedClient			= false;
	public boolean isDedicatedClient			= false;

	/**Map of all current players and their world properties manager. Server side only.**/
	public Map<String, WorldPropertiesManager> playerWorldManagerMap = new HashMap<String, WorldPropertiesManager>();

	/**Map of the last block that a player tried to harvest. Used for experience. */
	public Map<String, PlayerInteractEntry> playerBlockHarvestingMap = new HashMap<String, PlayerInteractEntry>();

	/** Mod instance */
	@Instance("arrowsplus")
	public static ArrowsPlus instance;

	@SidedProxy(clientSide="arrowsplus.core.forge.ClientProxy", serverSide="arrowsplus.core.forge.CommonProxy")
	public static CommonProxy proxy;

	//Names to keep up with.
	public static final String[] woodNames = new String[] {"aspen", "cottonwood", "alder", "sycamore", "gum", "softmaple",
		"ash", "beech", "hardmaple", "hickory", "mahogany", "sypherus"};

	public static final String[] woodNamesCapitalized = new String[] {"Aspen", "Cottonwood", "Alder", "Sycamore", "Gum", "Soft Maple",
	"Ash", "Beech", "Hard Maple", "Hickory", "Mahogany", "Sypherus"};

	/**
	 * Gets a random boolean with a probability of being true.
	 * 
	 * @param	probabilityOfTrue	The probability that true should be returned.
	 * 
	 * @return	A randomly generated boolean.
	 */
	public static boolean getBooleanWithProbability(int probabilityOfTrue)
	{
		int randomNumber = ArrowsPlus.instance.random.nextInt(100) + 1;
	
		if (randomNumber <= probabilityOfTrue)
		{
			return true;
		}
	
		else
		{
			return false;
		}
	}

	public static float getBowDamageModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 3.0F;
		case 1: return 3.0F;
		case 2: return 3.5F;
		case 3: return 4.5F;
		case 4: return 4.5F;
		case 5: return 4.5F;
		case 6: return 5.0F;
		case 7: return 6.0F;
		case 8: return 7.0F;
		case 9: return 7.5F;
		case 10: return 8.0F;
		case 11: return 10.0F;
		default: return 0.0F;
		}
	}

	public static double getArrowDamageModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0.5D;
		case 1: return 1.0D;
		case 2: return 1.5D;
		case 3: return 2.0D;
		case 4: return 3.0D;
		case 5: return 4.0D;
		case 6: return 5.0D;
		case 7: return 6.0D;
		case 8: return 7.0D;
		case 9: return 8.0D;
		case 10: return 9.0D;
		case 11: return 10.0D;
		default: return 0.0D;
		}
	}

	public static float getArrowVelocityModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0.0F;
		case 1: return +0.5F;
		case 2: return 0.0F;
		case 3: return -0.25F;
		case 4: return +0.5F;
		case 5: return 0.0F;
		case 6: return 0.0F;
		case 7: return 0.0F;
		case 8: return 0.0F;
		case 9: return -0.25F;
		case 10: return -0.25F;
		case 11: return 0.0F;
		default: return 0.0F;
		}
	}

	public static float getBowVelocityModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0.0F;
		case 1: return 0.0F;
		case 2: return +0.75F;
		case 3: return +0.75F;
		case 4: return 0.0F;
		case 5: return 0.0F;
		case 6: return 0.0F;
		case 7: return 0.0F;
		case 8: return 0.0F;
		case 9: return +1.0F;
		case 10: return 0.0F;
		case 11: return 0.0F;
		default: return 0.0F;
		}
	}

	public static float getBowStabilityModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0.0F;
		case 1: return 2.5F;
		case 2: return 0.0F;
		case 3: return 0.0F;
		case 4: return 0.0F;
		case 5: return -2.5F;
		case 6: return 1.5F;
		case 7: return 0.0F;
		case 8: return -3.1F;
		case 9: return 0.0F;
		case 10: return -2.5F;
		case 11: return 1.5F;
		default: return 0.0F;
		}
	}

	public static double getArrowWindResistanceByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0.0D;
		case 1: return 0.05D;
		case 2: return 0.0D;
		case 3: return 0.03D;
		case 4: return 0.02D;
		case 5: return 0.0D;
		case 6: return 0.0D;
		case 7: return 0.10D;
		case 8: return 0.02D;
		case 9: return 0.03D;
		case 10: return 0.08D;
		case 11: return 0.05D;
		default: return 0.0D;
		}
	}

	public static int getBowDrawSpeedModifierByWoodType(int woodType)
	{
		switch (woodType)
		{
		case 0: return 0;
		case 1: return -15;
		case 2: return 0;
		case 3: return +15;
		case 4: return +15;
		case 5: return 0;
		case 6: return 0;
		case 7: return +15;
		case 8: return 0;
		case 9: return +30;
		case 10: return 0;
		case 11: return 0;
		default: return 0;
		}
	}

	/**
	 * Deflates a byte array.
	 * 
	 * @param 	input	The byte array to be deflated.
	 * 
	 * @return	Deflated byte array.
	 */
	public static byte[] compressBytes(byte[] input)
	{
		try
		{
			Deflater deflater = new Deflater();
			deflater.setLevel(Deflater.BEST_COMPRESSION);
			deflater.setInput(input);
	
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(input.length);
			deflater.finish();
	
			byte[] buffer = new byte[1024];
	
			while(!deflater.finished())
			{
				int count = deflater.deflate(buffer);
				byteOutput.write(buffer, 0, count);
			}
	
			byteOutput.close();
			return byteOutput.toByteArray();
		}
	
		catch (Throwable e)
		{
			ArrowsPlus.instance.quitWithError("Error compressing byte array.", e);
			return null;
		}
	}

	/**
	 * Inflates a deflated byte array.
	 * 
	 * @param 	input	The byte array to be deflated.
	 * 
	 * @return	Decompressed byte array.
	 */
	public static byte[] decompressBytes(byte[] input)
	{
		try
		{
			Inflater inflater = new Inflater();
			inflater.setInput(input);
	
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(input.length);
	
			byte[] buffer = new byte[1024];
	
			while(!inflater.finished())
			{
				int count = inflater.inflate(buffer);
				byteOutput.write(buffer, 0, count);
			}
	
			byteOutput.close();
			return byteOutput.toByteArray();
		}
	
		catch (Throwable e)
		{
			ArrowsPlus.instance.quitWithError("Error decompressing byte array.", e);
			return null;
		}
	}

	/**
	 * Deletes a path and all files and folders within.
	 * 
	 * @param 	file	The path to delete.
	 */
	public static void deletePath(File file)
	{
		if (file.isDirectory())
		{
			if (file.list().length == 0)
			{
				file.delete();
			}
	
			else
			{
				String files[] = file.list();
	
				for (String temp : files)
				{
					File fileDelete = new File(file, temp);
					deletePath(fileDelete);
				}
	
				if (file.list().length == 0)
				{
					file.delete();
				}
			}
		}
	
		else
		{
			file.delete();
		}
	}

	/**
	 * Runs code as soon as possible. Specifically before a player's stats are checked for invalidity (Achievement bug).
	 * So just for the heck of it, let's init EVERYTHING here along with achievements.
	 * 
	 * @param 	event	An instance of the FMLPreInitializationEvent.
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//Set instance.
		instance = this;
		
		//Set running directory.
		if (event.getSide() == Side.CLIENT)
		{
			runningDirectory = System.getProperty("user.dir");
		}

		else if (event.getSide() == Side.SERVER)
		{
			runningDirectory = System.getProperty("user.dir");
		}

		//Load mod properties.
		modPropertiesManager = new ModPropertiesManager();

		//Register all needed hooks, handlers, etc.
		MinecraftForge.EVENT_BUS.register(new EventHooks());
		proxy.registerTickHandlers();
		proxy.registerRenderers();
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
		GameRegistry.registerWorldGenerator(new WorldGeneratorTrees());
		GameRegistry.registerCraftingHandler(new CraftingHandler());

		//Create creative tab.
		tabArrowsPlus = new CreativeTabs("tabArrowsPlus") 
		{
			public ItemStack getIconItemStack() 
			{
				return new ItemStack(Item.arrow, 1, 0);
			}
		};

		LanguageRegistry.instance().addStringLocalization("itemGroup.tabArrowsPlus", "Arrows Plus");

		//Register all items and blocks.
		//Tree logs
		blockArrowTreeLog = new BlockArrowTreeLog(modPropertiesManager.modProperties.blockId_ArrowLog).setHardness(2.0F).setStepSound(Block.soundWoodFootstep).setUnlocalizedName("arrowtreelog").func_111022_d("tree");
		GameRegistry.registerBlock(blockArrowTreeLog, ItemBlockLog.class, "Arrow Log");

		for (int i = 0; i < 12; i++)
		{
			ItemStack logStack = new ItemStack(blockArrowTreeLog, 1, i);
			LanguageRegistry.addName(logStack, woodNamesCapitalized[i] + " Log");
		}

		//Tree leaves
		blockArrowTreeLeaves = new BlockArrowTreeLeaves(modPropertiesManager.modProperties.blockId_ArrowLogLeaves).setStepSound(Block.soundGrassFootstep).func_111022_d("tree_leaves").setUnlocalizedName("arrowtreeleaves");
		GameRegistry.registerBlock(blockArrowTreeLeaves, ItemBlockLeaves.class, "Arrow Leaves");

		for (int i = 0; i < 12; i++)
		{
			ItemStack logStack = new ItemStack(blockArrowTreeLeaves, 1, i);
			LanguageRegistry.addName(logStack, woodNamesCapitalized[i] + " Leaves");
		}

		//Saplings. Hate using so many IDs, but the icons wouldn't appear properly for some reason. Oh well.
		blockSaplingAspen = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingAspen, 0).setUnlocalizedName("Aspen Sapling");
		blockSaplingCottonwood = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingCottonwood, 1).setUnlocalizedName("Cottonwood Sapling");
		blockSaplingAlder = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingAlder, 2).setUnlocalizedName("Alder Sapling");
		blockSaplingSycamore = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingSycamore, 3).setUnlocalizedName("Sycamore Sapling");
		blockSaplingGum = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingGum, 4).setUnlocalizedName("Gum Sapling");
		blockSaplingSoftMaple = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingSoftMaple, 5).setUnlocalizedName("Soft Maple Sapling");
		blockSaplingAsh = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingAsh, 6).setUnlocalizedName("Ash Sapling");
		blockSaplingBeech = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingBeech, 7).setUnlocalizedName("Beech Sapling");
		blockSaplingHardMaple = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingHardMaple, 8).setUnlocalizedName("Hard Maple Sapling");
		blockSaplingHickory = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingHickory, 9).setUnlocalizedName("Hickory Sapling");
		blockSaplingMahogany = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingMahogany, 10).setUnlocalizedName("Mahogany Sapling");
		blockSaplingSypherus = new BlockSaplingBase(modPropertiesManager.modProperties.blockId_SaplingSypherus, 11).setUnlocalizedName("Sypherus Sapling");

		GameRegistry.registerBlock(blockSaplingAspen, ItemBlockSapling.class, "Aspen Sapling");
		GameRegistry.registerBlock(blockSaplingCottonwood, ItemBlockSapling.class, "Cottonwood Sapling");
		GameRegistry.registerBlock(blockSaplingAlder, ItemBlockSapling.class, "Alder Sapling");
		GameRegistry.registerBlock(blockSaplingSycamore, ItemBlockSapling.class, "Sycamore Sapling");
		GameRegistry.registerBlock(blockSaplingGum, ItemBlockSapling.class, "Gum Sapling");
		GameRegistry.registerBlock(blockSaplingSoftMaple, ItemBlockSapling.class, "Soft Maple Sapling");
		GameRegistry.registerBlock(blockSaplingAsh, ItemBlockSapling.class, "Ash Sapling");
		GameRegistry.registerBlock(blockSaplingBeech, ItemBlockSapling.class, "Beech Sapling");
		GameRegistry.registerBlock(blockSaplingHardMaple, ItemBlockSapling.class, "Hard Maple Sapling");
		GameRegistry.registerBlock(blockSaplingHickory, ItemBlockSapling.class, "Hickory Sapling");
		GameRegistry.registerBlock(blockSaplingMahogany, ItemBlockSapling.class, "Mahogany Sapling");
		GameRegistry.registerBlock(blockSaplingSypherus, ItemBlockSapling.class, "Sypherus Sapling");

		LanguageRegistry.addName(new ItemStack(blockSaplingAspen), "Aspen Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingCottonwood), "Cottonwood Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingAlder), "Alder Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingSycamore), "Sycamore Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingGum), "Gum Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingSoftMaple), "Soft Maple Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingAsh), "Ash Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingBeech), "Beech Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingHardMaple), "Hard Maple Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingHickory), "Hickory Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingMahogany), "Mahogany Sapling");
		LanguageRegistry.addName(new ItemStack(blockSaplingSypherus), "Sypherus Sapling");

		//Sticks
		itemStickAspen = new Item(modPropertiesManager.modProperties.itemId_StickAspen).func_111206_d("arrowsPlus:stick_aspen").setUnlocalizedName("Aspen Stick").setCreativeTab(tabArrowsPlus);
		itemStickCottonwood = new Item(modPropertiesManager.modProperties.itemId_StickCottonwood).func_111206_d("arrowsPlus:stick_cottonwood").setUnlocalizedName("Cottonwood Stick").setCreativeTab(tabArrowsPlus);
		itemStickAlder = new Item(modPropertiesManager.modProperties.itemId_StickAlder).func_111206_d("arrowsPlus:stick_alder").setUnlocalizedName("Alder Stick").setCreativeTab(tabArrowsPlus);
		itemStickSycamore = new Item(modPropertiesManager.modProperties.itemId_StickSycamore).func_111206_d("arrowsPlus:stick_sycamore").setUnlocalizedName("Sycamore Stick").setCreativeTab(tabArrowsPlus);
		itemStickGum = new Item(modPropertiesManager.modProperties.itemId_StickGum).func_111206_d("arrowsPlus:stick_gum").setUnlocalizedName("Gum Stick").setCreativeTab(tabArrowsPlus);
		itemStickSoftMaple = new Item(modPropertiesManager.modProperties.itemId_StickSoftMaple).func_111206_d("arrowsPlus:stick_softmaple").setUnlocalizedName("Soft Maple Stick").setCreativeTab(tabArrowsPlus);
		itemStickAsh = new Item(modPropertiesManager.modProperties.itemId_StickAsh).func_111206_d("arrowsPlus:stick_ash").setUnlocalizedName("Ash Stick").setCreativeTab(tabArrowsPlus);
		itemStickBeech = new Item(modPropertiesManager.modProperties.itemId_StickBeech).func_111206_d("arrowsPlus:stick_beech").setUnlocalizedName("Beech Stick").setCreativeTab(tabArrowsPlus);
		itemStickHardMaple = new Item(modPropertiesManager.modProperties.itemId_StickHardMaple).func_111206_d("arrowsPlus:stick_hardmaple").setUnlocalizedName("Hard Maple Stick").setCreativeTab(tabArrowsPlus);
		itemStickHickory = new Item(modPropertiesManager.modProperties.itemId_StickHickory).func_111206_d("arrowsPlus:stick_hickory").setUnlocalizedName("Hickory Stick").setCreativeTab(tabArrowsPlus);
		itemStickMahogany = new Item(modPropertiesManager.modProperties.itemId_StickMahogany).func_111206_d("arrowsPlus:stick_mahogany").setUnlocalizedName("Mahogany Stick").setCreativeTab(tabArrowsPlus);
		itemStickSypherus = new Item(modPropertiesManager.modProperties.itemId_StickSypherus).func_111206_d("arrowsPlus:stick_sypherus").setUnlocalizedName("Sypherus Stick").setCreativeTab(tabArrowsPlus);

		LanguageRegistry.addName(itemStickAspen, "Aspen Stick");
		LanguageRegistry.addName(itemStickCottonwood, "Cottonwood Stick");
		LanguageRegistry.addName(itemStickAlder, "Alder Stick");
		LanguageRegistry.addName(itemStickSycamore, "Sycamore Stick");
		LanguageRegistry.addName(itemStickGum, "Gum Stick");
		LanguageRegistry.addName(itemStickSoftMaple, "Soft Maple Stick");
		LanguageRegistry.addName(itemStickAsh, "Ash Stick");
		LanguageRegistry.addName(itemStickBeech, "Beech Stick");
		LanguageRegistry.addName(itemStickHardMaple, "Hard Maple Stick");
		LanguageRegistry.addName(itemStickHickory, "Hickory Stick");
		LanguageRegistry.addName(itemStickMahogany, "Mahogany Stick");
		LanguageRegistry.addName(itemStickSypherus, "Sypherus Stick");

		//Arrows
		itemArrowAspen = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowAspen, 0.5F, 0);
		itemArrowCottonwood = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowCottonwood, 1.0F, 1);
		itemArrowAlder = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowAlder, 1.5F, 2);
		itemArrowSycamore = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowSycamore, 2.0F, 3);
		itemArrowGum = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowGum, 3.0F, 4);
		itemArrowSoftMaple = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowSoftMaple, 4.0F, 5);
		itemArrowAsh = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowAsh, 5.0F, 6);
		itemArrowBeech = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowBeech, 6.0F, 7);
		itemArrowHardMaple = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowHardMaple, 7.0F, 8);
		itemArrowHickory = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowHickory, 8.0F, 9);
		itemArrowMahogany = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowMahogany, 9.0F, 10);
		itemArrowSypherus = new ItemArrowBase(modPropertiesManager.modProperties.itemId_ArrowSypherus, 10.0F, 11);

		LanguageRegistry.addName(itemArrowAspen, "Aspen Arrow");
		LanguageRegistry.addName(itemArrowCottonwood, "Cottonwood Arrow");
		LanguageRegistry.addName(itemArrowAlder, "Alder Arrow");
		LanguageRegistry.addName(itemArrowSycamore, "Sycamore Arrow");
		LanguageRegistry.addName(itemArrowGum, "Gum Arrow");
		LanguageRegistry.addName(itemArrowSoftMaple, "Soft Maple Arrow");
		LanguageRegistry.addName(itemArrowAsh, "Ash Arrow");
		LanguageRegistry.addName(itemArrowBeech, "Beech Arrow");
		LanguageRegistry.addName(itemArrowHardMaple, "Hard Maple Arrow");
		LanguageRegistry.addName(itemArrowHickory, "Hickory Arrow");
		LanguageRegistry.addName(itemArrowMahogany, "Mahogany Arrow");
		LanguageRegistry.addName(itemArrowSypherus, "Sypherus Arrow");

		//Bows
		itemBowAspen = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowAspen, 200, 0);
		itemBowCottonwood = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowCottonwood, 300, 1);
		itemBowAlder = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowAlder, 400, 2);
		itemBowSycamore = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowSycamore, 500, 3);
		itemBowGum = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowGum, 600, 4);
		itemBowSoftMaple = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowSoftMaple, 700, 5);
		itemBowAsh = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowAsh, 800, 6);
		itemBowBeech = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowBeech, 900, 7);
		itemBowHardMaple = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowHardMaple, 1000, 8);
		itemBowHickory = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowHickory, 1100, 9);
		itemBowMahogany = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowMahogany, 1200, 10);
		itemBowSypherus = new ItemBowBase(modPropertiesManager.modProperties.itemId_BowSypherus, 1500, 11);

		LanguageRegistry.addName(itemBowAspen, "Aspen Bow");
		LanguageRegistry.addName(itemBowCottonwood, "Cottonwood Bow");
		LanguageRegistry.addName(itemBowAlder, "Alder Bow");
		LanguageRegistry.addName(itemBowSycamore, "Sycamore Bow");
		LanguageRegistry.addName(itemBowGum, "Gum Bow");
		LanguageRegistry.addName(itemBowSoftMaple, "Soft Maple Bow");
		LanguageRegistry.addName(itemBowAsh, "Ash Bow");
		LanguageRegistry.addName(itemBowBeech, "Beech Bow");
		LanguageRegistry.addName(itemBowHardMaple, "Hard Maple Bow");
		LanguageRegistry.addName(itemBowHickory, "Hickory Bow");
		LanguageRegistry.addName(itemBowMahogany, "Mahogany Bow");
		LanguageRegistry.addName(itemBowSypherus, "Sypherus Bow");


		//Misc. items
		itemIronShard = new Item(modPropertiesManager.modProperties.itemId_IronShard).func_111206_d("arrowsPlus:shard_iron").setUnlocalizedName("Iron Shard").setCreativeTab(tabArrowsPlus);
		itemHammer = new Item(modPropertiesManager.modProperties.itemId_Hammer).func_111206_d("arrowsPlus:hammer").setUnlocalizedName("Hammer").setCreativeTab(tabArrowsPlus).setMaxStackSize(1);

		LanguageRegistry.addName(itemIronShard, "Iron Shard");
		LanguageRegistry.addName(itemHammer, "Hammer");

		//Register recipes.
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickAspen, 8), new ItemStack(blockArrowTreeLog, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickCottonwood, 8), new ItemStack(blockArrowTreeLog, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickAlder, 8), new ItemStack(blockArrowTreeLog, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickSycamore, 8), new ItemStack(blockArrowTreeLog, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickGum, 8), new ItemStack(blockArrowTreeLog, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickSoftMaple, 8), new ItemStack(blockArrowTreeLog, 1, 5));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickAsh, 8), new ItemStack(blockArrowTreeLog, 1, 6));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickBeech, 8), new ItemStack(blockArrowTreeLog, 1, 7));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickHardMaple, 8), new ItemStack(blockArrowTreeLog, 1, 8));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickHickory, 8), new ItemStack(blockArrowTreeLog, 1, 9));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickMahogany, 8), new ItemStack(blockArrowTreeLog, 1, 10));
		GameRegistry.addShapelessRecipe(new ItemStack(itemStickSypherus, 8), new ItemStack(blockArrowTreeLog, 1, 11));
		GameRegistry.addShapelessRecipe(new ItemStack(itemIronShard, 4), new ItemStack(Item.ingotIron, 1), new ItemStack(itemHammer));

		GameRegistry.addRecipe(new ItemStack(itemHammer, 1), new Object[]
				{
			"IIS", "  S", "  S", 'I', Item.ingotIron, 'S', Item.stick
				});

		GameRegistry.addRecipe(new ItemStack(itemBowAspen), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickAspen
				});

		GameRegistry.addRecipe(new ItemStack(itemBowCottonwood), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickCottonwood
				});

		GameRegistry.addRecipe(new ItemStack(itemBowAlder), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickAlder
				});

		GameRegistry.addRecipe(new ItemStack(itemBowSycamore), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickSycamore
				});

		GameRegistry.addRecipe(new ItemStack(itemBowGum), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickGum
				});

		GameRegistry.addRecipe(new ItemStack(itemBowSoftMaple), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickSoftMaple
				});

		GameRegistry.addRecipe(new ItemStack(itemBowAsh), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickAsh
				});

		GameRegistry.addRecipe(new ItemStack(itemBowBeech), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickBeech
				});

		GameRegistry.addRecipe(new ItemStack(itemBowHardMaple), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickHardMaple
				});

		GameRegistry.addRecipe(new ItemStack(itemBowHickory), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickHickory
				});

		GameRegistry.addRecipe(new ItemStack(itemBowMahogany), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickMahogany
				});

		GameRegistry.addRecipe(new ItemStack(itemBowSypherus), new Object[]
				{
			" ST", "S T", " ST", 'T', Item.silk, 'S', itemStickSypherus
				});
		///////////////////////////////////////////////////////////////////////
		GameRegistry.addRecipe(new ItemStack(itemArrowAspen), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickAspen
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowCottonwood), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickCottonwood
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowAlder), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickAlder
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowSycamore), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickSycamore
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowGum), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickGum
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowSoftMaple), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickSoftMaple
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowAsh), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickAsh
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowBeech), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickBeech
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowHardMaple), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickHardMaple
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowHickory), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickHickory
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowMahogany), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickMahogany
				});

		GameRegistry.addRecipe(new ItemStack(itemArrowSypherus), new Object[]
				{
			" I ", " S ", " F ", 'I', itemIronShard, 'F', Item.feather, 'S', itemStickSypherus
				});

		//Register entities
		EntityRegistry.registerModEntity(EntityArrowBase.class, EntityArrowBase.class.getSimpleName(), 7, this, 200, 2, true);

		//Create achievements
		//TODO

		//Register achievement page.
		//TODO
	}

	/**
	 * Writes the specified object's string representation to System.out.
	 * 
	 * @param 	obj	The object to write to System.out.
	 */
	public void log(Object obj)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		if (obj instanceof Throwable)
		{
			((Throwable)obj).printStackTrace();
		}

		try
		{
			logger.log(Level.FINER, "Arrows Plus " + side.toString() + ": " + obj.toString());
			System.out.println("Arrows Plus " + side.toString() + ": " + obj.toString());

			MinecraftServer server = MinecraftServer.getServer();

			if (server != null)
			{
				if (server.isDedicatedServer())
				{
					MinecraftServer.getServer().logInfo("Arrows Plus: " + obj.toString());
				}
			}
		}

		catch (NullPointerException e)
		{
			logger.log(Level.FINER, "Arrows Plus " + side.toString() + ": null");
			System.out.println("Arrows Plus: null");

			MinecraftServer server = MinecraftServer.getServer();

			if (server != null)
			{
				if (server.isDedicatedServer())
				{
					MinecraftServer.getServer().logDebug("Arrows Plus " + side.toString() + ": null");
				}
			}
		}
	}

	/**
	 * Writes the specified object's string representation to System.out.
	 * 
	 * @param 	obj	The object to write to System.out.
	 */
	public void logDebug(Object obj)
	{
		if (inDebugMode)
		{
			Side side = FMLCommonHandler.instance().getEffectiveSide();

			if (obj instanceof Throwable)
			{
				((Throwable)obj).printStackTrace();
			}
			try
			{
				logger.log(Level.FINER, "Arrows Plus [DEBUG] " + side.toString() + ": " + obj.toString());
				System.out.println("Arrows Plus [DEBUG] " + side.toString() + ": " + obj.toString());
			}

			catch (NullPointerException e)
			{
				logger.log(Level.FINER, "Arrows Plus [DEBUG] " + side.toString() + ": null");
				System.out.println("Arrows Plus [DEBUG]: null");
			}
		}
	}

	/**
	 * Stops the game and writes the error to the Forge crash log.
	 * 
	 * @param 	description	A string providing a short description of the problem.
	 * @param 	e			The exception that caused this method to be called.
	 */
	@SideOnly(Side.CLIENT)
	public void quitWithError(String description, Throwable e)
	{
		Writer stackTrace = new StringWriter();

		PrintWriter stackTraceWriter = new PrintWriter(stackTrace);
		e.printStackTrace(stackTraceWriter);

		logger.log(Level.FINER, "Arrows Plus: An exception occurred.\n" + stackTrace.toString());
		System.out.println("Arrows Plus: An exception occurred.\n" + stackTrace.toString());

		CrashReport crashReport = new CrashReport("Arrows Plus: " + description, e);
		net.minecraft.client.Minecraft.getMinecraft().crashed(crashReport);
	}

	/**
	 * Handles the FMLServerStartingEvent.
	 * 
	 * @param 	event	An instance of the FMLServerStarting event.
	 */
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandDebugMode());
		event.registerServerCommand(new CommandShowExperience());
		event.registerServerCommand(new CommandCheckUpdates());
		
		if (event.getServer() instanceof DedicatedServer)
		{
			isDedicatedServer = true;
			isIntegratedServer = false;
		}

		else
		{
			isDedicatedServer = false;
			isIntegratedServer = true;
		}

		ArrowsPlus.instance.log("Arrows Plus is running.");
	}

	/**
	 * Handles the FMLServerStoppingEvent.
	 * 
	 * @param 	event	An instance of the FMLServerStopping event.
	 */
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{
		if (isDedicatedServer)
		{
			for (WorldPropertiesManager manager : playerWorldManagerMap.values())
			{
				manager.saveWorldProperties();
			}
		}

		playerWorldManagerMap.clear();
		hasLoadedProperties = false;
		hasCompletedMainMenuTick = false;
	}

	/**
	 * Gets a player with the name provided.
	 * 
	 * @param 	username	The username of the player.
	 * 
	 * @return	The player entity with the specified username. Null if not on the server.
	 */
	public EntityPlayer getPlayerByName(String username)
	{
		for (WorldServer world : MinecraftServer.getServer().worldServers)
		{
			EntityPlayer player = world.getPlayerEntityByName(username);

			if (player != null)
			{
				return player;
			}

			else
			{
				continue;
			}
		}

		return null;
	}
}