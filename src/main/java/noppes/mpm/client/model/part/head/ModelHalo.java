package noppes.mpm.client.model.part.head;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import noppes.mpm.ModelData;
import noppes.mpm.ModelPartData;
import noppes.mpm.client.ClientEventHandler;
import noppes.mpm.client.model.ModelMPM;
import noppes.mpm.client.model.ModelPartInterface;
import noppes.mpm.constants.EnumParts;
import org.lwjgl.opengl.GL11;

public class ModelHalo extends ModelPartInterface {
    private AbstractClientPlayer entity;
    private boolean thinHalo;
    public float haloWidth;
    public float haloElevation;
    public float haloRotationX;
    public float haloRotationZ;
    public boolean shouldFloat;
    public boolean shouldRotate;
    public byte haloMaterial;
    public byte type;

    private final ModelRenderer haloBase;
    private final ModelRenderer haloThin;
    public final ModelRenderer[] haloSegments;
    public final ModelRenderer[] haloSegmentsThin;

    public ModelHalo(ModelMPM base) {
        super(base);
        this.textureWidth = 81;
        this.textureHeight = 34;

        haloBase = new ModelRenderer(base, 0, 34);
        haloBase.setTextureSize((int) textureWidth, (int) textureHeight);
        haloBase.setRotationPoint(0.0F, 0.0F, 0.0F);
        haloBase.addBox(-4.0F, -8.0F, -4.0F, 0, 0, 0, 0.0F);

        haloThin = new ModelRenderer(base, 0, 34);
        haloThin.setTextureSize((int) textureWidth, (int) textureHeight);
        haloThin.setRotationPoint(0.0F, 0.0F, 0.0F);
        haloThin.addBox(-4.0F, -8.0F, -4.0F, 0, 0, 0, 0.0F);

        haloSegments = new ModelRenderer[12];

        for(int i = 0; i < haloSegments.length; i++) {
            haloSegments[i] = new ModelRenderer(base, 0, 32);
            haloSegments[i].setTextureSize((int) textureWidth, (int) textureHeight);
            if(i == 0) {
                haloSegments[i].setRotationPoint(0.0F, -9.0F, -3.85F);
            } else {
                haloSegments[i].setRotationPoint(2F, 0F, 0F);
            }
            haloSegments[i].addBox(0F, -1F, 0F, 2, 1, 1, 0F);
            if(i == 0) {
                setRotateAngle(haloSegments[i], 0F, (float) (-Math.PI / haloSegments.length), 0F);
            } else {
                setRotateAngle(haloSegments[i], 0F, (float) (-2D * Math.PI / haloSegments.length), 0F);
                haloSegments[i - 1].addChild(haloSegments[i]);
            }
        }
        haloBase.addChild(haloSegments[0]);

        haloSegmentsThin = new ModelRenderer[48];

        for(int i = 0; i < haloSegmentsThin.length; i++) {
            haloSegmentsThin[i] = new ModelRenderer(base, 0, 32);
            haloSegmentsThin[i].setTextureSize((int) textureWidth, (int) textureHeight);
            haloSegmentsThin[i].setRotationPoint(0F, -9.0F, 0F);
            haloSegmentsThin[i].addBox((float) (40F/Math.PI), -1F, -1, 1, 1, 2, 0F);
            setRotateAngle(haloSegmentsThin[i], 0F, i * (float) (-2D * Math.PI / haloSegmentsThin.length), 0F);
            haloThin.addChild(haloSegmentsThin[i]);
        }

        addChild(haloBase);
        addChild(haloThin);
    }

    @Override
    public void setData(ModelData data, AbstractClientPlayer entity) {
        super.setData(data, entity);
        this.entity = entity;
    }

    @Override
    public void render(float f5) {
        if (this.isHidden || !this.showModel)
            return;
        GL11.glPushMatrix();
        GL11.glRotatef(haloRotationX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(haloRotationZ, 0.0F, 0.0F, 1.0F);
        if(shouldRotate) {
            GL11.glRotatef((float) entity.worldObj.getTotalWorldTime(), 0.0F, 1.0F, 0.0F);
        }
        float f1 = 0;
        if(shouldFloat) {
            float f = (float) entity.ticksExisted + ClientEventHandler.partialTicks;
            f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
            f1 = f1 * f1 + f1;
        }
        GL11.glTranslatef(0.0F, -0.2F + f1 * 0.05F, 0.0F);
        if (entity.isSneaking()) {
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if(thinHalo) {
            float width = haloWidth * 0.05F - 0.4F;
            float elevation = haloElevation * 0.05F - 0.4F;
            GL11.glTranslatef(0F, -0.4F - elevation, 0F);
            GL11.glScalef(0.7F + width, 0.3F, 0.7F + width);
        }

        float prevX = OpenGlHelper.lastBrightnessX;
        float prevY = OpenGlHelper.lastBrightnessY;
        if(haloMaterial <= 1) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }
        if(haloMaterial == 1) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        }

        super.render(f5);

        if(haloMaterial == 1) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);
        }
        if(haloMaterial <= 1) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
    }

    private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void initData(ModelData data) {
        ModelPartData config = data.getPartData(EnumParts.HALO);
        if(config == null) {
            isHidden = true;
            return;
        }
        isHidden = false;
        this.color = config.color;

        type = config.type;
        thinHalo = type == 1;
        shouldFloat = config.pattern <= 1;
        shouldRotate = config.pattern % 2 == 0;
        haloBase.isHidden = thinHalo;
        haloThin.isHidden = !thinHalo;
        haloWidth = getWidth(config) * 14F;
        haloElevation = getElevation(config) * 14F;
        haloRotationX = getRotationX(config) * 180 - 90;
        haloRotationZ = getRotationZ(config) * 180 - 90;
        haloMaterial = getMaterial(config);
    }

    public static float getWidth(ModelPartData data) {
        return data.customData.hasKey("width") ? data.customData.getFloat("width") : 0.5F;
    }

    public static void setWidth(ModelPartData data, float value) {
        if(Math.abs(value - 0.5F) > 0.001) {
            data.customData.setFloat("width", value);
        } else {
            data.customData.removeTag("width");
        }
    }

    public static float getElevation(ModelPartData data) {
        return data.customData.hasKey("elevation") ? data.customData.getFloat("elevation") : 0.5F;
    }

    public static void setElevation(ModelPartData data, float value) {
        if(Math.abs(value - 0.5F) > 0.001) {
            data.customData.setFloat("elevation", value);
        } else {
            data.customData.removeTag("elevation");
        }
    }

    public static float getRotationX(ModelPartData data) {
        return data.customData.hasKey("rotX") ? data.customData.getFloat("rotX") : 0.5F;
    }

    public static void setRotationX(ModelPartData data, float value) {
        if(Math.abs(value - 0.5F) > 0.001) {
            data.customData.setFloat("rotX", value);
        } else {
            data.customData.removeTag("rotX");
        }
    }

    public static float getRotationZ(ModelPartData data) {
        return data.customData.hasKey("rotZ") ? data.customData.getFloat("rotZ") : 0.5F;
    }

    public static void setRotationZ(ModelPartData data, float value) {
        if(Math.abs(value - 0.5F) > 0.001) {
            data.customData.setFloat("rotZ", value);
        } else {
            data.customData.removeTag("rotZ");
        }
    }

    public static byte getMaterial(ModelPartData data) {
        return data.customData.hasKey("material") ? data.customData.getByte("material") : 0;
    }

    public static void setMaterial(ModelPartData data, byte material) {
        if(material != 0) {
            data.customData.setByte("material", material);
        } else {
            data.customData.removeTag("material");
        }
    }
}
