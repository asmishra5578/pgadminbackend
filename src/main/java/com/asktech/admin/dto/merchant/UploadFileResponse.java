package com.asktech.admin.dto.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {
	private String fileName;
	private String fileDownloadLink;
    private String fileUploadStatus;

}
