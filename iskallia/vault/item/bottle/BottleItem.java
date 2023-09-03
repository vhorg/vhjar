package iskallia.vault.item.bottle;

import com.google.common.base.Functions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.talent.type.PrudentTalent;
import iskallia.vault.skill.tree.TalentTree;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.SidedHelper;
import iskallia.vault.world.data.InventorySnapshotData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.data.ServerVaults;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class BottleItem extends Item {
   public static final String TYPE = "type";
   public static final String RECHARGE = "recharge";
   public static final String VAULT = "vault";
   public static final String PROGRESS = "progress";
   public static final String CHARGES = "charges";
   public static final String EFFECT = "effect";
   private static final float CHARGE_NOTIFICATION_DURATION = 20.0F;
   private static final float PROGRESS_NOTIFICATION_DURATION = 40.0F;
   private static ItemStack notifiableBottleStack = ItemStack.EMPTY;
   private static long chargeNotificationTime = -1L;
   private static long progressNotificationTime = -1L;
   private static int progressPoints = 0;

   public BottleItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }

   public static int getEffectColor(ItemStack stack) {
      return getEffect(stack).map(effect -> effect.getColor()).orElse(16150747);
   }

   public static void onChestOpen(ItemStack stack, ServerPlayer player, BlockState state) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && ModConfigs.POTION.getPotion(type).getCharges() > getCharges(stack) && recharge == BottleItem.Recharge.CHESTS) {
         if (state.getBlock() instanceof VaultChestBlock) {
            CompoundTag nbt = stack.getOrCreateTag();
            int newProgress = nbt.getInt("progress") + 1;
            nbt.putInt("progress", newProgress);
            int vaultLevel = SidedHelper.getVaultLevel(player);
            int progressRequired = ModConfigs.POTION.getPotion(type).getChestRecharge(vaultLevel);
            if (progressRequired <= newProgress && nbt.getInt("charges") < ModConfigs.POTION.getPotion(type).getCharges()) {
               nbt.putInt("charges", nbt.getInt("charges") + 1);
               nbt.putInt("progress", newProgress - progressRequired);
            }
         }
      }
   }

   public static void addCharges(ItemStack bottle, int charges) {
      int currentCharges = bottle.getOrCreateTag().getInt("charges");
      getType(bottle).ifPresent(type -> {
         int maxCharges = ModConfigs.POTION.getPotion(type).getCharges();
         if (currentCharges < maxCharges) {
            bottle.getOrCreateTag().putInt("charges", Math.min(maxCharges, currentCharges + charges));
         }
      });
   }

   public static void setActive(Vault vault, ServerPlayer player) {
      getAnyInactive(player).ifPresent(stack -> {
         CompoundTag nbt = stack.getOrCreateTag();
         nbt.putString("vault", vault.get(Vault.ID).toString());
         nbt.remove("progress");
         getType(stack).ifPresent(type -> stack.getOrCreateTag().putInt("charges", ModConfigs.POTION.getPotion(type).getCharges()));
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static void onAfterGuiItemRender(ItemStack stack, PoseStack poseStack) {
      if (shouldRenderNotification(stack)) {
         poseStack.popPose();
      }
   }

   public boolean isDamageable(ItemStack stack) {
      return getType(stack).isPresent();
   }

   public int getMaxDamage(ItemStack stack) {
      return ModConfigs.POTION.getPotion(getType(stack).orElse(BottleItem.Type.VIAL)).getCharges();
   }

   public int getDamage(ItemStack stack) {
      return stack.hasTag() ? this.getMaxDamage(stack) - stack.getOrCreateTag().getInt("charges") : 0;
   }

   public boolean isBarVisible(ItemStack pStack) {
      return false;
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity entity) {
      if (!(entity instanceof ServerPlayer player)) {
         return stack;
      } else {
         Vault vault = ServerVaults.get(player.level).orElse(null);
         if ((vault == null || isActive(vault, stack)) && stack.getOrCreateTag().getInt("charges") > 0) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            getEffect(stack).ifPresent(effect -> effect.trigger(player));
            getType(stack).ifPresent(type -> entity.heal(ModConfigs.POTION.getPotion(type).getHealing()));
            consumeCharge(stack, player);
            world.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
         }

         return stack;
      }
   }

   public static Optional<BottleEffect> getEffect(ItemStack bottle) {
      return bottle.hasTag() && bottle.getTag().contains("effect") ? BottleEffectManager.deserialize(bottle.getTag().getCompound("effect")) : Optional.empty();
   }

   public static void setEffect(ItemStack bottle, BottleEffect effect) {
      bottle.getOrCreateTag().put("effect", effect.serialize());
   }

   public static void consumeCharge(ItemStack stack, ServerPlayer player) {
      float probability = 0.0F;
      TalentTree talents = PlayerTalentsData.get((ServerLevel)player.level).getTalents(player);

      for (PrudentTalent talent : talents.getAll(PrudentTalent.class, Skill::isUnlocked)) {
         probability += talent.getProbability();
      }

      if (player.level.getRandom().nextFloat() >= probability) {
         int currentCharges = stack.getOrCreateTag().getInt("charges");
         stack.getOrCreateTag().putInt("charges", currentCharges - 1);
      }
   }

   public int getUseDuration(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().getInt("charges") > 0 ? 32 : Integer.MAX_VALUE;
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack stack) {
      return UseAnim.DRINK;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, @NotNull InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      int currentCharges = stack.getOrCreateTag().getInt("charges");
      return currentCharges > 0 ? ItemUtils.startUsingInstantly(world, player, hand) : super.use(world, player, hand);
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      return new TranslatableComponent(
            this.getDescriptionId() + ".recharge." + getRecharge(stack).orElse(BottleItem.Recharge.TIME).getName().toLowerCase(Locale.ROOT)
         )
         .append(
            new TranslatableComponent(getType(stack).map(type -> this.getDescriptionId() + "." + type.getName()).orElseGet(() -> super.getDescriptionId()))
         )
         .setStyle(Style.EMPTY.withColor(getColor(stack)));
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@NotNull ItemStack stack, Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, world, tooltip, flag);
      if (ModConfigs.POTION != null) {
         getType(stack)
            .ifPresent(
               type -> {
                  getRecharge(stack).ifPresent(recharge -> {
                     switch (recharge) {
                        case TIME:
                           tooltip.add(new TextComponent("Passively restores charges while inside a vault").withStyle(ChatFormatting.GRAY));
                           break;
                        case MOBS:
                           tooltip.add(new TextComponent("Kill vault mobs to restore charges").withStyle(ChatFormatting.GRAY));
                           break;
                        case CHESTS:
                           tooltip.add(new TextComponent("Loot vault chests to restore charges").withStyle(ChatFormatting.GRAY));
                     }
                  });
                  this.addProgressTooltip(stack, tooltip, type);
                  tooltip.add(
                     new TextComponent("Consume a ")
                        .append(new TextComponent("charge").withStyle(Style.EMPTY.withColor(16777215)))
                        .append(new TextComponent(" to"))
                        .withStyle(Style.EMPTY.withColor(13619151))
                  );
                  tooltip.add(
                     new TextComponent(" • Heal ")
                        .append(new TextComponent(String.valueOf(ModConfigs.POTION.getPotion(type).getHealing())))
                        .append(new TextComponent(" Hit Points"))
                        .withStyle(ChatFormatting.GREEN)
                  );
                  getEffect(stack).ifPresent(effect -> tooltip.add(new TextComponent(" • ").append(effect.getTooltip())));
               }
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void addProgressTooltip(ItemStack stack, List<Component> tooltip, BottleItem.Type type) {
      if (ModConfigs.POTION.getPotion(type).getCharges() > getCharges(stack)) {
         getRecharge(stack)
            .ifPresent(
               recharge -> {
                  switch (recharge) {
                     case TIME:
                        int remainingTime = ModConfigs.POTION.getPotion(type).getTimeRecharge() - this.getProgress(stack);
                        String time = remainingTime / 20 / 60 + "m " + remainingTime / 20 % 60 + "s";
                        tooltip.add(
                           new TextComponent("Restores a charge in ")
                              .append(new TextComponent(time).withStyle(Style.EMPTY.withColor(16777215)))
                              .setStyle(Style.EMPTY.withColor(13619151))
                        );
                        break;
                     case MOBS:
                        tooltip.add(
                           new TextComponent("Kill ")
                              .append(
                                 new TextComponent(
                                       this.getProgress(stack)
                                          + " / "
                                          + ModConfigs.POTION.getPotion(type).getMobRecharge(SidedHelper.getVaultLevel(Minecraft.getInstance().player))
                                    )
                                    .withStyle(Style.EMPTY.withColor(16777215))
                              )
                              .append(new TextComponent(" vault mobs to recharge"))
                              .setStyle(Style.EMPTY.withColor(13619151))
                        );
                        break;
                     case CHESTS:
                        tooltip.add(
                           new TextComponent("Loot ")
                              .append(
                                 new TextComponent(
                                       this.getProgress(stack)
                                          + " / "
                                          + ModConfigs.POTION.getPotion(type).getChestRecharge(SidedHelper.getVaultLevel(Minecraft.getInstance().player))
                                    )
                                    .withStyle(Style.EMPTY.withColor(16777215))
                              )
                              .append(new TextComponent(" vault chests to recharge"))
                              .setStyle(Style.EMPTY.withColor(13619151))
                        );
                  }
               }
            );
      } else {
         tooltip.add(new TextComponent("Fully charged").withStyle(ChatFormatting.GRAY));
      }
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
      super.inventoryTick(stack, world, entity, itemSlot, isSelected);
      if (entity instanceof ServerPlayer player) {
         if (stack.hasTag() && stack.getTag().contains("vault")) {
            Vault vault = ServerVaults.get(UUID.fromString(stack.getOrCreateTag().getString("vault"))).orElse(null);
            if (vault == null || !vault.get(Vault.LISTENERS).contains(entity.getUUID())) {
               stack.getOrCreateTag().remove("vault");
               getType(stack).ifPresent(type -> stack.getOrCreateTag().putInt("charges", ModConfigs.POTION.getPotion(type).getCharges()));
            }
         }
      } else {
         if (this.isActive(stack) && getRecharge(stack).map(r -> r != BottleItem.Recharge.TIME).orElse(false) && notifiableBottleStack != stack) {
            if (!notifiableBottleStack.isEmpty()) {
               if (getCharges(stack) > getCharges(notifiableBottleStack)) {
                  chargeNotificationTime = world.getGameTime();
               }

               if (this.getProgress(stack) > this.getProgress(notifiableBottleStack)) {
                  progressPoints = progressPoints + (this.getProgress(stack) - this.getProgress(notifiableBottleStack));
                  progressNotificationTime = world.getGameTime();
               }
            }

            notifiableBottleStack = stack;
         }

         if ((float)Math.abs(chargeNotificationTime - world.getGameTime()) > 20.0F) {
            chargeNotificationTime = -1L;
         }

         if ((float)Math.abs(progressNotificationTime - world.getGameTime()) > 40.0F) {
            progressNotificationTime = -1L;
            progressPoints = 0;
         }
      }
   }

   private int getProgress(ItemStack stack) {
      return stack.getOrCreateTag().getInt("progress");
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged;
   }

   public void fillItemCategory(@NotNull CreativeModeTab category, @NotNull NonNullList<ItemStack> items) {
      if (this.allowdedIn(category)) {
         for (BottleItem.Type type : BottleItem.Type.values()) {
            for (BottleItem.Recharge recharge : BottleItem.Recharge.values()) {
               items.add(create(type, recharge));
            }
         }
      }
   }

   public static ItemStack create(BottleItem.Type type, BottleItem.Recharge recharge) {
      ItemStack stack = new ItemStack(ModItems.BOTTLE);
      if (type != null) {
         stack.getOrCreateTag().putString("type", type.getName());
         stack.getOrCreateTag().putInt("charges", 6);
      }

      if (recharge != null) {
         stack.getOrCreateTag().putString("recharge", recharge.getName());
      }

      return stack;
   }

   public static Optional<BottleItem.Type> getType(ItemStack stack) {
      if (stack != null && stack.hasTag()) {
         String raw = stack.getOrCreateTag().getString("type");
         return Optional.ofNullable(BottleItem.Type.fromString(raw));
      } else {
         return Optional.empty();
      }
   }

   public static Optional<BottleItem.Recharge> getRecharge(ItemStack stack) {
      if (stack != null && stack.hasTag()) {
         String raw = stack.getOrCreateTag().getString("recharge");
         return Optional.ofNullable(BottleItem.Recharge.fromString(raw));
      } else {
         return Optional.empty();
      }
   }

   public static int getCharges(ItemStack stack) {
      return stack != null && stack.hasTag() ? stack.getOrCreateTag().getInt("charges") : 0;
   }

   public static boolean isActive(Vault vault, ItemStack stack) {
      if (stack.getItem() instanceof BottleItem && stack.hasTag()) {
         String uuid = stack.getOrCreateTag().getString("vault");
         return vault.get(Vault.ID).toString().equals(uuid);
      } else {
         return false;
      }
   }

   private boolean isActive(ItemStack stack) {
      return stack.getItem() instanceof BottleItem && stack.hasTag() ? stack.getOrCreateTag().contains("vault") : false;
   }

   public static Optional<ItemStack> getActive(Vault vault, ServerPlayer player) {
      int size = ((InventorySnapshotData.InventoryAccessor)player.getInventory()).getSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (isActive(vault, stack)) {
            return Optional.of(stack);
         }
      }

      return Optional.empty();
   }

   public static Optional<ItemStack> getAnyInactive(ServerPlayer player) {
      int size = ((InventorySnapshotData.InventoryAccessor)player.getInventory()).getSize();

      for (int i = 0; i < size; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (stack.getItem() instanceof BottleItem && stack.hasTag() && !stack.getOrCreateTag().contains("vault")) {
            return Optional.of(stack);
         }
      }

      return Optional.empty();
   }

   public static void onMobKill(ItemStack stack, ServerPlayer player, Entity entityKilled) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null
         && ModConfigs.POTION.getPotion(type).getCharges() > getCharges(stack)
         && recharge == BottleItem.Recharge.MOBS
         && entityKilled instanceof LivingEntity livingKilled) {
         CompoundTag nbt = stack.getOrCreateTag();
         int newProgress = nbt.getInt("progress") + EntityHelper.getEntityValue(livingKilled);
         nbt.putInt("progress", newProgress);
         int vaultLevel = SidedHelper.getVaultLevel(player);
         int progressRequired = ModConfigs.POTION.getPotion(type).getMobRecharge(vaultLevel);
         if (progressRequired <= newProgress && nbt.getInt("charges") < ModConfigs.POTION.getPotion(type).getCharges()) {
            nbt.putInt("charges", nbt.getInt("charges") + 1);
            nbt.putInt("progress", newProgress - progressRequired);
         }
      }
   }

   public static void onTimeTick(ItemStack stack) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && ModConfigs.POTION.getPotion(type).getCharges() > getCharges(stack) && recharge == BottleItem.Recharge.TIME) {
         CompoundTag nbt = stack.getOrCreateTag();
         int value = nbt.getInt("progress") + 1;
         nbt.putInt("progress", value);
         if (value % ModConfigs.POTION.getPotion(type).getTimeRecharge() == 0 && nbt.getInt("charges") < ModConfigs.POTION.getPotion(type).getCharges()) {
            nbt.putInt("charges", nbt.getInt("charges") + 1);
            nbt.putInt("progress", 0);
         }
      }
   }

   public static int getColor(ItemStack stack) {
      BottleItem.Type type = getType(stack).orElse(null);
      BottleItem.Recharge recharge = getRecharge(stack).orElse(null);
      if (type != null && recharge != null) {
         return switch (recharge) {
            case TIME -> 1004218;
            case MOBS -> 16720896;
            case CHESTS -> 16766720;
         };
      } else {
         return 16777215;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void renderBottleProgressNotification(Font font, ItemStack stack, int xPosition, int yPosition) {
      if (shouldRenderProgressNotification(stack)) {
         PoseStack poseStack = new PoseStack();
         poseStack.translate(0.0, 0.0, Minecraft.getInstance().getItemRenderer().blitOffset + 200.0F);
         float scale = 0.8F;
         poseStack.scale(scale, scale, 1.0F);
         BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         String s = "+" + progressPoints;
         int color = DyeColor.LIME.getTextColor();
         Minecraft.getInstance()
            .font
            .drawInBatch(
               s,
               (xPosition + 19 - 2 - font.width(s) * scale) / scale,
               (yPosition + (1.0F / (scale * scale) - 1.0F)) / scale,
               color,
               true,
               poseStack.last().pose(),
               bufferSource,
               false,
               0,
               15728880
            );
         bufferSource.endBatch();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void onBeforeGuiItemRender(ItemStack stack, PoseStack poseStack) {
      if (shouldRenderNotification(stack)) {
         poseStack.pushPose();
         float additionalScale = 0.5F;
         long timePassedSinceNotification = (Minecraft.getInstance().level.getGameTime() - chargeNotificationTime) % 20L;
         float partial = 1.0F - Math.abs(((float)timePassedSinceNotification - 10.0F) / 10.0F);
         float scale = 1.0F + additionalScale * partial;
         poseStack.scale(scale, scale, scale);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean shouldRenderNotification(ItemStack stack) {
      return stack == notifiableBottleStack && chargeNotificationTime > 0L;
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean shouldRenderProgressNotification(ItemStack stack) {
      return stack == notifiableBottleStack && progressNotificationTime > 0L;
   }

   public static enum Recharge {
      TIME,
      MOBS,
      CHESTS;

      private static final Map<String, BottleItem.Recharge> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(BottleItem.Recharge::getName, Functions.identity()));

      public String getName() {
         return this.name().toLowerCase();
      }

      public static BottleItem.Recharge fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase());
      }
   }

   public static enum Type {
      VIAL,
      POTION,
      MIXTURE,
      BREW;

      private static final Map<String, BottleItem.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(BottleItem.Type::getName, Functions.identity()));

      public String getName() {
         return this.name().toLowerCase();
      }

      public static BottleItem.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase());
      }
   }
}
