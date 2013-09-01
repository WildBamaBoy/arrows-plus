/*******************************************************************************
 * CommandShowExperience.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.command;

import java.text.DecimalFormat;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.io.WorldPropertiesManager;
import arrowsplus.core.util.Color;

/**
 * Defines the show experience command and what it does.
 */
public class CommandShowExperience extends CommandBase
{
	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/ap.xp";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public String getCommandName() 
	{
		return "ap.xp";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) 
	{
		try
		{
			WorldPropertiesManager manager = ArrowsPlus.instance.playerWorldManagerMap.get(sender.getCommandSenderName());
			sender.sendChatToPlayer(new ChatMessageComponent().func_111072_b(
					Color.DARKGREEN + 
					"Your experience: " + Float.parseFloat(new DecimalFormat("#.##").format(manager.worldProperties.stat_WoodcuttingExperience)) + 
					"/100"));
		}
		
		catch (Throwable e)
		{
			sender.sendChatToPlayer(new ChatMessageComponent().func_111072_b(Color.RED + "An unknown error occurred."));
		}
	}
}
