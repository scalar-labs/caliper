package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

import com.example.contract.smallbank.Const;

public class CreateAccount extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String customerId = "" + argument.getInt(Const.CID);
    String customerName = argument.getString(Const.CNAME);
    int checking_balance = argument.getInt(Const.INIT_C_BALANCE);
    int savings_balance = argument.getInt(Const.INIT_S_BALANCE);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      ledger.put(customerId, Json.createObjectBuilder()
        .add(Const.CNAME, customerName)
        .add(Const.C_BALANCE, checking_balance)
        .add(Const.S_BALANCE, savings_balance)
        .build()
      );
    }

    return null;
  }
}
