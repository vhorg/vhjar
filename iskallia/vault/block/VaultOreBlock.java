package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.item.VaultOreBlockItem;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.util.calc.CopiousHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VaultOreBlock extends OreBlock implements EntityBlock {
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
      ItemStack stack = (ItemStack)builder.getOptionalParameter(LootContextParams.TOOL);
      List<ItemStack> drops;
      if (stack != null && HAS_SILK_TOUCH.matches(stack)) {
         drops = new ArrayList<>();
         drops.add(VaultOreBlockItem.fromType(this, (VaultOreBlock.Type)state.getValue(TYPE)));
      } else {
         drops = super.getDrops(state, builder);
      }

      Entity miner = (Entity)builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
      Vec3 minedVec = (Vec3)builder.getParameter(LootContextParams.ORIGIN);
      BlockPos minedPos = new BlockPos(minedVec);
      if (miner instanceof ServerPlayer sPlayer && (Boolean)state.getValue(GENERATED)) {
         float chance = getCopiouslyChance(builder);
         BlockPos playerPos = new BlockPos(sPlayer.getBlockX(), sPlayer.getBlockY(), sPlayer.getBlockZ());

         for (List<ItemStack> copy = new ArrayList<>(drops); chance > 0.0F && this.RANDOM.nextFloat() < chance; chance--) {
            sPlayer.level.playSound(null, playerPos, ModSounds.VAULT_CHEST_OMEGA_OPEN, SoundSource.BLOCKS, 0.1F, 0.85F);
            copy.forEach(s -> drops.add(s.copy()));
            if (stack != null && stack.getItem() instanceof ToolItem tool) {
               tool.hurt(stack, builder.getLevel(), sPlayer, 6.0);
            }
         }

         BlockEntity tile = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
         if (tile != null) {
            CommonEvents.ORE_LOOT_GENERATION_EVENT.invoke(sPlayer, state, minedPos, tile, drops, JavaRandom.ofNanoTime());
         }
      }

      ToolItem.handleLoot(builder, drops);
      return drops;
   }

   private static float getCopiouslyChance(net.minecraft.world.level.storage.loot.LootContext.Builder ctx) {
      float chance = 0.0F;
      if (ctx.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof LivingEntity entity) {
         chance += CopiousHelper.getCopiousChance(entity);
      }

      return chance;
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

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.VAULT_ORE_TILE_ENTITY.create(pos, state);
   }

   public static enum Type implements StringRepresentable {
      STONE,
      VAULT_STONE,
      WHITE;

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
