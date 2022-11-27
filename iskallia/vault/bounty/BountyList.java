package iskallia.vault.bounty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class BountyList extends ArrayList<Bounty> implements INBTSerializable<CompoundTag> {
   public BountyList() {
   }

   public BountyList(List<Bounty> list) {
      this.addAll(list);
   }

   public BountyList(CompoundTag tag) {
      this.deserializeNBT(tag);
   }

   public Optional<Bounty> findById(UUID id) {
      return this.stream().filter(bounty -> bounty.getId().equals(id)).findFirst();
   }

   public boolean removeById(UUID id) {
      return this.removeIf(bounty -> bounty.getId().equals(id));
   }

   public boolean contains(UUID id) {
      return this.stream().anyMatch(bounty -> bounty.getId().equals(id));
   }

   public CompoundTag serializeNBT() {
      CompoundTag bountyListTag = new CompoundTag();
      this.forEach(bounty -> bountyListTag.put(bounty.getId().toString(), bounty.serializeNBT()));
      return bountyListTag;
   }

   public void deserializeNBT(CompoundTag nbt) {
      nbt.getAllKeys().forEach(key -> this.add(new Bounty(nbt.getCompound(key))));
   }
}
