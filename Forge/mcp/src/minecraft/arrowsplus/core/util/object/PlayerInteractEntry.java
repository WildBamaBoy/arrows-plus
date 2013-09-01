/*******************************************************************************
 * PlayerInteractEntry.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.core.util.object;

/**
 * Small object used in determining if experience should be gained or not. Makes things easier.
 */
public class PlayerInteractEntry 
{
	public int x;
	public int y;
	public int z;
	public int blockID;
	public int meta;
	
	/**
	 * Constructor
	 * 
	 * @param 	x		The block's x position.
	 * @param 	y		The block's y position.
	 * @param 	z		The block's z position.
	 * @param 	blockID	The block's ID.
	 * @param 	meta	The block's metadata value.
	 */
	public PlayerInteractEntry(int x, int y, int z, int blockID, int meta)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockID = blockID;
		this.meta = meta;
	}
}
