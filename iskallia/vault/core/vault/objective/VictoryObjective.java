package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.time.TickClock;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VictoryObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("victory", Objective.class).with(Version.v1_0, VictoryObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> TICKS_LEFT = FieldKey.of("ticks_left", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(7), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Void> TICKED = FieldKey.of("ticked", Void.class).with(Version.v1_0, Adapter.ofVoid(), DISK.all()).register(FIELDS);
   public static final FieldKey<VictoryObjective.TicksMap> TICKS = FieldKey.of("ticks", VictoryObjective.TicksMap.class)
      .with(Version.v1_1, Adapter.ofCompound(), DISK.all().or(CLIENT.all()), VictoryObjective.TicksMap::new)
      .register(FIELDS);

   protected VictoryObjective() {
   }

   protected VictoryObjective(int ticksLeft) {
      this.set(TICKS_LEFT, Integer.valueOf(ticksLeft));
      this.set(TICKS, new VictoryObjective.TicksMap());
   }

   public static VictoryObjective of(int ticksLeft) {
      return new VictoryObjective(ticksLeft);
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
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      vault.ifPresent(Vault.CLOCK, clock -> {
         clock.set(TickClock.PAUSED);
         clock.remove(TickClock.VISIBLE);
      });
      super.tickServer(world, vault);
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      int ticksLeft;
      boolean firstTick;
      if (this.get(TICKS).containsKey(listener.getId())) {
         ticksLeft = this.get(TICKS).get(listener.getId());
         firstTick = false;
      } else {
         ticksLeft = this.get(TICKS_LEFT);
         firstTick = true;
         this.get(TICKS).put(listener.getId(), Integer.valueOf(ticksLeft));
      }

      if (ticksLeft == 0) {
         vault.ifPresent(Vault.LISTENERS, listeners -> listeners.remove(world, vault, listener));
      } else {
         vault.ifPresent(
            Vault.CLOCK,
            clock -> {
               if (firstTick) {
                  listener.getPlayer()
                     .ifPresent(
                        player -> {
                           FireworkRocketEntity fireworks = new FireworkRocketEntity(
                              world, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.FIREWORK_ROCKET)
                           );
                           world.addFreshEntity(fireworks);
                           world.playSound(
                              null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 0.6F, 1.0F
                           );
                           TextComponent title = new TextComponent("Vault Completed!");
                           title.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(14536734)));
                           ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
                           player.connection.send(titlePacket);
                           CommonEvents.ENTITY_DAMAGE.register(listener, event -> {
                              if (event.getEntity().equals(player)) {
                                 event.setCanceled(true);
                              }
                           });
                        }
                     );
               }

               if (ticksLeft % 20 == 0) {
                  listener.getPlayer().ifPresent(player -> {
                     String s = "Teleporting back in %d seconds...".formatted(ticksLeft / 20);
                     player.displayClientMessage(new TextComponent(s).withStyle(ChatFormatting.WHITE), true);
                  });
               }
            }
         );
         this.get(TICKS).put(listener.getId(), Integer.valueOf(ticksLeft - 1));
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(PoseStack matrixStack, Window window, float partialTicks, Player player) {
      return false;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      return objective == this;
   }

   private static class TicksMap extends DataMap<VictoryObjective.TicksMap, UUID, Integer> {
      public TicksMap() {
         super(new HashMap<>(), Adapter.ofUUID(), Adapter.ofSegmentedInt(7));
      }
   }
}
