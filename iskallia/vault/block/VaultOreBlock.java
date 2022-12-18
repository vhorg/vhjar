package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.item.VaultOreBlockItem;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;

public class VaultOreBlock extends OreBlock {
   public static final List<VaultOreBlock> ALL = new ArrayList<>();
   private static final ItemPredicate HAS_SILK_TOUCH = net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
      .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, Ints.atLeast(1)))
      .build();
   public static final BooleanProperty GENERATED = BooleanProperty.create("generated");
   public static final EnumProperty<VaultOreBlock.Type> TYPE = EnumProperty.create("type", VaultOreBlock.Type.class);
   private final Item associatedGem;

   public VaultOreBlock(@Nonnull Item associatedGem) {
      super(
         Properties.of(Material.STONE, MaterialColor.DIAMOND)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 9)
            .strength(4.6F, 3.0F)
            .sound(ModSounds.VAULT_GET_SOUND_TYPE)
      );
      this.associatedGem = associatedGem;
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(GENERATED, false)).setValue(TYPE, VaultOreBlock.Type.STONE)
      );
      ALL.add(this);
   }

   @Nonnull
   public Item getAssociatedGem() {
      return this.associatedGem;
   }

   public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
      return !state.getValue(GENERATED) && silktouch > 0 ? 0 : Mth.nextInt(this.RANDOM, 3, 7);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{GENERATED}).add(new Property[]{TYPE});
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      List<ItemStack> drops = new ArrayList<>();
      ItemStack tool = (ItemStack)builder.getOptionalParameter(LootContextParams.TOOL);
      if (tool != null && HAS_SILK_TOUCH.matches(tool)) {
         drops.add(VaultOreBlockItem.fromType(this, (VaultOreBlock.Type)state.getValue(TYPE)));
      } else {
         drops = super.getDrops(state, builder);
      }

      if ((Boolean)state.getValue(GENERATED)) {
         LootContext ctx = builder.create(LootContextParamSets.EMPTY);
         if (ctx.hasParam(LootContextParams.TOOL)) {
            tool = (ItemStack)ctx.getParam(LootContextParams.TOOL);
            if (!tool.isEmpty() && tool.getItem() instanceof PaxelItem) {
               float copiouslyChance = PaxelItem.getUsableStat(tool, PaxelItem.Stat.COPIOUSLY);
               if (copiouslyChance > 0.0F && this.RANDOM.nextFloat() < copiouslyChance / 100.0F) {
                  drops.addAll(super.getDrops(state, builder));
               }
            }
         }
      }

      return drops;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = this.defaultBlockState();
      CompoundTag nbt = context.getItemInHand().getTag();
      if (nbt != null) {
         VaultOreBlock.Type type = VaultOreBlock.Type.fromString(nbt.getString("type"));
         if (type != null) {
            blockState = (BlockState)blockState.setValue(TYPE, type);
         }
      }

      return blockState;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (VaultOreBlock.Type type : VaultOreBlock.Type.values()) {
         items.add(VaultOreBlockItem.fromType(this, type));
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("type", ((VaultOreBlock.Type)state.getValue(TYPE)).getSerializedName());
      return itemStack;
   }

   public static enum Type implements StringRepresentable {
      STONE,
      VAULT_STONE;

      private static final Map<String, VaultOreBlock.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(VaultOreBlock.Type::getSerializedName, Functions.identity()));

      public static VaultOreBlock.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
      }

      @Nonnull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
