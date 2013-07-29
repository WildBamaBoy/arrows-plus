/*******************************************************************************
 * ClientTickHandler.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Handles ticks client-side.
 */
public class ClientTickHandler implements ITickHandler
{
	/** The number of ticks since the main loop has been run.*/
	public int ticks = 20;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		//Determine where the tick came from and pass the tick to 
		//the appropriate tick handler.
		if (type.equals(EnumSet.of(TickType.CLIENT)))
		{
			GuiScreen guiScreen = Minecraft.getMinecraft().currentScreen;

			if (guiScreen != null)
			{
				onTickInGui(guiScreen);
			}

			else
			{
				onTickInGame();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() 
	{
		return "ArrowsPlus Client Ticks";
	}

	/**
	 * Fires once per tick in the game.
	 */
	public void onTickInGame()
	{
		//Run this every 5 ticks.
		if (ticks >= 5)
		{
			//Check if Setup needs to run.
			if (Minecraft.getMinecraft().isSingleplayer())
			{
				try
				{
					//Debug checks
					if (ArrowsPlus.instance.inDebugMode)
					{
						//Run any debug logic.
					}
				}

				catch (NullPointerException e)
				{
					ArrowsPlus.instance.log("Client tick error!");
					ArrowsPlus.instance.log(e);
				}

				finally
				{
					//Reset ticks back to zero.
					ticks = 0;
				}
			}

			else
			{
				if (!ArrowsPlus.instance.isDedicatedClient)
				{
					ArrowsPlus.instance.isDedicatedClient = true;
					ArrowsPlus.instance.isIntegratedClient = false;
				}

				else
				{
					WorldPropertiesManager clientPropertiesManager = ArrowsPlus.instance.playerWorldManagerMap.get(Minecraft.getMinecraft().thePlayer.username);

					if (clientPropertiesManager != null)
					{
						//Copy of any client logic for the dedicated client.
					}
				}
			}
		}

		else //Ticks isn't greater than or equal to 20.
		{
			ticks++;
		}
	}

	/**
	 * Fires once per tick when a GUI screen is open.
	 * 
	 * @param 	guiScreen	The GUI that is currently open.
	 */
	public void onTickInGui(GuiScreen guiScreen)
	{
		//If the GUI is the main menu, reset ticks and world properties.
		if (guiScreen instanceof GuiMainMenu)
		{
			if (!ArrowsPlus.instance.hasCompletedMainMenuTick)
			{
				ticks = 20;

				//Reset world specific data.
				ArrowsPlus.instance.playerWorldManagerMap.clear();
				ArrowsPlus.instance.hasCompletedMainMenuTick = true;
				ArrowsPlus.instance.playerWorldManagerMap.clear();
				ArrowsPlus.instance.hasLoadedProperties = false;
			}
		}
		
		//If the GUI screen is the Select World screen, empty all world properties.
		else if (guiScreen instanceof GuiSelectWorld)
		{
			WorldPropertiesManager.emptyOldWorldProperties();
		}
	}
}
