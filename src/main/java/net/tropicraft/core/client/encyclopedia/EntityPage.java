package net.tropicraft.core.client.encyclopedia;

import com.tterrag.registrate.util.entry.EntityEntry;
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
import net.tropicraft.core.common.entity.TropicraftEntities;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;
//TODO:Implement a way to discover entity pages, currently you only discover their drops
public class EntityPage extends ItemPage {
    private static final Identifier PANEL_BG = Identifier.fromNamespaceAndPath(Tropicraft.ID, "textures/block/thatch_side.png");
    private  final EntityEntry<? extends LivingEntity> entityEntry;
    private  @Nullable LivingEntity entity;

    public EntityPage(String id, EntityEntry<? extends LivingEntity> entityEntry, ItemStack icon) {
        super(id, icon);
        this.entityEntry = entityEntry;
    }

    private LivingEntity makeEntity() {
        EntityType<? extends LivingEntity> type = entityEntry.get();
        LivingEntity ret;
        ret = (LivingEntity) type.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
        ret.setId(ret.level().getRandom().nextInt());
        return ret;
    }

    protected void drawEntity(GuiGraphicsExtractor graphics, int x, int y, float mouseX, float mouseY) {
        if (entity == null) {entity = makeEntity();}
        x += 35;
        y -= 2;
        int x2 = x + 67;
        int y2 = y + getHeaderHeight() + 10;

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

        graphics.entity(renderState, 30F, translation, rotation, xRotation, x, y, x2, y2);
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = renderDispatcher.getRenderer(entity);
        return renderer.createRenderState(entity, 1.0F);
    }

    @Override
    public int getHeaderHeight() { return (int) (entity == null ? 0 : entity.getBbHeight() * 33) + 12;}
    @Override
    public void drawHeader(GuiGraphicsExtractor graphics,  int x, int y, float mouseX, float mouseY, float cycle) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, PANEL_BG, x + 14, y,
                0,0,106, getHeaderHeight() + 10, 16, 16);
        drawEntity(graphics, x ,y, mouseX, mouseY);
    }
}
