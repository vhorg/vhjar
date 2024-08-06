package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundHunterParticlesMessage;
import iskallia.vault.skill.ability.effect.spi.HunterAbility;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.network.NetworkDirection;

public class HunterModifier extends VaultModifier<HunterModifier.Properties> {
   public HunterModifier(ResourceLocation id, HunterModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.SERVER_TICK
         .at(Phase.END)
         .register(
            context.getUUID(),
            event -> {
               if (world.getGameTime() % 10L == 0L) {
                  for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                     listener.getPlayer()
                        .ifPresent(
                           player -> {
                              for (HunterModifier.Properties.Entry entry : this.properties.getEntries()) {
                                 HunterAbility.selectPositions(world, player, entry.radius, tile -> getHighlightPosition(entry, tile))
                                    .forEach(
                                       highlightPosition -> {
                                          for (int i = 0; i < 8; i++) {
                                             Vec3 v = MiscUtils.getRandomOffset(highlightPosition.blockPos(), world.getRandom());
                                             ModNetwork.CHANNEL
                                                .sendTo(
                                                   new ClientboundHunterParticlesMessage(v.x, v.y, v.z, null, highlightPosition.color()),
                                                   player.connection.getConnection(),
                                                   NetworkDirection.PLAY_TO_CLIENT
                                                );
                                          }
                                       }
                                    );
                              }
                           }
                        );
                  }
               }
            }
         );
      super.initServer(world, vault, context);
   }

   private static Optional<HunterAbility.HighlightPosition> getHighlightPosition(HunterModifier.Properties.Entry entry, PartialTile tile) {
      return entry.filter.test(tile.getState(), tile.getEntity())
         ? Optional.of(new HunterAbility.HighlightPosition(tile.getPos(), null, entry.color))
         : Optional.empty();
   }

   @Override
   public void releaseServer(ModifierContext context) {
      CommonEvents.SERVER_TICK.release(context.getUUID());
      super.releaseServer(context);
   }

   public static class Properties {
      @Expose
      private List<HunterModifier.Properties.Entry> entries = new ArrayList<>();

      public List<HunterModifier.Properties.Entry> getEntries() {
         return this.entries;
      }

      public static class Entry {
         @Expose
         public TilePredicate filter;
         @Expose
         public double radius;
         @Expose
         public int color;
      }
   }
}
