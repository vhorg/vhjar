package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class BreakableTalent extends PlayerTalent {
   @Expose
   private final float damagePreventionChance;
   @Expose
   private final float damageAsDurabilityMultiplier;

   public BreakableTalent(int cost, float damagePreventionChance, float damageAsDurabilityMultiplier) {
      super(cost);
      this.damagePreventionChance = damagePreventionChance;
      this.damageAsDurabilityMultiplier = damageAsDurabilityMultiplier;
   }

   @SubscribeEvent
   public static void onPlayerDamage(LivingHurtEvent event) {
      if (!event.getEntityLiving().field_70170_p.field_72995_K) {
         if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getEntityLiving();
            int armorPieces = 0;

            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
               if (slotType.func_188453_a() != Group.HAND) {
                  ItemStack stack = player.func_184582_a(slotType);
                  if (!stack.func_190926_b() && stack.func_77984_f()) {
                     armorPieces++;
                  }
               }
            }

            if (armorPieces > 0) {
               float durabilityDamageMultiplier = 1.0F;
               float preventionChance = 0.0F;
               TalentTree talents = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);

               for (BreakableTalent talent : talents.getTalents(BreakableTalent.class)) {
                  preventionChance += talent.damagePreventionChance;
                  durabilityDamageMultiplier += talent.damageAsDurabilityMultiplier;
               }

               if (!(preventionChance <= 0.0F) && !(rand.nextFloat() >= preventionChance)) {
                  float dmgAmount = event.getAmount();
                  float postArmorAmount = dmgAmount / 4.0F * (4 - armorPieces);
                  float armorDmgHit = dmgAmount / 4.0F * durabilityDamageMultiplier;

                  for (EquipmentSlotType slotTypex : EquipmentSlotType.values()) {
                     if (slotTypex.func_188453_a() != Group.HAND) {
                        ItemStack stack = player.func_184582_a(slotTypex);
                        if (!stack.func_190926_b() && stack.func_77984_f()) {
                           stack.func_222118_a(MathHelper.func_76123_f(armorDmgHit), player, brokenStack -> player.func_213361_c(slotType));
                        }
                     }
                  }

                  player.func_130014_f_()
                     .func_184148_a(
                        null,
                        player.func_226277_ct_(),
                        player.func_226278_cu_(),
                        player.func_226281_cx_(),
                        SoundEvents.field_226142_fM_,
                        SoundCategory.MASTER,
                        0.5F,
                        1.0F
                     );
                  event.setAmount(postArmorAmount);
                  if (armorPieces >= 4) {
                     event.setAmount(0.0F);
                  }
               }
            }
         }
      }
   }
}
