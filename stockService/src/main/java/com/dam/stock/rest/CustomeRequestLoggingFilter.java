package com.dam.stock.rest;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

public class CustomeRequestLoggingFilter 
extends CommonsRequestLoggingFilter {

  public CustomeRequestLoggingFilter() {
      super.setIncludeQueryString(true);
      super.setIncludePayload(true);
      super.setMaxPayloadLength(10000);
  }
}