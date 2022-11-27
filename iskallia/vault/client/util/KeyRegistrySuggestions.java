package iskallia.vault.client.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.vault.VaultRegistry;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyRegistrySuggestions {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private static final Style UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
   private static final Style LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
   private static final List<Style> ARGUMENT_STYLES = Stream.of(
         ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD
      )
      .map(Style.EMPTY::withColor)
      .collect(ImmutableList.toImmutableList());
   final Minecraft minecraft;
   final Screen screen;
   final EditBox input;
   final Font font;
   private final boolean commandsOnly;
   private final boolean onlyShowIfCursorPastError;
   final int lineStartOffset;
   final int suggestionLineLimit;
   final boolean anchorToBottom;
   final int fillColor;
   private final List<FormattedCharSequence> commandUsage = Lists.newArrayList();
   private int commandUsagePosition;
   private int commandUsageWidth;
   @Nullable
   private ParseResults<SharedSuggestionProvider> currentParse;
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Nullable
   KeyRegistrySuggestions.SuggestionsList suggestions;
   private boolean allowSuggestions;
   boolean keepSuggestions;

   public KeyRegistrySuggestions(
      Minecraft pMinecraft,
      Screen pScreen,
      EditBox pInput,
      Font pFont,
      boolean pCommandsOnly,
      boolean pOnlyShowIfCursorPastError,
      int pLineStartOffset,
      int pSuggestionLineLimit,
      boolean pAnchorToBottom,
      int pFillColor
   ) {
      this.minecraft = pMinecraft;
      this.screen = pScreen;
      this.input = pInput;
      this.font = pFont;
      this.commandsOnly = pCommandsOnly;
      this.onlyShowIfCursorPastError = pOnlyShowIfCursorPastError;
      this.lineStartOffset = pLineStartOffset;
      this.suggestionLineLimit = pSuggestionLineLimit;
      this.anchorToBottom = pAnchorToBottom;
      this.fillColor = pFillColor;
      pInput.setFormatter(this::formatChat);
   }

   public void setAllowSuggestions(boolean pAutoSuggest) {
      this.allowSuggestions = pAutoSuggest;
      if (!pAutoSuggest) {
         this.suggestions = null;
      }
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.suggestions != null && this.suggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
         return true;
      } else if (this.screen.getFocused() == this.input && pKeyCode == 258) {
         this.showSuggestions(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double pDelta) {
      return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(pDelta, -1.0, 1.0));
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pMouseButton) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)pMouseX, (int)pMouseY, pMouseButton);
   }

   public void showSuggestions(boolean pNarrateFirstSuggestion) {
      String value = this.input.getValue();
      List<Suggestion> suggestionList = VaultRegistry.TEMPLATE_POOL
         .getKeys()
         .stream()
         .map(VersionedKey::getId)
         .<String>map(ResourceLocation::toString)
         .filter(id -> id.contains(value))
         .map(id -> new Suggestion(new StringRange(0, value.length()), id))
         .collect(Collectors.toList());
      if (!suggestionList.isEmpty()) {
         int i = 0;

         for (Suggestion suggestion : suggestionList) {
            i = Math.max(i, this.font.width(suggestion.getText()));
         }

         int j = Mth.clamp(this.input.getScreenX(0), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
         int k = this.anchorToBottom ? this.screen.height - 12 : this.input.y + this.input.getHeight() + 2;
         this.suggestions = new KeyRegistrySuggestions.SuggestionsList(j, k, i, suggestionList, pNarrateFirstSuggestion);
      }
   }

   private List<Suggestion> sortSuggestions(Suggestions pSuggestions) {
      String s = this.input.getValue().substring(0, this.input.getCursorPosition());
      int i = getLastWordIndex(s);
      String s1 = s.substring(i).toLowerCase(Locale.ROOT);
      List<Suggestion> list = Lists.newArrayList();
      List<Suggestion> list1 = Lists.newArrayList();

      for (Suggestion suggestion : pSuggestions.getList()) {
         if (!suggestion.getText().startsWith(s1) && !suggestion.getText().startsWith("minecraft:" + s1)) {
            list1.add(suggestion);
         } else {
            list.add(suggestion);
         }
      }

      list.addAll(list1);
      return list;
   }

   public void updateCommandInfo() {
      String s = this.input.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(s)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.input.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
   }

   private static int getLastWordIndex(String pText) {
      if (Strings.isNullOrEmpty(pText)) {
         return 0;
      } else {
         int i = 0;
         Matcher matcher = WHITESPACE_PATTERN.matcher(pText);

         while (matcher.find()) {
            i = matcher.end();
         }

         return i;
      }
   }

   private static FormattedCharSequence getExceptionMessage(CommandSyntaxException pException) {
      Component component = ComponentUtils.fromMessage(pException.getRawMessage());
      String s = pException.getContext();
      return s == null
         ? component.getVisualOrderText()
         : new TranslatableComponent("command.context.parse_error", new Object[]{component, pException.getCursor(), s}).getVisualOrderText();
   }

   private void updateUsageInfo() {
      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.screen.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(ChatFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.allowSuggestions && this.minecraft.options.autoSuggestions) {
         this.showSuggestions(false);
      }
   }

   private void fillNodeUsage(ChatFormatting pFormatting) {
   }

   private FormattedCharSequence formatChat(String p_93915_, int p_93916_) {
      return this.currentParse != null ? formatText(this.currentParse, p_93915_, p_93916_) : FormattedCharSequence.forward(p_93915_, Style.EMPTY);
   }

   @Nullable
   static String calculateSuggestionSuffix(String pInputText, String pSuggestionText) {
      return pSuggestionText.startsWith(pInputText) ? pSuggestionText.substring(pInputText.length()) : null;
   }

   private static FormattedCharSequence formatText(ParseResults<SharedSuggestionProvider> pProvider, String pCommand, int pMaxLength) {
      List<FormattedCharSequence> list = Lists.newArrayList();
      int i = 0;
      int j = -1;
      CommandContextBuilder<SharedSuggestionProvider> commandcontextbuilder = pProvider.getContext().getLastChild();

      for (ParsedArgument<SharedSuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
         if (++j >= ARGUMENT_STYLES.size()) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - pMaxLength, 0);
         if (k >= pCommand.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - pMaxLength, pCommand.length());
         if (l > 0) {
            list.add(FormattedCharSequence.forward(pCommand.substring(i, k), LITERAL_STYLE));
            list.add(FormattedCharSequence.forward(pCommand.substring(k, l), ARGUMENT_STYLES.get(j)));
            i = l;
         }
      }

      if (pProvider.getReader().canRead()) {
         int i1 = Math.max(pProvider.getReader().getCursor() - pMaxLength, 0);
         if (i1 < pCommand.length()) {
            int j1 = Math.min(i1 + pProvider.getReader().getRemainingLength(), pCommand.length());
            list.add(FormattedCharSequence.forward(pCommand.substring(i, i1), LITERAL_STYLE));
            list.add(FormattedCharSequence.forward(pCommand.substring(i1, j1), UNPARSED_STYLE));
            i = j1;
         }
      }

      list.add(FormattedCharSequence.forward(pCommand.substring(i), LITERAL_STYLE));
      return FormattedCharSequence.composite(list);
   }

   public void render(PoseStack pPoseStack, int pMouseX, int pMouseY) {
      if (this.suggestions != null) {
         this.suggestions.render(pPoseStack, pMouseX, pMouseY);
      } else {
         int i = 0;

         for (FormattedCharSequence formattedcharsequence : this.commandUsage) {
            int j = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * i : 72 + 12 * i;
            GuiComponent.fill(pPoseStack, this.commandUsagePosition - 1, j, this.commandUsagePosition + this.commandUsageWidth + 1, j + 12, this.fillColor);
            this.font.drawShadow(pPoseStack, formattedcharsequence, this.commandUsagePosition, j + 2, -1);
            i++;
         }
      }
   }

   public String getNarrationMessage() {
      return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
   }

   @OnlyIn(Dist.CLIENT)
   public class SuggestionsList {
      private final Rect2i rect;
      private final String originalContents;
      private final List<Suggestion> suggestionList;
      private int offset;
      private int current;
      private Vec2 lastMouse = Vec2.ZERO;
      private boolean tabCycles;
      private int lastNarratedEntry;

      SuggestionsList(int p_93957_, int p_93958_, int p_93959_, List<Suggestion> p_93960_, boolean p_93961_) {
         int i = p_93957_ - 1;
         int j = KeyRegistrySuggestions.this.anchorToBottom
            ? p_93958_ - 3 - Math.min(p_93960_.size(), KeyRegistrySuggestions.this.suggestionLineLimit) * 12
            : p_93958_;
         this.rect = new Rect2i(i, j, p_93959_ + 1, Math.min(p_93960_.size(), KeyRegistrySuggestions.this.suggestionLineLimit) * 12);
         this.originalContents = KeyRegistrySuggestions.this.input.getValue();
         this.lastNarratedEntry = p_93961_ ? -1 : 0;
         this.suggestionList = p_93960_;
         this.select(0);
      }

      public void render(PoseStack pPoseStack, int pMouseX, int pMouseY) {
         int i = Math.min(this.suggestionList.size(), KeyRegistrySuggestions.this.suggestionLineLimit);
         int j = -5592406;
         boolean flag = this.offset > 0;
         boolean flag1 = this.suggestionList.size() > this.offset + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.lastMouse.x != pMouseX || this.lastMouse.y != pMouseY;
         if (flag3) {
            this.lastMouse = new Vec2(pMouseX, pMouseY);
         }

         if (flag2) {
            GuiComponent.fill(
               pPoseStack,
               this.rect.getX(),
               this.rect.getY() - 1,
               this.rect.getX() + this.rect.getWidth(),
               this.rect.getY(),
               KeyRegistrySuggestions.this.fillColor
            );
            GuiComponent.fill(
               pPoseStack,
               this.rect.getX(),
               this.rect.getY() + this.rect.getHeight(),
               this.rect.getX() + this.rect.getWidth(),
               this.rect.getY() + this.rect.getHeight() + 1,
               KeyRegistrySuggestions.this.fillColor
            );
            if (flag) {
               for (int k = 0; k < this.rect.getWidth(); k++) {
                  if (k % 2 == 0) {
                     GuiComponent.fill(pPoseStack, this.rect.getX() + k, this.rect.getY() - 1, this.rect.getX() + k + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for (int i1 = 0; i1 < this.rect.getWidth(); i1++) {
                  if (i1 % 2 == 0) {
                     GuiComponent.fill(
                        pPoseStack,
                        this.rect.getX() + i1,
                        this.rect.getY() + this.rect.getHeight(),
                        this.rect.getX() + i1 + 1,
                        this.rect.getY() + this.rect.getHeight() + 1,
                        -1
                     );
                  }
               }
            }
         }

         boolean flag4 = false;

         for (int l = 0; l < i; l++) {
            Suggestion suggestion = this.suggestionList.get(l + this.offset);
            GuiComponent.fill(
               pPoseStack,
               this.rect.getX(),
               this.rect.getY() + 12 * l,
               this.rect.getX() + this.rect.getWidth(),
               this.rect.getY() + 12 * l + 12,
               KeyRegistrySuggestions.this.fillColor
            );
            if (pMouseX > this.rect.getX()
               && pMouseX < this.rect.getX() + this.rect.getWidth()
               && pMouseY > this.rect.getY() + 12 * l
               && pMouseY < this.rect.getY() + 12 * l + 12) {
               if (flag3) {
                  this.select(l + this.offset);
               }

               flag4 = true;
            }

            KeyRegistrySuggestions.this.font
               .drawShadow(
                  pPoseStack, suggestion.getText(), this.rect.getX() + 1, this.rect.getY() + 2 + 12 * l, l + this.offset == this.current ? -256 : -5592406
               );
         }

         if (flag4) {
            Message message = this.suggestionList.get(this.current).getTooltip();
            if (message != null) {
               KeyRegistrySuggestions.this.screen.renderTooltip(pPoseStack, ComponentUtils.fromMessage(message), pMouseX, pMouseY);
            }
         }
      }

      public boolean mouseClicked(int pMouseX, int pMouseY, int pMouseButton) {
         if (!this.rect.contains(pMouseX, pMouseY)) {
            return false;
         } else {
            int i = (pMouseY - this.rect.getY()) / 12 + this.offset;
            if (i >= 0 && i < this.suggestionList.size()) {
               this.select(i);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double pDelta) {
         int i = (int)(
            KeyRegistrySuggestions.this.minecraft.mouseHandler.xpos()
               * KeyRegistrySuggestions.this.minecraft.getWindow().getGuiScaledWidth()
               / KeyRegistrySuggestions.this.minecraft.getWindow().getScreenWidth()
         );
         int j = (int)(
            KeyRegistrySuggestions.this.minecraft.mouseHandler.ypos()
               * KeyRegistrySuggestions.this.minecraft.getWindow().getGuiScaledHeight()
               / KeyRegistrySuggestions.this.minecraft.getWindow().getScreenHeight()
         );
         if (this.rect.contains(i, j)) {
            this.offset = Mth.clamp((int)(this.offset - pDelta), 0, Math.max(this.suggestionList.size() - KeyRegistrySuggestions.this.suggestionLineLimit, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
         if (pKeyCode == 265) {
            this.cycle(-1);
            this.tabCycles = false;
            return true;
         } else if (pKeyCode == 264) {
            this.cycle(1);
            this.tabCycles = false;
            return true;
         } else if (pKeyCode == 258) {
            if (this.tabCycles) {
               this.cycle(Screen.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (pKeyCode == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int pChange) {
         this.select(this.current + pChange);
         int i = this.offset;
         int j = this.offset + KeyRegistrySuggestions.this.suggestionLineLimit - 1;
         if (this.current < i) {
            this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - KeyRegistrySuggestions.this.suggestionLineLimit, 0));
         } else if (this.current > j) {
            this.offset = Mth.clamp(
               this.current + KeyRegistrySuggestions.this.lineStartOffset - KeyRegistrySuggestions.this.suggestionLineLimit,
               0,
               Math.max(this.suggestionList.size() - KeyRegistrySuggestions.this.suggestionLineLimit, 0)
            );
         }
      }

      public void select(int pIndex) {
         this.current = pIndex;
         if (this.current < 0) {
            this.current = this.current + this.suggestionList.size();
         }

         if (this.current >= this.suggestionList.size()) {
            this.current = this.current - this.suggestionList.size();
         }

         Suggestion suggestion = this.suggestionList.get(this.current);
         KeyRegistrySuggestions.this.input
            .setSuggestion(
               KeyRegistrySuggestions.calculateSuggestionSuffix(KeyRegistrySuggestions.this.input.getValue(), suggestion.apply(this.originalContents))
            );
         if (this.lastNarratedEntry != this.current) {
            NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
         }
      }

      public void useSuggestion() {
         Suggestion suggestion = this.suggestionList.get(this.current);
         KeyRegistrySuggestions.this.keepSuggestions = true;
         KeyRegistrySuggestions.this.input.setValue(suggestion.apply(this.originalContents));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         KeyRegistrySuggestions.this.input.setCursorPosition(i);
         KeyRegistrySuggestions.this.input.setHighlightPos(i);
         this.select(this.current);
         KeyRegistrySuggestions.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      Component getNarrationMessage() {
         this.lastNarratedEntry = this.current;
         Suggestion suggestion = this.suggestionList.get(this.current);
         Message message = suggestion.getTooltip();
         return message != null
            ? new TranslatableComponent(
               "narration.suggestion.tooltip", new Object[]{this.current + 1, this.suggestionList.size(), suggestion.getText(), message}
            )
            : new TranslatableComponent("narration.suggestion", new Object[]{this.current + 1, this.suggestionList.size(), suggestion.getText()});
      }

      public void hide() {
         KeyRegistrySuggestions.this.suggestions = null;
      }
   }
}
