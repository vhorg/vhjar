package iskallia.vault.block.entity;

import iskallia.vault.container.ScavengerChestContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScavengerChestTileEntity extends ChestTileEntity {
   private static final Random rand = new Random();

   protected ScavengerChestTileEntity(TileEntityType<?> typeIn) {
      super(typeIn);
      this.func_199721_a(NonNullList.func_191197_a(45, ItemStack.field_190927_a));
   }

   public ScavengerChestTileEntity() {
      this(ModBlocks.SCAVENGER_CHEST_TILE_ENTITY);
   }

   public void func_73660_a() {
      super.func_73660_a();
      if (this.field_145850_b.func_201670_d()) {
         this.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      ParticleManager mgr = Minecraft.func_71410_x().field_71452_i;
      BlockPos pos = this.func_174877_v();
      Vector3d rPos = new Vector3d(
         pos.func_177958_n() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F,
         pos.func_177956_o() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 7.0F,
         pos.func_177952_p() + 0.5 + (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 3.0F
      );
      SimpleAnimatedParticle p = (SimpleAnimatedParticle)mgr.func_199280_a(
         ParticleTypes.field_197629_v, rPos.field_72450_a, rPos.field_72448_b, rPos.field_72449_c, 0.0, 0.0, 0.0
      );
      if (p != null) {
         p.field_187149_H = 0.0F;
         p.func_187146_c(2347008);
      }
   }

   public int func_70302_i_() {
      return 45;
   }

   protected Container func_213906_a(int id, PlayerInventory playerInventory) {
      Container ct = new ScavengerChestContainer(id, playerInventory, this, this);
      if (this.field_145850_b instanceof ServerWorld) {
         ServerWorld sWorld = (ServerWorld)this.field_145850_b;
         VaultRaid vault = VaultRaidData.get(sWorld).getAt(sWorld, this.func_174877_v());
         if (vault != null) {
            ct = vault.getActiveObjective(ScavengerHuntObjective.class).map(objective -> {
               Container linkedCt = new ScavengerChestContainer(id, playerInventory, this, objective.getScavengerChestInventory());
               linkedCt.func_75132_a(objective.getChestWatcher());
               return linkedCt;
            }).orElse(ct);
         }
      }

      return ct;
   }

   public ITextComponent func_145748_c_() {
      return new TranslationTextComponent(ModBlocks.SCAVENGER_CHEST.func_149739_a());
   }
}
