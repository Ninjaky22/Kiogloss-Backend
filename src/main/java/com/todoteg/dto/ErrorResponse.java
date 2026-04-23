package com.todoteg.dto;

//Clase auxiliar para respuestas de error
public class ErrorResponse {
 private String detail;
 
 public ErrorResponse(String detail) {
     this.detail = detail;
 }
 
 public String getDetail() {
     return detail;
 }
 
 public void setDetail(String detail) {
     this.detail = detail;
 }
}