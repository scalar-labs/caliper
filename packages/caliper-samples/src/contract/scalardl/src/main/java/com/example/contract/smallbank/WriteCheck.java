package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class WriteCheck extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String customerId = "" + argument.getInt(Const.KEY_CUSTOMER_ID);
    int amount = argument.getInt(Const.KEY_AMOUNT);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    JsonObject data = asset.get().data();
    int checkingBalance = data.getInt(Const.KEY_CHECKING_BALANCE);
    checkingBalance -= amount;

    JsonObjectBuilder newData = Json.createObjectBuilder();
    data.forEach(newData::add);
    newData.add(Const.KEY_CHECKING_BALANCE, checkingBalance);
    ledger.put(customerId, newData.build());

    return null;
  }
}