package iskallia.vault.world.vault.influence;

import iskallia.vault.Vault;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DamageTakenInfluence extends VaultInfluence {
   public static final ResourceLocation ID = Vault.id("dmg_taken");
   private float damageTakenMultiplier;

   DamageTakenInfluence() {
      super(ID);
   }

   public DamageTakenInfluence(float damageTakenMultiplier) {
      this();
      this.damageTakenMultiplier = damageTakenMultiplier;
   }

   public float getDamageTakenMultiplier() {
      return this.damageTakenMultiplier;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      LivingEntity entity = event.getEntityLiving();
      World world = entity.func_130014_f_();
      if (!world.func_201670_d()) {
         if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity sPlayer = (ServerPlayerEntity)entity;
            VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getAt(sPlayer.func_71121_q(), sPlayer.func_233580_cy_());
            if (vault != null) {
               float dmg = event.getAmount();

               for (DamageTakenInfluence influence : vault.getInfluences().getInfluences(DamageTakenInfluence.class)) {
                  dmg *= influence.getDamageTakenMultiplier();
               }

               event.setAmount(dmg);
            }
         }
      }
   }

   @Override
   public CompoundNBT serializeNBT() {
      CompoundNBT tag = super.serializeNBT();
      tag.func_74776_a("dmg", this.damageTakenMultiplier);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundNBT tag) {
      super.deserializeNBT(tag);
      this.damageTakenMultiplier = tag.func_74760_g("dmg");
   }
}
