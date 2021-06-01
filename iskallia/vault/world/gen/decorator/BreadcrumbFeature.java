package iskallia.vault.world.gen.decorator;

import com.mojang.serialization.Codec;
import iskallia.vault.Vault;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent.Register;

public class BreadcrumbFeature extends Feature<NoFeatureConfig> {
   public static Feature<NoFeatureConfig> INSTANCE;

   public BreadcrumbFeature(Codec<NoFeatureConfig> codec) {
      super(codec);
   }

   public boolean func_241855_a(ISeedReader world, ChunkGenerator gen, Random rand, BlockPos pos, NoFeatureConfig config) {
      for (int i = 0; i < 128; i++) {
         int x = rand.nextInt(16);
         int z = rand.nextInt(16);
         int y = rand.nextInt(256);
         BlockPos c = pos.func_177982_a(x, y, z);
         if (world.func_180495_p(c).func_177230_c() == Blocks.field_150350_a && world.func_180495_p(c.func_177977_b()).func_200132_m()) {
            world.func_180501_a(c, Blocks.field_150486_ae.func_176223_P(), 2);
            TileEntity te = world.func_175625_s(c);
            if (te instanceof ChestTileEntity) {
               ((ChestTileEntity)te).func_189404_a(Vault.id("chest/breadcrumb"), 0L);
            }

            return true;
         }
      }

      return false;
   }

   public static void register(Register<Feature<?>> event) {
      INSTANCE = new BreadcrumbFeature(NoFeatureConfig.field_236558_a_);
      INSTANCE.setRegistryName(Vault.id("breadcrumb_chest"));
      event.getRegistry().register(INSTANCE);
   }
}
