package com.demo.customer;

public class CustomerView
{
  private String viewKey;
  private String customerName;
  private String country;
  private int level;
  private String orderProduct;
  private String company;
  private String problems;
  
  public String getViewKey() {
    return this.viewKey;
  } 
  public void setViewKey(String viewKey) {
    this.viewKey = viewKey;
  } 
  public String getCustomerName() {
    return this.customerName;
  } 
  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  } 
  public String getCountry() {
    return this.country;
  } 
  public void setCountry(String country) {
    this.country = country;
  } 
  public int getLevel() {
    return this.level;
  } 
  public void setLevel(int level) {
    this.level = level;
  } 
  public String getOrderProduct() {
    return this.orderProduct;
  } 
  public void setOrderProduct(String orderProduct) {
    this.orderProduct = orderProduct;
  } 
  public String getCompany() {
    return this.company;
  } 
  public void setCompany(String company) {
    this.company = company;
  } 
  public String getProblems() {
    return this.problems;
  } 
  public void setProblems(String problems) {
    this.problems = problems;
  } 
}