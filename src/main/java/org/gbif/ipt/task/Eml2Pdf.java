package org.gbif.ipt.task;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.gbif.metadata.eml.GeospatialCoverage; //***
import org.gbif.ipt.config.AppConfig;
import org.gbif.ipt.config.Constants;
import org.gbif.ipt.model.ExcelFileSource;
import org.gbif.ipt.model.Resource;
import org.gbif.ipt.model.TextFileSource;
import org.gbif.ipt.service.manage.ResourceManager;
import org.gbif.ipt.service.admin.VocabulariesManager;
import org.gbif.metadata.eml.Eml;
import org.gbif.metadata.eml.KeywordSet;
import org.gbif.metadata.eml.TaxonKeyword;
import org.gbif.metadata.eml.TaxonomicCoverage;
import org.gbif.metadata.eml.BBox;
import org.gbif.ipt.utils.CoordinateUtils;
import org.gbif.metadata.eml.TemporalCoverage;
import org.gbif.metadata.eml.TemporalCoverageType;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.text.DateFormat;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.text.SimpleDateFormat;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;


@Singleton
public class Eml2Pdf {
	
	private ResourceBundle resourceBundle;
	@Inject
	private VocabulariesManager vocabManager;
	@Inject
	private AppConfig cfg;
	@Inject
	private ResourceManager resourceManager;
	private Map<String, String> roles;
	private Map<String, String> authoritiesHome;
	private String aut="--";
	private String num="--";
	private String hol="--";
	private String nit="--";
	private String dte="--";
	private String dateCert="--";
	protected Logger log = Logger.getLogger(this.getClass());
	
	/**
	   * Construct PDF document, mainly out of information extracted from Resource's EML object. 
	   *
	   * @param doc      Document
	   * @param resource Resource
	   * @param lng language of the document
	   *
	   * @throws DocumentException if problem occurs during add
	*/
	
	private void addMetaData(Document document)
	{
		log.info("Adding metadata info to document");
		document.addTitle(getText("pdf.title.doc"));
		document.addSubject(getText("pdf.subj.doc"));
		document.addAuthor(getText("pdf.author.doc"));
		document.addCreator(getText("pdf.creator.doc"));
	}
	
	private static void parseHTML(Document doc, String html, PdfWriter pdf) throws IOException
	{
		InputStream is = new ByteArrayInputStream(html.getBytes()); 
		XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
		worker.parseXHtml(pdf, doc, is);
		//worker.parseXHtml(pdf, doc, new StringReader(html));
	}
	
	public void writeEmlIntoPdf (Document doc, Resource resource, PdfWriter pdfWriter, String lng, String cert, String url)  {
		Locale loc = new Locale(lng);
		String[] dataCert=cert.split("-");
		resourceBundle = ResourceBundle.getBundle("ApplicationResources", loc);
		// this.action = action;
		authoritiesHome = new LinkedHashMap<String, String>();
	    authoritiesHome.putAll(cfg.loadAuthorities());
	  //roles list, derived from XML vocabulary, and displayed in drop-down where new contacts are created
	    roles = new LinkedHashMap<String, String>();
	    roles.put("", getText("eml.agent.role.selection"));
	    roles.putAll(vocabManager.getI18nVocab(Constants.VOCAB_URI_ROLES, "es", false));
	    Eml eml = resource.getEml();
	    setInfo(eml);
	    setDate(dataCert[0]);
	    String keys = "";
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    for (KeywordSet kw : eml.getKeywords()) {
	      if (keys.length() == 0) {
	        keys = kw.getKeywordsString(", ");
	      } else {
	        keys += ", " + kw.getKeywordsString(", ");
	      }
	    }
	    doc.addKeywords(keys);
	    addMetaData(doc);
	    doc.open();
	    
	    String htmlText = "<html>";
	    htmlText += "<style type='text/css'>";
	    htmlText += " .mainTable{width:100%;} "
	    			+ ".nota{color:#e75170;font-family:sans-serif;font-size: 14px;padding: 40px;font-weight:bold;margin-top:90px;margin-botom:400px}"
	    			+ ".centerLine{text-align:center;} "
	    			+ ".subtitle{color:#ebf7fb;} "
	    			+ ".resumeInfo{width: 418px;padding: 40px;}"
	    			+ "h4{font-family:sans-serif;font-size: 22px;color: #58585b}"
	    			+ "span{color:#268ac7}"
	    			+ "label{font-family:sans-serif;font-size: 18px;color: #58585b;margin-bottom:5px;font-weight:100}"
	    			+ "a{color:#43afe3}"
	    			+ "b{font-weight:bold}"
	    			+ "h2{font-family:sans-serif;font-size: 26px;color: #58585b}"
	    			+ "h3{font-family:sans-serif;color: #58585b}"
	    			+ "p{font-family:sans-serif;font-size: 16px;color: #58585b;font-weight:100;text-align:justify;}";
	    htmlText += "</style>";
	    htmlText += "<body><table class='mainTable'><tr align='center' class='centerLine'><td>";
	    htmlText += "<img width='520px' src='"+url+"/styles/certificado_header_crsibA.png' class='imgTitle'/>";
	    htmlText += "</td></tr>";
	    htmlText += "<tr align='center' class='centerLine'><td>";
	    htmlText += "</td></tr>";
	    htmlText += "<tr><td class='resumeInfo'>";
	    htmlText += "<p id='resumeInfo'>";
	    htmlText += "<h4><span>1. </span>"+getText("pdf.info.cert")+"</h4>";
	    htmlText +="<br />";
	    htmlText += "<label>"+getText("pdf.cert.number")+"<b>"+dataCert[0]+"</b></label><br/>";
	    htmlText += "<label>"+getText("pdf.cert.resource.actu")+"<b>"+dateFormat.format(resource.getModified())+"</b></label><br/>";
	    htmlText += "<label>"+getText("pdf.cert.resource.url")+"<a href='"+url+"/resource.do?r="+resource.getShortname()+"'>"+url+"/resource.do?r="+resource.getShortname()+"</a></label><br/>";
	    
	   
	    if(resource.getRecordsPublished()!=0){
	    	htmlText += "<label>"+getText("pdf.cert.resource.records")+"<b>"+resource.getRecordsPublished()+"</b></label>";
	    }else{
	    	if(resource.getSources().size()>0){
	    		if(resource.getSources().get(0).isExcelSource()){
	    			ExcelFileSource excl = new ExcelFileSource();
	    			excl =(ExcelFileSource) resource.getSources().get(0);
	    			htmlText += "<label>"+getText("pdf.cert.resource.records")+"<b>"+excl.getRows()+"</b></label>";
	    		}else if(resource.getSources().get(0).isFileSource()){
	    			TextFileSource txtcl = new TextFileSource();
	    			txtcl = (TextFileSource) resource.getSources().get(0);
	    			htmlText += "<label>"+getText("pdf.cert.resource.records")+"<b>"+txtcl.getRows()+"</b></label>";
	    		}
	    	}
	    }
	    
	    
	    htmlText += "</p>";
	    htmlText += "</td></tr>";
	    
	    
	    htmlText += "<tr ><td style='text-align:justify;'>";
	    //htmlText +="<br />";
	    htmlText += "<h2><span>2. </span>"+getText("pdf.license")+"</h2> ";
	    htmlText += "<p><b>"+getText("pdf.license.authority")+"</b><br/>"+aut+"<br/>";
	    htmlText +=	"<b>"+getText("pdf.license.number")+"</b><br/>"+num+"<br/>";
	    htmlText +=	"<b>"+getText("pdf.license.holder")+"</b><br/>"+hol+"<br/>";
	    htmlText += "<b>"+getText("pdf.license.nit")+"</b><br/>"+nit+"<br/>";
	    htmlText +=	"<b>"+getText("pdf.license.date")+"</b><br/>"+dte+"</p>"; 
	    
	    htmlText += "<h2><span>3. </span>"+getText("pdf.info.project")+"</h2> ";
	    htmlText += "<p>";
	    htmlText += "<b>"+getText("pdf.title.proyect")+"</b><br/>";
	    htmlText += eml.getTitle().replaceAll("<", "&#60;").replaceAll(">", "&#62;");;
	    htmlText += "</p>";
	    htmlText += "<p>";
	    htmlText += "<b>"+getText("pdf.abstract")+"</b><br/>";
	    htmlText += eml.getDescription().replaceAll("<", "&#60;").replaceAll(">", "&#62;");;
	    htmlText += "</p>";
	    htmlText += "<p>";
	    
	    htmlText += "<b>"+getText("pdf.keywords")+"</b><br/>";
	    htmlText += keys.replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	    htmlText += "</p>";
	    
	    int subI=0;
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.contact.resource")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += "<b>"+getText("pdf.contact.resource.name")+"</b><br/>";
	    htmlText += eml.getContact().getFirstName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+" "+eml.getContact().getLastName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.contact.resource.position")+"</b><br/>";
	    htmlText += eml.getContact().getPosition().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.contact.resource.organisation")+"</b><br/>";
	    htmlText += eml.getContact().getOrganisation().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.contact.resource.address.address")+"</b><br/>";
	    htmlText += eml.getContact().getAddress().getAddress().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.contact.resource.address.city")+"</b><br/>";
	    htmlText += eml.getContact().getAddress().getCity().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getContact().getAddress().getPostalCode())){
	    	htmlText += "<b>"+getText("pdf.contact.resource.address.postalcode")+"</b><br/>"; 
	    	htmlText += eml.getContact().getAddress().getPostalCode().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "<b>"+getText("pdf.contact.resource.phone")+"</b><br/>";
	    htmlText += eml.getContact().getPhone().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.contact.resource.email")+"</b><br/>";
	    htmlText += eml.getContact().getEmail().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getContact().getHomepage())){
	    	htmlText += "<b>"+getText("pdf.contact.resource.homepage")+"</b><br/>"; //***
	    	htmlText += eml.getContact().getHomepage().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "</p>";
	   
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.resourceCreator")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.name")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getFirstName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+" "+eml.getResourceCreator().getLastName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.position")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getPosition().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.organisation")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getOrganisation().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.address.address")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getAddress().getAddress().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.address.city")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getAddress().getCity().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getResourceCreator().getAddress().getPostalCode())){
	    	htmlText += "<b>"+getText("pdf.resourceCreator.address.postalcode")+"</b><br/>"; 
	    	htmlText += eml.getResourceCreator().getAddress().getPostalCode().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "<b>"+getText("pdf.resourceCreator.phone")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getPhone().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.resourceCreator.email")+"</b><br/>";
	    htmlText += eml.getResourceCreator().getEmail().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getResourceCreator().getHomepage())){
	    	htmlText += "<b>"+getText("pdf.resourceCreator.homepage")+"</b><br/>"; //***
	    	htmlText += eml.getResourceCreator().getHomepage().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "</p>";
	    
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.metadataProvider")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.name")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getFirstName()+" "+eml.getMetadataProvider().getLastName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.position")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getPosition().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.organisation")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getOrganisation().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.address.address")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getAddress().getAddress().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.address.city")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getAddress().getCity().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getMetadataProvider().getAddress().getPostalCode())){
	    	htmlText += "<b>"+getText("pdf.metadataProvider.address.postalcode")+"</b><br/>"; 
	    	htmlText += eml.getMetadataProvider().getAddress().getPostalCode().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "<b>"+getText("pdf.metadataProvider.phone")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getPhone().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    htmlText += "<b>"+getText("pdf.metadataProvider.email")+"</b><br/>";
	    htmlText += eml.getMetadataProvider().getEmail().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    if(exists(eml.getMetadataProvider().getHomepage())){
	    	htmlText += "<b>"+getText("pdf.metadataProvider.homepage")+"</b><br/>"; //***
	    	htmlText += eml.getMetadataProvider().getHomepage().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    }
	    htmlText += "</p>";
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.spatialCoverage")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += getGeoCov(eml).replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	    htmlText += "</p>";
	    //------
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.taxcoverage")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += getTaxCov(eml);
	    htmlText += "</p>";
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.tempcoverage")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += getTempCov(eml, loc);
	    htmlText += "</p>";
	    
	    
	    subI++;
	    htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.methods")+"</h3> ";
	    htmlText += "<p>";
	    htmlText += eml.getSampleDescription().replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	    htmlText += "</p>";
	    
	    //-------
	    
	    
	    if(exists(eml.getCollectionName())){
	    	subI++;
	    	htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.collections")+"</h3> ";
	    	htmlText += "<p>";
	    	htmlText += "<b>"+getText("pdf.collections.name")+"</b><br/>";
	    	htmlText += eml.getCollectionName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	htmlText += "<b>"+getText("pdf.collections.identifier")+"</b><br/>";
	    	htmlText += eml.getCollectionId().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	htmlText += "<b>"+getText("pdf.collections.parent")+"</b><br/>";
	    	htmlText += eml.getParentCollectionId().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	Map<String, String> metp =vocabManager.getI18nVocab(Constants.VOCAB_URI_PRESERVATION_METHOD, "es", false);
	    	if(exists(eml.getSpecimenPreservationMethod())){
	    		htmlText += "<b>"+getText("pdf.collections.specimen")+"</b><br/>"; //***
	    		htmlText += metp.get(eml.getSpecimenPreservationMethod().replaceAll("<", "&#60;").replaceAll(">", "&#62;"))+"<br/>";
	    	}
	    	htmlText += "</p>";
	    }
	    
	    
	    if(exists(eml.getProject().getTitle())){
	    	subI++;
	    	htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.project")+"</h3> ";
	    	htmlText += "<p>";
	    	htmlText += "<b>"+getText("pdf.project.title")+"</b><br/>";
    		htmlText += eml.getProject().getTitle().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	if(exists(eml.getProject().getPersonnel())){
	    		htmlText += "<b>"+getText("pdf.project.personnel.firstName")+"</b><br/>";
	    		htmlText += eml.getProject().getPersonnel().getFirstName()+" "+eml.getProject().getPersonnel().getLastName()+"<br/>";
	    	}
	    	if(exists(eml.getProject().getPersonnel().getRole())){
	    		htmlText += "<b>"+getText("pdf.project.personnel.role")+"</b><br/>";
	    		htmlText += roles.get(eml.getProject().getPersonnel().getRole().replaceAll("<", "&#60;").replaceAll(">", "&#62;"))+"<br/>";
	    	}
	    	if(exists(eml.getProject().getFunding())){
	    		htmlText += "<b>"+getText("pdf.project.funding")+"</b><br/>";
	    		htmlText += eml.getProject().getFunding().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	}
	    	if(exists(eml.getProject().getStudyAreaDescription())){
	    		htmlText += "<b>"+getText("pdf.project.studyAreaDescription.descriptorValue")+"</b><br/>";
	    		htmlText += eml.getProject().getStudyAreaDescription().getDescriptorValue().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	}
	    	if(exists(eml.getProject().getDesignDescription())){
	    		htmlText += "<b>"+getText("pdf.project.designDescription")+"</b><br/>";
	    		htmlText += eml.getProject().getDesignDescription().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    	}
	    	htmlText += "</p>";
	    }
	    
	    
	    
	    if(eml.getAssociatedParties().size()!=0){
	    	if(exists(eml.getAssociatedParties().get(0))){
	    		subI++;
	    		htmlText += "<h3><span>3."+subI+" </span>"+getText("pdf.associatedParties")+"</h3> ";
	    		htmlText += "<p>";
	    		for(int i=0;i<eml.getAssociatedParties().size();i++){
	    			if(exists(eml.getAssociatedParties().get(i).getLastName())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.name")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getFirstName()+" "+eml.getAssociatedParties().get(i).getLastName().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getPosition())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.position")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getPosition().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getOrganisation())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.organisation")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getOrganisation().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getAddress().getAddress())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.address.address")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getAddress().getAddress().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getAddress().getCity())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.city")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getAddress().getCity().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getAddress().getPostalCode())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.address.postalcode")+"</b><br/>"; 
	    				htmlText += eml.getAssociatedParties().get(i).getAddress().getPostalCode().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getPhone())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.phone")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getPhone().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getEmail())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.email")+"</b><br/>";
	    				htmlText += eml.getAssociatedParties().get(i).getEmail().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			if(exists(eml.getAssociatedParties().get(i).getHomepage())){
	    				htmlText += "<b>"+getText("pdf.associatedParties.homepage")+"</b><br/>"; //***
	    				htmlText += eml.getAssociatedParties().get(i).getHomepage().replaceAll("<", "&#60;").replaceAll(">", "&#62;")+"<br/>";
	    			}
	    			htmlText += "<br/>";
	    	}
	    	htmlText += "</p>";
	    	}
	    }
	    
	    
	    htmlText += "</td>";
	    htmlText += "</tr>";
	    
	    htmlText += "<tr class='centerLine'>";
	    htmlText += "<td>";
	    htmlText += "</td>";
	    htmlText += "</tr>";
	    
	    htmlText += "</table>";
	    htmlText += "<p class='nota'>"+getText("pdf.cert.dis")+"<a href='"+url+"/pdf.do?r="+resource.getShortname()+"&n"+"="+dataCert[0]+"'>"+url+"/pdf.do?r="+resource.getShortname()+"&amp;n="+dataCert[0]+"</a></p><p></p></body></html>";
	    
	    try { 
	    	Image background = Image.getInstance(url+"/styles/certificado_bg1.png");
	    	float width = 498;
	    	float height = 174;
	    	pdfWriter.getDirectContentUnder().addImage(background, width, 0, 0, height, 50, 490);
	    	
	    	parseHTML(doc, htmlText,pdfWriter);
			
			Image background_1 = Image.getInstance(url+"/styles/certificado_footer.png");
	    	width = 498;
	    	height = 103;
	    	pdfWriter.getDirectContentUnder().addImage(background_1, width, 0, 0, height, 50, 20);
	    	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    doc.close();
	}
	
	
	/**
	   * Get text for resource bundle property key.
	   *
	   * @param key key
	   *
	   * @return text corresponding to key in property file
	   */
	  public String getText(String key) {
	    return resourceBundle.getString(key);
	  }

	  
	  public String getTaxCov(Eml eml){
		  String tax="";
		  boolean firstTaxon = true;
		  for (TaxonomicCoverage taxcoverage : eml.getTaxonomicCoverages()) {
	        if (!firstTaxon) {
	          tax +="<br />";
	        }
	        
	        firstTaxon = false;
	        
	        if (exists(taxcoverage.getDescription())) {
	          tax +=taxcoverage.getDescription().replace("\r\n", "\n").replaceAll("<", "&#60;").replaceAll(">", "&#62;")+" ";
	          tax +="<br />";
	        }
	        
	        Map<String, String> ranks =
	          //vocabManager.getI18nVocab(Constants.VOCAB_URI_RANKS, "es", false);
	          vocabManager.getI18nVocab(Constants.VOCAB_URI_RANKS, "es", false);
	        boolean firstRank = true;
	        
	        for (String rank : ranks.keySet()) {
	          boolean wroteRank = false;
	          for (TaxonKeyword keyword : taxcoverage.getTaxonKeywords()) {
	            if (exists(keyword.getRank()) && keyword.getRank().equals(rank)) {
	              if (!wroteRank) {
	                if (firstRank) {
					  tax +="<b>"+getText("pdf.taxcoverage.rank")+"</b>";
	                }
	                tax +="<br />";
	                tax +=StringUtils.capitalize(ranks.get(rank)) + ": ";
	                tax +=keyword.getScientificName().replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	                wroteRank = true;
	                firstRank = false;
	              } else {
	                tax +=", " + keyword.getScientificName().replaceAll("<", "&#60;").replaceAll(">", "&#62;");
	              }
	            }
	          }
	        }
	        
	        tax +="<br />";
	        boolean isFirst = true;
	        
	        for (TaxonKeyword keyword : taxcoverage.getTaxonKeywords()) {
	          if (exists(keyword.getCommonName())) {
	            if (isFirst) {
	              tax +="<b>"+getText("pdf.taxcoverage.common")+": "+"</b>";
	            } else {
	              tax +=", ";
	            }
	            isFirst = false;
	            tax +=keyword.getCommonName();
	          }
	        }
	        
	      }
		  return tax;
	  }
	  
	  
	  private String getGeoCov(Eml eml)  {
			String geotext="";
		      
		      boolean firstCoverage = true;
		      for (GeospatialCoverage coverage : eml.getGeospatialCoverages()) {
		        if (firstCoverage) {
		          firstCoverage = false;
		        } else {
		          geotext +="</br>";
		        }
		        if (exists(coverage.getDescription())) {
		          geotext += coverage.getDescription().replace("\r\n", "\n");
		          //geotext +="</br>";
		        }
		        geotext +=" "+getText("pdf.spatialCoverage.coordinates") + ": ";
		        BBox coordinates = coverage.getBoundingCoordinates();
		        geotext += CoordinateUtils.decToDms(coordinates.getMin().getLatitude(), CoordinateUtils.LATITUDE);
		        geotext += " " + getText("pdf.spatialCoverage.and") + " ";
		        geotext += CoordinateUtils.decToDms(coordinates.getMax().getLatitude(), CoordinateUtils.LATITUDE);
		        geotext += " " + getText("pdf.spatialCoverage.latitude") + "; ";
		        geotext += CoordinateUtils.decToDms(coordinates.getMin().getLongitude(), CoordinateUtils.LONGITUDE);
		        geotext += " " + getText("pdf.spatialCoverage.and") + " ";
		        geotext += CoordinateUtils.decToDms(coordinates.getMax().getLongitude(), CoordinateUtils.LONGITUDE);
		        geotext += " " + getText("pdf.spatialCoverage.longitude") + " ";
		      }
		  return geotext;
	  }
	  
	  
	  private String getTempCov(Eml eml, Locale loc) {
			String temp= "";
		    if (exists(eml.getTemporalCoverages()) && !eml.getTemporalCoverages().isEmpty()) {
		      DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, loc);
		      SimpleDateFormat timeFormat = new SimpleDateFormat("SSS", loc);
		      SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", loc);
		      boolean firstCoverage = true;
		      for (TemporalCoverage coverage : eml.getTemporalCoverages()) {
		        if (coverage.getType().equals(TemporalCoverageType.SINGLE_DATE)) {
		          if (firstCoverage) {
		            firstCoverage = false;
		          } else {
		            temp +="</br>";
		          }
		          if (timeFormat.format(coverage.getStartDate()).equals("001")) {
		            temp += yearFormat.format(coverage.getStartDate());
		          } else {
		            temp += dateFormat.format(coverage.getStartDate());
		          }
		        } else if (coverage.getType() == TemporalCoverageType.DATE_RANGE) {
		          if (firstCoverage) {
		            firstCoverage = false;
		          } else {
		            temp +="</br>";
		          }
		          if (timeFormat.format(coverage.getStartDate()).equals("001")) {
		            temp += yearFormat.format(coverage.getStartDate());
		          } else {
		            temp += dateFormat.format(coverage.getStartDate());
		          }
		          temp +=" - ";
		          if (timeFormat.format(coverage.getEndDate()).equals("001")) {
		            temp += yearFormat.format(coverage.getEndDate());
		          } else {
		            temp += dateFormat.format(coverage.getEndDate());
		          }
		        }
		      }
		    }
		    return temp;
	  }
	  
	  public void setInfo(Eml eml){
		  if(exists(eml.getAdditionalInfo())){
				String addIn=eml.getAdditionalInfo().replaceAll("<", "&#60;").replaceAll(">", "&#62;");
				if((addIn.indexOf("[autoridad]")!=-1)&&(addIn.indexOf("[numero]")!=-1)&&(addIn.indexOf("[titular]")!=-1)&&(addIn.indexOf("[nit o cedula]")!=-1)&&(addIn.indexOf("[fecha]")!=-1)){
					int autPos=addIn.indexOf("[autoridad]")+"[autoridad]".length();
					int numPos=addIn.indexOf("[numero]")+"[numero]".length();
					int holPos=addIn.indexOf("[titular]")+"[titular]".length();
					int nitPos=addIn.indexOf("[nit o cedula]")+"[nit o cedula]".length();
					int dtePos=addIn.indexOf("[fecha]")+"[fecha]".length();
				
					if(addIn.charAt(autPos)!='['){
						aut=authoritiesHome.get(addIn.substring(autPos, addIn.indexOf("[numero]")));
					}
				
					if(addIn.charAt(numPos)!='['){
						num=addIn.substring(numPos, addIn.indexOf("[titular]"));
					}
				
					if(addIn.charAt(holPos)!='['){
						hol=addIn.substring(holPos, addIn.indexOf("[nit o cedula]"));
					}
				
					if(addIn.charAt(nitPos)!='['){
						nit=addIn.substring(nitPos, addIn.indexOf("[fecha]"));
					}
				
					if(dtePos < addIn.length()){
						dte=addIn.substring(dtePos, addIn.length());
					}
				}
		  }
	  }
	  
	  private void setDate(String certName){
		  	String cert=certName;
		    long timeLong=Long.parseLong(cert, 16); 
			Date dateN = new Date();
			dateN.setTime(timeLong);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateCert=dateFormat.format(dateN);
	  }

	  public void setVocabManager(VocabulariesManager vocabManager) {
	    this.vocabManager = vocabManager;
	  }
	  
	  
	  /**
	   * Checks whether a given object is null. If the object is an instance of String, it checks if the String is an empty
	   * String. Only if the object is not null, and not an empty String, will the method return true.
	   *
	   * @param obj Object
	   *
	   * @return whether true if the object is not null, or not an empty string
	   */
	  private boolean exists(Object obj) {
	    if (obj == null) {
	      return false;
	    }
	    if (obj instanceof String) {
	      if (StringUtils.isEmpty((String) obj)) {
	        return false;
	      }
	    }
	    return true;
	  }
	  
	  
}