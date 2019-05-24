package com.imall.common.domain;

import java.sql.Timestamp;

public class AccountFlow extends BasicDomain {

  /**
   * 时间
   */
  private Timestamp time;

  /**
   * 订单id
   */
  private Long orderId;


  /**
   *  币种：0-cash;3-inb;4-inbp
   */
  private Integer coinType;

  /**
   * 货币数量
   */
  private Double amount;

  /**
   * 类型：1-充值；2-提现
   */
  private Integer type;

  public Timestamp getTime() {
    return time;
  }

  public void setTime(Timestamp time) {
    this.time = time;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Integer getCoinType() {
    return coinType;
  }

  public void setCoinType(Integer coinType) {
    this.coinType = coinType;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
