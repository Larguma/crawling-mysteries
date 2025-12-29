package dev.larguma.crawlingmysteries.client.screen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;

import dev.larguma.crawlingmysteries.ConfigClient;
import dev.larguma.crawlingmysteries.CrawlingMysteries;
import dev.larguma.crawlingmysteries.client.event.KeyMappingsEvents;
import dev.larguma.crawlingmysteries.client.particle.BackgroundParticle;
import dev.larguma.crawlingmysteries.client.particle.FloatingRuneParticle;
import dev.larguma.crawlingmysteries.client.particle.OrbitingStarParticle;
import dev.larguma.crawlingmysteries.client.screen.render.PanelBorderRenderer;
import dev.larguma.crawlingmysteries.codex.CodexCategory;
import dev.larguma.crawlingmysteries.codex.CodexEntry;
import dev.larguma.crawlingmysteries.codex.CodexPage;
import dev.larguma.crawlingmysteries.codex.CodexRegistry;
import dev.larguma.crawlingmysteries.codex.CodexUnlockManager;
import dev.larguma.crawlingmysteries.data.ModDataComponents;
import dev.larguma.crawlingmysteries.networking.packet.RequestStatsPacket;
import dev.larguma.crawlingmysteries.recipe.SmithingAwakeningRecipe;
import dev.larguma.crawlingmysteries.spell.ModSpells;
import dev.larguma.crawlingmysteries.spell.Spell;
import dev.larguma.crawlingmysteries.spell.SpellCooldownManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * An in-game wiki accessed through the Cryptic Eye.
 */
public class CrypticCodexScreen extends Screen {

  // #region Variables

  // Layout
  private static final int SIDEBAR_WIDTH = 140;
  private static final int CONTENT_PADDING = 16;
  private static final int CATEGORY_BUTTON_HEIGHT = 24;
  private static final int ENTRY_BUTTON_HEIGHT = 20;
  private static final int SCROLL_BUTTON_SIZE = 10;

  // Random magic numbers
  private static final int LEFT_PANEL_SCROLL_OFFSET_TOP = -6;
  private static final int LEFT_PANEL_SCROLL_OFFSET_BOT = 4;

  // Colors
  private static final int PRIMARY_COLOR = 0x6B33D7;
  private static final int SECONDARY_COLOR = 0x7D90FD;
  private static final int PANEL_COLOR = 0xDD0D0D1A;
  private static final int TEXT_COLOR = 0xE0E0E0;
  private static final int TEXT_MUTED = 0x888888;

  // State
  private boolean animate;
  private float animationTick = 0;
  private CodexCategory selectedCategory = null;
  private CodexEntry selectedEntry = null;
  private int currentPage = 0;
  private int entryScrollOffset = 0;
  private int contentScrollOffset = 0;
  private int maxContentScroll = 0;
  private int categoryScrollOffset = 0;
  private int cachedMaxCategoryScroll = 0;
  private static final int MAX_VISIBLE_CATEGORIES = 4;

  // Cached layout values for mouse interactions
  private int cachedEntryListStartY = 0;
  private int cachedVisibleEntries = 0;
  private int cachedMaxEntryScroll = 0;
  private int cachedScrollUpY = 0;
  private int cachedScrollDownY = 0;

  // Particles
  private List<BackgroundParticle> particles = new ArrayList<>();
  private List<FloatingRuneParticle> floatingRunes = new ArrayList<>();

  // Cached entry list for current category
  private List<CodexEntry> currentEntries = new ArrayList<>();

  // Entity display state
  private float entityRotationY = -30f;
  private float entityRotationX = 0f;
  private boolean isDraggingEntity = false;
  private double lastMouseX = 0;
  private double lastMouseY = 0;
  private Entity cachedEntity = null;
  private String cachedEntityId = null;

  // Entity display bounds for mouse interaction
  private int entityDisplayX = 0;
  private int entityDisplayY = 0;
  private int entityDisplayWidth = 0;
  private int entityDisplayHeight = 0;

  // #endregion Variables

  // #region Constructor and Initialization

  public CrypticCodexScreen() {
    super(Component.translatable("screen.crawlingmysteries.codex"));
  }

  @Override
  protected void init() {
    super.init();

    animate = ConfigClient.CLIENT.renderScreenAnimations.get();

    PacketDistributor.sendToServer(new RequestStatsPacket());

    particles = BackgroundParticle.init(this.width, this.height);
    floatingRunes = FloatingRuneParticle.init(this.width, this.height);

    if (selectedCategory == null && !CodexRegistry.getCategories().isEmpty()) {
      selectedCategory = CodexRegistry.getCategories().get(0);
    }

    refreshEntries();

    if (!currentEntries.isEmpty() && selectedEntry == null) {
      selectedEntry = currentEntries.get(0);
    }
  }

  @Override
  public void tick() {
    super.tick();
    if (animate) {
      BackgroundParticle.update(particles, this.width, this.height);
      FloatingRuneParticle.update(floatingRunes, this.width, this.height, this.animationTick);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.animationTick += this.minecraft.getTimer().getRealtimeDeltaTicks();

    renderTransparentBackground(guiGraphics);
    BackgroundParticle.render(guiGraphics, particles, partialTick);
    FloatingRuneParticle.render(guiGraphics, floatingRunes, partialTick);

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    // Main layout
    int sidebarX = 20;
    int sidebarY = 40;
    int contentX = sidebarX + SIDEBAR_WIDTH + 20;
    int contentWidth = this.width - contentX - 20;
    int contentHeight = this.height - 60;

    // Render components
    renderHeader(guiGraphics, mouseX, mouseY);
    renderSidebar(guiGraphics, sidebarX, sidebarY, mouseX, mouseY);
    renderContent(guiGraphics, contentX, sidebarY, contentWidth, contentHeight, mouseX, mouseY);
  }

  // #endregion Constructor and Initialization

  // #region Render Basic Layout Components

  /**
   * Renders the header section with title and decorative elements.
   */
  private void renderHeader(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    // Title
    Component title = Component.translatable("screen.crawlingmysteries.codex.title");
    int titleWidth = this.font.width(title);
    guiGraphics.drawString(this.font, title, (this.width - titleWidth) / 2, 12, PRIMARY_COLOR, true);

    // Decorative line
    int lineY = 28;
    int lineWidth = 200;
    int lineX = (this.width - lineWidth) / 2;
    float pulse = 1.0f;
    if (animate) {
      pulse = 0.5f + 0.5f * (float) Math.sin(this.animationTick * 0.05f);
    }
    int alpha = (int) (pulse * 128) + 64;
    guiGraphics.fill(lineX, lineY, lineX + lineWidth, lineY + 1, (alpha << 24) | (PRIMARY_COLOR & 0xFFFFFF));
  }

  /**
   * Renders the sidebar with category buttons and entry list.
   */
  private void renderSidebar(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
    // Panel background
    guiGraphics.fill(x - 4, y - 4, x + SIDEBAR_WIDTH + 4, this.height - 16, PANEL_COLOR);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, x - 4, y - 4, SIDEBAR_WIDTH + 8, this.height - y - 12,
        PRIMARY_COLOR, 6);

    // Category buttons
    int buttonY = y;
    List<CodexCategory> categories = CodexRegistry.getCategories();
    cachedMaxCategoryScroll = Math.max(0, categories.size() - MAX_VISIBLE_CATEGORIES);
    categoryScrollOffset = Mth.clamp(categoryScrollOffset, 0, cachedMaxCategoryScroll);

    int visibleCategories = Math.min(categories.size(), MAX_VISIBLE_CATEGORIES);

    // Category Scroll Up
    if (categoryScrollOffset > 0) {
      boolean upHovered = isMouseOver(mouseX, mouseY, x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4,
          y + LEFT_PANEL_SCROLL_OFFSET_TOP, SCROLL_BUTTON_SIZE, SCROLL_BUTTON_SIZE);
      int upColor = upHovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "▲", x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE, y + LEFT_PANEL_SCROLL_OFFSET_TOP,
          upColor, false);
    }

    for (int i = categoryScrollOffset; i < categoryScrollOffset + visibleCategories; i++) {
      if (i >= categories.size())
        break;
      CodexCategory category = categories.get(i);
      boolean isSelected = category == selectedCategory;
      boolean isHovered = isMouseOver(mouseX, mouseY, x, buttonY, SIDEBAR_WIDTH, CATEGORY_BUTTON_HEIGHT);

      renderCategoryButton(guiGraphics, x, buttonY, category, isSelected, isHovered);
      buttonY += CATEGORY_BUTTON_HEIGHT + 4;
    }

    // Category Scroll Down
    if (categoryScrollOffset < cachedMaxCategoryScroll) {
      boolean downHovered = isMouseOver(mouseX, mouseY, x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4,
          buttonY + LEFT_PANEL_SCROLL_OFFSET_BOT - 4, SCROLL_BUTTON_SIZE, SCROLL_BUTTON_SIZE);
      int downColor = downHovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "▼", x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE,
          buttonY + LEFT_PANEL_SCROLL_OFFSET_BOT - 4, downColor, false);
    }

    // Separator
    buttonY += 8;
    guiGraphics.fill(x + 10, buttonY, x + SIDEBAR_WIDTH - 10, buttonY + 1, 0x44FFFFFF);
    buttonY += 12;

    // Entry list
    Component entriesLabel = Component.translatable("screen.crawlingmysteries.codex.entries");
    guiGraphics.drawString(this.font, entriesLabel, x + 4, buttonY, TEXT_MUTED, false);
    buttonY += 14;

    // Cache layout values for mouse interactions
    cachedEntryListStartY = buttonY;
    cachedVisibleEntries = (this.height - buttonY - 30) / (ENTRY_BUTTON_HEIGHT + 2);
    cachedMaxEntryScroll = Math.max(0, currentEntries.size() - cachedVisibleEntries);
    entryScrollOffset = Mth.clamp(entryScrollOffset, 0, cachedMaxEntryScroll);

    for (int i = entryScrollOffset; i < Math.min(currentEntries.size(),
        entryScrollOffset + cachedVisibleEntries); i++) {
      CodexEntry entry = currentEntries.get(i);
      boolean isSelected = entry == selectedEntry;
      boolean isHovered = isMouseOver(mouseX, mouseY, x, buttonY, SIDEBAR_WIDTH, ENTRY_BUTTON_HEIGHT);

      renderEntryButton(guiGraphics, x, buttonY, entry, isSelected, isHovered);
      buttonY += ENTRY_BUTTON_HEIGHT + 2;
    }

    // Scroll buttons
    cachedScrollUpY = cachedEntryListStartY - 2;
    cachedScrollDownY = this.height - 36;

    if (entryScrollOffset > 0) {
      boolean upHovered = isMouseOver(mouseX, mouseY, x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4,
          cachedScrollUpY + LEFT_PANEL_SCROLL_OFFSET_TOP, SCROLL_BUTTON_SIZE, SCROLL_BUTTON_SIZE);
      int upColor = upHovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "▲", x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE,
          cachedScrollUpY + LEFT_PANEL_SCROLL_OFFSET_TOP, upColor, false);
    }
    if (entryScrollOffset < cachedMaxEntryScroll) {
      boolean downHovered = isMouseOver(mouseX, mouseY, x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4,
          cachedScrollDownY + LEFT_PANEL_SCROLL_OFFSET_BOT, SCROLL_BUTTON_SIZE, SCROLL_BUTTON_SIZE);
      int downColor = downHovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "▼", x + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE,
          cachedScrollDownY + LEFT_PANEL_SCROLL_OFFSET_BOT, downColor, false);
    }
  }

  /**
   * Renders a category button in the sidebar.
   */
  private void renderCategoryButton(GuiGraphics guiGraphics, int x, int y, CodexCategory category,
      boolean isSelected, boolean isHovered) {
    int bgColor = isSelected ? (0xAA000000 | (category.getColor() & 0xFFFFFF))
        : (isHovered ? 0x44FFFFFF : 0x22FFFFFF);

    guiGraphics.fill(x, y, x + SIDEBAR_WIDTH, y + CATEGORY_BUTTON_HEIGHT, bgColor);

    if (isSelected) {
      guiGraphics.fill(x, y, x + 3, y + CATEGORY_BUTTON_HEIGHT, 0xFF000000 | category.getColor());
    }

    // Category icon
    int iconSize = 16;
    int iconX = x + 8;
    int iconY = y + (CATEGORY_BUTTON_HEIGHT - iconSize) / 2;
    int iconPadding = 0;
    ResourceLocation iconLoc = category.getIcon();

    if (this.minecraft.getResourceManager().getResource(iconLoc).isPresent()) {
      guiGraphics.blit(iconLoc, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
      iconPadding = 20;
    }

    // Text
    int textColor = isSelected ? 0xFFFFFF : (isHovered ? 0xDDDDDD : TEXT_COLOR);
    int titleX = x + 8 + iconPadding;
    guiGraphics.drawString(this.font, category.getName(), titleX, y + (CATEGORY_BUTTON_HEIGHT - 8) / 2,
        textColor, false);
  }

  /**
   * Renders an entry button in the sidebar.
   */
  private void renderEntryButton(GuiGraphics guiGraphics, int x, int y, CodexEntry entry,
      boolean isSelected, boolean isHovered) {
    int bgColor = isSelected ? 0x44000000 | (PRIMARY_COLOR & 0xFFFFFF)
        : (isHovered ? 0x33FFFFFF : 0x11FFFFFF);

    guiGraphics.fill(x + 4, y, x + SIDEBAR_WIDTH - 4, y + ENTRY_BUTTON_HEIGHT, bgColor);

    if (isSelected) {
      guiGraphics.fill(x + 4, y, x + 6, y + ENTRY_BUTTON_HEIGHT, 0xFF000000 | SECONDARY_COLOR);
    }

    // Entry icon
    int iconSize = 0;
    int iconX = x + 10;
    int iconPadding = 0;
    ResourceLocation iconLoc = entry.icon();
    if (this.minecraft.getResourceManager().getResource(iconLoc).isPresent()) {
      iconSize = 16;
      int iconY = y + (ENTRY_BUTTON_HEIGHT - iconSize) / 2;
      guiGraphics.blit(iconLoc, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
      iconPadding = 36;
    }

    // Truncate title if needed
    String title = entry.title().getString();
    int maxTitleWidth = SIDEBAR_WIDTH - iconPadding;
    if (this.font.width(title) > maxTitleWidth) {
      title = this.font.plainSubstrByWidth(title, maxTitleWidth - 8) + "...";
    }

    int textColor = isSelected ? 0xFFFFFF : (isHovered ? 0xDDDDDD : TEXT_COLOR);
    guiGraphics.drawString(this.font, title, x + 12 + (int) (iconSize * 1.2), y + (ENTRY_BUTTON_HEIGHT - 8) / 2,
        textColor, false);
  }

  /**
   * Renders the main content area with the selected entry and page.
   */
  private void renderContent(GuiGraphics guiGraphics, int x, int y, int width, int height,
      int mouseX, int mouseY) {
    // Panel background
    guiGraphics.fill(x - 4, y - 4, x + width + 4, y + height + 4, PANEL_COLOR);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, x - 4, y - 4, width + 8, height + 8, PRIMARY_COLOR, 6);

    if (selectedEntry == null) {
      // No entry selected
      Component noSelection = Component.translatable("screen.crawlingmysteries.codex.no_selection");
      int textWidth = this.font.width(noSelection);
      guiGraphics.drawString(this.font, noSelection, x + (width - textWidth) / 2, y + height / 2, TEXT_MUTED, false);
      return;
    }

    // Entry icon in header
    int headerIconSize = 0;
    int headerIconX = x + CONTENT_PADDING;
    int headerIconY = y + CONTENT_PADDING - 2;
    ResourceLocation iconLoc = selectedEntry.icon();
    if (this.minecraft.getResourceManager().getResource(iconLoc).isPresent()) {
      headerIconSize = 24;
      guiGraphics.blit(iconLoc, headerIconX, headerIconY, 0, 0, headerIconSize, headerIconSize, headerIconSize,
          headerIconSize);
    }

    // Entry title
    // Truncate
    String title = selectedEntry.title().getString();
    if (title.isEmpty()) {
      title = "Untitled";
    }
    int maxTitleWidth = width - CONTENT_PADDING * 2 - (int) (headerIconSize * 1.2);
    if (this.font.width(title) > maxTitleWidth) {
      title = this.font.plainSubstrByWidth(title, maxTitleWidth - 6) + "...";
    }

    int titleX = x + CONTENT_PADDING + (int) (headerIconSize * 1.2);
    guiGraphics.drawString(this.font, title, titleX, y + CONTENT_PADDING,
        0xFFFFFF, true);

    // Subtitle
    String subtitle = selectedEntry.subtitle().getString();
    if (!subtitle.isEmpty()) {
      // Truncate
      if (this.font.width(subtitle) > maxTitleWidth) {
        subtitle = this.font.plainSubstrByWidth(subtitle, maxTitleWidth - 6) + "...";
      }
      guiGraphics.drawString(this.font, subtitle, titleX, y + CONTENT_PADDING + 14,
          TEXT_MUTED, false);
    }

    // Separator
    int sepY = y + CONTENT_PADDING + 30;
    guiGraphics.fill(x + CONTENT_PADDING, sepY, x + width - CONTENT_PADDING, sepY + 1, 0x44FFFFFF);

    // Page content
    int contentY = sepY + 12;
    int contentMaxHeight = height - 96;
    renderPageContent(guiGraphics, x + CONTENT_PADDING, contentY, width - CONTENT_PADDING * 2, contentMaxHeight);

    // Page navigation
    if (selectedEntry.pages().size() > 1) {
      renderPageNavigation(guiGraphics, x, y + height - 30, width, mouseX, mouseY);
    }
  }

  /**
   * Renders the content of the current page with scrolling support.
   */
  private void renderPageContent(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight) {
    if (selectedEntry == null || selectedEntry.pages().isEmpty()) {
      maxContentScroll = 0;
      return;
    }

    currentPage = Mth.clamp(currentPage, 0, selectedEntry.pages().size() - 1);
    CodexPage page = selectedEntry.pages().get(currentPage);

    switch (page.type()) {
      case TEXT -> {
        renderTextPage(guiGraphics, x, y, width, maxHeight, page);
      }
      case ITEM_SHOWCASE -> {
        renderItemShowcasePage(guiGraphics, x, y, width, maxHeight, page);
      }
      case SPELL_INFO -> {
        renderSpellInfoPage(guiGraphics, x, y, width, maxHeight, page);
      }
      case IMAGE -> {
        renderImagePage(guiGraphics, x, y, width, maxHeight, page);
      }
      case CRAFTING -> {
        renderCraftingPage(guiGraphics, x, y, width, maxHeight, page);
      }
      case ENTITY_DISPLAY -> {
        renderEntityDisplayPage(guiGraphics, x, y, width, maxHeight, page);
      }
      default -> {
        maxContentScroll = 0;
      }
    }
  }

  // #endregion Render Basic Layout Components

  // #region Text Page Rendering

  /**
   * Renders a text page with wrapping and scrolling.
   */
  public void renderTextPage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page) {
    int contentHeight = 10;
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += lines.size() * 12;
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    renderFormattedText(guiGraphics, page.content(), x, y - contentScrollOffset, width);

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  // #endregion Text Page Rendering

  // #region Item Showcase Page Rendering

  /**
   * Renders an item showcase page with a large item icon and description.
   */
  private void renderItemShowcasePage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page) {
    String itemId = page.extraData();
    if (itemId == null || itemId.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cNo item specified", x, y, TEXT_MUTED, false);
      return;
    }

    // Parse item ID (format: "modid:item_name" or just "item_name")
    ResourceLocation itemLocation;
    if (itemId.contains(":")) {
      itemLocation = ResourceLocation.parse(itemId);
    } else {
      itemLocation = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, itemId);
    }

    Optional<Item> itemOpt = BuiltInRegistries.ITEM.getOptional(itemLocation);
    if (itemOpt.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cItem not found: " + itemId, x, y, TEXT_MUTED, false);
      return;
    }

    ItemStack itemStack = new ItemStack(itemOpt.get());
    int centerX = x + width / 2;

    // Calculate total content height
    int itemSize = 64;
    int contentHeight = 10 + itemSize + 16 + 16; // padding + item + spacing + name
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += 16 + lines.size() * 12; // spacing + lines
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    int offsetY = y - contentScrollOffset;

    // Draw large item (scaled up)
    int itemX = centerX - itemSize / 2;
    int itemY = offsetY + 10;

    // Background panel for item
    guiGraphics.fill(itemX - 8, itemY - 8, itemX + itemSize + 8, itemY + itemSize + 8, 0x44000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, itemX - 8, itemY - 8, itemSize + 16, itemSize + 16,
        SECONDARY_COLOR, 4);

    // Render item scaled up
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(itemX, itemY, 0);
    guiGraphics.pose().scale(4.0f, 4.0f, 1.0f);
    guiGraphics.renderItem(itemStack, 0, 0);
    guiGraphics.pose().popPose();

    Component itemName = itemStack.getHoverName();
    int nameWidth = this.font.width(itemName);
    guiGraphics.drawString(this.font, itemName, centerX - nameWidth / 2, itemY + itemSize + 16, 0xFFFFFF, true);

    int descY = itemY + itemSize + 32;
    if (!page.content().getString().isEmpty()) {
      descY = renderFormattedText(guiGraphics, page.content(), x, descY, width);
    }

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  // #endregion Item Showcase Page Rendering

  // #region Spell Info Page Rendering

  /**
   * Renders a spell info page with spell icon, stats, and description.
   */
  private void renderSpellInfoPage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page) {
    String spellId = page.extraData();
    if (spellId == null || spellId.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cNo spell specified", x, y, TEXT_MUTED, false);
      return;
    }

    Optional<Spell> spellOpt = ModSpells.getSpell(spellId);
    if (spellOpt.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cSpell not found: " + spellId, x, y, TEXT_MUTED, false);
      return;
    }

    Spell spell = spellOpt.get();
    int centerX = x + width / 2;

    // Calculate total content height
    int iconSize = 48;
    int contentHeight = 10 + iconSize + 12 + 14; // padding + icon + name spacing + name
    contentHeight += 14; // cooldown line
    contentHeight += 20; // source line
    contentHeight += 12; // separator

    // Description lines
    Component description = spell.description();
    if (!description.getString().isEmpty()) {
      List<FormattedCharSequence> descLines = this.font.split(description, width);
      contentHeight += descLines.size() * 12;
    }

    // Additional content lines
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> contentLines = this.font.split(page.content(), width);
      contentHeight += 8 + contentLines.size() * 12;
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    // Spell icon with border and particles
    int iconX = centerX - iconSize / 2;
    int offsetY = y - contentScrollOffset;
    int iconY = offsetY + 10;
    int primaryColor = spell.getPrimaryColor();

    guiGraphics.fill(iconX - 4, iconY - 4, iconX + iconSize + 4, iconY + iconSize + 4, 0xDD0D0D1A);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, iconX - 4, iconY - 4, iconSize + 8, iconSize + 8, primaryColor,
        4);

    ResourceLocation iconLocation = ResourceLocation.fromNamespaceAndPath(
        CrawlingMysteries.MOD_ID, "textures/" + spell.icon().getPath() + ".png");
    guiGraphics.blit(iconLocation, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
    if (animate) {
      OrbitingStarParticle.renderOrbitingStars(guiGraphics, iconX + iconSize / 2, iconY + iconSize / 2, primaryColor,
          animationTick, iconSize + 8);
    }

    Component spellName = spell.name();
    int nameWidth = this.font.width(spellName);
    guiGraphics.drawString(this.font, spellName, centerX - nameWidth / 2, iconY + iconSize + 12, 0xFFFFFF, true);

    int statsY = iconY + iconSize + 28;

    if (spell.cooldownTicks() > 0) {
      String cooldownText = String.format("§7Cooldown: §e" + SpellCooldownManager.getTotalCooldownFormatted(spell));
      int cooldownWidth = this.font.width(cooldownText);
      guiGraphics.drawString(this.font, cooldownText, centerX - cooldownWidth / 2, statsY, TEXT_COLOR, false);
      statsY += 14;
    }

    Component sourceText = Component.literal("§7Source: §b" + spell.sourceItem().getPath().replace("_", " "));
    int sourceWidth = this.font.width(sourceText);
    guiGraphics.drawString(this.font, sourceText, centerX - sourceWidth / 2, statsY, TEXT_COLOR, false);
    statsY += 20;

    // Separator
    guiGraphics.fill(x + 20, statsY, x + width - 20, statsY + 1, 0x44FFFFFF);
    statsY += 12;

    if (!description.getString().isEmpty()) {
      statsY = renderFormattedText(guiGraphics, description, x, statsY, width);
    }

    if (!page.content().getString().isEmpty()) {
      statsY += 8;
      statsY = renderFormattedText(guiGraphics, page.content(), x, statsY, width);
    }

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  // #endregion Spell Info Page Rendering

  // #region Image Page Rendering

  /**
   * Renders an image page with a texture.
   */
  private void renderImagePage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page) {
    String imagePath = page.extraData();
    if (imagePath == null || imagePath.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cNo image specified", x, y, TEXT_MUTED, false);
      return;
    }

    // Parse image path (format: "modid:path/to/image.png" or just
    // "path/to/image.png")
    ResourceLocation imageLocation;
    if (imagePath.contains(":")) {
      imageLocation = ResourceLocation.parse(imagePath);
    } else {
      imageLocation = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, imagePath);
    }

    int maxImageWidth = width - 20;
    int maxImageHeight = maxHeight - 40;

    int[] textureDimensions = getTextureDimensions(imageLocation);
    int textureWidth = textureDimensions[0];
    int textureHeight = textureDimensions[1];

    float aspectRatio = (float) textureWidth / (float) textureHeight;
    int imageWidth;
    int imageHeight;

    if (textureWidth > textureHeight) {
      // Landscape or square
      imageWidth = Math.min(maxImageWidth, textureWidth);
      imageHeight = (int) (imageWidth / aspectRatio);

      if (imageHeight > maxImageHeight) {
        imageHeight = maxImageHeight;
        imageWidth = (int) (imageHeight * aspectRatio);
      }
    } else {
      // Portrait
      imageHeight = Math.min(maxImageHeight, textureHeight);
      imageWidth = (int) (imageHeight * aspectRatio);

      if (imageWidth > maxImageWidth) {
        imageWidth = maxImageWidth;
        imageHeight = (int) (imageWidth / aspectRatio);
      }
    }

    // Calculate total content height
    int contentHeight = 10 + imageHeight + 12; // padding + image + spacing
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += lines.size() * 12;
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    int offsetY = y - contentScrollOffset;

    // Center the image
    int imageX = x + (width - imageWidth) / 2;
    int imageY = offsetY + 10;

    // Background for image
    guiGraphics.fill(imageX - 4, imageY - 4, imageX + imageWidth + 4, imageY + imageHeight + 4, 0x44000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, imageX - 4, imageY - 4, imageWidth + 8, imageHeight + 8,
        SECONDARY_COLOR, 4);

    // Render the image
    guiGraphics.blit(imageLocation, imageX, imageY, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

    // Caption text below image
    int captionY = imageY + imageHeight + 12;
    if (!page.content().getString().isEmpty()) {
      captionY = renderFormattedText(guiGraphics, page.content(), x, captionY, width, true, TEXT_MUTED);
    }

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  // #endregion Image Page Rendering

  // #region Crafting Page Rendering

  /**
   * Renders a crafting recipe page (simplified display).
   */
  private void renderCraftingPage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page) {
    String recipeId = page.extraData();
    if (recipeId == null || recipeId.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cNo recipe specified", x, y, TEXT_MUTED, false);
      return;
    }

    // Get recipe
    Minecraft mc = Minecraft.getInstance();
    if (mc.level == null)
      return;

    ResourceLocation id;
    try {
      if (recipeId.contains(":")) {
        id = ResourceLocation.parse(recipeId);
      } else {
        id = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, recipeId);
      }
    } catch (Exception e) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cInvalid recipe ID: " + recipeId, x, y, TEXT_MUTED, false);
      return;
    }

    Optional<RecipeHolder<?>> recipeHolderOpt = mc.level.getRecipeManager().byKey(id);
    if (recipeHolderOpt.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cRecipe not found: " + recipeId, x, y, TEXT_MUTED, false);
      return;
    }

    RecipeHolder<?> recipeHolder = recipeHolderOpt.get();
    Recipe<?> recipe = recipeHolder.value();

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    if (recipe instanceof SmithingRecipe smithingRecipe) {
      renderSmithing(guiGraphics, x, y, width, maxHeight, page, smithingRecipe);
    } else {
      renderCraftingGrid(guiGraphics, x, y, width, maxHeight, page, recipe);
    }

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  /**
   * Renders a standard crafting table recipe (3x3 grid).
   */
  private void renderCraftingGrid(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page,
      Recipe<?> recipe) {
    Minecraft mc = Minecraft.getInstance();
    int centerX = x + width / 2;

    // Crafting grid dimensions
    int slotSize = 24;
    int gridSize = slotSize * 3;

    // Calculate total content height
    int contentHeight = 14 + 20 + gridSize + 20; // title + spacing + grid + spacing
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += lines.size() * 12;
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    int offsetY = y - contentScrollOffset;

    Component title = Component.literal("§lCrafting Recipe");
    int titleWidth = this.font.width(title);
    guiGraphics.drawString(this.font, title, centerX - titleWidth / 2, offsetY, 0xFFFFFF, true);

    int gridX = centerX - gridSize / 2 - 30;
    int gridY = offsetY + 20;

    // Draw crafting grid background
    guiGraphics.fill(gridX - 4, gridY - 4, gridX + gridSize + 4, gridY + gridSize + 4, 0x66000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, gridX - 4, gridY - 4, gridSize + 8, gridSize + 8, TEXT_MUTED, 3);

    NonNullList<Ingredient> ingredients = recipe.getIngredients();

    // Draw grid slots and ingredients
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        int slotX = gridX + col * slotSize;
        int slotY = gridY + row * slotSize;

        // Slot background
        guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x44FFFFFF);

        Ingredient ingredient = Ingredient.EMPTY;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
          int recipeWidth = shapedRecipe.getWidth();
          int recipeHeight = shapedRecipe.getHeight();
          if (col < recipeWidth && row < recipeHeight) {
            int index = row * recipeWidth + col;
            if (index < ingredients.size()) {
              ingredient = ingredients.get(index);
            }
          }
        } else {
          // Shapeless or other
          int index = row * 3 + col;
          if (index < ingredients.size()) {
            ingredient = ingredients.get(index);
          }
        }

        if (!ingredient.isEmpty()) {
          renderIngredient(guiGraphics, ingredient, slotX + 4, slotY + 4);
        }
      }
    }

    // Arrow
    int arrowX = gridX + gridSize + 12;
    int arrowY = gridY + gridSize / 2 - 4;
    guiGraphics.drawString(this.font, "→", arrowX, arrowY, TEXT_COLOR, false);

    // Result slot
    int resultX = arrowX + 20;
    int resultY = gridY + gridSize / 2 - slotSize / 2;
    ItemStack resultStack = recipe.getResultItem(mc.level.registryAccess());

    guiGraphics.fill(resultX - 2, resultY - 2, resultX + slotSize + 2, resultY + slotSize + 2, 0x66000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, resultX - 2, resultY - 2, slotSize + 4, slotSize + 4,
        PRIMARY_COLOR, 3);
    guiGraphics.fill(resultX + 1, resultY + 1, resultX + slotSize - 1, resultY + slotSize - 1, 0x44FFFFFF);

    guiGraphics.renderItem(resultStack, resultX + 4, resultY + 4);
    guiGraphics.renderItemDecorations(this.font, resultStack, resultX + 4, resultY + 4);

    int descY = gridY + gridSize + 20;
    if (!page.content().getString().isEmpty()) {
      descY = renderFormattedText(guiGraphics, page.content(), x, descY, width);
    }
  }

  /**
   * Renders a smithing recipe page.
   */
  private void renderSmithing(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight, CodexPage page,
      SmithingRecipe recipe) {
    int centerX = x + width / 2;

    // Calculate total content height
    int slotSize = 24;
    int contentHeight = 14 + 20 + slotSize + 20; // title + spacing + slots + spacing
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += lines.size() * 12;
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    int offsetY = y - contentScrollOffset;

    Component title = Component.literal("§lSmithing Recipe");
    int titleWidth = this.font.width(title);
    guiGraphics.drawString(this.font, title, centerX - titleWidth / 2, offsetY, 0xFFFFFF, true);

    int startY = offsetY + 30;
    int gap = 8;

    // Layout: [Template] [Base] [Addition] -> [Result]
    int totalWidth = (slotSize * 3) + (gap * 2) + 20 + slotSize;
    int startX = centerX - totalWidth / 2;

    NonNullList<Ingredient> ingredients = recipe.getIngredients();

    // Slots
    for (int i = 0; i < 3; i++) {
      int slotX = startX + i * (slotSize + gap);
      Ingredient ingredient = (i < ingredients.size()) ? ingredients.get(i) : Ingredient.EMPTY;

      guiGraphics.fill(slotX - 2, startY - 2, slotX + slotSize + 2, startY + slotSize + 2, 0x66000000);
      PanelBorderRenderer.renderPanelBorder(guiGraphics, slotX - 2, startY - 2, slotSize + 4, slotSize + 4, TEXT_MUTED,
          3);
      guiGraphics.fill(slotX + 1, startY + 1, slotX + slotSize - 1, startY + slotSize - 1, 0x44FFFFFF);

      if (!ingredient.isEmpty()) {
        renderIngredient(guiGraphics, ingredient, slotX + 4, startY + 4);
      }
    }

    // Arrow
    int arrowX = startX + 3 * (slotSize + gap) + 2;
    int arrowY = startY + slotSize / 2 - 4;
    guiGraphics.drawString(this.font, "→", arrowX, arrowY, TEXT_COLOR, false);

    // Result Slot
    int resultX = arrowX + 16;
    ItemStack resultStack;

    if (recipe instanceof SmithingAwakeningRecipe awakeningRecipe) {
      ItemStack[] baseItems = awakeningRecipe.getBase().getItems();
      if (baseItems.length > 0) {
        int index = (int) (animationTick / 20) % baseItems.length;
        if (index < 0)
          index = 0;
        resultStack = baseItems[index].copy();
        resultStack.set(ModDataComponents.GOOGLY_EYES, true);
      } else {
        resultStack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
      }
    } else {
      resultStack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
    }

    guiGraphics.fill(resultX - 2, startY - 2, resultX + slotSize + 2, startY + slotSize + 2, 0x66000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, resultX - 2, startY - 2, slotSize + 4, slotSize + 4,
        PRIMARY_COLOR, 3);
    guiGraphics.fill(resultX + 1, startY + 1, resultX + slotSize - 1, startY + slotSize - 1, 0x44FFFFFF);

    guiGraphics.renderItem(resultStack, resultX + 4, startY + 4);
    guiGraphics.renderItemDecorations(this.font, resultStack, resultX + 4, startY + 4);

    int descY = startY + slotSize + 20;
    if (!page.content().getString().isEmpty()) {
      descY = renderFormattedText(guiGraphics, page.content(), x, descY, width);
    }
  }

  // #endregion Crafting Page Rendering

  // #region Entity Display Page Rendering

  /**
   * Renders an entity display page with an animated 3D entity model.
   * Supports mouse drag rotation and displays entity stats.
   */
  private void renderEntityDisplayPage(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight,
      CodexPage page) {
    String entityId = page.extraData();
    if (entityId == null || entityId.isEmpty()) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cNo entity specified", x, y, TEXT_MUTED, false);
      return;
    }

    // Parse entity ID (format: "modid:entity_name" or just "entity_name")
    ResourceLocation entityLocation;
    if (entityId.contains(":")) {
      entityLocation = ResourceLocation.parse(entityId);
    } else {
      entityLocation = ResourceLocation.fromNamespaceAndPath(CrawlingMysteries.MOD_ID, entityId);
    }

    // Get or create cached entity
    if (cachedEntity == null || !entityId.equals(cachedEntityId)) {
      Optional<EntityType<?>> entityTypeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(entityLocation);
      if (entityTypeOpt.isEmpty()) {
        maxContentScroll = 0;
        guiGraphics.drawString(this.font, "§cEntity not found: " + entityId, x, y, TEXT_MUTED, false);
        return;
      }

      Minecraft mc = Minecraft.getInstance();
      cachedEntity = entityTypeOpt.get().create(mc.level);
      cachedEntityId = entityId;
      // Reset rotation when entity changes
      entityRotationY = -30f;
      entityRotationX = 0f;
    }

    if (cachedEntity == null) {
      maxContentScroll = 0;
      guiGraphics.drawString(this.font, "§cFailed to create entity", x, y, TEXT_MUTED, false);
      return;
    }

    int centerX = x + width / 2;

    // Entity display area dimensions
    int displaySize = Math.min(width - 40, maxHeight - 80);
    displaySize = Math.min(displaySize, 180);

    // Calculate total content height
    int contentHeight = 10 + displaySize + 24; // padding + display + name spacing
    contentHeight += 16; // "Drag to rotate" hint
    if (!page.content().getString().isEmpty()) {
      List<FormattedCharSequence> lines = this.font.split(page.content(), width);
      contentHeight += 16 + lines.size() * 12; // spacing + lines
    }

    // Add entity stats for living entities
    if (cachedEntity instanceof LivingEntity) {
      contentHeight += 40; // Health and other stats
    }

    maxContentScroll = Math.max(0, contentHeight - maxHeight);
    contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);

    guiGraphics.enableScissor(x, y, x + width, y + maxHeight);

    int offsetY = y - contentScrollOffset;

    // Entity display area background
    int displayX = centerX - displaySize / 2;
    int displayY = offsetY + 10;

    // Cache display bounds for mouse interaction
    entityDisplayX = displayX - 10;
    entityDisplayY = displayY - 10;
    entityDisplayWidth = displaySize + 20;
    entityDisplayHeight = displaySize + 20;

    // Background panel for entity
    guiGraphics.fill(displayX - 10, displayY - 10, displayX + displaySize + 10, displayY + displaySize + 10,
        0x66000000);
    PanelBorderRenderer.renderPanelBorder(guiGraphics, displayX - 10, displayY - 10, displaySize + 20, displaySize + 20,
        PRIMARY_COLOR, 4);

    if (cachedEntity instanceof LivingEntity livingEntity) {
      renderLivingEntityInPanel(guiGraphics, displayX, displayY, displaySize, livingEntity);
    } else {
      guiGraphics.drawString(this.font, "§7[Entity Preview]", centerX - 40, displayY + displaySize / 2, TEXT_MUTED,
          false);
    }

    // Entity name
    Component entityName = cachedEntity.getType().getDescription();
    int nameWidth = this.font.width(entityName);
    int nameY = displayY + displaySize + 16;
    guiGraphics.drawString(this.font, entityName, centerX - nameWidth / 2, nameY, 0xFFFFFF, true);

    // Rotation hint with animated opacity
    int hintAlpha = 200;
    if (animate) {
      float hintPulse = 0.5f + 0.3f * (float) Math.sin(animationTick * 0.08f);
      hintAlpha = (int) (hintPulse * 200);
    }
    String rotateHint = "§7⟲ Drag to rotate";
    int hintWidth = this.font.width(rotateHint);
    guiGraphics.drawString(this.font, rotateHint, centerX - hintWidth / 2, nameY + 14, (hintAlpha << 24) | 0x888888,
        false);

    int statsY = nameY + 32;

    // Entity stats for living entities
    if (cachedEntity instanceof LivingEntity livingEntity) {
      // Health
      float maxHealth = livingEntity.getMaxHealth();
      String healthText = String.format("§c❤ §7Max Health: §f%.0f", maxHealth);
      int healthWidth = this.font.width(healthText);
      guiGraphics.drawString(this.font, healthText, centerX - healthWidth / 2, statsY, TEXT_COLOR, false);
      statsY += 14;

      // Armor
      float armor = livingEntity.getArmorValue();
      if (armor > 0) {
        String armorText = String.format("§9🛡 §7Armor: §f%.0f", armor);
        int armorWidth = this.font.width(armorText);
        guiGraphics.drawString(this.font, armorText, centerX - armorWidth / 2, statsY, TEXT_COLOR, false);
        statsY += 14;
      }
    }

    // Separator before description
    if (!page.content().getString().isEmpty()) {
      statsY += 8;
      guiGraphics.fill(x + 20, statsY, x + width - 20, statsY + 1, 0x44FFFFFF);
      statsY += 12;

      statsY = renderFormattedText(guiGraphics, page.content(), x, statsY, width);
    }

    guiGraphics.disableScissor();

    renderScrollIndicators(guiGraphics, x, y, width, maxHeight);
  }

  /**
   * Renders a living entity in the display panel with rotation support.
   * Uses custom rendering to avoid the internal scissoring of
   * InventoryScreen.renderEntityInInventory
   */
  private void renderLivingEntityInPanel(GuiGraphics guiGraphics, int displayX, int displayY, int displaySize,
      LivingEntity entity) {
    // Calculate entity scale based on its bounding box
    float entityHeight = entity.getBbHeight();
    float entityWidth = entity.getBbWidth();
    float maxDimension = Math.max(entityHeight, entityWidth);
    int scale = (int) ((displaySize * 0.6f) / maxDimension);
    scale = Mth.clamp(scale, 10, 80);

    // Position entity in center of display area
    int entityX = displayX + displaySize / 2;
    int entityY = displayY + displaySize - 5;

    // Create rotation quaternion from rotation state
    Quaternionf rotation = new Quaternionf();
    rotation.rotateX((float) Math.toRadians(entityRotationX + 180));
    rotation.rotateY((float) Math.toRadians(entityRotationY));

    // Store and set entity properties for rendering
    float oldYaw = entity.getYRot();
    float oldYawHead = entity.yHeadRot;
    float oldYawBody = entity.yBodyRot;
    float oldPitch = entity.getXRot();

    entity.setYRot(0);
    entity.yHeadRot = 0;
    entity.yBodyRot = 0;
    entity.setXRot(0);

    renderEntityWithoutScissor(guiGraphics, entityX, entityY, scale, rotation, entity);

    // Restore entity properties
    entity.setYRot(oldYaw);
    entity.yHeadRot = oldYawHead;
    entity.yBodyRot = oldYawBody;
    entity.setXRot(oldPitch);
  }

  /**
   * Renders an entity without using scissor, allowing full 3D rotation without
   * clipping.
   * InventoryScreen.renderEntityInInventory
   */
  private void renderEntityWithoutScissor(GuiGraphics guiGraphics, float x, float y, float scale,
      Quaternionf rotation, LivingEntity entity) {
    guiGraphics.flush();

    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(x, y, 500.0F);
    guiGraphics.pose().scale(scale, scale, -scale);
    guiGraphics.pose().mulPose(rotation);

    Lighting.setupForEntityInInventory();

    EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    dispatcher.setRenderShadow(false);

    var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
    dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, guiGraphics.pose(), bufferSource, 15728880);
    bufferSource.endBatch();

    dispatcher.setRenderShadow(true);

    guiGraphics.pose().popPose();
    Lighting.setupFor3DItems();
  }

  // #endregion Entity Display Page Rendering

  // #region Helper Methods

  /**
   * Renders text with proper support for Minecraft formatting codes.
   * Splits the text into multiple lines if needed and renders each line.
   * This method handles the issue where color codes bleed across line wraps.
   * 
   * @param yOffset  The starting Y position
   * @param centered If true, each line will be centered within the width
   * @return The final Y position after rendering all lines
   */
  private int renderFormattedText(GuiGraphics guiGraphics, Component text, int x, int yOffset, int width,
      boolean centered, int color) {
    String rawText = text.getString();
    List<String> lines = new ArrayList<>();

    String[] paragraphs = rawText.split("\n", -1);
    String carriedFormatting = "";

    for (String paragraph : paragraphs) {
      if (paragraph.isEmpty()) {
        lines.add("");
        continue;
      }

      String[] words = paragraph.split(" ");
      StringBuilder currentLine = new StringBuilder();
      currentLine.append(carriedFormatting);

      for (int i = 0; i < words.length; i++) {
        String word = words[i];
        boolean isLineStart = currentLine.length() == carriedFormatting.length();
        String testLine = isLineStart ? currentLine + word : currentLine + " " + word;

        // Remove formatting codes for width calculation
        String testLineNoFormat = testLine.replaceAll("§.", "");

        if (this.font.width(testLineNoFormat) <= width) {
          if (!isLineStart)
            currentLine.append(" ");
          currentLine.append(word);
        } else {
          if (!isLineStart) {
            lines.add(currentLine.toString());

            carriedFormatting = getActiveFormatting(currentLine.toString());
            currentLine = new StringBuilder(carriedFormatting);
            currentLine.append(word);
          } else {
            // Single word is too long, add it anyway
            currentLine.append(word);
          }
        }
      }

      if (currentLine.length() > 0) {
        lines.add(currentLine.toString());
        carriedFormatting = getActiveFormatting(currentLine.toString());
      }
    }

    for (String line : lines) {
      Component lineComponent = Component.literal(line);
      int renderX = x;

      if (centered) {
        String lineNoFormat = line.replaceAll("§.", "");
        int lineWidth = this.font.width(lineNoFormat);
        renderX = x + (width - lineWidth) / 2;
      }

      guiGraphics.drawString(this.font, lineComponent, renderX, yOffset, color, false);
      yOffset += 12;
    }

    return yOffset;
  }

  /**
   * Overloaded method for renderFormattedText centered with default color.
   */
  private int renderFormattedText(GuiGraphics guiGraphics, Component text, int x, int yOffset, int width) {
    return renderFormattedText(guiGraphics, text, x, yOffset, width, false, TEXT_COLOR);
  }

  /**
   * Extracts the active formatting codes at the end of a string.
   */
  private String getActiveFormatting(String text) {
    String formatting = "";
    for (int i = 0; i < text.length() - 1; i++) {
      if (text.charAt(i) == '§') {
        char code = text.charAt(i + 1);
        if (code == 'r') {
          formatting = "";
        } else if ("0123456789abcdef".indexOf(code) >= 0) {
          formatting = "§" + code;
        } else if ("klmno".indexOf(code) >= 0) {
          formatting += "§" + code;
        }
      }
    }
    return formatting;
  }

  /**
   * Render scroll indicators at top/bottom of content area.
   */
  private void renderScrollIndicators(GuiGraphics guiGraphics, int x, int y, int width, int maxHeight) {
    if (contentScrollOffset > 0) {
      // Fade gradient at top
      guiGraphics.fillGradient(x - 4, y - 4, x + width + 4, y + 12, 0xDD0D0D1A, 0x000D0D1A);
      guiGraphics.drawString(this.font, "▲", x + width / 2 - 4, y - 8, TEXT_MUTED, false);
    }
    if (contentScrollOffset < maxContentScroll) {
      // Fade gradient at bottom
      guiGraphics.fillGradient(x - 4, y + maxHeight - 12, x + width + 4, y + maxHeight + 4, 0x000D0D1A, 0xDD0D0D1A);
      guiGraphics.drawString(this.font, "▼", x + width / 2 - 4, y + maxHeight, TEXT_MUTED, false);
    }
  }

  /**
   * Render an ingredient in a recipe slot.
   */
  private void renderIngredient(GuiGraphics guiGraphics, Ingredient ingredient, int x, int y) {
    ItemStack[] items = ingredient.getItems();
    if (items.length == 0)
      return;

    int index = (int) (animationTick / 20) % items.length;
    if (index < 0)
      index = 0;
    ItemStack stack = items[index];
    guiGraphics.renderItem(stack, x, y);
    guiGraphics.renderItemDecorations(this.font, stack, x, y);
  }

  /**
   * Renders the page navigation controls (previous/next buttons and page number).
   */
  private void renderPageNavigation(GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY) {
    int totalPages = selectedEntry.pages().size();
    String pageText = (currentPage + 1) + " / " + totalPages;
    int textWidth = this.font.width(pageText);

    int centerX = x + width / 2;
    guiGraphics.drawString(this.font, pageText, centerX - textWidth / 2, y + 6, TEXT_COLOR, false);

    // Previous button
    if (currentPage > 0) {
      boolean hovered = isMouseOver(mouseX, mouseY, centerX - 60, y, 20, 20);
      int color = hovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "◀", centerX - 55, y + 6, color, false);
    }

    // Next button
    if (currentPage < totalPages - 1) {
      boolean hovered = isMouseOver(mouseX, mouseY, centerX + 40, y, 20, 20);
      int color = hovered ? 0xFFFFFF : TEXT_MUTED;
      guiGraphics.drawString(this.font, "▶", centerX + 45, y + 6, color, false);
    }
  }

  /**
   * Checks if the mouse is over a rectangular area.
   */
  private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
    return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
  }

  /**
   * Checks if the current page is an entity display page.
   */
  private boolean isEntityDisplayPage() {
    if (selectedEntry == null || selectedEntry.pages().isEmpty()) {
      return false;
    }
    int pageIndex = Mth.clamp(currentPage, 0, selectedEntry.pages().size() - 1);
    return selectedEntry.pages().get(pageIndex).type() == CodexPage.PageType.ENTITY_DISPLAY;
  }

  /**
   * Gets the actual dimensions of a texture from the resource pack.
   * Returns [width, height]. Falls back to [256, 256] if unable to read.
   */
  private int[] getTextureDimensions(ResourceLocation textureLocation) {
    try {
      Minecraft mc = Minecraft.getInstance();

      Optional<Resource> resourceOpt = mc.getResourceManager().getResource(textureLocation);
      if (resourceOpt.isPresent()) {
        try (InputStream stream = resourceOpt.get().open()) {
          NativeImage image = NativeImage.read(stream);
          int width = image.getWidth();
          int height = image.getHeight();
          image.close();
          return new int[] { width, height };
        }
      }
    } catch (Exception e) {
      CrawlingMysteries.LOGGER.warn("Failed to read texture dimensions for {}: {}", textureLocation, e.getMessage());
    }

    return new int[] { 256, 256 };
  }

  /**
   * Plays a click sound effect.
   */
  private void playClickSound() {
    if (this.minecraft != null) {
      this.minecraft.getSoundManager().play(
          SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.2f, 0.3f));
    }
  }

  /**
   * Clears the cached entity for entity display pages.
   * Should be called when changing entries or categories.
   */
  private void clearCachedEntity() {
    cachedEntity = null;
    cachedEntityId = null;
    entityRotationY = -30f;
    entityRotationX = 0f;
    isDraggingEntity = false;
  }

  /**
   * Refreshes the list of entries for the currently selected category.
   */
  private void refreshEntries() {
    currentEntries = CodexRegistry.getByCategory(selectedCategory).stream()
        .filter(entry -> CodexUnlockManager.isUnlocked(minecraft.player, entry))
        .toList();
    entryScrollOffset = 0;
  }

  /**
   * Static helper to refresh the codex screen if it is currently open.
   */
  public static void refreshIfOpen() {
    Minecraft mc = Minecraft.getInstance();
    if (mc.screen instanceof CrypticCodexScreen codex) {
      codex.refreshEntries();
    }
  }

  // #endregion Helper Methods

  // #region Mouse/Keyboard Handling

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (button != 0)
      return super.mouseClicked(mouseX, mouseY, button);

    // Check if clicking on entity display area for drag rotation
    if (isEntityDisplayPage() && isMouseOver((int) mouseX, (int) mouseY,
        entityDisplayX, entityDisplayY, entityDisplayWidth, entityDisplayHeight)) {
      isDraggingEntity = true;
      lastMouseX = mouseX;
      lastMouseY = mouseY;
      return true;
    }

    int sidebarX = 20;
    int sidebarY = 40;

    // Check category buttons
    int visibleCategories = Math.min(CodexRegistry.getCategories().size(), MAX_VISIBLE_CATEGORIES);
    int buttonY = sidebarY;

    // Check scroll buttons (Category)
    if (categoryScrollOffset > 0
        && isMouseOver((int) mouseX, (int) mouseY, sidebarX + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4,
            sidebarY + LEFT_PANEL_SCROLL_OFFSET_TOP, SCROLL_BUTTON_SIZE * 2, SCROLL_BUTTON_SIZE * 2)) {
      categoryScrollOffset = Math.max(0, categoryScrollOffset - 1);
      playClickSound();
      return true;
    }

    int categoryListHeight = visibleCategories * (CATEGORY_BUTTON_HEIGHT + 4);
    int downY = sidebarY + categoryListHeight + LEFT_PANEL_SCROLL_OFFSET_BOT - 4;

    if (categoryScrollOffset < cachedMaxCategoryScroll && isMouseOver((int) mouseX, (int) mouseY,
        sidebarX + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4, downY, SCROLL_BUTTON_SIZE * 2, SCROLL_BUTTON_SIZE * 2)) {
      categoryScrollOffset = Math.min(cachedMaxCategoryScroll, categoryScrollOffset + 1);
      playClickSound();
      return true;
    }

    for (int i = categoryScrollOffset; i < categoryScrollOffset + visibleCategories; i++) {
      if (i >= CodexRegistry.getCategories().size())
        break;
      CodexCategory category = CodexRegistry.getCategories().get(i);

      if (isMouseOver((int) mouseX, (int) mouseY, sidebarX, buttonY, SIDEBAR_WIDTH, CATEGORY_BUTTON_HEIGHT)) {
        if (category != selectedCategory) {
          selectedCategory = category;
          refreshEntries();
          selectedEntry = currentEntries.isEmpty() ? null : currentEntries.get(0);
          currentPage = 0;
          contentScrollOffset = 0;
          clearCachedEntity();
          playClickSound();
        }
        return true;
      }
      buttonY += CATEGORY_BUTTON_HEIGHT + 4;
    }

    // Check scroll buttons
    if (entryScrollOffset > 0 && isMouseOver((int) mouseX, (int) mouseY,
        sidebarX + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4, cachedScrollUpY + LEFT_PANEL_SCROLL_OFFSET_TOP,
        SCROLL_BUTTON_SIZE * 2,
        SCROLL_BUTTON_SIZE * 2)) {
      entryScrollOffset = Math.max(0, entryScrollOffset - 3);
      playClickSound();
      return true;
    }
    if (entryScrollOffset < cachedMaxEntryScroll && isMouseOver((int) mouseX, (int) mouseY,
        sidebarX + SIDEBAR_WIDTH - SCROLL_BUTTON_SIZE - 4, cachedScrollDownY + LEFT_PANEL_SCROLL_OFFSET_BOT,
        SCROLL_BUTTON_SIZE * 2,
        SCROLL_BUTTON_SIZE * 2)) {
      entryScrollOffset = Math.min(cachedMaxEntryScroll, entryScrollOffset + 3);
      playClickSound();
      return true;
    }

    // Check entry buttons
    buttonY = cachedEntryListStartY;

    for (int i = entryScrollOffset; i < Math.min(currentEntries.size(),
        entryScrollOffset + cachedVisibleEntries); i++) {
      if (isMouseOver((int) mouseX, (int) mouseY, sidebarX + 4, buttonY, SIDEBAR_WIDTH - 8, ENTRY_BUTTON_HEIGHT)) {
        CodexEntry entry = currentEntries.get(i);
        if (entry != selectedEntry) {
          selectedEntry = entry;
          currentPage = 0;
          contentScrollOffset = 0;
          clearCachedEntity();
          playClickSound();
        }
        return true;
      }
      buttonY += ENTRY_BUTTON_HEIGHT + 2;
    }

    // Check page navigation
    if (selectedEntry != null && selectedEntry.pages().size() > 1) {
      int contentX = sidebarX + SIDEBAR_WIDTH + 20;
      int contentWidth = this.width - contentX - 20;
      int navY = this.height - 50;
      int centerX = contentX + contentWidth / 2;

      // Previous button
      if (currentPage > 0 && isMouseOver((int) mouseX, (int) mouseY, centerX - 60, navY, SCROLL_BUTTON_SIZE * 2,
          SCROLL_BUTTON_SIZE * 2)) {
        currentPage--;
        contentScrollOffset = 0;
        playClickSound();
        return true;
      }

      // Next button
      if (currentPage < selectedEntry.pages().size() - 1
          && isMouseOver((int) mouseX, (int) mouseY, centerX + 40, navY, SCROLL_BUTTON_SIZE * 2,
              SCROLL_BUTTON_SIZE * 2)) {
        currentPage++;
        contentScrollOffset = 0;
        playClickSound();
        return true;
      }
    }

    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    if (isDraggingEntity && button == 0) {
      double deltaX = mouseX - lastMouseX;
      double deltaY = mouseY - lastMouseY;

      // Update rotation based on mouse movement
      entityRotationY += (float) deltaX * 0.8f;
      entityRotationX += (float) deltaY * 0.5f;

      // Clamp vertical rotation to prevent flipping
      entityRotationX = Mth.clamp(entityRotationX, -45f, 45f);

      lastMouseX = mouseX;
      lastMouseY = mouseY;
      return true;
    }
    return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (button == 0 && isDraggingEntity) {
      isDraggingEntity = false;
      return true;
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    int sidebarX = 20;
    int contentX = sidebarX + SIDEBAR_WIDTH + 20;

    // Over sidebar
    if (mouseX >= sidebarX && mouseX < sidebarX + SIDEBAR_WIDTH + 4) {
      if (mouseY < cachedEntryListStartY - 20) {
        categoryScrollOffset -= (int) scrollY;
        categoryScrollOffset = Mth.clamp(categoryScrollOffset, 0, cachedMaxCategoryScroll);
      } else {
        entryScrollOffset -= (int) scrollY;
        entryScrollOffset = Mth.clamp(entryScrollOffset, 0, cachedMaxEntryScroll);
      }
      return true;
    }

    // Over content area
    if (mouseX >= contentX) {
      contentScrollOffset -= (int) (scrollY * 24);
      contentScrollOffset = Mth.clamp(contentScrollOffset, 0, maxContentScroll);
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == KeyMappingsEvents.OPEN_CODEX.get().getKey().getValue()) {
      this.onClose();
      return true;
    }

    if (selectedEntry != null && selectedEntry.pages().size() > 1) {
      if ((keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) && currentPage > 0) {
        currentPage--;
        contentScrollOffset = 0;
        playClickSound();
        return true;
      }
      if ((keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D)
          && currentPage < selectedEntry.pages().size() - 1) {
        currentPage++;
        contentScrollOffset = 0;
        playClickSound();
        return true;
      }
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  // #endregion Mouse/Keyboard Handling

  @Override
  public boolean isPauseScreen() {
    return false;
  }
}
