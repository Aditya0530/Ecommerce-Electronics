package com.ecommerce.main.dto;


import java.sql.Date;
import java.sql.Time;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDto {
	private String message;

	private String date;
	private String time;

	public ErrorDto(String message) {
		this.message = message;
		this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		this.time = new SimpleDateFormat("HH:mm:ss").format(new Time(System.currentTimeMillis()));

	}
}
