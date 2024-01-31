package com.asktech.admin.dto.admin;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileResponseDto {
	
	private String fileUrl;
	private File fileData; 
	private String fileName;

}
