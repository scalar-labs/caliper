package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.JsonObject;

public class QueryAccount extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String customerId = "" + argument.getInt(Const.KEY_QUERY_KEY);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    return asset.get().data();
  }
}