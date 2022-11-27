package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EternalsNamepoolConfig extends Config {
   @Expose
   List<String> NAMEPOOL;

   @Override
   public String getName() {
      return "eternals_namepool";
   }

   public String getRandomName() {
      return this.NAMEPOOL.get(new Random().nextInt(this.NAMEPOOL.size()));
   }

   @Override
   protected void reset() {
      this.NAMEPOOL = new ArrayList<>();
      this.NAMEPOOL.add("iskall85");
      this.NAMEPOOL.add("iGoodie");
      this.NAMEPOOL.add("JoeFoxe");
   }
}
