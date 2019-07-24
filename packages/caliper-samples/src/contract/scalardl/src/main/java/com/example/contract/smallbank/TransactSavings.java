package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class TransactSavings extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    if (!argument.containsKey(Const.KEY_CUSTOMER_ID) || !argument.containsKey(Const.KEY_AMOUNT)) {
      throw new ContractContextException(
          "Please set " + Const.KEY_CUSTOMER_ID + " and " + Const.KEY_AMOUNT + " in the argument");
    }

    String customerId = Integer.toString(argument.getInt(Const.KEY_CUSTOMER_ID));
    int amount = argument.getInt(Const.KEY_AMOUNT);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    JsonObject data = asset.get().data();
    int savingsBalance = data.getInt(Const.KEY_SAVINGS_BALANCE);
    savingsBalance += amount;

    JsonObjectBuilder newData = Json.createObjectBuilder(data);
    newData.add(Const.KEY_SAVINGS_BALANCE, savingsBalance);
    ledger.put(customerId, newData.build());

    return null;
  }
}
