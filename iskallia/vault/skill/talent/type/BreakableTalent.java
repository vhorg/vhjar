package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Deprecated(
   forRemoval = true
)
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
      if (!event.getEntityLiving().level.isClientSide) {
         if (event.getEntityLiving() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)event.getEntityLiving();
            int armorPieces = 0;

            for (EquipmentSlot slotType : EquipmentSlot.values()) {
               if (slotType.getType() != Type.HAND) {
                  ItemStack stack = player.getItemBySlot(slotType);
                  if (!stack.isEmpty() && stack.isDamageableItem()) {
                     armorPieces++;
                  }
               }
            }

            if (armorPieces > 0) {
               float durabilityDamageMultiplier = 1.0F;
               float preventionChance = 0.0F;
               TalentTree talents = PlayerTalentsData.get(player.getLevel()).getTalents(player);

               for (BreakableTalent talent : talents.getTalents(BreakableTalent.class)) {
                  preventionChance += talent.damagePreventionChance;
                  durabilityDamageMultiplier += talent.damageAsDurabilityMultiplier;
               }

               if (!(preventionChance <= 0.0F) && !(rand.nextFloat() >= preventionChance)) {
                  float dmgAmount = event.getAmount();
                  float postArmorAmount = dmgAmount / 4.0F * (4 - armorPieces);
                  float armorDmgHit = dmgAmount / 4.0F * durabilityDamageMultiplier;

                  for (EquipmentSlot slotTypex : EquipmentSlot.values()) {
                     if (slotTypex.getType() != Type.HAND) {
                        ItemStack stack = player.getItemBySlot(slotTypex);
                        if (!stack.isEmpty() && stack.isDamageableItem()) {
                           stack.hurtAndBreak(Mth.ceil(armorDmgHit), player, brokenStack -> player.broadcastBreakEvent(slotType));
                        }
                     }
                  }

                  player.getCommandSenderWorld()
                     .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.MASTER, 0.5F, 1.0F);
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
