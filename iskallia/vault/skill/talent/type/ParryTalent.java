package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ParryTalent extends PlayerTalent {
   @Expose
   private final float parryChance;

   public ParryTalent(int cost, float parryChance) {
      super(cost);
      this.parryChance = parryChance;
   }

   public float getParryChance() {
      return this.parryChance;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingAttackEvent event) {
      if (!event.getEntityLiving().field_70170_p.field_72995_K) {
         if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            if (event.getSource() != VaultRaid.VAULT_FAILED) {
               ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
               float totalParryChance = 0.0F;
               TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

               for (TalentNode<?> node : abilities.getNodes()) {
                  if (node.getTalent() instanceof ParryTalent) {
                     ParryTalent talent = (ParryTalent)node.getTalent();
                     totalParryChance += talent.getParryChance();
                  }
               }

               SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

               for (SetNode<?> nodex : sets.getNodes()) {
                  if (nodex.getSet() instanceof AssassinSet) {
                     AssassinSet set = (AssassinSet)nodex.getSet();
                     totalParryChance += set.getParryChance();
                  } else if (nodex.getSet() instanceof NinjaSet) {
                     NinjaSet set = (NinjaSet)nodex.getSet();
                     totalParryChance += set.getParryChance();
                  }
               }

               for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                  ItemStack stack = player.func_184582_a(slot);
                  totalParryChance += ModAttributes.EXTRA_PARRY_CHANCE.getOrDefault(stack, 0.0F).getValue(stack);
               }

               if (event.getEntity().field_70170_p.field_73012_v.nextFloat() <= totalParryChance) {
                  player.field_70170_p
                     .func_184148_a(
                        null,
                        player.func_226277_ct_(),
                        player.func_226278_cu_(),
                        player.func_226281_cx_(),
                        SoundEvents.field_187767_eL,
                        SoundCategory.MASTER,
                        1.0F,
                        1.0F
                     );
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
