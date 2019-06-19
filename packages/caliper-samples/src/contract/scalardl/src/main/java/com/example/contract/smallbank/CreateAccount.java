package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;

public class CreateAccount extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String customerId = "" + argument.getInt(Const.KEY_CUSTOMER_ID);
    String customerName = argument.getString(Const.KEY_CUSTOMER_NAME);
    int checkingBalance = argument.getInt(Const.KEY_INIT_CHK_BALANCE);
    int savingsBalance = argument.getInt(Const.KEY_INIT_SV_BALANCE);

    Optional<Asset> asset = ledger.get(customerId);
    if (!asset.isPresent()) {
      ledger.put(
          customerId,
          Json.createObjectBuilder()
              .add(Const.KEY_CUSTOMER_NAME, customerName)
              .add(Const.KEY_CHECKING_BALANCE, checkingBalance)
              .add(Const.KEY_SAVINGS_BALANCE, savingsBalance)
              .build());
    }

    return null;
  }
}