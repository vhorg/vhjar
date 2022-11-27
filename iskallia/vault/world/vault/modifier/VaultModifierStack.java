package iskallia.vault.world.vault.modifier;

import com.google.common.base.Preconditions;
import iskallia.vault.world.vault.modifier.modifier.EmptyModifier;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.IVaultModifierStack;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultModifierStack implements IVaultModifierStack, INBTSerializable<CompoundTag> {
   public static final VaultModifierStack EMPTY = new VaultModifierStack(EmptyModifier.INSTANCE, 1);
   private VaultModifier<?> modifier;
   private int size;
   private static final String TAG_ID = "id";
   private static final String TAG_SIZE = "size";

   public IVaultModifierStack immutable() {
      return VaultModifierStack.Immutable.of(this);
   }

   public static VaultModifierStack of(CompoundTag nbt) {
      return new VaultModifierStack(nbt);
   }

   public static VaultModifierStack of(@Nonnull VaultModifier<?> modifier) {
      return of(modifier, 1);
   }

   public static VaultModifierStack of(@Nonnull VaultModifier<?> modifier, int size) {
      return new VaultModifierStack(modifier, size);
   }

   private VaultModifierStack(CompoundTag nbt) {
      this.deserializeNBT(nbt);
   }

   public VaultModifierStack(VaultModifierStack toCopy) {
      this(toCopy.modifier, toCopy.size);
   }

   public VaultModifierStack(@Nonnull VaultModifier<?> modifier, int size) {
      this.modifier = (VaultModifier<?>)Preconditions.checkNotNull(modifier);
      this.size = size;
      this.checkAndSetEmpty();
   }

   public VaultModifierStack copy() {
      return new VaultModifierStack(this);
   }

   private void checkAndSetEmpty() {
      if (this.size <= 0) {
         this.modifier = EMPTY.modifier;
         this.size = 1;
      }
   }

   @Override
   public VaultModifier<?> getModifier() {
      return this.modifier;
   }

   @Override
   public ResourceLocation getModifierId() {
      return this.modifier.getId();
   }

   @Override
   public int getSize() {
      return this.size;
   }

   public VaultModifierStack setSize(int size) {
      this.size = size;
      this.checkAndSetEmpty();
      return this;
   }

   public VaultModifierStack grow(int amount) {
      this.size += amount;
      this.checkAndSetEmpty();
      return this;
   }

   public VaultModifierStack shrink(int amount) {
      return this.grow(-amount);
   }

   @Override
   public boolean isEmpty() {
      return this.modifier == EMPTY.modifier;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VaultModifierStack that = (VaultModifierStack)o;
         return this.size == that.size && this.modifier.getId().equals(that.modifier.getId());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.modifier.getId(), this.size);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("id", this.modifier.getId().toString());
      nbt.putInt("size", this.size);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ResourceLocation id = new ResourceLocation(nbt.getString("id"));
      this.modifier = VaultModifierRegistry.getOrDefault(id, EmptyModifier.INSTANCE);
      this.size = nbt.getInt("size");
   }

   public void encode(FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(this.modifier.getId());
      buffer.writeInt(this.size);
   }

   public static VaultModifierStack decode(FriendlyByteBuf buffer) {
      ResourceLocation resourceLocation = buffer.readResourceLocation();
      VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(resourceLocation, EmptyModifier.INSTANCE);
      int size = buffer.readInt();
      return new VaultModifierStack(modifier, size);
   }

   public static final class Immutable implements IVaultModifierStack {
      private final IVaultModifierStack vaultModifierStack;

      private Immutable(IVaultModifierStack vaultModifierStack) {
         this.vaultModifierStack = vaultModifierStack;
      }

      public static VaultModifierStack.Immutable of(IVaultModifierStack vaultModifierStack) {
         return new VaultModifierStack.Immutable(vaultModifierStack);
      }

      @Override
      public VaultModifier<?> getModifier() {
         return this.vaultModifierStack.getModifier();
      }

      @Override
      public ResourceLocation getModifierId() {
         return this.vaultModifierStack.getModifierId();
      }

      @Override
      public int getSize() {
         return this.vaultModifierStack.getSize();
      }

      @Override
      public boolean isEmpty() {
         return this.vaultModifierStack.isEmpty();
      }
   }
}
