package iskallia.vault.item;

import iskallia.vault.world.raid.VaultRaid;
import iskallia.vault.world.raid.modifier.VaultModifiers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class CrystalData implements INBTSerializable<CompoundNBT> {
   private final ItemStack delegate;
   protected List<CrystalData.Modifier> modifiers = new ArrayList<>();

   public CrystalData(ItemStack delegate) {
      this.delegate = delegate;
      if (this.delegate != null) {
         this.deserializeNBT(this.delegate.func_190925_c("CrystalData"));
      }
   }

   public ItemStack getDelegate() {
      return this.delegate;
   }

   public void updateDelegate() {
      if (this.delegate != null) {
         this.delegate.func_196082_o().func_218657_a("CrystalData", this.serializeNBT());
      }
   }

   public boolean addModifier(String name, CrystalData.Modifier.Operation operation, float chance) {
      Iterator<CrystalData.Modifier> it = this.modifiers.iterator();

      while (it.hasNext()) {
         CrystalData.Modifier modifier = it.next();
         if (modifier.name.equals(name) && modifier.operation == operation) {
            float oldValue = modifier.chance;
            float newValue = MathHelper.func_76131_a(oldValue + chance, 0.0F, 1.0F);
            if (oldValue == newValue) {
               return false;
            }

            chance = newValue;
            it.remove();
         }
      }

      this.modifiers.add(new CrystalData.Modifier(name, operation, MathHelper.func_76131_a(chance, 0.0F, 1.0F)));
      this.updateDelegate();
      return true;
   }

   public void apply(VaultRaid raid, Random random) {
      this.modifiers.forEach(modifier -> modifier.apply(raid.modifiers, random));
   }

   public void addInformation(World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
      for (CrystalData.Modifier modifier : this.modifiers) {
         tooltip.add(
            new StringTextComponent("- Has ")
               .func_230529_a_(new StringTextComponent(Math.round(modifier.chance * 100.0F) + "%").func_240699_a_(modifier.operation.color))
               .func_230529_a_(new StringTextComponent(" chance to "))
               .func_230529_a_(new StringTextComponent(modifier.operation.title).func_240699_a_(modifier.operation.color))
               .func_230529_a_(new StringTextComponent(" the "))
               .func_230529_a_(new StringTextComponent(modifier.name).func_240699_a_(TextFormatting.AQUA))
               .func_230529_a_(new StringTextComponent(" modifier!"))
         );
      }
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      ListNBT modifiersList = new ListNBT();
      this.modifiers.forEach(modifier -> modifiersList.add(modifier.toNBT()));
      nbt.func_218657_a("Modifiers", modifiersList);
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.modifiers.clear();
      ListNBT modifiersList = nbt.func_150295_c("Modifiers", 10);
      modifiersList.forEach(inbt -> this.modifiers.add(CrystalData.Modifier.fromNBT((CompoundNBT)inbt)));
   }

   public static class Modifier {
      public final String name;
      public final CrystalData.Modifier.Operation operation;
      public final float chance;

      public Modifier(String name, CrystalData.Modifier.Operation operation, float chance) {
         this.name = name;
         this.operation = operation;
         this.chance = chance;
      }

      public void apply(VaultModifiers modifiers, Random random) {
         if (this.operation == CrystalData.Modifier.Operation.ADD) {
            if (random.nextFloat() < this.chance) {
               modifiers.add(this.name);
            }
         } else if (this.operation == CrystalData.Modifier.Operation.REMOVE && random.nextFloat() < this.chance) {
            modifiers.remove(this.name);
         }
      }

      public CompoundNBT toNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Name", this.name);
         nbt.func_74768_a("Operation", this.operation.ordinal());
         nbt.func_74776_a("Chance", this.chance);
         return nbt;
      }

      public static CrystalData.Modifier fromNBT(CompoundNBT nbt) {
         return new CrystalData.Modifier(
            nbt.func_74779_i("Name"), CrystalData.Modifier.Operation.values()[nbt.func_74762_e("Operation")], nbt.func_74760_g("Chance")
         );
      }

      public static enum Operation {
         ADD("add", TextFormatting.GREEN),
         REMOVE("cancel", TextFormatting.RED);

         public final String title;
         private final TextFormatting color;

         private Operation(String title, TextFormatting color) {
            this.title = title;
            this.color = color;
         }
      }
   }
}
