package com.example.contract.simple;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class SimpleTransfer extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    if (!argument.containsKey("account1")
        || !argument.containsKey("account2")
        || !argument.containsKey("money")) {
      throw new ContractContextException(
          "Please set 'account1', 'account2' and 'money' in the argument");
    }

    String srcAccountId = argument.getString("account1");
    String dstAccountId = argument.getString("account2");
    // open.js sends amount as number but transfer.js sends it as string...
    int amount = Integer.parseInt(argument.getString("money"));

    Optional<Asset> srcAsset = ledger.get(srcAccountId);
    Optional<Asset> dstAsset = ledger.get(dstAccountId);
    if (!srcAsset.isPresent() || !dstAsset.isPresent()) {
      throw new ContractContextException("Specified account does not exist");
    }

    JsonObject srcData = srcAsset.get().data();
    JsonObject dstData = dstAsset.get().data();
    int srcBalance = srcData.getInt("balance");
    int dstBalance = dstData.getInt("balance");
    srcBalance -= amount;
    dstBalance += amount;

    JsonObjectBuilder newSrcData = Json.createObjectBuilder(srcData);
    JsonObjectBuilder newDstData = Json.createObjectBuilder(dstData);
    newSrcData.add("balance", srcBalance);
    newDstData.add("balance", dstBalance);
    ledger.put(srcAccountId, newSrcData.build());
    ledger.put(dstAccountId, newDstData.build());

    return null;
  }
}
