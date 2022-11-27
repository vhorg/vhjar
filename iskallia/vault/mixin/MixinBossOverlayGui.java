package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BossHealthOverlay.class})
public abstract class MixinBossOverlayGui {
   @Shadow
   @Final
   Map<UUID, LerpingBossEvent> events;

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Map;values()Ljava/util/Collection;"
      )
   )
   private Collection<LerpingBossEvent> thing(Map<UUID, LerpingBossEvent> map) {
      if (ServerVaults.isVaultWorld(Minecraft.getInstance().level)) {
         return this.events.values();
      } else {
         Map<UUID, Entity> entities = new HashMap<>();
         Minecraft.getInstance().level.entitiesForRendering().forEach(entity -> entities.put(entity.getUUID(), entity));
         return this.events.entrySet().stream().sorted(Comparator.comparingDouble(o -> {
            Player player = Minecraft.getInstance().player;
            Entity entity = entities.get(o.getKey());
            if (entity == null) {
               return 2.147483647E9;
            } else {
               return player.getName().getString().equals(entity.getCustomName().getString()) ? -2.1474836E9F : player.distanceTo(entity);
            }
         })).map(Entry::getValue).collect(Collectors.toList());
      }
   }
}
