package com.example.contract.smallbank;

public class Const {

  private Const(){}

  /* Key names for JSON objects stored in Scalar DL */
  public static final String CID = "customer_id";
  public static final String CNAME = "customer_name";
  public static final String C_BALANCE = "checking_balance";
  public static final String S_BALANCE = "savings_balance";

  /* Key names for argments from Hyperledger Caliper */
  public static final String AMOUNT = "amount";
  public static final String INIT_C_BALANCE = "initial_checking_balance";
  public static final String INIT_S_BALANCE = "initial_savings_balance";
  public static final String SRC_CID = "source_customer_id";
  public static final String DST_CID = "dest_customer_id";
  public static final String QUERY_KEY = "query_key"; // For Scalar DL Adapter

  /* Error messages */
  public static final String ERR_NOT_FOUND = "Could not find specified account";

}
