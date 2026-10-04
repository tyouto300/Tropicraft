package net.tropicraft.core.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.client.encyclopedia.EntityPage;
import net.tropicraft.core.client.encyclopedia.GuiTropicalBook;
import net.tropicraft.core.client.encyclopedia.ItemPage;
import net.tropicraft.core.client.encyclopedia.SectionPage;
import net.tropicraft.core.common.encyclopedia.TropicalBook;
import net.tropicraft.core.common.entity.TropicraftEntities;

import static net.neoforged.fml.loading.FMLEnvironment.getDist;

public class EncyclopediaItem extends Item {

    public EncyclopediaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        String insidePath = "textures/gui/encyclopedia/encyclopedia_tropica_inside.png";
        String outsidePath = "textures/gui/encyclopedia/encyclopedia_tropica_outside.png";
        TropicalBook book = new TropicalBook("savedata", insidePath, outsidePath);
        ItemPage encyclopediaPage = new ItemPage("encyclopedia_tropica", new ItemStack(TropicraftItems.ENCYCLOPEDIA.get()));
        EntityPage iguanaPage = new EntityPage("iguana", TropicraftEntities.IGUANA, new ItemStack(TropicraftItems.IGUANA_SPAWN_EGG.get()));
        SectionPage foodPage = new SectionPage("tropical_food");
        book.addPage(encyclopediaPage);
        book.addPage(foodPage);
        book.addPage(iguanaPage);
        for(int i = 0; i < book.getPageCount(); i++) {
            book.markPageAsNewlyVisible(i);
        }

        if(getDist().isClient() /*&& Tropicraft.encyclopedia != null*/) {
            //Tropicraft.encyclopedia.discoverPages(level, player);

            net.minecraft.client.Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().gui.setScreen(new GuiTropicalBook(/*Tropicraft.encyclopedia*/book , true));
            });
        }
        return InteractionResult.SUCCESS;
    }

}
