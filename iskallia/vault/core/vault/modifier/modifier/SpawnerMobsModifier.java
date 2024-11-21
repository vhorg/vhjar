package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.item.tool.ToolItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SpawnerMobsModifier extends VaultModifier<SpawnerMobsModifier.Properties> {
   public SpawnerMobsModifier(ResourceLocation id, SpawnerMobsModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getIncrease() * s * 100.0)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.SURFACE_GENERATION.in(world).register(context.getUUID(), data -> data.getChunk().getBlockEntitiesPos().forEach(pos -> {
         if (ToolItem.SPAWNER_ID.equals(data.getGenRegion().getBlockState(pos).getBlock().getRegistryName())) {
            BlockEntity entity = data.getChunk().getBlockEntity(pos);
            CompoundTag nbt;
            if (entity != null) {
               nbt = entity.saveWithFullMetadata();
            } else {
               nbt = data.getChunk().getBlockEntityNbt(pos);
            }

            if (nbt == null) {
               nbt = new CompoundTag();
               nbt.putInt("x", pos.getX());
               nbt.putInt("y", pos.getY());
               nbt.putInt("z", pos.getZ());
               data.getChunk().setBlockEntityNbt(nbt);
            }

            CompoundTag manager = nbt.getCompound("Manager");
            CompoundTag modifiers = manager.getCompound("AttemptModifiers");
            modifiers.putDouble(context.getUUID().toString(), this.properties.getIncrease());
            manager.put("AttemptModifiers", modifiers);
            nbt.put("Manager", manager);
            BlockEntity existing = data.getGenRegion().getBlockEntity(pos);
            if (existing != null) {
               existing.load(nbt);
            }
         }
      }), -100);
   }

   @Override
   public void releaseServer(ModifierContext context) {
      CommonEvents.SURFACE_GENERATION.release(context.getUUID());
   }

   public static class Properties {
      @Expose
      private final double increase;

      public Properties(double increase) {
         this.increase = increase;
      }

      public double getIncrease() {
         return this.increase;
      }
   }
}
