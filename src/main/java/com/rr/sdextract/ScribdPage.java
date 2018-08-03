package com.rr.sdextract;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ScribdPage {

	private String path;
	private String url;
	private String html;
	
	private List<String> scripts;
	private List<String> jsonP;
	private List<String> imgUrls;
	private int pageCount;
	private String title;
	
	private MainGUI mainGui;

	public ScribdPage(MainGUI mainGui) {
		this.mainGui = mainGui;
	}
	
	public void parse() {
		mainGui.getScanText().setText("0%");
		mainGui.getImgText().setText("0%");
		mainGui.getPdfText().setText("0%");
		
		html = HttpHelper.getAsString(url);
		scripts = new ArrayList<String>();
		jsonP = new ArrayList<String>();
		imgUrls = new ArrayList<String>();
		
		parseForTitle();
		parseForPageCount();
		
		mainGui.getScanBar().setMinimum(0);
		mainGui.getScanBar().setMaximum(pageCount);
		mainGui.getScanBar().setValue(0);
		
		mainGui.getImgBar().setMinimum(0);
		mainGui.getImgBar().setMaximum(pageCount);
		mainGui.getImgBar().setValue(0);

		mainGui.getPdfBar().setMinimum(0);
		mainGui.getPdfBar().setMaximum(pageCount);
		mainGui.getPdfBar().setValue(0);
		
		parseForScripts();
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		int n=1;
		for(String script : scripts) {
			parseForJsonP(script);
			mainGui.getScanBar().setValue(n);
			mainGui.getScanText().setText(df.format(100*n/pageCount)+"%");
			n++;
		}
		mainGui.getScanText().setText("Done!");

		n=1;
		for(String jUrl : jsonP) {
			parseForActualUrl(jUrl);
			mainGui.getImgBar().setValue(n);
			mainGui.getImgText().setText(df.format(100*n/pageCount)+"%");
			n++;
		}
		mainGui.getImgText().setText("Done!");
		
		String filename = title+".pdf";
		File output = new File(path+"/"+filename);
		
		PdfGenerator pGen = new PdfGenerator(mainGui, output, imgUrls);
		pGen.create();
	}
	
	public void parseForTitle() {
		int startScript = 0;
		int endScript = 0;
		
		startScript = html.indexOf("<h1 class=\"doc_title\">");
		if(startScript > -1) {
			endScript = html.indexOf("</h1>", startScript);
			title = html.substring(startScript+22, endScript);
		}
	}
	
	public void parseForPageCount() {
		int pointer = 0;
		
		int startScript = 0;
		int endScript = 0;
		
		while(startScript != -1) {
			startScript = html.indexOf("<script", Math.min(pointer, html.length()));
			if(startScript > -1) {
				endScript = html.indexOf("</script", startScript);
				String script = html.substring(startScript, endScript);
				if(script.contains("page_count")) {
					parsePageCount(script);
					return;
				}				
				pointer = endScript;
			}
		}
	}
	
	public void parsePageCount(String script) {
		int startP = script.indexOf("page_count")+12;
		int endP = script.indexOf(",", startP);
		String pc = script.substring(startP, endP);
		pageCount = Integer.valueOf(pc);		
	}
	
	public void parseForScripts() {
		int pointer = 0;
		
		int startScript = 0;
		int endScript = 0;
		
		while(startScript != -1) {
			startScript = html.indexOf("<script", Math.min(pointer, html.length()));
			if(startScript > -1) {
				endScript = html.indexOf("</script", startScript);
				String script = html.substring(startScript, endScript);
				if(script.contains("pageParams.contentUrl")) {
					scripts.add(script);
				}
				pointer = endScript;
			}
		}
	}
	
	public void parseForJsonP(String script) {
		String jUrl = extractUrl(script);
		jsonP.add(jUrl);
	}
	
	public void parseForActualUrl(String jUrl) {
		String data = HttpHelper.getAsString(jUrl);
		String imgUrl = extractUrl(data);
		imgUrls.add(imgUrl);
	}
	
	private String extractUrl(String data) {
		int startHttp = data.indexOf("http");
		if(startHttp > -1) {
			int endHttp = data.indexOf("\"", startHttp);
			String url = data.substring(startHttp, endHttp);
			while(url.endsWith("/") || url.endsWith("\\")) {
				url = url.substring(0, url.length()-1);
			}
			return url;
		}
		return "";
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
