package iskallia.vault.mixin;

import iskallia.vault.Vault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.concurrent.TickDelayedTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CommandBlockTileEntity.class})
public abstract class MixinCommandBlockTileEntity extends TileEntity {
   @Shadow
   private boolean field_184260_f;

   @Shadow
   public abstract void func_184253_b(boolean var1);

   @Shadow
   public abstract CommandBlockLogic func_145993_a();

   @Shadow
   public abstract void func_184250_a(boolean var1);

   public MixinCommandBlockTileEntity(TileEntityType<?> type) {
      super(type);
   }

   @Inject(
      method = {"validate"},
      at = {@At("RETURN")}
   )
   public void validate(CallbackInfo ci) {
      if (!this.field_145850_b.field_72995_K && this.field_145850_b.func_234923_W_() == Vault.VAULT_KEY) {
         this.field_145850_b
            .func_73046_m()
            .func_212871_a_(
               new TickDelayedTask(
                  this.field_145850_b.func_73046_m().func_71259_af() + 10,
                  () -> {
                     if (!this.field_145850_b.func_205220_G_().func_205359_a(this.func_174877_v(), Blocks.field_150483_bI) && this.field_184260_f) {
                        this.field_184260_f = false;
                        this.func_184253_b(true);
                     }

                     this.func_184250_a(false);
                     BlockState state = this.field_145850_b.func_180495_p(this.func_174877_v());
                     this.field_145850_b
                        .func_180495_p(this.func_174877_v())
                        .func_215697_a(this.field_145850_b, this.func_174877_v(), state.func_177230_c(), this.func_174877_v(), false);
                  }
               )
            );
      }
   }
}
