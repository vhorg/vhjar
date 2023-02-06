package iskallia.vault.item.tool;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import iskallia.vault.config.PaxelConfigs;
import iskallia.vault.event.ActiveFlags;
import iskallia.vault.event.InputEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.IConditionalDamageable;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PaxelItem extends DiggerItem implements IConditionalDamageable {
   private static final UUID PAXEL_REACH_ID = UUID.randomUUID();

   public PaxelItem(ResourceLocation id) {
      super(3.0F, -3.0F, PaxelItemTier.INSTANCE, BlockTags.MINEABLE_WITH_PICKAXE, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
      this.setRegistryName(id);
   }

   @Override
   public boolean isImmuneToDamage(ItemStack stack, @Nullable Player player) {
      return false;
   }

   public float getDestroySpeed(ItemStack pStack, BlockState pState) {
      List<PaxelItem.Perk> perks = getPerks(pStack);
      if (isCorrectTool(pState, perks)) {
         boolean isQuick = perks.contains(PaxelItem.Perk.QUICK);
         return getUsableStat(pStack, PaxelItem.Stat.MINING_SPEED) * (isQuick ? 1.25F : 1.0F);
      } else {
         return 1.0F;
      }
   }

   private static boolean isCorrectTool(BlockState pState, List<PaxelItem.Perk> perks) {
      return pState.is(BlockTags.MINEABLE_WITH_PICKAXE)
         || pState.is(BlockTags.MINEABLE_WITH_AXE) && perks.contains(PaxelItem.Perk.AXING)
         || pState.is(BlockTags.MINEABLE_WITH_SHOVEL) && perks.contains(PaxelItem.Perk.SHOVELING)
         || pState.is(BlockTags.MINEABLE_WITH_HOE) && perks.contains(PaxelItem.Perk.FARMING);
   }

   public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
      return isCorrectTool(state, getPerks(stack)) && TierSortingRegistry.isCorrectTierForDrops(this.getTier(), state);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment == Enchantments.MENDING ? false : super.canApplyAtEnchantingTable(stack, enchantment);
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING) ? false : super.isBookEnchantable(stack, book);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (worldIn != null) {
         int level = getPaxelLevel(stack);
         tooltip.add(new TextComponent("Level " + ChatFormatting.YELLOW + level));
         addRepairTooltip(tooltip, getUsedRepairSlots(stack), getMaxRepairSlots(stack));
         int sockets = getSockets(stack);
         if (sockets != 0) {
            tooltip.add(new TextComponent("Sockets: ").append(tooltipDots(sockets, ChatFormatting.GRAY)));
         }

         tooltip.add(new TextComponent(" "));

         for (PaxelItem.Stat s : PaxelItem.Stat.values()) {
            float value = getStatUpgrade(stack, s);
            if (value != 0.0F) {
               PaxelConfigs.Upgrade upgradeCfg = ModConfigs.PAXEL_CONFIGS.getUpgrade(s);
               String valueStr = upgradeCfg.formatValue(value);
               MutableComponent component = new TextComponent(s.getReadableName() + (value > 0.0F ? " +" : " ") + valueStr)
                  .withStyle(Style.EMPTY.withColor(ModConfigs.PAXEL_CONFIGS.getStatColor(s)));
               if (InputEvents.isShiftDown()) {
                  component.append(new TextComponent(" " + ChatFormatting.DARK_GRAY + upgradeCfg.getAdvancedTooltip()));
               }

               tooltip.add(component);
            }
         }

         List<PaxelItem.Perk> perks = getPerks(stack);
         if (perks.contains(PaxelItem.Perk.HAMMERING) || perks.contains(PaxelItem.Perk.EXCAVATING) || perks.contains(PaxelItem.Perk.SHATTERING)) {
            tooltip.add(new TextComponent(" "));
            tooltip.add(new TextComponent("Use Shift+Arrow Keys to change Mining Area Offset.").withStyle(ChatFormatting.DARK_GRAY));
         }

         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }

   public static int getPaxelLevel(ItemStack stack) {
      return (100 - getSturdiness(stack)) / ModConfigs.MAGNET_CONFIG.getSturdinessDecrement();
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      int durability = this.getMaxDamage(stack);
      float miningSpeed = getUsableStat(stack, PaxelItem.Stat.MINING_SPEED);
      float reach = getUsableStat(stack, PaxelItem.Stat.REACH);
      float copiously = getUsableStat(stack, PaxelItem.Stat.COPIOUSLY);
      List<PaxelItem.Perk> perks = getPerks(stack);
      return Optional.of(new PaxelItem.PaxelTooltip(durability, miningSpeed, reach, copiously, perks));
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

   public static int getMaxRepairSlots(ItemStack stack) {
      return ModConfigs.PAXEL_CONFIGS.getTierValues(stack).getBaseRepairSlots() + (getPerks(stack).contains(PaxelItem.Perk.REINFORCED) ? 2 : 0);
   }

   public static int getUsedRepairSlots(ItemStack stack) {
      return stack.getOrCreateTag().getInt("UsedRepairs");
   }

   public static void useRepairSlot(ItemStack stack) {
      CompoundTag tag = stack.getOrCreateTag();
      int current = tag.getInt("UsedRepairs");
      tag.putInt("UsedRepairs", current + 1);
   }

   public static int getSockets(ItemStack stack) {
      return stack.getOrCreateTag().getInt("Sockets");
   }

   public static void setSockets(ItemStack stack, int sockets) {
      stack.getOrCreateTag().putInt("Sockets", sockets);
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return toRepair.getItem() instanceof PaxelItem && repair.getItem() == ModItems.REPAIR_CORE;
   }

   public boolean isRepairable(ItemStack stack) {
      return false;
   }

   public int getMaxDamage(ItemStack stack) {
      int durability = Mth.ceil(getUsableStat(stack, PaxelItem.Stat.DURABILITY) + getReinforcedDurability(stack))
         + ModConfigs.PAXEL_CONFIGS.getTierValues(stack).getAdditionalDurability();
      if (getPerks(stack).contains(PaxelItem.Perk.STURDY)) {
         durability = (int)(durability * 1.5F);
      }

      return durability;
   }

   public static float getUsableStat(ItemStack stack, PaxelItem.Stat stat) {
      return getBaseStat(stat) + getStatUpgrade(stack, stat);
   }

   public static float getBaseStat(PaxelItem.Stat stat) {
      return ModConfigs.PAXEL_CONFIGS.getUpgrade(stat).getBaseValue();
   }

   public static float getStatUpgrade(ItemStack stack, PaxelItem.Stat stat) {
      CompoundTag c = stack.getOrCreateTag();
      return c.getFloat(stat.name);
   }

   public static void increaseStatUpgrade(ItemStack stack, PaxelItem.Stat stat, float increase) {
      CompoundTag c = stack.getOrCreateTag();
      c.putFloat(stat.name, c.getFloat(stat.name) + increase);
   }

   public static void setLevel(ItemStack stack, int level) {
      stack.getOrCreateTag().putInt("Level", level);
   }

   public static int getLevel(ItemStack stack) {
      return stack.getOrCreateTag().getInt("Level");
   }

   public static int getSturdiness(ItemStack stack) {
      return 100 - getLevel(stack) * ModConfigs.PAXEL_CONFIGS.getTierValues(stack).getSturdinessDecrement();
   }

   public static void addOffset(ItemStack stack, Vec2 offset) {
      int range = 0;
      List<PaxelItem.Perk> perks = getPerks(stack);
      if (perks.contains(PaxelItem.Perk.SHATTERING)) {
         range = PaxelItem.Perk.SHATTERING.digRadius;
      } else if (perks.contains(PaxelItem.Perk.EXCAVATING)) {
         range = PaxelItem.Perk.EXCAVATING.digRadius;
      } else if (perks.contains(PaxelItem.Perk.HAMMERING)) {
         range = PaxelItem.Perk.HAMMERING.digRadius;
      }

      if (range != 0) {
         range = (range - 1) / 2;
         Vec2 current = getOffset(stack);
         float x = Math.max((float)(-range), Math.min(current.x + offset.x, (float)range));
         float y = Math.max((float)(-range), Math.min(current.y + offset.y, (float)range));
         setOffset(stack, new Vec2(x, y));
      }
   }

   public static void setOffset(ItemStack stack, Vec2 offset) {
      stack.getOrCreateTag().put("Offset", NBTHelper.serializeVec2(offset));
   }

   public static Vec2 getOffset(ItemStack stack) {
      CompoundTag tag = stack.getOrCreateTag();
      if (tag.contains("Offset")) {
         CompoundTag offsetTag = tag.getCompound("Offset");
         return NBTHelper.deserializeVec2(offsetTag);
      } else {
         return Vec2.ZERO;
      }
   }

   public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
      return (Multimap<Attribute, AttributeModifier>)(ModConfigs.isInitialized() && slot == EquipmentSlot.MAINHAND
         ? ImmutableMultimap.of(
            (Attribute)ForgeMod.REACH_DISTANCE.get(),
            new AttributeModifier(PAXEL_REACH_ID, "PaxelReach", getUsableStat(stack, PaxelItem.Stat.REACH), Operation.ADDITION)
         )
         : super.getAttributeModifiers(slot, stack));
   }

   public static float getReinforcedDurability(ItemStack stack) {
      return stack.getOrCreateTag().getFloat("ReinforcedDurability");
   }

   public static void setReinforcedDurability(ItemStack stack, float durability) {
      stack.getOrCreateTag().putFloat("ReinforcedDurability", durability);
   }

   public static List<PaxelItem.Perk> getPerks(ItemStack stack) {
      CompoundTag c = stack.getOrCreateTag();
      return Arrays.stream(c.getIntArray("Perks")).mapToObj(i -> PaxelItem.Perk.values()[i]).toList();
   }

   public static void addPerk(ItemStack stack, PaxelItem.Perk perk) {
      CompoundTag tag = stack.getOrCreateTag();
      ArrayList<Integer> perks = new ArrayList<>();

      for (int i : tag.getIntArray("Perks")) {
         perks.add(i);
      }

      perks.add(perk.ordinal());
      tag.putIntArray("Perks", perks);
   }

   public static boolean canAddPerk(ItemStack stack, PaxelItem.Perk perk) {
      List<PaxelItem.Perk> perks = getPerks(stack);
      return perk.digRadius != 0 && perks.stream().anyMatch(p -> p.digRadius != 0) ? false : !perks.contains(perk);
   }

   public boolean canBeDepleted() {
      return true;
   }

   public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
      if (ActiveFlags.IS_AOE_MINING.isSet()) {
         return false;
      } else {
         List<PaxelItem.Perk> perks = getPerks(stack);
         int range;
         if (perks.contains(PaxelItem.Perk.SHATTERING)) {
            range = PaxelItem.Perk.SHATTERING.digRadius;
         } else if (perks.contains(PaxelItem.Perk.EXCAVATING)) {
            range = PaxelItem.Perk.EXCAVATING.digRadius;
         } else {
            if (!perks.contains(PaxelItem.Perk.HAMMERING)) {
               return false;
            }

            range = PaxelItem.Perk.HAMMERING.digRadius;
         }

         range = (range - 1) / 2;
         HitResult hitResult = player.pick(player.getReachDistance(), 1.0F, false);
         if (!player.level.isClientSide && hitResult.getType() == Type.BLOCK) {
            Direction face = ((BlockHitResult)hitResult).getDirection();
            this.areaDig(range, player, stack, pos, pos, face);
         }

         return false;
      }
   }

   private void areaDig(int range, Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
      Level level = player.level;
      if (level.isAreaLoaded(pos, range)) {
         Direction ort1 = side != Direction.UP && side != Direction.DOWN ? side.getCounterClockWise() : player.getDirection().getClockWise();
         Direction ort2 = side != Direction.UP && side != Direction.DOWN
            ? Direction.UP
            : (side == Direction.UP ? player.getDirection() : player.getDirection().getOpposite());
         Vec2 offset = getOffset(stack);
         pos = pos.relative(ort1, (int)offset.x);
         pos = pos.relative(ort2, (int)offset.y);
         float destroySpeed = player.level.getBlockState(pos).getDestroySpeed(level, pos);
         Iterator<MutableBlockPos> it = BlockPos.spiralAround(pos, range, ort1, ort2).iterator();

         while (it.hasNext() && !stack.isEmpty()) {
            BlockPos currentPos = it.next().immutable();
            BlockState state = level.getBlockState(currentPos);
            if (state.getBlock() == ModBlocks.VAULT_GLASS || state.getBlock() == ModBlocks.VAULT_BEDROCK) {
               return;
            }

            this.destroyBlock((ServerLevel)level, (ServerPlayer)player, currentPos, destroySpeed);
         }

         return;
      }
   }

   public void destroyBlock(ServerLevel level, ServerPlayer player, BlockPos pos, float originDestroySpeed) {
      BlockState blockState = level.getBlockState(pos);
      float currentDestroySpeed = blockState.getDestroySpeed(level, pos);
      if (!blockState.isAir() && !(currentDestroySpeed < 0.0F)) {
         ActiveFlags.IS_AOE_MINING
            .runIfNotSet(
               () -> {
                  if (!player.isCreative()
                     && currentDestroySpeed <= originDestroySpeed + ModConfigs.PAXEL_CONFIGS.getMaxHardnessAboveTarget()
                     && player.gameMode.destroyBlock(pos)) {
                     SoundType soundtype = blockState.getSoundType(level, pos, null);
                     level.playSound(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        soundtype.getBreakSound(),
                        SoundSource.BLOCKS,
                        (soundtype.getVolume() + 1.0F) / 2.0F * 0.1F,
                        soundtype.getPitch() * 0.8F
                     );
                  }
               }
            );
      }
   }

   private static void addRepairTooltip(List<Component> tooltip, int usedRepairs, int totalRepairs) {
      if (totalRepairs > 0) {
         int remaining = totalRepairs - usedRepairs;
         tooltip.add(new TextComponent("Repairs: ").append(tooltipDots(usedRepairs, ChatFormatting.YELLOW)).append(tooltipDots(remaining, ChatFormatting.GRAY)));
      }
   }

   private static Component tooltipDots(int amount, ChatFormatting formatting) {
      return new TextComponent("⬢ ".repeat(Math.max(0, amount))).withStyle(formatting);
   }

   public static final class PaxelTooltip implements TooltipComponent {
      public final PaxelItem.Stat[] stats = new PaxelItem.Stat[]{
         PaxelItem.Stat.DURABILITY, PaxelItem.Stat.REACH, PaxelItem.Stat.MINING_SPEED, PaxelItem.Stat.COPIOUSLY
      };
      public final float[] statValues;
      public final TreeSet<PaxelItem.Perk> perks;

      public PaxelTooltip(float durability, float miningSpeed, float reach, float copiously, List<PaxelItem.Perk> perks) {
         this.statValues = new float[]{durability, reach, miningSpeed, copiously};
         this.perks = new TreeSet<>(perks);
      }
   }

   public static enum Perk implements StringRepresentable {
      QUICK("Quick"),
      IMMORTAL("Immortal"),
      STURDY("Sturdy"),
      HAMMERING("Hammering", 3),
      EXCAVATING("Excavating", 5),
      SHATTERING("Shattering", 7),
      SMELTING("Smelting"),
      PULVERISING("Pulverising"),
      REINFORCED("Reinforced"),
      AXING("Axing"),
      SHOVELING("Shoveling"),
      FARMING("Farming");

      private final int digRadius;
      private final String name;

      private Perk(String name, int digRadius) {
         this.name = name;
         this.digRadius = digRadius;
      }

      private Perk(String name) {
         this(name, 0);
      }

      public String getSerializedName() {
         return this.name;
      }

      public int getDigRadius() {
         return this.digRadius;
      }
   }

   public static enum Stat implements StringRepresentable {
      DURABILITY("Durability", "Durability"),
      REACH("Reach", "Reach"),
      MINING_SPEED("MiningSpeed", "Mining Speed"),
      COPIOUSLY("Copiously", "Copiously");

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
