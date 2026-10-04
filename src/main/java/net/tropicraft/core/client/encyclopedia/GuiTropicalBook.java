package net.tropicraft.core.client.encyclopedia;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.client.data.TropicraftEncyclopediaLangKeys;
import net.tropicraft.core.common.encyclopedia.TropicalBook;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuiTropicalBook extends Screen {
    private static final Component TITLE = Component.translatable("encyclopedia_tropica.view");
    private int currentPage;
    //private List<FormattedCharSequence> cachedPageComponents;
    private int cachedPage;
    private Component pageMsg;
    private EncyclopediaButton forwardButton;
    private EncyclopediaButton backButton;
    private EncyclopediaCoverButton coverButton;
    private EncyclopediaButton bookmarkButton;

    private TropicalBook book;

    //list of index buttons for jumping to pages from toc
    List<List<EncyclopediaIndexButton>> pageButtons = new ArrayList<>();
    List<EncyclopediaBookmarkButton> bookmarks = new ArrayList<>();
    private int indexPage = -1;
    private Page selectedPage;
    private int contentPage = 0;
    private boolean showingBookmarks;
    public static Style PAGE_TEXT_STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
    //possibly changed book_left/right to point to the e_t_inside texture and take their parts from it
    private static final Identifier BOOK_LEFT_TEXTURE = Identifier.fromNamespaceAndPath(Tropicraft.ID, "textures/gui/encyclopedia/encyclopedia_tropica_background_left.png");
    private static final Identifier BOOK_RIGHT_TEXTURE = Identifier.fromNamespaceAndPath(Tropicraft.ID, "textures/gui/encyclopedia/encyclopedia_tropica_background_right.png");
    private static Identifier OUTSIDE_TEXTURE;
    private static Identifier INSIDE_TEXTURE;
    //to translate a hex color into minecraft, take hex color - 0xFFFFFF
    public static final int COLOR_READ = -12320767;
    private static final int COLOR_NEW = -13421568;
    public static final int COLOR_HIGHLIGHT = -6750207;

    private static final Component COVER_MESSAGE = Component.translatable("key.encyclopedia_tropica_button_cover");
    public GuiTropicalBook(TropicalBook tropBook, boolean playTurnSound) {
        super(TITLE);
        this.book = tropBook;
        INSIDE_TEXTURE = Identifier.fromNamespaceAndPath(Tropicraft.ID, book.insideTexture);
        OUTSIDE_TEXTURE = Identifier.fromNamespaceAndPath(Tropicraft.ID, book.outsideTexture);

    }

    protected void init() {
        addButtons();
        updateButtonVisibility();
    }
    //Right-clicking should go back a page, ie on a recipe screen right-clicking goes to list, then again goes to the  cover
    //Render Methods
    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).pos( (this.width - 200) / 2, this.menuControlsTop()).width(200).build());
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.pose().pushMatrix();
        extractBackground(graphics, mouseX, mouseY, a);
        graphics.pose().popMatrix();
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int w = this.width / 2;
        int h = this.height / 2;
        if (indexPage == -1) {
            //Draw cover
            graphics.blit(RenderPipelines.GUI_TEXTURED, OUTSIDE_TEXTURE, w - 64, h - 86,
                    0.0F, 0.0F, 128, 173, 256, 256);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LEFT_TEXTURE, w - 167, h - 117,
                    89F, 0.0F, 167, 235, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED,BOOK_RIGHT_TEXTURE, w, h - 117,
                    0.0F, 0.0F, 166, 235, 256, 256);
            if (selectedPage != null) {//Index page selected
                int baseY = h - 80;
                int y = selectedPage.getHeaderHeight();
                if (contentPage == 0) {
                    //Draw page title
                    Component pageTitle = selectedPage.getTitle();
                    //Component pageTitle = Component.translatable(selectedPage.getTitle());
                    graphics.text(this.font, pageTitle, w - 150, h - 110, COLOR_READ, false);
                    selectedPage.drawHeader(graphics, w - 150, h - 80, mouseX, mouseY, 1.0F);
                }
                //Draw page description with word wrap
                drawPageDesc(graphics, w - 150, baseY + selectedPage.getHeaderHeight() + font.lineHeight + 12, 135, COLOR_READ);

                if (contentPage == 0) {
                    int boxW = 32;
                    int boxH = 32;
                    //Draw title underline graphic
                    graphics.blit(RenderPipelines.GUI_TEXTURED, INSIDE_TEXTURE, w - 159, h - 115,
                            145F, 201F, 113, 32, 256, 256);
                    //Draw icon outline box graphic
                    graphics.blit(RenderPipelines.GUI_TEXTURED, INSIDE_TEXTURE, w - 47, h -115,
                            90F, 201F, boxW, boxH, 256, 256);
                    selectedPage.drawIcon(graphics, w - (47 - (boxW / 4)), h - (115 - (boxH / 4)), 0.0F);

                }
            } else {//draw TOC graphics
                graphics.blit(RenderPipelines.GUI_TEXTURED, INSIDE_TEXTURE, w - 156, h - 102,
                        122F, 214F, 134, 8, 256, 256);
                //graphics.pose().popMatrix();
                String title = Tropicraft.ID + (showingBookmarks ? ".book.bookmarks.title" : ".book.toc.title");
                Component test = Component.literal("Table of Contents");
                graphics.text(this.font, test, w - 150, h - 110, COLOR_READ, false);

                //graphics.pose().pushMatrix();
            }
        }
    }
    private void drawPageDesc(GuiGraphicsExtractor graphics, int x, int y, int width, int color) {
        FormattedText pageDesc = ComponentUtils.mergeStyles(selectedPage.getDescription(), PAGE_TEXT_STYLE);
        graphics.textWithWordWrap(this.font, pageDesc, x, y, width, color);
    }
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            ActiveTextCollector.ClickableStyleFinder finder = new ActiveTextCollector.ClickableStyleFinder(this.font, (int) event.x(), (int) event.y());
            Style clickedStyle = finder.result();
            if (clickedStyle != null && this.handleClickEvent(clickedStyle.getClickEvent())) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
    protected void addButtons() {
        boolean left = true;
        int xAdjusted = width / 2;
        int yAdjusted = height / 2;
        int y = 0;
        int page = 0;
        int bookmarkY = 0;
        backButton = (EncyclopediaButton) this.addRenderableWidget(
                new EncyclopediaButton(xAdjusted - 164, yAdjusted - 20, 11, 22,Component.translatable("encyclopedia.message.button.page_back"),
                        (button) -> pageBack(), 1, INSIDE_TEXTURE)
        );
        forwardButton = (EncyclopediaButton) this.addRenderableWidget(
                new EncyclopediaButton(xAdjusted + 152, yAdjusted - 20, 11, 22,Component.translatable("encyclopedia.message.button.page_forward"),
                        (button) -> pageForward(), 2, INSIDE_TEXTURE)
        );
        coverButton = (EncyclopediaCoverButton) this.addRenderableWidget(
                new EncyclopediaCoverButton(0, 0, width, height,Component.translatable("encyclopedia.message.button.cover"),
                        (button) -> openCover(), 0)
        );
        bookmarkButton = (EncyclopediaButton) this.addRenderableWidget(
                new EncyclopediaButton(xAdjusted + 130, yAdjusted - 119, 11, 17,Component.translatable("encyclopedia.message.button.page_forward"),
                        (button) -> openBookmarks(), 4, INSIDE_TEXTURE)
        );
        /*forwardButton = (EncyclopediaPageButton) this.addRenderableWidget(
                new EncyclopediaPageButton(width / 2 + 152, height / 2 - 20, true, (button) -> this.pageForward()));//, this.playTurnSound));
        backButton = (EncyclopediaPageButton) this.addRenderableWidget(
                new EncyclopediaPageButton(width/ 2 - 164, height / 2 - 20, false, (button) -> this.pageBack()));//, this.playTurnSound));
        coverButton = (EncyclopediaButton) this.addRenderableWidget(
                new EncyclopediaButton(0, 0, width, height, COVER_MESSAGE, (button) -> this.openCover()));//, this.playTurnSound)*/
        //bookmarkButton = (EncyclopediaButton) this.addRenderableWidget(
        //        new EncyclopediaButton(width / 2 + 130, height / 2 - 119, 11, 17, )
        //)
        for(int entry = 0; entry < book.getPageCount(); entry++) {
            if (book.isPageVisible(entry)) {
                if (y > (book.getPage(entry).isBookmark() ? 155 : 175)) {
                    if (!left) {
                        page++;
                    }
                    left = !left;
                    y = left ? 0 : -20;
                }
                addIndexButton(entry, page, width / 2 + (left ? -150 : 14), height / 2 - 84 + y);

                //LogUtils.getLogger().error("Is page bookmark?: "+ book.getPage(entry).isBookmark());
                if (book.getPage(entry).isBookmark()) {
                    addBookmarkButton(entry, page, bookmarkY);
                    bookmarkY += 10;
                }
                y += 20;
            }
        }


        //this.updateButtonVisibility();
    }

    private void addIndexButton(int entry, int page, int x, int y) {
        String indexTitle = TropicraftEncyclopediaLangKeys.getUnknown().getTitle().getString();
        int color = COLOR_READ;
        if (book.isPageVisible(entry)) {
            indexTitle = book.getPage(entry).getTitle().getString();
            if (!book.hasPageBeenRead(entry)) {
                color = COLOR_NEW;
            }
        }
        int titleW = font.width(indexTitle);
        int maxW = 116;
        if(titleW > maxW) {
            //TODO:see if creating displayTitle is really necessary
            //indexTitle += "...";
            String displayTitle = indexTitle + "...";
            while(titleW > maxW) {
                //indexTitle = indexTitle.substring(0, indexTitle.length() - 4) + "...";
                displayTitle = displayTitle.substring(0, displayTitle.length() - 4) + "...";
                titleW = font.width(displayTitle);
                //titleW = font.width(indexTitle);
            }
            indexTitle = displayTitle;
        }

        Component indexMsg = Component.translatable("indexmsgtest");
        EncyclopediaIndexButton btn = (EncyclopediaIndexButton) this.addRenderableWidget(
                new EncyclopediaIndexButton(book.getPage(entry).isBookmark() ? x : x + 20, y, 116, 15, indexMsg, (button) -> setCurrentPage(entry), -1, color, indexTitle, book.getPage(entry), INSIDE_TEXTURE)
        );
        if (pageButtons.size() == page) {
            pageButtons.add(new ArrayList<>());
        }
        pageButtons.get(page).add(btn);
    }
    private void addBookmarkButton(int entry, int page, int y) {
        //LogUtils.getLogger().error("Making bookmark button");
        Component msg = Component.translatable("bookmarkbutton");
        EncyclopediaBookmarkButton btn = (EncyclopediaBookmarkButton) this.addRenderableWidget(
                new EncyclopediaBookmarkButton(book.getPage(entry), page, (width / 2 ) - 150, (height / 2) - 85 + y, 116, 10, msg, (button) -> jumpToSectionPage(entry))
        );
        bookmarks.add(btn);
    }
    private void jumpToSectionPage(int i) {
        ;
    }
    //End of render methods
    //protected boolean forcePage(int page) { return this.setPage(page);}
    /*public Component getNarrationMessage() {
        return CommonComponents.joinLines(new Component[]{super.getNarrationMessage(), this.getPageNumberMessage(), book.getPage(this.currentPage)});
    }
    s
     */

    private void setCurrentPage(int entry) {
        setCurrentPage(book.getPage(entry));
    }
    private void setCurrentPage(Page page) {
        if (page.hasContent()) {
            selectedPage = page;
            updateButtonVisibility();
        }
    }
    private void updateButtonVisibility() {
        //TODO: Remove the indexPage check as once the book is fully setup it wont matter
        forwardButton.visible = currentPage < book.getPageCount() - 1 && indexPage != -1;
        backButton.visible = currentPage > 0 && indexPage != -1;
        coverButton.visible = indexPage == -1;
        //LogUtils.getLogger().error("Showing bookmarks?: " + showingBookmarks);
        for (int i = 0; i < pageButtons.size(); i++) {
            for (EncyclopediaIndexButton btn : pageButtons.get(i)) {
                btn.visible = !showingBookmarks && selectedPage == null && i == indexPage;
            }
        }
        bookmarkButton.visible = !showingBookmarks && indexPage >= 0 && selectedPage == null;
        for(EncyclopediaBookmarkButton btn : bookmarks) {
            btn.visible = showingBookmarks;
            //LogUtils.getLogger().error("Bookmark btn visible?: " + btn.visible);
        }
        //recipeCycle = 0;
    }
    private Component getPageNumberMessage() {
        return Component.translatable("encyclopedia_tropica.pageIndicator", new Object[]{this.currentPage + 1, Math.max(book.getPageCount(), 1)}).withStyle(PAGE_TEXT_STYLE);
        //Page ${this.currentPage + 1} of ${Math.max(this.getNumPages(), 1)}
    }

    protected void pageBack() {
        if (currentPage > 0) {
            --currentPage;
        }
        showingBookmarks = false;
        updateButtonVisibility();
    }
    protected void pageForward() {
        if(currentPage < book.getPageCount() - 1) {
            ++currentPage;
        }
        showingBookmarks = false;
        updateButtonVisibility();
    }
    private void openCover() {
        if (indexPage == -1) {
            indexPage = 0;
            contentPage = 0;
        }
        showingBookmarks = false;
        updateButtonVisibility();
    }
    private void openBookmarks() {
        showingBookmarks = true;
        contentPage = 0;
        selectedPage = null;
        updateButtonVisibility();
    }
    @Override
    public void onClose() {
        super.onClose();
    }
    @Override
    public void removed() {
        super.removed();
    }
    @Override
    public boolean keyPressed(@Nullable KeyEvent event) {
        int code = event.key();
        if (Minecraft.getInstance().options.keyInventory.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }




    private int backgroundLeft() {
        return (this.width - 192) / 2;
    }
    private int backgroundTop() {
        return 2;
    }
    protected int menuControlsTop() {
        return this.backgroundTop() + 192 + 2;
    }


    //change this maybe so if the mouse is clicked on the cover then it automatically does it without need of button
    protected boolean handleClickEvent(@Nullable ClickEvent event) {
        if (event == null) {
            return false;
        } else {
            /*if (indexPage == -1) {
                indexPage = 0;
                contentPage = 0;
                TestingMod.LOGGER.info("Cover pressed");
            }

             */
            LocalPlayer player = (LocalPlayer) Objects.requireNonNull(this.minecraft.player, "Player not available");
            switch (event) {
                case ClickEvent.ChangePage(int page) when true:
                    //this.forcePage(page - 1);
                    return true;
                case ClickEvent.RunCommand(String command):
                    this.closeContainerOnServer();
                    clickCommandAction(player, command, (Screen) null);
                    return true;
                default:
                    defaultHandleGameClickEvent(event, this.minecraft, this);
                    return true;
            }
        }
    }
    protected void closeContainerOnServer() {

    }
    public boolean isInGameUI() { return true;}

}

