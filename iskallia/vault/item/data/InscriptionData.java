package iskallia.vault.item.data;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.core.world.generator.layout.ArchitectRoomEntry;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.layout.ArchitectCrystalLayout;
import iskallia.vault.item.crystal.time.ValueCrystalTime;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.MysticExpertise;
import iskallia.vault.world.data.PlayerExpertisesData;
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
import net.minecraft.server.level.ServerPlayer;
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
   private float completion;
   private int time;
   private float instability;
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

   public void setModel(int model) {
      this.model = model;
   }

   public void setColor(Integer color) {
      this.color = color;
   }

   public boolean apply(Player player, ItemStack stack, CrystalData crystal) {
      if (crystal.isUnmodifiable()) {
         return false;
      } else if (!(crystal.getLayout() instanceof ArchitectCrystalLayout layout)) {
         return false;
      } else {
         for (InscriptionData.Entry entry : this.entries) {
            layout.add(entry.toRoomEntry());
         }

         layout.addCompletion(this.completion);
         if (crystal.getTime() instanceof ValueCrystalTime data) {
            if (data.getRoll() instanceof IntRoll.Constant constant) {
               crystal.setTime(new ValueCrystalTime(IntRoll.ofConstant(constant.getCount() + this.time)));
            } else if (data.getRoll() instanceof IntRoll.Uniform uniform) {
               crystal.setTime(new ValueCrystalTime(IntRoll.ofUniform(uniform.getMin() + this.time, uniform.getMax() + this.time)));
            }
         }

         float instability = crystal.getInstability();
         Random random = new Random();
         if (random.nextFloat() < instability) {
            double instabilityAvoidanceChance = 0.0;
            if (player instanceof ServerPlayer serverPlayer) {
               instabilityAvoidanceChance = PlayerExpertisesData.get(serverPlayer.getLevel())
                  .getExpertises(serverPlayer)
                  .getAll(MysticExpertise.class, Skill::isUnlocked)
                  .stream()
                  .mapToDouble(MysticExpertise::getInstabilityChanceReduction)
                  .sum();
            }

            if (random.nextDouble() > instabilityAvoidanceChance) {
               if (random.nextFloat() < ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.exhaustProbability) {
                  VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, stack);
               } else {
                  VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("catalyst_curse")), stack);
               }
            }
         }

         crystal.setInstability(crystal.getInstability() + this.instability);
         crystal.write(stack);
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
      nbt.putFloat("completion", this.completion);
      nbt.putInt("time", this.time);
      nbt.putFloat("instability", this.instability);
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

      this.completion = nbt.getFloat("completion");
      this.time = nbt.getInt("time");
      this.instability = nbt.getFloat("instability");
      this.model = nbt.getInt("model");
      if (nbt.contains("color", 3)) {
         this.color = nbt.getInt("color");
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(
         new TextComponent("Completion: ").append(new TextComponent(Math.round(this.completion * 100.0F) + "%").withStyle(Style.EMPTY.withColor(4766456)))
      );
      tooltip.add(new TextComponent("Time: ").append(new TextComponent(UIHelper.formatTimeString(this.time)).withStyle(ChatFormatting.GRAY)));
      tooltip.add(new TextComponent("Instability: ").append(new TextComponent("%.1f%%".formatted(this.instability * 100.0F)).withStyle(ChatFormatting.RED)));

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
