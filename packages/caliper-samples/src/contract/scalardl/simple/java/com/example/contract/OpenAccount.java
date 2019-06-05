package com.example.contract;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

public class OpenAccount extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String accountId = argument.getString("account");
    int balance = argument.getInt("money");

    Optional<Asset> asset = ledger.get(accountId);

    if (!asset.isPresent()) {
      ledger.put(accountId, Json.createObjectBuilder().add("balance", balance).build());
    }
    // Should write error handling for case that account already exists

    return null;
  }
}
