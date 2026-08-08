package com.ecommerce.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailDetailsDto {
public String toMail;
public String subject;
public String text;
}
