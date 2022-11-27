package iskallia.vault.aura;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class AuraManager {
   private static final AuraManager INSTANCE = new AuraManager();
   private final Map<ResourceKey<Level>, Set<ActiveAura>> activeAuras = new HashMap<>();

   private AuraManager() {
   }

   public static AuraManager getInstance() {
      return INSTANCE;
   }

   @SubscribeEvent
   public static void onTick(WorldTickEvent event) {
      if (!event.world.isClientSide() && event.phase == Phase.START) {
         Set<ActiveAura> auras = INSTANCE.activeAuras.getOrDefault(event.world.dimension(), Collections.emptySet());
         if (!auras.isEmpty()) {
            auras.removeIf(aura -> !aura.canPersist());
            auras.forEach(ActiveAura::updateFromProvider);
            auras.forEach(aura -> aura.getAura().onTick(event.world, aura));
         }
      }
   }

   public void provideAura(AuraProvider provider) {
      this.activeAuras.computeIfAbsent(provider.getWorld(), key -> new HashSet<>()).add(new ActiveAura(provider));
   }

   @Nonnull
   public Collection<ActiveAura> getAurasAffecting(Entity entity) {
      Collection<ActiveAura> worldAuras = this.activeAuras.getOrDefault(entity.getCommandSenderWorld().dimension(), Collections.emptySet());
      return (Collection<ActiveAura>)(worldAuras.isEmpty()
         ? worldAuras
         : worldAuras.stream().filter(aura -> aura.isAffected(entity)).collect(Collectors.toSet()));
   }
}
