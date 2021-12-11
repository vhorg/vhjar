package iskallia.vault.world.vault.logic.objective.raid.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.ActiveRaid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class BlockPlacementModifier extends RaidModifier {
   @Expose
   private final String block;
   @Expose
   private final int blocksToSpawn;
   @Expose
   private final String blockDescription;

   public BlockPlacementModifier(String name, Block block, int blocksToSpawn, String blockDescription) {
      this(name, block.getRegistryName().toString(), blocksToSpawn, blockDescription);
   }

   public BlockPlacementModifier(String name, String block, int blocksToSpawn, String blockDescription) {
      super(false, true, name);
      this.block = block;
      this.blocksToSpawn = blocksToSpawn;
      this.blockDescription = blockDescription;
   }

   @Override
   public void affectRaidMob(MobEntity mob, float value) {
   }

   @Override
   public void onVaultRaidFinish(VaultRaid vault, ServerWorld world, BlockPos controller, ActiveRaid raid, float value) {
      BlockState placementState = Registry.field_212618_g.func_241873_b(new ResourceLocation(this.block)).orElse(Blocks.field_150350_a).func_176223_P();
      int toPlace = this.blocksToSpawn * Math.round(value);
      AxisAlignedBB placementBox = raid.getRaidBoundingBox();

      for (int i = 0; i < toPlace; i++) {
         BlockPos at;
         do {
            at = MiscUtils.getRandomPos(placementBox, rand);
         } while (!world.func_175623_d(at) || !world.func_180495_p(at.func_177977_b()).func_224755_d(world, at, Direction.UP));

         world.func_180501_a(at, placementState, 2);
      }
   }

   @Override
   public ITextComponent getDisplay(float value) {
      int sets = Math.round(value);
      String set = sets > 1 ? "sets" : "set";
      return new StringTextComponent("+" + sets + " " + set + " of " + this.blockDescription).func_240699_a_(TextFormatting.GREEN);
   }
}
