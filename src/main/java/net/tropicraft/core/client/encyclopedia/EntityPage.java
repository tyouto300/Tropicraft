package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.tropicraft.Tropicraft;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;

public class EntityPage extends ItemPage {
    private static final Identifier PANEL_BG = Identifier.fromNamespaceAndPath(Tropicraft.ID, "textures/block/thatch_side.png");
    private final Identifier entityId;
    private  @Nullable LivingEntity entity;

    public EntityPage(String id, Identifier entityId, ItemStack icon) {
        super(id, icon);
        this.entityId = entityId;
    }
    protected Identifier getEntityId() { return entityId; }

    private LivingEntity makeEntity() {
        Optional<EntityType<?>> optionalType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityId);
        LivingEntity ret;
        if(optionalType.isPresent()) {
            EntityType<?> realType = optionalType.get();
            ret =  (LivingEntity) realType.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
        } else {
            ret = EntityTypes.ZOMBIE.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
        }
        ret.setId(ret.level().getRandom().nextInt());
        return ret;
    }

    protected void drawEntity(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY) {
        if (entity == null) {entity = makeEntity();}
        x += 67;
        y -= 2;
        int x2 = x + 32;
        int y2 = y + getHeaderHeight();

        float centerX = (float)(x + x2) / 2.0F;
        float centerY = (float)(y + y2) / 2.0F;
        float xAngle = (float)Math.atan((double)((centerX - mouseX) / 40.0F));
        float yAngle = (float)Math.atan((double)((centerY - mouseY) / 40.0F));
        Quaternionf rotation = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf xRotation = (new Quaternionf()).rotateX(yAngle * 20.0F * ((float)Math.PI / 180F));
        rotation.mul(xRotation);

        EntityRenderState renderState = extractRenderState(entity);
        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.bodyRot = 180.0F + xAngle * 20.0F;
            livingRenderState.yRot = xAngle * 20.0F;
            if (livingRenderState.pose != Pose.FALL_FLYING) {
                livingRenderState.xRot = -yAngle * 20.0F;
            } else {
                livingRenderState.xRot = 0.0F;
            }

            livingRenderState.boundingBoxWidth /= livingRenderState.scale;
            livingRenderState.boundingBoxHeight /= livingRenderState.scale;
            livingRenderState.scale = 1.0F;
        }

        Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);

        graphics.entity(renderState, 20F, translation, rotation, xRotation, x, y, x2, y2);
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = renderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        return renderState;
    }

    @Override
    public int getHeaderHeight() { return (int) (entity == null ? 0 : entity.getBbHeight() * 33) + 12;}
    @Override
    public void drawHeader(GuiGraphicsExtractor graphics,  int x, int y, float mouseX, float mouseY, float cycle) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, PANEL_BG, x + 14, y,
                0,0,106, getHeaderHeight() + 10, 16, 16);
        drawEntity(graphics, x ,y, mouseX, mouseY);
    }

    @Override
    public String getLocalizedTitle() {
        if (entity == null) {
            entity = makeEntity();
        }
        return Component.translatableWithFallback(super.getLocalizedTitle(), entity.getDisplayName().toString()).toString();
    }

}
