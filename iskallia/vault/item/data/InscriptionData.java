package iskallia.vault.item.data;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.layout.ArchitectCrystalLayout;
import iskallia.vault.item.crystal.layout.CompoundCrystalLayout;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.item.crystal.properties.InstabilityCrystalProperties;
import iskallia.vault.item.crystal.time.ValueCrystalTime;
import iskallia.vault.nbt.VListNBT;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

public class InscriptionData implements INBTSerializable<CompoundTag> {
   public static final String NBT_KEY = "data";
   public static final int MODELS = 16;
   private VListNBT<InscriptionData.Entry, CompoundTag> entries = VListNBT.of(InscriptionData.Entry::new);
   private Float completion;
   private Integer time;
   private Float instability;
   private Integer size;
   private int model;
   private Integer color;

   private InscriptionData() {
   }

   public static InscriptionData empty() {
      return new InscriptionData();
   }

   public static InscriptionData from(ItemStack stack) {
      InscriptionData data = new InscriptionData();
      if (stack.hasTag()) {
         data.deserializeNBT(stack.getOrCreateTag().getCompound("data"));
      }

      return data;
   }

   public InscriptionData write(ItemStack stack) {
      stack.getOrCreateTag().put("data", this.serializeNBT());
      return this;
   }

   public int getModel() {
      return this.model;
   }

   public Integer getColor() {
      return this.color;
   }

   public List<InscriptionData.Entry> getEntries() {
      return this.entries;
   }

   public InscriptionData add(ResourceLocation pool, int count, int color) {
      this.entries.add(new InscriptionData.Entry(pool, null, count, color));
      return this;
   }

   public InscriptionData add(ArchitectRoomEntry.Type type, int count, int color) {
      this.entries.add(new InscriptionData.Entry(null, type, count, color));
      return this;
   }

   public InscriptionData add(InscriptionData.Entry entry) {
      this.entries.add(entry);
      return this;
   }

   public void setCompletion(float completion) {
      this.completion = completion;
   }

   public void setTime(int time) {
      this.time = time;
   }

   public void setInstability(float instability) {
      this.instability = instability;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public int getSize() {
      return this.size != null ? this.size : 10;
   }

   public void setModel(int model) {
      this.model = model;
   }

   public void setColor(Integer color) {
      this.color = color;
   }

   public boolean apply(Player player, ItemStack stack, CrystalData crystal) {
      if (crystal.getProperties().isUnmodifiable()) {
         return false;
      } else {
         ArchitectCrystalLayout layout = CompoundCrystalLayout.get(crystal.getLayout(), ArchitectCrystalLayout.class);
         ArchitectCrystalLayout architect;
         if (layout == null) {
            architect = new ArchitectCrystalLayout();
            crystal.setLayout(CompoundCrystalLayout.flatten(crystal.getLayout(), architect));
         } else {
            architect = layout;
         }

         for (InscriptionData.Entry entry : this.entries) {
            architect.add(entry.toRoomEntry());
         }

         if (this.completion != null) {
            architect.addCompletion(this.completion);
         }

         if (this.size != null && this.time != null && crystal.getTime() instanceof ValueCrystalTime data) {
            if (data.getRoll() instanceof IntRoll.Constant constant) {
               crystal.setTime(new ValueCrystalTime(IntRoll.ofConstant(constant.getCount() + this.time)));
            } else if (data.getRoll() instanceof IntRoll.Uniform uniform) {
               crystal.setTime(new ValueCrystalTime(IntRoll.ofUniform(uniform.getMin() + this.time, uniform.getMax() + this.time)));
            }
         }

         if (crystal.getProperties() instanceof InstabilityCrystalProperties properties) {
            if (this.instability == null) {
               return false;
            }

            float instability = properties.getInstability();
            Random random = new Random();
            if (random.nextFloat() < instability && !stack.isEmpty()) {
               double instabilityAvoidanceChance = 0.0;
               if (random.nextDouble() > instabilityAvoidanceChance) {
                  if (random.nextFloat() < ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.exhaustProbability) {
                     VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, stack);
                  } else {
                     VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("catalyst_curse"), 1), stack);
                  }
               }
            }

            properties.setInstability(instability + this.instability);
         } else if (crystal.getProperties() instanceof CapacityCrystalProperties properties) {
            Integer capacity = properties.getCapacity().orElse(null);
            Integer level = crystal.getProperties().getLevel().orElse(null);
            if (capacity == null || level == null) {
               return false;
            }

            if (capacity < this.getSize()) {
               ModConfigs.VAULT_MODIFIER_POOLS
                  .getRandom(VaultMod.id("catalyst_curse"), level, JavaRandom.ofNanoTime())
                  .forEach(modifier -> crystal.getModifiers().add(VaultModifierStack.of((VaultModifier<?>)modifier)));
            }

            properties.setSize(properties.getSize() + this.getSize());
         }

         if (!stack.isEmpty()) {
            crystal.write(stack);
         }

         return true;
      }
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      ListTag entriesList = new ListTag();

      for (InscriptionData.Entry entry : this.entries) {
         entriesList.add(entry.serializeNBT());
      }

      nbt.put("entries", entriesList);
      if (this.completion != null) {
         nbt.putFloat("completion", this.completion);
      }

      if (this.time != null) {
         nbt.putInt("time", this.time);
      }

      if (this.instability != null) {
         nbt.putFloat("instability", this.instability);
      }

      if (this.size != null) {
         nbt.putInt("size", this.size);
      }

      nbt.putInt("model", this.model);
      if (this.color != null) {
         nbt.putInt("color", this.color);
      }

      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      ListTag entriesList = nbt.getList("entries", 10);
      this.entries.clear();

      for (int i = 0; i < entriesList.size(); i++) {
         InscriptionData.Entry entry = new InscriptionData.Entry();
         entry.deserializeNBT(entriesList.getCompound(i));
         this.entries.add(entry);
      }

      this.completion = nbt.contains("completion") ? nbt.getFloat("completion") : null;
      this.time = nbt.contains("time") ? nbt.getInt("time") : null;
      this.instability = nbt.contains("instability") ? nbt.getFloat("instability") : null;
      this.size = nbt.contains("size") ? nbt.getInt("size") : null;
      this.model = nbt.getInt("model");
      if (nbt.contains("color", 3)) {
         this.color = nbt.getInt("color");
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      if (this.completion != null) {
         tooltip.add(
            new TextComponent("Completion: ").append(new TextComponent(Math.round(this.completion * 100.0F) + "%").withStyle(Style.EMPTY.withColor(4766456)))
         );
      }

      if (this.time != null) {
         tooltip.add(new TextComponent("Time: ").append(new TextComponent(UIHelper.formatTimeString(this.time.intValue())).withStyle(ChatFormatting.GRAY)));
      }

      if (this.instability != null) {
         tooltip.add(new TextComponent("Instability: ").append(new TextComponent("%.1f%%".formatted(this.instability * 100.0F)).withStyle(ChatFormatting.RED)));
      }

      tooltip.add(new TextComponent("Size: ").append(new TextComponent(String.valueOf(this.getSize())).withStyle(ChatFormatting.RED)));

      for (InscriptionData.Entry entry : this.entries) {
         String roomStr = entry.count > 1 ? "Rooms" : "Room";
         Component txt = new TextComponent(" • ")
            .withStyle(ChatFormatting.GRAY)
            .append(new TextComponent(String.valueOf(entry.count)).withStyle(ChatFormatting.GRAY))
            .append(" ")
            .append(entry.toRoomEntry().getName())
            .append(new TextComponent(" " + roomStr).withStyle(ChatFormatting.GRAY));
         tooltip.add(txt);
      }
   }

   public static class Entry implements INBTSerializable<CompoundTag> {
      @Expose
      private ResourceLocation pool;
      @Expose
      private ArchitectRoomEntry.Type type;
      @Expose
      public int count;
      @Expose
      public int color;

      public Entry() {
      }

      public Entry(ResourceLocation pool, ArchitectRoomEntry.Type type, int count, int color) {
         this.pool = pool;
         this.type = type;
         this.count = count;
         this.color = color;
      }

      public ArchitectRoomEntry toRoomEntry() {
         return this.pool != null
            ? ArchitectRoomEntry.ofPool(this.pool, this.count).set(ArchitectRoomEntry.COLOR, Integer.valueOf(this.color))
            : ArchitectRoomEntry.ofType(this.type, this.count).set(ArchitectRoomEntry.COLOR, Integer.valueOf(this.color));
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         if (this.pool != null) {
            nbt.putString("pool", this.pool.toString());
         }

         if (this.type != null) {
            nbt.putInt("type", this.type.ordinal());
         }

         nbt.putInt("count", this.count);
         nbt.putInt("color", this.color);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         if (nbt.contains("pool")) {
            this.pool = new ResourceLocation(nbt.getString("pool"));
         }

         if (nbt.contains("type")) {
            this.type = ArchitectRoomEntry.Type.values()[nbt.getInt("type")];
         }

         this.count = nbt.getInt("count");
         this.color = nbt.getInt("color");
      }
   }
}
