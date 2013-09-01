/*******************************************************************************
 * PacketHandler.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core.forge;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.io.ModPropertiesManager;
import arrowsplus.core.io.WorldPropertiesManager;
import arrowsplus.core.util.PacketHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

/**
 * Handles packets received both client and server side.
 */
public class PacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player senderPlayer) 
	{
		try
		{
			ArrowsPlus.instance.logDebug("Received packet: " + packet.channel + ". Size = " + packet.length);

			if (packet.channel.equals("AP_LOGIN"))
			{
				handleLogin(packet, senderPlayer);
			}

			else if (packet.channel.equals("AP_WORLDPROP"))
			{
				handleWorldProperties(packet, senderPlayer);
			}
			
			else if (packet.channel.equals("AP_ADDITEM"))
			{
				handleAddItem(packet, senderPlayer);
			}
			
			else
			{
				throw new IllegalArgumentException("Unhandled packet channel.");
			}
		}

		catch (Throwable e)
		{
			ArrowsPlus.instance.log(e);
		}
	}

	/**
	 * Handles a login packet.
	 * 
	 * @param 	packet	The packet containing the login information.
	 * @param	player	The player that the packet came from.
	 */
	private void handleLogin(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = ArrowsPlus.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;
		EntityPlayer player = (EntityPlayer)senderPlayer;

		//Assign received data.
		ModPropertiesManager modPropertiesManager = (ModPropertiesManager) objectInput.readObject();

		//Ensure item IDs are the same.
		if (modPropertiesManager.equals(ArrowsPlus.instance.modPropertiesManager))
		{
			//Give the player a world settings manager.
			WorldPropertiesManager manager = new WorldPropertiesManager(world.getSaveHandler().getWorldDirectoryName(), player.username);

			manager.worldProperties.playerName = player.username;
			manager.saveWorldProperties();
			
			ArrowsPlus.instance.playerWorldManagerMap.put(player.username, manager);

			//Send it to the client.
			PacketDispatcher.sendPacketToPlayer(PacketHelper.createWorldPropertiesPacket(manager), senderPlayer);
		}

		else
		{
			((EntityPlayerMP)senderPlayer).playerNetServerHandler.kickPlayerFromServer("Arrows Plus: Server item IDs do not match your own. You cannot log in.");
		}
	}

	/**
	 * Handles a world properties packet.
	 * 
	 * @param 	packet	The packet containing the world properties information.
	 * @param	player	The player that the packet came from.
	 */
	private void handleWorldProperties(Packet250CustomPayload packet, Player senderPlayer) throws IOException, ClassNotFoundException
	{
		byte[] data = ArrowsPlus.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		World world = ((EntityPlayer)senderPlayer).worldObj;
		EntityPlayer player = (EntityPlayer)senderPlayer;

		//Assign received data.
		WorldPropertiesManager manager = (WorldPropertiesManager)objectInput.readObject();
		ArrowsPlus.instance.logDebug("Received world properties manager for " + ((EntityPlayer)senderPlayer).username);

		//Client side.
		if (world.isRemote)
		{
			ArrowsPlus.instance.playerWorldManagerMap.put(player.username, manager);
		}

		//Server side.
		else
		{
			//Update only the actual properties on the old manager to retain the ability to save.
			WorldPropertiesManager oldWorldPropertiesManager = ArrowsPlus.instance.playerWorldManagerMap.get(player.username);
			oldWorldPropertiesManager.worldProperties = manager.worldProperties;

			//Put the changed manager back into the map and save it.
			ArrowsPlus.instance.playerWorldManagerMap.put(player.username, oldWorldPropertiesManager);
			oldWorldPropertiesManager.saveWorldProperties();
		}
	}
	
	/**
	 * Handles a packet that adds an item to the player's inventory.
	 * 
	 * @param 	packet	The packet containing the item data.
	 * @param 	player	The player that the packet came from.
	 */
	private void handleAddItem(Packet250CustomPayload packet, Player senderPlayer) throws ClassNotFoundException, IOException 
	{
		byte[] data = ArrowsPlus.decompressBytes(packet.data);

		ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);

		//Assign received data.
		int itemId = (Integer)objectInput.readObject();
		int stackSize = (Integer)objectInput.readObject();
		int playerId = (Integer)objectInput.readObject();

		EntityPlayer player = (EntityPlayer)senderPlayer;
		player.inventory.addItemStackToInventory(new ItemStack(itemId, stackSize, 0));
	}
}
