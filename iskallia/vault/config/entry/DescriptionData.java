package iskallia.vault.config.entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import iskallia.vault.init.ModConfigs;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;

public class DescriptionData {
   @Expose
   private final JsonElement description;

   public DescriptionData(JsonElement description) {
      this.description = description;
   }

   public JsonElement getDescription() {
      return this.description;
   }

   public static DescriptionData getDefault(String name) {
      return new DescriptionData(
         JsonParser.parseString(
            "[{text:'Default config description for ', color:'$text'},{text: '"
               + name
               + "', color: '$name'},{text: ' - please configure me!', color: '$text'}]"
         )
      );
   }

   public MutableComponent getComponent() {
      JsonElement element = this.description;
      ModConfigs.COLORS.replaceColorStrings(element);
      return Serializer.fromJson(element);
   }
}
