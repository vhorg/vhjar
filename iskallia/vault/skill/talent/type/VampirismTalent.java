package iskallia.vault.skill.talent.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.set.SetNode;
import iskallia.vault.skill.set.SetTree;
import iskallia.vault.skill.set.VampirismSet;
import iskallia.vault.skill.talent.TalentNode;
import iskallia.vault.skill.talent.TalentTree;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerSetsData;
import iskallia.vault.world.data.PlayerTalentsData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class VampirismTalent extends PlayerTalent {
   @Expose
   private final float leechRatio;

   public VampirismTalent(int cost, float leechRatio) {
      super(cost);
      this.leechRatio = leechRatio;
   }

   public float getLeechRatio() {
      return this.leechRatio;
   }

   public void onDamagedEntity(PlayerEntity player, LivingHurtEvent event) {
      player.func_70691_i(event.getAmount() * this.getLeechRatio());
      if (player.func_70681_au().nextFloat() <= 0.2) {
         float pitch = MathUtilities.randomFloat(1.0F, 1.5F);
         player.field_70170_p
            .func_184148_a(
               player,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.VAMPIRE_HISSING_SFX,
               SoundCategory.MASTER,
               0.020000001F,
               pitch
            );
         player.func_213823_a(ModSounds.VAMPIRE_HISSING_SFX, SoundCategory.MASTER, 0.020000001F, pitch);
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource() != null) {
         if (event.getSource().func_76346_g() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.getSource().func_76346_g();
            TalentTree abilities = PlayerTalentsData.get(player.func_71121_q()).getTalents(player);
            float leech = 0.0F;

            for (TalentNode<?> node : abilities.getNodes()) {
               if (node.getTalent() instanceof VampirismTalent) {
                  VampirismTalent vampirism = (VampirismTalent)node.getTalent();
                  leech += vampirism.getLeechRatio();
               }
            }

            SetTree sets = PlayerSetsData.get(player.func_71121_q()).getSets(player);

            for (SetNode<?> nodex : sets.getNodes()) {
               if (nodex.getSet() instanceof VampirismSet) {
                  VampirismSet set = (VampirismSet)nodex.getSet();
                  Vault.LOGGER.info("Set: " + set.getLeechRatio());
                  leech += set.getLeechRatio();
               }
            }

            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
               ItemStack stack = player.func_184582_a(slot);
               leech += ModAttributes.EXTRA_LEECH_RATIO.getOrDefault(stack, 0.0F).getValue(stack);
            }

            if (!(leech <= 0.0F)) {
               onDamagedEntity(event, player, leech);
            }
         }
      }
   }

   private static void onDamagedEntity(LivingHurtEvent event, ServerPlayerEntity player, float leech) {
      player.func_70691_i(event.getAmount() * leech);
      if (player.func_70681_au().nextFloat() <= 0.2) {
         float pitch = MathUtilities.randomFloat(1.0F, 1.5F);
         player.field_70170_p
            .func_184148_a(
               null,
               player.func_226277_ct_(),
               player.func_226278_cu_(),
               player.func_226281_cx_(),
               ModSounds.VAMPIRE_HISSING_SFX,
               SoundCategory.MASTER,
               0.020000001F,
               pitch
            );
      }
   }
}
