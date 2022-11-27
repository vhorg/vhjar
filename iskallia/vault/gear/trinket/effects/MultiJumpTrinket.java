package iskallia.vault.gear.trinket.effects;

import iskallia.vault.gear.trinket.TrinketEffect;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.TrinketJumpMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MultiJumpTrinket extends TrinketEffect.Simple {
   private static final int MAX_JUMPS = 3;
   private static int clientJumpCount = 0;
   private static boolean clientIsJumpHeld = false;

   public MultiJumpTrinket(ResourceLocation name) {
      super(name);
   }

   @Override
   public void onWornTick(LivingEntity entity, ItemStack stack) {
      super.onWornTick(entity, stack);
      if (entity.getLevel().isClientSide() && entity instanceof Player player) {
         this.onClientTick(player);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void onClientTick(Player player) {
      if (player instanceof LocalPlayer clientPlayer) {
         if (clientPlayer == Minecraft.getInstance().player) {
            if (!clientPlayer.getAbilities().flying) {
               if (clientPlayer.isOnGround()) {
                  clientJumpCount = 0;
               } else if (clientPlayer.input.jumping) {
                  if (!clientIsJumpHeld && clientJumpCount <= 3) {
                     clientJumpCount++;
                     clientPlayer.jumpFromGround();
                     ModNetwork.CHANNEL.sendToServer(TrinketJumpMessage.getInstance());
                  }

                  clientIsJumpHeld = true;
               } else {
                  clientIsJumpHeld = false;
               }
            }
         }
      }
   }
}
