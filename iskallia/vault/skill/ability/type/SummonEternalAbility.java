package iskallia.vault.skill.ability.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.entity.EternalEntity;
import iskallia.vault.world.data.EternalsData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SummonEternalAbility extends PlayerAbility {
   @Expose
   private int despawnTime;
   @Expose
   private boolean vaultOnly;
   @Expose
   private int count;

   public SummonEternalAbility(int cost, int cooldown, int despawnTime, boolean vaultOnly, int count) {
      super(cost, PlayerAbility.Behavior.RELEASE_TO_PERFORM);
      this.cooldown = cooldown;
      this.despawnTime = despawnTime;
      this.vaultOnly = vaultOnly;
      this.count = count;
   }

   public int getDespawnTime() {
      return this.despawnTime;
   }

   public boolean isVaultOnly() {
      return this.vaultOnly;
   }

   public int getCount() {
      return this.count;
   }

   @Override
   public void onAction(PlayerEntity player, boolean active) {
      if (!player.func_130014_f_().field_72995_K) {
         EternalsData.EternalGroup eternals = EternalsData.get((ServerWorld)player.field_70170_p).getEternals(player);
         if (eternals.getEternals().isEmpty()) {
            player.func_145747_a(new StringTextComponent(TextFormatting.RED + "You have no eternals to summon."), player.func_110124_au());
         } else if (player.func_130014_f_().func_234923_W_() != Vault.VAULT_KEY && this.isVaultOnly()) {
            player.func_145747_a(new StringTextComponent(TextFormatting.RED + "You can only summon eternals in the Vault!"), player.func_110124_au());
         } else {
            for (int i = 0; i < this.getCount(); i++) {
               EternalEntity eternal = eternals.getRandom(player.field_70170_p.func_201674_k()).create(player.field_70170_p);
               eternal.func_70012_b(player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), player.field_70177_z, player.field_70125_A);
               eternal.setDespawnTime(player.func_184102_h().func_71259_af() + this.getDespawnTime());
               eternal.owner = player.func_110124_au();
               eternal.func_184195_f(true);
               player.field_70170_p.func_217376_c(eternal);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onDamage(LivingAttackEvent event) {
      LivingEntity damagedEntity = event.getEntityLiving();
      Entity dealerEntity = event.getSource().func_76346_g();
      if (damagedEntity instanceof EternalEntity && dealerEntity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)dealerEntity;
         if (!player.func_184812_l_()) {
            event.setCanceled(true);
         }
      }
   }
}
