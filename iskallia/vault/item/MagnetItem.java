package iskallia.vault.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.block.entity.DemagnetizerTileEntity;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.gear.VaultGearClassification;
import iskallia.vault.gear.VaultGearHelper;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.VaultGearType;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.CuriosGearItem;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.EnderAnchorTrinket;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModParticles;
import iskallia.vault.util.SidedHelper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class MagnetItem extends Item implements VaultGearItem, CuriosGearItem, ICurioItem {
   public static final String SLOT = "belt";
   public static final String BLACKLIST = "PreventMagnetMovement";
   public static final String PULLED = "MagnetPulled";
   private static final String SWITCHED_OFF_TAG = "SwitchedOff";
   public static final Set<String> LEGACY_KEYS = new HashSet<>(
      Arrays.asList("Perk", "PerkPower", "UsedRepairs", "Durability", "Range", "Velocity", "ManaEfficiency", "SturdinessDamage")
   );

   public MagnetItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public boolean isDamageable(ItemStack stack) {
      return GearDataCache.of(stack).getState() == VaultGearState.IDENTIFIED;
   }

   public int getMaxDamage(ItemStack stack) {
      return isLegacy(stack) ? 0 : VaultGearData.read(stack).get(ModGearAttributes.DURABILITY, VaultGearAttributeTypeMerger.intSum());
   }

   public boolean isRepairable(@NotNull ItemStack stack) {
      return false;
   }

   public Component getName(@NotNull ItemStack stack) {
      return isLegacy(stack) ? super.getName(stack) : VaultGearHelper.getDisplayName(stack, super.getName(stack));
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID uuid, ItemStack stack) {
      if (isLegacy(stack)) {
         return ImmutableMultimap.of();
      } else if (context.entity() instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem())) {
         return ImmutableMultimap.of();
      } else {
         return (Multimap<Attribute, AttributeModifier>)("belt".equals(context.identifier())
            ? VaultGearHelper.getModifiers(VaultGearData.read(stack))
            : ImmutableMultimap.of());
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return VaultGearHelper.shouldPlayGearReequipAnimation(oldStack, newStack, slotChanged);
   }

   public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
      return super.getDefaultTooltipHideFlags(stack) | TooltipPart.MODIFIERS.getMask();
   }

   public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
      return Collections.emptyList();
   }

   public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player player, @NotNull InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (isLegacy(heldStack)) {
         return InteractionResultHolder.fail(heldStack);
      } else {
         EquipmentSlot slot = Mob.getEquipmentSlotForItem(heldStack);
         InteractionResultHolder<ItemStack> defaultAction = this.canEquip(heldStack, slot, player)
            ? super.use(world, player, hand)
            : InteractionResultHolder.fail(heldStack);
         return VaultGearHelper.rightClick(world, player, hand, defaultAction);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      if (!isLegacy(stack)) {
         tooltip.addAll(this.createTooltip(stack, GearTooltip.itemTooltip()));
      }
   }

   public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         ItemStack stack = new ItemStack(this);
         VaultGearData data = VaultGearData.read(stack);
         data.setState(VaultGearState.UNIDENTIFIED);
         data.write(stack);
         items.add(stack);
      }
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (isLegacy(stack) && !hasLegacyDataRemoved(stack)) {
         removeLegacyData(stack);
      }

      if (entity instanceof ServerPlayer player) {
         this.vaultGearTick(stack, player);
      }
   }

   @Override
   public VaultRecyclerConfig.RecyclerOutput getOutput(ItemStack input) {
      return ModConfigs.VAULT_RECYCLER.getMagnetRecyclingOutput();
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.player instanceof ServerPlayer player && player.level instanceof ServerLevel world) {
            getMagnet(event.player)
               .ifPresent(
                  stack -> {
                     if (!(stack.getItem() instanceof VaultGearItem gearItem && gearItem.isBroken(stack))) {
                        if (!DemagnetizerTileEntity.hasDemagnetizerAround(event.player)) {
                           VaultGearData data = VaultGearData.read(stack);
                           float range = data.get(ModGearAttributes.RANGE, VaultGearAttributeTypeMerger.floatSum());
                           float speed = data.get(ModGearAttributes.VELOCITY, VaultGearAttributeTypeMerger.floatSum());
                           List<ItemEntity> items = world.getEntitiesOfClass(
                              ItemEntity.class,
                              player.getBoundingBox().inflate(range),
                              entity -> entity.distanceToSqr(player) <= range * range && !entity.getTags().contains("PreventMagnetMovement")
                           );
                           List<ExperienceOrb> orbs = world.getEntitiesOfClass(
                              ExperienceOrb.class,
                              player.getBoundingBox().inflate(range),
                              entity -> entity.distanceToSqr(player) <= range * range && !entity.getTags().contains("PreventMagnetMovement")
                           );
                           TrinketHelper.getTrinkets(player, EnderAnchorTrinket.class).forEach(enderTrinket -> {
                              if (enderTrinket.isUsable(player)) {
                                 teleportToPlayer(player, items);
                                 teleportToPlayer(player, orbs);
                              }
                           });
                           moveToPlayer(player, items, speed);
                           moveToPlayer(player, orbs, speed);
                        }
                     }
                  }
               );
         }
      }
   }

   public static void moveToPlayer(Player player, List<? extends Entity> entities, float speed) {
      for (Entity entity : entities) {
         if (entity instanceof ItemEntity item && allowsNoPickupDelay(item, player)) {
            item.setNoPickUpDelay();
         }

         Vec3 velocity = player.position().subtract(entity.position()).normalize().scale(speed);
         entity.push(velocity.x, velocity.y, velocity.z);
         entity.hurtMarked = true;
         entity.getTags().add("MagnetPulled");
      }
   }

   public static void teleportToPlayer(Player player, List<? extends Entity> entities) {
      for (Entity entity : entities) {
         if (entity instanceof ItemEntity item && allowsNoPickupDelay(item, player)) {
            item.setNoPickUpDelay();
         }

         if (player.level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
               (SimpleParticleType)ModParticles.ENDER_ANCHOR.get(), entity.position().x, entity.position().y + 0.25, entity.position().z, 1, 0.0, 0.0, 0.0, 0.0
            );
         }

         entity.teleportTo(player.position().x, player.position().y, player.position().z);
         entity.hurtMarked = true;
         entity.getTags().add("MagnetPulled");
      }
   }

   private static boolean allowsNoPickupDelay(ItemEntity itemEntity, Player player) {
      if (itemEntity.getThrower() != null && !itemEntity.getThrower().equals(player.getUUID())) {
         for (Player otherPlayer : player.getLevel().players()) {
            if (!otherPlayer.getUUID().equals(player.getUUID()) && otherPlayer.getBoundingBox().inflate(1.0).intersects(itemEntity.getBoundingBox())) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static void onPlayerPickup(Player player, ItemEntity item) {
      if (item.getTags().contains("MagnetPulled")) {
         getMagnet(player)
            .ifPresent(
               stack -> {
                  if (!stack.is(ModItems.SOUL_SHARD)) {
                     stack.hurtAndBreak(
                        1,
                        player,
                        entity -> {
                           if (!entity.isSilent()) {
                              entity.level
                                 .playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    SoundEvents.ITEM_BREAK,
                                    entity.getSoundSource(),
                                    0.8F,
                                    0.8F + entity.level.random.nextFloat() * 0.4F
                                 );
                           }

                           spawnItemParticles(entity, stack, 5);
                        }
                     );
                  }
               }
            );
      }
   }

   private static void spawnItemParticles(Entity entity, ItemStack stack, int count) {
      Random random = new Random();

      for (int i = 0; i < count; i++) {
         Vec3 vec3 = new Vec3((random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3 = vec3.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
         vec3 = vec3.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
         double d0 = -random.nextFloat() * 0.6 - 0.3;
         Vec3 vec31 = new Vec3((random.nextFloat() - 0.5) * 0.3, d0, 0.6);
         vec31 = vec31.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
         vec31 = vec31.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
         vec31 = vec31.add(entity.getX(), entity.getEyeY(), entity.getZ());
         if (entity.level instanceof ServerLevel world) {
            world.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05, vec3.z, 0.0);
         } else {
            entity.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z);
         }
      }
   }

   public static void toggleMagnet(Player player) {
      getMagnet(player, false)
         .ifPresent(
            stack -> {
               if (stack.hasTag() && stack.getTag().contains("SwitchedOff")) {
                  stack.getTag().remove("SwitchedOff");
                  player.displayClientMessage(
                     new TranslatableComponent(
                        "message.the_vault.magnet_toggled", new Object[]{new TranslatableComponent("message.the_vault.on").withStyle(ChatFormatting.GREEN)}
                     ),
                     true
                  );
               } else {
                  stack.getOrCreateTag().putBoolean("SwitchedOff", true);
                  player.displayClientMessage(
                     new TranslatableComponent(
                        "message.the_vault.magnet_toggled", new Object[]{new TranslatableComponent("message.the_vault.off").withStyle(ChatFormatting.RED)}
                     ),
                     true
                  );
               }
            }
         );
   }

   private static boolean isSwitchedOn(ItemStack magnet) {
      return !magnet.hasTag() || !magnet.getTag().contains("SwitchedOff");
   }

   public static Optional<ItemStack> getMagnet(LivingEntity entity) {
      return getMagnet(entity, true);
   }

   public static Optional<ItemStack> getMagnet(LivingEntity entity, boolean switchedOnOnly) {
      return entity.isSpectator()
         ? Optional.empty()
         : CuriosApi.getCuriosHelper().getCuriosHandler(entity).map(handler -> handler).flatMap(handler -> handler.getStacksHandler("belt")).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
               ItemStack stack = handler.getStacks().getStackInSlot(i);
               if (stack.getItem() == ModItems.MAGNET && !isLegacy(stack) && (!switchedOnOnly || isSwitchedOn(stack))) {
                  return stack;
               }
            }

            return null;
         }).map(stack -> {
            VaultGearData data = VaultGearData.read(stack);
            return (ItemStack)(entity instanceof Player player && SidedHelper.getVaultLevel(player) < data.getItemLevel() ? null : stack);
         });
   }

   public static boolean isLegacy(ItemStack stack) {
      return hasLegacyDataRemoved(stack) || hasLegacyData(stack);
   }

   public static boolean hasLegacyData(ItemStack stack) {
      for (String key : stack.getOrCreateTag().getAllKeys()) {
         if (LEGACY_KEYS.contains(key)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasLegacyDataRemoved(ItemStack stack) {
      return stack.getOrCreateTag().getBoolean("Legacy");
   }

   public static void removeLegacyData(ItemStack stack) {
      stack.getOrCreateTag().remove("vaultGearData");
      stack.getOrCreateTag().putBoolean("Legacy", true);
   }

   @NotNull
   @Override
   public VaultGearClassification getClassification(ItemStack stack) {
      return VaultGearClassification.MAGNET;
   }

   @NotNull
   @Override
   public ProficiencyType getCraftingProficiencyType(ItemStack stack) {
      return ProficiencyType.MAGNET;
   }

   @Nonnull
   @Override
   public VaultGearType getGearType(ItemStack stack) {
      return VaultGearType.MAGNET;
   }

   @Nullable
   @Override
   public ResourceLocation getRandomModel(ItemStack stack, Random random) {
      return ModDynamicModels.Magnets.DEFAULT.getId();
   }

   @Override
   public boolean isIntendedSlot(ItemStack stack, String slot) {
      return "belt".equals(slot);
   }
}
