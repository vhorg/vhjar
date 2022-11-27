package iskallia.vault.client.gui.framework;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.DynamicProgressElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.ProgressElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.init.ModTextureAtlases;
import net.minecraft.resources.ResourceLocation;

public final class ScreenTextures {
   public static final NineSlice.TextureRegion DEFAULT_WINDOW_BACKGROUND = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/default_window_background.9"), NineSlice.slice(5, 5, 5, 5)
   );
   public static final NineSlice.TextureRegion INSET_BLACK_BACKGROUND = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_black_background.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion INSET_GREY_BACKGROUND = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_grey_background.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion INSET_VERTICAL_SEPARATOR = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_vertical_separator.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion SLOT_FOREGROUND = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/progress_foreground.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final TextureAtlasRegion EMPTY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/empty"));
   public static final TextureAtlasRegion ICON_IDONA = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon_idona"));
   public static final TextureAtlasRegion ICON_TENOS = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon_tenos"));
   public static final TextureAtlasRegion ICON_VELARA = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon_velara"));
   public static final TextureAtlasRegion ICON_WENDARR = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon_wendarr"));
   public static final TextureAtlasRegion ICON_PLUS_SIGN = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/icon_plus_sign"));
   public static final TextureAtlasRegion PROFICIENCY_DISPLAY_FRAME = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/proficiency_display_frame")
   );
   public static final TextureAtlasRegion SOUL_SHARD_TRADE_ORNAMENT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/soul_shard_trade_ornament")
   );
   public static final TextureAtlasRegion INSET_ITEM_SLOT_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_item_slot_background")
   );
   public static final TextureAtlasRegion INSET_DISABLED_ITEM_SLOT_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_item_slot_background_disabled")
   );
   public static final TextureAtlasRegion INSET_CRAFTING_RESULT_SLOT_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_crafting_result_slot_background")
   );
   public static final TextureAtlasRegion INSET_SLOT_SELECT_FRAME = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_item_slot_select_frame")
   );
   public static final TextureAtlasRegion SCROLLBAR_HANDLE = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/scrollbar_handle"));
   public static final TextureAtlasRegion SCROLLBAR_HANDLE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/scrollbar_handle_disabled")
   );
   public static final TextureAtlasRegion BUTTON_CRAFT = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_craft"));
   public static final TextureAtlasRegion BUTTON_CRAFT_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_craft_disabled")
   );
   public static final TextureAtlasRegion BUTTON_CRAFT_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_craft_hover")
   );
   public static final TextureAtlasRegion BUTTON_CRAFT_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_craft_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_CRAFT_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_CRAFT, BUTTON_CRAFT_HOVER, BUTTON_CRAFT_PRESSED, BUTTON_CRAFT_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_TRANSMOG = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_transmog"));
   public static final TextureAtlasRegion BUTTON_TRANSMOG_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_transmog_disabled")
   );
   public static final TextureAtlasRegion BUTTON_TRANSMOG_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_transmog_hover")
   );
   public static final TextureAtlasRegion BUTTON_TRANSMOG_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_transmog_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_TRANSMOG_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_TRANSMOG, BUTTON_TRANSMOG_HOVER, BUTTON_TRANSMOG_PRESSED, BUTTON_TRANSMOG_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_RELIC = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_relic"));
   public static final TextureAtlasRegion BUTTON_RELIC_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_relic_disabled")
   );
   public static final TextureAtlasRegion BUTTON_RELIC_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_relic_hover")
   );
   public static final TextureAtlasRegion BUTTON_RELIC_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_relic_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_RELIC_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_RELIC, BUTTON_RELIC_HOVER, BUTTON_RELIC_PRESSED, BUTTON_RELIC_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_CLOSE = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_close"));
   public static final TextureAtlasRegion BUTTON_CLOSE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_close_disabled")
   );
   public static final TextureAtlasRegion BUTTON_CLOSE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_close_hover")
   );
   public static final TextureAtlasRegion BUTTON_CLOSE_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_close_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_CLOSE_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_CLOSE, BUTTON_CLOSE_HOVER, BUTTON_CLOSE_PRESSED, BUTTON_CLOSE_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_TRADE_WIDE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_trade_wide")
   );
   public static final TextureAtlasRegion BUTTON_TRADE_WIDE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_trade_wide_disabled")
   );
   public static final TextureAtlasRegion BUTTON_TRADE_WIDE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_trade_wide_hover")
   );
   public static final ButtonElement.ButtonTextures BUTTON_TRADE_WIDE_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_TRADE_WIDE, BUTTON_TRADE_WIDE_HOVER, BUTTON_TRADE_WIDE_HOVER, BUTTON_TRADE_WIDE_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_PAY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_pay"));
   public static final TextureAtlasRegion BUTTON_PAY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_pay_disabled")
   );
   public static final TextureAtlasRegion BUTTON_PAY_HOVER = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_pay_hover"));
   public static final TextureAtlasRegion BUTTON_PAY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_pay_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_PAY_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_PAY, BUTTON_PAY_HOVER, BUTTON_PAY_PRESSED, BUTTON_PAY_DISABLED
   );
   public static final TextureAtlasRegion PROGRESS_ARROW_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/progress/arrow_right_background")
   );
   public static final TextureAtlasRegion PROGRESS_ARROW_FOREGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/progress/arrow_right_foreground")
   );
   public static final ProgressElement.ProgressTextures PROGRESS_ARROW = new ProgressElement.ProgressTextures(
      PROGRESS_ARROW_BACKGROUND, PROGRESS_ARROW_FOREGROUND
   );
   public static final TextureAtlasRegion BLANK = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/blank"));
   public static final TextureAtlasRegion TAB_BACKGROUND_TOP = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_background_top"));
   public static final TextureAtlasRegion TAB_BACKGROUND_TOP_SELECTED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_background_top_selected")
   );
   public static final TextureAtlasRegion TAB_BACKGROUND_RIGHT = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_background_right"));
   public static final TextureAtlasRegion TAB_BACKGROUND_RIGHT_SELECTED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_background_right_selected")
   );
   public static final TextureAtlasRegion TAB_BACKGROUND_RIGHT_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_background_right_disabled")
   );
   public static final TextureAtlasRegion TAB_ICON_STATISTICS = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_statistics"));
   public static final TextureAtlasRegion TAB_ICON_ABILITIES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_abilities"));
   public static final TextureAtlasRegion TAB_ICON_TALENTS = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_talents"));
   public static final TextureAtlasRegion TAB_ICON_ARCHETYPES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_archetypes"));
   public static final TextureAtlasRegion TAB_ICON_RESEARCHES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_researches"));
   public static final TextureAtlasRegion TAB_ICON_PORTAL_VAULT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_portal_vault")
   );
   public static final TextureAtlasRegion TAB_ICON_CRYSTAL = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_crystal"));
   public static final TextureAtlasRegion TAB_ICON_LOOT = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_loot"));
   public static final TextureAtlasRegion TAB_ICON_MOBS_KILLED = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/mob"));
   public static final TextureAtlasRegion TAB_ICON_COOP = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_coop"));
   public static final TextureAtlasRegion ICON_COIN_STACKS = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/coin_stacks"));
   public static final TextureAtlasRegion ICON_GILDED_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/gilded_chest"));
   public static final TextureAtlasRegion ICON_LIVING_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/living_chest"));
   public static final TextureAtlasRegion ICON_ORNATE_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/ornate_chest"));
   public static final TextureAtlasRegion ICON_WOODEN_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/wooden_chest"));
   public static final TextureAtlasRegion ICON_TREASURE_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/treasure_chest"));
   public static final TextureAtlasRegion ICON_ALTAR_CHEST = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/altar_chest"));
   public static final TextureAtlasRegion VAULT_EXIT_ELEMENT_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_exit_element_background")
   );
   public static final TextureAtlasRegion VAULT_EXIT_ELEMENT_HORIZONTAL_SPLITTER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_exit_element_horizontal_splitter")
   );
   public static final TextureAtlasRegion STEVE_HEAD = TextureAtlasRegion.of(ModTextureAtlases.MOB_HEADS, VaultMod.id("gui/mob_heads/minecraft/player"));
   public static final NineSlice.TextureRegion VAULT_EXIT_ELEMENT_ICON = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_exit_element_1.9"), NineSlice.slice(3, 3, 3, 3)
   );
   public static final NineSlice.TextureRegion VAULT_EXIT_ELEMENT_BG = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_exit_element_2.9"), NineSlice.slice(3, 3, 3, 3)
   );
   public static final NineSlice.TextureRegion VAULT_EXIT_ELEMENT_TITLE = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_exit_element_3.9"), NineSlice.slice(3, 3, 3, 3)
   );
   public static final TextureAtlasRegion VAULT_LEVEL_BAR = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_level_bar"));
   public static final TextureAtlasRegion VAULT_LEVEL_BAR_GAINED_XP = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_level_bar_gained_xp")
   );
   public static final TextureAtlasRegion VAULT_LEVEL_BAR_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_level_bar_background")
   );
   public static final NineSlice.TextureRegion BUTTON_EMPTY = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_normal.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion BUTTON_EMPTY_HOVER = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_hover.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion BUTTON_EMPTY_PRESSED = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_pressed.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSlice.TextureRegion BUTTON_EMPTY_DISABLED = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_disabled.9"), NineSlice.slice(1, 1, 1, 1)
   );
   public static final NineSliceButtonElement.NineSliceButtonTextures BUTTON_EMPTY_TEXTURES = new NineSliceButtonElement.NineSliceButtonTextures(
      BUTTON_EMPTY, BUTTON_EMPTY_HOVER, BUTTON_EMPTY_PRESSED, BUTTON_EMPTY_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16 = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16"));
   public static final TextureAtlasRegion BUTTON_EMPTY_16_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_hover")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_pressed")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_EMPTY_16_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_EMPTY_16, BUTTON_EMPTY_16_HOVER, BUTTON_EMPTY_16_PRESSED, BUTTON_EMPTY_16_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_32 = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_32"));
   public static final TextureAtlasRegion BUTTON_EMPTY_32_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_32_hover")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_32_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_32_pressed")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_32_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_32_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_EMPTY_32_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_EMPTY_32, BUTTON_EMPTY_32_HOVER, BUTTON_EMPTY_32_PRESSED, BUTTON_EMPTY_32_DISABLED
   );
   public static final DynamicProgressElement.ProgressTextures BOUNTY_PROGRESS_BAR = new DynamicProgressElement.ProgressTextures(
      INSET_GREY_BACKGROUND, SLOT_FOREGROUND
   );
   public static final TextureAtlasRegion BOUNTY_KILL_ICON = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/kill_entity_bounty2")
   );
   public static final TextureAtlasRegion BOUNTY_COMPLETION = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/completion_bounty")
   );
   public static final TextureAtlasRegion BOUNTY_DAMAGE_ENTITY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/damage_entity_bounty")
   );
   public static final TextureAtlasRegion BOUNTY_ITEM_DISCOVERY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/item_discovery_bounty")
   );
   public static final TextureAtlasRegion BOUNTY_ITEM_SUBMISSION = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/item_submission_bounty")
   );
   public static final TextureAtlasRegion BOUNTY_MINING = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/mining_bounty"));
   public static final TextureAtlasRegion BOUNTY_UNIDENTIFIED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/unidentified_bounty")
   );
   public static final TextureAtlasRegion BOUNTY_REROLL = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_reroll"));
   public static final TextureAtlasRegion BOUNTY_REROLL_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_reroll_hover")
   );
   public static final TextureAtlasRegion BOUNTY_REROLL_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_reroll_pressed")
   );
   public static final TextureAtlasRegion BOUNTY_REROLL_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_reroll_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_BUTTON_REROLL_TEXTURES = new ButtonElement.ButtonTextures(
      BOUNTY_REROLL, BOUNTY_REROLL_HOVER, BOUNTY_REROLL_PRESSED, BOUNTY_REROLL_DISABLED
   );
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability_tree.png");
   public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability_tree_bgs.png");

   private ScreenTextures() {
   }
}
