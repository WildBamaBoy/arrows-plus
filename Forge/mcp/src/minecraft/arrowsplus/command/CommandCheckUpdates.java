/*******************************************************************************
 * CommandCheckUpdates.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.command;

import arrowsplus.core.ArrowsPlus;
import arrowsplus.core.util.Color;
import arrowsplus.core.util.object.UpdateHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;

/**
 * Defines the debug mode command and what it does.
 */
public class CommandCheckUpdates extends CommandBase
{
	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/ap.checkupdates <on/off>";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) 
	{
		return true;
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Override
	public String getCommandName() 
	{
		return "ap.checkupdates";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] arguments) 
	{
		if (arguments.length == 1)
		{
			if (arguments[0].toLowerCase().equals("on"))
			{
				sender.sendChatToPlayer(new ChatMessageComponent().func_111072_b(Color.GREEN + "Arrows Plus will now automatically check for updates."));
				ArrowsPlus.instance.hasCheckedForUpdates = false;
				ArrowsPlus.instance.modPropertiesManager.modProperties.checkForUpdates = true;
				ArrowsPlus.instance.modPropertiesManager.saveModProperties();
				
				new Thread(new UpdateHandler(sender)).run();
			}

			else
			{
				sender.sendChatToPlayer(new ChatMessageComponent().func_111072_b(Color.RED + "Arrows Plus will no longer report that version " + UpdateHandler.mostRecentVersion + " is available."));				
				ArrowsPlus.instance.modPropertiesManager.modProperties.checkForUpdates = false;
				ArrowsPlus.instance.modPropertiesManager.saveModProperties();
				
				new Thread(new UpdateHandler(sender)).run();
			}
		}

		else
		{
			throw new WrongUsageException(getCommandUsage(sender));
		}
	}
}
