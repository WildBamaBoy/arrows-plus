/*******************************************************************************
 * WorldPropertiesList.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core.io;

import java.io.Serializable;

/**
 * Contains the fields that are used by the world properties manager.
 */
public class WorldPropertiesList implements Serializable
{
	public String playerName = "";
	public int playerID = 0;
	public int selectedArrowMeta = -1;
	public float stat_WoodcuttingExperience = 0.0F;
	public float archerFactor = 0.0F;
}
