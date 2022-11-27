package iskallia.vault.world.vault.influence;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultInfluences implements INBTSerializable<CompoundTag>, Iterable<VaultInfluence> {
   private final List<VaultInfluence> influences = new ArrayList<>();
   protected boolean initialized = false;

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized() {
      this.initialized = true;
   }

   public void addInfluence(VaultInfluence influence, VaultRaid vault, ServerLevel world) {
      this.influences.add(influence);
      Random rand = world.getRandom();
      vault.getPlayers().forEach(vPlayer -> influence.apply(vault, vPlayer, world, rand));
   }

   public void tick(VaultRaid vault, VaultPlayer vPlayer, ServerLevel world) {
      this.forEach(influence -> influence.tick(vault, vPlayer, world));
   }

   public <T extends VaultInfluence> List<T> getInfluences(Class<T> influenceClass) {
      return this.influences
         .stream()
         .filter(influence -> influenceClass.isAssignableFrom(influence.getClass()))
         .map(influence -> (VaultInfluence)influence)
         .collect(Collectors.toList());
   }

   @Override
   public Iterator<VaultInfluence> iterator() {
      return this.influences.iterator();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("initialized", this.initialized);
      ListTag influenceList = new ListTag();

      for (VaultInfluence influence : this.influences) {
         CompoundTag ct = new CompoundTag();
         ct.putString("id", influence.getKey().toString());
         ct.put("data", influence.serializeNBT());
      }

      tag.put("influences", influenceList);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.initialized = tag.getBoolean("initialized");
      ListTag influenceList = tag.getList("influences", 10);

      for (int i = 0; i < influenceList.size(); i++) {
         CompoundTag ct = influenceList.getCompound(i);
         VaultInfluenceRegistry.getInfluence(new ResourceLocation(ct.getString("id"))).ifPresent(influence -> {
            influence.deserializeNBT(ct.getCompound("data"));
            this.influences.add(influence);
         });
      }
   }
}
