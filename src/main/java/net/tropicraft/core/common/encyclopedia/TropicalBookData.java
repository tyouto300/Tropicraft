package net.tropicraft.core.common.encyclopedia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
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

/*
TropicalBook(TB) is responsible for serving loaded pages to the GuiTropicalBook(GTB), which is responsible for displaying them to the client.
TropicalBook is also responsible for initiating/requesting data writes to the saved data.
Should only be one TB object created, and it is on the server side.
TropicalBookSavedData(TBSD) is responsible for managing the written data. It is the one that actually writes and updates data(on request from TropicalBook),
it is responsible for reading the data(on request from TropicalBook) and sending the pages that are visible.

Thus, TBSD is the one who maintains an internal mapping of strings representing page names(from TropicraftEncyclopediaLangKeys) along with a
tri-state(or maybe rather boolean) value representing the state of that page(read, newly visible, hidden).
TBSD should have an attribute regarding who it belongs to.
While the gui is open, the TBSD should not be updated or else it would break the viewing. Test with this, maybe it can rewrite fast enough
or pause the screen while it does.
When updating the TBSD, everything unfortunately must be rewritten at once, hence why I should try to group the rewrite requests together to rewrite
the least amount of times for the most changes
*/
@EventBusSubscriber(modid = Tropicraft.ID)
public class TropicalBookData{
    private boolean dirty;
    private List<String> pageNames; //Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title).toList();
    //Id rather this be integers instead that map to read states
    private List<String> readStates;
    public static final MapCodec<TropicalBookData> CODEC = RecordCodecBuilder.mapCodec( i -> i.group(
            Codec.STRING.listOf().fieldOf("pageNames").forGetter(TropicalBookData::getPageNames),
            //TropicraftEncyclopediaLangKeys.CODEC.listOf().fieldOf("pageNames").forGetter(TropicalBookData::getPageNames)
            //ReadState.CODEC.listOf().fieldOf("readStates").forGetter(TropicalBookData::getReadStates)
            Codec.STRING.listOf().fieldOf("visibilities").forGetter(TropicalBookData::getReadStates)
            ).apply(i, TropicalBookData::new));
    public static final StreamCodec<ByteBuf, Map<String, String>> MAP_STREAM_CODEC = ByteBufCodecs.map(
        HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            ByteBufCodecs.STRING_UTF8
    );
    //public static final Codec<Map<TropicraftEncyclopediaLangKeys, ReadState>> CODEC = Codec.unboundedMap(TropicraftEncyclopediaLangKeys.CODEC, ReadState.CODEC);

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Tropicraft.ID);
    public static final Supplier<AttachmentType<TropicalBookData>> ATTACHMENT = ATTACHMENT_TYPES.register(
        "tropical_book_data", () -> AttachmentType.builder(TropicalBookData::new).serialize(CODEC).build()
    );
    //Possibly move to only writing the page names of those that have a state other than hidden
    public List<String> getPageNames() {
        return Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title).toList();
         //return pageNames;
    }
    public List<String> getReadStates() {
        return readStates;
    }

    void updateClient(ServerPlayer target, boolean force) {
        if (dirty || force) {
            PacketDistributor.sendToPlayer(target, new ClientboundUpdateEncyclopediaPacket(createMap()));
        }
    }
    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        Iterator<String> pageIterator = pageNames.iterator();
        Iterator<String> visibilityIterator = readStates.iterator();
        //both will have the same size, just redundant implmentation until it can be improved
        while(pageIterator.hasNext() && visibilityIterator.hasNext()) {
            map.put(pageIterator.next(), visibilityIterator.next());
        }
        return map;
    }
    /*public void copyFrom(TropicalBookData data) {
        pageNames = data.getPageNames();
        readStates = data.getReadStates();
    }*/
    public void copyFrom(Map<String, String> map) {
        pageNames = map.keySet().stream().toList();
        readStates = map.values().stream().toList();
    }
        public TropicalBookData() {
            pageNames = Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title).toList();
        }
        public TropicalBookData(List<String> pageNames, List<String> readStates) {

            this.pageNames = pageNames;
            //this.pageNames = Arrays.stream(TropicraftEncyclopediaLangKeys.values()).map(value -> value.title).toList();
            this.readStates = readStates;
            //this.readStates = new ArrayList<ReadState>(pageNames.size());
        }

        //note for future: When implementing the book writing itself on item pickup, do it with packets. When an item is picked up, wait a little bit(some ticks)
        //before sending the update packet in case it turns out you write another one and maybe have to recreate the whole savedData map or something. idk just a thought

}
