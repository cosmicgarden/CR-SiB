<#escape x as x?html>
<#setting number_format="#####.##">
<#include "/WEB-INF/pages/inc/header.ftl">
<#include "/WEB-INF/pages/macros/metadata.ftl"/>
<script type="text/javascript" src="${baseURL}/js/ajaxfileupload.js"></script>
<script type="text/javascript" src="${baseURL}/js/jconfirmation.jquery.js"></script>
<title><@s.text name='manage.metadata.additional.title'/></title>
<script type="text/javascript">
	$(document).ready(function(){
        <#if (Session.curr_user)??>

        </#if>

        <#if authorities??>
              <#list authorities?keys as key> 
                
              </#list>
        </#if>

        <#if eml.additionalInfo??>
        var admt='${eml.additionalInfo}';
        var aut=admt.indexOf('[autoridad]')+'[autoridad]'.length;
        var num=admt.indexOf('[numero]')+'[numero]'.length;
        var hol=admt.indexOf('[titular]')+'[titular]'.length;
        var nit=admt.indexOf('[nit o cedula]')+'[nit o cedula]'.length;
        var dte=admt.indexOf('[fecha]')+'[fecha]'.length;
        if(admt.charAt(aut)!='['){
            $('#eml\\.license\\.auto').val((admt.substring(aut, admt.indexOf('[numero]'))).replace(/&quot;/g, "\""));
        }
        if(admt.charAt(num)!='['){
            $('#eml\\.license\\.number').val((admt.substring(num, admt.indexOf('[titular]'))).replace(/&quot;/g, "\""));
        }
        <#if currentUser.getRole()=="Admin">
            if(admt.charAt(hol)!='['){
                $('#eml\\.license\\.holder').val((admt.substring(hol, admt.indexOf('[nit o cedula]'))).replace(/&quot;/g, "\""));
            }
            if(admt.charAt(nit)!='['){
                $('#eml\\.license\\.nit').val((admt.substring(nit, admt.indexOf('[fecha]'))).replace(/&quot;/g, "\""));
            }
        </#if>
        if(dte < admt.length){
            $('#startDate').val(admt.substring(dte, admt.length));
        }
        <#if currentUser.getRole()!="Admin">
            <#if (Session.curr_user)??>
                $('#eml\\.license\\.holder').val('${Session.curr_user.getName()}');
                $('#eml\\.license\\.nit').val('${Session.curr_user.getIdentification()}');
            </#if>
        </#if>
        <#else>
            <#if (Session.curr_user)??>
                $('#eml\\.license\\.holder').val('${Session.curr_user.getName()}');
                $('#eml\\.license\\.nit').val('${Session.curr_user.getIdentification()}');
            </#if>
        </#if>
        
        
        $( '#eml\\.license\\.holder' ).hide();        
        $( '#eml\\.license\\.nit' ).hide();

        $( 'label[for="eml\\.license\\.holder"]' ).hide();  
        $( 'label[for="eml\\.license\\.holder"]' ).next().hide();      
        $( 'label[for="eml\\.license\\.nit"]' ).hide();
        $( 'label[for="eml\\.license\\.nit"]' ).next().hide();

        var erFA=false;
        var erFN=false;
        var erFH=false;
        var erFC=false;
        var erFD=false;

        $( '.hidden' ).hide();
        //$( '.errorMessage' ).hide();
		initHelp();

		var optionSelected=$(this).find("option:selected").text();
		if($.trim(optionSelected)=='<@s.text name="eml.intellectualRights.nolicenses"/>'){
			$('.confirm').unbind('click');
		}

         $('#save').click(function(event){
            //event.preventDefault();
            var allF=true;

            var re = /^[0-9]{4}\-(0[1-9]|1[012])\-(0[1-9]|[12][0-9]|3[01])$/;

            var auto=jQuery('#eml\\.license\\.auto').val();
            var number=jQuery('#eml\\.license\\.number').val();
            var holder=jQuery('#eml\\.license\\.holder').val();
            var nitc=jQuery('#eml\\.license\\.nit').val();
            var date=jQuery('#startDate').val();

            if(auto.indexOf('[autoridad]')!=-1){
                auto='';
            }

            if(number.indexOf('[numero]')!=-1){
                number='';
            }

            if(holder.indexOf('[titular]')!=-1){
                holder='';
            }

            if(nitc.indexOf('[nit o cedula]')!=-1){
                nitc='';
            }

            if(date.indexOf('[fecha]')!=-1){
                date='';
            }

            if(auto==''){
                allF=false; 
                if(!erFA){
                    erFA=true;
                    $('<ul class="fielderror" id="auto" ><li><span>Autoridad ambiental que emitió el permiso es requerido</span></li></ul>').insertBefore( '#eml\\.license\\.auto' );   
                }         
            }else if(erFA){
                erFA=false;
                $('#auto').remove();
            }

            if((number.length <=2 ) || (number.length >=50)){
                allF=false; 
                if(!erFN){
                    erFN=true;
                    $('<ul class="fielderror" id="num"><li><span>Número del permiso es requerido, y debe tener de 2 a 50 caracteres</span></li></ul>').insertBefore( '#eml\\.license\\.number' );   
                }         
            }else if(erFN){
                erFN=false;
                $('#num').remove();
            }

            if(holder==''){
                allF=false;
                if(!erFH){
                    erFH=true;
                    $('<ul class="fielderror" id="hold" ><li><span>Titular del permiso es requerido</span></li></ul>').insertBefore( '#eml\\.license\\.holder' );
                }
            }else if(erFH){
                erFH=false;
                $('#hold').remove();
            }

            if(nitc==''){
                allF=false;
                if(!erFC){
                    erFC=true;
                    $('<ul class="fielderror" id="nitc" ><li><span>Nit o Cédula del titular del permiso es requerido</span></li></ul>').insertBefore( '#eml\\.license\\.nit' );
                }
            }else if(erFC){
                erFC=false;
                $('#nitc').remove();
            }

            if((date=='')||(!(date.match(re)))){ 
                allF=false;
                if(!erFD){
                    erFD=true;
                    $('<ul class="fielderror" id="date" ><li><span>Fecha de emisión del permiso es requerido y debe tener formato <br> YYYY-MM-DD</span></li></ul>').insertBefore( '#startDate' );
                }
            }else if(erFD){
                erFD=false;
                $('#date').remove();
            }

            if(allF){
                var total='[autoridad]'+auto+'[numero]'+number+'[titular]'+holder+'[nit o cedula]'+nitc+'[fecha]'+date;
                total=total.replace(/'/g, '"');
                if(total!='[autoridad][numero][titular][nit o cedula][fecha]'){
                    $('#eml\\.additionalInfo').val(total);
                }else{
                    $('#eml\\.additionalInfo').val('');
                }
            }else{
                event.preventDefault();
            }
            
        }); 

		$("#buttonUpload").click(function() {
  			return ajaxFileUpload();
		});

		function ajaxFileUpload()
    {
        var logourl=$("#resourcelogo img").attr("src");
        $.ajaxFileUpload
        (
            {
                url:'uploadlogo.do',
                secureuri:false,
                fileElementId:'file',
                dataType: 'json',
                success: function (data, status)
                {
                    if(typeof(data.error) != 'undefined')
                    {
                        if(data.error != '')
                        {
                            alert(data.error);
                        }else
                        {
                            alert(data.msg);
                        }
                    }
                },
                error: function (data, status, e)
                {
                    alert(e);
                }
            }
        )
        if(logourl==undefined){
        	var baseimg=$('#baseimg').clone();
			baseimg.appendTo('#resourcelogo');
			logourl=$("#resourcelogo img").attr("src");
			$("#resourcelogo img").hide('slow').removeAttr("src");
			$("#resourcelogo img").show('slow', function() {
    			$("[id$='eml.logoUrl']").attr("value",logourl);
				$("#resourcelogo img").attr("src", logourl+"&t="+(new Date()).getTime());
  			});
        }else{
        	$("#resourcelogo img").hide('slow').removeAttr("src");
         	logourl=$("#baseimg").attr("src");
         	 $("#resourcelogo img").show('slow', function() {
    			$("#resourcelogo img").attr("src", logourl+"&t="+(new Date()).getTime());
    			$("#logofields input[name$='logoUrl']").val( logourl);
  			});
        }
        return false;

    }

	});


</script>
<#assign sideMenuEml=true />
<#assign currentMenu="manage"/>
<#include "/WEB-INF/pages/inc/menu.ftl">
<#include "/WEB-INF/pages/macros/forms.ftl"/>

<h1><span class="superscript"><@s.text name='manage.overview.title.label'/></span>
    <a href="resource.do?r=${resource.shortname}" title="${resource.title!resource.shortname}">${resource.title!resource.shortname}</a>
</h1>
<div class="">
<form class="topForm" action="metadata-${section}.do" method="post">
<h2 class="subTitle"><@s.text name='manage.metadata.additional.title'/></h2>
    <p></p>
    <div id="Additional-Metadata">
        
    	<!--<@input name="eml.license.auto" i18nkey="eml.license.auto"  requiredField=true />-->

        <@select name="eml.license.auto" help="i18n" options=authorities value=" " requiredField=true/>
        
        <@input name="eml.license.number" i18nkey="eml.license.number"  requiredField=true />
        
        <@input name="eml.license.holder" i18nkey="eml.license.holder"  requiredField=true />

        <@input name="eml.license.nit" i18nkey="eml.license.nit"  requiredField=true />

    	<div id="logofields">
    		<div class="halfcolumn">
    			<div style="">
                
    			 <@input date=true i18nkey="eml.license.date" name="startDate" help="i18n" requiredField=true />
    			</div>
    		</div>
    		
    		<div class="clearfix"></div>
    	</div>
  	  	
      	
      	<div id='disclaimerRigths' style='display: none'><p><@s.text name='eml.intellectualRights.license.disclaimer'/></p></div>
      	
        <div class="hidden">
            <@text name="eml.additionalInfo" i18nkey="eml.additionalInfo" help="i18n"/>
        </div>
    	

  	</div>
    <div id="Alternative-Identifiers">
      	<div id="items">
    		<#list eml.alternateIdentifiers as item>
    			<div id="item-${item_index}" class="item">
    			<div class="right">
    				<a id="removeLink-${item_index}" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.alternateIdentifiers.item'/> ]</a>
    		    </div>
    			<@input name="eml.alternateIdentifiers[${item_index}]" i18nkey="eml.alternateIdentifier" help="i18n"/>
    		  	</div>
    		</#list>
    	</div>
      	

      	<div class="buttons">
     		<@s.submit cssClass="button" name="save" key="button.save" cssClass="confirm" />
     		<@s.submit cssClass="button" name="cancel" key="button.cancel"/>
      	</div>
      	<div class="clearfix"></div>
  	</div>
    <!-- internal parameters needed by ajaxFileUpload.js - do not remove -->
	  <input id="r" name="r" type="hidden" value="${resource.shortname}" />
    <input id="validate" name="validate" type="hidden" value="false" />
</form>
</div>

<div id="baseItem" class="item clearfix" style="display:none;">
	<div class="right">
      <a id="removeLink" class="removeLink" href="">[ <@s.text name='manage.metadata.removethis'/> <@s.text name='manage.metadata.alternateIdentifiers.item'/> ]</a>
    </div>
	<@input name="alternateIdentifiers" i18nkey="eml.alternateIdentifier" help="i18n"/>
</div>
<img id="baseimg" src="${baseURL}/logo.do?r=${resource.shortname}" style="display:none;"/>
<img id="loadingimg" src="${baseURL}/images/loading_indicator_bar.gif" style="display:none;"/>

<#include "/WEB-INF/pages/inc/footer.ftl">
</#escape>
S