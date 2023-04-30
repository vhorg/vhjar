package iskallia.vault.client.gui.screen.block;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.EntityModelElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.element.TabElement;
import iskallia.vault.client.gui.framework.element.TextureAtlasElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.player.element.CuriosElement;
import iskallia.vault.container.WardrobeContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.CuriosGearItem;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.GearAttributeTrinket;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.integration.IntegrationCurios;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.network.message.ServerboundWardrobeTabMessage;
import iskallia.vault.util.StatUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class WardrobeScreen<T extends WardrobeContainer> extends AbstractElementContainerScreen<T> {
   private static final Map<Class<?>, WardrobeScreen.AttributeTypeHandler<?>> ATTRIBUTE_TYPE_HANDLERS = Map.of(
      Integer.class,
      new WardrobeScreen.AttributeTypeHandler<>(100, v -> v == 0, v -> v > 0, v -> -v),
      Float.class,
      new WardrobeScreen.AttributeTypeHandler<>(99, v -> Math.abs(v) < 1.0E-4F, v -> v > 0.0F, v -> -v),
      Double.class,
      new WardrobeScreen.AttributeTypeHandler<>(98, v -> Math.abs(v) < 1.0E-4, v -> v > 0.0, v -> -v)
   );
   private static final WardrobeScreen.AttributeTypeHandler<?> DEFAULT_ATTRIBUTE_TYPE_HANDLER = new WardrobeScreen.AttributeTypeHandler<>(
      0, v -> false, v -> false, v -> v
   );
   private static final Set<VaultGearAttribute<?>> EXCLUDED_ATTRIBUTES = Set.of(ModGearAttributes.DURABILITY, ModGearAttributes.SOULBOUND);
   private static final Map<VaultGearAttribute<?>, Integer> ATTRIBUTES_ORDER = new LinkedHashMap<>();

   private static <T> WardrobeScreen.AttributeTypeHandler<T> getAttributeTypeHandler(T value) {
      return (WardrobeScreen.AttributeTypeHandler<T>)ATTRIBUTE_TYPE_HANDLERS.getOrDefault(value.getClass(), DEFAULT_ATTRIBUTE_TYPE_HANDLER);
   }

   public WardrobeScreen(T container, Inventory inventory, Component title) {
      super(container, inventory, title, ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.setGuiSize(Spatials.size(204, 194));
      this.addElement(
         (NineSliceElement)new NineSliceElement(Spatials.positionXY(0, 0).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)
            .layout((screen, gui, parent, world) -> world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(-28, 10))))
      );
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, 6), new TextComponent("Wardrobe").withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      MutableComponent inventoryName = inventory.getDisplayName().copy();
      inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
      this.addElement(
         (LabelElement)new LabelElement(
               Spatials.positionXY(8, ((WardrobeContainer)this.getMenu()).getSlot(0).y - 12), inventoryName, LabelTextStyle.defaultStyle()
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
      this.addElement((SlotsElement)new SlotsElement(this).layout((screen, gui, parent, world) -> world.positionXY(gui)));
      this.addElement(
         (EntityModelElement)new EntityModelElement(
               Spatials.positionXY(this.imageWidth / 2 - 42, 17), Spatials.size(54, 72), () -> inventory.player, Spatials.positionY(8), 1.0F
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
   }

   private static TabElement<?> createTab(boolean selected, TextureAtlasRegion icon, int x, Runnable onClick) {
      return new TabElement(
            Spatials.positionXYZ(x, selected ? -28 : -24, 1),
            new TextureAtlasElement(selected ? ScreenTextures.TAB_BACKGROUND_TOP_SELECTED : ScreenTextures.TAB_BACKGROUND_TOP),
            new TextureAtlasElement(Spatials.positionXYZ(6, selected ? 9 : 5, 1), icon),
            onClick
         )
         .layout((screen, gui, parent, world) -> world.translateXY(gui));
   }

   private static <T> int compareAttributeValues(VaultGearAttribute<T> attribute, Object o1Value, Object o2Value) {
      VaultGearAttributeType<T> type = attribute.getType();
      return attribute.getAttributeComparator() == null ? 0 : attribute.getAttributeComparator().compare(type.cast(o1Value), type.cast(o2Value));
   }

   static {
      int i = 0;

      for (VaultGearAttribute<?> attribute : VaultGearAttributeRegistry.getRegistry()) {
         ATTRIBUTES_ORDER.put(attribute, i++);
      }
   }

   private static class AttributeTypeHandler<T> {
      private final Predicate<T> isZero;
      private final Predicate<T> isGreaterThanZero;
      private final int sortOrder;
      private UnaryOperator<T> invert;

      private AttributeTypeHandler(int sortOrder, Predicate<T> isZero, Predicate<T> isGreaterThanZero, UnaryOperator<T> invert) {
         this.isZero = isZero;
         this.isGreaterThanZero = isGreaterThanZero;
         this.sortOrder = sortOrder;
         this.invert = invert;
      }

      public T invert(T value) {
         return this.invert.apply(value);
      }

      public boolean isZero(T value) {
         return this.isZero.test(value);
      }

      public boolean isGreaterThanZero(T value) {
         return this.isGreaterThanZero.test(value);
      }

      public int getSortOrder() {
         return this.sortOrder;
      }
   }

   public static class Gear extends WardrobeScreen<WardrobeContainer.Gear> {
      private final ButtonElement<?> swapButton;
      private final List<Component> swapTooltip = new ArrayList<>();
      private boolean isShiftPressed = false;
      boolean inTooltipClearCooldown = false;
      private long tooltipClearTime = 0L;

      public Gear(WardrobeContainer.Gear container, Inventory inventory, Component title) {
         super(container, inventory, title);
         this.addElement(WardrobeScreen.createTab(true, ScreenTextures.TAB_ICON_WARDROBE_GEAR, 4, () -> {}));
         this.addElement(
            WardrobeScreen.createTab(
               false,
               ScreenTextures.TAB_ICON_WARDROBE_HOTBAR,
               35,
               () -> ModNetwork.CHANNEL.sendToServer(new ServerboundWardrobeTabMessage(true, ((WardrobeContainer.Gear)this.getMenu()).getPos()))
            )
         );
         this.addElement(
            new CuriosElement(
               () -> Spatials.positionXY(-8, this.topPos + 14),
               ((WardrobeContainer.Gear)this.getMenu()).getCurioContainerHandler().canScroll(),
               ((WardrobeContainer.Gear)this.getMenu()).getCurioContainerHandler().getVisibleSlotCount(),
               value -> ((WardrobeContainer.Gear)this.getMenu()).getCurioContainerHandler().scrollTo(value)
            )
         );
         this.addElement(
            (NineSliceElement)new NineSliceElement(
                  Spatials.positionXYZ(this.imageWidth - 32, 10, -20)
                     .size(32, ((WardrobeContainer.Gear)this.getMenu()).getCurioContainerHandler().getVisibleSlotCount() * 18 + 16),
                  ScreenTextures.DEFAULT_WINDOW_BACKGROUND
               )
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
         this.swapButton = new ButtonElement(
               Spatials.positionXY(this.imageWidth / 2 - 15 - 9, 89),
               ScreenTextures.BUTTON_WARDROBE_SWAP_TEXTURES,
               ((WardrobeContainer.Gear)this.getMenu())::swap
            )
            .layout((screen, gui, parent, world) -> world.translateXY(gui));
         this.swapButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            tooltipRenderer.renderComponentTooltip(poseStack, this.getSwapButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
            return true;
         });
         this.swapButton.setDisabled(!((WardrobeContainer.Gear)this.getMenu()).isOwner());
         this.addElement(this.swapButton);
         this.addElement(new WardrobeScreen.Gear.SolidRenderButtonElement(Spatials.positionXY(this.imageWidth - 51, 3)))
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
            .tooltip(
               () -> ((WardrobeContainer.Gear)this.getMenu()).shouldRenderSolid()
                  ? new TranslatableComponent("screen.the_vault.wardrobe.tooltip.render_solid")
                  : new TranslatableComponent("screen.the_vault.wardrobe.tooltip.render_transparent")
            );
         ((WardrobeContainer.Gear)this.getMenu()).setSlotChangeListener(() -> {
            this.inTooltipClearCooldown = true;
            this.tooltipClearTime = System.currentTimeMillis() + 100L;
         });
      }

      protected void containerTick() {
         super.containerTick();
         if (this.inTooltipClearCooldown && System.currentTimeMillis() > this.tooltipClearTime) {
            this.inTooltipClearCooldown = false;
            this.swapTooltip.clear();
         }
      }

      private List<Component> getSwapButtonTooltipLines() {
         this.initSwapTooltip();
         return this.swapTooltip;
      }

      private void initSwapTooltip() {
         if (this.swapTooltip.isEmpty() && this.isShiftPressed == ((WardrobeContainer.Gear)this.getMenu()).isHoldingShift()) {
            this.swapTooltip.add(new TranslatableComponent("screen.the_vault.wardrobe.tooltip.swap"));
            if (this.isShiftPressed) {
               this.swapTooltip
                  .add(
                     new TranslatableComponent("screen.the_vault.wardrobe.tooltip.swap_all")
                        .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
                  );
            } else {
               this.swapTooltip
                  .add(
                     new TranslatableComponent("screen.the_vault.wardrobe.tooltip.swap_matching")
                        .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
                  );
            }

            this.swapTooltip.add(TextComponent.EMPTY);
            LocalPlayer player = Minecraft.getInstance().player;
            Player wardrobePlayer = this.getPlayerWithWardrobeItemsSwapped(player);
            double playerDps = StatUtils.getAverageDps(player);
            double playerArmor = StatUtils.getDefence(player);
            double wardrobeDps = StatUtils.getAverageDps(wardrobePlayer);
            double wardrobeArmor = StatUtils.getDefence(wardrobePlayer);
            double diffDps = wardrobeDps - playerDps;
            double diffArmor = wardrobeArmor - playerArmor;
            boolean showDamage = Math.abs((int)diffDps) > 0;
            if (showDamage) {
               this.swapTooltip.add(this.getTooltipWithValue(diffDps, "screen.the_vault.wardrobe.tooltip.damage", "%.1f", Style.EMPTY.withColor(16730699)));
            }

            boolean showDefense = Math.abs((int)(diffArmor * 100.0)) > 0;
            if (showDefense) {
               this.swapTooltip
                  .add(this.getTooltipWithValue(diffArmor * 100.0, "screen.the_vault.wardrobe.tooltip.defense", "%.0f", Style.EMPTY.withColor(4766456)));
            }

            if (showDamage || showDefense) {
               this.swapTooltip.add(TextComponent.EMPTY);
            }

            List<VaultGearAttributeInstance<?>> addingAttributeInstances = new ArrayList<>();
            List<VaultGearAttributeInstance<?>> removingAttributeInstances = new ArrayList<>();
            Map<VaultGearAttribute<?>, VaultGearAttributeInstance<?>> mergeableAttributes = new HashMap<>();
            this.addEquipmentSlotsAttributes(
               mergeableAttributes,
               removingAttributeInstances,
               equipmentSlot -> !this.isShiftPressed && ((WardrobeContainer.Gear)this.getMenu()).getStoredEquipmentBySlot(equipmentSlot).isEmpty()
                  ? ItemStack.EMPTY
                  : this.simulateVaultGear(equipmentSlot, player.getItemBySlot(equipmentSlot)),
               true
            );
            Map<String, List<Tuple<ItemStack, Integer>>> curiosItemStacks = IntegrationCurios.getCuriosItemStacks(player);
            if (!this.isShiftPressed) {
               curiosItemStacks.forEach((slotKey, tuples) -> tuples.forEach(t -> {
                  if (((WardrobeContainer.Gear)this.getMenu()).getStoredCurio(slotKey, (Integer)t.getB()).isEmpty()) {
                     t.setA(ItemStack.EMPTY);
                  }
               }));
            }

            this.addCuriosAttributes(player, mergeableAttributes, removingAttributeInstances, curiosItemStacks, true);
            this.addEquipmentSlotsAttributes(
               mergeableAttributes, addingAttributeInstances, ((WardrobeContainer.Gear)this.getMenu())::getStoredEquipmentBySlot, false
            );
            this.addCuriosAttributes(player, mergeableAttributes, addingAttributeInstances, ((WardrobeContainer.Gear)this.getMenu()).getStoredCurios(), false);
            mergeableAttributes.values()
               .forEach(instance -> addMergeableAttribute(addingAttributeInstances, removingAttributeInstances, (VaultGearAttributeInstance<?>)instance));
            addingAttributeInstances.sort(WardrobeScreen.GearAttributeInstanceRegistryOrderComparator.INSTANCE);
            removingAttributeInstances.sort(WardrobeScreen.GearAttributeInstanceRegistryOrderComparator.INSTANCE);
            if (removingAttributeInstances.isEmpty() && addingAttributeInstances.isEmpty()) {
               this.swapTooltip.add(new TranslatableComponent("screen.the_vault.wardrobe.tooltip.no_difference").withStyle(ChatFormatting.YELLOW));
            } else {
               this.swapTooltip.add(new TranslatableComponent("screen.the_vault.wardrobe.tooltip.difference").withStyle(ChatFormatting.GRAY));
               WardrobeScreen.ATTRIBUTES_ORDER
                  .keySet()
                  .forEach(
                     attribute -> {
                        addingAttributeInstances.stream()
                           .filter(instance -> instance.getAttribute().equals(attribute))
                           .forEach(inst -> this.addTooltipDisplay((VaultGearAttributeInstance<?>)inst, "+", ChatFormatting.GREEN));
                        removingAttributeInstances.stream()
                           .filter(instance -> instance.getAttribute().equals(attribute))
                           .forEach(inst -> this.addTooltipDisplay((VaultGearAttributeInstance<?>)inst, "-", ChatFormatting.RED));
                     }
                  );
            }
         }
      }

      private Player getPlayerWithWardrobeItemsSwapped(LocalPlayer player) {
         Player gearPlayer = new Player(
            player.level, ((WardrobeContainer.Gear)this.getMenu()).getBlockPos(), 0.0F, new GameProfile(null, "dummyWardrobeTooltip")
         ) {
            public boolean isSpectator() {
               return false;
            }

            public boolean isCreative() {
               return false;
            }
         };
         Map<String, List<Tuple<ItemStack, Integer>>> curiosItemStacks;
         if (this.isShiftPressed) {
            curiosItemStacks = ((WardrobeContainer.Gear)this.getMenu()).getStoredCurios();
         } else {
            curiosItemStacks = IntegrationCurios.getCuriosItemStacks(player);
            curiosItemStacks.forEach((slotKey, tuples) -> tuples.forEach(t -> {
               ItemStack storedStack = ((WardrobeContainer.Gear)this.getMenu()).getStoredCurio(slotKey, (Integer)t.getB());
               if (!storedStack.isEmpty()) {
                  t.setA(storedStack);
               }
            }));
         }

         gearPlayer.removeAllEffects();
         player.getActiveEffects().forEach(gearPlayer::addEffect);

         for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack storedEquipment = equipmentSlot != EquipmentSlot.MAINHAND
               ? ((WardrobeContainer.Gear)this.getMenu()).getStoredEquipmentBySlot(equipmentSlot)
               : ((WardrobeContainer.Gear)this.getMenu()).getHotbarItems().getStackInSlot(0);
            if (this.isShiftPressed) {
               this.updateItemSlot(gearPlayer, equipmentSlot, storedEquipment);
            } else if (!storedEquipment.isEmpty()) {
               this.updateItemSlot(gearPlayer, equipmentSlot, storedEquipment);
            } else {
               this.updateItemSlot(gearPlayer, equipmentSlot, player.getItemBySlot(equipmentSlot));
            }
         }

         curiosItemStacks.forEach(
            (slotKey, stacks) -> stacks.forEach(t -> IntegrationCurios.setCurioItemStack(gearPlayer, (ItemStack)t.getA(), slotKey, (Integer)t.getB()))
         );
         return gearPlayer;
      }

      private void updateItemSlot(Player player, EquipmentSlot equipmentSlot, ItemStack stack) {
         player.setItemSlot(equipmentSlot, stack);
         stack.getAttributeModifiers(equipmentSlot).forEach((attribute, modifier) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
               attributeInstance.addTransientModifier(modifier);
            }
         });
      }

      private Component getTooltipWithValue(double value, String translationKey, String format, Style style) {
         return value > 0.0
            ? new TextComponent("+")
               .append(new TranslatableComponent(translationKey, new Object[]{String.format(format, value)}).withStyle(style))
               .withStyle(ChatFormatting.GREEN)
            : new TextComponent("-")
               .append(new TranslatableComponent(translationKey, new Object[]{String.format(format, Math.abs(value))}).withStyle(style))
               .withStyle(ChatFormatting.RED);
      }

      private static <T> void addMergeableAttribute(
         List<VaultGearAttributeInstance<?>> addingAttributeInstances,
         List<VaultGearAttributeInstance<?>> removingAttributeInstances,
         VaultGearAttributeInstance<T> instance
      ) {
         WardrobeScreen.AttributeTypeHandler<T> ath = WardrobeScreen.getAttributeTypeHandler(instance.getValue());
         if (!ath.isZero(instance.getValue())) {
            if (ath.isGreaterThanZero(instance.getValue())) {
               addingAttributeInstances.add(instance);
            } else {
               instance.setValue(ath.invert(instance.getValue()));
               removingAttributeInstances.add(instance);
            }
         }
      }

      private void addEquipmentSlotsAttributes(
         Map<VaultGearAttribute<?>, VaultGearAttributeInstance<?>> mergeableAttributes,
         List<VaultGearAttributeInstance<?>> attributeInstances,
         Function<EquipmentSlot, ItemStack> getItemBySlot,
         boolean inverted
      ) {
         for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot != EquipmentSlot.MAINHAND) {
               ItemStack stack = getItemBySlot.apply(equipmentSlot);
               if (!stack.isEmpty()) {
                  stack = this.simulateVaultGear(equipmentSlot, stack);
                  Item gearData = stack.getItem();
                  if (gearData instanceof VaultGearItem) {
                     VaultGearItem gearItem = (VaultGearItem)gearData;
                     if (!gearItem.isIntendedForSlot(stack, equipmentSlot)) {
                        continue;
                     }
                  }

                  gearData = stack.getItem();
                  if (gearData instanceof CuriosGearItem) {
                     CuriosGearItem gearItem = (CuriosGearItem)gearData;
                     if (!gearItem.isIntendedSlot(stack, equipmentSlot)) {
                        continue;
                     }
                  }

                  AttributeGearData data = AttributeGearData.read(stack);
                  if (data instanceof VaultGearData gearDatax) {
                     VaultGearData.Type.ALL_MODIFIERS
                        .getAttributeSource(gearDatax)
                        .forEach(
                           instance -> this.addAttribute(
                              mergeableAttributes, attributeInstances, inverted, VaultGearAttributeInstance.cast(instance.getAttribute(), instance.getValue())
                           )
                        );
                  } else {
                     data.getAttributes()
                        .forEach(
                           instance -> this.addAttribute(
                              mergeableAttributes, attributeInstances, inverted, VaultGearAttributeInstance.cast(instance.getAttribute(), instance.getValue())
                           )
                        );
                  }
               }
            }
         }
      }

      private void addAttribute(
         Map<VaultGearAttribute<?>, VaultGearAttributeInstance<?>> mergeableAttributes,
         List<VaultGearAttributeInstance<?>> attributeInstances,
         boolean inverted,
         VaultGearAttributeInstance<?> instance
      ) {
         VaultGearAttribute<?> attribute = instance.getAttribute();
         if (!WardrobeScreen.EXCLUDED_ATTRIBUTES.contains(attribute)) {
            if (attribute.getAttributeComparator() == null) {
               attributeInstances.add(instance);
            } else {
               this.mergeAttribute(mergeableAttributes, inverted, instance, attribute);
            }
         }
      }

      private <T> void mergeAttribute(
         Map<VaultGearAttribute<?>, VaultGearAttributeInstance<?>> mergeableAttributes,
         boolean inverted,
         VaultGearAttributeInstance<T> instance,
         VaultGearAttribute<?> attribute
      ) {
         if (mergeableAttributes.containsKey(attribute)) {
            VaultGearAttributeInstance<T> mergeIntoInstance = (VaultGearAttributeInstance<T>)mergeableAttributes.get(attribute);
            T value = instance.getValue();
            if (inverted) {
               WardrobeScreen.AttributeTypeHandler<T> ath = WardrobeScreen.getAttributeTypeHandler(value);
               value = ath.invert(value);
            }

            mergeIntoInstance.setValue(mergeIntoInstance.getAttribute().getAttributeComparator().merge(mergeIntoInstance.getValue(), value));
         } else {
            T value = instance.getValue();
            if (inverted) {
               WardrobeScreen.AttributeTypeHandler<T> ath = WardrobeScreen.getAttributeTypeHandler(value);
               instance.setValue(ath.invert(value));
            }

            mergeableAttributes.put(attribute, instance);
         }
      }

      private void addCuriosAttributes(
         Player player,
         Map<VaultGearAttribute<?>, VaultGearAttributeInstance<?>> mergeableAttributes,
         List<VaultGearAttributeInstance<?>> attributeInstances,
         Map<String, List<Tuple<ItemStack, Integer>>> curiosItemStacks,
         boolean inverted
      ) {
         TrinketHelper.getTrinkets(curiosItemStacks, GearAttributeTrinket.class)
            .forEach(
               gearTrinket -> {
                  if (gearTrinket.isUsable(player)) {
                     ((GearAttributeTrinket)gearTrinket.trinket())
                        .getAttributes()
                        .forEach(instance -> this.addAttribute(mergeableAttributes, attributeInstances, inverted, (VaultGearAttributeInstance<?>)instance));
                  }
               }
            );
         curiosItemStacks.forEach(
            (slot, stacks) -> stacks.forEach(
               stackTpl -> {
                  ItemStack stack = (ItemStack)stackTpl.getA();
                  if (AttributeGearData.hasData(stack)) {
                     if (!(stack.getItem() instanceof CuriosGearItem curiosGearItem && !curiosGearItem.isIntendedSlot(stack, slot))) {
                        if (!stack.is(ModItems.MAGNET) || !MagnetItem.isLegacy(stack)) {
                           AttributeGearData.<AttributeGearData>read(stack)
                              .getAttributes()
                              .forEach(
                                 instance -> this.addAttribute(mergeableAttributes, attributeInstances, inverted, (VaultGearAttributeInstance<?>)instance)
                              );
                        }
                     }
                  }
               }
            )
         );
      }

      private ItemStack simulateVaultGear(EquipmentSlot slot, ItemStack stack) {
         if (stack.getItem() instanceof VaultGearItem) {
            return stack;
         } else if (stack.getItem() instanceof ArmorItem armorItem) {
            ItemStack gearStack = new ItemStack(VaultArmorItem.forSlot(slot));
            VaultGearData data = VaultGearData.read(gearStack);
            data.setState(VaultGearState.IDENTIFIED);
            data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.ARMOR, armorItem.getDefense()));
            data.addModifier(VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.ARMOR_TOUGHNESS, (int)armorItem.getToughness()));
            data.write(gearStack);
            return gearStack;
         } else {
            return stack;
         }
      }

      private void addTooltipDisplay(VaultGearAttributeInstance<?> vaultGearAttributeInstance, String prefix, ChatFormatting formatting) {
         vaultGearAttributeInstance.getDisplay(VaultGearData.read(new ItemStack(ModItems.BOOTS)), VaultGearModifier.AffixType.IMPLICIT, ItemStack.EMPTY, true)
            .ifPresent(displayText -> {
               if (!prefix.isEmpty()) {
                  displayText = new TextComponent(prefix).withStyle(formatting).append(displayText);
               }

               this.swapTooltip.add(displayText);
            });
      }

      @Override
      public void mouseMoved(double mouseX, double mouseY) {
         super.mouseMoved(mouseX, mouseY);
         if (!this.swapTooltip.isEmpty() && !this.swapButton.isMouseOver(mouseX, mouseY)) {
            this.swapTooltip.clear();
         }
      }

      public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
         Key key = InputConstants.getKey(pKeyCode, pScanCode);
         if (pKeyCode != 256 && !Minecraft.getInstance().options.keyInventory.isActiveAndMatches(key)) {
            if (this.isShiftKey(pKeyCode) && !this.isShiftPressed) {
               this.swapTooltip.clear();
               this.isShiftPressed = true;
            }

            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
         } else {
            this.onClose();
            return true;
         }
      }

      public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
         if (this.isShiftKey(pKeyCode) && this.isShiftPressed) {
            this.swapTooltip.clear();
            this.isShiftPressed = false;
         }

         return super.keyReleased(pKeyCode, pScanCode, pModifiers);
      }

      public boolean isShiftKey(int keyCode) {
         return keyCode == 340 || keyCode == 344;
      }

      private class SolidRenderButtonElement extends ButtonElement<WardrobeScreen.Gear.SolidRenderButtonElement> {
         public SolidRenderButtonElement(IMutableSpatial position) {
            super(position, ScreenTextures.BUTTON_WARDROBE_TRANSPARENT_TEXTURES, () -> ((WardrobeContainer.Gear)Gear.this.getMenu()).toggleSolidRender());
         }

         @Override
         public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            ButtonElement.ButtonTextures buttonTextures = ((WardrobeContainer.Gear)Gear.this.getMenu()).shouldRenderSolid()
               ? ScreenTextures.BUTTON_WARDROBE_SOLID_TEXTURES
               : ScreenTextures.BUTTON_WARDROBE_TRANSPARENT_TEXTURES;
            TextureAtlasRegion texture = buttonTextures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), this.clickHeld);
            renderer.render(texture, poseStack, this.worldSpatial);
         }
      }
   }

   private static class GearAttributeInstanceComparator implements Comparator<VaultGearAttributeInstance<?>> {
      public static final WardrobeScreen.GearAttributeInstanceRegistryOrderComparator INSTANCE = new WardrobeScreen.GearAttributeInstanceRegistryOrderComparator();

      public int compare(VaultGearAttributeInstance<?> o1, VaultGearAttributeInstance<?> o2) {
         WardrobeScreen.AttributeTypeHandler<?> ath1 = WardrobeScreen.getAttributeTypeHandler(o1.getValue().getClass());
         WardrobeScreen.AttributeTypeHandler<?> ath2 = WardrobeScreen.getAttributeTypeHandler(o2.getValue().getClass());
         int result = ath1.getSortOrder() - ath2.getSortOrder();
         return result != 0 ? result : WardrobeScreen.compareAttributeValues(o1.getAttribute(), o1.getValue(), o2.getValue());
      }
   }

   private static class GearAttributeInstanceRegistryOrderComparator implements Comparator<VaultGearAttributeInstance<?>> {
      public static final WardrobeScreen.GearAttributeInstanceRegistryOrderComparator INSTANCE = new WardrobeScreen.GearAttributeInstanceRegistryOrderComparator();

      public int compare(VaultGearAttributeInstance<?> o1, VaultGearAttributeInstance<?> o2) {
         VaultGearAttribute<?> attribute = o1.getAttribute();
         int result = WardrobeScreen.ATTRIBUTES_ORDER.getOrDefault(attribute, -1) - WardrobeScreen.ATTRIBUTES_ORDER.getOrDefault(o2.getAttribute(), -1);
         return result != 0 ? result : WardrobeScreen.compareAttributeValues(attribute, o1.getValue(), o2.getValue());
      }
   }

   public static class Hotbar extends WardrobeScreen<WardrobeContainer.Hotbar> {
      public Hotbar(WardrobeContainer.Hotbar container, Inventory inventory, Component title) {
         super(container, inventory, title);
         this.addElement(
            WardrobeScreen.createTab(
               false,
               ScreenTextures.TAB_ICON_WARDROBE_GEAR,
               4,
               () -> ModNetwork.CHANNEL.sendToServer(new ServerboundWardrobeTabMessage(false, ((WardrobeContainer.Hotbar)this.getMenu()).getPos()))
            )
         );
         this.addElement(WardrobeScreen.createTab(true, ScreenTextures.TAB_ICON_WARDROBE_HOTBAR, 35, () -> {}));
      }
   }
}
