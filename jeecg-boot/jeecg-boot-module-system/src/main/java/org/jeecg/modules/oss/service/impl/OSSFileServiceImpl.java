package org.jeecg.modules.oss.service.impl;

import java.io.IOException;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.config.oss.OSSManager;
import org.jeecg.config.oss.OSSProperties;
import org.jeecg.modules.oss.entity.OSSFile;
import org.jeecg.modules.oss.mapper.OSSFileMapper;
import org.jeecg.modules.oss.service.IOSSFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("ossFileService")
public class OSSFileServiceImpl extends ServiceImpl<OSSFileMapper, OSSFile> implements IOSSFileService {

	@Autowired
	private OSSManager ossManager;

	@Autowired
	private OSSProperties properties;

	@Override
	public String upload(MultipartFile multipartFile) throws IOException {

		String orgName = multipartFile.getOriginalFilename();
		String fileName = properties.getProperties().get("customFolder") + "/" + orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
		OSSFile ossFile = new OSSFile();
		ossFile.setFileName(fileName);
		ossFile.setUrl(properties.getProperties().get("customDomain") + "/" + fileName);
		this.save(ossFile);
		ossManager.upload(fileName, multipartFile.getInputStream());
		return ossFile.getUrl();
	}

	@Override
	public boolean delete(OSSFile ossFile) {
		try {
			this.removeById(ossFile.getId());
			ossManager.delete(ossFile.getFileName());
		}
		catch (Exception ex) {
			return false;
		}
		return true;
	}

}
