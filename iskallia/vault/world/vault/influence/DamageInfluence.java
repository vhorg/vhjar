package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DamageInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("dmg_dealt");
   private float damageDealtMultiplier;

   DamageInfluence() {
      super(ID);
   }

   public DamageInfluence(float damageDealtMultiplier) {
      this();
      this.damageDealtMultiplier = damageDealtMultiplier;
   }

   public float getDamageDealtMultiplier() {
      return this.damageDealtMultiplier;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d()) {
         Entity attacker = event.getSource().func_76346_g();
         if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)attacker;
            VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getAt(sPlayer.func_71121_q(), sPlayer.func_233580_cy_());
            if (vault != null) {
               float dmg = event.getAmount();

               for (DamageInfluence influence : vault.getInfluences().getInfluences(DamageInfluence.class)) {
                  dmg *= influence.getDamageDealtMultiplier();
               }

               event.setAmount(dmg);
            }
         }
      }
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("dmg", this.damageDealtMultiplier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.damageDealtMultiplier = tag.func_74760_g("dmg");
   }
}
