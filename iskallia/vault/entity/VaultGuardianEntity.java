package iskallia.vault.entity;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class VaultGuardianEntity extends PiglinBruteEntity {
   public VaultGuardianEntity(EntityType<? extends PiglinBruteEntity> type, World world) {
      super(type, world);
      this.func_98053_h(false);
      ModifiableAttributeInstance attribute = this.func_110148_a(Attributes.field_233824_g_);
      if (attribute != null) {
         attribute.func_111128_a(6.0);
      }
   }

   protected void func_213354_a(DamageSource source, boolean attackedRecently) {
      if (this.func_70681_au().nextInt(ModConfigs.VAULT_GENERAL.getObeliskDropChance()) == 0) {
         this.func_199701_a_(new ItemStack(ModItems.OBELISK_INSCRIPTION));
      }
   }

   public boolean func_70097_a(DamageSource source, float amount) {
      if (!(source.func_76346_g() instanceof PlayerEntity) && !(source.func_76346_g() instanceof EternalEntity) && source != DamageSource.field_76380_i) {
         return false;
      } else if (!this.func_180431_b(source) && source != DamageSource.field_76379_h) {
         this.func_184581_c(source);
         return super.func_70097_a(source, amount);
      } else {
         return false;
      }
   }

   public boolean func_180431_b(DamageSource source) {
      return super.func_180431_b(source) || source.func_76352_a();
   }

   public void func_70037_a(CompoundNBT compound) {
      super.func_70037_a(compound);
      this.func_242340_t(true);
      this.field_242334_c = compound.func_74762_e("TimeInOverworld");
   }

   public void func_233627_a_(float strength, double ratioX, double ratioZ) {
   }

   protected float func_225515_ai_() {
      return 0.75F;
   }
}
