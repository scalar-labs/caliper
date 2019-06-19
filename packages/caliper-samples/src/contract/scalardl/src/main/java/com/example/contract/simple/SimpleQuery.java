package com.example.contract.simple;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.asset.InternalAsset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.JsonObject;

public class SimpleQuery extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String accountId = argument.getString("query_key");

    Optional<Asset> asset = ledger.get(accountId);
    InternalAsset internal = (InternalAsset) asset.get();

    return internal.data();
  }
}