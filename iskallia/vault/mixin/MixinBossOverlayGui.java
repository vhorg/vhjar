package iskallia.vault.mixin;

import iskallia.vault.Vault;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BossOverlayGui.class})
public abstract class MixinBossOverlayGui {
   @Shadow
   @Final
   private Map<UUID, ClientBossInfo> field_184060_g;

   @Redirect(
      method = {"func_238484_a_"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Map;values()Ljava/util/Collection;"
      )
   )
   private Collection<ClientBossInfo> thing(Map<UUID, ClientBossInfo> map) {
      if (Minecraft.func_71410_x().field_71441_e.func_234923_W_() != Vault.VAULT_KEY) {
         return this.field_184060_g.values();
      } else {
         Map<UUID, Entity> entities = new HashMap<>();
         Minecraft.func_71410_x().field_71441_e.func_217416_b().forEach(entity -> entities.put(entity.func_110124_au(), entity));
         return this.field_184060_g.entrySet().stream().sorted(Comparator.comparingDouble(o -> {
            PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
            Entity entity = entities.get(o.getKey());
            if (entity == null) {
               return 2.147483647E9;
            } else {
               return player.func_200200_C_().getString().equals(entity.func_200201_e().getString()) ? -2.1474836E9F : player.func_70032_d(entity);
            }
         })).map(Entry::getValue).collect(Collectors.toList());
      }
   }
}
