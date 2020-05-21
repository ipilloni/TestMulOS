package com.example.demo;

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

@ConfigurationProperties(prefix="multiva.properties.util")
@Component
public class MultivaConfiguration {
	

	private String file;
	private String fkey;

	
	public String getFile() { return file; }
	public void setFile(String f) { file = f; }
	
	public String getfKey() { return fkey; }
	public void setfKey(String k) { fkey = k; }
	

	


}
