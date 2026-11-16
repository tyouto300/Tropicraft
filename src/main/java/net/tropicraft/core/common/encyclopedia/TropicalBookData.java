package net.tropicraft.core.common.encyclopedia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.client.data.TropicraftEncyclopediaLangKeys;
import net.tropicraft.core.common.network.message.ClientboundUpdateEncyclopediaPacket;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Tropicraft.ID)
public class TropicalBookData{
    private boolean dirty;
    private Map<String, ReadState> pageStates;

    public static final MapCodec<TropicalBookData> CODEC = RecordCodecBuilder.mapCodec( i -> i.group(
            Codec.STRING.listOf().fieldOf("pageNames").forGetter(TropicalBookData::getPageNames),
            Codec.STRING.listOf().fieldOf("readStates").forGetter(TropicalBookData::getReadStates)
            ).apply(i, TropicalBookData::new)
            );
    public static final StreamCodec<ByteBuf, TropicalBookData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            TropicalBookData::getPageNames,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            TropicalBookData::getReadStates,
            TropicalBookData::new
    );

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tropicraft.ID);
    public static final Supplier<AttachmentType<TropicalBookData>> ATTACHMENT = ATTACHMENT_TYPES.register(
        "tropical_book_data", () -> AttachmentType.builder((Supplier<TropicalBookData>) TropicalBookData::new).serialize(CODEC).build()
    );

    //Instead of having two lists stored in player data separately, write as pageName:readState
    //Possibly move to only writing the page names of those that have a state other than hidden
    public List<String> getPageNames() {
        return pageStates.keySet().stream().toList();
    }
    public void updateReadState(String pageName) {
        int stateNum = pageStates.get(pageName).getState();
        pageStates.put(pageName,
                ReadState.byStateNum(Math.min(stateNum + 1, ReadState.values().length)));
    }
    public void setReadState(String pageName, ReadState readState) {
        pageStates.put(pageName, readState);
    }
    public List<String> getReadStates() {
        return pageStates.values().stream().map(ReadState::getSerializedName).toList();
    }
    public Map<String, ReadState> getPageStates() {
        return pageStates;
    }
    void updateClient(ServerPlayer target, boolean force) {
        if (dirty || force) {
            PacketDistributor.sendToPlayer(target, new ClientboundUpdateEncyclopediaPacket(this));
        }
    }
    /*private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        for ( String pageName : pageStates.keySet()) {
            map.put(pageName, pageStates.get(pageName).getSerializedName());
        }
        return map;
    }*/
    private Map<String, ReadState> initMap() {
        Map<String, ReadState> map = new HashMap<>();
        Iterator<String> pageIterator = Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title)
                .toList().iterator();
        while (pageIterator.hasNext()) {
            map.put(pageIterator.next(), ReadState.HIDDEN);
        }
        return map;
    }
    private Map<String, ReadState> recreateMap(List<String> pgNames, List<String> visibilities) {
        Map<String,ReadState> map = new HashMap<>();
        for(int i = 0; i < pgNames.size();i++) {
            map.put(pgNames.get(i), ReadState.valueOf(visibilities.get(i)));
        }
        return map;
    }
    /*public void copyFrom(TropicalBookData data) {
        pageNames = data.getPageNames();
        readStates = data.getReadStates();
    }*/
    public void copyFrom(TropicalBookData data) {
        pageStates = data.getPageStates();
    }
    public void copyFrom(Map<String, ReadState> map) {
        pageStates = map;
    }
    public TropicalBookData() {
        //pageNames = Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title).toList();
        //readStates = pageNames.stream().map(value -> ReadState.HIDDEN).toList();
        pageStates = initMap();
    }
    public TropicalBookData(List<String> pageNames, List<String> readStates) {
        pageStates = recreateMap(pageNames, readStates);
    }
    public TropicalBookData(Map<String,ReadState> map) {
        pageStates = map;
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){ updateClient(event);}
    private static void updateClient(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(ATTACHMENT).updateClient(player, true);
        }
    }
        //note for future: When implementing the book writing itself on item pickup, do it with packets. When an item is picked up, wait a little bit(some ticks)
        //before sending the update packet in case it turns out you write another one and maybe have to recreate the whole savedData map or something. idk just a thought

}
