package org.gbif.ipt.action.manage;

import org.apache.commons.lang3.StringUtils;
import org.gbif.ipt.action.BaseAction;
import org.gbif.ipt.config.AppConfig;
import org.gbif.ipt.config.Constants;
import org.gbif.ipt.model.Resource;
import org.gbif.ipt.service.admin.RegistrationManager;
import org.gbif.ipt.service.admin.VocabulariesManager;
import org.gbif.ipt.service.manage.ResourceManager;
import org.gbif.ipt.struts2.SimpleTextProvider;
import org.gbif.ipt.utils.MapUtils;
import org.gbif.ipt.validation.EmlValidator;
import org.gbif.metadata.eml.Eml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class HomeAction extends BaseAction {

  private List<Resource> resources = new ArrayList<Resource>();

  private final ResourceManager resourceManager;
  private final VocabulariesManager vocabManager;
  private Map<String, String> types;
  private Map<String, String> datasetSubtypes;
  
  //To show info in table CR-SiB
  private List<String> cond = new ArrayList<String>();
  private List<String> authority = new ArrayList<String>();
  private List<String> number = new ArrayList<String>();
  private List<String> holder = new ArrayList<String>();
  private List<String> identification = new ArrayList<String>();
  private List<String> date = new ArrayList<String>();
  private List<String> dateCert = new ArrayList<String>();
  private List<String> codCert = new ArrayList<String>();
  //To show the authorities CR-SiB
  private Map<String, String> authoritiesHome;
  //Validate eml (CR-SiB)
  private EmlValidator emlValidator;

  @Inject
  public HomeAction(SimpleTextProvider textProvider, AppConfig cfg, RegistrationManager registrationManager,
    ResourceManager resourceManager, VocabulariesManager vocabManager) {
    super(textProvider, cfg, registrationManager);
    this.resourceManager = resourceManager;
    this.vocabManager = vocabManager;
    this.emlValidator = new EmlValidator(cfg, registrationManager, textProvider);
  }

  @Override
  public String execute() {
    resources = resourceManager.list(getCurrentUser());
    
    //CR-SiB
    authoritiesHome = new LinkedHashMap<String, String>();
    authoritiesHome.putAll(cfg.loadAuthorities());
    
    //Flag to show if the resource is incomplete CR-SiB
    boolean missingMetadata=false;
    
    //CR-SiB
    for(Resource res : resources){
    	if(!emlValidator.isValidGen(res.getEml(), "basic")){
      	  missingMetadata = true;
        }
        
        if(!emlValidator.isValidGen(res.getEml(), "additional")){
      	  missingMetadata = true;
        }
        
        if(!emlValidator.isValidGen(res.getEml(), "geocoverage")){
      	  missingMetadata = true;
        }
        
        if(!emlValidator.isValidGen(res.getEml(), "taxcoverage")){
      	  missingMetadata = true;
        }
        
        if(!emlValidator.isValidGen(res.getEml(), "tempcoverage")){
      	  missingMetadata = true;
        }
        
        if(!emlValidator.isValidGen(res.getEml(), "methods")){
      	  missingMetadata = true;
        }
        
        if(res.getEmlVersion()==0){
        	cond.add("P"); //En proceso
        }else {
        	cond.add("R"); //Reportado
        }
        
        missingMetadata=false;
        
        String aut="--";
		String num="--";
		String hol="--";
		String nit="--";
		String dte="--";
        
        Eml eml = res.getEml();
        eml.getAdditionalInfo();
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

    // Dataset core type list, derived from XML vocabulary, and displayed in drop-down on Basic Metadata page
    types = new LinkedHashMap<String, String>();
    types.put("", getText("manage.resource.create.coreType.selection"));
    types.putAll(vocabManager.getI18nVocab(Constants.VOCAB_URI_DATASET_TYPE, getLocaleLanguage(), false));
    types = MapUtils.getMapWithLowercaseKeys(types);

    // Dataset Subtypes list, derived from XML vocabulary
    datasetSubtypes = new LinkedHashMap<String, String>();
    datasetSubtypes.putAll(vocabManager.getI18nVocab(Constants.VOCAB_URI_DATASET_SUBTYPES, getLocaleLanguage(), false));
    datasetSubtypes = MapUtils.getMapWithLowercaseKeys(datasetSubtypes);

    return SUCCESS;
  }

  public List<Resource> getResources() {
    return resources;
  }
  
  public List<String> getCond() {
	    return cond;
  }

  /**
   * Get map of resource types to populate resource type selection.
   * 
   * @return map of resource types
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

  /**
   * method for dealing with the action for a locked resource.
   * Does nothing but the regular home plus an error message
   */
  public String locked() {
    addActionError(getText("manage.home.resource.locked"));
    return execute();
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
  
  //CR-SiB
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
