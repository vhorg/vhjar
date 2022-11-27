package iskallia.vault.item;

import iskallia.vault.world.data.PlayerVaultStatsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class RegretOrbItem extends UsableItem {
   public RegretOrbItem(ResourceLocation id) {
      super(id);
   }

   @Override
   protected SoundEvent getSuccessSound() {
      return SoundEvents.PLAYER_LEVELUP;
   }

   @Override
   protected void doUse(ServerLevel level, ServerPlayer player) {
      PlayerVaultStatsData statsData = PlayerVaultStatsData.get(level);
      statsData.addRegretPoints(player, 1);
   }

   @Override
   protected TextColor getNameColor() {
      return TextColor.fromLegacyFormat(ChatFormatting.DARK_RED);
   }
}
