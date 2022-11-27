package iskallia.vault.skill.archetype;

import iskallia.vault.client.ClientArchetypeData;
import iskallia.vault.world.data.PlayerArchetypeData;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class AbstractArchetype<C extends AbstractArchetypeConfig> extends ForgeRegistryEntry<AbstractArchetype<?>> {
   protected final Supplier<C> configSupplier;
   protected final String name;

   protected AbstractArchetype(Supplier<C> configSupplier, ResourceLocation id) {
      this.configSupplier = configSupplier;
      this.name = "archetype." + id.getNamespace() + "." + id.getPath();
      this.setRegistryName(id);
   }

   public C getConfig() {
      return this.configSupplier.get();
   }

   public String getName() {
      return this.name;
   }

   protected boolean hasThisArchetype(LivingEntity entity) {
      if (entity instanceof Player && entity.getServer() == null) {
         return ClientArchetypeData.getCurrentArchetype() == this;
      } else {
         return entity instanceof ServerPlayer player
            ? PlayerArchetypeData.get(player.server).getArchetypeContainer(player).getCurrentArchetype() == this
            : false;
      }
   }

   public void onAdded(MinecraftServer server, ServerPlayer player) {
   }

   public void onTick(MinecraftServer server, ServerPlayer player) {
   }

   public void onRemoved(MinecraftServer server, ServerPlayer player) {
   }
}
