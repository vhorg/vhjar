package iskallia.vault.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.card.CardDeckConfig;
import iskallia.vault.container.inventory.CardDeckContainer;
import iskallia.vault.container.inventory.CardDeckContainerMenu;
import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.item.CuriosGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.tool.IManualModelLoading;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CardDeckItem extends Item implements CuriosGearItem, ICurioItem, IManualModelLoading {
   public static final String SLOT = "deck";

   public CardDeckItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   @Override
   public boolean isIntendedSlot(ItemStack stack, String slot) {
      return "deck".equals(slot);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid, ItemStack stack) {
      if (context.entity() instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem())) {
         return ImmutableMultimap.of();
      } else {
         return (Multimap<Attribute, AttributeModifier>)("deck".equals(context.identifier())
            ? VaultGearHelper.getModifiers(AttributeGearData.read(stack))
            : ImmutableMultimap.of());
      }
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      if (slotContext.entity() instanceof Player player && ServerVaults.get(player.level).isPresent()) {
         return false;
      } else if (!"deck".equals(slotContext.identifier())) {
         return false;
      } else {
         return CuriosApi.getSlotHelper() != null && !CuriosApi.getSlotHelper().getSlotType("deck").<Boolean>map(ISlotType::isVisible).orElse(false)
            ? false
            : super.canEquip(slotContext, stack);
      }
   }

   public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
      return slotContext.entity() instanceof Player player && ServerVaults.get(player.level).isPresent() ? false : super.canUnequip(slotContext, stack);
   }

   public Component getName(ItemStack pStack) {
      return !ModConfigs.isInitialized()
         ? super.getName(pStack)
         : ModConfigs.CARD_DECK.getName(getId(pStack)).<TextComponent>map(TextComponent::new).map(cmp -> (Component)cmp).orElseGet(() -> super.getName(pStack));
   }

   public CompoundTag getShareTag(ItemStack stack) {
      CompoundTag nbt = super.getShareTag(stack);
      if (nbt == null) {
         return null;
      } else {
         nbt = nbt.copy();
         nbt.remove("inventory");
         return nbt;
      }
   }

   public static String getId(ItemStack stack) {
      return stack.getTag() != null && stack.getTag().contains("id") ? stack.getTag().getString("id") : null;
   }

   public static void setId(ItemStack stack, String pool) {
      stack.getOrCreateTag().putString("id", pool);
   }

   public static Optional<CardDeck> getCardDeck(ItemStack stack) {
      if (stack.getTag() != null && stack.getTag().contains("data")) {
         CardDeck deck = new CardDeck();
         deck.readNbt(stack.getTag().getCompound("data"));
         return Optional.of(deck);
      } else {
         return Optional.empty();
      }
   }

   public static CardDeck setCardDeck(ItemStack stack, CardDeck card) {
      card.writeNbt().ifPresent(tag -> stack.getOrCreateTag().put("data", tag));
      return card;
   }

   public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      getCardDeck(stack).ifPresent(deck -> deck.addText(tooltip, tooltip.size(), flag, (float)ClientScheduler.INSTANCE.getTickCount()));
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(stack, world, entity, slot, selected);
      if (!world.isClientSide()) {
         RandomSource random = JavaRandom.ofNanoTime();
         String deckId = getId(stack);
         if (deckId == null || !ModConfigs.CARD_DECK.has(deckId) && getCardDeck(stack).isEmpty()) {
            setId(stack, ModConfigs.CARD_DECK.getFirst());
         }

         if (getCardDeck(stack).isEmpty()) {
            ModConfigs.CARD_DECK.generate(getId(stack), random).ifPresent(deck -> setCardDeck(stack, deck));
         }
      }
   }

   public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         if (ModConfigs.isInitialized()) {
            ModConfigs.CARD_DECK.getIds().forEach(id -> {
               ItemStack stack = new ItemStack(this);
               setId(stack, id);
               items.add(stack);
            });
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      final ItemStack stack = player.getItemInHand(hand);
      if (hand == InteractionHand.MAIN_HAND && !world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
         final int slot = serverPlayer.getInventory().selected;
         NetworkHooks.openGui(serverPlayer, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Card Deck");
            }

            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
               return new CardDeckContainerMenu(windowId, inventory, slot, false, new CardDeckContainer(stack));
            }
         }, buf -> {
            buf.writeItem(stack);
            buf.writeInt(slot);
            buf.writeBoolean(false);
         });
      }

      return InteractionResultHolder.pass(stack);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return oldStack.getItem() != newStack.getItem();
   }

   public void initializeClient(Consumer<IItemRenderProperties> consumer) {
      consumer.accept(new IItemRenderProperties() {
         public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return CardDeckItemRenderer.INSTANCE;
         }
      });
   }

   @Override
   public void loadModels(Consumer<ModelResourceLocation> consumer) {
      CardDeckConfig config = new CardDeckConfig().readConfig();
      ModConfigs.CONFIGS.remove(config);

      for (String model : config.getModels()) {
         consumer.accept(new ModelResourceLocation(model));
      }
   }
}
