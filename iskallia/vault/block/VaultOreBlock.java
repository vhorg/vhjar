package iskallia.vault.block;

import iskallia.vault.init.ModSounds;
import iskallia.vault.item.paxel.PaxelItem;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class VaultOreBlock extends OreBlock {
   public static final BooleanProperty GENERATED = BooleanProperty.create("generated");
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
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(GENERATED, false));
   }

   @Nonnull
   public Item getAssociatedGem() {
      return this.associatedGem;
   }

   public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
      return !state.getValue(GENERATED) && silktouch > 0 ? 0 : Mth.nextInt(this.RANDOM, 3, 7);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{GENERATED});
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      if ((Boolean)state.getValue(GENERATED)) {
         LootContext ctx = builder.create(LootContextParamSets.EMPTY);
         if (ctx.hasParam(LootContextParams.TOOL)) {
            ItemStack tool = (ItemStack)ctx.getParam(LootContextParams.TOOL);
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
}
