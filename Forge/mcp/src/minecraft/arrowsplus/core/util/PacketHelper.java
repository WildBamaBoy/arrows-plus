/*******************************************************************************
 * PacketCreator.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.io.ModPropertiesManager;
import arrowsplus.core.io.WorldPropertiesManager;

/**
 * Creates packets used by ArrowsPlus to communicate between the client and server.
 */
public final class PacketHelper 
{
	/**
	 * Creates a packet used to log in to a server running ArrowsPlus.
	 * 
	 * @param 	modPropertiesManager	An instance of the client's mod properties manager.
	 * 
	 * @return	A login packet.
	 */
	public static Packet createLoginPacket(ModPropertiesManager modPropertiesManager)
	{
		try
		{
			Packet250CustomPayload thePacket = new Packet250CustomPayload();
			thePacket.channel = "AP_LOGIN";

			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeObject(modPropertiesManager);
			objectOutput.close();

			thePacket.data = ArrowsPlus.compressBytes(byteOutput.toByteArray());
			thePacket.length = thePacket.data.length;

			ArrowsPlus.instance.logDebug("Sent packet: " + thePacket.channel);
			return thePacket;
		}

		catch (Throwable e)
		{
			ArrowsPlus.instance.log(e);
			return null;
		}
	}
	
	/**
	 * Creates a packet used to give a client or server a player's world properties.
	 * 
	 * @param 	worldPropertiesManager	An instance of the server world properties manager.
	 * 
	 * @return	A world properties packet.
	 */
	public static Packet createWorldPropertiesPacket(WorldPropertiesManager worldPropertiesManager)
	{
		try
		{
			Packet250CustomPayload thePacket = new Packet250CustomPayload();
			thePacket.channel = "AP_WORLDPROP";

			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeObject(worldPropertiesManager);
			objectOutput.close();

			thePacket.data = ArrowsPlus.compressBytes(byteOutput.toByteArray());
			thePacket.length = thePacket.data.length;

			ArrowsPlus.instance.logDebug("Created world properties packet for " + worldPropertiesManager.worldProperties.playerName);
			ArrowsPlus.instance.logDebug("Sent packet: " + thePacket.channel);
			return thePacket;
		}

		catch (Throwable e)
		{
			ArrowsPlus.instance.log(e);
			return null;
		}
	}
	
	/**
	 * Creates a packet used to add an item to the provided player's inventory.
	 * 
	 * @param 	itemId		The id of the item to add.
	 * @param	stackSize	The size of the stack that the item will be put in.
	 * @param 	playerId	The id of the player that is receiving the item.
	 * 
	 * @return	An add item packet.
	 */
	public static Packet createAddItemPacket(int itemId, int stackSize, int playerId)
	{
		try
		{
			Packet250CustomPayload thePacket = new Packet250CustomPayload();
			thePacket.channel = "AP_ADDITEM";

			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeObject(itemId);
			objectOutput.writeObject(stackSize);
			objectOutput.writeObject(playerId);
			objectOutput.close();

			thePacket.data = ArrowsPlus.compressBytes(byteOutput.toByteArray());
			thePacket.length = thePacket.data.length;

			ArrowsPlus.instance.logDebug("Sent packet: " + thePacket.channel);
			return thePacket;
		}

		catch (Throwable e)
		{
			ArrowsPlus.instance.log(e);
			return null;
		}
	}
}
