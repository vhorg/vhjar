package iskallia.vault.item;

import iskallia.vault.world.data.PlayerExpertisesData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public class NeuralizerItem extends UsableItem {
   public NeuralizerItem(ResourceLocation id) {
      super(id);
   }

   @Override
   protected SoundEvent getSuccessSound() {
      return SoundEvents.BEACON_DEACTIVATE;
   }

   @Override
   protected void doUse(ServerLevel level, ServerPlayer player) {
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      statsData.resetAndReturnExpertisePoints(player);
      PlayerExpertisesData.get(level).resetExpertiseTree(player);
      level.sendParticles(
         ParticleTypes.FLASH,
         player.position().x() + Mth.cos((float)Math.toRadians(player.yHeadRot + 90.0F)) / 2.0F,
         player.position().y() + player.getBbHeight() - player.getBbHeight() / 4.0F,
         player.position().z() + Mth.sin((float)Math.toRadians(player.yHeadRot + 90.0F)) / 2.0F,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
   }

   @Override
   protected TextColor getNameColor() {
      return TextColor.fromLegacyFormat(ChatFormatting.YELLOW);
   }
}
