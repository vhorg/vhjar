package iskallia.vault.world.data;

import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class SoulboundSnapshotData extends InventorySnapshotData {
   protected static final String DATA_NAME = "the_vault_Soulbound";

   @Override
   protected boolean shouldSnapshotItem(Player player, ItemStack stack) {
      return !stack.isEmpty() && AttributeGearData.<AttributeGearData>read(stack).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue());
   }

   @SubscribeEvent
   public static void onTick(PlayerTickEvent event) {
      Player player = event.player;
      if (player.isAlive() && player.level instanceof ServerLevel) {
         ServerLevel world = (ServerLevel)event.player.level;
         SoulboundSnapshotData data = get(world);
         if (data.hasSnapshot(player)) {
            data.restoreSnapshot(player);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof ServerPlayer sPlayer) {
         SoulboundSnapshotData data = get(sPlayer.getLevel());
         if (!data.hasSnapshot(sPlayer)) {
            data.createSnapshot(sPlayer);
         }
      }
   }

   private static SoulboundSnapshotData create(CompoundTag tag) {
      SoulboundSnapshotData data = new SoulboundSnapshotData();
      data.load(tag);
      return data;
   }

   public static SoulboundSnapshotData get(ServerLevel world) {
      return (SoulboundSnapshotData)world.getServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(SoulboundSnapshotData::create, SoulboundSnapshotData::new, "the_vault_Soulbound");
   }
}
