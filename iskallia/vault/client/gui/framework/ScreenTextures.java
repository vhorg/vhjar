package iskallia.vault.client.gui.framework;

import iskallia.vault.VaultMod;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.DynamicProgressElement;
import iskallia.vault.client.gui.framework.element.NineSliceButtonElement;
import iskallia.vault.client.gui.framework.element.ProgressElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.ThreeSliceHorizontal;
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
   public static final TextureAtlasRegion BLACK_MARKET_ORNAMENT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/black_market_ornament")
   );
   public static final TextureAtlasRegion OMEGA_BLACK_MARKET_ORNAMENT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/omega_black_market_ornament")
   );
   public static final TextureAtlasRegion VAULT_FORGE_BACKGROUND = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_forge"));
   public static final TextureAtlasRegion VAULT_FORGE_PROFICIENCY_BAR = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/vault_forge_proficiency_bar")
   );
   public static final TextureAtlasRegion ANTIQUE_COLLECTOR_BOOK_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/antique_collector_book")
   );
   public static final TextureAtlasRegion ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_ACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/antique_collector_book_arrow_left_active")
   );
   public static final TextureAtlasRegion ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_INACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/antique_collector_book_arrow_left_inactive")
   );
   public static final TextureAtlasRegion ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_ACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/antique_collector_book_arrow_right_active")
   );
   public static final TextureAtlasRegion ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_INACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/antique_collector_book_arrow_right_inactive")
   );
   public static final ButtonElement.ButtonTextures ANTIQUE_COLLECTOR_BOOK_NAV_LEFT = new ButtonElement.ButtonTextures(
      ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_INACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_ACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_ACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_LEFT_INACTIVE
   );
   public static final ButtonElement.ButtonTextures ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT = new ButtonElement.ButtonTextures(
      ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_INACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_ACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_ACTIVE,
      ANTIQUE_COLLECTOR_BOOK_NAV_RIGHT_INACTIVE
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/background")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_EXTRACT_TUMBLE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/extract_tumble")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_SLOT_HINT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/card_slot")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_PROGRESS_INPUT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/card_progress_input")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_PROGRESS_OUTPUT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/card_progress_output")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_ACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/upgrade_active")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_ACTIVE_HELD = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/upgrade_held")
   );
   public static final TextureAtlasRegion CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_INACTIVE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_essence_extractor/upgrade_inactive")
   );
   public static final ButtonElement.ButtonTextures CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE = new ButtonElement.ButtonTextures(
      CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_ACTIVE,
      CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_ACTIVE_HELD,
      CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_ACTIVE_HELD,
      CARD_ESSENCE_EXTRACTOR_BUTTON_UPGRADE_INACTIVE
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
   public static final TextureAtlasRegion INSET_CARD_SLOT_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/inset_card_slot_background")
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
   public static final TextureAtlasRegion BUTTON_PROFICIENCY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_proficiency")
   );
   public static final TextureAtlasRegion BUTTON_PROFICIENCY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_proficiency_disabled")
   );
   public static final TextureAtlasRegion BUTTON_PROFICIENCY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_proficiency_hover")
   );
   public static final TextureAtlasRegion BUTTON_PROFICIENCY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_proficiency_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_PROFICIENCY__TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_PROFICIENCY, BUTTON_PROFICIENCY_HOVER, BUTTON_PROFICIENCY_PRESSED, BUTTON_PROFICIENCY_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_CRAFT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_craft")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_CRAFT_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_craft_disabled")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_CRAFT_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_craft_hover")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_CRAFT_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_craft_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_ALCHEMY_CRAFT_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_ALCHEMY_CRAFT, BUTTON_ALCHEMY_CRAFT_HOVER, BUTTON_ALCHEMY_CRAFT_PRESSED, BUTTON_ALCHEMY_CRAFT_DISABLED
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
   public static final TextureAtlasRegion BUTTON_SHARE = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_share"));
   public static final TextureAtlasRegion BUTTON_SHARE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_share_disabled")
   );
   public static final TextureAtlasRegion BUTTON_SHARE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_share_hover")
   );
   public static final TextureAtlasRegion BUTTON_SHARE_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_share_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SHARE_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_SHARE, BUTTON_SHARE_HOVER, BUTTON_SHARE_PRESSED, BUTTON_SHARE_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_HISTORY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_history"));
   public static final TextureAtlasRegion BUTTON_HISTORY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_history_disabled")
   );
   public static final TextureAtlasRegion BUTTON_HISTORY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_history_hover")
   );
   public static final TextureAtlasRegion BUTTON_HISTORY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_history_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_HISTORY_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_HISTORY, BUTTON_HISTORY_HOVER, BUTTON_HISTORY_PRESSED, BUTTON_HISTORY_DISABLED
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
   public static final TextureAtlasRegion OMEGA_BUTTON_TRADE_WIDE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/omega_button_trade_wide")
   );
   public static final TextureAtlasRegion OMEGA_BUTTON_TRADE_WIDE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/omega_button_trade_wide_disabled")
   );
   public static final TextureAtlasRegion OMEGA_BUTTON_TRADE_WIDE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/omega_button_trade_wide_hover")
   );
   public static final ButtonElement.ButtonTextures OMEGA_BUTTON_TRADE_WIDE_TEXTURES = new ButtonElement.ButtonTextures(
      OMEGA_BUTTON_TRADE_WIDE, OMEGA_BUTTON_TRADE_WIDE_HOVER, OMEGA_BUTTON_TRADE_WIDE_HOVER, OMEGA_BUTTON_TRADE_WIDE_DISABLED
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
   public static final TextureAtlasRegion BUTTON_TOGGLE_ON = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_toggle_on"));
   public static final TextureAtlasRegion BUTTON_TOGGLE_ON_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_toggle_on_hover")
   );
   public static final ButtonElement.ButtonTextures BUTTON_TOGGLE_ON_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_TOGGLE_ON, BUTTON_TOGGLE_ON_HOVER, BUTTON_TOGGLE_ON, BUTTON_TOGGLE_ON
   );
   public static final TextureAtlasRegion BUTTON_TOGGLE_OFF = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_toggle_off")
   );
   public static final TextureAtlasRegion BUTTON_TOGGLE_OFF_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_toggle_off_hover")
   );
   public static final ButtonElement.ButtonTextures BUTTON_TOGGLE_OFF_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_TOGGLE_OFF, BUTTON_TOGGLE_OFF_HOVER, BUTTON_TOGGLE_OFF, BUTTON_TOGGLE_OFF
   );
   public static final TextureAtlasRegion BUTTON_RESET_TRADES = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_reset_trades")
   );
   public static final TextureAtlasRegion BUTTON_RESET_TRADES_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_reset_trades_hover")
   );
   public static final TextureAtlasRegion BUTTON_RESET_TRADES_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_reset_trades_disabled")
   );
   public static final TextureAtlasRegion BUTTON_RESET_TRADES_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_reset_trades_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_RESET_TRADES_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_RESET_TRADES, BUTTON_RESET_TRADES_HOVER, BUTTON_RESET_TRADES_PRESSED, BUTTON_RESET_TRADES_DISABLED
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
   public static final NineSlice.TextureRegion TAB_BACKGROUND_LEFT_9 = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_left_background.9"), NineSlice.slice(5, 3, 5, 5)
   );
   public static final NineSlice.TextureRegion TAB_BACKGROUND_LEFT_9_DISABLED = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_left_background_disabled.9"), NineSlice.slice(5, 3, 5, 5)
   );
   public static final NineSlice.TextureRegion TAB_BACKGROUND_RIGHT_9 = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_right_background.9"), NineSlice.slice(3, 5, 5, 5)
   );
   public static final NineSlice.TextureRegion TAB_BACKGROUND_RIGHT_9_DISABLED = NineSlice.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_right_background_disabled.9"), NineSlice.slice(3, 5, 5, 5)
   );
   public static final NineSlice.TextureRegion CARD_DECK_BACKGROUND_9 = NineSlice.region(
      ModTextureAtlases.SCREEN,
      VaultMod.id("gui/screen/card_deck_background.9"),
      NineSlice.slice(20, 20, 20, 20),
      NineSlice.CenterDrawMode.Tiled,
      NineSlice.FrameDrawMode.Tiled
   );
   public static final ThreeSliceHorizontal.TextureRegion CARD_DECK_TITLE_SMALL_3 = ThreeSliceHorizontal.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_deck_title_small"), ThreeSliceHorizontal.slice(4, 4)
   );
   public static final ThreeSliceHorizontal.TextureRegion CARD_DECK_TITLE_LARGE_3 = ThreeSliceHorizontal.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/card_deck_title_large"), ThreeSliceHorizontal.slice(16, 16)
   );
   public static final TextureAtlasRegion TAB_ICON_STATISTICS = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_statistics"));
   public static final TextureAtlasRegion TAB_ICON_ABILITIES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_abilities"));
   public static final TextureAtlasRegion TAB_ICON_TALENTS = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_talents"));
   public static final TextureAtlasRegion TAB_ICON_EXPERTISES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_expertises"));
   public static final TextureAtlasRegion TAB_ICON_ARCHETYPES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_archetypes"));
   public static final TextureAtlasRegion TAB_ICON_RESEARCHES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_researches"));
   public static final TextureAtlasRegion TAB_ICON_PORTAL_VAULT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_portal_vault")
   );
   public static final TextureAtlasRegion TAB_ICON_CRYSTAL = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_crystal"));
   public static final TextureAtlasRegion TAB_ICON_LOOT = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_loot"));
   public static final TextureAtlasRegion TAB_ICON_MOBS_KILLED = TextureAtlasRegion.of(ModTextureAtlases.SCAVENGER, VaultMod.id("gui/scavenger/mob"));
   public static final TextureAtlasRegion TAB_ICON_COOP = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_coop"));
   public static final TextureAtlasRegion TAB_ICON_HISTORY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_history"));
   public static final TextureAtlasRegion TAB_ICON_FAVORITES = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_favorites"));
   public static final TextureAtlasRegion TAB_ICON_FAVORITES_GRAY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_icon_favorites_gray")
   );
   public static final TextureAtlasRegion TAB_COUNTDOWN_BACKGROUND = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/tab_countdown_background")
   );
   public static final TextureAtlasRegion TAB_ICON_WARDROBE_GEAR = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/tab_icon_gear")
   );
   public static final TextureAtlasRegion TAB_ICON_WARDROBE_HOTBAR = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/tab_icon_hotbar")
   );
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
   public static final TextureAtlasRegion BUTTON_EMPTY_16_48 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_48")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16_48_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_48_hover")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16_48_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_48_pressed")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_16_48_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_empty_16_48_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_EMPTY_16_48_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_EMPTY_16_48, BUTTON_EMPTY_16_48_HOVER, BUTTON_EMPTY_16_48_PRESSED, BUTTON_EMPTY_16_48_DISABLED
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
   public static final TextureAtlasRegion BUTTON_EMPTY_LEGENDARY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_empty_legendary")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_LEGENDARY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_empty_legendary_hover")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_LEGENDARY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_empty_legendary_pressed")
   );
   public static final TextureAtlasRegion BUTTON_EMPTY_LEGENDARY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/button_empty_legendary_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_EMPTY_LEGENDARY_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_EMPTY_LEGENDARY, BUTTON_EMPTY_LEGENDARY_HOVER, BUTTON_EMPTY_LEGENDARY_PRESSED, BUTTON_EMPTY_LEGENDARY_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_WORKBENCH_MODIFIER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_workbench_select")
   );
   public static final TextureAtlasRegion BUTTON_WORKBENCH_MODIFIER_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_workbench_select_hover")
   );
   public static final TextureAtlasRegion BUTTON_WORKBENCH_MODIFIER_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_workbench_select_pressed")
   );
   public static final TextureAtlasRegion BUTTON_WORKBENCH_MODIFIER_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_workbench_select_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_WORKBENCH_MODIFIER_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_WORKBENCH_MODIFIER, BUTTON_WORKBENCH_MODIFIER_HOVER, BUTTON_WORKBENCH_MODIFIER_PRESSED, BUTTON_WORKBENCH_MODIFIER_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_MODIFIER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_select")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_MODIFIER_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_select_hover")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_MODIFIER_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_select_pressed")
   );
   public static final TextureAtlasRegion BUTTON_ALCHEMY_MODIFIER_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_alchemy_select_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_ALCHEMY_MODIFIER_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_ALCHEMY_MODIFIER, BUTTON_ALCHEMY_MODIFIER_HOVER, BUTTON_ALCHEMY_MODIFIER_PRESSED, BUTTON_ALCHEMY_MODIFIER_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_MODIFIER_DISCOVERY = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_modifier_discovery_select")
   );
   public static final TextureAtlasRegion BUTTON_MODIFIER_DISCOVERY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_modifier_discovery_select_hover")
   );
   public static final TextureAtlasRegion BUTTON_MODIFIER_DISCOVERY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_modifier_discovery_select_pressed")
   );
   public static final TextureAtlasRegion BUTTON_MODIFIER_DISCOVERY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_modifier_discovery_select_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_MODIFIER_DISCOVERY_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_MODIFIER_DISCOVERY, BUTTON_MODIFIER_DISCOVERY_HOVER, BUTTON_MODIFIER_DISCOVERY_PRESSED, BUTTON_MODIFIER_DISCOVERY_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_ENCHANTER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_enchanter_select")
   );
   public static final TextureAtlasRegion BUTTON_ENCHANTER_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_enchanter_select_hover")
   );
   public static final TextureAtlasRegion BUTTON_ENCHANTER_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_enchanter_select_pressed")
   );
   public static final TextureAtlasRegion BUTTON_ENCHANTER_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_enchanter_select_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_ENCHANTER_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_ENCHANTER, BUTTON_ENCHANTER_HOVER, BUTTON_ENCHANTER_PRESSED, BUTTON_ENCHANTER_DISABLED
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
   public static final TextureAtlasRegion BOUNTY_KILL_ICON_32 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/kill_entity_bounty_32")
   );
   public static final TextureAtlasRegion BOUNTY_COMPLETION_32 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/completion_bounty_32")
   );
   public static final TextureAtlasRegion BOUNTY_DAMAGE_ENTITY_32 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/damage_entity_bounty_32")
   );
   public static final TextureAtlasRegion BOUNTY_ITEM_DISCOVERY_32 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/item_discovery_bounty_32")
   );
   public static final TextureAtlasRegion BOUNTY_ITEM_SUBMISSION_32 = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/item_submission_bounty_32")
   );
   public static final TextureAtlasRegion BOUNTY_MINING_32 = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/bounty/mining_bounty_32"));
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
   public static final TextureAtlasRegion WARDROBE_SWAP = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_swap"));
   public static final TextureAtlasRegion WARDROBE_SWAP_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_swap_hover")
   );
   public static final TextureAtlasRegion WARDROBE_SWAP_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_swap_pressed")
   );
   public static final TextureAtlasRegion WARDROBE_SWAP_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_swap_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_WARDROBE_SWAP_TEXTURES = new ButtonElement.ButtonTextures(
      WARDROBE_SWAP, WARDROBE_SWAP_HOVER, WARDROBE_SWAP_PRESSED, WARDROBE_SWAP_DISABLED
   );
   public static final TextureAtlasRegion WARDROBE_TRANSPARENT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_transparent")
   );
   public static final TextureAtlasRegion WARDROBE_TRANSPARENT_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_transparent_hover")
   );
   public static final TextureAtlasRegion WARDROBE_SOLID = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_solid"));
   public static final TextureAtlasRegion WARDROBE_SOLID_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/wardrobe/button_solid_hover")
   );
   public static final ButtonElement.ButtonTextures BUTTON_WARDROBE_TRANSPARENT_TEXTURES = new ButtonElement.ButtonTextures(
      WARDROBE_TRANSPARENT, WARDROBE_TRANSPARENT_HOVER, WARDROBE_TRANSPARENT, WARDROBE_TRANSPARENT
   );
   public static final ButtonElement.ButtonTextures BUTTON_WARDROBE_SOLID_TEXTURES = new ButtonElement.ButtonTextures(
      WARDROBE_SOLID, WARDROBE_SOLID_HOVER, WARDROBE_SOLID, WARDROBE_SOLID
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SAVE = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_save"));
   public static final TextureAtlasRegion SKILL_ALTAR_SAVE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_save_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SAVE_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_save_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SAVE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_save_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_SAVE_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_SAVE, SKILL_ALTAR_SAVE_HOVER, SKILL_ALTAR_SAVE_PRESSED, SKILL_ALTAR_SAVE_DISABLED
   );
   public static final TextureAtlasRegion SKILL_ALTAR_LOAD = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_load"));
   public static final TextureAtlasRegion SKILL_ALTAR_LOAD_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_load_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_LOAD_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_load_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_LOAD_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_load_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_LOAD_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_LOAD, SKILL_ALTAR_LOAD_HOVER, SKILL_ALTAR_LOAD_PRESSED, SKILL_ALTAR_LOAD_DISABLED
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SHARE = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_share")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SHARE_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_share_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SHARE_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_share_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_SHARE_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_share_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_SHARE_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_SHARE, SKILL_ALTAR_SHARE_HOVER, SKILL_ALTAR_SHARE_PRESSED, SKILL_ALTAR_SHARE_DISABLED
   );
   public static final TextureAtlasRegion SKILL_ALTAR_COPY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_copy"));
   public static final TextureAtlasRegion SKILL_ALTAR_COPY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_copy_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_COPY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_copy_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_COPY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_copy_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_COPY_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_COPY, SKILL_ALTAR_COPY_HOVER, SKILL_ALTAR_COPY_PRESSED, SKILL_ALTAR_COPY_DISABLED
   );
   public static final TextureAtlasRegion SKILL_ALTAR_IMPORT = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_import")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_IMPORT_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_import_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_IMPORT_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_import_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_IMPORT_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_import_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_IMPORT_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_IMPORT, SKILL_ALTAR_IMPORT_HOVER, SKILL_ALTAR_IMPORT_PRESSED, SKILL_ALTAR_IMPORT_DISABLED
   );
   public static final TextureAtlasRegion SKILL_ALTAR_BACK = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_back"));
   public static final TextureAtlasRegion SKILL_ALTAR_BACK_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_back_hover")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_BACK_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_back_pressed")
   );
   public static final TextureAtlasRegion SKILL_ALTAR_BACK_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/skill_altar/button_back_disabled")
   );
   public static final ButtonElement.ButtonTextures BUTTON_SKILL_ALTAR_BACK_TEXTURES = new ButtonElement.ButtonTextures(
      SKILL_ALTAR_BACK, SKILL_ALTAR_BACK_HOVER, SKILL_ALTAR_BACK_PRESSED, SKILL_ALTAR_BACK_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_QUEST = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_quest"));
   public static final TextureAtlasRegion BUTTON_QUEST_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_quest_disabled")
   );
   public static final TextureAtlasRegion BUTTON_QUEST_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_quest_hover")
   );
   public static final TextureAtlasRegion BUTTON_QUEST_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_quest_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_QUEST_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_QUEST, BUTTON_QUEST_HOVER, BUTTON_QUEST_PRESSED, BUTTON_QUEST_DISABLED
   );
   public static final TextureAtlasRegion BUTTON_EMBER_PAY = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_ember_pay"));
   public static final TextureAtlasRegion BUTTON_EMBER_PAY_DISABLED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_ember_pay_disabled")
   );
   public static final TextureAtlasRegion BUTTON_EMBER_PAY_HOVER = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_ember_pay_hover")
   );
   public static final TextureAtlasRegion BUTTON_EMBER_PAY_PRESSED = TextureAtlasRegion.of(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/button/button_ember_pay_pressed")
   );
   public static final ButtonElement.ButtonTextures BUTTON_EMBER_PAY_TEXTURES = new ButtonElement.ButtonTextures(
      BUTTON_EMBER_PAY, BUTTON_EMBER_PAY_HOVER, BUTTON_EMBER_PAY_PRESSED, BUTTON_EMBER_PAY_DISABLED
   );
   public static final TextureAtlasRegion SLIDER = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider"));
   public static final TextureAtlasRegion SLIDER_HOVER = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider_hover"));
   public static final TextureAtlasRegion SLIDER_SMALL = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider_small"));
   public static final TextureAtlasRegion SLIDER_SMALL_HOVER = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider_small_hover"));
   public static final TextureAtlasRegion SLIDER_BACKGROUND = TextureAtlasRegion.of(ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider_background"));
   public static final ThreeSliceHorizontal.TextureRegion SLIDER_BAR_SLICES = ThreeSliceHorizontal.region(
      ModTextureAtlases.SCREEN, VaultMod.id("gui/screen/slider_bar_slices"), ThreeSliceHorizontal.slice(3, 3)
   );
   public static final TextureAtlasRegion JEWEL_NO_ITEM = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/jewel_no_item"));
   public static final TextureAtlasRegion KEY_ASHIUM = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_ashium"));
   public static final TextureAtlasRegion KEY_BOMIGNITE = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_bomignite"));
   public static final TextureAtlasRegion KEY_GORGINITE = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_gorginite"));
   public static final TextureAtlasRegion KEY_ISKALLIUM = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_iskallium"));
   public static final TextureAtlasRegion KEY_PETZANITE = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_petzanite"));
   public static final TextureAtlasRegion KEY_SPARKLETINE = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_sparkletine"));
   public static final TextureAtlasRegion KEY_TUBIUM = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_tubium"));
   public static final TextureAtlasRegion KEY_UPALINE = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_upaline"));
   public static final TextureAtlasRegion KEY_XENIUM = TextureAtlasRegion.of(ModTextureAtlases.SLOT, VaultMod.id("gui/slot/key/key_xenium"));
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability_tree.png");
   public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability_tree_bgs.png");

   private ScreenTextures() {
   }
}
