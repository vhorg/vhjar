package iskallia.vault.task.renderer;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.Task;
import iskallia.vault.task.renderer.context.RendererContext;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class CardTaskRenderer extends TaskRenderer<Task, RendererContext> {
   private Component tooltip;

   public Component getTooltip() {
      return this.tooltip;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.COMPONENT.asNullable().writeBits(this.tooltip, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.tooltip = Adapters.COMPONENT.asNullable().readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.COMPONENT.writeNbt(this.tooltip).ifPresent(value -> nbt.put("tooltip", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.tooltip = Adapters.COMPONENT.readNbt(nbt.get("tooltip")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.COMPONENT.writeJson(this.tooltip).ifPresent(value -> json.add("tooltip", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.tooltip = Adapters.COMPONENT.readJson(json.get("tooltip")).orElse(null);
   }
}
