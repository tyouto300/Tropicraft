package net.tropicraft.core.common.encyclopedia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.tropicraft.core.client.encyclopedia.Page;
import org.apache.logging.log4j.LogManager;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TropicalBook {
    public static enum ReadState {
        HIDDEN,
        VISIBLE,
        READ,
    }
    private LinkedHashMap<String, Page> pages = new LinkedHashMap<>();
    private List<String> pageIds = new ArrayList<>();

    private Page currentBookmark;
    private Map<Page, Page> pageToBookmark = new IdentityHashMap<>();

    private HashMap<String, ReadState> visibilities = new HashMap<>();

    private String fileName;

    public String outsideTexture;
    public String insideTexture;

    public TropicalBook(String saveFile, String insideTexture, String outsideTexture) {
        fileName = saveFile;
        this.outsideTexture = outsideTexture;
        this.insideTexture = insideTexture;
    }

    public void addPage(Page page) {
        if (!pages.containsKey(page.getId())) {
            this.pages.put(page.getId(), page);
            this.pageIds.add(page.getId());
            if (page.isBookmark()) {
                currentBookmark = page;
            } else if (currentBookmark != null) {
                this.pageToBookmark.put(page, currentBookmark);
            }
        } else {
            throw new IllegalArgumentException("Duplicate page: " + page.getId());
        }
    }
    protected File getSaveFile() {
        File root = null;//DimensionManager.getCurrentSaveRootDirectory();

        if (root == null) {
            ServerData serverData = Minecraft.getInstance().getCurrentServer();
            if (serverData == null) {
                throw new IllegalStateException("Cannot load encyclopedia outside of a game!");
            }
            //Encyclopedia save data breaks if the client changes the server name
            //needs to be moved to serverside storage
            return Paths.get("encyclopedia-servers", serverData.name.replaceAll("[^A-Za-z0-9.\\- ]+", ""), fileName).toFile();
        }
        return new File(root, fileName);
    }
    public String getPageName(int i) {
        if (i >= 0 && i < pageIds.size()) {
            return pageIds.get(i);
        }
        return null;
    }
    public Page getPage(int i) {
        return getPage(pageIds.get(i));
    }
    public Page getPage(String key) {
        return pages.get(key);
    }
    public int getPageCount() { return pages.size();}
    /*@SubscribeEvent
    public void loadData(LevelEvent.Load event) {
        //if(event.getLevel().)
        try {
            File dataFile = getSaveFile();
            visibilities.clear();
            if (dataFile.canRead()) {
                try (InputStream dataInput = new FileInputStream(dataFile)) {
                    CompoundTag data = NbtIo.readCompressed(dataInput, NbtAccounter.unlimitedHeap());
                    Iterator<String> it = data.keySet().iterator();

                    while(it.hasNext()) {
                        String tagName = it.next();
                        ReadState s = ReadState.values()[data.getByte(tagName) % ReadState.values().length];
                    }
                }
            }
        }
    }

     */
    private void saveData() {
        try {
            File dataFile = getSaveFile();
            dataFile.getParentFile().mkdirs();
            dataFile.createNewFile();
            if (dataFile.canWrite()) {
                try (OutputStream dataOutput = new FileOutputStream(dataFile)) {
                    /*NBTTagCompound data = new NBTTagCompound();
                    for (String s : visibilities.keySet()) {
                        data.setByte(s, (byte) visibilities.get(s).ordinal());
                    }
                    CompressedStreamTools.writeCompressed(data, dataOutput);

                     */

                }
            }

        } catch (IllegalStateException | IOException ex) {
            LogManager.getLogger().error("Error writing encyclopedia data.", ex);
        }
    }
    public boolean isPageVisible(String entry) {
        return visibilities.containsKey(entry) && visibilities.get(entry) != ReadState.HIDDEN;
    }
    public boolean isPageVisible(int i) { return isPageVisible(getPageName(i));}
    public boolean hasPageBeenRead(String entry) {
        return visibilities.containsKey(entry) && visibilities.get(entry) != ReadState.HIDDEN;
    }
    public boolean hasPageBeenRead(int i) {
        return hasPageBeenRead(getPageName(i));
    }
    public void markPageAsNewlyVisible(String entry) {
        Page page = getPage(entry);
        visibilities.put(entry, page == null || !page.isBookmark() ? ReadState.VISIBLE : ReadState.READ);
        saveData();
    }
    public void markPageAsNewlyVisible(int i) { markPageAsNewlyVisible(getPageName(i)); }
    public void markPageAsRead(String entry) {
        visibilities.put(entry, ReadState.READ);
        saveData();
    }
    public void markPageAsRead(int i) { markPageAsRead(getPageName(i)); }
    public void hidePage(String entry) {
        visibilities.remove(entry);
        saveData();
    }
    public void discoverPages(@NonNull Level level, @NonNull Player player) {
        for(Map.Entry<String, Page> e : pages.entrySet()) {
            if (!isPageVisible(e.getKey()) && e.getValue().discover(level, player)) {
                markPageAsNewlyVisible(e.getKey());
                Page bookmark = pageToBookmark.get(e.getValue());
                if (bookmark != null) {
                    markPageAsRead(bookmark.getId());
                }
            }
        }
    }
    public void hidePage(int i) { hidePage(getPageName(i)); }
    public boolean pageExists(String name) { return pages.containsKey(name); }
}
