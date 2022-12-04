package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.IdentifierList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.player.Completion;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.vault.stat.StatCollector;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BailObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("bail", Objective.class).with(Version.v1_0, BailObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> LOCKED_STACK = FieldKey.of("locked_stack", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<IdentifierList> TAGS = FieldKey.of("tags", IdentifierList.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), IdentifierList::create)
      .register(FIELDS);

   protected BailObjective() {
      this.set(LOCKED_STACK, Integer.valueOf(0));
      this.set(TAGS, IdentifierList.create());
   }

   public static BailObjective create(ResourceLocation... tags) {
      BailObjective objective = new BailObjective();
      objective.get(TAGS).addAll(Arrays.asList(tags));
      return objective;
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.VAULT_PORTAL_COLLIDE
         .in(world)
         .register(
            this,
            data -> {
               Listener listener = vault.get(Vault.LISTENERS).get(data.getPlayer().getUUID());
               if (listener instanceof Runner runner) {
                  if (this.get(LOCKED_STACK) > 0) {
                     data.getPlayer().displayClientMessage(new TextComponent("You cannot bail a locked vault.").withStyle(ChatFormatting.RED), true);
                  } else if (!data.getPlayer().isOnPortalCooldown()) {
                     if (vault.has(Vault.WORLD)) {
                        vault.get(Vault.WORLD)
                           .ifPresent(
                              WorldManager.PORTAL_LOGIC,
                              portalLogic -> portalLogic.getPortals()
                                 .filter(portal -> portal.contains(data.getPos()))
                                 .filter(portal -> portal.hasAnyTag(this.get(TAGS)))
                                 .findAny()
                                 .ifPresent(portal -> {
                                    vault.ifPresent(Vault.STATS, collector -> {
                                       StatCollector stats = collector.get(listener.get(Listener.ID));
                                       stats.set(StatCollector.COMPLETION, Completion.BAILED);
                                    });
                                    vault.get(Vault.LISTENERS).remove(world, vault, runner);
                                 })
                           );
                     }
                  }
               }
            }
         );
   }

   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }
}
