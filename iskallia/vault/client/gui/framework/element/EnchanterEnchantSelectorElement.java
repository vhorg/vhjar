package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.framework.text.TextAlign;
import iskallia.vault.client.gui.framework.text.TextWrap;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.util.ItemRenderHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.EnchantmentEntry;
import iskallia.vault.util.InventoryUtil;
import iskallia.vault.util.RomanNumber;
import iskallia.vault.util.StringUtils;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class EnchanterEnchantSelectorElement<E extends EnchanterEnchantSelectorElement<E, V>, V extends EnchanterEnchantSelectorElement.EnchanterListElement<V>>
   extends ScrollableListSelectorElement<E, EnchantmentEntry, V> {
   public static final ResourceLocation ENCHANT_FONT = new ResourceLocation("minecraft", "alt");
   private final ObservableSupplier<ItemStack> inputSupplier;

   public EnchanterEnchantSelectorElement(ISpatial spatial, ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
      super(
         Spatials.copy(spatial).width(ScreenTextures.BUTTON_ENCHANTER_TEXTURES.button().width()),
         new EnchanterEnchantSelectorElement.EnchanterEnchantSelectorModel<>(inputSupplier, searchFilter)
      );
      this.inputSupplier = inputSupplier;
   }

   public void onSelect(Consumer<EnchantmentEntry> fn) {
      if (this.getSelectorModel() instanceof EnchanterEnchantSelectorElement.EnchanterEnchantSelectorModel<?> selModel) {
         selModel.whenSelected(cfg -> {
            EnchantmentEntry entry = selModel.getSelectedEnchantingEntry();
            if (entry != null) {
               fn.accept(entry);
            }
         });
      }
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.inputSupplier.ifChanged(change -> this.refreshElements());
   }

   public static class EnchanterEnchantSelectorModel<E extends EnchanterEnchantSelectorElement.EnchanterListElement<E>>
      extends ScrollableListSelectorElement.SelectorModel<E, EnchantmentEntry> {
      private final ObservableSupplier<ItemStack> inputSupplier;
      private final Supplier<String> searchFilter;

      public EnchanterEnchantSelectorModel(ObservableSupplier<ItemStack> inputSupplier, Supplier<String> searchFilter) {
         this.inputSupplier = inputSupplier;
         this.searchFilter = searchFilter;
      }

      @Override
      public List<EnchantmentEntry> getEntries() {
         ItemStack input = this.inputSupplier.get();
         if (input.isEmpty()) {
            return Collections.emptyList();
         } else {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
               return Collections.emptyList();
            } else {
               String searchTerm = this.searchFilter.get().toLowerCase(Locale.ROOT);
               Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(input);
               List<EnchantmentEntry> out = new ArrayList<>();

               for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
                  if (!enchantment.isCurse() && enchantment.canEnchant(input)) {
                     String enchantName = new TranslatableComponent(enchantment.getDescriptionId()).getString();
                     if (enchantName.toLowerCase(Locale.ROOT).contains(searchTerm)) {
                        out.add(new EnchantmentEntry(enchantment, enchantment.getMaxLevel()));
                     }
                  }
               }

               Map<EnchantmentEntry, Boolean> canCraftLookup = new HashMap<>();
               out.forEach(enchantmentEntry -> canCraftLookup.put(enchantmentEntry, canCraft(input, enchantmentEntry)));
               Map<EnchantmentEntry, Boolean> alreadyHasLookup = new HashMap<>();
               out.forEach(
                  enchantmentEntry -> alreadyHasLookup.put(
                     enchantmentEntry, currentEnchantments.getOrDefault(enchantmentEntry.getEnchantment(), 0) >= enchantmentEntry.getLevel()
                  )
               );
               out.sort(Comparator.comparing(o -> o.getEnchantment().getRegistryName().toString()));
               out.sort((c1, c2) -> -Boolean.compare(canCraftLookup.get(c1), canCraftLookup.get(c2)));
               out.sort((c1, c2) -> Boolean.compare(alreadyHasLookup.get(c1), alreadyHasLookup.get(c2)));
               return out;
            }
         }
      }

      public E createSelectable(ISpatial spatial, EnchantmentEntry entry) {
         return (E)(new EnchanterEnchantSelectorElement.EnchanterListElement(spatial, this.inputSupplier.get(), entry));
      }

      @Nullable
      protected EnchantmentEntry getSelectedEnchantingEntry() {
         E element = (E)((EnchanterEnchantSelectorElement.EnchanterListElement)this.getSelectedElement());
         return element == null ? null : element.getConfig();
      }

      public static boolean canCraft(ItemStack gearStack, EnchantmentEntry config) {
         if (gearStack.isEmpty()) {
            return false;
         } else if (config.getEnchantment() == null) {
            return false;
         } else {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
               return false;
            } else {
               Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(gearStack);
               boolean hasExp = player.isCreative() || player.experienceLevel >= ModConfigs.VAULT_GEAR_ENCHANTMENT_CONFIG.getCost(config).getLevels();
               return !hasExp
                  ? false
                  : config.getEnchantment() != null
                     && (enchantments.get(config.getEnchantment()) == null || enchantments.get(config.getEnchantment()) < config.getLevel());
            }
         }
      }
   }

   public static class EnchanterListElement<E extends EnchanterEnchantSelectorElement.EnchanterListElement<E>> extends SelectableButtonElement<E> {
      private final ItemStack gearStack;
      private final EnchantmentEntry config;
      private final LabelTextStyle textStyle;
      private List<ItemStack> inputs;
      private List<Enchantment> incompatibleEnchants;

      public EnchanterListElement(IPosition position, ItemStack gearStack, EnchantmentEntry config) {
         super(position, ScreenTextures.BUTTON_ENCHANTER_TEXTURES, () -> {});
         this.gearStack = gearStack;
         this.config = config;
         this.textStyle = LabelTextStyle.defaultStyle().shadow().build();
         Enchantment enchantment = this.config.getEnchantment();
         Map<Enchantment, Integer> gearEnchantments = EnchantmentHelper.getEnchantments(this.gearStack);
         this.incompatibleEnchants = gearEnchantments.keySet().stream().filter(other -> other != enchantment && !other.isCompatibleWith(enchantment)).toList();
         this.tooltip(
            (tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
               Player player = Minecraft.getInstance().player;
               if (player == null) {
                  return false;
               } else {
                  List<ItemStack> missingItems = InventoryUtil.getMissingInputs(config.getCost().getItems(), player.getInventory());
                  List<Component> tooltip = new LinkedList<>();
                  tooltip.add(
                     new TranslatableComponent(enchantment.getDescriptionId())
                        .append(" " + RomanNumber.toRoman(config.getLevel()))
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
                  );
                  if (this.isSelected()) {
                     tooltip.add(new TextComponent("Currently selected").withStyle(ChatFormatting.DARK_PURPLE).withStyle(ChatFormatting.ITALIC));
                  }

                  if (gearEnchantments.get(config.getEnchantment()) != null && gearEnchantments.get(config.getEnchantment()) >= config.getLevel()) {
                     tooltip.add(new TextComponent(""));
                     tooltip.add(new TextComponent("Already enchanted with this enchantment").withStyle(ChatFormatting.RED));
                  }

                  if (!this.incompatibleEnchants.isEmpty()) {
                     tooltip.add(new TextComponent(""));
                     tooltip.add(new TextComponent("Enchanting with this will ").withStyle(ChatFormatting.DARK_RED));
                     tooltip.add(new TextComponent("remove following enchantment(s):").withStyle(ChatFormatting.DARK_RED));

                     for (Enchantment incompatibleEnchant : this.incompatibleEnchants) {
                        tooltip.add(
                           new TextComponent("☀ ")
                              .append(new TranslatableComponent(incompatibleEnchant.getDescriptionId()))
                              .append(" " + RomanNumber.toRoman(gearEnchantments.get(incompatibleEnchant)))
                              .withStyle(ChatFormatting.RED)
                        );
                     }
                  }

                  tooltip.add(new TextComponent(""));
                  tooltip.add(new TextComponent("Cost:"));

                  for (ItemStack costStack : config.getCost().getItems()) {
                     boolean missing = missingItems.contains(costStack);
                     tooltip.add(
                        new TextComponent((missing ? "✘ " : "✔ ") + costStack.getCount() + "x ")
                           .append(costStack.getHoverName().copy())
                           .withStyle(missing ? ChatFormatting.RED : ChatFormatting.GREEN)
                     );
                  }

                  int levelCost = config.getCost().getLevels();
                  if (levelCost != 0) {
                     boolean missing = player.experienceLevel < levelCost;
                     tooltip.add(
                        new TextComponent((missing ? "✘ " : "✔ ") + levelCost + " EXP Levels").withStyle(missing ? ChatFormatting.RED : ChatFormatting.GREEN)
                     );
                  }

                  tooltipRenderer.renderTooltip(poseStack, tooltip, mouseX, mouseY, ItemStack.EMPTY, TooltipDirection.RIGHT);
                  return true;
               }
            }
         );
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(this.gearStack);
         int currentLevel = enchantments.getOrDefault(this.config.getEnchantment(), 0);
         this.setDisabled(currentLevel >= this.config.getLevel());
         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
         Font font = Minecraft.getInstance().font;
         int offsetX = this.worldSpatial.x() + this.worldSpatial.width() - 18;
         int offsetY = this.worldSpatial.y() + this.worldSpatial.height() - 18;
         List<ItemStack> inputs = this.getInputs();
         List<ItemStack> missingInputs = new ArrayList<>();
         if (Minecraft.getInstance().player != null) {
            missingInputs = InventoryUtil.getMissingInputs(inputs, Minecraft.getInstance().player.getInventory());
         }

         for (ItemStack stack : inputs) {
            float scl = 0.8F;
            ItemRenderHelper.renderGuiItem(stack, offsetX, offsetY, modelViewStack -> modelViewStack.scale(scl, scl, scl));
            MutableComponent text = new TextComponent(String.valueOf(stack.getCount()));
            if (missingInputs.contains(stack)) {
               text.withStyle(ChatFormatting.RED);
            }

            poseStack.pushPose();
            poseStack.translate(offsetX + 17 - font.width(text), offsetY + 9, 200.0);
            poseStack.scale(scl, scl, scl);
            BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text, 0.0F, 0.0F, 16777215, true, poseStack.last().pose(), buffers, false, 0, LightmapHelper.getPackedFullbrightCoords());
            buffers.endBatch();
            poseStack.popPose();
            offsetX -= 17;
         }

         Enchantment enchantment = this.config.getEnchantment();
         TranslatableComponent enchantNameText = new TranslatableComponent(enchantment.getDescriptionId());
         this.textStyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               enchantNameText.copy()
                  .append(" " + RomanNumber.toRoman(this.config.getLevel()))
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.isDisabled() ? -9740731 : -11911124))),
               TextWrap.overflow(),
               TextAlign.LEFT,
               this.worldSpatial.x() + 4,
               this.worldSpatial.y() + 4,
               this.worldSpatial.z(),
               this.worldSpatial.width()
            );
         String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toUpperCase();
         String gibberishName = enchantNameText.getString().replaceAll("\\s+", "") + alphabet.charAt(this.config.getLevel() % alphabet.length());
         this.textStyle
            .textBorder()
            .render(
               renderer,
               poseStack,
               new TextComponent(StringUtils.truncateMaxLength(gibberishName, 12))
                  .withStyle(Style.EMPTY.withFont(EnchanterEnchantSelectorElement.ENCHANT_FONT))
                  .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.incompatibleEnchants.isEmpty() ? -10003127 : -5307855))),
               TextWrap.wrap(),
               TextAlign.LEFT,
               this.worldSpatial.x() + 4,
               this.worldSpatial.y() + 15,
               this.worldSpatial.z(),
               this.worldSpatial.width()
            );
      }

      public EnchantmentEntry getConfig() {
         return this.config;
      }

      protected List<ItemStack> getInputs() {
         if (this.inputs == null) {
            this.inputs = this.createNeededInputs();
         }

         return this.inputs;
      }

      protected List<ItemStack> createNeededInputs() {
         return this.config.getCost().getItems();
      }

      private boolean canCraft() {
         return EnchanterEnchantSelectorElement.EnchanterEnchantSelectorModel.canCraft(this.gearStack, this.config);
      }
   }
}
