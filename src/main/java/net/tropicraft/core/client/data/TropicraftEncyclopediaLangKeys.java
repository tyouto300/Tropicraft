package net.tropicraft.core.client.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import net.tropicraft.Tropicraft;

import javax.annotation.Nullable;

public enum TropicraftEncyclopediaLangKeys {
    UNKNOWN("unknown", "???", "Page not found"),
    //Section page entries
    TROPICAL_FOOD("section.food","Tropical Food"),

    //Item page entries
    ENCYCLOPEDIA_TROPICA("encyclopedia", "About Encyclopedia", "The Encyclopedia Tropica is the source for information about the Tropics many items. Having been printed with magical ink, the book writes itself as you acquire the item. New Entries will be blue, and you can navigate the pages using the tabs on the sides of the book. Items that are craftable will have an anvil tab."),

    //Entity page entries
    IGUANA("iguana", "Iguana", "These neutral lizards like to hang around the tropics. I wonder if their scales could be used for anything?")
    ;
    //section + title is the translation key
    //TODO:investigate using EnumHashMap as a way to quickly register all pages
    private final String titleKey;
    private final String title;
    private final Component titleComponent;
    private final @Nullable String descKey;
    private final @Nullable String desc;
    private final @Nullable Component descComponent;

    //Intended for section pages, who only need one entry in the lang table
    TropicraftEncyclopediaLangKeys(String sectionName, String sectionTitle) {
        //add section + sectionName . title to the translation key
        this(sectionName, sectionTitle, null);
    }
    //intended for entry pages, with a translatable title and description
    TropicraftEncyclopediaLangKeys(String name, String title, @Nullable String desc) {
        //add name . title and name . desc to key
        this.titleKey = Util.makeDescriptionId(Tropicraft.ID + ".encyclopedia.", Tropicraft.id(name + ".title"));
        this.title = title;
        this.titleComponent = Component.translatable(titleKey);
        if (desc != null && !desc.isEmpty()) {
            this.descKey = Util.makeDescriptionId(Tropicraft.ID + ".encyclopedia.", Tropicraft.id(name + ".desc"));
            this.desc = desc;
            descComponent = Component.translatable(descKey);
        }
        else {//improve this
            descKey = null;
            this.desc = null;
            descComponent = null;
        }
    }
    public static TropicraftEncyclopediaLangKeys getUnknown() { return TropicraftEncyclopediaLangKeys.UNKNOWN; }
    public static TropicraftEncyclopediaLangKeys get(String name) {
        try {
            return TropicraftEncyclopediaLangKeys.valueOf(name);
        } catch (IllegalArgumentException e) {
            return getUnknown();
        }
    }
    public Component getTitle() { return titleComponent; }
    @Nullable
    public Component getDesc() { return descComponent; }
    public MutableComponent formatTitle(ChatFormatting format) { return titleComponent.copy().withStyle(format);}

    public static void generate(RegistrateLangProvider prov) {
        for(TropicraftEncyclopediaLangKeys lang : values()) {
            prov.add(lang.titleKey, lang.title);
            if(lang.descKey != null) { prov.add(lang.descKey, lang.desc); }
        }

    }
}
