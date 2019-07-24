package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class SendPayment extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String srcCustomerId = Integer.toString(argument.getInt(Const.KEY_SRC_CUSTOMER_ID));
    String dstCustomerId = Integer.toString(argument.getInt(Const.KEY_DST_CUSTOMER_ID));
    int amount = argument.getInt(Const.KEY_AMOUNT);

    Optional<Asset> srcAsset = ledger.get(srcCustomerId);
    if (!srcAsset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }
    Optional<Asset> dstAsset = ledger.get(dstCustomerId);
    if (!dstAsset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    JsonObject srcData = srcAsset.get().data();
    JsonObject dstData = dstAsset.get().data();
    int srcBalance = srcData.getInt(Const.KEY_CHECKING_BALANCE);
    int dstBalance = dstData.getInt(Const.KEY_CHECKING_BALANCE);
    srcBalance -= amount;
    dstBalance += amount;

    JsonObjectBuilder newSrcData = Json.createObjectBuilder();
    JsonObjectBuilder newDstData = Json.createObjectBuilder();
    srcData.forEach(newSrcData::add);
    dstData.forEach(newDstData::add);
    newSrcData.add(Const.KEY_CHECKING_BALANCE, srcBalance);
    newDstData.add(Const.KEY_CHECKING_BALANCE, dstBalance);
    ledger.put(srcCustomerId, newSrcData.build());
    ledger.put(dstCustomerId, newDstData.build());

    return null;
  }
}
