package iskallia.vault.core.vault.stat;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import iskallia.vault.block.VaultOreBlock;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.adapter.DirectAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.init.ModBlocks;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class MinedBlocksStat extends DataMap<MinedBlocksStat, ResourceLocation, MinedBlocksStat.Entry> {
   public static final BiMap<ResourceLocation, Integer> ALLOWED_BLOCKS = HashBiMap.create();

   public MinedBlocksStat() {
      super(
         new HashMap<>(),
         new DirectAdapter<>(
            (buffer, context, value) -> buffer.writeIntSegmented((Integer)ALLOWED_BLOCKS.get(value), 7),
            (buffer, context) -> (ResourceLocation)ALLOWED_BLOCKS.inverse().get(buffer.readIntSegmented(7))
         ),
         Adapter.ofCompound(MinedBlocksStat.Entry::new)
      );
      ALLOWED_BLOCKS.keySet().forEach(block -> this.put(block, new MinedBlocksStat.Entry()));
   }

   public void onMine(BlockState state, Player player) {
      if (!(state.getBlock() instanceof VaultOreBlock) || (Boolean)state.getValue(VaultOreBlock.GENERATED) || player.isCreative()) {
         if (this.containsKey(state.getBlock().getRegistryName())) {
            this.get(state.getBlock().getRegistryName()).modify(MinedBlocksStat.Entry.COUNT, count -> count + 1);
         }
      }
   }

   static {
      ALLOWED_BLOCKS.put(ModBlocks.TREASURE_SAND.getRegistryName(), 0);
      ALLOWED_BLOCKS.put(ModBlocks.TREASURE_SAND.getRegistryName(), 1);
      ALLOWED_BLOCKS.put(ModBlocks.COIN_PILE.getRegistryName(), 2);
      ALLOWED_BLOCKS.put(ModBlocks.ALEXANDRITE_ORE.getRegistryName(), 3);
      ALLOWED_BLOCKS.put(ModBlocks.BENITOITE_ORE.getRegistryName(), 4);
      ALLOWED_BLOCKS.put(ModBlocks.LARIMAR_ORE.getRegistryName(), 5);
      ALLOWED_BLOCKS.put(ModBlocks.BLACK_OPAL_ORE.getRegistryName(), 6);
      ALLOWED_BLOCKS.put(ModBlocks.PAINITE_ORE.getRegistryName(), 7);
      ALLOWED_BLOCKS.put(ModBlocks.ISKALLIUM_ORE.getRegistryName(), 8);
      ALLOWED_BLOCKS.put(ModBlocks.GORGINITE_ORE.getRegistryName(), 9);
      ALLOWED_BLOCKS.put(ModBlocks.SPARKLETINE_ORE.getRegistryName(), 10);
      ALLOWED_BLOCKS.put(ModBlocks.WUTODIE_ORE.getRegistryName(), 11);
      ALLOWED_BLOCKS.put(ModBlocks.ASHIUM_ORE.getRegistryName(), 12);
      ALLOWED_BLOCKS.put(ModBlocks.BOMIGNITE_ORE.getRegistryName(), 13);
      ALLOWED_BLOCKS.put(ModBlocks.TUBIUM_ORE.getRegistryName(), 15);
      ALLOWED_BLOCKS.put(ModBlocks.UPALINE_ORE.getRegistryName(), 16);
      ALLOWED_BLOCKS.put(ModBlocks.PUFFIUM_ORE.getRegistryName(), 17);
      ALLOWED_BLOCKS.put(ModBlocks.PETZANITE_ORE.getRegistryName(), 18);
      ALLOWED_BLOCKS.put(ModBlocks.XENIUM_ORE.getRegistryName(), 19);
      ALLOWED_BLOCKS.put(ModBlocks.ECHO_ORE.getRegistryName(), 20);
   }

   public static class Entry extends DataObject<MinedBlocksStat.Entry> {
      public static final FieldRegistry FIELDS = new FieldRegistry();
      public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class)
         .with(Version.v1_0, Adapter.ofSegmentedInt(7), DISK.all())
         .register(FIELDS);

      public Entry() {
         this.set(COUNT, Integer.valueOf(0));
      }

      @Override
      public FieldRegistry getFields() {
         return FIELDS;
      }
   }
}
