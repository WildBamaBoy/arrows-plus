/*******************************************************************************
 * RenderArrow.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package arrowsplus.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import arrowsplus.entity.EntityArrowBase;

/**
 * Determines how to render an arrow.
 */
public class RenderArrow extends Render
{
	//All resource locations for the different arrows.
	private static final ResourceLocation textureAlder = new ResourceLocation("arrowsplus:textures/entity/arrow_alder.png");
	private static final ResourceLocation textureAsh = new ResourceLocation("arrowsplus:textures/entity/arrow_ash.png");
	private static final ResourceLocation textureAspen = new ResourceLocation("arrowsplus:textures/entity/arrow_aspen.png");
	private static final ResourceLocation textureBeech = new ResourceLocation("arrowsplus:textures/entity/arrow_beech.png");
	private static final ResourceLocation textureCottonwood = new ResourceLocation("arrowsplus:textures/entity/arrow_cottonwood.png");
	private static final ResourceLocation textureGum = new ResourceLocation("arrowsplus:textures/entity/arrow_gum.png");
	private static final ResourceLocation textureHardMaple = new ResourceLocation("arrowsplus:textures/entity/arrow_hardmaple.png");
	private static final ResourceLocation textureHickory = new ResourceLocation("arrowsplus:textures/entity/arrow_hickory.png");
	private static final ResourceLocation textureMahogany = new ResourceLocation("arrowsplus:textures/entity/arrow_mahogany.png");
	private static final ResourceLocation textureSoftMaple = new ResourceLocation("arrowsplus:textures/entity/arrow_softmaple.png");
	private static final ResourceLocation textureSycamore = new ResourceLocation("arrowsplus:textures/entity/arrow_sycamore.png");
	private static final ResourceLocation textureSypherus = new ResourceLocation("arrowsplus:textures/entity/arrow_sypherus.png");

	@Override
	public void doRender(Entity entity, double posX, double posY, double posZ, float rotationYaw, float rotationPitch)
	{
		this.renderArrow((EntityArrowBase)entity, posX, posY, posZ, rotationYaw, rotationPitch);
	}

	/**
	 * Renders the arrow.
	 * 
	 * @param entityArrow	The arrow to render.
	 * @param posX			The x position to render the arrow at.
	 * @param posY			The y position to render the arrow at.
	 * @param posZ			The z position to render the arrow at.
	 * @param yaw			The yaw rotation to render the arrow with.
	 * @param pitch			The pitch to render the arrow with.
	 */
	public void renderArrow(EntityArrowBase entityArrow, double posX, double posY, double posZ, float yaw, float pitch)
	{
		this.func_110777_b(entityArrow);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)posX, (float)posY, (float)posZ);
		GL11.glRotatef(entityArrow.prevRotationYaw + (entityArrow.rotationYaw - entityArrow.prevRotationYaw) * pitch - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entityArrow.prevRotationPitch + (entityArrow.rotationPitch - entityArrow.prevRotationPitch) * pitch, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		byte b0 = 0;
		float f2 = 0.0F;
		float f3 = 0.5F;
		float f4 = (float)(0 + b0 * 10) / 32.0F;
		float f5 = (float)(5 + b0 * 10) / 32.0F;
		float f6 = 0.0F;
		float f7 = 0.15625F;
		float f8 = (float)(5 + b0 * 10) / 32.0F;
		float f9 = (float)(10 + b0 * 10) / 32.0F;
		float f10 = 0.05625F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		float f11 = (float)entityArrow.arrowShake - pitch;
	
		if (f11 > 0.0F)
		{
			float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
			GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
		}
	
		GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(f10, f10, f10);
		GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
		GL11.glNormal3f(f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f9);
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f9);
		tessellator.draw();
		GL11.glNormal3f(-f10, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f8);
		tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f8);
		tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f9);
		tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f9);
		tessellator.draw();
	
		for (int i = 0; i < 4; ++i)
		{
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f10);
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double)f2, (double)f4);
			tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double)f3, (double)f4);
			tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double)f3, (double)f5);
			tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double)f2, (double)f5);
			tessellator.draw();
		}
	
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation func_110775_a(Entity entity)
	{
		return this.getResourceLocation((EntityArrowBase)entity);
	}

	/**
	 * Gets the correct ResourceLocation for the provided arrow.
	 * 
	 * @param 	entityArrow	The arrow being rendered.
	 * 
	 * @return	A ResourceLocation appropriate for the provided EntityArrow.
	 */
	protected ResourceLocation getResourceLocation(EntityArrowBase entityArrow)
	{
		switch (entityArrow.arrowType)
		{
		case 0: return textureAspen; 
		case 1: return textureCottonwood;
		case 2: return textureAlder;
		case 3: return textureSycamore;
		case 4: return textureGum;
		case 5: return textureSoftMaple;
		case 6: return textureAsh;
		case 7: return textureBeech;
		case 8: return textureHardMaple;
		case 9: return textureHickory;
		case 10: return textureMahogany;
		case 11: return textureSypherus;
		default: return null;
		}
	}
}
