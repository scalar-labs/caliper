package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.example.contract.smallbank.Const;

public class DepositChecking extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String customerId = "" + argument.getInt(Const.CID);
    int amount = argument.getInt(Const.AMOUNT);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    JsonObject data = asset.get().data();
    int checking_balance = data.getInt(Const.C_BALANCE);
    checking_balance += amount;

    JsonObjectBuilder new_data = Json.createObjectBuilder();
    data.forEach(new_data::add);
    new_data.add(Const.C_BALANCE, checking_balance);
    ledger.put(customerId, new_data.build());

    return null;
  }
}
