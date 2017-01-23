package org.gbif.ipt.action.portal;

import org.apache.commons.lang3.StringUtils;
import org.gbif.ipt.action.BaseAction;
import org.gbif.ipt.config.AppConfig;
import org.gbif.ipt.config.Constants;
import org.gbif.ipt.model.Resource;
import org.gbif.ipt.model.voc.PublicationStatus;
import org.gbif.ipt.service.admin.RegistrationManager;
import org.gbif.ipt.service.admin.VocabulariesManager;
import org.gbif.ipt.service.manage.ResourceManager;
import org.gbif.ipt.struts2.SimpleTextProvider;
import org.gbif.ipt.utils.MapUtils;
import org.gbif.metadata.eml.Eml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class HomeAction extends BaseAction {

  private final ResourceManager resourceManager;
  private final VocabulariesManager vocabManager;

  private List<Resource> resources;
  private Map<String, String> types;
  private Map<String, String> datasetSubtypes;
  
  //Information to show in tables Home CR-SIB
  private List<String> cond = new ArrayList<String>();
  private List<String> authority = new ArrayList<String>();
  private List<String> number = new ArrayList<String>();
  private List<String> holder = new ArrayList<String>();
  private List<String> identification = new ArrayList<String>();
  private List<String> date = new ArrayList<String>();
  private List<String> dateCert = new ArrayList<String>();
  private List<String> codCert = new ArrayList<String>();
  


//to show the authorities
  private Map<String, String> authoritiesHome;


  @Inject
  public HomeAction(SimpleTextProvider textProvider, AppConfig cfg, RegistrationManager registrationManager,
    ResourceManager resourceManager, VocabulariesManager vocabManager) {
    super(textProvider, cfg, registrationManager);
    this.resourceManager = resourceManager;
    this.vocabManager = vocabManager;
  }

  @Override
  public String execute() {
    return SUCCESS;
  }

  @Override
  public void prepare() {
    super.prepare();
    resources = resourceManager.list(PublicationStatus.PUBLIC);
    resources.addAll(resourceManager.list(PublicationStatus.REGISTERED));
    // sort alphabetically
    Collections.sort(resources);
    
    //CR-SiB
    authoritiesHome = new LinkedHashMap<String, String>();
    authoritiesHome.putAll(cfg.loadAuthorities());
    
    String aut="--";
	String num="--";
	String hol="--";
	String nit="--";
	String dte="--";
	Eml eml = null;
    
    //To show informatio on Table (CR-SiB)
    for(Resource res : resources){
        aut="--";
		num="--";
		hol="--";
		nit="--";
		dte="--";
		
		eml = res.getEml();
		
		if(exists(eml.getAdditionalInfo())){
			String addIn=eml.getAdditionalInfo();
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
		authority.add(aut);
        number.add(num);
        holder.add(hol);
        identification.add(nit);
        date.add(dte);
        
        String cert=res.getCertNameFile();
        long timeLong=Long.parseLong(cert, 16); 
    	Date dateN = new Date();
    	dateN.setTime(timeLong);
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	dateCert.add(dateFormat.format(dateN));
    	codCert.add(cert);
    }
    
    // Dataset core type list, derived from XML vocabulary
    types = new LinkedHashMap<String, String>();
    types.putAll(vocabManager.getI18nVocab(Constants.VOCAB_URI_DATASET_TYPE, getLocaleLanguage(), false));
    types = MapUtils.getMapWithLowercaseKeys(types);

    // Dataset Subtypes list, derived from XML vocabulary
    datasetSubtypes = new LinkedHashMap<String, String>();
    datasetSubtypes.putAll(vocabManager.getI18nVocab(Constants.VOCAB_URI_DATASET_SUBTYPES, getLocaleLanguage(), false));
    datasetSubtypes = MapUtils.getMapWithLowercaseKeys(datasetSubtypes);
  }

  /**
   * A list of all public or registered resources.
   * 
   * @return a list of resources
   */
  public List<Resource> getResources() {
    return resources;
  }

  /**
   * A map of dataset types keys to internationalized values.
   * 
   * @return map of dataset subtypes
   */
  public Map<String, String> getTypes() {
    return types;
  }

  /**
   * A map of dataset subtypes keys to internationalized values.
   * 
   * @return map of dataset subtypes
   */
  public Map<String, String> getDatasetSubtypes() {
    return datasetSubtypes;
  }
  
  public List<String> getCond() {
	return cond;
  }

  public void setCond(List<String> cond) {
	this.cond = cond;
  }

  public List<String> getAuthority() {
	return authority;
  }

  public void setAuthority(List<String> authority) {
	this.authority = authority;
  }

  public List<String> getNumber() {
	return number;
  }

  public void setNumber(List<String> number) {
	this.number = number;
  }

  public List<String> getHolder() {
	return holder;
  }

  public void setHolder(List<String> holder) {
	this.holder = holder;
  }

  public List<String> getIdentification() {
	return identification;
  }

  public void setIdentification(List<String> identification) {
	this.identification = identification;
  }

  public List<String> getDate() {
	return date;
  }

  public void setDate(List<String> date) {
	this.date = date;
  }
  
  public List<String> getDateCert() {
		return dateCert;
	}

	public void setDateCert(List<String> dateCert) {
		this.dateCert = dateCert;
	}

	public void setDatasetSubtypes(Map<String, String> datasetSubtypes) {
		this.datasetSubtypes = datasetSubtypes;
	}
	
	public List<String> getCodCert() {
		return codCert;
	}

	public void setCodCert(List<String> codCert) {
		this.codCert = codCert;
	}
  
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
