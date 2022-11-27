package iskallia.vault.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.block.entity.DemagnetizerTileEntity;
import iskallia.vault.event.InputEvents;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.item.CuriosGearItem;
import iskallia.vault.gear.tooltip.VaultGearDataTooltip;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.mana.Mana;
import iskallia.vault.world.data.ServerVaults;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class MagnetItem extends Item implements DyeableLeatherItem, IConditionalDamageable, ICurioItem, CuriosGearItem {
   private static final UUID HEALTH_MODIFIER_ID = UUID.fromString("6d7e39e3-b6c8-4410-a8ce-d2cd344e465a");
   private static final HashMap<UUID, UUID> PULLED_ITEMS_TO_PULLING_PLAYERS = new HashMap<>();
   private static int PULLED_ITEM_BEING_PICKED_UP_COUNT = 0;

   public MagnetItem(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   @Override
   public boolean isImmuneToDamage(ItemStack stack, @Nullable Player player) {
      return player != null && getPerk(stack) == MagnetItem.Perk.IMMORTAL && ServerVaults.isInVault(player);
   }

   public static int getTextureColor(ItemStack stack) {
      CompoundTag compoundtag = stack.getTagElement("display");
      return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : -65536;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (worldIn != null) {
         int level = (100 - getSturdiness(stack)) / ModConfigs.MAGNET_CONFIG.getSturdinessDecrement();
         tooltip.add(new TextComponent("Level " + ChatFormatting.YELLOW + level));
         VaultGearDataTooltip.addRepairTooltip(tooltip, getUsedRepairSlots(stack), getMaxRepairSlots(stack));
         if (getMagnetInCurio(Minecraft.getInstance().player) == stack) {
            tooltip.add(new TextComponent(" "));
            tooltip.add(new TextComponent(ChatFormatting.BLUE + "Enabled"));
         }

         tooltip.add(new TextComponent(" "));

         for (MagnetItem.Stat s : MagnetItem.Stat.values()) {
            int value = getStatUpgrade(stack, s);
            if (value != 0) {
               MutableComponent component = new TextComponent(s.getReadableName() + (value > 0 ? " +" : " ") + value)
                  .withStyle(Style.EMPTY.withColor(ModConfigs.MAGNET_CONFIG.getStatColor(s)));
               if (InputEvents.isShiftDown()) {
                  component.append(new TextComponent(" " + ChatFormatting.DARK_GRAY + ModConfigs.MAGNET_CONFIG.getUpgrade(s).getAdvancedTooltip()));
               }

               tooltip.add(component);
            }
         }

         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
      int mana = getUsableStat(pStack, MagnetItem.Stat.MANA_EFFICIENCY);
      int range = getUsableStat(pStack, MagnetItem.Stat.RANGE);
      int speed = getUsableStat(pStack, MagnetItem.Stat.VELOCITY);
      int perkPower = 0;
      MagnetItem.Perk p = getPerk(pStack);
      if (p != MagnetItem.Perk.NONE) {
         perkPower = getPerkPower(pStack);
      }

      return Optional.of(new MagnetItem.MagnetTooltip(range, speed, mana, p, perkPower));
   }

   public static ChatFormatting getSturdinessColor(int sturdiness) {
      float cc = ModConfigs.MAGNET_CONFIG.getSturdinessCutoff();
      ChatFormatting cl;
      if (sturdiness <= cc) {
         cl = ChatFormatting.RED;
      } else if (sturdiness < cc + (100.0F - cc) / 2.0F) {
         cl = ChatFormatting.YELLOW;
      } else {
         cl = ChatFormatting.GREEN;
      }

      return cl;
   }

   public boolean isFoil(ItemStack stack) {
      return false;
   }

   public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
      if (!world.isClientSide) {
         if (entity instanceof Player player
            && itemSlot == -1
            && this.hasDurabilityLeft(stack)
            && !player.getCooldowns().isOnCooldown(stack.getItem())
            && !DemagnetizerTileEntity.hasDemagnetizerAround(entity)
            && getMagnetInCurio(player) == stack) {
            boolean instant = this.shouldPullInstantly(stack);
            boolean moveItems = this.shouldPullItems(stack);
            boolean moveXp = this.shouldPullExperience(stack);
            float speed = getUsableStat(stack, MagnetItem.Stat.VELOCITY) * 0.05F;
            float radius = getUsableStat(stack, MagnetItem.Stat.RANGE);
            if (moveXp) {
               for (ExperienceOrb orb : world.getEntitiesOfClass(ExperienceOrb.class, player.getBoundingBox().inflate(radius))) {
                  this.moveXpToPlayer(orb, player, speed, instant);
               }
            }

            if (moveItems) {
               float maxMana = Mana.get(player);
               int itemsForOneMana = getUsableStat(stack, MagnetItem.Stat.MANA_EFFICIENCY);
               float manaCostPerItem = 1.0F / (itemsForOneMana * 20.0F);
               int itemsICanPickUp = (int)(maxMana / manaCostPerItem);
               if (itemsICanPickUp <= 0) {
                  return;
               }

               List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(radius));
               items.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(player.position())));

               for (ItemEntity itemEntity : items) {
                  if (itemsICanPickUp <= 0) {
                     return;
                  }

                  if (itemEntity.isAlive()
                     && !stack.getOrCreateTag().getBoolean("PreventRemoteMovement")
                     && !itemEntity.getTags().contains("PreventMagnetFMovement")) {
                     if (!PULLED_ITEMS_TO_PULLING_PLAYERS.containsKey(itemEntity.getUUID())) {
                        Player closest = this.getClosestPlayerWithMagnet(itemEntity, radius);
                        PULLED_ITEMS_TO_PULLING_PLAYERS.put(itemEntity.getUUID(), closest == null ? player.getUUID() : closest.getUUID());
                     }

                     if (PULLED_ITEMS_TO_PULLING_PLAYERS.get(itemEntity.getUUID()).equals(player.getUUID())) {
                        ItemStack entityStack = itemEntity.getItem();
                        int amount = entityStack.getCount();
                        itemEntity.setNoPickUpDelay();
                        if (itemsICanPickUp < amount) {
                           ItemEntity split = itemEntity.copy();
                           world.addFreshEntity(split);
                           split.getItem().setCount(amount - itemsICanPickUp);
                           itemEntity.getItem().setCount(itemsICanPickUp);
                        }

                        this.moveItemToPlayer(itemEntity, player, speed, instant);
                        itemsICanPickUp -= itemEntity.getItem().getCount();
                     }
                  }
               }
            }
         }
      }
   }

   public boolean hasDurabilityLeft(ItemStack stack) {
      return stack.getDamageValue() < stack.getMaxDamage() - 1;
   }

   public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
      int missing = stack.getMaxDamage() - 1 - stack.getDamageValue();
      return Math.min(missing, amount);
   }

   private void moveItemToPlayer(ItemEntity item, Player player, float speed, boolean instant) {
      if (instant) {
         item.setPos(player.getX(), player.getY(), player.getZ());
      } else {
         Vec3 target = player.position();
         Vec3 current = item.position();
         Vec3 velocity = target.subtract(current).normalize().scale(speed);
         item.push(velocity.x, velocity.y, velocity.z);
         item.hurtMarked = true;
      }
   }

   private void moveXpToPlayer(ExperienceOrb orb, Player player, float speed, boolean instant) {
      if (instant) {
         orb.setPos(player.getX(), player.getY(), player.getZ());
      } else {
         Vec3 target = player.position();
         Vec3 current = orb.position();
         Vec3 velocity = target.subtract(current).normalize().scale(speed);
         orb.push(velocity.x, velocity.y, velocity.z);
         orb.hurtMarked = true;
      }
   }

   public static int getMaxRepairSlots(ItemStack stack) {
      return ModConfigs.MAGNET_CONFIG.getBaseRepairSlots() + (getPerk(stack) == MagnetItem.Perk.REINFORCED ? 2 : 0);
   }

   public static int getUsedRepairSlots(ItemStack stack) {
      return stack.getOrCreateTag().getInt("UsedRepairs");
   }

   public static void useRepairSlot(ItemStack stack) {
      CompoundTag tag = stack.getOrCreateTag();
      int current = tag.getInt("UsedRepairs");
      tag.putInt("UsedRepairs", current + 1);
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return toRepair.getItem() instanceof MagnetItem && repair.getItem() == ModItems.REPAIR_CORE;
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   @javax.annotation.Nullable
   private Player getClosestPlayerWithMagnet(ItemEntity item, double radius) {
      List<Player> players = item.getCommandSenderWorld().getEntitiesOfClass(Player.class, item.getBoundingBox().inflate(radius));
      if (players.isEmpty()) {
         return null;
      } else {
         Player closest = players.get(0);
         double distance = radius;

         for (Player player : players) {
            double temp = player.distanceTo(item);
            if (temp < distance && this.hasEnabledMagnetInRange(player, radius)) {
               closest = player;
               distance = temp;
            }
         }

         return closest;
      }
   }

   private boolean hasEnabledMagnetInRange(Player player, double radius) {
      ItemStack magnet = getMagnetInCurio(player);
      if (!magnet.isEmpty()) {
         int range = getUsableStat(magnet, MagnetItem.Stat.RANGE);
         return range >= radius;
      } else {
         return false;
      }
   }

   public static ItemStack getMagnetInCurio(LivingEntity entity) {
      AtomicReference<ItemStack> magnet = new AtomicReference<>(ItemStack.EMPTY);
      CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(c -> c.getStacksHandler("belt").ifPresent(s -> {
         ItemStack stack = s.getStacks().getStackInSlot(0);
         if (stack.getItem() instanceof MagnetItem) {
            magnet.set(stack);
         }
      }));
      return magnet.get();
   }

   public int getMaxDamage(ItemStack stack) {
      return getUsableStat(stack, MagnetItem.Stat.DURABILITY);
   }

   public static int getUsableStat(ItemStack stack, MagnetItem.Stat stat) {
      return getBaseStat(stat) + getStatUpgrade(stack, stat);
   }

   public static int getBaseStat(MagnetItem.Stat stat) {
      return ModConfigs.MAGNET_CONFIG.getUpgrade(stat).getBaseValue();
   }

   public static int getStatUpgrade(ItemStack stack, MagnetItem.Stat stat) {
      CompoundTag c = stack.getOrCreateTag();
      return c.getInt(stat.name);
   }

   public static void setStatUpgrade(ItemStack stack, int statValue, MagnetItem.Stat stat) {
      stack.getOrCreateTag().putInt(stat.name, statValue);
   }

   public static void increaseStatUpgrade(ItemStack stack, MagnetItem.Stat stat, int increase) {
      CompoundTag c = stack.getOrCreateTag();
      c.putInt(stat.name, c.getInt(stat.name) + increase);
   }

   public static int getSturdiness(ItemStack stack) {
      CompoundTag c = stack.getOrCreateTag();
      return 100 - c.getInt("SturdinessDamage");
   }

   public static void decreaseSturdiness(ItemStack stack, int decrease) {
      CompoundTag c = stack.getOrCreateTag();
      c.putInt("SturdinessDamage", c.getInt("SturdinessDamage") + decrease);
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
      Multimap<Attribute, AttributeModifier> attrMap = HashMultimap.create();
      if (this.isIntendedSlot(stack, slotContext.identifier()) && getPerk(stack) == MagnetItem.Perk.CHUNKY) {
         attrMap.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_MODIFIER_ID, "Chunky Health Mod", 0.25, Operation.MULTIPLY_TOTAL));
      }

      return attrMap;
   }

   @Override
   public boolean isIntendedSlot(ItemStack stack, String slot) {
      return "belt".equals(slot);
   }

   public static MagnetItem.Perk getPerk(ItemStack stack) {
      CompoundTag c = stack.getOrCreateTag();
      return MagnetItem.Perk.values()[c.getInt("Perk")];
   }

   public static int getPerkPower(ItemStack stack) {
      CompoundTag c = stack.getOrCreateTag();
      return c.getInt("PerkPower");
   }

   public static void setPerk(ItemStack stack, MagnetItem.Perk perk, int perkValue) {
      stack.getOrCreateTag().putInt("Perk", perk.ordinal());
      stack.getOrCreateTag().putInt("PerkPower", perkValue);
      switch (perk) {
         case HOARD: {
            AttributeGearData data = AttributeGearData.read(stack);
            data.updateAttribute(ModGearAttributes.ITEM_QUANTITY, 0.25F);
            data.write(stack);
            break;
         }
         case TREASURE: {
            AttributeGearData data = AttributeGearData.read(stack);
            data.updateAttribute(ModGearAttributes.ITEM_RARITY, 0.25F);
            data.write(stack);
            break;
         }
         case SHARP: {
            AttributeGearData data = AttributeGearData.read(stack);
            data.updateAttribute(ModGearAttributes.DAMAGE_INCREASE, 0.25F);
            data.write(stack);
            break;
         }
         case SOUL_HUNTING: {
            AttributeGearData data = AttributeGearData.read(stack);
            data.updateAttribute(ModGearAttributes.SOUL_CHANCE, 0.5F);
            data.write(stack);
         }
      }
   }

   public static void addRandomPerk(ItemStack magnet, Random random) {
      MagnetItem.Perk perk = MagnetItem.Perk.values()[1 + random.nextInt(MagnetItem.Perk.values().length - 1)];
      setPerk(magnet, perk, ModConfigs.MAGNET_CONFIG.getPerkUpgrade(perk).getYield(random));
   }

   private boolean shouldPullInstantly(ItemStack stack) {
      return getPerk(stack) == MagnetItem.Perk.TELEPORTING;
   }

   private boolean shouldPullExperience(ItemStack stack) {
      return false;
   }

   private boolean shouldPullItems(ItemStack stack) {
      return true;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onBeforeItemPickup(EntityItemPickupEvent event) {
      if (!event.getPlayer().getLevel().isClientSide()) {
         ItemEntity itemBeingPickedUp = event.getItem();
         if (PULLED_ITEMS_TO_PULLING_PLAYERS.containsKey(itemBeingPickedUp.getUUID()) && !itemBeingPickedUp.isRemoved()) {
            PULLED_ITEM_BEING_PICKED_UP_COUNT = itemBeingPickedUp.getItem().getCount();
         } else {
            PULLED_ITEM_BEING_PICKED_UP_COUNT = 0;
         }
      }
   }

   public static void onAfterItemPickup(Player player, ItemEntity itemEntity) {
      if (!player.getLevel().isClientSide() && PULLED_ITEM_BEING_PICKED_UP_COUNT != 0) {
         int countAfterPickup = itemEntity.isAlive() ? itemEntity.getItem().getCount() : 0;
         if (countAfterPickup < PULLED_ITEM_BEING_PICKED_UP_COUNT) {
            if (countAfterPickup == 0) {
               PULLED_ITEMS_TO_PULLING_PLAYERS.remove(itemEntity.getUUID());
            }

            ItemStack magnet = getMagnetInCurio(player);
            if (!magnet.isEmpty()) {
               handleMagnetDurabilityAndManaCost(player, magnet, PULLED_ITEM_BEING_PICKED_UP_COUNT - countAfterPickup);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onItemActuallyPickedUp(ItemPickupEvent event) {
      ItemEntity itemEntity = event.getOriginalEntity();
      Player player = event.getPlayer();
      UUID pulling = PULLED_ITEMS_TO_PULLING_PLAYERS.get(itemEntity.getUUID());
      if (pulling == player.getUUID()) {
         PULLED_ITEMS_TO_PULLING_PLAYERS.remove(itemEntity.getUUID());
         PULLED_ITEM_BEING_PICKED_UP_COUNT = 0;
         if (!player.level.isClientSide) {
            ItemStack magnet = getMagnetInCurio(player);
            if (!magnet.isEmpty()) {
               handleMagnetDurabilityAndManaCost(player, magnet, event.getStack().getCount());
            }
         }
      }
   }

   private static void handleMagnetDurabilityAndManaCost(Player player, ItemStack magnet, int stackSize) {
      MagnetItem.Perk perk = getPerk(magnet);
      if (perk != MagnetItem.Perk.IMMORTAL || ServerVaults.isInVault(player)) {
         float itemsForOneDurability = ModConfigs.MAGNET_CONFIG.getItemsForOneDurability();
         float amount = stackSize / itemsForOneDurability;
         if (player.level.random.nextFloat() < amount) {
            int i = (int)Math.ceil(amount);
            magnet.hurtAndBreak(i, player, breakEvent -> CuriosApi.getCuriosHelper().onBrokenCurio(new SlotContext("belt", player, 0, false, true)));
         }

         int itemsForOneMana = getUsableStat(magnet, MagnetItem.Stat.MANA_EFFICIENCY);
         float manaCostPerItem = 1.0F / (itemsForOneMana * 20.0F);
         Mana.decrease(player, manaCostPerItem * stackSize);
      }
   }

   public boolean canBeDepleted() {
      return true;
   }

   public static final class MagnetTooltip implements TooltipComponent {
      public final int[] stats;
      public final int perkPower;
      @javax.annotation.Nullable
      public final MagnetItem.Perk perk;

      public MagnetTooltip(int range, int speed, int manaCost, MagnetItem.Perk perk, int perkPower) {
         this.stats = new int[]{range, speed, manaCost};
         this.perk = perk;
         this.perkPower = perkPower;
      }
   }

   public static enum Perk implements StringRepresentable {
      NONE("None"),
      CHUNKY("Chunky"),
      HOARD("Hoard"),
      IMMORTAL("Immortal"),
      REINFORCED("Reinforced"),
      SHARP("Sharp"),
      SOUL_HUNTING("SoulHunting"),
      TELEPORTING("Teleporting"),
      TREASURE("Treasure");

      private final String name;

      private Perk(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum Stat implements StringRepresentable {
      DURABILITY("Durability", "Durability"),
      RANGE("Range", "Range"),
      VELOCITY("Velocity", "Velocity"),
      MANA_EFFICIENCY("ManaEfficiency", "Mana Efficiency");

      private final String name;
      private final String readableName;

      private Stat(String name, String readableName) {
         this.name = name;
         this.readableName = readableName;
      }

      public String getSerializedName() {
         return this.name;
      }

      public String getReadableName() {
         return this.readableName;
      }
   }
}
