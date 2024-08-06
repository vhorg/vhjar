package iskallia.vault.gear.attribute;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.data.GearDataVersion;
import iskallia.vault.gear.data.VaultGearData;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VaultGearAttributeInstance<T> {
   private final VaultGearAttribute<T> attribute;
   private T value;

   VaultGearAttributeInstance(VaultGearAttribute<T> attribute) {
      this.attribute = attribute;
   }

   public VaultGearAttributeInstance(VaultGearAttribute<T> attribute, T value) {
      this.attribute = attribute;
      this.setValue(value);
   }

   public static <T> VaultGearAttributeInstance<T> cast(VaultGearAttribute<T> attr, Object value) {
      return new VaultGearAttributeInstance<>(attr, attr.getType().cast(value));
   }

   public VaultGearAttribute<T> getAttribute() {
      return this.attribute;
   }

   public boolean isValid() {
      return this.value != null && this.getAttribute().getType().isValid(this.value);
   }

   public boolean canBeModified() {
      return true;
   }

   @Nonnull
   public T getValue() {
      return this.value;
   }

   @Nonnull
   public T setValue(T value) {
      Preconditions.checkNotNull(value);
      T prevValue = this.value;
      this.value = value;
      return prevValue;
   }

   @OnlyIn(Dist.CLIENT)
   public Optional<MutableComponent> getDisplay(VaultGearData data, VaultGearModifier.AffixType type, ItemStack stack, boolean displayDetail) {
      return Optional.ofNullable(this.getAttribute().getReader().getDisplay(this, data, type, stack));
   }

   protected void write(BitBuffer buf) {
      this.attribute.getType().write(buf, this.getValue());
   }

   protected void read(BitBuffer buf, GearDataVersion version) {
      this.value = this.attribute.getType().read(buf);
   }

   public void toNbt(CompoundTag tag) {
      tag.put("value", this.getAttribute().getType().nbtWrite(this.getValue()));
   }

   protected void fromNbt(CompoundTag tag, GearDataVersion version) {
      this.value = this.getAttribute().getType().nbtRead(tag.get("value"));
   }

   public JsonObject serialize(VaultGearModifier.AffixType type) {
      JsonObject obj = new JsonObject();
      obj.addProperty("name", this.getAttribute().getRegistryName().toString());
      obj.add("value", this.getAttribute().getType().serialize(this.getValue()));
      obj.add("display", this.getAttribute().getReader().serializeDisplay(this, type));
      return obj;
   }

   @Override
   public String toString() {
      return "%s:%s".formatted(this.attribute, this.value);
   }
}
